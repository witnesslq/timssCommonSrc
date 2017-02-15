package com.yudean.mvc.listener;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.yudean.mvc.exception.RuntimeDataNotFoundException;
import com.yudean.mvc.interfaces.InitClassAfterContextBuildInterface;
import com.yudean.mvc.util.ContextUtil;
import com.yudean.mvc.util.LogUtil;

/**
 * spring容器启动监听类，实现在容器构建之前和之后都可以执行一系列构建
 * 
 * @author kChen
 * 
 */
public class MvcContextLoaderListener extends ContextLoaderListener {
	private static final Logger log = Logger.getLogger(MvcContextLoaderListener.class);

	@Override
	public void contextInitialized(ServletContextEvent event) {
		try {
			final ServletContext context = event.getServletContext();
			initBeforeBuildSpringContainClass(context);
			super.contextInitialized(event);
			initAfterBuildSpringContainClass(context);
		} catch (Exception e) {
			log.error("启动初始化类异常", e);
		}
	}

	/**
	 * 
	 * @param sInitAfterClassPath
	 * @param context
	 */
	private void initBeforeBuildSpringContainClass(ServletContext context) {

	}

	/**
	 * 
	 * @param sInitAfterClassPath
	 * @param context
	 */
	private void initAfterBuildSpringContainClass(ServletContext context) {
		initClassListAfter(context);
	}

	/**
	 * 
	 * @param context
	 */
	private void initClassListAfter(ServletContext context) {
		List<String> initClassList = null;
		try {
			LogUtil.debug("spring容器构建完毕，开始构架timss initClassAfterContextBuild-*-config 配置对象");
			initClassList = ContextUtil.parasInitClassList(context);
			LogUtil.debug("initClassAfterContextBuild-*-config 对象初始化完毕，包括：");
			LogUtil.debug(initClassList);
			for (int index = 0; initClassList.size() > index; index++) {
				try {
					initClassAfter(initClassList.get(index), context);
				} catch (Exception e) {
					LogUtil.error("执行业务类异常", e);
				}
			}
			LogUtil.debug("initClassAfterContextBuild-*-config 对象构架完毕，");
		} catch (RuntimeDataNotFoundException e1) {
			log.error("spring容器后的启动初始化类异常", e1);
		} catch (Exception e1) {
			log.error("pring容器后的启动初始化类异常", e1);
		}
	}

	/**
	 * 
	 * @param initParam
	 * @param context
	 * @throws Exception
	 */
	private void initClassAfter(String _classPath, ServletContext context) throws Exception {
		Class<?> clazz = Class.forName(_classPath);
		boolean isNotHasBean = false;
		WebApplicationContext Webcontext = WebApplicationContextUtils.getRequiredWebApplicationContext(context);
		try {
			InitClassAfterContextBuildInterface initClass = (InitClassAfterContextBuildInterface) Webcontext.getBean(clazz);
			initClass.initClass(Webcontext);
		} catch (BeansException e) {
			log.info("容器中" + clazz.toString() + "不存在。采用默认构造");
			isNotHasBean = true;
		} catch (Exception e) {
			log.error("处理初始化方法时异常", e);
		}
		if (isNotHasBean) {
			InitClassAfterContextBuildInterface _init = (InitClassAfterContextBuildInterface) clazz.newInstance();
			_init.initClass(Webcontext);
		}
	}
}
