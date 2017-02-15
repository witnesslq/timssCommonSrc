package com.yudean.mvc.context;

import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.context.ApplicationContext;

import com.yudean.mvc.bean.context.MvcApplicationContext;
import com.yudean.mvc.interfaces.InitClassAfterContextBuildInterface;

/**
 * TimssContext的初始化类
 * @title: {title}
 * @description: {desc}
 * @company: gdyd
 * @className: TimssContextListener.java
 * @author: kChen
 * @createDate: 2014-7-1
 * @updateUser: kChen
 * @version: 1.0
 */
public class MvcContextListener implements InitClassAfterContextBuildInterface, ServletContextAttributeListener, ServletContextListener {

	@Override
	public void attributeAdded(ServletContextAttributeEvent event) {
		MvcContext.servletContext = event.getServletContext();
	}

	@Override
	public void attributeRemoved(ServletContextAttributeEvent event) {
		MvcContext.servletContext = event.getServletContext();
	}

	@Override
	public void attributeReplaced(ServletContextAttributeEvent event) {
		MvcContext.servletContext = event.getServletContext();
	}

	@Override
	public void initClass(ApplicationContext context) throws Exception {
		MvcContext.CoreContext = context.getBean("MvcApplicationContext", MvcApplicationContext.class);
		MvcContext.CoreContext.setTimssContext(context);
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub
		
	}

}
