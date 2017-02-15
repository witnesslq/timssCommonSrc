package com.yudean.mvc.testunit;

import com.yudean.mvc.service.impl.FrameWorkServiceTestUnitImpl;

/**
 * 測試用例,全局設置參數接口
 * @title: {title}
 * @description: {desc}
 * @company: gdyd
 * @className: TimssTestUnitGolbalService.java
 * @author: kChen
 * @createDate: 2014-7-5
 * @updateUser: kChen
 * @version: 1.0
 */
public class TestUnitGolbalService {
	static public Boolean TestUnitMode = false;
	/**
	 * 根據ID設置當前登錄人，设置后会影响分支版本执行或ItcMvcService的接口参数
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-7-5
	 * @param userId: 用户ID
	 * @param siteid: 站点ID
	 * @throws Exception 
	 */
	static public void  SetCurentUserById(String userId, String siteid) throws RuntimeException{
		FrameWorkServiceTestUnitImpl.setUserInfo(userId, siteid);
	}
}
