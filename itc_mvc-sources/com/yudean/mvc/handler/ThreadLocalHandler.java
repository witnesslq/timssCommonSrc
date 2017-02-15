package com.yudean.mvc.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.yudean.itc.dto.sec.SecureUser;
import com.yudean.itc.manager.sec.IAuthenticationManager;
import com.yudean.itc.util.Constant;
import com.yudean.mvc.bean.userinfo.impl.UserInfoImpl;
import com.yudean.mvc.util.LogUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.yudean.itc.bean.exception.ExceptionData;
import com.yudean.itc.util.ClassCastUtil;
import com.yudean.mvc.bean.handler.ThreadLocalVariable;
import com.yudean.mvc.bean.userinfo.UserInfo;
import com.yudean.mvc.bean.userinfo.UserInfoScope;
import com.yudean.mvc.bean.userinfo.impl.UserInfoScopeImpl;
import com.yudean.mvc.configs.MvcWebConfig;
import com.yudean.mvc.exception.SessionOverdueException;
import com.yudean.mvc.service.impl.FrameWorkServiceImpl;
import com.yudean.mvc.util.ParseStrUtil;

/**
 * TIMSS框架域线程管理，每个request都会分配一个线程对象，在发生请求时，工程会配置一个线层给当前请求使用。
 * 该类的功能是在线程上增加一个handler，将传递数据写入到 handler上。
 * 
 * @author kChen
 * 
 */
public class ThreadLocalHandler {
	static private Logger log = Logger.getLogger(ThreadLocalHandler.class);
	static private ThreadLocal<ThreadLocalVariable> local = new ThreadLocal<ThreadLocalVariable>();

	/**
	 * 获取线程数据
	 * 
	 * @return
	 * @throws Exception
	 */
	static public ThreadLocalVariable getVariable() throws RuntimeException {
		ThreadLocalVariable variable = local.get();
		return variable;
	}

	/**
	 * 设置线程数据
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-10-29
	 * @param variable
	 * @throws RuntimeException
	 *             :
	 */
	static public void setVariable(ThreadLocalVariable variable) throws RuntimeException {
		local.set(variable);
	}

	/**
	 * 设置线程数据
	 * 
	 * @throws Exception
	 */
	static void initVariable() throws RuntimeException {// 每次进入时从线程池注入
														// request域变量
		Thread current = Thread.currentThread();
		log.debug("ThreanId:" + current.getId());
		ThreadLocalVariable value = new ThreadLocalVariable();
		local.set(value);
	}

	/**
	 * 创建线程实例的全局Session
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-10-11
	 * @param userInfo
	 * @throws RuntimeException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 *             :
	 */
	static public void createNewVarableOweUserInfo(UserInfo userInfo) throws RuntimeException, InstantiationException, IllegalAccessException, NoSuchFieldException {
		initVariable();
		setUserInfoScope(userInfo, null, null);
	}

	/**
	 * 设置线程数据
	 * 
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws Exception
	 */
	static void setUserInfoVariable(HttpServletRequest request, HttpServletResponse response, IAuthenticationManager manager) throws SecurityException, IllegalArgumentException, InstantiationException,
			IllegalAccessException, NoSuchFieldException {
		Thread current = Thread.currentThread();
		log.debug("ThreanId:" + current.getId());
		HttpSession session = request.getSession();
		// TEIP用的token登录接口
		String token = StringUtils.trimToNull(request.getParameter("_login_token"));
		if(token != null && session.getAttribute(Constant.secUser) == null){
			SecureUser secUser = manager.signWithToken(token);
			if(secUser != null){
				UserInfoImpl userInfo = null;
				try {
					// 将权限管理模块的SecureUser对象数据注入到 timss UserInfo接口中
					userInfo = ClassCastUtil.castAllField2Class(UserInfoImpl.class, secUser);
				} catch (Exception ex) {
					LogUtil.error("转换用户类型数据异常 secUser->userInfo,数据类容:" + secUser, ex);
				}
				session.setAttribute(Constant.secUser, userInfo);
			}
		}
		UserInfo userInfo = (UserInfo) session.getAttribute(FrameWorkServiceImpl.getRunEnvironmentData().getSessionUserInfoName());
		if (null == userInfo) {
			String requestUrl = request.getRequestURI();
			if (accessPathListWithoutLog(requestUrl)) {
				log.info("路径" + requestUrl + "未登陆直接访问后端");
			} else {
				log.info("路径" + requestUrl + "用户权限过期或未登陆");
				ThreadLocalVariable value = local.get();
				value.setMvcRunException(new SessionOverdueException("用户权限过期或未登陆"));
			}
		} else {
			//设置userScope信息
			setUserInfoScope(userInfo, request, session);
			ThreadLocalVariable value = local.get();
			//获取请求路径
			String sServletPath = request.getServletPath();
			//解析模块名称
			String sModuleName = ParseStrUtil.parseModuleName(sServletPath);
			value.setModelName(sModuleName);
			value.setExceptionData(null);
		}
	}

	static void setUserInfoScope(UserInfo userInfo, HttpServletRequest request, HttpSession session) throws SecurityException, IllegalArgumentException, InstantiationException,
			IllegalAccessException, NoSuchFieldException {
		UserInfoScope userInfoScope = ClassCastUtil.castAllField2Class(UserInfoScopeImpl.class, userInfo);
		userInfoScope.setRequest(request);
		userInfoScope.setSession(session);
		ThreadLocalVariable value = local.get();
		value.setUserInfoScope(userInfoScope);
	}

	static private boolean accessPathListWithoutLog(String path) {
		for (String access : MvcWebConfig.accessPathListWithoutLog) {
			if (path.contains(access)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 设置异常信息
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-7-14
	 * @param exceptionData
	 * @throws RuntimeException
	 *             :
	 */
	static void setExceptionVariable(ExceptionData exceptionData) throws RuntimeException {// 每次进入时从线程池注入
																							// request域变量
		Thread current = Thread.currentThread();
		log.debug("ThreanId:" + current.getId());
		ThreadLocalVariable value = local.get();
		value.setExceptionData(exceptionData);
	}

	/**
	 * 获取本来类实例
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-7-14
	 * @return:
	 */
	static public ThreadLocalHandler getInstance() {
		return new ThreadLocalHandler();
	}

	/**
	 * 获取线程变量
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-7-14
	 * @return
	 * @throws RuntimeException
	 *             :
	 */
	public ThreadLocalVariable getVariableIns() throws RuntimeException {
		return ThreadLocalHandler.getVariable();
	}
}
