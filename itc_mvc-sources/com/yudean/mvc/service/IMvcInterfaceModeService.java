package com.yudean.mvc.service;

/**
 * 框架处理接口登陆的服务类
 * @company: gdyd
 * @className: IMvcInterfaceModeService.java
 * @author: kChen
 * @createDate: 2014-9-19
 * @updateUser: kChen
 * @version: 1.0
 */
public interface IMvcInterfaceModeService {
	/**
	 * 处理接口登陆模式
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-9-19
	 * @param mode
	 * @param data
	 * @throws Exception:
	 */
	String processInterfaceMode(String mode, String data) throws Exception;
}
