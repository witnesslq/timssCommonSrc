package com.yudean.itc.exception;

/**
 * 单纯文字信息的异常
 * @author 890157
 *
 */
public class SimpleMessageException extends Exception {
	private static final long serialVersionUID = 901193433466470709L;

	public SimpleMessageException(String message) {
        super(message);
    }
}
