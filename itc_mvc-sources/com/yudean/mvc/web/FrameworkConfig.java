package com.yudean.mvc.web;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yudean.itc.code.StatusCode;
import com.yudean.itc.dto.Page;
import com.yudean.itc.dto.sec.SecureExclusiveRule;
import com.yudean.itc.dto.support.AppEnum;
import com.yudean.itc.dto.support.AppEnumCat;
import com.yudean.itc.dto.support.AppEnumCatVO;
import com.yudean.itc.dto.support.AppLog;
import com.yudean.itc.dto.support.AppLogCategory;
import com.yudean.itc.dto.support.Configuration;
import com.yudean.itc.manager.support.ISystemLogManager;
import com.yudean.itc.util.json.JsonHelper;
import com.yudean.mvc.bean.userinfo.UserInfoScope;
import com.yudean.mvc.service.ItcEnumConfService;
import com.yudean.mvc.service.ItcMvcService;
import com.yudean.mvc.service.ItcRuleConfService;
import com.yudean.mvc.service.ItcSysConfService;
import com.yudean.mvc.service.impl.FrameWorkServiceImpl;
import com.yudean.mvc.view.ModelAndViewAjax;
import com.yudean.mvc.view.viewResolver.JsonView;

/**
 * @title: 枚举配置
 * @description: 枚举配置
 * @company: gdyd
 * @className: ConfEnumConfig.java
 * @author: yuanzh
 * @createDate: 2015-9-2
 * @updateUser: yuanzh
 * @version: 1.0
 */
@Controller
@RequestMapping(value = "sysconf/sysConfig")
public class FrameworkConfig {

    private static final Logger LOG = Logger.getLogger( FrameworkConfig.class );

    @Autowired
    private ItcMvcService itcMvcService;

    @Autowired
    private ItcEnumConfService itcEnumConfService;

    @Autowired
    private ItcRuleConfService itcRuleConfService;

    @Autowired
    private ItcSysConfService itcSysConfService;

    @Autowired
    private ISystemLogManager iSystemLogManager;

    /**
     * @description: 系统配置中的枚举变量配置功能
     * @author: yuanzh（改造）
     * @createDate: 2015-8-27
     * @return
     * @throws Exception:
     */
    @RequestMapping(value = "/enumConfigPage")
    public String enumConfigPage() throws Exception {
        return "/enumConfig/enumConfigList.jsp";
    }

    /**
     * @description:系统配置中的权限例外配置功能
     * @author: yuanzh
     * @createDate: 2016-3-10
     * @return
     * @throws Exception:
     */
    @RequestMapping(value = "/ruleConfigPage")
    public String ruleConfigPage() throws Exception {
        return "/ruleConfig/ruleConfigList.jsp";
    }

    /**
     * @description:枚举列表查询
     * @author: yuanzh
     * @createDate: 2015-8-27
     * @param search
     * @return
     * @throws Throwable
     */
    @RequestMapping(value = "/queryBEnumCatList", method = RequestMethod.POST)
    public Page<AppEnumCatVO> queryBEnumCatList(String search) throws Throwable {
        LOG.debug( "------------------枚举列表查询------------------ queryBEnumCatList " );
        AppEnumCatVO becv = new AppEnumCatVO();
        UserInfoScope userInfo = itcMvcService.getUserInfoScopeDatas();
        // 若表头查询参数不为空
        if ( StringUtils.isNotBlank( search ) ) {
            becv = JsonHelper.fromJsonStringToBean( search, AppEnumCatVO.class );
        }
        LOG.debug( "------------------枚举列表查询完毕------------------ queryBEnumCatList " );
        return itcEnumConfService.queryBEnumCatList( userInfo, becv );
    }

    /**
     * @description: 跳转枚举表单
     * @author: yuanzh
     * @createDate: 2015-8-27
     * @param ecatCode
     * @return
     * @throws Exception:
     */
    @RequestMapping(value = "/bEnumForm", method = RequestMethod.GET)
    public ModelAndView bEnumForm(@RequestParam String ecatCode, @RequestParam String enumType) throws Exception {
        LOG.debug( "------------------跳转枚举表单------------------ bEnumForm " );
        ModelAndView mav = new ModelAndView( "/enumConfig/enumConfigForm.jsp" );
        mav.addObject( "ecatCode", ecatCode );
        mav.addObject( "enumType", enumType );
        LOG.debug( "------------------跳转枚举表单完毕------------------ bEnumForm " );
        return mav;
    }

