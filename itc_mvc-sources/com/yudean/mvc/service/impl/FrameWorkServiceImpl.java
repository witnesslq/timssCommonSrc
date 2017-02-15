package com.yudean.mvc.service.impl;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.XmlWebApplicationContext;

import com.yudean.mvc.bean.context.MvcApplicationContext;
import com.yudean.mvc.bean.handler.ThreadLocalVariable;
import com.yudean.mvc.bean.userinfo.UserInfo;
import com.yudean.mvc.bean.userinfo.UserInfoScope;
import com.yudean.mvc.bean.userinfo.impl.UserInfoImpl;
import com.yudean.mvc.configs.MvcConfig;
import com.yudean.mvc.context.MvcContext;
import com.yudean.mvc.exception.RunInstantiationException;
import com.yudean.mvc.handler.ThreadLocalHandler;
import com.yudean.mvc.service.ItcMvcService;
import com.yudean.mvc.util.EnumUtil;
import com.yudean.mvc.util.ViewUtil;
import com.yudean.mvc.view.ModelAndViewAjax;
import com.yudean.mvc.view.ModelAndViewPage;
import com.yudean.itc.bean.environment.RuntimeEnvironmentData;
import com.yudean.itc.bean.exception.ExceptionData;
import com.yudean.itc.dto.sec.SecureUser;
import com.yudean.itc.dto.support.AppEnum;
import com.yudean.itc.dto.support.Configuration;
import com.yudean.itc.manager.sec.IAuthorizationManager;
import com.yudean.itc.manager.support.IConfigurationManager;
import com.yudean.itc.util.ClassCastUtil;

/**
 * TIMSS全局服务方法，作为框架工具提供快捷方法给所有模块使用， 方法提供静态访问，也提供全局spring注入调用
 * 
 * @author kChen
 * 
 */

@Service
public class FrameWorkServiceImpl implements ItcMvcService {
	private static final Logger log = Logger.getLogger(FrameWorkServiceImpl.class);
	static private RuntimeEnvironmentData RunEnvironmentData = null;

	/**
	 * 从TIMSS核心容器中获取bean实例化对象，如无特别申明，默认为单例。无法获取从模块建立的独立容器中获得任何bean。
	 * 
	 * @param beanName
	 * @return Object对象实例
	 * @throws Exception
	 */
	static public Object getBean(String beanName) throws RuntimeException {
		return MvcContext.getCoreContext().getBean(beanName);
	}

	/**
	 * 从TIMSS核心容器中获取bean实例化对象，如无特别申明，默认为单例。无法获取从模块建立的独立容器中获得任何bean。
	 * 
	 * @param beanName
	 * @return Object对象实例
	 * @throws Exception
	 */
	@Override
    public Object getBeans(String beanName) throws RuntimeException {
		return FrameWorkServiceImpl.getBean(beanName);
	}

	/**
	 * 从TIMSS核心容器中获取bean实例化对象，如无特别申明，默认为单例。无法获取从模块建立的独立容器中获得任何bean。
	 * 
	 * @param classtype
	 *            模板类
	 * @return 实例化的classtype对象
	 * @throws Exception
	 */
	static public <T> T getBean(Class<T> classtype) throws RuntimeException {
		return MvcContext.getCoreContext().getBean(classtype);
	}

	/**
	 * 从TIMSS核心容器中获取bean实例化对象，如无特别申明，默认为单例。无法获取从模块建立的独立容器中获得任何bean。
	 * 
	 * @param classtype
	 *            模板类
	 * @return 实例化的classtype对象
	 * @throws Exception
	 */
	@Override
    public <T> T getBeans(Class<T> classtype) throws RuntimeException {
		return FrameWorkServiceImpl.getBean(classtype);
	}

	/**
	 * 从TIMSS核心容器中获取bean实例化对象，如无特别申明，默认为单例。无法获取从模块建立的独立容器中获得任何bean。
	 * 
	 * @param beanName
	 *            bean名称
	 * @param classtype
	 *            模板类
	 * @return 实例化的classtype对象
	 * @throws Exception
	 */
	static public <T> T getBean(String beanName, Class<T> classtype) throws RuntimeException {
		return MvcContext.getCoreContext().getBean(beanName, classtype);
	}

	/**
	 * 从TIMSS核心容器中获取bean实例化对象，如无特别申明，默认为单例。无法获取从模块建立的独立容器中获得任何bean。
	 * 
	 * @param beanName
	 *            bean名称
	 * @param classtype
	 *            模板类
	 * @return 实例化的classtype对象
	 * @throws Exception
	 */
	@Override
    public <T> T getBeans(String beanName, Class<T> classtype) throws RuntimeException {
		return FrameWorkServiceImpl.getBean(beanName, classtype);
	}

