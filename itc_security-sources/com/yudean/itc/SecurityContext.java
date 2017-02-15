package com.yudean.itc;

import org.apache.log4j.Logger;

import com.yudean.itc.dto.sec.SecureUser;

public class SecurityContext  {
	private static final Logger log = Logger.getLogger(SecurityContext.class);
	private SecurityContext me;
	
	private SecurityContext() {
		log.info("-- SecurityContext Initialized --");
		me = new SecurityContext();
	}
	
	
}
