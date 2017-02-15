package com.yudean.mvc.service;

import java.util.List;
import java.util.Map;

import com.yudean.mvc.bean.userinfo.UserInfo;

/**
 * 消息发送传递接口,已经增和内部消息广播和点对点发送功能.根据需求将要整合系统间消息传递功能
 * 
 * @company: gdyd
 * @className: MessageService.java
 * @author: kChen
 * @createDate: 2014-8-5
 * @updateUser: kChen
 * @version: 1.0
 */
public interface ItcMsgService {

	/**
	 * 发送短信，绑定velocity模板的变量
	 * 
	 * @param velocityTempId
	 *            模板ID
	 * @param velocityBindMap
	 *            模板绑定参数
	 * @param sendUserList
	 *            发送人列表
	 */
	void SendSms(String velocityTempId, Map<String, Object> velocityBindMap, List<UserInfo> sendUserList, UserInfo userInfo);

	/**
	 * 发送短信
	 * 
	 * @param msg
	 *            内容
	 * @param sendUserList
	 *            发送人列表
	 */
	void SendSms(String title, String msg, List<UserInfo> sendUserList, UserInfo userInfo) throws Exception;

	/**
	 * 发送邮件，绑定velocity模板的变量
	 * 
	 * @param velocityTempId
	 * @param velocityBindMap
	 * @param sendUserList
	 */
	void SendMail(String velocityTempId, Map<String, Object> velocityBindMap, List<UserInfo> sendUserList, UserInfo userInfo);
	
	/**
	 * 发送邮件，并不用通过发送参数检查
	 * @param velocityTempId
	 * @param velocityBindMap
	 * @param sendUserList
	 * @param userInfo
	 */
	void SendMailImm(String velocityTempId, Map<String, Object> velocityBindMap, List<UserInfo> sendUserList, UserInfo userInfo);

	/**
	 * 发送邮件
	 * 
	 * @param msg
	 * @param sendUserList
	 */
	void SendMail(String title, String msg, List<UserInfo> sendUserList, UserInfo userInfo) throws Exception;
}
