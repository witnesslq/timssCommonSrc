package com.yudean.itc.exception;

/**
 * 带有状态位和文字信息的异常(JSON格式)
 * @author 890157
 *
 */
public class StatusMessageException extends Exception {
	private static final long serialVersionUID = -2654985212593312738L;
	public StatusMessageException(String message) {
        super(message);
    }
}
