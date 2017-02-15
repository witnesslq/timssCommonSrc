package com.yudean.mvc.configs;

import java.util.HashSet;
import java.util.Set;

import com.yudean.itc.code.LoadOnAuditJS;

/**
 * MVC前端相关配置
 * 
 * @company: gdyd
 * @className: MvcWebConfig.java
 * @author: kChen
 * @createDate: 2014-9-11
 * @updateUser: kChen
 * @version: 1.0
 */
public class MvcWebConfig {
	/**
	 * 标记是否加载页面检查相关的js itc_timssaudit.js、itc_timssaudit_rules.js
	 */
	static public LoadOnAuditJS loadOnAuditJS;

	/**
	 * 未登陆允许直接访问的路径
	 */
	static public Set<String> accessPathListWithoutLog;

	/**
	 * 每次启动的版本号
	 */
	static public String curRunVersion;

	/**
	 * 系统全路径
	 */
	static public String serverBasePath = null;

	/**
	 * 系统根路径
	 */
	static public String serverRootPath = null;

	/**
	 * 报表根路径
	 */
	static public String birtRootPath = null;

	/**
	 * 报表服务上下文路径
	 */
	static public String birtContextPath = null;

	/**
	 * 报表全路径
	 */
	static public String birtServicePath = null;

	static {
		accessPathListWithoutLog = new HashSet<String>();
		accessPathListWithoutLog.add("EipInterface");
		accessPathListWithoutLog.add("login?method=index");
	}
}
