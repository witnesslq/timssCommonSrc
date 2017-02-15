package com.yudean.mvc.exception;

/**
 * 标记TIMSS运行列表数据为空的异常，返回状态为200，是一个运行期异常，返回前端列表数据
 * @title: {title}
 * @description: {desc}
 * @company: gdyd
 * @className: TimssRuntimeListDataNotFoundException.java
 * @author: kChen
 * @createDate: 2014-7-15
 * @updateUser: kChen
 * @version: 1.0
 */
public class RuntimeListDataNotFoundException extends RuntimeDataNotFoundException {
	private static final long serialVersionUID = 3315891511723084163L;
	public RuntimeListDataNotFoundException() {
		super();
	}
	public RuntimeListDataNotFoundException(String errInfo) {
		super(errInfo);
	}
}
