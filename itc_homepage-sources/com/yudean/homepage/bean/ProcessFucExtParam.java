package com.yudean.homepage.bean;

import com.yudean.itc.dto.sec.SecProcRoute.VisibleType;

/**
 * 传递的homepageService.Process()的扩展参数
 * 
 * @author kchen
 * 
 */
public class ProcessFucExtParam {
	private VisibleType visibleType;
	// 是否是子流程
	private boolean hasSubProcess = false;
	// 父流程对应业务id
	private String parentBusinessId;
	// 标记流程扭转时，用户的站点信息是否去路由表获取
	private boolean siteGetRoute = false;

	public boolean getHasSubProcess() {
		return hasSubProcess;
	}

	public void setHasSubProcess(boolean hasSubProcess) {
		this.hasSubProcess = hasSubProcess;
	}

	public String getParentBusinessId() {
		return parentBusinessId;
	}

	public void setParentBusinessId(String parentBusinessId) {
		this.parentBusinessId = parentBusinessId;
	}

	public VisibleType getVisibleType() {
		return visibleType;
	}

	public void setVisibleType(VisibleType visibleType) {
		this.visibleType = visibleType;
	}

	public boolean isSiteGetRoute() {
		return siteGetRoute;
	}

	public void setSiteGetRoute(boolean siteGetRoute) {
		this.siteGetRoute = siteGetRoute;
	}
}
