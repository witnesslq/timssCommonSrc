package com.yudean.homepage.vo;

import java.util.Date;

import com.yudean.homepage.bean.WorktaskBean.WorkTaskClass;
import com.yudean.homepage.bean.WorktaskBean.WorkTaskURLType;
import com.yudean.homepage.bean.WorktaskUserBean.WorkTaskUserFlag;

/**
 * 任务查询数据传递VO
 * 
 * @title: {title}
 * @description: {desc}
 * @company: gdyd
 * @className: WorktaskViewObj.java
 * @author: kChen
 * @createDate: 2014-7-10
 * @updateUser: kChen
 * @version: 1.0
 */
public class WorktaskViewObj {
	String id;
	String flowno;
	String name;
	String typename;
	String statusname;
	Date statusdate;
	String createuser;
	String createusername;
	Date createdate;
	String modifyuser;
	String modifyusername;
	Date modifydate;
	String siteid;
	String deptid;
	String deptname;
	WorkTaskClass classtype;
	String url;
	WorkTaskURLType urltype;
	String usercode;
	String username;
	String usercreateuser;
	Date usercreatedate;
	String usermodifyuser;
	Date usermodifydate;
	WorkTaskUserFlag userflag;
	String extcode;
	String curusername;
	String groupid;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFlowno() {
		return flowno;
	}

	public void setFlowno(String flowno) {
		this.flowno = flowno;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTypename() {
		return typename;
	}

	public void setTypename(String typename) {
		this.typename = typename;
	}

	public String getStatusname() {
		return statusname;
	}

	public void setStatusname(String statusname) {
		this.statusname = statusname;
	}

	public Date getStatusdate() {
		return statusdate;
	}

	public void setStatusdate(Date statusdate) {
		this.statusdate = statusdate;
	}

	public String getCreateuser() {
		return createuser;
	}

	public void setCreateuser(String createuser) {
		this.createuser = createuser;
	}

	public String getCreateusername() {
		return createusername;
	}

	public void setCreateusername(String createusername) {
		this.createusername = createusername;
	}

	public Date getCreatedate() {
		return createdate;
	}

	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}

	public String getModifyuser() {
		return modifyuser;
	}

	public void setModifyuser(String modifyuser) {
		this.modifyuser = modifyuser;
	}

	public String getModifyusername() {
		return modifyusername;
	}

	public void setModifyusername(String modifyusername) {
		this.modifyusername = modifyusername;
	}

	public Date getModifydate() {
		return modifydate;
	}

	public void setModifydate(Date modifydate) {
		this.modifydate = modifydate;
	}

	public String getSiteid() {
		return siteid;
	}

	public void setSiteid(String siteid) {
		this.siteid = siteid;
	}

	public String getDeptid() {
		return deptid;
	}

	public void setDeptid(String deptid) {
		this.deptid = deptid;
	}

	public String getDeptname() {
		return deptname;
	}

	public void setDeptname(String deptname) {
		this.deptname = deptname;
	}

	public WorkTaskClass getClasstype() {
		return classtype;
	}

	public void setClasstype(WorkTaskClass classtype) {
		this.classtype = classtype;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public WorkTaskURLType getUrltype() {
		return urltype;
	}

	public void setUrltype(WorkTaskURLType urltype) {
		this.urltype = urltype;
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

	public String getUsercreateuser() {
		return usercreateuser;
	}

	public void setUsercreateuser(String usercreateuser) {
		this.usercreateuser = usercreateuser;
	}

	public Date getUsercreatedate() {
		return usercreatedate;
	}

	public void setUsercreatedate(Date usercreatedate) {
		this.usercreatedate = usercreatedate;
	}

	public String getUsermodifyuser() {
		return usermodifyuser;
	}

	public void setUsermodifyuser(String usermodifyuser) {
		this.usermodifyuser = usermodifyuser;
	}

	public Date getUsermodifydate() {
		return usermodifydate;
	}

	public void setUsermodifydate(Date usermodifydate) {
		this.usermodifydate = usermodifydate;
	}

	public WorkTaskUserFlag getUserflag() {
		return userflag;
	}

	public void setUserflag(WorkTaskUserFlag userflag) {
		this.userflag = userflag;
	}

	public String getCurusername() {
		return curusername;
	}

	public void setCurusername(String curusername) {
		this.curusername = curusername;
	}

	public String getExtcode() {
		return extcode;
	}

	public void setExtcode(String extcode) {
		this.extcode = extcode;
	}

	public String getGroupid() {
		return groupid;
	}

	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}
}
