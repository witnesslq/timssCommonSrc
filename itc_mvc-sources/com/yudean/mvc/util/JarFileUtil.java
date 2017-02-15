package com.yudean.mvc.util;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.yudean.mvc.context.MvcContext;

/**
 *  jar包文件操作工具
 * 
 * @author kChen
 * 
 */
public class JarFileUtil {
	static private String JARLIB_PATH = "/WEB-INF/lib/";// 工程jar包放置位置 servlet定义
	static private String JARLIB_TIMSS_PREFIX = "timss-,core-";// TIMSS包前缀
	static private String JARLIB_TIMSS_SUFFIX = ".jar";// TIMSS包后缀
	static private String JARLIB_TIMSS_SPLITE = "\\";// TIMSS包分隔符
	static private String JARLIB_TIMSS_DOMAIN_PREFIX = "file:/";// 探查包路径后的返回值
	static private String JARLIB_TIMSS_DOMAIN_SPLITE = "/";// 包路径后的分隔符
	static private String CLASS_TIMSS_CURPOSITION = "CUR_POSITION_NOTJAR";//标记为当前工程的相对路径，非jar包路径
	static private String CLASS_TIMSS_CLASSSUFIX = ".class";
	/**
	 * 获取当前所在的jar包名称
	 * 
	 * @param obj
	 *            包中的Class对象(静态)，或者就是当前的this（实例）
	 * @return
	 */
	static public String getCurJarName(Object obj) {
		String jarName = getCurJarPath(obj);
		int pos = jarName.lastIndexOf(JARLIB_TIMSS_DOMAIN_SPLITE);
		jarName = jarName.substring(pos + 1);
		return jarName;
	}

	/**
	 * 获取当前所在jar包的绝对路径
	 * 
	 * @param obj
	 *            包中的Class对象(静态)，或者就是当前的this（实例）
	 * @return 异常则返回null
	 */
	static public String getCurJarPath(Object obj) {
		String ret = null;
		try {
			Class<?> clazz = getLoadClass(obj);
			URL url = clazz.getProtectionDomain().getCodeSource().getLocation();// 获取所在jar包的绝对路径
			String jarPath = url.toString();
			ret = jarPath.replaceAll(JARLIB_TIMSS_DOMAIN_PREFIX, "");
		} catch (Exception e) {
			LogUtil.error("获取jar路径异常", e);
		}
		return ret;
	};
	
	/**
	 * 获取当期包中的资源文件（CLASS相对路径)
	 * 
	 * @param jarName
	 * @param path
	 * @return 资源部存在则返回null
	 */
	static public InputStream getJarSourceFileInputStreanm(Object obj, String path) {
		InputStream in = null;
		try {
			in = getLoadClassResourceStream(path, obj);
		} catch (Exception e) {
			LogUtil.error("打开文件异常", e);
		}
		return in;
	}
	
	/**
	 * 获取TIMSSjar包
	 * 
	 * @return 返回所包含TIMSS包绝对路径
	 */
	static public List<String> jarFilePath() {
		List<String> list = new ArrayList<String>();
		try {
			String sLibPath = MvcContext.getServletContext().getRealPath(JARLIB_PATH);
			File file = new File(sLibPath);
			String[] jarLibList = file.list();
			for (int index = 0; index < jarLibList.length; index++) {
				final String jarName = jarLibList[index].toLowerCase();
				if (jarName.startsWith(JARLIB_TIMSS_PREFIX) && jarName.endsWith(JARLIB_TIMSS_SUFFIX)) {
					list.add(sLibPath + JARLIB_TIMSS_SPLITE + jarName);
				}
			}
			if(MvcContext.getLocation())
				list.add(CLASS_TIMSS_CURPOSITION);//标记当前工程路径的也要获取
		} catch (Exception e) {
			LogUtil.error("获取所有timss包方法TimssjarFilePath异常", e);
		}
		return list;
	}

