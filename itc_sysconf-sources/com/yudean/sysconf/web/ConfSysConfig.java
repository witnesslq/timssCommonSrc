package com.yudean.sysconf.web;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yudean.itc.dto.Page;
import com.yudean.itc.dto.batch.TaskInfo;
import com.yudean.itc.dto.sec.Site;
import com.yudean.itc.webservice.batch.ISchedulerService;
import com.yudean.mvc.service.ItcMvcService;
import com.yudean.mvc.util.ViewUtil;
import com.yudean.mvc.view.ModelAndViewAjax;

/**
 * TIMSS框架，系统维护页面
 * 
 * @title: {title}
 * @description: {desc}
 * @company: gdyd
 * @className: FrameworkSysConfig.java
 * @author: kChen
 * @createDate: 2014-7-14
 * @updateUser: kChen
 * @version: 1.0
 */
@Controller
@RequestMapping(value = "sysconf/systemConfig")
public class ConfSysConfig {
    private static final Logger log = Logger.getLogger( ConfSysConfig.class );

    @Autowired
    private ItcMvcService itcMvcService;

    // @Autowired
    // private ISecSiteInfoManager secSiteInfoManager;

    private ISchedulerService schedulerService = null;

    private Boolean SchedulerServiceExists = null;

    @RequestMapping(value = "/QuartzConfigPage")
    public String quartzConfigPage(HttpServletRequest request) throws Exception {
        return "/systemConfig/quartz/timelist.jsp";
    }

    @RequestMapping(value = "/timelist")
    public String quartzTimePage(HttpServletRequest request, HttpServletResponse response) throws Exception {

        return "/systemConfig/quartz/timelist.jsp";
    }

    @RequestMapping(value = "/timelistInfo")
    public Page<TaskInfo> quartzTimeList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Page<TaskInfo> page = itcMvcService.getUserInfoScopeDatas().getPage();
        if ( getISchedulerService() ) {
            page = schedulerService.queryPageTask( page );
        } else {

        }
        return page;
    }

    @RequestMapping(value = "/timedetailpage")
    public String quartzTimedetailpage(HttpServletRequest request, HttpServletResponse response) throws Exception {

        return "/systemConfig/quartz/timedetail.jsp";
    }

    /**
     * 模糊查询站点
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sitefuzzyinfo")
    public ModelAndViewAjax QuerySite(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String kw = request.getParameter( "kw" );
        log.debug( kw );
        Pattern p = Pattern.compile( "[\u4e00-\u9fa5]" );
        Matcher m = p.matcher( kw );
        log.debug( m.find() );
        JSONArray array = new JSONArray();
        if ( null != kw && !"".equals( kw ) ) {
            /*
             * String id = null; String name = null; if ( m.find() ) { id =
             * null; name = kw; } else { id = kw; name = null; } List<Site>
             * siteList = secSiteInfoManager.getSiteInfo(id, name, true);
             */
            List<Site> siteList = new ArrayList<Site>();
            int count = 0;
            if ( null != siteList && 0 < siteList.size() ) {
                for ( Site site : siteList ) {
                    JSONObject object = new JSONObject();
                    object.put( "id", site.getId() );
                    object.put( "name", site.getName() + "(" + site.getId() + ")" );
                    array.add( object );
                    if ( count++ > 9 ) {
                        break;
                    }
                }
            }
        } else {
            JSONObject object = new JSONObject();
            object.put( "id", "" );
            object.put( "name", "" );
            array.add( object );
        }
        log.debug( array.toString() );
        return ViewUtil.Json( array.toString() );
    }

    private boolean getISchedulerService() {
        if ( null == SchedulerServiceExists ) {
            try {
                schedulerService = itcMvcService.getBeans( ISchedulerService.class );
            } catch (RuntimeException re) {
                log.debug( "schedulerService 服务不存在", re );
                SchedulerServiceExists = false;
            }
            if ( null != schedulerService ) {
                SchedulerServiceExists = true;
            } else {
                SchedulerServiceExists = false;
            }
        }
        return SchedulerServiceExists;
    }

}
