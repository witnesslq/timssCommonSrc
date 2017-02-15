package com.yudean.mvc.scheduler.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.yudean.mvc.configs.MvcConfig;
import com.yudean.mvc.dao.FrameworkRuntasticStatusRateDao;
import com.yudean.mvc.scheduler.FrameworkScheduler;
import com.yudean.mvc.service.ItcMvcService;
import com.yudean.itc.code.NotificationType;
import com.yudean.itc.code.RuntimeEnvironment_Mode;
import com.yudean.itc.dto.sec.SecureUser;
import com.yudean.itc.dto.support.Configuration;
import com.yudean.itc.manager.support.IConfigurationManager;
import com.yudean.itc.manager.support.INotificationManager;

@Component
@Lazy(false)
public class IFrameworkScheduler implements FrameworkScheduler {
	static final private Logger log = Logger.getLogger(IFrameworkScheduler.class);
	static final private String s_DBTextSpliteFlag = ",";// 数据库字段分割标记
	static final private String s_EmailSendRecp = "framework_heartRateERRSendEmail";// 数据库邮件接收人字段
	static final private String s_SmsSendRecp = "framework_heartRateERRSendSms";// 数据库短信接收人字段

	static final private String s_VeName = "framework_heartRateError";// 模板VE名称

	private enum smsFlag {
		init, // 未初始化
		none, // 停止发送
		send// 发送
	}

	@Autowired
	FrameworkRuntasticStatusRateDao statusRateDao;

	@Autowired
	ItcMvcService itcMvcService;

	private Boolean heartRateNoInit = true;

	String[] smss = null;

	private smsFlag smsSendFlag = smsFlag.init;

	private int heartRateCount = 0;

	private int heartrunNumber = -1;

	@Scheduled(cron = "0/8 * *  * * * ")
	// 每8秒执行一次
	@Override
	public void heartRate() {
		if (MvcConfig.getCurRunMode() == RuntimeEnvironment_Mode.Produce) {
			heartRateTest();// 非开发环境才运行心跳测试
		}
	}

	private void heartRateTest() {
		heartRateCount++;
		if (heartRateCount > heartrunNumber) {
			try {
				if (heartRateNoInit) {
					heartRateNoInit = false;
					log.info("初始化心跳包扫描任务。当前扫描间隔 8 秒。当前扫面执行SQL：select '1' from dual");
				}
				statusRateDao.QueryHeartRate();
				if (heartRateCount == 500) {
					log.info("已经执行" + heartRateCount + "次心跳检测。数据库连接正常.");
					heartRateCount = 0;
				}
				heartrunNumber = -1;
			} catch (Exception e) {
				send(e);
			}
		}
	}

	private void send(Exception e) {
		heartRateCount = 0;
		heartrunNumber = 800;
		try {
			log.error("心跳测试数据库发生异常,发送 通知信息,延迟2个小时后再次执行心跳测试,发送通知信息！", e);

			// 发送邮件通知
			Configuration confEmail = getConfig(s_EmailSendRecp);
			StringTokenizer tokenEmail = new StringTokenizer(confEmail.getVal(), s_DBTextSpliteFlag);
			List<String> emailList = new ArrayList<String>();
			while (tokenEmail.hasMoreElements()) {
				emailList.add((String) tokenEmail.nextElement());
			}
			String[] emails = new String[emailList.size()];
			for (int i = 0; i < emailList.size(); i++) {
				emails[i] = emailList.get(i);
			}
			INotificationManager notify = itcMvcService.getBeans(INotificationManager.class);
			Map<String, Object> args = new HashMap<String, Object>();
			Date curDate = new Date();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss");
			args.put("err_time", format.format(curDate));
			args.put("err_res", e == null || null == e.getMessage() ? "无" : e.getMessage());
			StringBuffer sbuff = new StringBuffer();
			StackTraceElement[] stack = e.getStackTrace();
			for (int i = 0; i < stack.length; i++) {
				sbuff.append(stack[i]);
				sbuff.append("<br>");
			}
			args.put("err_sta", sbuff);
			notify.notify(emails, NotificationType.EMAIL, s_VeName, args);
			// 发送短信通知
			if (getTokenSms()) {
				notify.notify(smss, NotificationType.SMS, s_VeName, args);
			}
		} catch (Exception ex) {
			log.error("处理数据库连接异常失败", ex);
		}
	}

	private boolean getTokenSms() {
		try {
			if (smsFlag.send == smsSendFlag) {
				return true;
			} else if (smsFlag.init == smsSendFlag) {
				Configuration confSms = getConfig(s_SmsSendRecp);
				String smsPhone = confSms.getVal();
				if (null != smsPhone && !"".equals(smsPhone) && !"NaN".equals(smsPhone)) {
					StringTokenizer tokenSms = new StringTokenizer(confSms.getVal(), s_DBTextSpliteFlag);
					List<String> smsList = new ArrayList<String>();
					while (tokenSms.hasMoreElements()) {
						smsList.add((String) tokenSms.nextElement());
						smss = new String[smsList.size()];
						for (int i = 0; i < smsList.size(); i++) {
							smss[i] = smsList.get(i);
						}
					}
					smsSendFlag = smsFlag.send;
					return true;
				} else {
					smsSendFlag = smsFlag.none;
					return false;
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			smsSendFlag = smsFlag.none;
			log.error("设置短信发送消息异常，停止发送", e);
			return false;
		}
	}

	private Configuration getConfig(String param) {
		IConfigurationManager iconfig = itcMvcService.getBeans(IConfigurationManager.class);
		Configuration conf = new Configuration();
		conf.setConf(param);
		// 根据站点和ID获取参数
		List<Configuration> list = iconfig.query(conf, (SecureUser) null);
		if (null != list && 0 < list.size()) {
			return list.get(0);
		} else {
			return null;
		}
	}
}
