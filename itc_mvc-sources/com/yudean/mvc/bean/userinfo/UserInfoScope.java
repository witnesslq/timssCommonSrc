package com.yudean.mvc.bean.userinfo;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.yudean.itc.dto.Page;

/**
 * user区域数据，根据用户每次访问的request创建，继承自UserInfo，
 * @author kChen
 *
 */
public interface UserInfoScope extends UserInfo {
	void setRequest(HttpServletRequest request)throws RuntimeException;
	void setSession(HttpSession session)throws RuntimeException;
	HttpSession getSession();
	
	/**
	 * 获取前端传递的参数,此方法不带模板类
	 * @param paramName
	 * @return
	 * @throws Exception
	 */
	String getParam(String paramName)throws Exception;
	
	/**
	 * 从参数中获取JavaBean
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-6-23
	 * @param paramName
	 * @param clazz
	 * @return
	 * @throws Exception:
	 */
	<T extends Object> T getJavaBeanParam(String paramName, Class<T> clazz)throws RuntimeException;
	/**
	 * 获取参数的数据字典集
	 * @return
	 * @throws Exception
	 */
	 Map<String, String[]> getParamMap()throws RuntimeException;
	
	/**
	 * 获取当前分页信息
	 * @return
	 * @throws Exception
	 */
	Integer getRows()throws RuntimeException;
	
	/**
	 * 获取当前页面
	 * @return
	 * @throws Exception
	 */
	Integer getCurrentPage()throws RuntimeException;

	/**
	 * 获取分页对象
	 * @return
	 * @throws Exception
	 */
	<T extends Object> Page<T>  getPage()throws RuntimeException;
}
