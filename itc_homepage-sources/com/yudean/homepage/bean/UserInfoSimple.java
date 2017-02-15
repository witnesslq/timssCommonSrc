package com.yudean.homepage.bean;

import java.util.List;

import com.yudean.itc.dto.sec.Organization;

public class UserInfoSimple {

	private String id;
	private String siteId;
	private List<Organization> orgs;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSiteId() {
		return siteId;
	}
	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}
	public List<Organization> getOrgs() {
		return orgs;
	}
	public void setOrgs(List<Organization> orgs) {
		this.orgs = orgs;
	}
	
	
}
