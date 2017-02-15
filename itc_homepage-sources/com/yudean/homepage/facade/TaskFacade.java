package com.yudean.homepage.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yudean.homepage.bean.HomepageWorkTask;
import com.yudean.homepage.bean.NoticeBean;
import com.yudean.homepage.bean.ProcessFucExtParam;
import com.yudean.homepage.bean.WorktaskBean;
import com.yudean.homepage.bean.WorktaskBean.WorkTaskURLType;
import com.yudean.homepage.bean.WorktaskUserBean;
import com.yudean.homepage.bean.WorktaskBean.TaskType;
import com.yudean.homepage.bean.WorktaskBean.WorkTaskClass;
import com.yudean.homepage.dao.HomepageWorktaskDao;
import com.yudean.homepage.dao.HomepageWorktaskUserDao;
import com.yudean.homepage.dao.SecProcRouteDao;
import com.yudean.homepage.interfaces.HomepageNotifyInterface;
import com.yudean.homepage.service.HomepageFrontService;
import com.yudean.homepage.service.HomepagePortalService;
import com.yudean.homepage.service.core.IHomepageServiceImpl;
import com.yudean.homepage.vo.WorktaskViewObj;
import com.yudean.itc.code.ParamConfig;
import com.yudean.itc.code.StatusCode;
import com.yudean.itc.dto.Page;
import com.yudean.itc.dto.sec.Organization;
import com.yudean.itc.dto.sec.SecProcRoute;
import com.yudean.itc.dto.sec.SecProcRoute.VisibleType;
import com.yudean.itc.exception.homepage.homePageServiceException;
import com.yudean.itc.manager.sec.IAuthorizationManager;
import com.yudean.itc.util.UUIDGenerator;
import com.yudean.mvc.bean.userinfo.UserInfo;
import com.yudean.mvc.bean.userinfo.impl.UserInfoImpl;
import com.yudean.mvc.exception.RuntimeDataNotFoundException;
import com.yudean.mvc.service.ItcMvcService;

@Service
public class TaskFacade implements ITaskFacade {
	
	private static final Logger LOG = Logger.getLogger( IHomepageServiceImpl.class );

	@Autowired
	HomepageWorktaskDao worktaskDao;
	
    @Autowired
    SecProcRouteDao secProcRouteDao;
    
    @Autowired
    HomepageWorktaskUserDao taskuserDao;
    
    @Autowired
    @Qualifier("HomepageSendWhenProcess")
    HomepageNotifyInterface notifyProcess;
    
    @Autowired
    @Qualifier("HomepageSendWhenComplete")
    HomepageNotifyInterface notifyComplete;

    @Autowired
    IAuthorizationManager authManager;
    
    @Autowired
    ItcMvcService itcMvcService;

    @Autowired
    private HomepagePortalService homepageNoticeService;

	
	@Override
	@Transactional(rollbackFor = { Exception.class })
	public String create(String flowNo, String extCode, String typeName,
			String name, String statusName,String url, UserInfo userInfo,
			ProcessFucExtParam extParam) {

        if ( null == extCode || "".equals( extCode ) ) {
        	extCode = flowNo;
        }
        // by ahua
        VisibleType visibleType = getVisibleType( extParam );

        WorktaskBean task = this.getTaskByFlowNo( flowNo, userInfo ); //查询是否已经有数据，如果是草稿的话，就会已经有数据的。但是会存在二次

        WorktaskBean.WorkTaskClass worktaslClass = WorktaskBean.WorkTaskClass.Draft;

        String pk = null;
        if ( null == task ) {
            String seq = getMainSeq();
            Date curDate = new Date();
            WorktaskBean taskBean = new WorktaskBean();
            taskBean.setId( seq );
            taskBean.setGroupid( seq );
            taskBean.setFlowno( flowNo );
            taskBean.setTypename( typeName );
            taskBean.setName( name );
            taskBean.setStatusname( null == statusName || "".equals( statusName ) ? "草稿" : statusName );
            taskBean.setStatusdate( curDate );
            taskBean.setCreateuser( userInfo.getUserId());
            taskBean.setCreateusername( userInfo.getUserName() );
            taskBean.setCreatedate( curDate );
            taskBean.setSiteid( userInfo.getSiteId() );
            taskBean.setDeptid( userInfo.getOrgId() );
            taskBean.setDeptname( userInfo.getOrgName() );
            taskBean.setClasstype( worktaslClass );
            taskBean.setUrl( url );
            taskBean.setUrltype( WorktaskBean.WorkTaskURLType.Tab );
            taskBean.setExtCode( extCode );
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
            userBean.setModifyuser( userInfo.getUserId() );
            userBean.setModifydate( curDate );
            userBean.setSiteid( userInfo.getSiteId() );
            userBean.setDeptid( userInfo.getOrgId() );
            userBean.setFlag( WorktaskUserBean.WorkTaskUserFlag.His ); //为什么是历史的呢？
            taskuserDao.insertWorktaskUser( userBean );

            updateAndSaveSecProcRout( taskBean.getFlowno(), userInfo.getUserId(), userInfo.getSiteId(),
                    userInfo.getOrgs(), visibleType );
        } else {
            pk = task.getId();
            Date curDate = new Date();
            WorktaskBean taskBean = new WorktaskBean();
            taskBean.setId( task.getId() );
            taskBean.setName( null == name || "".equals( name ) ? task.getName()
                    : name );
            taskBean.setStatusname( null == statusName || "".equals( statusName ) ? task
                    .getStatusname() : statusName );
            taskBean.setExtCode( extCode );
            taskBean.setStatusdate( curDate );
            taskBean.setModifyuser( userInfo.getUserId() );
            taskBean.setModifyusername( userInfo.getUserName() );
            taskBean.setModifydate( curDate );
            taskBean.setClasstype( worktaslClass );
            taskBean.setSiteid( userInfo.getSiteId() );
            worktaskDao.updateWorktask( taskBean );

            updateAndSaveSecProcRout( task.getFlowno(), userInfo.getUserId(), userInfo.getSiteId(), userInfo.getOrgs(),
                    visibleType );
        }
        return pk;
	}

