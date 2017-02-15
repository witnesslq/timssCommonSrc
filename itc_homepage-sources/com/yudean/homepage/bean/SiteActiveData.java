package com.yudean.homepage.bean;

public class SiteActiveData {
	private int sitecount;
	private String siteId;
	private String cdate;

	public int getSitecount() {
		return sitecount;
	}

	public void setSitecount(int sitecount) {
		this.sitecount = sitecount;
	}

	public String getSiteId() {
		return siteId;
	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	public String getCdate() {
		return cdate;
	}

	public void setCdate(String cdate) {
		this.cdate = cdate;
	}

	@Override
	public String toString() {
		return "SiteActiveDate [sitecount=" + sitecount + ", siteId=" + siteId + ", cdate=" + cdate + "]";
	}
}
