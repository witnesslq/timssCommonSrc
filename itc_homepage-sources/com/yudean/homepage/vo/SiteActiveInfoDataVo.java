package com.yudean.homepage.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SiteActiveInfoDataVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7493897340416529927L;

	/*
	 * 数据 格式： [ {"name":"ITC","data":[40,66,55,44,67,80,78]},
	 * {"name":"SBS","data":[50,20,15,15,20,23,64]},
	 * {"name":"HYC","data":[23,12,4,6,9,7,2]}]
	 */
	private List<SiteActiveInfoSeriesVo> series;//加权计算后的数据

	/*
	 * 横坐标显示标题 xAxisLable":["3/8","3/9","3/10"]
	 */
	private List<String> xlable;// x轴显示内容

	private Map<String, Map<String, Integer>> xrData;//真实数据 索引<站点，数据列表索引>
	
	public SiteActiveInfoDataVo() {
		series = new ArrayList<SiteActiveInfoSeriesVo>();
		xlable = new ArrayList<String>();
		xrData = new HashMap<String, Map<String, Integer>>();
	}

	public List<SiteActiveInfoSeriesVo> getSeries() {
		return series;
	}

	public void setSeries(List<SiteActiveInfoSeriesVo> series) {
		this.series = series;
	}

	public List<String> getXlable() {
		return xlable;
	}

	public void setXlable(List<String> xlable) {
		this.xlable = xlable;
	}
	
	public Map<String, Map<String, Integer>> getXrData() {
		return xrData;
	}

	public void setXrData(Map<String, Map<String, Integer>> xrData) {
		this.xrData = xrData;
	}

	/**
	 * 向列表中增加真实数据
	 * @param name
	 * @param key
	 * @param value
	 */
	public void addXrData(String name, String key, Integer value){
		Map<String, Integer> rMap = this.xrData.get(name);
		if(null == rMap){
			rMap = new HashMap<String, Integer>();
			this.xrData.put(name, rMap);
		}
		rMap.put(key, value);
	}
	/**
	 * 增加一个serie
	 * 
	 * @param seriesVo
	 */
	public void addSerie(SiteActiveInfoSeriesVo seriesVo) {
		series.add(seriesVo);
	}

	/**
	 * 判断十分包含这个serie
	 * 
	 * @param name
	 * @return
	 */
	public boolean containsSeries(String name) {
		for (SiteActiveInfoSeriesVo serie : series) {
			if (serie.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取serie
	 * 
	 * @param name
	 * @return
	 */
	public SiteActiveInfoSeriesVo retSerie(String name) {
		for (SiteActiveInfoSeriesVo serie : series) {
			if (serie.getName().equals(name)) {
				return serie;
			}
		}
		return null;
	}

	/**
	 * 添加横坐标数据
	 * 
	 * @param xlable
	 */
	public void addXlable(String xlable) {
		if (!this.xlable.contains(xlable)) {
			this.xlable.add(xlable);
		}
	}

	public int sizeOfXlable() {
		return this.xlable.size();
	}

	public void validata(List<String> siteList) {
		Object[] xlableTmp = null;
		xlableTmp = this.xlable.toArray();
		this.xlable = new ArrayList<String>();
		for (int index = xlableTmp.length - 1; -1 < index; index--) {
			this.xlable.add((String) xlableTmp[index]);
		}
		for (String site : siteList) {
			if (this.containsSeries(site)) {
				SiteActiveInfoSeriesVo serieVo = this.retSerie(site);
				serieVo.validata(this.xlable);
			} else {
				SiteActiveInfoSeriesVo serieVo = new SiteActiveInfoSeriesVo();
				serieVo.setName(site);
				List<Double> list = new ArrayList<Double>();
				for (@SuppressWarnings("unused")
				String x : this.xlable) {
					list.add(0D);
				}
				serieVo.setData(list);
				this.addSerie(serieVo);
			}
		}
		for (int index = 0; index < this.xlable.size(); index++) {
			String xlable = this.xlable.get(index);
			this.xlable.set(index, xlable.substring(4));
		}
	}

	@Override
	public String toString() {
		return "SiteActiveInfoDataVo [series=" + series + ", xlable=" + xlable + "]";
	}
}
