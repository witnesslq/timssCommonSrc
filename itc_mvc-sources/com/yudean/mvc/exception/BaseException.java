package com.yudean.mvc.exception;

/**
 * Timss基础异常
 * @title: {title}
 * @description: {desc}
 * @company: gdyd
 * @className: TimssException.java
 * @author: kChen
 * @createDate: 2014-7-15
 * @updateUser: kChen
 * @version: 1.0
 */
public class BaseException extends Exception implements  BaseExceptionInterface {
	/**
	 * timss异常基础抽象类
	 */
	private static final long serialVersionUID = 1L;
	public BaseException(){
		super();
	}
	
	public BaseException(String errInfo){
		super(errInfo);
	}

	@Override
	public Object getExceptionMsg() throws Exception {
		return this.getMessage();
	}
}
