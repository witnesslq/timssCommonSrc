package com.yudean.homepage.service.core;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yudean.homepage.bean.HomepageWorkTask;
import com.yudean.homepage.bean.NoticeBean;
import com.yudean.homepage.bean.ProcessFucExtParam;
import com.yudean.homepage.bean.WorktaskBean;
import com.yudean.homepage.bean.WorktaskBean.TaskType;
import com.yudean.homepage.bean.WorktaskUserBean;
import com.yudean.homepage.dao.HomepageWorktaskDao;
import com.yudean.homepage.dao.HomepageWorktaskUserDao;
import com.yudean.homepage.dao.SecProcRouteDao;
import com.yudean.homepage.interfaces.HomepageNotifyInterface;
import com.yudean.homepage.service.HomepageFrontService;
import com.yudean.homepage.service.HomepagePortalService;
import com.yudean.homepage.service.HomepageService;
import com.yudean.homepage.vo.WorktaskViewObj;
import com.yudean.itc.code.ParamConfig;
import com.yudean.itc.code.StatusCode;
import com.yudean.itc.dto.Page;
import com.yudean.itc.dto.sec.Organization;
import com.yudean.itc.dto.sec.SecProcRoute;
import com.yudean.itc.dto.sec.SecProcRoute.VisibleType;
import com.yudean.itc.exception.homepage.homePageServiceException;
import com.yudean.itc.manager.sec.IAuthorizationManager;
import com.yudean.itc.manager.support.ISequenceManager;
import com.yudean.itc.util.UUIDGenerator;
import com.yudean.mvc.bean.userinfo.UserInfo;
import com.yudean.mvc.exception.RuntimeDataNotFoundException;
import com.yudean.mvc.service.ItcMvcService;

@Service
public class IHomepageServiceImpl implements HomepageService {
    private static final Logger LOG = Logger.getLogger( IHomepageServiceImpl.class );
    // private final String MainSeqCode = "HOP_MAIN_ID_SEQ";

    @Autowired
    HomepageWorktaskDao worktaskDao;

    @Autowired
    HomepageWorktaskUserDao taskuserDao;

    @Autowired
    ItcMvcService itcMvcService;

    @Autowired
    private HomepageFrontService homepageFrontService;

    @Autowired
    private HomepagePortalService homepageNoticeService;

    @Autowired
    @Qualifier("HomepageSendWhenProcess")
    HomepageNotifyInterface notifyProcess;

    @Autowired
    @Qualifier("HomepageSendWhenComplete")
    HomepageNotifyInterface notifyComplete;

    @Autowired
    SecProcRouteDao secProcRouteDao;

    @Autowired
    ISequenceManager sequenceManager;

    @Autowired
    IAuthorizationManager authManager;

    @Override
    @Transactional(rollbackFor = { Exception.class })
    public String createNotice(HomepageWorkTask homeworkTask, List<String> operUser, UserInfo userInfo) {
        WorktaskBean task = this.getTaskByExtCode( homeworkTask.getProcessInstId(), userInfo );

        WorktaskBean.WorkTaskClass worktaslClass = WorktaskBean.WorkTaskClass.Processed;

        // modify by yuanzh 2016-6-23 为什么不将同样的代码提取到外部呢，反正都是if/else的判断了
        String statusName = homeworkTask.getStatusName();
        String pk = null;

        Date curDate = new Date();
        WorktaskBean taskBean = new WorktaskBean();
        taskBean.setTypename( homeworkTask.getTypeName() );
        taskBean.setName( homeworkTask.getName() );
        taskBean.setStatusname( null == statusName || "".equals( statusName ) ? "" : statusName );
        taskBean.setStatusdate( curDate );
        taskBean.setCreateuser( userInfo.getUserId() );
        taskBean.setCreateusername( userInfo.getUserName() );
        taskBean.setCreatedate( curDate );
        taskBean.setSiteid( userInfo.getSiteId() );
        taskBean.setDeptid( userInfo.getOrgId() );
        taskBean.setDeptname( userInfo.getOrgName() );
        taskBean.setClasstype( worktaslClass );
        taskBean.setUrl( homeworkTask.getUrl() );
        taskBean.setUrltype( WorktaskBean.WorkTaskURLType.Tab );
        taskBean.setExtCode( homeworkTask.getProcessInstId() );
        taskBean.setParentExtCode( "NaN" );
        taskBean.setActive( WorktaskBean.ACTIVEFLAG.ACTIVE );

        if ( null == task ) {
            String seq = getMainSeq();
            taskBean.setId( seq );
            taskBean.setGroupid( seq );
            taskBean.setFlowno( homeworkTask.getFlow() );
            worktaskDao.insertWorktask( taskBean );
            pk = taskBean.getId();
        } else {
            pk = task.getId();
            taskBean.setId( task.getId() );
            taskBean.setFlowno( task.getFlowno() );
            worktaskDao.updateWorktask( taskBean );
        }

        WorktaskUserBean statusUserBean = new WorktaskUserBean();
        statusUserBean.setId( pk );
        statusUserBean.setFlag( WorktaskUserBean.WorkTaskUserFlag.His );
        taskuserDao.updateWorktaskUser( statusUserBean );

        // modify by yuanzh 2016-6-23 使用foreach 隐式声明了userCode少写一行
        for ( String userCode : operUser ) {
            UserInfo operUserInfo = itcMvcService.getUserInfoById( userCode );
            WorktaskUserBean operUserBean = new WorktaskUserBean();
            operUserBean.setId( pk );

            // 这里的operUserInfo.getUserId()其实与userCode等价
            // UserInfo operUserInfo = itcMvcService.getUserInfoById( userCode);
            // 这句实质上只是为了获取operUserInfo.getUserName()
            // 为了这个而在循环中重复调用getUserInfoById性能消耗有点大
            operUserBean.setUsercode( operUserInfo.getUserId() );
            operUserBean.setUsername( operUserInfo.getUserName() );

            operUserBean.setFlag( WorktaskUserBean.WorkTaskUserFlag.Cur );
            operUserBean.setSiteid( userInfo.getSiteId() );
            operUserBean.setDeptid( userInfo.getOrgId() );
            operUserBean.setCreateuser( userInfo.getUserId() );
            operUserBean.setCreatedate( new Date() );
            operUserBean.setModifyuser( userInfo.getUserId() );
            operUserBean.setModifydate( new Date() );
            UpdateAndSaveUser( operUserBean );
        }

        return pk;
    }

    @Override
    public String deleteNotice(String processInsId, UserInfo userInfo) {
        Date curDate = new Date();
        WorktaskBean queryTask = getTaskByExtCode( processInsId, userInfo );
        WorktaskBean taskBean = new WorktaskBean();
        taskBean.setGroupid( queryTask.getGroupid() );
        taskBean.setStatusdate( curDate );
        taskBean.setModifyuser( userInfo.getUserId() );
        taskBean.setModifyusername( userInfo.getUserName() );
        taskBean.setActive( WorktaskBean.ACTIVEFLAG.NO );
        taskBean.setModifydate( curDate );
        taskBean.setClasstype( WorktaskBean.WorkTaskClass.Delete );
        taskBean.setSiteid( userInfo.getSiteId() );
        worktaskDao.updateWorktask( taskBean );
        WorktaskUserBean statusUserBean = new WorktaskUserBean();
        statusUserBean.setId( queryTask.getId() );
        statusUserBean.setFlag( WorktaskUserBean.WorkTaskUserFlag.His );
        taskuserDao.updateWorktaskUser( statusUserBean );
        return queryTask.getGroupid();
    }

