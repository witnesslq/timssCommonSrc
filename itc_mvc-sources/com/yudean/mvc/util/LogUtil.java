package com.yudean.mvc.util;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.yudean.itc.util.ApplicationConfig;
import com.yudean.mvc.bean.logstash.Logstash;

/**
 * TIMSS 日志工具，目前采用的4个推荐级别，每个级别的日志分别输出到不同文件夹中.
 * 
 * @author kChen
 * 
 */
public class LogUtil {
    private static Log log = LogFactory.getLog(LogUtil.class);

	static private enum modeType {
		debug, info, warn, error;// 日志级别
	}

	/**
	 * 调试日志
	 * 
	 * @param info
	 * @param clazz
	 */
	static public void debug(Object info) {
		writeLog(info, null, modeType.debug);
	}

	/**
	 * 调试日志
	 * 
	 * @param info
	 * @param clazz
	 * @param ex
	 */
	static public void debug(Object info, Class<?> clazz, Throwable ex) {
		writeLog(info, ex, modeType.debug);
	}

	/**
	 * 信息日志
	 * 
	 * @param info
	 * @param clazz
	 */
	static public void info(Object info) {
		if(info instanceof Logstash){
			writeStashLog((Logstash)info, modeType.info, null);
		}
		else{
			writeLog(info, null, modeType.info);
		}
	}

	/**
	 * 信息日志
	 * 
	 * @param info
	 * @param clazz
	 */
	static public void info(Object info, Throwable ex) {
		if(info instanceof Logstash){
			writeStashLog((Logstash)info, modeType.info, ex);
		}
		else{
			writeLog(info, ex, modeType.info);
		}
	}

	/**
	 * 警告日志
	 * 
	 * @param info
	 * @param clazz
	 */
	static public void warn(Object info) {
		writeLog(info, null, modeType.warn);
	}

	/**
	 * 警告日志
	 * 
	 * @param info
	 * @param clazz
	 */
	static public void warn(Object info, Throwable ex) {
		writeLog(info, ex, modeType.warn);
	}

	/**
	 * 错误异常日志
	 * 
	 * @param info
	 * @param clazz
	 */
	static public void error(Object info) {
		writeLog(info, null, modeType.error);
	}

	/**
	 * 错误异常日志
	 * 
	 * @param info
	 * @param clazz
	 */
	static public void error(Object info, Throwable ex) {
		writeLog(info, ex, modeType.error);
	}

	static private void writeLog(Object info, Throwable ex, modeType mode) {
		try {
			switch (mode) {
			case debug: {
				if (log.isDebugEnabled()) {
					if (null == ex)
						log.debug(info);
					else
						log.debug(info, ex);
					break;
				}
			}
			case info: {
				if (log.isInfoEnabled()) {
					if (null == ex)
						log.info(info);
					else
						log.info(info, ex);
					break;
				}
			}
			case warn: {
				if (null == ex)
					log.warn(info);
				else
					log.warn(info, ex);
				break;
			}
			case error: {
				if (null == ex)
					log.error(info);
				else
					log.error(info, ex);
				break;
			}
			}
		} catch (Exception e) {
			log.error("日志工具异常", e);
		}
	}

	// static private Log getClassLog(Class<?> clazz) throws Exception {
	// Log logger = logClassMap.get(clazz);
	// if (null == logger) {
	// logger = LogFactory.getLog(clazz);
	// logClassMap.put(clazz, logger);
	// }
	// return logger;
	// }

