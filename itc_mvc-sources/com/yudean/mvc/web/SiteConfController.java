package com.yudean.mvc.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.yudean.itc.dao.sec.SiteMapper;
import com.yudean.itc.dto.Page;
import com.yudean.itc.dto.sec.Organization;
import com.yudean.itc.dto.sec.SecureUser;
import com.yudean.itc.dto.sec.Site;
import com.yudean.itc.manager.sec.ISecurityMaintenanceManager;
import com.yudean.itc.util.Constant;
import com.yudean.itc.util.json.JsonHelper;
import com.yudean.itc.util.map.MapHelper;
import com.yudean.mvc.bean.userinfo.UserInfoScope;
import com.yudean.mvc.service.ItcMvcService;
import com.yudean.mvc.service.ItcSiteService;
import com.yudean.mvc.service.impl.FrameWorkServiceImpl;
import com.yudean.mvc.util.ViewUtil;
import com.yudean.mvc.view.ModelAndViewAjax;
import com.yudean.mvc.view.viewResolver.JsonView;

@Controller
@RequestMapping("sysconf/siteConfig")
public class SiteConfController {

	private static final Logger LOGGER = Logger.getLogger(SiteConfController.class);
	
	@Autowired
	private ItcMvcService itcMvcService;
	
	@Autowired
	private ItcSiteService itcSiteService;
	
	@Autowired
	ISecurityMaintenanceManager iSecurityMaintenanceManager;
	
	@RequestMapping("/siteListPage.do")
	public String siteListPage(HttpServletRequest request){
		LOGGER.info("123213");
		return "/siteConfig/siteConfigList.jsp";
	}
	
	@RequestMapping("/siteForm.do")
	public String siteForm(){
		return "/sysconf/core/siteConfig/siteConfigForm.jsp";
	}
	
	@RequestMapping("/getSiteList.do")
	public Page<Site> getSiteList(String search) throws Exception{
		
		UserInfoScope userInfo = itcMvcService.getUserInfoScopeDatas();
		Site site = new Site();
		if(StringUtils.isNotBlank(search)){
			site = JsonHelper.fromJsonStringToBean(search, Site.class);
		}
		return itcSiteService.selectSite(site, userInfo);
	}
	
	@RequestMapping("/addSite.do")
	public ModelAndViewAjax addSite() throws Exception{
		Map<String, Object> map = new HashMap<String, Object>( 0 );
		Map<String, Object> result = new HashMap<String, Object>( 0 );
		UserInfoScope userInfo = itcMvcService.getUserInfoScopeDatas();
	    String siteFormStr = userInfo.getParam( "siteForm" );
	    String siteOrg = userInfo.getParam("siteOrg");
	    Site site = JsonHelper.fromJsonStringToBean(siteFormStr, Site.class);
	    site.setUpdatedBy(userInfo.getUserId());
	    site.setUpdateTime(new Date());
	    itcSiteService.insertSite(site,siteOrg);
	    result.put("success", true);
	    map.put( "result", result );
        ModelAndViewAjax mav = new ModelAndViewAjax();
        mav.setView( new JsonView() );
        mav.addObject( FrameWorkServiceImpl.getRunEnvironmentData().getAjaxJsonDataflag(), map );
        return mav;
	}
	
	@RequestMapping("/updateSite.do")
	public ModelAndViewAjax updateSite() throws Exception{
		
		Map<String, Object> map = new HashMap<String, Object>( 0 );
		Map<String, Object> result = new HashMap<String, Object>( 0 );
		UserInfoScope userInfo = itcMvcService.getUserInfoScopeDatas();
	    String siteFormStr = userInfo.getParam( "siteForm" );
	    String siteOrg = userInfo.getParam("siteOrg");
	    Site site = JsonHelper.fromJsonStringToBean(siteFormStr, Site.class);
	    site.setUpdatedBy(userInfo.getUserId());
	    site.setUpdateTime(new Date());
	    itcSiteService.updateSiteById(site,siteOrg);
	    result.put("success", true);
	    map.put( "result", result );
        ModelAndViewAjax mav = new ModelAndViewAjax();
        mav.setView( new JsonView() );
        mav.addObject( FrameWorkServiceImpl.getRunEnvironmentData().getAjaxJsonDataflag(), map );
        return mav;
	}
	
	@RequestMapping("/getSiteById.do")
	public ModelAndView getSiteById() throws Exception{
		Map<String, Object> map = new HashMap<String, Object>( 0 );
		UserInfoScope userInfo = itcMvcService.getUserInfoScopeDatas();
		String siteId = userInfo.getParam("siteId");
		Site site = itcSiteService.selectSingleSite(siteId);
		List<Map<String, Object>> siteOrgsList = itcSiteService.selectSiteOrg(null, siteId);
		if(siteOrgsList != null && siteOrgsList.size() != 0){
			String orgCode = (String)siteOrgsList.get(0).get("ORG_CODE");
			map.put( "orgCode", orgCode);
		}
	    map.put( "site", JsonHelper.fromBeanToJsonString( site ) );
	    ModelAndView mav = new ModelAndView( "/siteConfig/siteConfigForm.jsp", map );
	    return mav;
	}
	
	@RequestMapping("/updateSiteSatus.do")
	public ModelAndView updateSiteSatus() throws Exception{
		Map<String, Object> map = new HashMap<String, Object>( 0 );
		Map<String, Object> result = new HashMap<String, Object>( 0 );
		UserInfoScope userInfo = itcMvcService.getUserInfoScopeDatas();
		String siteId = userInfo.getParam("siteId");
		String status = userInfo.getParam("active");
		Site site = new Site();
		site.setId(siteId);
		site.setActive(status);
		itcSiteService.updateSiteById(site,null);
		result.put("success", true);
	    map.put( "result", result );
        ModelAndViewAjax mav = new ModelAndViewAjax();
        mav.setView( new JsonView() );
        mav.addObject( FrameWorkServiceImpl.getRunEnvironmentData().getAjaxJsonDataflag(), map );
        return mav;
	}
	
	@RequestMapping("/getSites.do")
	public ModelAndViewAjax getSites(HttpServletRequest request, HttpServletResponse response){
		//Object user = itcMvcService.getLocalAttribute(Constant.secUser);
		Object user = request.getSession().getAttribute(Constant.secUser);
		if (user == null) {
		    LOGGER.info("user is null.");
			return null;
		}
		SecureUser sUser = (SecureUser) user;
		Collection<Site> sites = sUser.getAuthorizedSites();
		List<String> oids = new ArrayList<String>();
		oids.add("1");
		List<Organization> orgs = iSecurityMaintenanceManager.selectOrgsByParentIds(oids);
		if (orgs == null) {
			LOGGER.info("orgs is null.");
			return null;
		}
		List<List> resultList=new ArrayList<List>();
		for (Organization org : orgs) {
			List<String> record=new ArrayList<String>();
			record.add(org.getCode());
			record.add(org.getName());
			resultList.add(record);
		}
		return ViewUtil.Json(resultList);
		
	}
}
