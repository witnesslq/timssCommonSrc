package com.yudean.mvc.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.web.context.WebApplicationContext;

import com.yudean.itc.bean.environment.RuntimeEnvironmentData;
import com.yudean.itc.dto.sec.SecureUser;
import com.yudean.itc.dto.support.AppEnum;
import com.yudean.itc.dto.support.Configuration;
import com.yudean.itc.manager.sec.IAuthorizationManager;
import com.yudean.itc.manager.support.IConfigurationManager;
import com.yudean.itc.util.ClassCastUtil;
import com.yudean.mvc.bean.context.MvcApplicationContext;
import com.yudean.mvc.bean.userinfo.UserInfo;
import com.yudean.mvc.bean.userinfo.UserInfoScope;
import com.yudean.mvc.bean.userinfo.impl.UserInfoImpl;
import com.yudean.mvc.configs.MvcConfig;
import com.yudean.mvc.context.MvcContext;
import com.yudean.mvc.exception.RunInstantiationException;
import com.yudean.mvc.handler.ThreadLocalHandler;
import com.yudean.mvc.listener.InitItcFramework;
import com.yudean.mvc.service.ItcMvcService;
import com.yudean.mvc.util.EnumUtil;
import com.yudean.mvc.util.FrameworkServiceBranchUtil;
import com.yudean.mvc.util.ViewUtil;
import com.yudean.mvc.view.ModelAndViewAjax;
import com.yudean.mvc.view.ModelAndViewPage;

@Service
public class FrameWorkServiceTestUnitImpl extends AbstractJUnit4SpringContextTests implements ItcMvcService {

	private static final Logger log = Logger.getLogger(FrameWorkServiceTestUnitImpl.class);

	static private RuntimeEnvironmentData RunEnvironmentData = null;

	static private ApplicationContext ctxs;

	static private Map<String, Object> attributeMap;

	@PostConstruct
	public void postCo() {
		System.out.println("I'm  init  method  using  @PostConstrut....");
	}

	@Override
	public Object getBeans(String beanName) throws RuntimeException {
		return ctxs.getBean(beanName);
	}

	static public <T> T getBean(Class<T> classtype) throws RuntimeException {
		return ctxs.getBean(classtype);
	}

	@Override
	public <T> T getBeans(Class<T> classtype) throws RuntimeException {
		return ctxs.getBean(classtype);
	}

	static public <T> T getBean(String beanName, Class<T> classtype) throws RuntimeException {
		return ctxs.getBean(beanName, classtype);
	}

	@Override
	public <T> T getBeans(String beanName, Class<T> classtype) throws RuntimeException {
		return ctxs.getBean(beanName, classtype);
	}

