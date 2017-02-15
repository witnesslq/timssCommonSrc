package com.yudean.mvc.service;

import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;

import com.yudean.mvc.bean.userinfo.UserInfo;
import com.yudean.mvc.bean.userinfo.UserInfoScope;
import com.yudean.mvc.view.ModelAndViewAjax;
import com.yudean.mvc.view.ModelAndViewPage;
import com.yudean.itc.bean.environment.RuntimeEnvironmentData;
import com.yudean.itc.dto.support.AppEnum;
import com.yudean.itc.dto.support.Configuration;

/**
 * TIMSS主框架服务类，提供各种主框架相关的接口
 * @title: {title}
 * @description: {desc}
 * @company: gdyd
 * @className: ItcMvcService.java
 * @author: kChen
 * @createDate: 2014-6-30
 * @updateUser: kChen
 * @version: 1.0
 */
public interface ItcMvcService {
	/**
	 * 从TIMSS核心容器中获取bean实例化对象，如无特别申明，默认为单例。无法获取从模块建立的独立容器中获得任何bean。
	 * 
	 * @param beanName
	 * @return Object对象实例
	 * @throws Exception
	 */
	Object getBeans(String beanName) throws RuntimeException;

	/**
	 * 从TIMSS核心容器中获取bean实例化对象，如无特别申明，默认为单例。无法获取从模块建立的独立容器中获得任何bean。
	 * 
	 * @param classtype
	 *            模板类
	 * @return 实例化的classtype对象
	 * @throws Exception
	 */
	<T> T getBeans(Class<T> classtype) throws RuntimeException;

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
	<T> T getBeans(String beanName, Class<T> classtype) throws RuntimeException;

	/**
	 * 向TIMSS核心容器中增加所对顶的类并返回一个单例。无法向业务模块建立的独立容器中建立bean。
	 * 
	 * @param classtype
	 * @return
	 * @throws Exception
	 */
	Object addBeans(String beanName, Class<?> classtype) throws RuntimeException;

	/**
	 * 获取@Service Bean
	 * 
	 * @param beanName
	 * @param classtype
	 * @return
	 * @throws Exception
	 */
	<T> T getServices(Class<T> classtype) throws RuntimeException;

	/**
	 * 获取系统运行环境参数，参数内容参考TimssRuntimeEnvironmentData类
	 * 
	 * @return
	 * @throws Exception
	 */
	RuntimeEnvironmentData getRunEnvironmentDatas() throws RuntimeException;

	/**
	 * 获取web工程系统上下文(spring容器)
	 * 
	 * @param beanName
	 * @param classtype
	 * @return
	 * @throws Exception
	 */
	ApplicationContext getWebAppContexts() throws RuntimeException;
	
	/**
	 * 获取系统servlet工程上下文（servlet3.0）
	 * @description: 2014-5-6修改返回数据为Object,在测试用例的J2SE环境中，这里引用ServletContext会抛停机异常。
	 * @author: kChen
	 * @createDate: 2014-5-6
	 * @return
	 * @throws Exception:
	 */
	Object getServletContexts() throws RuntimeException;

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
	ModelAndViewPage Pages(String page, Object obj) throws RuntimeException;

	/**
	 * 返回页面，并绑定一个Model
	 * 
	 * @param page
	 * @param modelName
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	ModelAndViewPage Pages(String page, String modelName, Object obj) throws RuntimeException;

	/**
	 * 返回一个JSON对象
	 * 
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	ModelAndViewAjax jsons(Object obj) throws RuntimeException;

	/**
	 * 获取一个UserInfoScopeData对象
	 * 
	 * @return
	 * @throws Exception
	 */
	UserInfoScope getUserInfoScopeDatas() throws RuntimeException;
	
	/**
	 * 根据用户id获取用户所有信息
	 * @description: 只能获取当前操作的站点信息，不能跨站点
	 * @author: kChen
	 * @createDate: 2014-6-30
	 * @param id
	 * @return
	 * @throws Exception:
	 */
	UserInfo getUserInfoById(String userId) throws RuntimeException;
	
	/**
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-10-11
	 * @param userId
	 * @param siteId
	 * @return
	 * @throws RuntimeException:
	 */
	UserInfo getUserInfo(String userId, String siteId) throws RuntimeException;
	/**
	 * 获取枚举参数数据列表，框架很根据站点自动查找，
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-6-23
	 * @return 如果枚举变量没有设定站点信息(siteid字段为NaN)则返回通用设置。无该枚举变量返回NULL
	 * @throws Exception:
	 */
	List<AppEnum> getEnum(String eCateCode) throws RuntimeException;
	
	/**
	 * 获取枚举参数数据字典
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-6-23
	 * @return
	 * @throws Exception:
	 */
	@Deprecated
	Map<String, List<AppEnum>> getEnumMap() throws RuntimeException;
	
	/**
	 * 获取全局参数，已经和站点绑定
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-6-24
	 * @param confName
	 * @return
	 * @throws Exception:
	 */
	Configuration getConfiguration(String confName) throws RuntimeException;
	
	/**
	 * 设置域全局变量，对于当前请求相应有效
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-7-8
	 * @param name
	 * @param attribute
	 * @throws RuntimeException:
	 */
	void setLocalAttribute(String name, Object attribute) throws RuntimeException;

	/**
	 * 获取域全局变量，对于当前请求相应有效
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-7-8
	 * @param name
	 * @return
	 * @throws RuntimeException:
	 */
	Object getLocalAttribute(String name) throws RuntimeException;

	/**
	 * 获取域全局变量表，对于当前请求相应有效
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-7-8
	 * @return
	 * @throws RuntimeException:
	 */
	Map<String, Object> getLocalAttributeMap() throws RuntimeException;
}