	/**
	 * 向TIMSS核心容器中增加所对顶的类并返回一个单例。无法向业务模块建立的独立容器中建立bean。
	 * 
	 * @param classtype
	 * @return
	 * @throws Exception
	 */
	static public Object addBean(String beanName, Class<?> classtype) throws RuntimeException {
		MvcApplicationContext coreContext = MvcContext.getCoreContext();
		DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) ((XmlWebApplicationContext) coreContext.getTimssContext())
				.getBeanFactory();
		if (!beanFactory.containsBean(beanName)) {
			// 如果bean已经存在，则不会再创建 excep
			String className = classtype.getName();
			BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(className);
			bdb.setScope("singleton");
			beanFactory.registerBeanDefinition(beanName, bdb.getBeanDefinition());
		}
		return beanFactory.getBean(beanName);
	}

	/**
	 * 向TIMSS核心容器中增加所对顶的类并返回一个单例。无法向业务模块建立的独立容器中建立bean。
	 * 
	 * @param classtype
	 * @return
	 * @throws Exception
	 */
	@Override
    public Object addBeans(String beanName, Class<?> classtype) throws RuntimeException {
		return FrameWorkServiceImpl.addBean(beanName, classtype);
	}

	/**
	 * 获取@Service Bean
	 * 
	 * @param beanName
	 * @param classtype
	 * @return
	 * @throws Exception
	 */
	static public <T> T getService(Class<T> classtype) throws RuntimeException {
		return MvcContext.getCoreContext().getBean(classtype);
	}

	/**
	 * 获取@Service Bean
	 * 
	 * @param beanName
	 * @param classtype
	 * @return
	 * @throws Exception
	 */
	@Override
    public <T> T getServices(Class<T> classtype) throws RuntimeException {
		return FrameWorkServiceImpl.getService(classtype);
	}

	/**
	 * 获取系统运行环境参数，参数内容参考TimssRuntimeEnvironmentData类
	 * 
	 * @return
	 * @throws Exception
	 */
	static public RuntimeEnvironmentData getRunEnvironmentData() throws RuntimeException {
		if (null == RunEnvironmentData) {
			RunEnvironmentData = MvcContext.getCoreContext().getBean("RunEnvironmentData", RuntimeEnvironmentData.class);
		}
		return RunEnvironmentData;
	}

	/**
	 * 获取系统运行环境参数，参数内容参考TimssRuntimeEnvironmentData类
	 * 
	 * @return
	 * @throws Exception
	 */
	@Override
    public RuntimeEnvironmentData getRunEnvironmentDatas() throws RuntimeException {
		return FrameWorkServiceImpl.getRunEnvironmentData();
	}

	/**
	 * 返回页面，并制定一个未包含名称的Model
	 * 
	 * @param page
	 *            页面路径
	 * @param obj
	 *            model
	 * @return
	 * @throws Exception
	 */
	static public ModelAndViewPage Page(String page, Object obj) throws RuntimeException {
		return ViewUtil.Page(page, obj);
	}

	/**
	 * 返回页面，并制定一个未包含名称的Model
	 * 
	 * @param page
	 *            页面路径
	 * @param obj
	 *            model
	 * @return
	 * @throws Exception
	 */
	@Override
    public ModelAndViewPage Pages(String page, Object obj) throws RuntimeException {
		return FrameWorkServiceImpl.Page(page, obj);
	}

	/**
	 * 返回页面，并绑定一个Model
	 * 
	 * @param page
	 * @param modelName
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	static public ModelAndViewPage Page(String page, String modelName, Object obj) throws RuntimeException {
		return ViewUtil.Page(page, modelName, obj);
	}

	/**
	 * 返回页面，并绑定一个Model
	 * 
	 * @param page
	 * @param modelName
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	@Override
    public ModelAndViewPage Pages(String page, String modelName, Object obj) throws RuntimeException {
		return FrameWorkServiceImpl.Page(page, modelName, obj);
	}

	/**
	 * 返回一个JSON对象
	 * 
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	static public ModelAndViewAjax json(Object obj) throws RuntimeException {
		return ViewUtil.Json(obj);
	}

	/**
	 * 返回一个JSON对象
	 * 
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	@Override
    public ModelAndViewAjax jsons(Object obj) throws RuntimeException {
		return FrameWorkServiceImpl.json(obj);
	}

	/**
	 * 获取一个UserInfo对象
	 * 
	 * @return
	 * @throws Exception
	 */
	static public UserInfoScope getUserInfoScopeData() throws RuntimeException {
		return ThreadLocalHandler.getVariable().getUserInfoScope();
	}

	/**
	 * 获取一个UserInfoScopeData对象
	 * 
	 * @return
	 * @throws Exception
	 */

	@Override
	public UserInfoScope getUserInfoScopeDatas() throws RuntimeException {
		UserInfoScope ret = null;
		ThreadLocalVariable variable = ThreadLocalHandler.getVariable();
		if (null != variable) {
			ret = variable.getUserInfoScope();
		}
		return ret;
	}

	static public UserInfo getUserInfoByIdStatic(String userId) throws RuntimeException {
		String siteId = ThreadLocalHandler.getVariable().getUserInfoScope().getSiteId();
		IAuthorizationManager author = FrameWorkServiceImpl.getBean(IAuthorizationManager.class);
		SecureUser secUser = author.retriveUserById(userId, siteId);
		UserInfo userInfo;
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

	static public UserInfo getUserInfoStatic(String userId, String siteId) throws RuntimeException {
		IAuthorizationManager author = FrameWorkServiceImpl.getBean(IAuthorizationManager.class);
		SecureUser secUser = author.retriveUserById(userId, siteId);
		UserInfo userInfo;
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
	public UserInfo getUserInfoById(String userId) throws RuntimeException {
		return getUserInfoByIdStatic(userId);
	}

	@Override
	public UserInfo getUserInfo(String userId, String siteId) throws RuntimeException {
		return getUserInfoStatic(userId, siteId);
	}

	/**
	 * 获取异常信息
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-6-21
	 * @return
	 * @throws Exception
	 *             :
	 */
	static public ExceptionData getExceptionData() throws RuntimeException {
		return ThreadLocalHandler.getVariable().getExceptionData();
	}

	/**
	 * 获取系统上下文
	 * 
	 * @param beanName
	 * @param classtype
	 * @return
	 * @throws Exception
	 */
	static public ApplicationContext getAppContext() throws RuntimeException {
		return MvcContext.getCoreContext();
	}

	@Override
	public ApplicationContext getWebAppContexts() throws RuntimeException {
		return FrameWorkServiceImpl.getAppContext();
	}

	/**
	 * 获取系统上下文
	 * 
	 * @param beanName
	 * @param classtype
	 * @return
	 * @throws Exception
	 */
	static public ServletContext getServletContext() throws RuntimeException {
		return MvcContext.getServletContext();
	}

	@Override
	public ServletContext getServletContexts() throws RuntimeException {
		return FrameWorkServiceImpl.getServletContext();
	}

	@Override
	public List<AppEnum> getEnum(String eCateCode) throws RuntimeException {
		ThreadLocalVariable variable = ThreadLocalHandler.getVariable();
		UserInfoScope scope = ThreadLocalHandler.getVariable().getUserInfoScope();
		String siteId = "NaN";
		if (null != variable) {
			scope = variable.getUserInfoScope();
			siteId = scope.getSiteId();
		} else {
			log.info("未获取当前访问运行用户信息，站点为空，采用默认站点'NaN'获取枚举变量！");
		}
		List<AppEnum> list = EnumUtil.getEnumSite(eCateCode, siteId);
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
		IConfigurationManager iconfig = FrameWorkServiceImpl.getBean(IConfigurationManager.class);
		Configuration conf = new Configuration();
		ThreadLocalVariable variable = ThreadLocalHandler.getVariable();
		conf.setConf(confName);
		UserInfoScope scope = null;
		if (null != variable) {
			scope = ThreadLocalHandler.getVariable().getUserInfoScope();
			conf.setsiteId(scope.getSiteId());
		} else {
			log.info("未获取当前访问运行用户信息，站点为空，采用默认站点'NaN'获取全局参数！");
			conf.setsiteId("NaN");
		}
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

	@Override
	public void setLocalAttribute(String name, Object attribute) throws RuntimeException {
		ThreadLocalHandler.getVariable().setThreadLocalAttribute(name, attribute);
	}

	@Override
	public Object getLocalAttribute(String name) throws RuntimeException {
		return ThreadLocalHandler.getVariable().getThreadLocalAttribute(name);
	}

	@Override
	public Map<String, Object> getLocalAttributeMap() throws RuntimeException {
		return ThreadLocalHandler.getVariable().getThreadLocalAttributeMap();
	}
}
