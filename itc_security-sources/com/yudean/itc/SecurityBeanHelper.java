package com.yudean.itc;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SecurityBeanHelper implements ApplicationContextAware {
	private static final Logger log = Logger.getLogger(SecurityBeanHelper.class);
	private static ApplicationContext context;

	private SecurityBeanHelper() {
		log.info("-- Spring Context Initialized --");
	}

	private static class Holder {
		public static final SecurityBeanHelper instance = new SecurityBeanHelper();
	}

	public static SecurityBeanHelper getInstance() {
		return Holder.instance;
	}

	public Object getBean(String name) {
		return context.getBean(name);
	}

	public <T> T getBean(Class<T> c) {
		return context.getBean(c);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;
		// 初始化组织-站点映射
		OrgTreeUtil.buildOrgTree();
	}
}