    private VisibleType getVisibleType(ProcessFucExtParam extParam) {
        if ( extParam != null ) {
            return extParam.getVisibleType();
        } else {
            return VisibleType.V_USER;
        }
    }

    @Override
    @Transactional(rollbackFor = { Exception.class })
    public String create(HomepageWorkTask homeworkTask, UserInfo userInfo) {
        return create( homeworkTask, userInfo, null );
    }

    @Override
    @Transactional(rollbackFor = { Exception.class })
    public String create(HomepageWorkTask homeworkTask, UserInfo userInfo, ProcessFucExtParam extParam) {
        String flowNo = homeworkTask.getFlow();
        if ( null == homeworkTask.getProcessInstId() || "".equals( homeworkTask.getProcessInstId() ) ) {
            homeworkTask.setProcessInstId( flowNo );
        }
        // TODO by ahua
        VisibleType visibleType = getVisibleType( extParam );

        WorktaskBean task = this.getTaskByFlowNo( flowNo, userInfo );

        WorktaskBean.WorkTaskClass worktaslClass = WorktaskBean.WorkTaskClass.Draft;

        // modify by yuanzh 2016-6-23 又将可以提取出来的代码提取了出来了
        Date curDate = new Date();
        WorktaskBean taskBean = new WorktaskBean();
        taskBean.setExtCode( homeworkTask.getProcessInstId() );
        taskBean.setStatusdate( curDate );
        taskBean.setModifyuser( userInfo.getUserId() );
        taskBean.setModifydate( curDate );
        taskBean.setClasstype( worktaslClass );
        taskBean.setSiteid( userInfo.getSiteId() );

        String pk = null;

        // modify by yuanzh 2016-6-23
        // 增加一个flowno的变量，将updateAndSaveSecProcRout方法提取到if/else外部执行
        String flowno = null;
        if ( null == task ) {
            String seq = getMainSeq();
            taskBean.setId( seq );
            taskBean.setGroupid( seq );
            taskBean.setFlowno( homeworkTask.getFlow() );
            taskBean.setTypename( homeworkTask.getTypeName() );
            taskBean.setName( homeworkTask.getName() );
            String statusName = homeworkTask.getStatusName();
            taskBean.setStatusname( null == statusName || "".equals( statusName ) ? "草稿" : statusName );
            taskBean.setCreateuser( userInfo.getUserId() );
            taskBean.setCreateusername( userInfo.getUserName() );
            taskBean.setCreatedate( curDate );
            taskBean.setDeptid( userInfo.getOrgId() );
            taskBean.setDeptname( userInfo.getOrgName() );
            taskBean.setUrl( homeworkTask.getUrl() );
            taskBean.setUrltype( WorktaskBean.WorkTaskURLType.Tab );

            taskBean.setParentExtCode( "NaN" );
            taskBean.setActive( WorktaskBean.ACTIVEFLAG.ACTIVE );
            taskBean.setTaskType( TaskType.Main );
            worktaskDao.insertWorktask( taskBean );

            pk = taskBean.getId();

            WorktaskUserBean userBean = new WorktaskUserBean();
            userBean.setId( pk );
            userBean.setUsercode( userInfo.getUserId() );
            userBean.setUsername( userInfo.getUserName() );
            userBean.setCreateuser( userInfo.getUserId() );
            userBean.setCreatedate( curDate );
            userBean.setSiteid( userInfo.getSiteId() );
            userBean.setDeptid( userInfo.getOrgId() );
            userBean.setFlag( WorktaskUserBean.WorkTaskUserFlag.His );
            taskuserDao.insertWorktaskUser( userBean );

            flowno = taskBean.getFlowno();

        } else {
            pk = task.getId();

            taskBean.setId( task.getId() );
            taskBean.setName( null == homeworkTask.getName() || "".equals( homeworkTask.getName() ) ? task.getName()
                    : homeworkTask.getName() );
            taskBean.setStatusname( null == homeworkTask.getStatusName() || "".equals( homeworkTask.getStatusName() ) ? task
                    .getStatusname() : homeworkTask.getStatusName() );
            taskBean.setModifyusername( userInfo.getUserName() );
            worktaskDao.updateWorktask( taskBean );

            flowno = task.getFlowno();
        }

        updateAndSaveSecProcRout( flowno, userInfo.getUserId(), userInfo.getSiteId(), userInfo.getOrgs(), visibleType );

        return pk;
    }

    @Override
    @Transactional(rollbackFor = { Exception.class })
    public String createProcess(String flow, String processInstId, String typeName, String name, String statusName,
            String url, UserInfo userInfo, ProcessFucExtParam extParam) throws RuntimeException {
        return this.createProcess( flow, processInstId, typeName, name, statusName, url, WorktaskBean.TaskType.Main,
                userInfo, null/* 建立草稿时不发送任何消息 */, extParam );
    }

    @Override
    @Transactional(rollbackFor = { Exception.class })
    public String createProcess(String flow, String processInstId, String typeName, String name, String statusName,
            String url, WorktaskBean.TaskType tasktype, UserInfo userInfo, ProcessFucExtParam extParam)
            throws RuntimeException {
        return this.createProcess( flow, processInstId, typeName, name, statusName, url, tasktype, userInfo,
                null/* 建立草稿时不发送任何消息 */, extParam );
    }

    public String createProcess(String flow, String processInstId, String typeName, String name, String statusName,
            String url, WorktaskBean.TaskType tasktype, UserInfo userInfo, HomepageNotifyInterface notify,
            ProcessFucExtParam extParam) throws RuntimeException {
        // TODO by ahua
        VisibleType visibleType = getVisibleType( extParam );
        // by hxl
        Boolean isSupProcess = getSubProcessFlag( extParam );

        WorktaskBean task = this.getTaskByExtCode( processInstId, userInfo );
        String pk = null;
        if ( null == task ) {
            String seq = getMainSeq();
            Date curDate = new Date();
            WorktaskBean taskBean = new WorktaskBean();
            taskBean.setId( seq );
            taskBean.setGroupid( seq );
            taskBean.setFlowno( flow );
            taskBean.setTypename( typeName );
            taskBean.setName( name );
            taskBean.setStatusname( null == statusName || "".equals( statusName ) ? "草稿" : statusName );
            taskBean.setStatusdate( curDate );
            taskBean.setCreateuser( userInfo.getUserId() );
            taskBean.setCreateusername( userInfo.getUserName() );
            taskBean.setCreatedate( curDate );
            taskBean.setSiteid( userInfo.getSiteId() );
            taskBean.setDeptid( userInfo.getOrgId() );
            taskBean.setDeptname( userInfo.getOrgName() );
            taskBean.setClasstype( WorktaskBean.WorkTaskClass.Draft );
            taskBean.setUrl( url );
            taskBean.setUrltype( WorktaskBean.WorkTaskURLType.Tab );
            taskBean.setExtCode( processInstId );
            taskBean.setParentExtCode( "NaN" );
            taskBean.setActive( WorktaskBean.ACTIVEFLAG.ACTIVE );
            taskBean.setTaskType( tasktype );
            worktaskDao.insertWorktask( taskBean );
            pk = taskBean.getId();
            WorktaskUserBean userBean = new WorktaskUserBean();
            userBean.setId( pk );
            userBean.setUsercode( userInfo.getUserId() );
            userBean.setUsername( userInfo.getUserName() );
            userBean.setCreateuser( userInfo.getUserId() );
            userBean.setCreatedate( curDate );
            userBean.setModifyuser( userInfo.getUserId() );
            userBean.setModifydate( curDate );
            userBean.setSiteid( userInfo.getSiteId() );
            userBean.setDeptid( userInfo.getOrgId() );
            userBean.setFlag( WorktaskUserBean.WorkTaskUserFlag.His );
            taskuserDao.insertWorktaskUser( userBean );

            updateAndSaveSecProcRout( taskBean.getFlowno(), userInfo.getUserId(), userInfo.getSiteId(),
                    userInfo.getOrgs(), visibleType );
            // by hxl
            if ( isSupProcess ) {
                updateAndSaveSecProcRout( extParam.getParentBusinessId(), userInfo.getUserId(), userInfo.getSiteId(),
                        userInfo.getOrgs(), visibleType );
            }
        } else {
            pk = task.getId();
        }
        return pk;
    }

