package com.yudean.mvc.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;

import com.yudean.mvc.exception.RuntimeDataNotFoundException;

/**
 * TIMSS上下文解析工具
 * @author kChen
 *
 */
public class ContextUtil {
	final static private String INIT_CLASS_SPLITE_FLAG = ",";//分隔符
	
	final static private String INITBEANS_CONTEXT_PRE = "includeBeans-";//初始化容器的参数名前缀
	final static private String INITBEANS_CONTEXT_SUF = "-config";//初始化容器的参数名后缀
//	final static private String INITBEANS_PRE_PATH = "/config/context/";//制定路径
	
	final static private String AFTER_CONTEXT_BUILD_PRE = "initClassAfterContextBuild-";
	final static private String AFTER_CONTEXT_BUILD_SIF = "-config";

	/**
	 * 解析上下文中的includeBeans包含的内容
	 * @param enums
	 * @return
	 * @throws Exception
	 */
	static public List<String> parasInitClassList(ServletContext context) throws RuntimeDataNotFoundException, RuntimeException{
		TransData _data = new TransData(context);
		_data.parsePrefix = AFTER_CONTEXT_BUILD_PRE;
		_data.parseSuffix = AFTER_CONTEXT_BUILD_SIF;
		return parasRegularConfigText(_data);
	}
	
	/**
	 * 解析上下文中的includeBeans包含的内容
	 * @param enums
	 * @return
	 * @throws Exception
	 */
	static public String[] parasInitBeansList(ServletContext context) throws RuntimeDataNotFoundException, RuntimeException{
		TransData _data = new TransData(context);
		_data.parsePrefix = INITBEANS_CONTEXT_PRE;
		_data.parseSuffix = INITBEANS_CONTEXT_SUF;
		_data.prefix = "";
		List<String> list = parasRegularConfigText(_data);

		String[] beansList = null;
		if(list.size() > 0){
			beansList = new String[list.size()];
			for(int index=0; index < list.size();index++){
				beansList[index] = list.get(index);
			}
		}
		if(null == beansList){
			throw new RuntimeDataNotFoundException("数据不存在");
		}
		return beansList;
	}
	
	/**
	 * 专门用于解析includeBeans-framework-config格式的字符串上下问参数，会将解析后的数据以String[]格式返回。
	 * @param _data
	 * @return
	 * @throws TimssRunDataNotFoundException
	 * @throws Exception
	 */
	static private List<String> parasRegularConfigText(TransData _data) throws RuntimeException{
		Enumeration<String> enums = _data.context.getInitParameterNames();
		List<String> list = new ArrayList<String>();
		while(enums.hasMoreElements()){
			String initParamName = enums.nextElement();
			if(initParamName.startsWith(_data.parsePrefix) && initParamName.endsWith(_data.parseSuffix)){
				String initParamValue = ParseStrUtil.replaceStrSymbol(_data.context.getInitParameter(initParamName).trim().replaceAll(" ", ""));
				StringTokenizer token = new StringTokenizer(initParamValue, INIT_CLASS_SPLITE_FLAG);
				while(token.hasMoreElements()){
					String OneinitParams = (String)token.nextElement();
					list.add(_data.prefix + OneinitParams + _data.sufix);
				}
			}
		}
		return list;
	}
}

class TransData{
	ServletContext context = null;
	String parsePrefix = "";
	String parseSuffix = ""; 
	String prefix = "";
	String sufix = "";
	
	TransData(ServletContext context){
		this.context = context;
	}
}
