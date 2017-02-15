package com.yudean.mvc.exception;

/**
 * Timss运行期异常
 * @title: {title}
 * @description: {desc}
 * @company: gdyd
 * @className: TimssRuntimeException.java
 * @author: kChen
 * @createDate: 2014-7-7
 * @updateUser: kChen
 * @version: 1.0
 */
public class MvcRuntimeException extends RuntimeException implements  BaseExceptionInterface {
	/**
	 * timss异常基础抽象类
	 */
	private static final long serialVersionUID = 1L;
	public MvcRuntimeException(){
		super();
	}
	
	public MvcRuntimeException(String errInfo){
		super(errInfo);
	}

	@Override
	public Object getExceptionMsg() throws Exception {
		return this.getMessage();
	}
}