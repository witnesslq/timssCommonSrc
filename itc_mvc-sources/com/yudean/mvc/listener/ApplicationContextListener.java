package com.yudean.mvc.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.yudean.mvc.context.MvcContext;
import com.yudean.mvc.util.LogUtil;

/**
 * 常规系统上下文启动监听类，
 * @author kChen
 *
 */
public class ApplicationContextListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		try {
			//上下文环境先初始化Servlet的上下文
			MvcContext.initServletContext(arg0.getServletContext());
			//日志工具在系统一启动时就完成初始化工作，先于spring的日志工具初始化，让spring的调试信息输出到我们定义的日志文件中
			LogUtil.initClass(arg0.getServletContext());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println("厨师胡日志异常");
			e.printStackTrace();
		}
	}
}
