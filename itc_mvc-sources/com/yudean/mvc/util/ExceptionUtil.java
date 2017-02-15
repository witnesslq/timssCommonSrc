package com.yudean.mvc.util;

import java.lang.reflect.Method;

import org.springframework.web.servlet.ModelAndView;

import com.yudean.itc.bean.exception.ExceptionData;
import com.yudean.itc.util.StringHelper;
import com.yudean.mvc.configs.MvcConfig;
import com.yudean.mvc.exception.BaseExceptionInterface;
import com.yudean.mvc.exception.ExceptionFramework;
import com.yudean.mvc.view.ModelAndViewPage;

/**
 * 异常处理工具
 * 
 * @company: gdyd
 * @className: ExceptionUtil.java
 * @author: kChen
 * @createDate: 2014-9-18
 * @updateUser: kChen
 * @version: 1.0
 */
public class ExceptionUtil {
	final static private String s_INFO_NaN = "NaN";

	/**
	 * 异常处理方法，容错框架的处理接口
	 * 
	 * @description: 容错处理的切入口，只有到业务模块跑出到@Controller层之后才能被这里的容错框架获取。
	 *               容错框架主要处理2个事，1.识别异常类型。2根据异常类型和当前的请求类型返回指定数据或页面。
	 * @author: kChen
	 * @createDate: 2014-7-21
	 * @param e
	 * @param pjp
	 * @return
	 * @throws Throwable
	 *             :
	 */
	static public ExceptionData caseException(Exception e, Method method, ExceptionFramework excep) throws Exception {
		ExceptionData ret = null;
		try {
			ExceptionData expData = excep.TimssRunException(e);

			if (BaseExceptionInterface.class.isAssignableFrom(e.getClass())) {
				BaseExceptionInterface baseEx = (BaseExceptionInterface) e;
				expData.setErrInfo(baseEx.getExceptionMsg());
			}
			Class<?> retType = method.getReturnType();// 获取返回类型
			if (retType.equals(String.class) || retType.equals(ModelAndViewPage.class) || retType.equals(ModelAndView.class)) {
				expData.setType(ExceptionData.pageType.page);
			} else {
				expData.setType(ExceptionData.pageType.data);
			}
			ret = expData;
			if (!MvcConfig.RunExcFrameworkNoLogException.contains(e.getClass())) {
				StackTraceElement[] traceList = e.getStackTrace();
				Integer len = 0;
				if (null != traceList) {
					len = traceList.length;
				}
				String sExpData = s_INFO_NaN;
				String sExpPage = s_INFO_NaN;
				try {
					sExpData = expData.getData().toString();
					sExpPage = expData.getPage();
				} catch (Exception ex1) {
					LogUtil.error("获取异常数据失败", ex1);
				}
				LogUtil.error(StringHelper.concat("容错处理框架拦截异常,框架处理后返回data:", sExpData, ".page:", sExpPage, ".异常堆载长度:", len.toString()), e);
			}
		} catch (Exception ex) {
			LogUtil.error("容错框架处理异常", ex);
			throw ex;
		}
		return ret;
	}
}