    // 获取是否是子流程的标志
    private Boolean getSubProcessFlag(ProcessFucExtParam extParam) {
        if ( extParam == null ) {
            return false;
        } else {
            return extParam.getHasSubProcess();
        }
    }

    @Override
    @Transactional(rollbackFor = { Exception.class })
    public void Process(String processInstId, String statusName, List<String> operUser, UserInfo userInfo,
            ProcessFucExtParam extParam) throws RuntimeException {
        // TODO by ahua
        VisibleType visibleType = getVisibleType( extParam );

        for ( String userId : operUser ) { // 为下一个节点的候选人都添加到路由表中
            // UserInfoImpl userinfo = new UserInfoImpl();
            // userinfo.setId(userId);
            // userinfo.setCurrentSite(userInfo.getSiteId());
            // userinfo.setCurrOrgCode(userInfo.getOrgId());

            if ( null != visibleType ) { // 如果设置了这个参数
                String userCode = userId;
                String siteId = userInfo.getSiteId();
                // List<Organization> orgs = userInfo.getOrgs(); //候选人所在的部门列表
                WorktaskBean queryMainTask = getTaskByExtCode( processInstId, userInfo ); // 不跨站点
                if ( null == queryMainTask ) {
                    LOG.error( "流程实例未查询到，请检测是否创建实例，实例编号：" + processInstId + "。当前操作人" + userInfo.getUserId() );
                    throw new RuntimeDataNotFoundException( "工作任务数据未找到" );
                }

                // modify by yuanzh 2016-6-23
                // 将authManager.retriveUserById放在if判断里面
                // 因为之后updateAndSaveSecProcRout用到orgs变量，而updateAndSaveSecProcRout放在if判断里面
                // 若放在if外部则无论是否符合条件都需要查询一次，而不是需要的时候查询，所以将其挪到if里面

                // 获取部门信息
                // TODO by ahua 此处的部门不和登录人的部门一样，所有需要重新查
                // 根据工号和站点查询信息，以及所属的部门
                List<Organization> orgs = authManager.retriveUserById( userId, userInfo.getSiteId() )
                        .getOrganizations();
                // 插入路由表
                updateAndSaveSecProcRout( queryMainTask.getFlowno(), userCode, siteId, orgs, visibleType );
            }
        }
        this.Process( processInstId, statusName, operUser, userInfo, notifyProcess );
    }

    public void Process(String processInstId, String statusName, List<String> operUser, UserInfo userInfo,
            HomepageNotifyInterface notify) throws RuntimeException {
        Date curDate = new Date();
        WorktaskBean querytask = getTaskByExtCode( processInstId, userInfo );// 查询原始内容
        WorktaskBean taskBean = new WorktaskBean();
        taskBean.setId( querytask.getId() );
        taskBean.setStatusname( null == statusName || "".equals( statusName ) ? "" : statusName );
        taskBean.setStatusdate( curDate );
        taskBean.setModifyuser( userInfo.getUserId() );
        taskBean.setModifyusername( userInfo.getUserName() );
        taskBean.setModifydate( curDate );
        taskBean.setClasstype( WorktaskBean.WorkTaskClass.Processed );
        taskBean.setSiteid( userInfo.getSiteId() );
        worktaskDao.updateWorktask( taskBean );

        WorktaskUserBean statusUserBean = new WorktaskUserBean();
        statusUserBean.setId( querytask.getId() );
        statusUserBean.setFlag( WorktaskUserBean.WorkTaskUserFlag.His );
        taskuserDao.updateWorktaskUser( statusUserBean );

        SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );// 设置日期格式
        LOG.debug( "################start for time is " + df.format( new Date() ) );

