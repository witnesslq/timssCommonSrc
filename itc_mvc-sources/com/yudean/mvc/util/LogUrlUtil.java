package com.yudean.mvc.util;

import com.yudean.itc.code.ParamConfig;
import com.yudean.itc.code.InterfaceLoginType;
import com.yudean.itc.dto.interfaces.eip.TransferData;
import com.yudean.itc.util.EncryptUtil;
import com.yudean.itc.util.StringHelper;
import com.yudean.itc.util.json.JsonHelper;

/**
 * 生成登陆路径的工具类
 * @title: {title}
 * @description: {desc}
 * @company: gdyd
 * @className: LogUrlUtil.java
 * @author: kChen
 * @createDate: 2014-11-21
 * @updateUser: kChen
 * @version: 1.0
 */
public class LogUrlUtil {
//	static private String s_urlSplite = "/";
	static private String s_pathSplite = "?";
	static private String s_paramSplite = "&";
	static private String s_equflag = "=";
	static private String s_path = "login";
	static private String s_methodflag = "method=";
	
	/**
	 * 生成直接登陆并打开指定流程单的URL,需要客户端提供用户密码
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-11-21
	 * @param realUrl 系统服务路径
	 * @param inLineurl 系统内部单据路径
	 * @param flowNo 流水号
	 * @param siteid 站点ID
	 * @param userId
	 * @return 户号
	 * @throws Exception:
	 */
	static public String loginWithOpenWorkflowNoPasswd(String realUrl, String inLineurl, String flowNo, String siteid, String userId) throws Exception {
		String path = StringHelper.concat(realUrl, s_path, s_pathSplite);
		path = StringHelper.concat(path, s_methodflag, ParamConfig.LoginMethod, s_paramSplite, ParamConfig.SiteID, s_equflag, siteid, s_paramSplite,
				ParamConfig.UserIDName, s_equflag, userId, s_paramSplite);
		path = StringHelper.concat(path, ParamConfig.ModeName, s_equflag, InterfaceLoginType.openWorkflowDetail.toString(), s_paramSplite);
		TransferData transData = new TransferData();
		transData.setUrl(inLineurl);
		transData.setFlowNo(flowNo);
		String base64 = EncryptUtil.UrlBase64Encode(JsonHelper.fromBeanToJsonString(transData));
		path = StringHelper.concat(path, ParamConfig.DataName, s_equflag, base64);
		return path;
	}
	
	/**
	 * 生成直接登陆并打开指定流程单的URL
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-11-21
	 * @param realUrl 系统服务路径
	 * @param inLineurl 系统内部单据路径
	 * @param flowNo 流水号
	 * @param siteid 站点ID
	 * @param userId
	 * @return 户号
	 * @throws Exception:
	 */
	static public String loginWithOpenWorkflow(String realUrl, String inLineurl, String flowNo, String siteid, String userId, String passwd) throws Exception {
		String path = StringHelper.concat(realUrl, s_path, s_pathSplite);
		path = StringHelper.concat(path, s_methodflag, ParamConfig.LoginMethod, s_paramSplite, ParamConfig.SiteID, s_equflag, siteid, s_paramSplite,
				ParamConfig.UserIDName, s_equflag, userId, s_paramSplite, ParamConfig.PasswordIDName, s_equflag, passwd, s_paramSplite);
		path = StringHelper.concat(path, ParamConfig.ModeName, s_equflag, InterfaceLoginType.openWorkflowDetail.toString(), s_paramSplite);
		TransferData transData = new TransferData();
		transData.setUrl(inLineurl);
		transData.setFlowNo(flowNo);
		String base64 = EncryptUtil.UrlBase64Encode(JsonHelper.fromBeanToJsonString(transData));
		path = StringHelper.concat(path, ParamConfig.DataName, s_equflag, base64);
		return path;
	}
	
}
