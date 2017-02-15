package com.yudean.mvc.configs;

import java.util.Set;

import org.apache.log4j.Logger;

import com.yudean.itc.code.RuntimeEnvironment_Mode;

public class MvcConfig {
	private static final Logger log = Logger.getLogger(MvcConfig.class);
	
	static private RuntimeEnvironment_Mode CurRunMode;
	static public Set<Class<? extends Exception>> RunExcFrameworkNoLogException;//容错框架，不输出异常日志列表
	static public String defaultSiteId = "NaN";//默认全局站点
	//static public String host = "timss.gdyd.com";
	
	static public void setCurRunMode(RuntimeEnvironment_Mode runMode){
		RuntimeEnvironment_Mode mode = CurRunMode;
		CurRunMode = runMode;
		log.info("CurRunMode has been modify.bef: " + mode  + "aft :" + CurRunMode);
	}
	
	static public RuntimeEnvironment_Mode getCurRunMode(){
		if(null == CurRunMode){
			log.info("CurRunMode is null. set  "  + RuntimeEnvironment_Mode.Produce);
			CurRunMode = RuntimeEnvironment_Mode.Produce;
		}
		return CurRunMode;
	}
}
