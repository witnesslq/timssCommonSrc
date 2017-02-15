package com.yudean.mvc.util;

import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import com.yudean.mvc.util.jsonprocess.JsonDateValueProcessor;
import com.yudean.mvc.util.jsonprocess.JsonStatusCodeValueProcessor;
import com.yudean.mvc.util.jsonprocess.JsonStringValueProcessor;
import com.yudean.itc.code.StatusCode;
import com.yudean.itc.dto.Page;

public class MvcJsonUtil {
	/**
	 * java.utile.date和java.sql.timestamp日期格式化
	 */
	private static String datePattern = "yyyy-MM-dd HH:mm";
	/**
	 * java.sql.date日期格式化
	 */
	private static String sqlDatePattern = "yyyy-MM-dd";
	
	/**
	 * 将List转换为EasyUI中需要的数据，用于展示datagrid
	 * @param list 根据page查到的结果集
	 * @param pageSearchParam 基本的查询参数
	 * @param datePattern 日期格式化的样式
	 * @return
	 */
	public static JSONObject DatagridJsonFromList(Page<?> page){
		return DatagridJsonFromList(page, datePattern);
	}
	
	/**
	 * 将List转换为EasyUI中需要的数据，用于展示datagrid
	 * @param list 根据page查到的结果集
	 * @param pageSearchParam 基本的查询参数
	 * @param datePattern 日期格式化的样式
	 * @return
	 */
	public static JSONObject DatagridJsonFromList(Page<?> page,String datePattern){
		JSONObject result = new JSONObject();
		result.put("total", page.getTotalRecord());
		result.put("totalPage", page.getPageSize());
		result.put("rows",JSONArrayFromList(page.getResults(), datePattern));
		return result;
	}
	
	/**
	 * 将简单的HashMap或bean转换为JSONObject对象 ，其中Date类型会被格式化成yyyy-MM-dd HH:mm样式
	 * @param map
	 * @return
	 */
	public static JSONObject JSONObjectFromMap(Object obj){
		return JSONObjectFromMap(obj,datePattern);
	}
	/**
	 * 将简单的HashMap转换为JSONObject对象 ，其中Date类型会被格式化成yyyy-MM-dd HH:mm样式
	 * @param map
	 * @return
	 */
	public static JSONObject JSONObjectFromMap(Object obj,String datePattern){
		JsonConfig jsonConfig = new JsonConfig();  
		jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(datePattern));
		jsonConfig.registerJsonValueProcessor(java.sql.Date.class, new JsonDateValueProcessor(sqlDatePattern));
		jsonConfig.registerJsonValueProcessor(java.sql.Timestamp.class, new JsonDateValueProcessor(datePattern));  
		return JSONObject.fromObject(obj,jsonConfig);
	}
	
	/**
	 * 将List转换为JSONArray对象 ，其中Date类型会被格式化成yyyy-MM-dd HH:mm样式
	 * @param list
	 * @param datePattern日期格式化的字符串
	 * @return
	 */
	public static JSONArray JSONArrayFromList(List<?> list){
		return JSONArrayFromList(list, datePattern);
	}
	
	/**
	 * 将简单的List转换为JSONArray对象 ，其中Date类型会被格式化成yyyy-MM-dd HH:mm样式
	 * @param list
	 * @param datePattern日期格式化的字符串
	 * @return
	 */
	public static JSONArray JSONArrayFromList(List<?> list,String datePattern){
		JsonConfig jsonConfig = new JsonConfig();  
		jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(datePattern));
		jsonConfig.registerJsonValueProcessor(java.sql.Date.class, new JsonDateValueProcessor(sqlDatePattern));
		jsonConfig.registerJsonValueProcessor(java.sql.Timestamp.class, new JsonDateValueProcessor(datePattern));
		jsonConfig.registerJsonValueProcessor(String.class, new JsonStringValueProcessor());
		jsonConfig.registerJsonValueProcessor(StatusCode.class, new JsonStatusCodeValueProcessor());
		return JSONArray.fromObject(list,jsonConfig);
	}
}
