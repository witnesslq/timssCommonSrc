package com.yudean.mvc.exception;

/**
 * Timss框架內部定义的异常数据接口
 * @title: {title}
 * @description: {desc}
 * @company: gdyd
 * @className: TimssExceptionInterface.java
 * @author: kChen
 * @createDate: 2014-7-15
 * @updateUser: kChen
 * @version: 1.0
 */
public interface BaseExceptionInterface {
	/**
	 * 获取异常信息
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-7-15
	 * @return
	 * @throws Exception:
	 */
	Object getExceptionMsg() throws Exception;
}
