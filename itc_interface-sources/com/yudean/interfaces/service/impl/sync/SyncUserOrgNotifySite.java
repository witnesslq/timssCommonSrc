package com.yudean.interfaces.service.impl.sync;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.yudean.interfaces.dao.SyncLogDao;
import com.yudean.itc.code.NotificationType;
import com.yudean.itc.code.SyncLogType;
import com.yudean.itc.dto.interfaces.sync.SyncLogBean;
import com.yudean.itc.dto.sec.Organization;
import com.yudean.itc.dto.sec.SecureOrgUser;
import com.yudean.itc.dto.sec.SecureUser;
import com.yudean.itc.manager.support.impl.NotificationManager;
import com.yudean.itc.util.StringHelper;

/**
 * 同步日志更新日志发送更新信息类
 * 
 * @author kchen
 * 
 */
@Component
@Scope("prototype")
public class SyncUserOrgNotifySite {

	private static final Logger LOG = Logger.getLogger(SyncUserOrgNotifySite.class);

	private List<SyncLogBean> logInfoList;// 同步信息日志列表

	private String siteId;// 站点

	private boolean logModify = false;// 标记是否记录修改信息

	@Autowired
	private SyncLogDao syncLogDao;

	@Autowired
	private NotificationManager notificationManager;

	final private String s_AddDept = "新增部门信息";
	final private String s_ModifyDept = "修改部门信息";
	final private String s_ExceptionDept = "更新部门信息时发生异常";
	final private String s_DeleteDept = "删除部门";

	final private String s_UserDeptName = "部门";
	final private String s_PtLeft = "(";
	final private String s_PtRight = ")";
	final private String s_AddUserUserName = "添加用户:";
	final private String s_ModifyUserUserName = "添加用户:";
	final private String s_DeleteUserUserName = "删除用户:";
	final private String s_ExceptionUser = "更新用户信息时发生异常.";

	public void init(String siteId) {
		this.siteId = siteId;
		logInfoList = new ArrayList<SyncLogBean>();
	}

	/**
	 * 记录部门变动信息
	 * 
	 * @param newOrg
	 * @param oldOrg
	 * @param operType
	 */
	public void modifyDept(Organization newOrg, Organization oldOrg, SyncLogType operType, String extInfo) throws NullPointerException {
		SyncLogBean bean = new SyncLogBean();
		boolean isExcSuc = false;
		switch (operType) {
		case Add: {
			bean.setSyncInfo(s_AddDept);
			bean.setDataaft(newOrg.toString());
			bean.setType(operType);
			isExcSuc = true;
			break;
		}
		case Modify: {
			if (logModify) {
				bean.setSyncInfo(s_ModifyDept);
				bean.setDataaft(newOrg.toString());
				bean.setDatabef(oldOrg.toString());
				bean.setType(operType);
				isExcSuc = true;
			}
			break;
		}
		case Exception: {
			bean.setSyncInfo(s_ExceptionDept);
			bean.setDatabef(oldOrg.toString());
			bean.setType(operType);
			isExcSuc = true;
			break;
		}
		case Delete: {
			bean.setSyncInfo(s_DeleteDept);
			bean.setDatabef(oldOrg.toString());
			bean.setType(operType);
			isExcSuc = true;
			break;
		}
		default: {
			isExcSuc = false;
			break;
		}
		}
		if (isExcSuc) {
			bean.setSiteid(siteId);
			bean.setCreatetime(new Date());
			logInfoList.add(bean);
		}
	}

	private String subStr(String str) throws NullPointerException {
		if (null == str) {
			return "";
		} else if (str.length() < 201) {
			return str;
		} else {
			return str.substring(0, 200);
		}
	}

	/**
	 * 记录人员变动信息
	 * 
	 * @param newUser
	 * @param oldUser
	 * @param operType
	 */
	public void modifyUser(Organization orgCur, SecureUser newUser, SecureOrgUser oldUser, SyncLogType operType, String extInfo) throws NullPointerException {
		SyncLogBean bean = new SyncLogBean();
		boolean isExcSuc = false;
		switch (operType) {
		case Add: {
			bean.setSyncInfo(StringHelper.concat(s_UserDeptName, s_UserDeptName, orgCur.getName(), s_PtLeft, orgCur.getCode(), s_PtRight, s_AddUserUserName, newUser.getName(),
					s_PtLeft, newUser.getId(), s_PtRight));
			bean.setDataaft(newUser.toString());
			bean.setType(operType);
			isExcSuc = true;
			break;
		}
		case Modify: {
			if (logModify) {
				bean.setSyncInfo(StringHelper.concat(s_UserDeptName, orgCur.getName(), s_PtLeft, orgCur.getCode(), s_PtRight, s_ModifyUserUserName, newUser.getName(), s_PtLeft,
						newUser.getId(), s_PtRight));
				bean.setDataaft(newUser.toString());
				bean.setDatabef(oldUser.toString());
				bean.setType(operType);
				isExcSuc = true;
			}
			break;
		}
		case Exception: {
			bean.setSyncInfo(StringHelper.concat(s_ExceptionUser, orgCur.getName(), s_PtLeft, orgCur.getCode(), s_PtRight, s_ModifyUserUserName, newUser.getName(), s_PtLeft,
					newUser.getId(), s_PtRight, subStr(extInfo)));
			bean.setDatabef(oldUser.toString());
			bean.setType(operType);
			isExcSuc = true;
			break;
		}
		case Delete: {
			bean.setSyncInfo(StringHelper.concat(s_UserDeptName, orgCur.getName(), s_PtLeft, orgCur.getCode(), s_PtRight, s_DeleteUserUserName, oldUser.getName(), s_PtLeft,
					oldUser.getId(), s_PtRight));
			bean.setDatabef(oldUser.toString());
			bean.setType(operType);
			isExcSuc = true;
			break;
		}
		default: {
			isExcSuc = false;
			break;
		}
		}
		if (isExcSuc) {
			bean.setSiteid(siteId);
			bean.setCreatetime(new Date());
			logInfoList.add(bean);
		}
	}

	public void notifyLog(List<SecureUser> secUserList) throws NullPointerException {
		StringBuffer emailBuffer = new StringBuffer();
		emailBuffer.append("<DIV>");
		emailBuffer.append("<BR>");
		if (null != logInfoList && !logInfoList.isEmpty()) {
			for (SyncLogBean bean : logInfoList) {
				try {
					emailBuffer.append(bean.toString());
					emailBuffer.append("<BR>");
					syncLogDao.addLog(bean);
				} catch (Exception e) {
					LOG.error("更新日志发生异常", e);
				}
			}
			emailBuffer.append("</DIV>");
			String[] recipients = new String[secUserList.size()];
			int count = 0;
			for (SecureUser secUser : secUserList) {
				recipients[count++] = secUser.getEmail();
			}
			notificationManager.notify(recipients, NotificationType.EMAIL, "人事数据同步异动信息", emailBuffer.toString());
		}
	}
}
