package com.yudean.mvc.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yudean.itc.bean.enums.EnumType;
import com.yudean.itc.dto.support.AppEnum;

/**
 * Timss枚举变量工具类
 * 
 * @title: {title}
 * @description: {desc}
 * @company: gdyd
 * @className: TimssEnumUtil.java
 * @author: kChen
 * @createDate: 2014-6-25
 * @updateUser: kChen
 * @version: 1.0
 */
public class EnumUtil {
	static final String DbNullFlag = "NaN";
	/**
	 * 无站点信息
	 */
	static Map<String, List<AppEnum>> enumMap = new HashMap<String, List<AppEnum>>();

	/**
	 * 有站点信息
	 */
	static Map<EnumType, List<AppEnum>> enumMapSite = new HashMap<EnumType, List<AppEnum>>();

	static public Map<String, List<AppEnum>> getEnumMap() {
		return enumMap;
	}

	static public List<AppEnum> getEnum(String eCateCode) {
		return enumMap.get(eCateCode);
	}

	static public List<AppEnum> getEnumSite(String eCateCode, String siteId) {
		EnumType type = EnumType.getInstace(eCateCode, siteId);
		return enumMapSite.get(type);
	}
}
