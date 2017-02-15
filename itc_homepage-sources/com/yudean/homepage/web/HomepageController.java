package com.yudean.homepage.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yudean.homepage.bean.WorktaskBean;
import com.yudean.homepage.bean.WorktaskBean.WorkTaskClass;
import com.yudean.homepage.bean.WorktaskUserBean;
import com.yudean.homepage.bean.WorktaskUserBean.WorkTaskUserFlag;
import com.yudean.homepage.service.HomepageFrontService;
import com.yudean.homepage.vo.WorktaskMsgViewObj;
import com.yudean.homepage.vo.WorktaskViewObj;
import com.yudean.mvc.bean.userinfo.UserInfoScope;
import com.yudean.mvc.service.ItcMvcService;
import com.yudean.itc.code.ParamConfig;
import com.yudean.itc.dto.Page;
import com.yudean.itc.util.StringHelper;
import com.yudean.itc.util.json.JsonHelper;
import com.yudean.itc.util.map.MapHelper;

@Controller
@RequestMapping(value = "homepage/Info")
public class HomepageController {
    private static final Logger LOG = Logger.getLogger( HomepageController.class );
    private final String s_flag = "flag";
    private final String s_ERRFlag = "ERR";
    private final String s_SUCFlag = "SUC";

    @Autowired
    private HomepageFrontService homeService;

    @Autowired
    private ItcMvcService itcMvcService;

    /**
     * 待办页面
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-7-10
     * @return:
     */
    @RequestMapping(value = "/DoingList")
    public String doingInfoPage(HttpServletRequest request) {
        // MvcConfig.host = "http://" + request.getLocalAddr() + ":" +
        // request.getServerPort() + request.getContextPath();
        return "/list/doingList.jsp";
    }

    /**
     * 待办列表
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-7-10
     * @return
     * @throws Exception :
     */
    @RequestMapping(value = "/DoingListInfo")
    public Page<WorktaskViewObj> DoingInfoList() throws Exception {
        Page<WorktaskViewObj> page = itcMvcService.getUserInfoScopeDatas().getPage();
        UserInfoScope scope = itcMvcService.getUserInfoScopeDatas();
        // 固有条件
        page.setParameter( ParamConfig.HOP_SITEID, scope.getSiteId() );
        page.setParameter( ParamConfig.HOP_UserCode, scope.getUserId() );
        page.setParameter( ParamConfig.HOP_ClassType, WorktaskBean.WorkTaskClass.Processed );
        page.setParameter( ParamConfig.HOP_Flag, WorktaskUserBean.WorkTaskUserFlag.Cur );

        // 前端条件查询
        String fuzzySearchParams = scope.getParam( "search" );
        if ( null != fuzzySearchParams ) {
            Map<String, Object> fuzzyParams = MapHelper.jsonToHashMap( fuzzySearchParams );
            page.setFuzzyParams( fuzzyParams );
        }

        // 前端排序
        String sortKey = scope.getParam( "sort" );
        String orderKey = scope.getParam( "order" );
        if ( null != sortKey ) {
            page.setSortKey( sortKey );
            page.setSortOrder( orderKey == null ? "asc" : orderKey );
        } else {
            page.setSortKey( "createdate" );
            page.setSortOrder( "desc" );
        }

        page = homeService.getDoingTaskList( page, scope );
        return page;
    }

    /**
     * 已办页面
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-7-10
     * @return:
     */
    @RequestMapping(value = "/DoneList")
    public String DoneInfoPage() {
        return "/list/doneList.jsp";
    }

    /**
     * 已办列表
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-7-10
     * @return
     * @throws Exception :
     */
    @RequestMapping(value = "/DoneListInfo")
    public Page<WorktaskViewObj> DoneInfoList() throws Exception {
        Page<WorktaskViewObj> page = itcMvcService.getUserInfoScopeDatas().getPage();
        UserInfoScope scope = itcMvcService.getUserInfoScopeDatas();
        // 固有条件
        page.setParameter( "siteid", scope.getSiteId() );
        page.setParameter( "usercode", scope.getUserId() );
        page.setParameter( "classtype", WorktaskBean.WorkTaskClass.Processed );
        page.setParameter( "flag", WorktaskUserBean.WorkTaskUserFlag.His );

        // 前端条件查询
        String fuzzySearchParams = scope.getParam( "search" );
        if ( null != fuzzySearchParams ) {
            Map<String, Object> fuzzyParams = MapHelper.jsonToHashMap( fuzzySearchParams );
            page.setFuzzyParams( fuzzyParams );
        }

        // 前端排序
        String sortKey = scope.getParam( "sort" );
        String orderKey = scope.getParam( "order" );
        if ( null != sortKey ) {
            page.setSortKey( sortKey );
            page.setSortOrder( orderKey == null ? "asc" : orderKey );
        } else {
            page.setSortKey( "createdate" );
            page.setSortOrder( "desc" );
        }

        page = homeService.getTaskList( page, scope );
        return page;
    }

    /**
     * 办毕页面
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-7-10
     * @return:
     */
    @RequestMapping(value = "/CompleteList")
    public String completeInfoPage() {
        return "/list/completeList.jsp";
    }

