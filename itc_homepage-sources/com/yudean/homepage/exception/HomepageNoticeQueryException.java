package com.yudean.homepage.exception;

/**
 * 通知查询相关异常
 * @author kchen
 *
 */
@SuppressWarnings("serial")
public class HomepageNoticeQueryException extends RuntimeException{

	public HomepageNoticeQueryException(String msg){
		super(msg);
	}

	public HomepageNoticeQueryException(String msg, RuntimeException e){
		super(msg, e);
	}
}
