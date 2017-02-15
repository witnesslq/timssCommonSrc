package com.yudean.sysconf.vo;

public class QuartzTask {
	/**
	 * 启动，暂停，重启，删除，修改，执行
	 * */
	public enum OPERATE {
		START,
		PAUSE,
		RESTART,
		DELETE,
		UPDATE,
		EXECUTE 
	}
	
	private String id;
	private String createTimeStr;
	private String endTimeStr;
	private String group;
	private String jobClassStr;
	private String jobName;
	private String repeatCount;
	private String repeatInterval;
	private String retryCount;
	private String startDelay;
	private String startTimeStr;
	private OPERATE status;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCreateTimeStr() {
		return createTimeStr;
	}
	public void setCreateTimeStr(String createTimeStr) {
		this.createTimeStr = createTimeStr;
	}
	public String getEndTimeStr() {
		return endTimeStr;
	}
	public void setEndTimeStr(String endTimeStr) {
		this.endTimeStr = endTimeStr;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getJobClassStr() {
		return jobClassStr;
	}
	public void setJobClassStr(String jobClassStr) {
		this.jobClassStr = jobClassStr;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getRepeatCount() {
		return repeatCount;
	}
	public void setRepeatCount(String repeatCount) {
		this.repeatCount = repeatCount;
	}
	public String getRepeatInterval() {
		return repeatInterval;
	}
	public void setRepeatInterval(String repeatInterval) {
		this.repeatInterval = repeatInterval;
	}
	public String getRetryCount() {
		return retryCount;
	}
	public void setRetryCount(String retryCount) {
		this.retryCount = retryCount;
	}
	public String getStartDelay() {
		return startDelay;
	}
	public void setStartDelay(String startDelay) {
		this.startDelay = startDelay;
	}
	public String getStartTimeStr() {
		return startTimeStr;
	}
	public void setStartTimeStr(String startTimeStr) {
		this.startTimeStr = startTimeStr;
	}
	public OPERATE getStatus() {
		return status;
	}
	public void setStatus(OPERATE status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "QuartzTask [id=" + id + ", createTimeStr=" + createTimeStr + ", endTimeStr=" + endTimeStr + ", group="
				+ group + ", jobClassStr=" + jobClassStr + ", jobName=" + jobName + ", repeatCount=" + repeatCount
				+ ", repeatInterval=" + repeatInterval + ", retryCount=" + retryCount + ", startDelay=" + startDelay
				+ ", startTimeStr=" + startTimeStr + ", status=" + status + "]";
	}
}
