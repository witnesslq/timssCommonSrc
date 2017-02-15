package com.yudean.itc.util;

import java.lang.reflect.Field;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.yudean.itc.SecurityBeanHelper;
import com.yudean.itc.dto.sec.SecureUser;
import com.yudean.itc.manager.sec.IAuthorizationManager;

public class UserConfigHelper {
	/**
	 * 通过反射读取timss的用户设置
	 * @param request
	 * @return
	 */
	private static IAuthorizationManager authManager = SecurityBeanHelper.getInstance().getBean(IAuthorizationManager.class);
	private static Logger logger = Logger.getLogger(UserConfigHelper.class);
	public static UserConfig getPagerConfig(HttpServletRequest request){
		HttpSession session = request.getSession();
		SecureUser user = (SecureUser)session.getAttribute(Constant.userConf);
		UserConfig defConfig = new UserConfig();
		defConfig.type = "ITC";
		defConfig.theme = "normalTheme";
		defConfig.rows = "15";
		if(user==null){
			//没登陆或者session配置错
			return defConfig;
		}
		if(user instanceof SecureUser){
			//使用SecUser里的配置信息
			logger.debug("获取用户翻页配置（SecUser）");
			String v = authManager.retrieveUserConfig(user, "pagesize");
			if(v!=null){
				defConfig.rows = v;
			}
			v = authManager.retrieveUserConfig(user, "rowstyle");
			if(v!=null){
				defConfig.theme = v;
			}
			return defConfig;
		}
		else{
			try {
				logger.debug("获取用户翻页配置（TIMSS1 User，通过反射）");
				Field cfgField = user.getClass().getDeclaredField("userconfig");
				Object cfg = cfgField.get(user);
				Field rowField = cfg.getClass().getDeclaredField("showLine");
				Field themeField = cfg.getClass().getDeclaredField("showmode");
				defConfig.rows = (String)rowField.get(cfg);
				defConfig.theme = (String)themeField.get(cfg);
				defConfig.type = "TIMSS";
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return defConfig;
	}
	
}
