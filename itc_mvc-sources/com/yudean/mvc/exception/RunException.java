package com.yudean.mvc.exception;

/**
 * TIMSS运行期间异常基础类，针对TIMSS通用异常跑出的情況
 * @author kChen
 *
 */
public class RunException extends BaseException {
	private static final long serialVersionUID = -1L;
	public RunException(String errInfo){
		super(errInfo);
	}
}