	/**
	 * 获取配置jar包，返回所包含配置包绝对路径
	 * 
	 * @param order 排序匹配，系统会根据order中指定的名称的顺序放回列表，没有出现在order中的名称，会根据读取顺序自然排序
	 * @return
	 */
	static public List<String> jarFilePath(List<String> order) {
		List<String> list = new ArrayList<String>();
		Map<String, String> map = new HashMap<String, String>();
		try {
			String sLibPath = MvcContext.getServletContext().getRealPath(JARLIB_PATH);
			LogUtil.info("Lib目录：" + sLibPath);
			File file = new File(sLibPath);
			String[] jarLibList = file.list();
			for (int index = 0; index < jarLibList.length; index++) {
				final String jarName = jarLibList[index].toLowerCase();
				if (jarName.endsWith(JARLIB_TIMSS_SUFFIX)) {
					map.put(jarName, sLibPath + JARLIB_TIMSS_SPLITE + jarName);
				}
			}
			for (int index = 0; index < order.size(); index++) {
				final String jarName = order.get(index);
				final String jarPath = map.remove(jarName);
				if(null != jarPath)
					list.add(jarPath);
				else if(null == jarPath && jarName.endsWith(CLASS_TIMSS_CLASSSUFIX) && MvcContext.getLocation()){
					list.add(CLASS_TIMSS_CURPOSITION);
				}
			}
			Set<Entry<String, String>> set = map.entrySet();
			Iterator<Entry<String, String>> iter = set.iterator();
			while (iter.hasNext()) {
				Entry<String, String> entry = iter.next();
				final String jarPath = entry.getValue();
				list.add(jarPath);
			}
			if(!list.contains(CLASS_TIMSS_CURPOSITION) && MvcContext.getLocation()){
				list.add(CLASS_TIMSS_CURPOSITION);
			}
		} catch (Exception e) {
			LogUtil.error("获取所有timss包方法TimssjarFilePath异常", e);
		}
		return list;
	}
	
	/**
	 * 获取timssjar包的所有定位文件的资源流，处理完毕以后必须调用close方法关闭。
	 * 
	 * @param path 要获取资源的路径，包含jar包位置
	 * @return 资源不存在则返回size() == 0;
	 */
	static public List<InputStream> jarFileInputStream(String path) {
		List<InputStream> list = new ArrayList<InputStream>();
		try {
			List<String> jarPathList = jarFilePath();
			for (int index = 0; index < jarPathList.size(); index++) {
				String jarPath = jarPathList.get(index);
				JarFile jarFile = new JarFile(jarPath);
				JarEntry entrysjar = jarFile.getJarEntry(path);
				InputStream in = jarFile.getInputStream(entrysjar);
				list.add(in);
			}
		} catch (Exception e) {
			LogUtil.error("获取所有包指定资源方法jarFileInputStream异常", e);
		}
		return list;
	}

	/**
	 * 获取timssjar包的所有定位文件的资源流，处理完毕以后必须调用close方法关闭。
	 * 
	 * @param path
	 * @param order
	 *            ,排序匹配，系统会根据order中指定的名称的顺序放回列表，没有出现在order中的名称，会根据读取顺序自然排序
	 * @return 资源不存在则返回size() == 0;
	 */
	static public List<InputStream> jarFileInputStream(String path, List<String> order, Object obj) {
		List<InputStream> list = new ArrayList<InputStream>();
		LogUtil.info("当前jar包获取文件路径：" + path);
		try {
			List<String> jarPathList = jarFilePath(order);
			for (int index = 0; index < jarPathList.size(); index++) {
				String jarPath = jarPathList.get(index);
				InputStream in = null;
				if(CLASS_TIMSS_CURPOSITION.equals(jarPath)){
					in = getLoadClassResourceStream(path, obj);
				}else{
					JarFile jarFile = new JarFile(jarPath);
					JarEntry entrysjar = jarFile.getJarEntry(path);
					if (null != entrysjar) {
						in = jarFile.getInputStream(entrysjar);
					}
				}
				if (null != in)
					list.add(in);
			}
		} catch (Exception e) {
			LogUtil.error("获取所有 包指定资源方法jarFileInputStream异常", e);
		}
		return list;
	}
	
	static public InputStream getLoadClassResourceStream(String path, Object obj) throws Exception{
		return getLoadClass(obj).getClassLoader().getResourceAsStream(path);
	}
	
	static public Class<?> getLoadClass(Object obj) throws Exception{
		Class<?> clazz = null;
		if (obj instanceof Class<?>) {
			clazz = (Class<?>) obj;
		} else {
			clazz = obj.getClass();
		}
		return clazz;
	}
}
