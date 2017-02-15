package com.yudean.mvc.listener;

import java.util.Date;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;

import com.yudean.itc.code.ParamConfig;
import com.yudean.itc.util.StringHelper;

public class MvcSessionListener implements HttpSessionListener {
	private static final Logger log = Logger.getLogger(MvcSessionListener.class);
	private static final String s_Name_create = "session create: ";
	private static final String s_Name_destroyed = "session destroyed: ";
	private static final String s_Name_time = ".time:";
	private static final String s_Name_split = ": ";
	private static final String s_Name_username = "username";

	@Override
	public void sessionCreated(HttpSessionEvent se) {
		log.info(StringHelper.concat(s_Name_create, se.getSession().getId(), s_Name_time, ParamConfig.S_SSTIME_FORMATTER.format(new Date())));
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		HttpSession session = se.getSession();
		Object userObj = session.getAttribute(s_Name_username);
		String userName = "";
		if (null != userObj) {
			userName = (String) userObj;
		}
		log.info(StringHelper.concat(s_Name_destroyed, se.getSession().getId(), s_Name_time, ParamConfig.S_SSTIME_FORMATTER.format(new Date()), s_Name_split, userName));
	}
}