	@Override
	public String createProcess(String flow, String extCode, String typeName,
			String name, String statusName, String url, TaskType tasktype,
			UserInfo userInfo, ProcessFucExtParam extParam)
			throws RuntimeException {
		
		 // by ahua
        VisibleType visibleType = getVisibleType( extParam );
        // by hxl
        Boolean isSupProcess = getSubProcessFlag( extParam );

        WorktaskBean task = this.getTaskByExtCode( extCode, userInfo );
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
            taskBean.setExtCode( extCode );
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
        	//这个会执行么？
            pk = task.getId();
        }
        return pk;
	}

	@Override
	public void execTask(String extCode, String statusName, String name,
			List<String> operUser, UserInfo userInfo,
			ProcessFucExtParam extParam) throws RuntimeException {
		 //by ahua
        VisibleType visibleType = getVisibleType( extParam );
        for ( String userId : operUser ) { // 为下一个节点的候选人都添加到路由表中
            List<Organization> orgs = authManager.retriveUserById( userId, userInfo.getSiteId() ).getOrganizations();
            if ( null != visibleType ) { // 如果设置了这个参数
                String userCode = userId;
                String siteId = userInfo.getSiteId();
                // List<Organization> orgs = userInfo.getOrgs(); //候选人所在的部门列表
                WorktaskBean queryMainTask = getTaskByExtCode( extCode, userInfo ); // 不跨站点
                if ( null == queryMainTask ) {
                    LOG.error( "流程实例未查询到，请检测是否创建实例，实例编号：" + extCode + "。当前操作人" + userInfo.getUserId() );
                    throw new RuntimeDataNotFoundException( "工作任务数据未找到" );
                }
                // 插入路由表
                updateAndSaveSecProcRout( queryMainTask.getFlowno(), userCode, siteId, orgs, visibleType );
            }
        }
        this.Process( extCode, statusName, operUser, userInfo, notifyProcess );
		
	}

	@Override
	public void execTaskWithProcess(String taskId, String processInstId, String statusName,
			List<String> operUser, UserInfo userInfo,
			ProcessFucExtParam extParam) throws RuntimeException {
		
        Set<UserInfo> operUserList = new HashSet<UserInfo>();

        if ( null != extParam && extParam.isSiteGetRoute() ) {
            WorktaskBean taskBean = new WorktaskBean();
            taskBean.setExtCode( processInstId );
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
                    for ( String userId : operUser ) { // 为下一个节点的候选人都添加到路由表中
                        UserInfoImpl userinfo = new UserInfoImpl();
                        userinfo.setId( userId );
                        String siteId = map.get( userId );
                        userinfo.setCurrentSite( null == siteId ? userInfo.getSiteId() : siteId );
                        // 获取部门信息
                        // by ahua 此处的部门不和登录人的部门一样，所有需要重新查
                        // 根据工号和站点查询信息，以及所属的部门
                        List<Organization> orgs = authManager.retriveUserById( userId, userInfo.getSiteId() )
                                .getOrganizations();
                        userinfo.setOrganizations( orgs );
                        operUserList.add( userinfo );
                    }
                }
            }
        } else {
            for ( String userId : operUser ) { // 为下一个节点的候选人都添加到路由表中
                UserInfoImpl userinfo = new UserInfoImpl();
                userinfo.setId( userId );
                userinfo.setCurrentSite( userInfo.getSiteId() );
                // 获取部门信息
                // by ahua 此处的部门不和登录人的部门一样，所有需要重新查
                // 根据工号和站点查询信息，以及所属的部门
                List<Organization> orgs = authManager.retriveUserById( userId, userInfo.getSiteId() )
                        .getOrganizations();
                userinfo.setOrganizations( orgs );
                operUserList.add( userinfo );
            }
        }
        this.Process( taskId, processInstId, statusName, operUserList, userInfo, notifyProcess, extParam );
		
	}

	@Override
	public void execTaskWithProcess(String taskId, String ProcessInstId, String statusName,
			Set<UserInfo> operUser, UserInfo userInfo,
			ProcessFucExtParam extParam) throws RuntimeException {

		this.Process( taskId, ProcessInstId, statusName, operUser, userInfo, notifyProcess, extParam );
	}

	@Override
	public void deleteProcess(String taskId, String ProcessInstId,
			UserInfo userInfo) throws RuntimeException {
		
        taskId = null == taskId || "".equals( taskId ) ? "NaN" : taskId;
        WorktaskBean querytask = getTaskByExtCodeAUserSite( taskId, ProcessInstId, userInfo );// 查询原始内容
        if ( null != querytask ) {//第一个节点时，这个querytask为null，因为第一个节点对应的任务为main，其extcode为pid，parentextcode为null
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

	@Override
	public void delete(String extCode, UserInfo userInfo)
			throws RuntimeException {
		
        HomepageNotifyInterface notify = null;
        this.Delete( extCode, userInfo, notify );
	}

	@Override
	public void complete(String extCode, UserInfo userInfo, String statusName)
			throws RuntimeException {
		
        Date curDate = new Date();// 获取当前更新日期
        WorktaskBean querytask = getTaskByExtCodeAUserSite( extCode, userInfo );// 通过实例ID查询表单是否存在
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
        if ( null != notifyComplete && WorktaskBean.TaskType.Main.equals( querytask.getTaskType() ) ) {// 发送通知邮件
            switch (querytask.getTaskType()) {
                case Main: {
                    if ( null != notifyComplete ) {
                        // 主任务才发送通知邮件
                    	notifyComplete.notify( querytask.getFlowno(), extCode, querytask.getTypename(),
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
	public void modify(String flowNo, String name, UserInfo userInfo)
			throws RuntimeException {

		WorktaskBean taskBean = new WorktaskBean();
        taskBean.setFlowno( flowNo );
        taskBean.setName( name );
        worktaskDao.updateWorktask( taskBean );// 更新该任务的name
		
	}

	@Override
	public WorktaskBean queryTask(String extCode,UserInfo userInfo) {
		
		return getTaskByExtCodeAUserSite(extCode,userInfo);
	}

	@Override
	public String createTask(String flowNo, String extCode, String typeName,
			String name, String statusName, String url,
			List<UserInfo> operUser, UserInfo userInfo,
			ProcessFucExtParam extParam) throws RuntimeException {
		
		 // by ahua
        VisibleType visibleType = getVisibleType( extParam );
        String pk = null;
        WorktaskBean task = this.getTaskByExtCode( extCode, userInfo );
        String seq = task == null?getMainSeq():task.getId();
        Date curDate = new Date();
        WorktaskBean taskBean = new WorktaskBean();
        taskBean.setId( seq );
        taskBean.setGroupid( seq );
        taskBean.setFlowno( flowNo );
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
        taskBean.setClasstype( WorktaskBean.WorkTaskClass.Processed);
        taskBean.setUrl( url );
        taskBean.setUrltype( WorktaskBean.WorkTaskURLType.Tab );
        taskBean.setExtCode( extCode );
        taskBean.setParentExtCode( "NaN" );
        taskBean.setActive( WorktaskBean.ACTIVEFLAG.ACTIVE );
        taskBean.setTaskType( WorktaskBean.TaskType.Main );
        if(task == null){
        	worktaskDao.insertWorktask( taskBean );
        }else{
        	worktaskDao.updateWorktask(taskBean);
        }
        pk = taskBean.getId();
        
        
        
        for ( int i = 0; operUser.size() > i; i++ ) {
        	UserInfo operUserInfo = operUser.get( i );
            WorktaskUserBean operUserBean = new WorktaskUserBean();
            operUserBean.setId(pk);
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
        }

        updateAndSaveSecProcRout( taskBean.getFlowno(), userInfo.getUserId(), userInfo.getSiteId(),
                userInfo.getOrgs(), visibleType );
        return pk;
	}
	
	@Override
	public void deleteTask(String extCode,UserInfo userInfo) {
		
		delete(extCode, userInfo);
		
	}

    private VisibleType getVisibleType(ProcessFucExtParam extParam) {
        if ( extParam != null ) {
            return extParam.getVisibleType();
        } else {
            return VisibleType.V_USER;
        }
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
    // TODO by ahua 插入路由表数据，带可见类型和部门信息
    private void updateAndSaveSecProcRout(String flow, String userid, String siteid, List<Organization> orgs,
            VisibleType visibleType) throws RuntimeException {
        if ( null != orgs && !orgs.isEmpty() ) {
            insertSecProcRoute( flow, userid, siteid, orgs.get( 0 ).getCode(), visibleType );
        } else {
            insertSecProcRoute( flow, userid, siteid, null, visibleType );
        }
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
    
    // 获取是否是子流程的标志
    private Boolean getSubProcessFlag(ProcessFucExtParam extParam) {
        if ( extParam == null ) {
            return false;
        } else {
            return extParam.getHasSubProcess();
        }
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
    
    //处理不走流程的？
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
        for ( int i = 0; operUser.size() > i; i++ ) {
            String userCode = operUser.get( i );
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
        if ( null != notify ) {
            notify.notify( querytask.getFlowno(), processInstId, querytask.getTypename(), querytask.getName(),
                    statusName, querytask.getUrl(), operUser, userInfo, querytask );
        }
    }
    
    public void Process(String taskId, String ProcessInstId, String statusName, Set<UserInfo> operUser,
            UserInfo userInfo, HomepageNotifyInterface notify, ProcessFucExtParam extParam) throws RuntimeException {
        WorktaskBean querytask = getTaskByExtCode( taskId, ProcessInstId, userInfo );
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

            List<String> sendUserList = new ArrayList<String>();

            for ( UserInfo user : operUser ) {
                String userCode = user.getUserId();
                String siteId = null == user.getSiteId() || "".equals( user.getSiteId() ) ? userInfo.getSiteId() : user
                        .getSiteId();
                sendUserList.add( userCode );
                UserInfo operUserInfo = itcMvcService.getUserInfoById( userCode );
                WorktaskUserBean operUserBean = new WorktaskUserBean();
                operUserBean.setId( queryMainTask.getId() );
                operUserBean.setSubid( seq );
                operUserBean.setUsercode( operUserInfo.getUserId() );
                operUserBean.setUsername( operUserInfo.getUserName() );
                operUserBean.setFlag( WorktaskUserBean.WorkTaskUserFlag.Cur );
                operUserBean.setSiteid( siteId );
                operUserBean.setDeptid( userInfo.getOrgId() );
                operUserBean.setCreateuser( userInfo.getUserId() );
                operUserBean.setCreatedate( curDate );
                operUserBean.setModifyuser( userInfo.getUserId() );
                operUserBean.setModifydate( curDate );
                UpdateAndSaveUser( operUserBean );
                // TODO by ahua 在其上一级函数中已经插入路由表了，所以注释掉
                List<Organization> orgs = authManager.retriveUserById( userCode, siteId ).getOrganizations();
                // 插入路由表
                updateAndSaveSecProcRout( queryMainTask.getFlowno(), userCode, siteId, orgs, getVisibleType( extParam ) );
                // by hxl 如果是子流程需要更新父流程路由表
                Boolean isSupProcess = getSubProcessFlag( extParam );
                if ( isSupProcess ) {
                    updateAndSaveSecProcRout( extParam.getParentBusinessId(), userCode, siteId, orgs,
                            getVisibleType( extParam ) );
                }
            }

            if ( null != notify ) {
                notify.notify( queryMainTask.getFlowno(), queryMainTask.getExtCode(), queryMainTask.getTypename(),
                        queryMainTask.getName(), statusName, queryMainTask.getUrl(), sendUserList, userInfo,
                        queryMainTask );
            }
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
        }
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
     * 更新或添加用户信息
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-7-10
     * @param userBean
     * @return
     * @throws RuntimeException :
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
    
    public List<SecProcRoute> getSecRouteInfo(String flowNo, String userId, String siteId) {
        SecProcRoute secProcRoute = new SecProcRoute();
        secProcRoute.setFlowid( flowNo );
        secProcRoute.setUserid( userId );
        secProcRoute.setSiteid( siteId );
        secProcRoute.setVisibleType( null );
        return secProcRouteDao.selectSecProcRoute( secProcRoute );
    }


}