    /**
     * @description: 异步查询枚举主类信息
     * @author: yuanzh
     * @createDate: 2015-8-27
     * @param imaid
     * @return
     * @throws Throwable
     */
    @RequestMapping(value = "/queryBEnumCatForm", method = RequestMethod.POST)
    public AppEnumCat queryBEnumCatForm(String ecatCode) throws Throwable {
        LOG.debug( "------------------异步查询枚举主类信息------------------ queryBEnumCatForm " );
        UserInfoScope userInfo = itcMvcService.getUserInfoScopeDatas();
        return itcEnumConfService.queryBEnumCatByCode( userInfo, ecatCode );
    }

    /**
     * @description: 查询枚举子类列表
     * @author: yuanzh
     * @createDate: 2015-8-27
     * @param ecatCode
     * @return
     * @throws Throwable
     */
    @RequestMapping(value = "/queryBEnumList", method = RequestMethod.POST)
    public Page<AppEnum> queryBEnumList(String ecatCode, String enumType) throws Throwable {
        UserInfoScope userInfo = itcMvcService.getUserInfoScopeDatas();
        Page<AppEnum> page = null;
        LOG.debug( "------------------查询枚举变量------------------ ecatCode： " + ecatCode );
        if ( !("").equals( ecatCode ) ) {
            LOG.debug( "------------------ecatCode------------------ 不为空 " );
            page = itcEnumConfService.queryBEnumList( userInfo, ecatCode, enumType );
        } else {
            LOG.debug( "------------------ecatCode------------------ 为空 " );
            page = userInfo.getPage();
        }
        LOG.debug( "------------------查询完毕------------------  " );
        return page;
    }

    /**
     * @description: 删除枚举
     * @author: yuanzh
     * @createDate: 2015-8-28
     * @param ecatCode
     * @return
     * @throws Throwable
     */
    @RequestMapping(value = "/deleteEnum", method = RequestMethod.POST)
    public Map<String, Object> deleteEnum(String ecatCode) throws Exception {
        LOG.debug( "------------------删除枚举------------------ deleteEnum " );
        Map<String, Object> result = new HashMap<String, Object>();
        UserInfoScope userInfo = itcMvcService.getUserInfoScopeDatas();
        boolean flag = itcEnumConfService.deleteEnum( userInfo, ecatCode );
        if ( flag ) {
            result.put( "result", "success" );
        } else {
            result.put( "result", "false" );
        }
        LOG.debug( "------------------删除枚举完毕------------------ deleteEnum " );
        return result;
    }

    /**
     * @description:判断枚举编码是否存在
     * @author: yuanzh
     * @createDate: 2015-8-28
     * @return
     * @throws Throwable
     */

    @RequestMapping(value = "/verifyEnumExist", method = RequestMethod.POST)
    @ResponseBody
    public Boolean verifyEnumExist() throws Throwable {
        LOG.debug( "------------------判断枚举编码是否存在------------------ verifyEnumExist " );
        UserInfoScope userInfo = itcMvcService.getUserInfoScopeDatas();
        String ecatCode = userInfo.getParam( "ecatCode" );
        AppEnumCat bEnumCat = itcEnumConfService.queryBEnumCatByCode( userInfo, ecatCode );
        if ( null == bEnumCat ) {
            return true;
        }
        return false;
    }

    /**
     * @description: 枚举保存方法
     * @author: yuanzh
     * @createDate: 2015-8-28
     * @param formData
     * @param listData
     * @param enumType
     * @return
     * @throws Throwable:
     */
    @RequestMapping(value = "/saveEnumVal", method = RequestMethod.POST)
    public Map<String, Object> saveEnumVal(String formData, String listData, String enumType) throws Exception {
        LOG.debug( "------------------枚举保存方法------------------ saveEnumVal" );
        Map<String, Object> result = new HashMap<String, Object>();
        UserInfoScope userInfo = itcMvcService.getUserInfoScopeDatas();
        AppEnumCat bec = JsonHelper.fromJsonStringToBean( formData, AppEnumCat.class );
        List<AppEnum> beList = JsonHelper.toList( listData, AppEnum.class );
        boolean flag = itcEnumConfService.saveEnumInfo( userInfo, bec, beList );
        if ( flag ) {
            result.put( "ecatCode", bec.getEcatCode() );
            result.put( "enumType", enumType == null ? userInfo.getSiteId() : enumType );
            result.put( "result", "success" );
        } else {
            result.put( "result", "false" );
        }
        LOG.debug( "------------------枚举保存方法完毕------------------ saveEnumVal" );
        return result;
    }

