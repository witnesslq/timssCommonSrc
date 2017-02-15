package com.yudean.mvc.exception;

/**
 * TIMSS实例化异常
 * @title: {title}
 * @description: {desc}
 * @company: gdyd
 * @className: TimssRunInstantiationException.java
 * @author: kChen
 * @createDate: 2014-7-7
 * @updateUser: kChen
 * @version: 1.0
 */
public class RunInstantiationException extends MvcRuntimeException {
	private static final long serialVersionUID = -6620523093525639507L;
	public RunInstantiationException(){
		super();
	}
	
	public RunInstantiationException(String errInfo){
		super(errInfo);
	}
}
