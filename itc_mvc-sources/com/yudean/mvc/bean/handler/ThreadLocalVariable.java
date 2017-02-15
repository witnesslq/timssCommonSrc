package com.yudean.mvc.bean.handler;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.yudean.mvc.bean.userinfo.UserInfoScope;
import com.yudean.itc.bean.exception.ExceptionData;

/**
 * 线程全局变量
 * @company: gdyd
 * @className: ThreadLocalVariable.java
 * @author: kChen
 * @createDate: 2014-10-27
 * @updateUser: kChen
 * @version: 1.0
 */
public class ThreadLocalVariable{
	/**
	 * 框架所使用的参数定义名称
	 * @title: {title}
	 * @description: {desc}
	 * @company: gdyd
	 * @className: ThreadLocalVariable.java
	 * @author: kChen
	 * @createDate: 2014-7-21
	 * @updateUser: kChen
	 * @version: 1.0
	 */
	public enum GlobalVarableScopeType {
		Frame_Enums_Type,//定义枚举类型变量的传递名称
		Frame_Page_Path,//文档路径
		Frame_Page_Name,//文件名称
		Frame_Annotation_VaildParam//绑定字符处理标记
	}
	UserInfoScope scopeData;//用户requests数据
	Exception mvcRunException = null;//运行异常参数
	ExceptionData exceptionData = null;//异常处理类
	Method mvcExceptionControllerMethod = null;//执行异常的controller层方法
	String modelName;//运行模块名称
	Map<String, Object> attribute = null;//附加属性
	
	public ThreadLocalVariable(){
		attribute = new HashMap<String, Object>();
	}
		
	public void setUserInfoScope(UserInfoScope scope) throws RuntimeException {
		this.scopeData = scope;		
	}

	
	public UserInfoScope getUserInfoScope() throws RuntimeException {
		return this.scopeData;
	}

	
	public void setExceptionData(ExceptionData exceptionData) throws RuntimeException {
		this.exceptionData = exceptionData;
	}

	
	public ExceptionData getExceptionData() throws RuntimeException {
		return this.exceptionData;
	}

	
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	
	public String getModelName() {
		return this.modelName;
	}

	
	public void setThreadLocalAttribute(String name, Object attribute) throws RuntimeException {
		this.attribute.put(name, attribute);
	}

	
	public Object getThreadLocalAttribute(String name) throws RuntimeException {
		return this.attribute.get(name);
	}

	
	public Map<String, Object> getThreadLocalAttributeMap() throws RuntimeException {
		return this.attribute;
	}


	public Exception getMvcRunException() {
		return mvcRunException;
	}


	public void setMvcRunException(Exception mvcRunException) {
		this.mvcRunException = mvcRunException;
	}

	public Method getMvcExceptionControllerMethod() {
		return mvcExceptionControllerMethod;
	}

	public void setMvcExceptionControllerMethod(Method mvcExceptionControllerMethod) {
		this.mvcExceptionControllerMethod = mvcExceptionControllerMethod;
	}
}