    /**
     * @description: 系统配置中的系统变量配置功能
     * @author: gucw（改造）
     * @createDate: 2015-9-02
     * @return
     * @throws Exception:
     */
    @RequestMapping(value = "/sysConfigPage")
    public ModelAndView sysConfigPage() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>( 0 );
        UserInfoScope userInfo = itcMvcService.getUserInfoScopeDatas();
        StatusCode isSuperAdmin = userInfo.getSecureUser().getSuperAdminInd();
        map.put( "isSuperAdmin", null != isSuperAdmin && "Y".equals( isSuperAdmin.toString() ) );
        ModelAndView mav = new ModelAndView( "/systemConfig/sysConfigList.jsp", map );
        return mav;
    }

    /**
     * @description: 系统配置中的编辑/新增系统变量配置功能
     * @author: gucw（改造）
     * @createDate: 2015-9-06
     * @return
     * @throws Exception:
     */
    @RequestMapping(value = "/editSysConfig")
    public ModelAndView editSysConfig() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>( 0 );
        UserInfoScope userInfo = itcMvcService.getUserInfoScopeDatas();
        String conf = userInfo.getParam( "conf" );
        String siteId = userInfo.getParam( "siteId" );
        Configuration sysConf = null;
        try {
            sysConf = itcSysConfService.queryBSysById( conf, siteId );
        } catch (Throwable e) {
            LOG.info( "查询系统配置时出错", e );
        }
        map.put( "sysConf", JsonHelper.fromBeanToJsonString( sysConf ) );
        ModelAndView mav = new ModelAndView( "/systemConfig/sysConfigForm.jsp", map );
        return mav;
    }

    /**
     * @description: 保存系统变量配置功能
     * @author: gucw
     * @createDate: 2015-9-06
     * @return
     * @throws Exception:
     */
    @RequestMapping(value = "/saveSysConf")
    public ModelAndViewAjax saveSysConf() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>( 0 );
        Map<String, Object> result = new HashMap<String, Object>( 0 );
        UserInfoScope userInfo = itcMvcService.getUserInfoScopeDatas();
        String sysConfFormStr = userInfo.getParam( "sysConfForm" );
        String oldSysConfStr = userInfo.getParam( "oldSysConf" );
        String siteId = userInfo.getParam( "siteId" );
        Configuration sysConf = JsonHelper.fromJsonStringToBean( sysConfFormStr, Configuration.class );
        sysConf.setsiteId( siteId );
        Configuration oldSysConf = null;
        if ( StringUtils.isNotEmpty( oldSysConfStr ) ) {
            oldSysConf = JsonHelper.fromJsonStringToBean( oldSysConfStr, Configuration.class );
        }
        try {
            result = itcSysConfService.saveOrUpdateSysConf( userInfo, sysConf, oldSysConf );
        } catch (Throwable e) {
            LOG.info( "查询系统配置时出错", e );
        }
        map.put( "result", result );
        ModelAndViewAjax mav = new ModelAndViewAjax();
        mav.setView( new JsonView() );
        mav.addObject( FrameWorkServiceImpl.getRunEnvironmentData().getAjaxJsonDataflag(), map );
        return mav;
    }

    /**
     * @description: 删除系统变量配置功能
     * @author: gucw
     * @createDate: 2015-9-07
     * @return
     * @throws Exception:
     */
    @RequestMapping(value = "/deleteSysConf")
    public ModelAndViewAjax deleteSysConf() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>( 0 );
        Map<String, Object> result = new HashMap<String, Object>( 0 );
        UserInfoScope userInfo = itcMvcService.getUserInfoScopeDatas();
        String selecteds = userInfo.getParam( "selecteds" );
        List<Configuration> selectList = new ArrayList<Configuration>( 0 );
        if ( StringUtils.isNotEmpty( selecteds ) ) {
            selectList = JsonHelper.toList( selecteds, Configuration.class );
        }
        try {
            result = itcSysConfService.deleteSysConf( userInfo, selectList );
        } catch (Throwable e) {
            LOG.info( "查询系统配置时出错", e );
        }
        map.put( "result", result );
        ModelAndViewAjax mav = new ModelAndViewAjax();
        mav.setView( new JsonView() );
        mav.addObject( FrameWorkServiceImpl.getRunEnvironmentData().getAjaxJsonDataflag(), map );
        return mav;
    }

    /**
     * @description:系统参数列表查询
     * @author: gucw
     * @createDate: 2015-9-06
     * @param search
     * @return
     * @throws Throwable
     */
    @RequestMapping(value = "/querySysConfigList", method = RequestMethod.POST)
    public Page<Configuration> querySysConfigList(String search) throws Throwable {
        LOG.debug( "------------------系统参数列表查询------------------ querySysConfigList " );
        Configuration sysConf = new Configuration();
        UserInfoScope userInfo = itcMvcService.getUserInfoScopeDatas();
        // 若表头查询参数不为空
        if ( StringUtils.isNotBlank( search ) ) {
            sysConf = JsonHelper.fromJsonStringToBean( search, Configuration.class );
        }
        LOG.debug( "------------------系统参数列表查询完毕------------------ querySysConfigList " );
        return itcSysConfService.queryBSysConfList( userInfo, sysConf );
    }

    /**
     * @description: 系统日志列表跳转
     * @author: zhuw
     * @createDate: 2015-9-30
     * @return
     * @throws Exception:
     */
    @RequestMapping(value = "/logConfigPage")
    public String logConfigPage() throws Exception {
        return "/logConfig/logConfigList.jsp";
    }

    /**
     * @description：日志列表查询
     * @author: zhuw
     * @createDate: 2015-9-30
     * @param search
     * @return
     */
    @RequestMapping(value = "/queryLogList", method = RequestMethod.POST)
    public Page<AppLog> queryLogList(String searchBy, String category) {

        Map<String, Object> map = new HashMap<String, Object>();
        UserInfoScope userInfo = itcMvcService.getUserInfoScopeDatas();
        Page<AppLog> page = userInfo.getPage();

        searchBy = StringUtils.trimToEmpty( searchBy );
        if ( StringUtils.isNotBlank( searchBy ) ) {
            map.put( "searchBy", searchBy );
            page.setParams( map );
        }
        if ( StringUtils.isNotBlank( category ) ) {
            if ( !"-1".equals( category ) ) {
                map.put( "category", category );
                page.setParams( map );
            }
        }
        // 默认只显示最近3个月的日志
        Calendar c = Calendar.getInstance();
        c.add( Calendar.MONTH, -3 );
        page.setParameter( "timeFrom", c.getTime() );

        page = iSystemLogManager.retrieveLogPage( page, userInfo.getSecureUser() );
        return page;
    }

    /**
     * @description：日志类别查询
     * @author: zhuw
     * @createDate: 2015-10-08
     * @param
     * @return
     */
    @RequestMapping(value = "/queryLogCategory", method = RequestMethod.POST)
    @ResponseBody
    public List<AppLogCategory> queryLogCategory() {
        List<AppLogCategory> list = iSystemLogManager.retrieveLogCategory();
        return list;
    }

    /**
     * @description:查询权限例外列表
     * @author: yuanzh
     * @createDate: 2016-3-11
     * @param search
     * @return
     * @throws Throwable:
     */
    @RequestMapping(value = "/querySecExclusiveRuleList", method = RequestMethod.POST)
    public Page<SecureExclusiveRule> querySecExclusiveRuleList(String search) throws Throwable {
        LOG.debug( "------------------权限例外列表查询------------------ querySecExclusiveRuleList " );
        SecureExclusiveRule secExclusiveRule = null;
        Page<SecureExclusiveRule> page = new Page<SecureExclusiveRule>();
        UserInfoScope userInfo = itcMvcService.getUserInfoScopeDatas();

        if ( StringUtils.isNotBlank( search ) ) {
            secExclusiveRule = JsonHelper.fromJsonStringToBean( search, SecureExclusiveRule.class );
        }
        page = itcRuleConfService.querySecExclusiveRuleList( userInfo, secExclusiveRule );
        LOG.debug( "------------------权限例外列表查询------------------ querySecExclusiveRuleList " );
        return page;
    }

    /**
     * @description:保存权限例外功能
     * @author: yuanzh
     * @createDate: 2016-3-11
     * @param listData
     * @return
     * @throws Throwable
     */
    @RequestMapping(value = "/saveSecExclusiveRule", method = RequestMethod.POST)
    public Map<String, Object> saveSecExclusiveRule(String listData) throws Throwable {
        Map<String, Object> result = new HashMap<String, Object>();
        List<SecureExclusiveRule> serList = JsonHelper.toList( listData, SecureExclusiveRule.class );
        UserInfoScope userInfo = itcMvcService.getUserInfoScopeDatas();
        itcRuleConfService.saveSecExclusiveRule( userInfo, serList );
        return result;
    }

}
