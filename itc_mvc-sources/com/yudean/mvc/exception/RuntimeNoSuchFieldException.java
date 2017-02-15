package com.yudean.mvc.exception;
/**
 * Timss运行期映射转换异常
 * @title: {title}
 * @description: {desc}
 * @company: gdyd
 * @className: TimssRuntimeNoSuchFieldException.java
 * @author: kChen
 * @createDate: 2014-7-7
 * @updateUser: kChen
 * @version: 1.0
 */
public class RuntimeNoSuchFieldException extends MvcRuntimeException {
	/**
	 * 继承序列号
	 */
	private static final long serialVersionUID = -8837736846015120761L;
	public RuntimeNoSuchFieldException() {
		super();
	}
	public RuntimeNoSuchFieldException(String errInfo) {
		super(errInfo);
	}
}
