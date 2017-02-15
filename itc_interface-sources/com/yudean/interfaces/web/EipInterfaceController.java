package com.yudean.interfaces.web;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.yudean.itc.dto.sec.SecureUser;
import com.yudean.itc.exception.sec.AuthenticationException;
import com.yudean.itc.util.ClassCastUtil;
import com.yudean.itc.util.Constant;
import com.yudean.mvc.bean.userinfo.impl.UserInfoImpl;
import com.yudean.mvc.util.LogUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yudean.interfaces.service.IEipInterfaceService;
import com.yudean.itc.dto.interfaces.eip.TaskListBean;
import com.yudean.itc.dto.interfaces.eip.mobile.RetDetailBean;
import com.yudean.itc.dto.interfaces.eip.mobile.RetProcessBean;


@Controller
@RequestMapping(value = "EipInterface")
public class EipInterfaceController {
	private static final Logger log = Logger.getLogger(EipInterfaceController.class);
	static private String s_pathCode = "UTF-8";

	@Autowired
	IEipInterfaceService eipInterfaceService;

	/**
	 * Eip
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-9-16
	 * @return:
	 */
	@RequestMapping(value = "Desktop/getTaskList")
	public TaskListBean getTaskList(HttpServletRequest request) {
		//	String url = "http://" + request.getLocalAddr() + ":" + request.getServerPort() +  request.getContextPath();
		return eipInterfaceService.getWorkflowTaskList(request.getParameter("uid"), request.getParameter("pwd"), null);
	}
	
	@RequestMapping(value = "Mobile/getDetail")
	public RetDetailBean getDetailMobile(HttpServletRequest request) {
		return eipInterfaceService.getTaskDetailMobile(request.getParameter("uid"), request.getParameter("pwd"), request.getParameter("flowNo"), request.getParameter("sid"));
	}

	@RequestMapping(value = "Desktop/getFavRoute")
	public Map<String, Object> getFavRoute(HttpServletRequest request){
		String token = request.getParameter("_login_token");
		Map<String, Object> ret = new HashMap<String, Object>();
		SecureUser user = eipInterfaceService.signInWithKey(token);
		if(user == null){
			ret.put("retcode", -1);
			ret.put("retmsg", "登录信息无效或者操作超时");
			return ret;
		}
		return eipInterfaceService.getUserFavouriteRoute(user);
	}

	@RequestMapping(value = "tokenRedirect")
	public String tokenRedirect(HttpServletRequest request, HttpServletResponse response) {
		String token = request.getParameter("token");
		String redirect = request.getParameter("redirect");
		log.debug("EIP token redirect for: " + redirect);
		SecureUser user = eipInterfaceService.signInWithKey(token);
		if(user == null){
			return "redirect:/login?method=logout";
		}
		HttpSession session = request.getSession();
		/*---------下面的代码都是原LoginServlet的---------*/
		UserInfoImpl userInfo = null;
		try {
			// 将权限管理模块的SecureUser对象数据注入到 timss UserInfo接口中
			userInfo = ClassCastUtil.castAllField2Class(UserInfoImpl.class, user);
		} catch (Exception ex) {
			LogUtil.error("转换用户类型数据异常 secUser->userInfo,数据类容:" + user, ex);
		}
		session.setAttribute("username", user.getName());
		if (user.getAuthorizedSites().size() > 1) {
			session.setAttribute("crossSite", true);
			// 需要把当前站点复制一份为默认站点 否则跨站选择后无法正确指示哪个是默认的
			session.setAttribute("defaultSite", user.getCurrentSite());
		} else {
			session.setAttribute("crossSite", false);
		}
		session.setAttribute(Constant.secUser, userInfo);
		return "redirect:/login?method=index#routeId=" + redirect;
	}

	@RequestMapping(value = "getLoginToken")
	public Map<String, Object> getLoginToken(HttpServletRequest request){
		Map<String, Object> ret = new HashMap<String, Object>();
		String uid = request.getParameter("uid");
		String pwd = request.getParameter("pwd");
		String siteId = StringUtils.trimToNull(request.getParameter("site"));
		try {
			Map<String, Object> result = eipInterfaceService.cachedSignIn(uid, pwd, siteId);
			ret.put("retcode", 1);
			ret.put("token", result.get("token"));
			ret.put("functions", result.get("functions"));
			return ret;
		}catch (AuthenticationException ex){
			ret.put("retcode", -1);
			ret.put("retmsg", ex.getMessage());
			return ret;
		}
	}

	@RequestMapping(value = "Mobile/processDetail")
	public RetProcessBean processDetailMobile(HttpServletRequest request) throws Exception {
		String userid = request.getParameter("uid");
		String password = request.getParameter("pwd");
		String siteId = request.getParameter("sid");
		String itemid = request.getParameter("id");
		String opinion = request.getParameter("opinion");
		try {
			opinion = URLDecoder.decode(null == opinion? "" : opinion, s_pathCode);
		} catch (UnsupportedEncodingException e) {
			log.error("解析EIP汉字编码异常，传入未编码数据", e);
		}
		String flowid = request.getParameter("flowid");
		String taskKey = request.getParameter("taskkey");
		String nextUsers = request.getParameter("userkey");
		String url = "http://" + request.getLocalAddr() + ":" + request.getServerPort() +  request.getContextPath();
		
		return eipInterfaceService.processTaskDetailMobile(userid, password, itemid, opinion, siteId, flowid, taskKey, nextUsers, url,request);
	}
}
