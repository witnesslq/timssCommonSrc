package com.yudean.interfaces.service;

import javax.servlet.http.HttpServletRequest;

import com.yudean.itc.dto.interfaces.eip.TaskListBean;
import com.yudean.itc.dto.interfaces.eip.mobile.RetDetailBean;
import com.yudean.itc.dto.interfaces.eip.mobile.RetProcessBean;
import com.yudean.itc.dto.sec.SecureUser;
import com.yudean.itc.exception.sec.AuthenticationException;

import java.util.Map;

/**
 * EIP接口
 * 
 * @company: gdyd
 * @className: EipInterfaceService.java
 * @author: kChen
 * @createDate: 2014-9-17
 * @updateUser: kChen
 * @version: 1.0
 */
public interface IEipInterfaceService {
	/**
	 * 获取待办信息
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-9-17
	 * @return:
	 */
	TaskListBean getWorkflowTaskList(String userid, String password, String url);

	/**
	 * 打开待办页面
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-9-17
	 * @return:
	 */
	RetDetailBean getTaskDetailMobile(String userid, String password, String flowNo, String siteId);
	
	/**
	 * 流程处理接口
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-9-23
	 * @param userid
	 * @param password
	 * @param itemid
	 * @param opinion
	 * @param siteId
	 * @param flowid
	 * @param nextUsers
	 * @return:
	 */
	RetProcessBean processTaskDetailMobile(String userid, String password, String itemid, String opinion, String siteId, String flowid, String taskKey, String nextUsers, String url,HttpServletRequest request);

	Map<String, Object> getUserFavouriteRoute(SecureUser user);

	/**
	 * 带有token返回的登录
	 * 该方法会返回一个持续时间为30分钟的token，以及该用户关联到外部系统(EIP)的权限
	 * @param userId
	 * @param password
     * @return
     */
	Map<String, Object> cachedSignIn(String userId, String password, String siteId) throws AuthenticationException;

	/**
	 * 使用key恢复登录
	 * @param key
	 * @return
     */
	SecureUser signInWithKey(String key);
}
