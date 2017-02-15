package com.yudean.mvc.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 解析各种需用字符串工具
 * @author kChen
 *
 */
public class ParseStrUtil {
	static public String SERVLETPATH_SEPARATOR = "/";
	static public String SERVLETPATH_PARAMTOR = "?";
	
	
	/**
	 * 从ServletPath中获取模块名称，必须按照规范，每个请求都之前都包含模块名称
	 * @param sServletPath
	 * @return
	 */
	static public String parseModuleName(String sServletPath) throws RuntimeException{
		sServletPath = sServletPath.startsWith(SERVLETPATH_SEPARATOR)? sServletPath.substring(1) : sServletPath;
		int iPos = sServletPath.indexOf(SERVLETPATH_SEPARATOR);
		sServletPath = sServletPath.substring(0, iPos);
		return sServletPath;
	}
	
	/**
	 * 去除字符串中的空格、回车、换行符、制表符
	 * @param sServletPath
	 * @return
	 * @throws Exception
	 */
	static public String replaceStrSymbol(String str) throws RuntimeException{
		String dest = ""; 
		if (str!=null) { 
			Pattern p = Pattern.compile("\\s*|\t|\r|\n"); 
			Matcher m = p.matcher(str); 
			dest = m.replaceAll(""); 
		} 
		return dest; 
	}
}
