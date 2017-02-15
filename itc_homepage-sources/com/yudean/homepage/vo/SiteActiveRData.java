package com.yudean.homepage.vo;

public class SiteActiveRData {
	private String xAixs;
	private Double data;
	public String getxAixs() {
		return xAixs;
	}
	public void setxAixs(String xAixs) {
		this.xAixs = xAixs;
	}
	public Double getData() {
		return data;
	}
	public void setData(Double data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "SiteActiveRData [xAixs=" + xAixs + ", data=" + data + "]";
	}
}
