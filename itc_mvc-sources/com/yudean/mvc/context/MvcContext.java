package com.yudean.mvc.context;

import javax.servlet.ServletContext;

import com.yudean.mvc.bean.context.MvcApplicationContext;

/**
 * 框架服务类上下文资源管理全局类,可以通过这个类获取框架定义的全局容器。
 * getCoreContext可以获取核心容器，这里面包括了框架启动时定义的主动拦截BEAN、AOP
 * BEAN等,容器实现了TimssInitClassAfterContextBuildInterface接口,在启动系统时初始化
 * 
 * @author kChen
 * 
 */

public class MvcContext{
	/**
	 * 文件位置，以后加到配置文件中
	 */
	// static private String[] scanPath = null;

	static public MvcApplicationContext CoreContext;

	static ServletContext servletContext;

	static boolean location = false;// 标记当前的运行版本

	/**
	 * 获取框架服务类上下文信息，核心容器。
	 * 
	 * @return
	 */
	static public MvcApplicationContext getCoreContext() {
		return CoreContext;
	}

	/**
	 * 获取servlete初始化的全局上下文
	 * 
	 * @return
	 */
	static public ServletContext getServletContext() {
		return servletContext;
	}

	/**
	 * 获取容器运行版本
	 * 
	 * @return
	 */
	static public boolean getLocation() {
		return location;
	}

	static public void initServletContext(ServletContext context) throws Exception {
		servletContext = context;
		location = "location".equals(servletContext.getInitParameter("MvcRunContext"));
	}
}