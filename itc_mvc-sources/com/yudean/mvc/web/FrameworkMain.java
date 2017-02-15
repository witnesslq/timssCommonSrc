package com.yudean.mvc.web;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yudean.itc.code.ParamConfig;
import com.yudean.mvc.service.IMvcInterfaceModeService;
import com.yudean.mvc.service.ItcMvcService;

/**
 * TIMSS框架，系统主页面控制类
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
@RequestMapping(value = "mvc/main")
public class FrameworkMain {
	private static final Logger log = Logger.getLogger(FrameworkMain.class);

	@Autowired
	ItcMvcService itcMvcService;
	
	@Autowired
	FrameworkController frameController;
	
	@Autowired
	IMvcInterfaceModeService mvcInterfaceModeService;
	
	
	@RequestMapping(value = "/mainpage")
	public String mainPage(HttpServletRequest request) throws Exception{
		JSONObject userInfo = frameController.userInfo();
		itcMvcService.setLocalAttribute("UserInfoJsonStr", userInfo.toString());
		
		String mode = request.getParameter("interfacemode");
		String data = request.getParameter("interfacedata");
		
		if(mode != null && data != null){
			String url = mvcInterfaceModeService.processInterfaceMode(mode, data);
			itcMvcService.setLocalAttribute("openTabModeMain", url);
		}
		
		return "/main.jsp";
	}
	
	@RequestMapping(value = "/logMainInit")
	public JSONObject logMainInit(HttpServletRequest request) throws Exception{
		log.info("用户" + request.getParameter("useId") + "登陆完成。 时间:" + ParamConfig.S_MSTIME_FORMATTER.format(new Date()) + ".Session:" + request.getSession().getId());
		JSONObject json = new JSONObject();
		json.put("msg", "success");
		return json;
	}
}