	@Override
	public Object addBeans(String beanName, Class<?> classtype) throws RuntimeException {
		DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) ctxs.getAutowireCapableBeanFactory();
		if (!beanFactory.containsBean(beanName)) {
			// 如果bean已经存在，则不会再创建 excep
			String className = classtype.getName();
			BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(className);
			bdb.setScope("singleton");
			beanFactory.registerBeanDefinition(beanName, bdb.getBeanDefinition());
		}
		return beanFactory.getBean(beanName);
	}

	@Override
	public <T> T getServices(Class<T> classtype) throws RuntimeException {
		return ctxs.getBean(classtype);
	}

	@Override
	public RuntimeEnvironmentData getRunEnvironmentDatas() throws RuntimeException {
		if (null == RunEnvironmentData) {
			RunEnvironmentData = ctxs.getBean("RunEnvironmentData", RuntimeEnvironmentData.class);
		}
		return RunEnvironmentData;
	}

	@Override
	public WebApplicationContext getWebAppContexts() throws RuntimeException {
		// TODO Auto-generated method stub
		// 非Web工程，无法提供WEB工程的上下文
		return null;
	}

	@Override
	public Object getServletContexts() throws RuntimeException {
		// TODO Auto-generated method stub
		// 非Web工程，无法提供WEB工程的上下文
		return null;
	}

	@Override
	public ModelAndViewPage Pages(String page, Object obj) throws RuntimeException {
		return ViewUtil.Page(page, obj);
	}

	@Override
	public ModelAndViewPage Pages(String page, String modelName, Object obj) throws RuntimeException {
		return ViewUtil.Page(page, modelName, obj);
	}

	@Override
	public ModelAndViewAjax jsons(Object obj) throws RuntimeException {
		return ViewUtil.Json(obj);
	}

	@Override
	public UserInfoScope getUserInfoScopeDatas() throws RuntimeException {
		return ThreadLocalHandler.getVariable().getUserInfoScope();
	}

	@Override
	public UserInfo getUserInfoById(String userId) throws RuntimeException {
		IAuthorizationManager author = FrameWorkServiceTestUnitImpl.getBean(IAuthorizationManager.class);
		String curentSiteId = ThreadLocalHandler.getVariable().getUserInfoScope().getSiteId();
		SecureUser secUser = author.retriveUserById(userId, curentSiteId);
		UserInfo userInfo = null;
		try {
			userInfo = ClassCastUtil.castAllField2Class(UserInfoImpl.class, secUser);
		} catch (InstantiationException e) {
			log.error("ClassCasetUtil InstantiationException error , throw RunInstantiationException ", e);
			throw new RunInstantiationException("实例化secUser类异常");
		} catch (IllegalAccessException e) {
			log.error("ClassCasetUtil IllegalAccessException error , throw RuntimeIllegalAccessException ", e);
			throw new RunInstantiationException("将secUser数据注入UserInfoImpl异常");
		} catch (NoSuchFieldException e) {
			log.error("ClassCasetUtil NoSuchFieldException error , throw RuntimeNoSuchFieldException ", e);
			throw new RunInstantiationException("将secUser类数据注入UserInfoImpl成员变量异常");
		}
		return userInfo;
	}

	@Override
	public UserInfo getUserInfo(String userId, String siteId) throws RuntimeException {
		IAuthorizationManager author = FrameWorkServiceTestUnitImpl.getBean(IAuthorizationManager.class);
		SecureUser secUser = author.retriveUserById(userId, siteId);
		UserInfo userInfo = null;
		try {
			userInfo = ClassCastUtil.castAllField2Class(UserInfoImpl.class, secUser);
		} catch (InstantiationException e) {
			log.error("ClassCasetUtil InstantiationException error , throw RunInstantiationException ", e);
			throw new RunInstantiationException("实例化secUser类异常");
		} catch (IllegalAccessException e) {
			log.error("ClassCasetUtil IllegalAccessException error , throw RuntimeIllegalAccessException ", e);
			throw new RunInstantiationException("将secUser数据注入UserInfoImpl异常");
		} catch (NoSuchFieldException e) {
			log.error("ClassCasetUtil NoSuchFieldException error , throw RuntimeNoSuchFieldException ", e);
			throw new RunInstantiationException("将secUser类数据注入UserInfoImpl成员变量异常");
		}
		return userInfo;
	}

	@Override
	public List<AppEnum> getEnum(String eCateCode) throws RuntimeException {
		String curentSiteId = ThreadLocalHandler.getVariable().getUserInfoScope().getSiteId();
		List<AppEnum> list = EnumUtil.getEnumSite(eCateCode, curentSiteId);
		if (null == list) {
			list = EnumUtil.getEnum(eCateCode);
		}
		return list;
	}

	@Override
	public Map<String, List<AppEnum>> getEnumMap() throws RuntimeException {
		return EnumUtil.getEnumMap();
	}

	@Override
	public Configuration getConfiguration(String confName) throws RuntimeException {
		IConfigurationManager iconfig = ctxs.getBean(IConfigurationManager.class);
		Configuration conf = new Configuration();
		UserInfoScope scope = ThreadLocalHandler.getVariable().getUserInfoScope();
		conf.setConf(confName);
		conf.setsiteId(scope.getSiteId());
		// 根据站点和ID获取参数
		List<Configuration> list = iconfig.query(conf, (SecureUser) scope);
		if (null != list && 0 < list.size()) {
			return list.get(0);
		} else {//默认站点未查询到，改用全局站点查询
			conf.setsiteId(MvcConfig.defaultSiteId);
			list = iconfig.query(conf, (SecureUser) scope);
			if (null != list && 0 < list.size()) {
				return list.get(0);
			}else{
				return null;
			}
		}
	}

	static public void init(ApplicationContext context) {
		try {
			MvcContext.CoreContext = context.getBean("MvcApplicationContext", MvcApplicationContext.class);
			MvcContext.CoreContext.setTimssContext(context);
			FrameWorkServiceTestUnitImpl.ctxs = context;

			InitItcFramework initItc = new InitItcFramework();
			ItcMvcService itcmvcService = context.getBean(ItcMvcService.class);
			initItc.initItcClass(itcmvcService);
			FrameworkServiceBranchUtil.initItcMvcService(itcmvcService);
			//setUserInfo("890128", "ITC");// 今后实现配置化
			attributeMap = new HashMap<String, Object>();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static public void setUserInfo(String userId, String siteId) throws RuntimeException {
		IAuthorizationManager author = ctxs.getBean(IAuthorizationManager.class);
		SecureUser secUser = author.retriveUserById(userId, siteId);
		try {
			UserInfo userInfo = ClassCastUtil.castAllField2Class(UserInfoImpl.class, secUser);
			ThreadLocalHandler.createNewVarableOweUserInfo(userInfo);
		} catch (InstantiationException e) {
			log.error("ClassCasetUtil InstantiationException error , throw RunInstantiationException ", e);
			throw new RunInstantiationException("实例化secUser类异常");
		} catch (IllegalAccessException e) {
			log.error("ClassCasetUtil IllegalAccessException error , throw RuntimeIllegalAccessException ", e);
			throw new RunInstantiationException("将secUser数据注入UserInfoImpl异常");
		} catch (NoSuchFieldException e) {
			log.error("ClassCasetUtil NoSuchFieldException error , throw RuntimeNoSuchFieldException ", e);
			throw new RunInstantiationException("将secUser类数据注入UserInfoImpl成员变量异常");
		}
	}

	@Override
	public void setLocalAttribute(String name, Object attribute) throws RuntimeException {
		attributeMap.put(name, attribute);
	}

	@Override
	public Object getLocalAttribute(String name) throws RuntimeException {
		return attributeMap.get(name);
	}

	@Override
	public Map<String, Object> getLocalAttributeMap() throws RuntimeException {
		return attributeMap;
	}
}