        // modify by yuanzh 2016-6-23 情况与createNotice方法中描述的一样
        for ( String userCode : operUser ) {
            UserInfo operUserInfo = itcMvcService.getUserInfoById( userCode );
            WorktaskUserBean operUserBean = new WorktaskUserBean();
            operUserBean.setId( querytask.getId() );
            operUserBean.setUsercode( operUserInfo.getUserId() );
            operUserBean.setUsername( operUserInfo.getUserName() );
            operUserBean.setFlag( WorktaskUserBean.WorkTaskUserFlag.Cur );
            operUserBean.setSiteid( userInfo.getSiteId() );
            operUserBean.setDeptid( userInfo.getOrgId() );
            operUserBean.setCreateuser( userInfo.getUserId() );
            operUserBean.setCreatedate( curDate );
            operUserBean.setModifyuser( userInfo.getUserId() );
            operUserBean.setModifydate( curDate );
            UpdateAndSaveUser( operUserBean );
            // TODO by ahua 调用此函数的函数已经插入了路由信息
            // updateAndSaveSecProcRout(querytask.getFlowno(), userCode,
            // userInfo.getSiteId());
        }
        LOG.debug( "################end for time is " + df.format( new Date() ) );
        if ( null != notify ) {
            notify.notify( querytask.getFlowno(), processInstId, querytask.getTypename(), querytask.getName(),
                    statusName, querytask.getUrl(), operUser, userInfo, querytask );
        }
    }

    @Override
    @Transactional(rollbackFor = { Exception.class })
    public void DeleteProcess(String taskId, String ProcessInstId, UserInfo userInfo) throws RuntimeException {
        taskId = null == taskId || "".equals( taskId ) ? "NaN" : taskId;
        WorktaskBean querytask = getTaskByExtCodeAUserSite( taskId, ProcessInstId, userInfo );// 查询原始内容
        if ( null != querytask ) {
            WorktaskUserBean statusUserBean = new WorktaskUserBean();
            statusUserBean.setId( querytask.getGroupid() );
            statusUserBean.setInputSubid( querytask.getId() );
            statusUserBean.setFlag( WorktaskUserBean.WorkTaskUserFlag.His );
            taskuserDao.updateWorktaskUser( statusUserBean );

            querytask.setModifydate( new Date() );
            querytask.setModifyuser( userInfo.getUserId() );
            querytask.setModifyusername( userInfo.getUserName() );
            querytask.setActive( WorktaskBean.ACTIVEFLAG.NO );
            worktaskDao.updateWorktask( querytask );
        }
    }

    // @Override
    // @Transactional(rollbackFor = { Exception.class })
    // public void Process(String taskId, String ProcessInstId, String
    // statusName, List<String> operUser, UserInfo userInfo) throws
    // RuntimeException {
    // Set<UserInfo> operUserList = new HashSet<UserInfo>();
    // for(String userId : operUser){
    // UserInfoImpl userinfo = new UserInfoImpl();
    // userinfo.setId(userId);
    // userinfo.setCurrentSite(userInfo.getSiteId());
    // operUserList.add(userinfo);
    // }
    // this.Process(taskId, ProcessInstId, statusName, operUserList, userInfo,
    // notifyProcess);
    // }
    // TODO by ahua 添加流程配置中对站点或部门可见的处理
    @Override
    @Transactional(rollbackFor = { Exception.class })
    public void Process(String taskId, String ProcessInstId, String statusName, List<String> operUser,
            UserInfo userInfo, ProcessFucExtParam extParam) throws RuntimeException {
        long startTime = System.currentTimeMillis();// 执行开始时间
        Set<UserInfo> operUserList = new HashSet<UserInfo>();

        if ( null != extParam && extParam.isSiteGetRoute() ) {
            WorktaskBean taskBean = new WorktaskBean();
            taskBean.setExtCode( ProcessInstId );
            taskBean.setParentExtCode( "NaN" );

            List<WorktaskBean> querylist = worktaskDao.queryWorktask( taskBean );

            if ( null != querylist && !querylist.isEmpty() ) {
                WorktaskBean queryBean = querylist.get( 0 );
                List<SecProcRoute> routeList = this.getSecRouteInfo( queryBean.getFlowno(), null, null );
                if ( null != routeList && !routeList.isEmpty() ) {
                    Map<String, String> map = new HashMap<String, String>();
                    for ( SecProcRoute route : routeList ) {
                        map.put( route.getUserid(), route.getSiteid() );
                    }
                    long forStartTime = System.currentTimeMillis();// 执行开始时间
                    for ( String userId : operUser ) { // 为下一个节点的候选人都添加到路由表中
                        String siteId = map.get( userId );

                        // modify by yuanzh 2016-6-23
                        // 虽然不知道为什么要这样做，但是若需要重新根据userId和siteId获取userInfo的信息直接查询内存中的就可以了
                        // 这里就不需要用authManager.retriveUserById后再getOrganizations了
                        // 一步到位而且将人员的信息全部都获取到了，一般情况下不会出现某些字段没有数据的情况
                        // 作为参数传给其他方法调用的时候，其他方法可以直接使用，就不需要重新根据userCode再查一遍了
                        UserInfo userinfo = itcMvcService.getUserInfo( userId, siteId );
                        /*
                         * userinfo.setCurrentSite( null == siteId ?
                         * userInfo.getSiteId() : siteId ); // 获取部门信息 // by ahua
                         * 此处的部门不和登录人的部门一样，所有需要重新查 // 根据工号和站点查询信息，以及所属的部门
                         * List<Organization> orgs =
                         * authManager.retriveUserById( userId,
                         * userInfo.getSiteId() ) .getOrganizations();
                         * userinfo.setOrganizations( orgs );
                         */
                        operUserList.add( userinfo );
                    }
                    long forEndTime = System.currentTimeMillis();// 执行结束时间
                    LOG.debug( ">>>>>>>>>>>>>>>>>>>> ... 外层 Process for 执行时间："
                            + String.valueOf( (forEndTime - forStartTime) / 1000 ) + " s" );
                }
            }
        } else {
            long forStartTime = System.currentTimeMillis();// 执行开始时间
            for ( String userId : operUser ) { // 为下一个节点的候选人都添加到路由表中
                /*
                 * UserInfoImpl userinfo = new UserInfoImpl(); userinfo.setId(
                 * userId ); userinfo.setCurrentSite( userInfo.getSiteId() ); //
                 * 获取部门信息 // by ahua 此处的部门不和登录人的部门一样，所有需要重新查 //
                 * 根据工号和站点查询信息，以及所属的部门 List<Organization> orgs =
                 * authManager.retriveUserById( userId, userInfo.getSiteId() )
                 * .getOrganizations(); userinfo.setOrganizations( orgs );
                 */
                // modify by yuanzh 2016-6-23 道理同上
                UserInfo userinfo = itcMvcService.getUserInfo( userId, userInfo.getSiteId() );
                operUserList.add( userinfo );
            }
            long forEndTime = System.currentTimeMillis();// 执行结束时间
            LOG.debug( ">>>>>>>>>>>>>>>>>>>> ... 外层 Process for 执行时间："
                    + String.valueOf( (forEndTime - forStartTime) / 1000 ) + " s" );
        }
        this.Process( taskId, ProcessInstId, statusName, operUserList, userInfo, notifyProcess, extParam );
        long endTime = System.currentTimeMillis();// 执行结束时间
        LOG.debug( ">>>>>>>>>>>>>>>>>>>> ...  Process 总执行时间：" + String.valueOf( (endTime - startTime) / 1000 ) + " s" );
    }

    // @Override
    // @Transactional(rollbackFor = { Exception.class })
    // public void Process(String taskId, String ProcessInstId, String
    // statusName, Set<UserInfo>operUser, UserInfo userInfo) throws
    // RuntimeException {
    // this.Process(taskId, ProcessInstId, statusName, operUser, userInfo,
    // notifyProcess);
    // }

    @Override
    @Transactional(rollbackFor = { Exception.class })
    public void Process(String taskId, String ProcessInstId, String statusName, Set<UserInfo> operUser,
            UserInfo userInfo, ProcessFucExtParam extParam) throws RuntimeException {
        // VisibleType visibleType = getVisibleType(extParam);
        // ;
        // for (UserInfo userinfo : operUser) { // 为下一个节点的候选人都添加到路由表中
        // if (null != visibleType) { // 如果设置了这个参数
        // SecureUser secureUser = userinfo.getSecureUser();
        // String userCode = secureUser.getId();
        // String siteId = secureUser.getCurrentSite();
        // List<Organization> orgs = secureUser.getOrganizations(); //
        // 候选人所在的部门列表
        // WorktaskBean queryMainTask = getTaskByExtCodeAUserSite(
        // ProcessInstId, userInfo);
        // if (null == queryMainTask) {
        // LOG.error("流程实例未查询到，请检测是否创建实例，实例编号：" + ProcessInstId
        // + ".子实例编号：" + taskId + "。当前操作人"
        // + userInfo.getUserId());
        // throw new RuntimeDataNotFoundException("工作任务数据未找到");
        // }
        // // 插入路由表
        // updateAndSaveSecProcRout(queryMainTask.getFlowno(), userCode,
        // siteId, orgs, visibleType);
        // }
        // }
        this.Process( taskId, ProcessInstId, statusName, operUser, userInfo, notifyProcess, extParam );
    }

    public void Process(String taskId, String ProcessInstId, String statusName, Set<UserInfo> operUser,
            UserInfo userInfo, HomepageNotifyInterface notify, ProcessFucExtParam extParam) throws RuntimeException {

        WorktaskBean querytask = getTaskByExtCode( taskId, ProcessInstId, userInfo );
        SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss:SSS" );// 设置日期格式
        LOG.debug( "################Process start time is " + df.format( new Date() ) );
        if ( null == querytask ) {// 如果传入的子节点任然激活，则什么都不干
            Date curDate = new Date();
            WorktaskBean queryMainTask = getTaskByExtCodeAUserSite( ProcessInstId, userInfo );
            if ( null == queryMainTask ) {
                LOG.error( "流程实例未查询到，请检测是否创建实例，实例编号：" + ProcessInstId + ".子实例编号：" + taskId + "。当前操作人"
                        + userInfo.getUserId() );
                throw new RuntimeDataNotFoundException( "工作任务数据未找到" );
            }
            boolean isAddNotice = null == queryMainTask.getModifydate() ? false : true;
            queryMainTask.setClasstype( WorktaskBean.WorkTaskClass.Processed );
            queryMainTask.setStatusdate( curDate );
            queryMainTask.setStatusname( statusName );
            queryMainTask.setModifydate( curDate );
            queryMainTask.setModifyuser( userInfo.getUserId() );
            queryMainTask.setModifyusername( userInfo.getUserName() );
            worktaskDao.updateWorktask( queryMainTask );

            String seq = getMainSeq();
            WorktaskBean taskBean = new WorktaskBean();
            taskBean.setId( seq );
            taskBean.setGroupid( queryMainTask.getGroupid() );
            taskBean.setFlowno( queryMainTask.getFlowno() );
            taskBean.setTypename( queryMainTask.getTypename() );
            taskBean.setName( queryMainTask.getName() );
            taskBean.setStatusname( statusName );
            taskBean.setStatusdate( curDate );
            taskBean.setCreateuser( userInfo.getUserId() );
            taskBean.setCreateusername( userInfo.getUserName() );
            taskBean.setCreatedate( curDate );
            taskBean.setSiteid( userInfo.getSiteId() );
            taskBean.setDeptid( userInfo.getOrgId() );
            taskBean.setDeptname( userInfo.getOrgName() );
            taskBean.setClasstype( WorktaskBean.WorkTaskClass.Processed );
            taskBean.setUrl( queryMainTask.getUrl() );
            taskBean.setUrltype( queryMainTask.getUrltype() );
            taskBean.setExtCode( taskId );
            taskBean.setParentExtCode( queryMainTask.getExtCode() );
            taskBean.setActive( WorktaskBean.ACTIVEFLAG.ACTIVE );
            worktaskDao.insertWorktask( taskBean );

            /*
             * List<String> sendUserList = new ArrayList<String>();
             * LOG.debug("################Process start for time is " +
             * df.format(new Date())); for ( UserInfo user : operUser ) { String
             * userCode = user.getUserId(); String siteId = null ==
             * user.getSiteId() || "".equals( user.getSiteId() ) ?
             * userInfo.getSiteId() : user .getSiteId(); sendUserList.add(
             * userCode ); LOG.debug(
             * "################Process start itcMvcService.getUserInfoById is "
             * + df.format(new Date())); UserInfo operUserInfo =
             * itcMvcService.getUserInfoById( userCode ); LOG.debug(
             * "################Process end itcMvcService.getUserInfoById is " +
             * df.format(new Date())); WorktaskUserBean operUserBean = new
             * WorktaskUserBean(); operUserBean.setId( queryMainTask.getId() );
             * operUserBean.setSubid( seq ); operUserBean.setUsercode(
             * operUserInfo.getUserId() ); operUserBean.setUsername(
             * operUserInfo.getUserName() ); operUserBean.setFlag(
             * WorktaskUserBean.WorkTaskUserFlag.Cur ); operUserBean.setSiteid(
             * siteId ); operUserBean.setDeptid( userInfo.getOrgId() );
             * operUserBean.setCreateuser( userInfo.getUserId() );
             * operUserBean.setCreatedate( curDate );
             * operUserBean.setModifyuser( userInfo.getUserId() );
             * operUserBean.setModifydate( curDate );
             * LOG.debug("################Process start UpdateAndSaveUser is " +
             * df.format(new Date())); UpdateAndSaveUser( operUserBean );
             * LOG.debug("################Process end UpdateAndSaveUser is " +
             * df.format(new Date())); // TODO by ahua 在其上一级函数中已经插入路由表了，所以注释掉
             * List<Organization> orgs = authManager.retriveUserById( userCode,
             * siteId ).getOrganizations(); LOG.debug(
             * "################Process end authManager.retriveUserById is " +
             * df.format(new Date())); // 插入路由表 updateAndSaveSecProcRout(
             * queryMainTask.getFlowno(), userCode, siteId, orgs,
             * getVisibleType( extParam ) );
             * LOG.debug("################Process end updateAndSaveSecProcRout is "
             * + df.format(new Date())); // by hxl 如果是子流程需要更新父流程路由表 Boolean
             * isSupProcess = getSubProcessFlag( extParam ); if ( isSupProcess )
             * { updateAndSaveSecProcRout( extParam.getParentBusinessId(),
             * userCode, siteId, orgs, getVisibleType( extParam ) ); } }
             */

            // modify by yuanzh 2016-6-23 将保存人员信息的内容提取到另外的一个方法中去进行优化，传入参数保持不变
            saveOrUpdateWorktaskUserBean( seq, queryMainTask, operUser, userInfo, curDate, extParam );

            LOG.debug( "################Process end for time is " + df.format( new Date() ) );
            if ( null != notify ) {
                /*
                 * notify.notify( queryMainTask.getFlowno(),
                 * queryMainTask.getExtCode(), queryMainTask.getTypename(),
                 * queryMainTask.getName(), statusName, queryMainTask.getUrl(),
                 * sendUserList, userInfo, queryMainTask );
                 */
                // modify by yuanzh 2016-6-23
                // 直接将Set<UserInfo>
                // operUser传入，这样就不需要循环获取userId后又调方法查询获取UserInfo信息了
                notify.notifyWithSetUser( queryMainTask.getFlowno(), queryMainTask.getExtCode(),
                        queryMainTask.getTypename(), queryMainTask.getName(), statusName, queryMainTask.getUrl(),
                        operUser, userInfo, queryMainTask );
            }
            LOG.debug( "################Process start AddNotice time is " + df.format( new Date() ) );
            if ( isAddNotice ) {
                NoticeBean noticeBean = new NoticeBean();
                noticeBean.setCode( queryMainTask.getFlowno() );
                noticeBean.setContent( queryMainTask.getName() );
                noticeBean.setActive( StatusCode.Y );
                noticeBean.setStatus( NoticeBean.Status.Notice );
                noticeBean.setStatusdate( curDate );
                noticeBean.setUserid( queryMainTask.getCreateuser() );
                noticeBean.setSiteId( queryMainTask.getSiteid() );
                noticeBean.setOperUrl( queryMainTask.getUrl() );
                noticeBean.setStatusName( queryMainTask.getTypename() );
                noticeBean.setName( queryMainTask.getTypename() );
                homepageNoticeService.modifyNotice( noticeBean, userInfo );
            }
            LOG.debug( "################Process end AddNotice time is " + df.format( new Date() ) );
        }
    }

    /**
     * 批量执行保存或更新WorktaskUserBean操作 modify by yuanzh 2016-6-23
     * 
     * @param seq 序列号
     * @param queryMainTask 任务信息
     * @param operUser 人员集合
     * @param userInfo 当前操作人信息
     * @param curDate 当前时间
     * @param extParam 扩展参数
     */
    private void saveOrUpdateWorktaskUserBean(String seq, WorktaskBean queryMainTask, Set<UserInfo> operUser,
            UserInfo userInfo, Date curDate, ProcessFucExtParam extParam) {
        long startTime = System.currentTimeMillis();// 执行开始时间

        // 将可以提取出来的变量尽可能提取到前面，这样可以使代码可读性增强
        String userCode = null;
        String siteId = null;
        String userOrg = null;

        String flowno = queryMainTask.getFlowno();
        String parentBusinessId = extParam.getParentBusinessId();

        Boolean isSupProcess = getSubProcessFlag( extParam );
        VisibleType getVisibleType = getVisibleType( extParam );

        // 预存保存（更新）数据集合
        List<WorktaskUserBean> wubList = new ArrayList<WorktaskUserBean>();
        List<SecProcRoute> procRoutList = new ArrayList<SecProcRoute>();
        List<SecProcRoute> parentProcRoutList = new ArrayList<SecProcRoute>();

        SecProcRoute procRout = null;
        SecProcRoute parentProcRout = null;

        long forStartTime = System.currentTimeMillis();// 执行开始时间
        // 遍历候选人
        for ( UserInfo user : operUser ) {

            long wubStartTime = System.currentTimeMillis();
            userCode = user.getUserId();
            siteId = null == user.getSiteId() || "".equals( user.getSiteId() ) ? userInfo.getSiteId() : user
                    .getSiteId();
            // 将候选人的信息放到一个list里面，由于是内存操作，这部分的操作将会很快完成
            WorktaskUserBean operUserBean = new WorktaskUserBean();
            operUserBean.setId( queryMainTask.getId() );
            operUserBean.setSubid( seq );
            operUserBean.setFlag( WorktaskUserBean.WorkTaskUserFlag.Cur );
            operUserBean.setDeptid( userInfo.getOrgId() );
            operUserBean.setCreateuser( userInfo.getUserId() );
            operUserBean.setCreatedate( curDate );
            operUserBean.setModifyuser( userInfo.getUserId() );
            operUserBean.setModifydate( curDate );
            operUserBean.setSiteid( siteId );
            operUserBean.setUsercode( user.getUserId() );
            operUserBean.setUsername( user.getUserName() );
            wubList.add( operUserBean );
            long wubEndTime = System.currentTimeMillis();
            LOG.debug( ">>>>>>>>>>>>>>>>>>>> ... saveOrUpdateWorktaskUserBean WorktaskUserBean 执行时间："
                    + String.valueOf( wubEndTime - wubStartTime ) + " ms" );

            long orgStartTime = System.currentTimeMillis();
            // TODO by ahua 在其上一级函数中已经插入路由表了，所以注释掉

            /*
             * List<Organization> orgs = authManager.retriveUserById( userCode,
             * siteId ).getOrganizations(); if ( null != orgs && !orgs.isEmpty()
             * ) { userOrg = orgs.get( 0 ).getCode(); }
             */

            userOrg = user.getOrgId();
            long orgEndTime = System.currentTimeMillis();
            LOG.debug( ">>>>>>>>>>>>>>>>>>>> ... saveOrUpdateWorktaskUserBean retriveUserById 执行时间："
                    + String.valueOf( orgEndTime - orgStartTime ) + " ms" );

            long procRStartTime = System.currentTimeMillis();
            // 插入路由表
            procRout = new SecProcRoute();
            procRout.setFlowid( flowno ); // 设置流水号
            procRout.setUserid( userCode );// 设置户号
            procRout.setSiteid( siteId );// 设置站点
            procRout.setDeptid( userOrg );// 设置部门
            if ( null == getVisibleType ) {// 设置默认的路由类型
                getVisibleType = VisibleType.V_USER;
            }
            procRout.setVisibleType( getVisibleType );
            procRoutList.add( procRout );
            long procREndTime = System.currentTimeMillis();
            LOG.debug( ">>>>>>>>>>>>>>>>>>>> ... saveOrUpdateWorktaskUserBean procRout 执行时间："
                    + String.valueOf( procREndTime - procRStartTime ) + " ms" );

            if ( isSupProcess ) {
                long pProcRStartTime = System.currentTimeMillis();
                parentProcRout = new SecProcRoute();
                parentProcRout.setFlowid( parentBusinessId );
                parentProcRout.setUserid( procRout.getUserid() );
                parentProcRout.setSiteid( procRout.getSiteid() );
                parentProcRout.setDeptid( procRout.getDeptid() );
                parentProcRout.setVisibleType( procRout.getVisibleType() );
                parentProcRoutList.add( parentProcRout );
                long pProcREndTime = System.currentTimeMillis();
                LOG.debug( ">>>>>>>>>>>>>>>>>>>> ... saveOrUpdateWorktaskUserBean parentProcRout 执行时间："
                        + String.valueOf( pProcREndTime - pProcRStartTime ) + " ms" );
            }
        }
        long forEndTime = System.currentTimeMillis();// 执行开始时间
        LOG.debug( ">>>>>>>>>>>>>>>>>>>> ... saveOrUpdateWorktaskUserBean for 执行时间："
                + String.valueOf( (forEndTime - forStartTime) / 1000 ) + " s" );
        // 批量保存（更新）WorktaskUserBean信息
        taskuserDao.updateAndSaveUserBatch( wubList );
        secProcRouteDao.insertSecProcRouteBatch( procRoutList );
        if ( null != parentProcRoutList && !parentProcRoutList.isEmpty() ) {
            secProcRouteDao.insertSecProcRouteBatch( parentProcRoutList );
        }
        long endTime = System.currentTimeMillis();// 执行结束时间
        LOG.debug( ">>>>>>>>>>>>>>>>>>>> ... saveOrUpdateWorktaskUserBean 执行时间："
                + String.valueOf( (endTime - startTime) / 1000 ) + " s" );
    }

    @Override
    @Transactional(rollbackFor = { Exception.class })
    public void Delete(String processInstId, UserInfo userInfo) throws RuntimeException {
        HomepageNotifyInterface notify = null;
        this.Delete( processInstId, userInfo, notify );
    }

    public void Delete(String processInstId, UserInfo userInfo, HomepageNotifyInterface notify) throws RuntimeException {
        Date curDate = new Date();
        WorktaskBean queryTask = getTaskByExtCode( processInstId, userInfo );
        if ( null == queryTask ) {
            queryTask = getTaskByFlowNo( processInstId, userInfo );
        }
        if ( null != queryTask ) {
            WorktaskBean taskBean = new WorktaskBean();
            taskBean.setGroupid( queryTask.getGroupid() );
            taskBean.setStatusdate( curDate );
            taskBean.setModifyuser( userInfo.getUserId() );
            taskBean.setModifyusername( userInfo.getUserName() );
            taskBean.setModifydate( curDate );
            taskBean.setClasstype( WorktaskBean.WorkTaskClass.Delete );
            taskBean.setSiteid( userInfo.getSiteId() );
            worktaskDao.updateWorktask( taskBean );
            WorktaskUserBean statusUserBean = new WorktaskUserBean();
            statusUserBean.setId( queryTask.getId() );
            statusUserBean.setFlag( WorktaskUserBean.WorkTaskUserFlag.His );
            taskuserDao.updateWorktaskUser( statusUserBean );
        } else {
            throw new homePageServiceException( "根据实例或流水号：" + processInstId + "获取任务失败" );
        }
    }

    /**
     * 流程归档调用
     */
    @Override
    @Transactional(rollbackFor = { Exception.class })
    public void complete(String processInstId, UserInfo userInfo) throws RuntimeException {
        this.complete( processInstId, userInfo, null, notifyComplete );
    }

    @Override
    public void complete(String processInstId, UserInfo userInfo, String statusName) throws RuntimeException {
        this.complete( processInstId, userInfo, statusName, notifyComplete );
    }

    public void complete(String processInstId, UserInfo userInfo, String statusName, HomepageNotifyInterface notify)
            throws RuntimeException {
        Date curDate = new Date();// 获取当前更新日期
        WorktaskBean querytask = getTaskByExtCodeAUserSite( processInstId, userInfo );// 通过实例ID查询表单是否存在
        WorktaskBean taskBean = new WorktaskBean();
        taskBean.setGroupid( querytask.getGroupid() );// 设置当前任务的组ID，通过组ID将所有相关的任务都设置为完成状态
        if ( null != statusName ) {
            taskBean.setStatusname( statusName );// 设置状态名称
        }
        taskBean.setStatusdate( curDate );// 设置状态日期
        taskBean.setModifyuser( userInfo.getUserId() );// 设置修改人
        taskBean.setModifyusername( userInfo.getUserName() );// 设置修改人名称冗余
        taskBean.setModifydate( curDate );// 设置更新日期
        taskBean.setClasstype( WorktaskBean.WorkTaskClass.Complete );// 设置为完成状态
        taskBean.setActive( WorktaskBean.ACTIVEFLAG.NO );// 设置为非活跃状态
        /*
         * 移除对站点的限制。因为每个待办都有唯一的group编号
         */
        // taskBean.setSiteid(userInfo.getSiteId());
        worktaskDao.updateWorktask( taskBean );// 更新该任务
        WorktaskUserBean statusUserBean = new WorktaskUserBean();
        statusUserBean.setId( querytask.getId() );// 设置更新的任务ID
        statusUserBean.setFlag( WorktaskUserBean.WorkTaskUserFlag.His );// 设置所有人都更新为非在办人状态
        taskuserDao.updateWorktaskUser( statusUserBean );// 更新任务相关用户
        if ( null != notify && WorktaskBean.TaskType.Main.equals( querytask.getTaskType() ) ) {// 发送通知邮件
            switch (querytask.getTaskType()) {
                case Main: {
                    if ( null != notify ) {
                        // 主任务才发送通知邮件
                        notify.notify( querytask.getFlowno(), processInstId, querytask.getTypename(),
                                querytask.getName(), "流程结束", querytask.getUrl(), null, userInfo, querytask );
                    }
                    // 主任务才生成通知信息
                    // 更新通知列表
                    NoticeBean noticeBean = new NoticeBean();
                    noticeBean.setCode( querytask.getFlowno() );
                    noticeBean.setContent( querytask.getName() );
                    noticeBean.setStatus( NoticeBean.Status.Complete );
                    noticeBean.setActive( StatusCode.Y );
                    noticeBean.setStatusdate( curDate );
                    noticeBean.setUserid( querytask.getCreateuser() );
                    noticeBean.setSiteId( querytask.getSiteid() );
                    noticeBean.setOperUrl( querytask.getUrl() );
                    noticeBean.setStatusName( taskBean.getTypename() );
                    noticeBean.setName( querytask.getTypename() );
                    homepageNoticeService.modifyNotice( noticeBean, userInfo );
                    break;
                }
                case Sub:// 子任务不发送通知
                default: {
                    break;
                }
            }
        }
    }

    @Override
    public void modify(Object id, String flow, String typeName, String name, String statusName, List<String> operUser,
            String url, UserInfo userInfo) throws RuntimeException {
        //2016/9/9 zhx 加上对flow的校验
    	if(null == flow){
    		throw new RuntimeException("flowNo 不能为空。");
    	}
    	WorktaskBean taskBean = new WorktaskBean();
        taskBean.setFlowno( flow );
        taskBean.setName( name );
        worktaskDao.updateWorktask( taskBean );// 更新该任务的name
    }

    @Override
    public List<WorktaskViewObj> getUserAllTasking(String userid, String siteid) throws RuntimeException {
        Page<WorktaskViewObj> page = new Page<WorktaskViewObj>();

        page.setPageNo( 1 );
        page.setPageSize( 500 );
        page.setParameter( ParamConfig.HOP_UserCode, userid );
        page.setParameter( ParamConfig.HOP_ClassType, WorktaskBean.WorkTaskClass.Processed );
        page.setParameter( ParamConfig.HOP_Flag, WorktaskUserBean.WorkTaskUserFlag.Cur );

        page = homepageFrontService.getDoingTaskList( page, null );
        if ( null != page ) {
            return page.getResults();
        } else {
            return new ArrayList<WorktaskViewObj>();
        }
    }

    @Override
    public WorktaskBean getOneTaskByFlowNo(String flowno, UserInfo userInfo) throws RuntimeException {
        return getTaskByFlowNo( flowno, userInfo );
    }

    /**
     * 获取主序列号
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-10-24
     * @return:
     */
    private String getMainSeq() {
        return UUIDGenerator.getUUID();
    }

    /**
     * 更新待办信息路由表
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-10-23
     * @param flow
     * @param userid
     * @throws RuntimeException :
     */
    // private void updateAndSaveSecProcRout(String flow, String userid,
    // String siteid) throws RuntimeException {
    // SecProcRoute secProcRoute = new SecProcRoute();
    // secProcRoute.setFlowid(flow);
    // secProcRoute.setUserid(userid);
    // secProcRoute.setSiteid(siteid);
    //
    // List<SecProcRoute> list = secProcRouteDao
    // .selectSecProcRoute(secProcRoute);
    // if (list.isEmpty()) {
    // secProcRouteDao.insertSecProcRoute(secProcRoute);
    // }
    // }

    // TODO by ahua 插入路由表数据，带可见类型和部门信息
    private void updateAndSaveSecProcRout(String flow, String userid, String siteid, List<Organization> orgs,
            VisibleType visibleType) throws RuntimeException {
        // modify by yuanzh 2016-6-23 这里先声明一个变量设置为null
        // 根据orgs集合判断是否为空给变量赋值
        // 并将insertSecProcRoute提取到外部
        String inputOrg = null;
        if ( null != orgs && !orgs.isEmpty() ) {
            inputOrg = orgs.get( 0 ).getCode();
        }
        insertSecProcRoute( flow, userid, siteid, inputOrg, visibleType );
    }

    /**
     * 向路由表添加数据
     * 
     * @param flow
     * @param userid
     * @param siteid
     * @param deptid
     */
    private void insertSecProcRoute(String flow, String userid, String siteid, String deptid, VisibleType visibleType) {
        SecProcRoute secProcRoute = new SecProcRoute(); // 新建路由表的持久层对象
        secProcRoute.setFlowid( flow ); // 设置流水号
        secProcRoute.setUserid( userid );// 设置户号
        secProcRoute.setSiteid( siteid );// 设置站点
        secProcRoute.setDeptid( deptid );// 设置部门
        if ( visibleType == null ) {// 设置默认的路由类型
            visibleType = VisibleType.V_USER;
        }
        secProcRoute.setVisibleType( visibleType );
        List<SecProcRoute> list = secProcRouteDao.selectSecProcRoute( secProcRoute );// 查询当前数据是否已经在路由表中
        if ( null == list || list.isEmpty() ) {
            secProcRouteDao.insertSecProcRoute( secProcRoute );
        }
    }

    /**
     * 更新或添加用户信息
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-7-10
     * @param userBean
     * @return
     * @throws RuntimeException
     * @say by yuanzh 2016-6-23：这种通过查询然后判断是否存在的方法是比较旧式的方法，适用于所有的编程方式
     *      但是遇上循环后套用的话效率就变得很低，况且数据量不大的话都不会循环遍历了，循环多少次就查多少次数据库*2，效率低
     *      所以尽量避免使用这种方法，网上有很多处理办法，这次使用的是通过数据库解决的也是其中一种
     */
    private Integer UpdateAndSaveUser(WorktaskUserBean userBean) throws RuntimeException {
        Integer flag = 0;
        WorktaskUserBean bean = new WorktaskUserBean();
        bean.setId( userBean.getId() );
        bean.setUsercode( userBean.getUsercode() );
        bean.setSiteid( userBean.getSiteid() );
        List<WorktaskUserBean> list = taskuserDao.queryWorktaksUser( bean );
        if ( 0 < list.size() ) {
            flag = taskuserDao.updateWorktaskUser( userBean );
        } else {
            flag = taskuserDao.insertWorktaskUser( userBean );
        }
        if ( 0 == flag ) {
            LOG.warn( "用户数据" + userBean.getUsercode() + "添加失败!" );
        }
        return flag;
    }

    private WorktaskBean getTaskByFlowNo(String flowno, UserInfo userInfo) throws RuntimeException {
        WorktaskBean task = null;
        WorktaskBean queryTaskBean = new WorktaskBean();
        queryTaskBean.setFlowno( flowno );
        queryTaskBean.setParentExtCode( "NaN" );
        queryTaskBean.setSiteid( userInfo.getSiteId() );
        List<WorktaskBean> list = worktaskDao.queryWorktask( queryTaskBean );
        if ( null != list && list.size() > 0 ) {
            task = list.get( 0 );
        }
        return task;
    }

    /**
     * 转换业务数据信息
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-7-10
     * @param code
     * @param userInfo
     * @return
     * @throws RuntimeException :
     */
    private WorktaskBean getTaskByExtCode(String extCode, UserInfo userInfo) throws RuntimeException {
        WorktaskBean task = null;
        WorktaskBean queryTaskBean = new WorktaskBean();
        queryTaskBean.setExtCode( extCode );
        queryTaskBean.setParentExtCode( "NaN" );
        queryTaskBean.setSiteid( userInfo.getSiteId() );
        List<WorktaskBean> list = worktaskDao.queryWorktask( queryTaskBean );
        if ( null != list && list.size() > 0 ) {
            task = list.get( 0 );
        }
        return task;
    }

    /**
     * 从用户的从属站点获取用户信息，适用于跨站点处理
     * 
     * @param extCode
     * @param userInfo
     * @return
     * @throws RuntimeException
     */
    private WorktaskBean getTaskByExtCodeAUserSite(String extCode, UserInfo userInfo) throws RuntimeException {
        WorktaskBean task = null;
        WorktaskBean queryTaskBean = new WorktaskBean();
        queryTaskBean.setExtCode( extCode );
        queryTaskBean.setParentExtCode( "NaN" );

        WorktaskUserBean user = new WorktaskUserBean();
        user.setFlag( WorktaskUserBean.WorkTaskUserFlag.Cur );
        user.setSiteid( userInfo.getSiteId() );
        user.setUsercode( userInfo.getUserId() );

        List<WorktaskBean> list = worktaskDao.queryWorktaskByUser( queryTaskBean, user );
        if ( null != list && list.size() > 0 ) {
            task = list.get( 0 );
        }
        return task;
    }

    /**
     * 转换业务数据信息
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-7-10
     * @param code
     * @param userInfo
     * @return
     * @throws RuntimeException :
     */
    private WorktaskBean getTaskByExtCode(String extCode, String parentExtCode, UserInfo userInfo)
            throws RuntimeException {
        WorktaskBean task = null;
        WorktaskBean queryTaskBean = new WorktaskBean();
        queryTaskBean.setExtCode( extCode );
        queryTaskBean.setParentExtCode( parentExtCode );
        queryTaskBean.setSiteid( userInfo.getSiteId() );
        List<WorktaskBean> list = worktaskDao.queryWorktask( queryTaskBean );
        if ( list.size() > 0 ) {
            task = list.get( 0 );
        }
        return task;
    }

    /**
     * 转换业务数据信息
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-7-10
     * @param code
     * @param userInfo
     * @return
     * @throws RuntimeException :
     */
    private WorktaskBean getTaskByExtCodeAUserSite(String extCode, String parentExtCode, UserInfo userInfo)
            throws RuntimeException {
        WorktaskBean task = null;
        WorktaskBean queryTaskBean = new WorktaskBean();
        queryTaskBean.setExtCode( extCode );
        queryTaskBean.setParentExtCode( parentExtCode );

        WorktaskUserBean user = new WorktaskUserBean();
        user.setFlag( WorktaskUserBean.WorkTaskUserFlag.Cur );
        user.setSiteid( userInfo.getSiteId() );
        user.setUsercode( userInfo.getUserId() );

        List<WorktaskBean> list = worktaskDao.queryWorktaskByUser( queryTaskBean, user );
        if ( list.size() > 0 ) {
            task = list.get( 0 );
        }
        return task;
    }

    @Override
    public List<SecProcRoute> getSecRouteInfo(String flowNo, String userId, String siteId) {
        SecProcRoute secProcRoute = new SecProcRoute();
        secProcRoute.setFlowid( flowNo );
        secProcRoute.setUserid( userId );
        secProcRoute.setSiteid( siteId );
        secProcRoute.setVisibleType( null );
        return secProcRouteDao.selectSecProcRoute( secProcRoute );
    }

    /**
     * @description: 创建待办或站内信息（不走流程）
     * @author: yuanzh
     * @createDate: 2015-9-8:
     */
    @Override
    @Transactional(rollbackFor = { Exception.class })
    public void createNoticeWithOutWorkflow(HomepageWorkTask task, List<String> userIds, UserInfo userInfo, String type) {

        // createNotice( task, userIds, userInfo );// 创建并转派给仓管员

        if ( null != userIds && !userIds.isEmpty() ) {
            for ( String userId : userIds ) {
                NoticeBean noticeBean = homepageNoticeService.queryNoticeByCode( task.getProcessInstId() + userId,
                        userInfo.getSiteId() );

                if ( null == noticeBean ) {
                    noticeBean = new NoticeBean();
                }

                if ( "INFO".equals( type ) ) {
                    noticeBean.setStatus( NoticeBean.Status.Info );
                } else if ( "WARN".equals( type ) ) {
                    noticeBean.setStatus( NoticeBean.Status.Warning );
                } else if ( "NOTICE".equals( type ) ) {
                    noticeBean.setStatus( NoticeBean.Status.Notice );
                } else if ( "COMPLETE".equals( type ) ) {
                    noticeBean.setStatus( NoticeBean.Status.Complete );
                }

                noticeBean.setCode( task.getProcessInstId() );

                noticeBean.setSiteId( userInfo.getSiteId() );
                noticeBean.setContent( task.getName() );
                noticeBean.setActive( StatusCode.Y );
                noticeBean.setStatusdate( new Date() );
                noticeBean.setOperUrl( task.getUrl() );
                noticeBean.setStatusName( task.getStatusName() );
                noticeBean.setName( task.getTypeName() );

                noticeBean.setUserid( userId );
                noticeBean.setCode( task.getProcessInstId() + userId );
                homepageNoticeService.modifyNotice( noticeBean, userInfo );
            }
        }
    }

    /**
     * @description: 删除待办或站内信息（不走流程）
     * @author: yuanzh
     * @createDate: 2015-9-8:
     */
    @Override
    @Transactional(rollbackFor = { Exception.class })
    public void deleteNoticeWithOutWorkflow(String processInitId, UserInfo userInfo) {
        deleteNotice( processInitId, userInfo );
        homepageNoticeService.completeNotice( processInitId, userInfo.getSiteId(), userInfo );
    }
}