package com.yudean.mvc.configs.init;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

import org.apache.log4j.Logger;

import com.yudean.itc.code.LoadOnAuditJS;
import com.yudean.itc.code.ParamConfig;
import com.yudean.itc.code.RuntimeEnvironment_Mode;
import com.yudean.itc.util.ApplicationConfig;
import com.yudean.mvc.configs.MvcConfig;
import com.yudean.mvc.configs.MvcWebConfig;
import com.yudean.mvc.exception.SessionOverdueException;

public class MvcWebConfigInit {
	private static final Logger log = Logger.getLogger(MvcWebConfigInit.class);
	static public void init() {
		initLoadOnAuditJS();
		initNologException();
		initCurRunVer();
		initServicePath();
	}

	/**
	 * 初始化是否加载检查JS的配置
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-9-11:
	 */
	static private void initLoadOnAuditJS() {
		String RuntimeEnvironmentMode = ApplicationConfig.getConfig("RuntimeEnvironment.Mode");
		MvcConfig.setCurRunMode(RuntimeEnvironment_Mode.Develop);
		try {
			MvcConfig.setCurRunMode(RuntimeEnvironment_Mode.valueOf(RuntimeEnvironmentMode));
		} catch (Exception e) {
			log.error("读取系统当前运行状态异常", e);
			MvcConfig.setCurRunMode(RuntimeEnvironment_Mode.Develop);
		}
		switch(MvcConfig.getCurRunMode()){
		case Develop:{
			MvcWebConfig.loadOnAuditJS = LoadOnAuditJS.Load;
			break;
		}
		case Test:{
			MvcWebConfig.loadOnAuditJS = LoadOnAuditJS.Load;
			break;
		}
		case Produce:{
			break;
		}
		default :{
			
		}
		}
	}
	static private void initNologException() {
		MvcConfig.RunExcFrameworkNoLogException = new HashSet<Class<? extends Exception>>();
		MvcConfig.RunExcFrameworkNoLogException.add(SessionOverdueException.class);
	}
	
	/**
	 * 初始化当前的启动版本
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-12-11:
	 */
	static private void initCurRunVer(){
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		MvcWebConfig.curRunVersion = format.format(new Date());
	}
	
	static private void initServicePath(){
		//设置基本网络路径
		String RuntimeEnvironment_Basepath = ApplicationConfig.getConfig("RuntimeEnvironment.BasePath");
		if(null == RuntimeEnvironment_Basepath){
			MvcWebConfig.serverBasePath = ParamConfig.S_NaN;
		}else{
			MvcWebConfig.serverBasePath = RuntimeEnvironment_Basepath;
		}
		//设置系统根路径
		String RuntimeEnvironment_RootPath = ApplicationConfig.getConfig("RuntimeEnvironment.BirtBasePath");
		if(null == RuntimeEnvironment_RootPath){
			MvcWebConfig.serverRootPath = ParamConfig.S_NaN;
		}else{
			MvcWebConfig.serverRootPath = RuntimeEnvironment_RootPath;
		}
		//设置报表服务根路径
		String RuntimeEnvironment_BirtRootPath = ApplicationConfig.getConfig("RuntimeEnvironment.BirtRootPath");
		if(null == RuntimeEnvironment_BirtRootPath){
			MvcWebConfig.birtRootPath = ParamConfig.S_NaN;
		}else{
			MvcWebConfig.birtRootPath = RuntimeEnvironment_BirtRootPath;
		}
		//设置报表服务上下文路径
		String RuntimeEnvironment_BirtContextPath = ApplicationConfig.getConfig("RuntimeEnvironment.BirContextPath");
		if(null == RuntimeEnvironment_BirtContextPath){
			MvcWebConfig.birtContextPath = ParamConfig.S_NaN;
		}else{
			MvcWebConfig.birtContextPath = RuntimeEnvironment_BirtContextPath;
		}
		
		if(!ParamConfig.S_NaN.equals(MvcWebConfig.birtRootPath) && !ParamConfig.S_NaN.equals(MvcWebConfig.birtContextPath)){
			MvcWebConfig.birtServicePath = MvcWebConfig.birtRootPath + MvcWebConfig.birtContextPath;
		}else{
			MvcWebConfig.birtServicePath = ParamConfig.S_NaN;
		}
	}
}
