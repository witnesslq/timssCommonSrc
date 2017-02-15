package com.yudean.interfaces.dao;

import com.yudean.itc.dto.interfaces.sync.SyncLogBean;

/**
 * 人事数据同步日志
 * @company: gdyd
 * @className: EsbInterfaceDao.java
 * @author: kChen
 * @createDate: 2014-9-29
 * @updateUser: kChen
 * @version: 1.0
 */
public interface SyncLogDao {
	/**
	 * 查询指定人事数据
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-9-29
	 * @param bean
	 * @return:
	 */
	 int addLog(SyncLogBean bean);
}
