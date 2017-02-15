package com.yudean.homepage.bean;

import com.yudean.mvc.bean.ItcMvcBean;

/**
 * 首页（工作任务）人员相关Bean,对应DB中工作任务人员表
 * 
 * @title: {title}
 * @description: {desc}
 * @company: gdyd
 * @className: WorktaskUserBean.java
 * @author: kChen
 * @createDate: 2014-7-8
 * @updateUser: kChen
 * @version: 1.0
 */
public class WorktaskUserBean extends ItcMvcBean {
	private static final long serialVersionUID = -3786743672442665809L;

	static public enum WorkTaskUserFlag {
		Cur, // 当前办理人
		His, // 历史办理人,
		NO // 停用
	}

	String id;
	String subid;
	String inputSubid;
	String usercode;
	String username;
	String siteid;
	WorkTaskUserFlag flag;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSubid() {
		return subid;
	}

	public void setSubid(String subid) {
		this.subid = subid;
	}

	public String getInputSubid() {
		return inputSubid;
	}

	public void setInputSubid(String inputSubid) {
		this.inputSubid = inputSubid;
	}

	public String getUsercode() {
		return usercode;
	}

	public void setUsercode(String usercode) {
		this.usercode = usercode;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getSiteid() {
		return siteid;
	}

	public void setSiteid(String siteid) {
		this.siteid = siteid;
	}

	public WorkTaskUserFlag getFlag() {
		return flag;
	}

	public void setFlag(WorkTaskUserFlag flag) {
		this.flag = flag;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((flag == null) ? 0 : flag.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((inputSubid == null) ? 0 : inputSubid.hashCode());
		result = prime * result + ((siteid == null) ? 0 : siteid.hashCode());
		result = prime * result + ((subid == null) ? 0 : subid.hashCode());
		result = prime * result + ((usercode == null) ? 0 : usercode.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WorktaskUserBean other = (WorktaskUserBean) obj;
		if (flag != other.flag)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (inputSubid == null) {
			if (other.inputSubid != null)
				return false;
		} else if (!inputSubid.equals(other.inputSubid))
			return false;
		if (siteid == null) {
			if (other.siteid != null)
				return false;
		} else if (!siteid.equals(other.siteid))
			return false;
		if (subid == null) {
			if (other.subid != null)
				return false;
		} else if (!subid.equals(other.subid))
			return false;
		if (usercode == null) {
			if (other.usercode != null)
				return false;
		} else if (!usercode.equals(other.usercode))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "WorktaskUserBean [id=" + id + ", subid=" + subid + ", inputSubid=" + inputSubid + ", usercode=" + usercode + ", username=" + username + ", siteid=" + siteid
				+ ", flag=" + flag + "]";
	}
}
