package com.yudean.homepage.vo;

import java.io.Serializable;

/**
 * 用户活跃度返回的视图对象
 * 
 * @author kchen
 * 
 */
public class SiteActiveInfoVo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1582340626079474834L;

	/**
	 * 返回状态
	 * 
	 * @author kchen
	 * 
	 */
	public enum retStatus {
		ok, err
	}

	private retStatus status;// 状态
	/*
	 * 返回的数据 格式: "status":"ok", "data":{ "series":[
	 * {"name":"ITC","data":[40,66,55,44,67,80,78]},
	 * {"name":"SBS","data":[50,20,15,15,20,23,64]},
	 * {"name":"HYC","data":[23,12,4,6,9,7,2]}],
	 * "xAxisLable":["3/8","3/9","3/10"]}}
	 */
	private SiteActiveInfoDataVo data;

	public retStatus getStatus() {
		return status;
	}

	public void setStatus(retStatus status) {
		this.status = status;
	}

	public SiteActiveInfoDataVo getData() {
		return data;
	}

	public void setData(SiteActiveInfoDataVo data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "SiteActiveInfoVo [status=" + status + ", data=" + data + "]";
	}
}
