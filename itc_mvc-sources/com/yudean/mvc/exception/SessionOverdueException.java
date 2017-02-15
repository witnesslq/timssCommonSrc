package com.yudean.mvc.exception;

/**
 * Session 过期异常
 * @company: gdyd
 * @className: SessionOverdueException.java
 * @author: kChen
 * @createDate: 2014-9-18
 * @updateUser: kChen
 * @version: 1.0
 */
@SuppressWarnings("serial")
public class SessionOverdueException extends MvcRuntimeException {
	public SessionOverdueException(String info){
		super(info);
	}
}
