package com.yudean.mvc.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yudean.itc.code.NotificationType;
import com.yudean.itc.dao.sec.SiteMapper;
import com.yudean.itc.dto.sec.SecureUser;
import com.yudean.itc.manager.support.impl.NotificationManager;
import com.yudean.itc.util.ReflectionUtils;
import com.yudean.mvc.bean.userinfo.UserInfo;
import com.yudean.mvc.service.ItcMsgService;
import com.yudean.mvc.service.ItcSiteService;
import com.yudean.mvc.service.ItcSysConfService;

@Service
public class ItcMsgServiceImpl implements ItcMsgService {

	private static final Logger LOGGER = Logger.getLogger(ItcMsgServiceImpl.class);
	
	@Autowired
	NotificationManager notificationManager;

	@Autowired
	ItcSysConfService SysConfService;
	
	@Autowired
	ItcSiteService itcSiteService;

	@Override
	public void SendSms(String velocityTempId, Map<String, Object> velocityBindMap, List<UserInfo> sendUserList, UserInfo userInfo) {
		NotificationType[] type = { NotificationType.SMS };
		Send(velocityTempId, velocityBindMap, sendUserList, userInfo, type);
	}

	@Override
	public void SendSms(String title, String msg, List<UserInfo> sendUserList, UserInfo userInfo) throws Exception {
		Send(title, msg, sendUserList, userInfo, NotificationType.SMS);
	}

	@Override
	public void SendMail(String velocityTempId, Map<String, Object> velocityBindMap, List<UserInfo> sendUserList, UserInfo userInfo) {
		NotificationType[] type = { NotificationType.EMAIL };
		Send(velocityTempId, velocityBindMap, sendUserList, userInfo, type);
	}

	@Override
	public void SendMail(String title, String msg, List<UserInfo> sendUserList, UserInfo userInfo) throws Exception {
		Send(title, msg, sendUserList, userInfo, NotificationType.EMAIL);
	}

	/*
	 * 发送模板类型
	 */
	private void Send(String velocityTempId, Map<String, Object> velocityBindMap, List<UserInfo> sendUserList, UserInfo userInfo, NotificationType... notificationTypes) {
		List<SecureUser> secUList = NotificationType.EMAIL.equals(notificationTypes[0]) ? getSendUserListEmail(userInfo, sendUserList) : getSendUserListSms(userInfo, sendUserList);
		if (!secUList.isEmpty()) {
			notificationManager.notify(secUList, userInfo.getSiteId(),notificationTypes, velocityTempId, velocityBindMap);
		}
	}

	/*
	 * 发送已定义的数据类型
	 */
	private void Send(String title, String msg, List<UserInfo> sendUserList, UserInfo userInfo, NotificationType notificationType) throws Exception {
		List<SecureUser> secUList = NotificationType.EMAIL.equals(notificationType) ? getSendUserListEmail(userInfo, sendUserList) : getSendUserListSms(userInfo, sendUserList);
		if (!secUList.isEmpty()) {
			notificationManager.notify(getSendAddress(secUList, notificationType),userInfo.getSiteId(), notificationType, title, msg);
		}
	}

	/*
	 * 获取可发送用户列表,短信
	 */
	private List<SecureUser> getSendUserListSms(UserInfo userInfo, List<UserInfo> sendUserList) {
		List<SecureUser> secUList = new ArrayList<SecureUser>();
		//20160428 zhx 站点短信配置移至site表
		//if (SysConfService.isSiteSend(userInfo.getSiteId(), userInfo)) 
		if (itcSiteService.isSMSSend(userInfo.getSiteId())) {
			for (UserInfo user : sendUserList) {
				if (SysConfService.isUserSendSms(user.getUserId(), userInfo.getSiteId(), userInfo)) {
					secUList.add(user.getSecureUser());
				}
			}
		}
		return secUList;
	}

	/*
	 * 获取可发送列表，邮件
	 */
	private List<SecureUser> getSendUserListEmail(UserInfo userInfo, List<UserInfo> sendUserList) {
		List<SecureUser> secUList = new ArrayList<SecureUser>();
		//20160428 zhx 站点短信配置移至site表
		//if (SysConfService.isSiteSend(userInfo.getSiteId(), userInfo))
		if (itcSiteService.isMailSend(userInfo.getSiteId())) {
			for (UserInfo user : sendUserList) {
				if (SysConfService.isUserSendEmail(user.getUserId(), userInfo.getSiteId(), userInfo)) {
					secUList.add(user.getSecureUser());
				}
			}
		}
		return secUList;
	}

	/*
	 * 获取发送地址
	 */
	private String[] getSendAddress(List<SecureUser> sendUserList, NotificationType notificationType) throws Exception {
		String[] retInfo = new String[sendUserList.size()];
		String fieldName = null;
		if (NotificationType.EMAIL.equals(notificationType)) {
			fieldName = "email";
		} else {
			fieldName = "mobile";
		}
		for (int index = 0; index < sendUserList.size(); index++) {
			retInfo[index] = (String) ReflectionUtils.invokeGetterMethod(sendUserList.get(index), fieldName);
		}
		return retInfo;
	}

	@Override
	public void SendMailImm(String velocityTempId, Map<String, Object> velocityBindMap, List<UserInfo> sendUserList, UserInfo userInfo) {
		//先判断当前站点是否配置可发送邮件
		if (!itcSiteService.isMailSend(userInfo.getSiteId())) {
			LOGGER.info("当前站点没有配置发送短信。");
			return ;
		}
		List<SecureUser> secUList = new ArrayList<SecureUser>();
		for (UserInfo user : sendUserList) {
			if (SysConfService.isUserSendEmail(user.getUserId(), userInfo.getSiteId(), userInfo)) {
				secUList.add(user.getSecureUser());
			}
		}
		NotificationType[] notificationTypes = { NotificationType.EMAIL };
		notificationManager.notify(secUList, notificationTypes, velocityTempId, velocityBindMap);
	}

	
}