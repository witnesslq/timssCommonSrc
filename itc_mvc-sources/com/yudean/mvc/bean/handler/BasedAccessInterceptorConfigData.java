package com.yudean.mvc.bean.handler;

public class BasedAccessInterceptorConfigData {
	String sufix;
	String bindPrefix;
	String bindPageClassFlag;
	String bindTotlaPageFlag;
	String bindRowsPageFlag;
	String noBranchPrefix;
	String javaBeanPrefix;
	String[] servletPrefix;
	public void setSufix(String sufix){
		this.sufix = sufix;
	}
	
	/**
	 * 获取过滤后缀，包含这些后缀的view将被视为页面请求
	 * @return
	 */
	public String getSufix(){
		return this.sufix;
	}
	
	public void setBindPrefix(String bindPrefix){
		this.bindPrefix = bindPrefix;
	}
	
	/**
	 * 绑定前缀，数据转换的判定符
	 */
	public String getBindPrefix(){
		return this.bindPrefix;
	}
	
	public void setBindPageClassFlag(String bindPageClassFlag){
		this.bindPageClassFlag = bindPageClassFlag;
	}
	
	/**
	 * 分页类名称
	 * @return
	 */
	public String getBindPageClassFlag(){
		return this.bindPageClassFlag;
	}
	
	public void setBindTotlaPageFlag(String bindTotlaPageFlag){
		this.bindTotlaPageFlag = bindTotlaPageFlag;
	}
	
	/**
	 * 分页类总页数标记
	 * @return
	 */
	public String getBindTotlaPageFlag(){
		return this.bindTotlaPageFlag;
	}
	
	public void setBindRowsPageFlag(String bindRowsPageFlag){
		this.bindRowsPageFlag = bindRowsPageFlag;
	}
	
	/**
	 * 分页类总页数标记
	 * @return
	 */
	public String getBindRowsPageFlag(){
		return this.bindRowsPageFlag;
	}
	
	public void setNoBranchPrefix(String noBranchPrefix){
		this.noBranchPrefix = noBranchPrefix;
	}
	
	/**
	 * 无分支前缀标记
	 * @return
	 */
	public String getNoBranchPrefix(){
		return this.noBranchPrefix;
	}
	
	public void setjavaBeanPrefix(String javaBeanPrefix){
		this.javaBeanPrefix = javaBeanPrefix;
	}
	
	/**
	 * javaBean前缀标记
	 * @return
	 */
	public String getjavaBeanPrefix(){
		return this.javaBeanPrefix;
	}

	/**
	 * servlet前缀标记
	 * @return
	 */
	public String[] getServletPrefix() {
		return servletPrefix;
	}

	public void setServletPrefix(String[] servletPrefix) {
		this.servletPrefix = servletPrefix;
	}
	
	
}
