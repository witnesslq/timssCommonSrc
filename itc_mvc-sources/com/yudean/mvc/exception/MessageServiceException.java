package com.yudean.mvc.exception;

/**
 * 消息服务异常
 * @title: {title}
 * @description: {desc}
 * @company: gdyd
 * @className: TimssMessageServiceException.java
 * @author: kChen
 * @createDate: 2014-8-6
 * @updateUser: kChen
 * @version: 1.0
 */
public class MessageServiceException extends MvcRuntimeException {
	private static final long serialVersionUID = 1L;
	public MessageServiceException(){
		super();
	}
	
	public MessageServiceException(String errInfo){
		super(errInfo);
	}
}
