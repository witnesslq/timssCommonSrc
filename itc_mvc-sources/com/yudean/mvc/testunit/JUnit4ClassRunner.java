package com.yudean.mvc.testunit;

import java.lang.reflect.Field;

import org.apache.log4j.Logger;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.yudean.mvc.service.impl.FrameWorkServiceTestUnitImpl;
import com.yudean.mvc.util.LogUtil;

/**
 * Timss测试用例入口类
 * @title: {title}
 * @description: 在测试用例入口类中，主要实现了在加载spring容器之后，构建TIMSS自己的运行环境。
 * @company: gdyd
 * @className: TimssJUnit4ClassRunner.java
 * @author: kChen
 * @createDate: 2014-7-1
 * @updateUser: kChen
 * @version: 1.0
 */
public class JUnit4ClassRunner extends SpringJUnit4ClassRunner {
	static private Logger log = Logger.getLogger(JUnit4ClassRunner.class);
	/**
	 * Timss工程LOG4J配置文件位置
	 */
	static final String log4jConfigXMLPath = "config/log/log4j.xml";
	
	static final String log4jFolderPath = "target";
	
	static final String outFolderPath = "/testUnitLog";
	
	public JUnit4ClassRunner(Class<?> clazz) throws InitializationError {
		super(clazz);//初始化spring
		TestUnitGolbalService.TestUnitMode = true;//全局测试运行参数
		initLog4j(clazz);//初始化log4j日志，日志会输出到系统根目录
	}
	
	/**
	 * 初始化Log4j日志
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-7-1
	 * @param clazz:
	 */
	private void initLog4j(Class<?> clazz) throws InitializationError {
		try {
			LogUtil.initLog4jConfigType(log4jConfigXMLPath, outFolderPath);
		} catch (Exception e) {
			e.printStackTrace();
			throw new InitializationError("日志配置初始化异常");
		}
		log.info("测试用例加载日志配置文件" + log4jConfigXMLPath);
		log.info("测试用例日志文件输出根目未设置. 默认为工程根目录" + outFolderPath);
		log.info("=================================================");
		log.info("日志工具初始化完成");
		log.info("=================================================");
	}
	
	@Override
	protected void runChild(FrameworkMethod frameworkMethod, RunNotifier notifier) {
		ApplicationContext context = castApplicationContext();
		//初始化TIMSS service实例
		FrameWorkServiceTestUnitImpl.init(context);
		super.runChild(frameworkMethod, notifier);
	}
	
	@Override
	protected Statement methodBlock(FrameworkMethod frameworkMethod) {
		Statement statement = super.methodBlock(frameworkMethod);
		return statement;
	}
	
	private ApplicationContext castApplicationContext(){
		ApplicationContext applicationContext = null;
		try {
			TestContextManager contextManager = this.getTestContextManager();
			Field field = TestContextManager.class.getDeclaredField("testContext");
			field.setAccessible(true);
			TestContext textContext = (TestContext)field.get(contextManager);
			applicationContext = textContext.getApplicationContext();
		} catch (Exception e) {
			log.error("获取spring上下文异常", e);
		} 
		return applicationContext;
	}
}
