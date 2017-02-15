package com.yudean.mvc.web;

import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yudean.itc.annotation.AopNone;
import com.yudean.itc.annotation.MethodCacheWeb;
import com.yudean.itc.code.CacheType;
import com.yudean.itc.code.ParamConfig;
import com.yudean.itc.dto.interfaces.eip.mobile.RetKeyValue;
import com.yudean.itc.dto.sec.Organization;
import com.yudean.itc.dto.sec.Role;
import com.yudean.itc.dto.sec.SecureUserGroup;
import com.yudean.itc.dto.support.AppEnum;
import com.yudean.mvc.bean.userinfo.UserInfo;
import com.yudean.mvc.service.ItcMvcService;
import com.yudean.mvc.util.MvcJsonUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * TIMSS框架获取参数前端接口
 * 
 * @title: {title}
 * @description: {desc}
 * @company: gdyd
 * @className: FrameworkController.java
 * @author: kChen
 * @createDate: 2014-6-29
 * @updateUser: kChen
 * @version: 1.0
 */
@Controller
@RequestMapping(value = "framework/itcMvcService")
public class FrameworkController {

	@Autowired
	ItcMvcService itcMvcService;

	/**
	 * 
	 * @description: 前端获取枚举变量
	 * @author: kChen
	 * @createDate: 2014-6-29
	 * @return
	 * @throws Exception
	 *             :
	 */
	@RequestMapping(value = "/enumParam")
	public JSONObject enumParam(String data) throws Exception {
		return enumParam(data, null);
	}

	/**
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-8-13
	 * @param data
	 * @param ErrorFlag
	 * @return
	 * @throws Exception:
	 */
	@AopNone
	public JSONObject enumParam(String data, Boolean ErrorFlag) throws Exception {
		StringTokenizer tokenizer = new StringTokenizer(data, ",");
		JSONObject json = new JSONObject();
		while (tokenizer.hasMoreElements()) {
			String eCateCode = tokenizer.nextToken();
			List<AppEnum> oneList = itcMvcService.getEnum(eCateCode);
			if (null != oneList && !oneList.isEmpty()) {
				JSONArray jsonList = MvcJsonUtil.JSONArrayFromList(oneList);
				json.put(eCateCode, jsonList);
			} else {
				ErrorFlag = Boolean.TRUE;
			}
		}
		return json;
	}

	/**
	 * 
	 * @description: 前端获取用户信息
	 * @author: kChen
	 * @createDate: 2014-6-29
	 * @return 返回用户信息的JSON字符串
	 * @throws Exception
	 *             :
	 */
	@RequestMapping(value = "/userInfo")
	public JSONObject userInfo() throws Exception {
		UserInfo userinfo = itcMvcService.getUserInfoScopeDatas();
		JSONObject json = new JSONObject();
		json.put("userId", userinfo.getUserId());
		json.put("userName", userinfo.getUserName());
		json.put("siteId", userinfo.getSiteId());
		List<Role> role = userinfo.getRoles();
		json.put("roles", MvcJsonUtil.JSONArrayFromList(role));
		List<Organization> orgs = userinfo.getOrgs();
		json.put("orgs", MvcJsonUtil.JSONArrayFromList(orgs));
		List<SecureUserGroup> groups = userinfo.getSecureUser().getGroups();
		json.put("groups", MvcJsonUtil.JSONArrayFromList(groups));
		return json;
	}

	/**
	 * 
	 * @description: 获取用户ID
	 * @author: kChen
	 * @createDate: 2014-6-29
	 * @return 返回用户Id的JSON字符串
	 * @throws Exception
	 *             :
	 */
	@RequestMapping(value = "/userId")
	public JSONObject userId() throws Exception {
		UserInfo userinfo = itcMvcService.getUserInfoScopeDatas();
		JSONObject json = new JSONObject();
		json.put("UserId", userinfo.getUserId());
		return json;
	}

	/**
	 * 获取用户名称
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-6-29
	 * @return 返回用户名称
	 * @throws Exception
	 *             :
	 */
	@RequestMapping(value = "/userName")
	public JSONObject userName() throws Exception {
		UserInfo userinfo = itcMvcService.getUserInfoScopeDatas();
		JSONObject json = new JSONObject();
		json.put("UserName", userinfo.getUserName());
		return json;
	}
	
	@RequestMapping(value = "/EipInterfacetestJson")
	@MethodCacheWeb(type = CacheType.Session)
	public RetKeyValue testJson() throws Exception {
		RetKeyValue ret = new RetKeyValue("time",ParamConfig.S_MSTIME_FORMATTER.format(new Date()));
		return ret;
	}
}
