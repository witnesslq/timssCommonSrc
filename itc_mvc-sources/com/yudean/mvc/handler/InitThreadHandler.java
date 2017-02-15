package com.yudean.mvc.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yudean.itc.bean.exception.ExceptionData;
import com.yudean.itc.manager.sec.IAuthenticationManager;
import com.yudean.mvc.bean.userinfo.UserInfo;

/**
 * 初始化访问域参数，向当前使用的线程池中注入数据
 * @author kChen
 *
 */
public class InitThreadHandler {
	
	/**
	 * 初始化域数据
	 * @throws Exception
	 */
	static public void initRequestScopeData(HttpServletRequest request, HttpServletResponse response, IAuthenticationManager manager) throws Exception{
		ThreadLocalHandler.initVariable();
		ThreadLocalHandler.setUserInfoVariable(request, response, manager);
	}
	
	static public void initScopeData(UserInfo userInfo) throws Exception{
		ThreadLocalHandler.initVariable();
		ThreadLocalHandler.setUserInfoScope(userInfo, null, null);
	}
	
	static public void initExceptionData(ExceptionData exceptionData) throws Exception{
		ThreadLocalHandler.setExceptionVariable(exceptionData);
	}
}
