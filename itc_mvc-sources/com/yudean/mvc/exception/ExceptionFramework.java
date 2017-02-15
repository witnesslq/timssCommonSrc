package com.yudean.mvc.exception;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.yudean.itc.bean.environment.RuntimeEnvironmentData;
import com.yudean.itc.bean.exception.ExceptionData;
import com.yudean.itc.util.ResourceLoader;
import com.yudean.mvc.interfaces.InitClassAfterContextBuildInterface;
import com.yudean.mvc.util.JarFileUtil;

import static com.yudean.mvc.util.ViewUtil.AjaxDataName;
import static com.yudean.mvc.util.ViewUtil.AjaxFlagName;
import static com.yudean.mvc.util.ViewUtil.AjaxSucFlag;

/**
 * TIMSS容错框架处理类
 * 
 * @author kChen
 * 
 */
@Component("timss_framework_timssExceptionFramework")
public class ExceptionFramework implements InitClassAfterContextBuildInterface {
	static private Log logger = LogFactory.getLog(ExceptionFramework.class);
	static private Map<Class<?>, AopExceptionData> exceptionMap = null;
	static private AopExceptionData defaultExcepData = null;

	@Autowired
	RuntimeEnvironmentData env;

	@Override
	/**
	 * 初始化异常容器
	 */
	public void initClass(ApplicationContext context) throws Exception {
		// TODO Auto-generated method stub
		AjaxFlagName = env.getAjaxFlagName();
		AjaxDataName = env.getAjaxDataName();
		AjaxSucFlag = env.getAjaxSucFlag();

		if (null == exceptionMap)
			exceptionMap = new HashMap<Class<?>, AopExceptionData>();
		final String curJarName = JarFileUtil.getCurJarName(this);// 获取主框架jar包名称
		List<String> list = new ArrayList<String>();
		list.add(curJarName);// 定义加载顺序

		Set<Resource> resSet = ResourceLoader.getResources(env.getExceptionConfigPath());

		// ResourcePatternResolver resourceLoader = new
		// PathMatchingResourcePatternResolver();
		// Resource[] source =
		// resourceLoader.getResources("classpath*:/config/exception/exception.xml");

		// List<InputStream> inList =
		// JarFileUtil.jarFileInputStream(env.getExceptionConfigPath(), list,
		// this);//获取jar包中指定文件的输入流

		SAXReader reader = new SAXReader();// 从配置文件解析数据
		for (Resource resource : resSet) {
			InputStream in = resource.getInputStream();
			Document doc = reader.read(in);
			Element root = doc.getRootElement();
			// 获取默认信息
			Element defaultelement = root.element("default");
			if (null == defaultExcepData && null != defaultelement) {
				String page = defaultelement.elementTextTrim("page");
				Element dataEle = defaultelement.element("data");
				defaultExcepData = new AopExceptionData();
				defaultExcepData.data = new HashMap<String, String>();
				defaultExcepData.page = page;
				defaultExcepData.data.put(AjaxFlagName, dataEle.elementTextTrim(AjaxFlagName));
				defaultExcepData.data.put(AjaxDataName, dataEle.elementTextTrim(AjaxDataName));
			}
			@SuppressWarnings("rawtypes")
			// 获取异常信息
			Iterator iter = root.elementIterator("exception");
			while (iter.hasNext()) {
				Element element = (Element) iter.next();
				Attribute attribute = null;
				attribute = element.attribute("name");
				String name = attribute.getStringValue();
				attribute = element.attribute("class");
				String clazzName = attribute.getStringValue();
				Class<?> clazz = null;
				try {
					clazz = Class.forName(clazzName);
				} catch (ClassNotFoundException e) {
					logger.error("类" + clazzName + "不存在没有初始化对应异常返回类型.");
					continue;
				}
				String page = element.elementTextTrim("page");
				Element dataele = element.element("data");
				String flag = dataele.elementTextTrim(AjaxFlagName);
				String msg = dataele.elementTextTrim(AjaxDataName);
				AopExceptionData _data = new AopExceptionData();
				_data.name = name;
				_data.clazz = clazz;
				_data.page = page;
				_data.data = new HashMap<String, String>();
				_data.data.put(AjaxFlagName, flag);
				_data.data.put(AjaxDataName, msg);

				AopExceptionData existsData = exceptionMap.get(clazz);
				if (null == existsData)
					exceptionMap.put(clazz, _data);
			}
			in.close();// 关闭IO流
		}
	}

	/**
	 * 异常处理
	 * 
	 * @param ex
	 * @return
	 */
	public ExceptionData TimssRunException(Exception ex) {// 执行异常
		ExceptionData exceptionData = new ExceptionData(defaultExcepData.page, defaultExcepData.data);
		try {
			Class<?> clazz = ex.getClass();
			AopExceptionData _data = exceptionMap.get(clazz);
			if (null != _data) {
				exceptionData.setPage(_data.page);
				exceptionData.setData(_data.data);
				exceptionData.setErrInfo(ex.getMessage());
			}
		} catch (Exception e) {
			logger.error("容错框架处理异常，采用默认参数返回", e);
		}
		return exceptionData;
	}
}

/**
 * 容错框架内部数据对象
 * 
 * @author kChen
 * 
 */
class AopExceptionData {
	String name;
	Class<?> clazz;
	String page;
	Map<String, String> data;
}