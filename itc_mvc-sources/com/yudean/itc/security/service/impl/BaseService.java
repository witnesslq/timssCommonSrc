package com.yudean.itc.security.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import net.sf.json.JSONArray;

import com.yudean.itc.manager.sec.ISecurityMaintenanceManager;

public class BaseService {
	private static Logger logger = Logger.getLogger(BaseService.class);
	public static String trimStrToNull(String s) {
		if (s == null) {
			return null;
		} else {
			s = s.trim();
			if (s.length() == 0) {
				return null;
			} else {
				return s;
			}
		}
	}
	/**
	 * 找到一组用户相关的所有组织机构
	 * @param result1
	 * @param manager
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static HashMap<String,Boolean> getParentOrgs(List<Map> result1,ISecurityMaintenanceManager manager){
		HashMap<String,Boolean> result = new HashMap<String, Boolean>();
		System.out.println("start with size:" + result1.size());
		//直属机构
		for(Map map:result1){
			String orgCode = (String) map.get("ORGCODE");
			result.put(orgCode, true);
			logger.debug("(direct org)put->" + orgCode);
		}
		//直属机构的父节点
		ArrayList<String> parents = new ArrayList<String>();
		for(Map map:result1){
			String parentCode = (String) map.get("PCODE");
			if(!result.containsKey(parentCode) && !parentCode.equals("1")){
				parents.add(parentCode);
				result.put(parentCode, true);
				logger.debug("(direct parent)put->" + parentCode);
			}
			
		}
		while(parents.size()>0){
			List<Map> result2 = manager.selectOrgsParents(parents);
			logger.debug("select parents for " + JSONArray.fromObject(parents).toString() + ",res length=" + result2.size());
			parents.clear();
			for(Map map:result2){
				String parentCode = (String) map.get("PCODE");
				if(!result.containsKey(parentCode) && !parentCode.equals("1")){
					parents.add(parentCode);
					result.put(parentCode, true);
				}
			}
		}
		//TODO:这里为什么会出null的key?
		if(result.containsKey(null)){
			result.remove(null);
		}
		return result;
	}
}
