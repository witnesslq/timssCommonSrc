package com.yudean.homepage.vo;

import java.util.Date;

public class WorktaskFlowViewObj {
	private String flowno;
	private String processid;
	private String name;
	private Date statusdate;
	private String statusname;
	private String createusername;
	private String previousUser;
	public String getFlowno() {
		return flowno;
	}
	public void setFlowno(String flowno) {
		this.flowno = flowno;
	}
	public String getProcessid() {
		return processid;
	}
	public void setProcessid(String processid) {
		this.processid = processid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getStatusdate() {
		return statusdate;
	}
	public void setStatusdate(Date statusdate) {
		this.statusdate = statusdate;
	}
	public String getStatusname() {
		return statusname;
	}
	public void setStatusname(String statusname) {
		this.statusname = statusname;
	}
	public String getCreateusername() {
		return createusername;
	}
	public void setCreateusername(String createusername) {
		this.createusername = createusername;
	}
	public String getPreviousUser() {
		return previousUser;
	}
	public void setPreviousUser(String previousUser) {
		this.previousUser = previousUser;
	}
}