	/**
	 * 根据指定的参数深度优先变量xml文档树，获取指定属性
	 * 
	 * @param nodeList
	 *            要探查的节点
	 * @param nodeName
	 *            获取值的节点名称
	 * @param attributeName
	 *            获取值的属性名称
	 * @param attributeValue
	 *            获取值的属性值
	 * @param findattributeName
	 *            要获取值的实行名称
	 * @return 返回属性列表
	 * @throws Exception
	 */
	static private List<Node> Log4jDomTraversalDep(NodeList nodeList, final String nodeName, final String attributeName, final String attributeValue,
			final String findattributeName) throws Exception {
		ArrayList<Node> retList = new ArrayList<Node>();
		final int len = nodeList.getLength();
		for (int index = 0; index < len; index++) {// 循环节点列表
			Node node = nodeList.item(index);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				String _nodeName = node.getNodeName();
				if (nodeName.equals(_nodeName)) {// 匹配节点名称是否对应
					NamedNodeMap attribute = node.getAttributes();
					for (int i = 0; i < attribute.getLength(); i++) {
						Node _attrbuteNode = attribute.item(i);
						String _attrNodeName = _attrbuteNode.getNodeName();
						String _attrNodeValue = _attrbuteNode.getNodeValue();
						if (attributeName.equals(_attrNodeName) && attributeValue.equals(_attrNodeValue)) {// 匹配属性名称和属性值是否对应
							for (int indx = 0; indx < attribute.getLength(); indx++) {// 匹配上了则获取对应属性值
								Node _findattr = attribute.item(indx);
								String _findName = _findattr.getNodeName();
								if (findattributeName.equals(_findName)) {
									retList.add(_findattr);
								}
							}
						}
					}
				}
				NodeList childNodeList = node.getChildNodes();
				List<Node> tempList = Log4jDomTraversalDep(childNodeList, nodeName, attributeName, attributeValue, findattributeName);// 深度优先搜索，递归向下搜索
				retList.addAll(tempList);
			}
		}
		return retList;
	}


	/**
	 * 在使用配置信息之前，寫入需要的信息（文件輸出路徑）
	 * 
	 * @param context
	 * @param doc
	 * @return
	 * @throws Exception
	 */
	static private Document parasLog4JxmlFilePath(String configxmlPath, Document doc) throws Exception {
		Element ele = doc.getDocumentElement();
		NodeList nodeList = ele.getChildNodes();
		List<Node> attrList = Log4jDomTraversalDep(nodeList, "param", "name", "File", "value");// 深度优先搜索获取所有对应的属性节点信息
		for (int index = 0; index < attrList.size(); index++) {// 修改节点属性
			Node node = attrList.get(index);
			final String locPath = node.getNodeValue();
			node.setNodeValue(configxmlPath + locPath);
		}
		return doc;
	}
	
	/**
	 * 日志初始化，必须先于spring先初始化
	 * 
	 * @param context
	 * @throws Exception
	 */
	static public void initClass(ServletContext context) throws Exception {
		final String logXmlConPaht = context.getInitParameter("framework-logConfigPath");
		String outFolderPath = context.getRealPath("/");
		String logFile = ApplicationConfig.getConfig("RuntimeEnvironment.logOutPutPath");
//		String outFolderPath = System.getProperty("catalina.home");webapps/timsslog
		if(null != logFile && !"@".equals(logFile)){
			outFolderPath = System.getProperty("catalina.home") + logFile;
		}
		
		outFolderPath = outFolderPath.replace("\\", "\\\\");// 修改写入Document的\符号，使用\\表达路径
		log.info("日志文件输出根目录为：" + outFolderPath + "log");
		initLog4jConfigType(logXmlConPaht, outFolderPath);
		log.info("=================================================");
		log.info("日志工具初始化完成");
		log.info("=================================================");
	}
	
	/**
	 * 初始化LOG4J日志
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-6-27
	 * @param configxmlPath
	 * @param outFolderPath
	 * @throws Exception:
	 */
	static public void initLog4jConfigType(String configxmlPath, String outFolderPath) throws Exception {
		InputStream in = JarFileUtil.getJarSourceFileInputStreanm(LogUtil.class, configxmlPath);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(in);
		in.close();
		parasLog4JxmlFilePath(outFolderPath, doc);
		org.w3c.dom.Element element = doc.getDocumentElement();
		DOMConfigurator.configure(element);
	}
	
	
	

	
	
	/**
	 * 组织输出满足logstash配置的正则表达式的日志信息
	 * @description:
	 * @author: 890151
	 * @createDate: 2016-10-21
	 * @param logstash
	 * @param mode
	 * @param exception
	 * @throws Exception:
	 */
	private static void writeStashLog(Logstash logstash, modeType mode, Throwable ex){
		//构造logstash规范日志
		String[] paramKeyArray = {"module","methodName","userId","siteId","diffTime","diffMemory",
				"intval1","intval2","intval3","floatval1","floatval2","floatval3","wordval1","wordval2",
				"userDefineInfo","methodArgs"};
		String logStashInfo = "";
		try {
			//按数组顺序输出
			for (int i = 0; i < paramKeyArray.length; i++) {
				String paramKey = paramKeyArray[i];
				char[] cs = paramKey.toCharArray();
		        cs[0] -= 32;//ASCII码 a：97  A：65
		        String methodKey = String.valueOf(cs);
				Method method = logstash.getClass().getDeclaredMethod("get" + methodKey, new Class[]{});
				if(method != null){
					Object invoke = method.invoke(logstash, new Object[]{});

					if(invoke != null){
						if(i<2){
							invoke = invoke.toString().replace(".", "_");//含有点号无法满足正则表达式
						}
						logStashInfo +=  paramKey + "=" + invoke.toString() + " ";
					}
				}
			}
			writeLog(logStashInfo, ex, mode);
		} catch (Exception e) {
			log.error("日志工具异常", e);
		}
	}

}
