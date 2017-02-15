package com.yudean.mvc.bean.userinfo.impl;

import java.util.List;

import com.yudean.mvc.bean.userinfo.UserInfo;
import com.yudean.itc.dto.sec.Organization;
import com.yudean.itc.dto.sec.Role;
import com.yudean.itc.dto.sec.SecureUser;

/**
 * User实现类,由于目前数据来源来自权限管理模块，这里数据继承自SecureUser，并实现了TIMSS的UserInfo接口，
 * 在TIMSS内部，只能看见UserInfo接口，而无需关心SecureUser的内容，实现和权限管理模块的松耦合
 * 
 * @author kChen
 * 
 */
@SuppressWarnings("serial")
public class UserInfoImpl extends SecureUser implements UserInfo {
	@Override
	public String getSiteId() {
		return this.getCurrentSite();
	}

	@Override
	public String getUserId() {
		return this.getId();
	}

	@Override
	public String getUserName() {
		return this.getName();
	}

	@Override
	public String getOrgId() {
		String orgId = null;
		List<Organization> list = this.getOrganizations();
		if (null != list && 0 < list.size()) {
			orgId = list.get(0).getCode();
		}
		return orgId;
	}

	@Override
	public String getOrgName() {
		String orgName = null;
		List<Organization> list = this.getOrganizations();
		if (null != list && 0 < list.size()) {
			orgName = list.get(0).getName();
		}
		return orgName;
	}

	@Override
	public List<Organization> getOrgs() {
		return this.getOrganizations();
	}

	@Override
	public String getRoleId() {
		String roleId = null;
		List<Role> list = this.getRoles();
		if (null != list && 0 < list.size()) {
			roleId = list.get(0).getId();
		}
		return roleId;
	}

	@Override
	public String getRoleName() {
		String roleName = null;
		List<Role> list = this.getRoles();
		if (null != list && 0 < list.size()) {
			roleName = list.get(0).getName();
		}
		return roleName;
	}

	@Override
	public List<Role> getRoles() {
		return super.getRoles();
	}

	@Override
	public SecureUser getSecureUser() {
		return this;
	}
}
