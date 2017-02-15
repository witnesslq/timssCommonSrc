package com.yudean.mvc.exception;

/**
 * 数据不存在异常
 * @title: {title}
 * @description: {desc}
 * @company: gdyd
 * @className: TimssRuntimeDataNotFoundException.java
 * @author: kChen
 * @createDate: 2014-7-15
 * @updateUser: kChen
 * @version: 1.0
 */
public class RuntimeDataNotFoundException extends MvcRuntimeException {
	private static final long serialVersionUID = -1L;
	public RuntimeDataNotFoundException() {
		super();
	}
	public RuntimeDataNotFoundException(String errInfo) {
		super(errInfo);
	}
}
