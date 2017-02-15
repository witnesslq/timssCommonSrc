package com.yudean.interfaces.scheduler;

/**
 * 统一数据交换平台人事数据同步接口
 * @company: gdyd
 * @className: FrameworkScheduler.java
 * @author: kChen
 * @createDate: 2014-7-29
 * @updateUser: kChen
 * @version: 1.0
 */
public interface ISyncUserOrgScheduler {
	/**
	 * 心跳检测
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-7-29:
	 */
	void  sync();
}
