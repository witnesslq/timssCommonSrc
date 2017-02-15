package com.yudean.homepage.exception;

/**
 * 通知查询相关异常
 * @author kchen
 *
 */
@SuppressWarnings("serial")
public class HomepageNoticeModifyException extends RuntimeException{

	public HomepageNoticeModifyException(String msg){
		super(msg);
	}

	public HomepageNoticeModifyException(String msg, RuntimeException e){
		super(msg, e);
	}
}
