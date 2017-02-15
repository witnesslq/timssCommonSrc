package com.yudean.homepage.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 前端用户活跃度x轴数据
 * 
 * @author kchen
 * 
 */
public class SiteActiveInfoSeriesVo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5451639066342860251L;
	private String name;// 名称
	private List<Double> data;// 数据项
	private Map<String, Double> compdata;
	
	public SiteActiveInfoSeriesVo() {
		data = new ArrayList<Double>();
		compdata = new HashMap<String, Double>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Double> getData() {
		return data;
	}

	public void setData(List<Double> data) {
		this.data = data;
	}

	public void addData(String day, Double count) {
		this.compdata.put(day, count);
	}

	public void validata(List<String> axisList){
		for(String axis : axisList){
			Double axisData = compdata.get(axis);
			if(null != axisData){
				data.add(axisData);
			}else{
				data.add(0D);
			}
		}
	}
	
	@Override
	public String toString() {
		return "SiteActiveInfoSeriesVo [name=" + name + ", data=" + data + "]";
	}
}