    /**
     * 办毕列表
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-7-10
     * @return
     * @throws Exception :
     */
    @RequestMapping(value = "/CompleteListInfo")
    public Page<WorktaskViewObj> completeInfoList() throws Exception {
        Page<WorktaskViewObj> page = itcMvcService.getUserInfoScopeDatas().getPage();
        UserInfoScope scope = itcMvcService.getUserInfoScopeDatas();
        // 固有条件
        page.setParameter( "siteid", scope.getSiteId() );
        page.setParameter( "usercode", scope.getUserId() );
        page.setParameter( "classtype", WorktaskBean.WorkTaskClass.Complete );
        page.setParameter( "flag", WorktaskUserBean.WorkTaskUserFlag.His );

        // 前端条件查询
        String fuzzySearchParams = scope.getParam( "search" );
        if ( null != fuzzySearchParams ) {
            Map<String, Object> fuzzyParams = MapHelper.jsonToHashMap( fuzzySearchParams );
            page.setFuzzyParams( fuzzyParams );
        }

        // 前端排序
        String sortKey = scope.getParam( "sort" );
        String orderKey = scope.getParam( "order" );
        if ( null != sortKey ) {
            page.setSortKey( sortKey );
            page.setSortOrder( orderKey == null ? "asc" : orderKey );
        } else {
            page.setSortKey( "createdate" );
            page.setSortOrder( "desc" );
        }

        page = homeService.getCompleteTaskList( page, scope );
        return page;
    }

    /**
     * 草稿页面
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-7-10
     * @return:
     */
    @RequestMapping(value = "/DraftList")
    public String draftInfoPage() {
        return "/list/draftList.jsp";
    }

    @RequestMapping(value = "/ProcessTaskCount")
    public Map<String, Object> taskCountInfo() throws Exception {
        Map<String, Object> retInfo = new HashMap<String, Object>();
        retInfo.put( s_flag, s_ERRFlag );
        UserInfoScope scope = itcMvcService.getUserInfoScopeDatas();
        try {
            retInfo.put( "count", homeService.getUserProcessTaskCount( scope.getUserId(), scope.getSiteId(), scope ) );
            retInfo.put( "draftCount", homeService.getUserTaskStaticCount( scope.getUserId(), scope.getSiteId(), WorkTaskClass.Draft, WorkTaskUserFlag.His ) );
            retInfo.put( s_flag, s_SUCFlag );
        } catch (Exception e) {
            LOG.error( "get process info error", e );
        }
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( StringHelper.concat( "user ", scope.getUserId(), "get process task count info" ) );
        }
        return retInfo;
    }

    /**
     * 草稿列表
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-7-10
     * @return
     * @throws Exception :
     */
    @RequestMapping(value = "/DraftListInfo")
    public Page<WorktaskViewObj> draftInfoList() throws Exception {
        Page<WorktaskViewObj> page = itcMvcService.getUserInfoScopeDatas().getPage();
        UserInfoScope scope = itcMvcService.getUserInfoScopeDatas();
        // 固有条件
        page.setParameter( "siteid", scope.getSiteId() );
        page.setParameter( "usercode", scope.getUserId() );
        page.setParameter( "classtype", WorktaskBean.WorkTaskClass.Draft );
        page.setParameter( "flag", WorktaskUserBean.WorkTaskUserFlag.His );

        // 前端条件查询
        String fuzzySearchParams = scope.getParam( "search" );
        if ( null != fuzzySearchParams ) {
            Map<String, Object> fuzzyParams = MapHelper.jsonToHashMap( fuzzySearchParams );
            page.setFuzzyParams( fuzzyParams );
        }

        // 前端排序
        String sortKey = scope.getParam( "sort" );
        String orderKey = scope.getParam( "order" );
        if ( null != sortKey ) {
            page.setSortKey( sortKey );
            page.setSortOrder( orderKey == null ? "asc" : orderKey );
        } else {
            page.setSortKey( "createdate" );
            page.setSortOrder( "desc" );
        }

        page = homeService.getTaskList( page, scope );
        return page;
    }

    @RequestMapping(value = "/DraftDelete")
    public WorktaskMsgViewObj draftDeleteOper() throws Exception {
        UserInfoScope scope = itcMvcService.getUserInfoScopeDatas();
        String deleteRows = scope.getParam( "deleteRows" );
        List<WorktaskViewObj> taskVoList = JsonHelper.toList( deleteRows, WorktaskViewObj.class );
        Integer ListCount = 9999;
        Integer Count = 0;
        try {
            if ( null != taskVoList && !taskVoList.isEmpty() ) {
                ListCount = taskVoList.size();
                Count = homeService.deleteTaskList( taskVoList, scope );
            }
        } catch (Exception e) {
            LOG.error( "删除草稿异常", e );
        }
        WorktaskMsgViewObj viewObj = new WorktaskMsgViewObj();
        if ( ListCount == Count ) {
            viewObj.setMsg( "删除成功" );
            viewObj.setOperCount( Count );
        } else if ( 0 < Count && Count < ListCount ) {
            viewObj.setMsg( "部分草稿删除失败" );
            viewObj.setOperCount( -1 );
        } else {
            viewObj.setMsg( "草稿删除失败" );
            viewObj.setOperCount( -1 );
        }

        return viewObj;
    }
}
