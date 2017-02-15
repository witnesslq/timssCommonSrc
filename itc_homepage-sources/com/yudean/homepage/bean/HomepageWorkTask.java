package com.yudean.homepage.bean;

public class HomepageWorkTask {
	public enum TaskType {
		/**
		 * 草稿
		 */
		Draft,
		/**
		 * 流程实例
		 */
		Process
	}
	private String flow; //流水号
	private String processInstId;//流程实例ID
	private String typeName;//类别名称
	private String name;//任务名称
	private String statusName;//状态名称
	private String url;//点击时的跳转路径
	private TaskType type;// 指定创建的类别{草稿OR实例}
	
	public String getFlow() {
		return flow;
	}
	
	public void setFlow(String flow) {
		this.flow = flow;
	}
	public String getProcessInstId() {
		return processInstId;
	}
	public void setProcessInstId(String processInstId) {
		this.processInstId = processInstId;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStatusName() {
		return statusName;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	@Deprecated
	public TaskType getType() {
		return type;
	}
	@Deprecated
	public void setType(TaskType type) {
		this.type = type;
	}
	
}
