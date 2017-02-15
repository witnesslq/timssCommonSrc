package com.yudean.homepage.interfaces.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.yudean.homepage.interfaces.HomepageNotifyInterface;
import com.yudean.itc.manager.sec.IAuthorizationManager;
import com.yudean.mvc.service.ItcMsgService;
import com.yudean.mvc.service.ItcMvcService;
import com.yudean.mvc.service.ItcSiteService;
import com.yudean.mvc.service.ItcSysConfService;

public abstract class HomepageNotifyCommon implements HomepageNotifyInterface {
	@Autowired
	IAuthorizationManager authorizationManager;

	@Autowired
	ItcSysConfService sysConfService;
	
	@Autowired 
	ItcMsgService itcMsgService;
	
	@Autowired
	ItcMvcService itcMvcService;
	
	@Autowired
	ItcSiteService itcSiteService;
}
