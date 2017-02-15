package com.yudean.mvc.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yudean.mvc.util.helper.TokenSupervisor;

/**
 * 生成Token工具
 * 
 * @author kchen
 * 
 */
public class FormTokenUtil {
	private static final Logger LOG = Logger.getLogger(FormTokenUtil.class);

	public static String TOKEN_ATTRIBUTE_NAME = "_token";//设置token的标记
	public static long TOKEN_ACT_TIME = 60000 * 30000;//设置token的存储时间
	public static short TOKEN_MAP_LEN = 30;

	private FormTokenUtil() {

	}

	public static void bulidFormToken(HttpServletRequest request, ResponseBody annot) {
		TokenSupervisor token = (TokenSupervisor)request.getSession().getAttribute(TOKEN_ATTRIBUTE_NAME);
		if(null == token){
			token = new TokenSupervisor();
			request.getSession().setAttribute(TOKEN_ATTRIBUTE_NAME, token);
		}
		Long _token = token.build();
		request.setAttribute(TOKEN_ATTRIBUTE_NAME, _token);
		if(null != annot){
			createRes(request, _token);
		}
	}

	public static boolean validFormToken(HttpServletRequest request) {
		TokenSupervisor token = (TokenSupervisor)request.getSession().getAttribute(TOKEN_ATTRIBUTE_NAME);
		if(null == token){
			return false;
		}
		String _token = request.getParameter("_token");
		long tokenL = new Long(_token);
		return token.validToken(tokenL);
	}
	
	private static void createRes(HttpServletRequest request, Long _token){
		try {
			Class<?> clazz = request.getClass();
			if("org.apache.catalina.core.ApplicationHttpRequest".equals(clazz.getName())){//如果是ApplicationHttpRequest则不做任何处理
				LOG.warn("request is ApplicationHttpRequest, not get response----not create token.");
			}else{//反射获取
				Field reqField = clazz.getDeclaredField("request");//获取request域
				Field resField = reqField.getType().getDeclaredField("response");//获取response域
				reqField.setAccessible(true);//设置request可读写私有对象
				Object reqObj = reqField.get(request);//获取request实例
				resField.setAccessible(true);//设置response可读写私有对象
				Object resObj = resField.get(reqObj);//获取reponse实例
				
				Class<?>[] paramTypes = new Class<?>[2];//设置调用response.setHeader的参数对象类型
				paramTypes[0] = String.class;
				paramTypes[1] = String.class;
				
				Method method = resField.getType().getMethod("setHeader", paramTypes);//获取方法对象
				
				Object[] objects = new Object[2];//设置调用response.setHeader的参数
				objects[0] = FormTokenUtil.TOKEN_ATTRIBUTE_NAME;
				objects[1] = _token.toString();
				
				method.invoke(resObj, objects);//执行setHeader方法
			}
		} catch (Exception e) {
			LOG.warn("create response head error!", e);
		}
	}
}