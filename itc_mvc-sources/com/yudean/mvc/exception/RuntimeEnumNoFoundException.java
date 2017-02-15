package com.yudean.mvc.exception;

/**
 * 枚举变量未找到
 * @title: {title}
 * @description: {desc}
 * @company: gdyd
 * @className: TimssRuntimeEnumNoFoundException.java
 * @author: kChen
 * @createDate: 2014-8-13
 * @updateUser: kChen
 * @version: 1.0
 */
public class RuntimeEnumNoFoundException extends MvcRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4446319112394188652L;

	public RuntimeEnumNoFoundException(){
		super();
	}
	
	public RuntimeEnumNoFoundException(String errInfo){
		super(errInfo);
	}
}
