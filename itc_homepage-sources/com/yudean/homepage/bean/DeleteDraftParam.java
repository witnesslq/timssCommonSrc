package com.yudean.homepage.bean;

public class DeleteDraftParam {
	/**
	 * 流水号
	 */
	private String flowId;
	/**
	 * 流程实例ID
	 */
	private String processInsId;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 站点
	 */
	private String siteid;
	
	public String getFlowId() {
		return flowId;
	}

	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}

	public String getProcessInsId() {
		return processInsId;
	}

	public void setProcessInsId(String processInsId) {
		this.processInsId = processInsId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSiteid() {
		return siteid;
	}

	public void setSiteid(String siteid) {
		this.siteid = siteid;
	}
}
