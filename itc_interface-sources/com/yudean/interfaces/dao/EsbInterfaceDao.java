package com.yudean.interfaces.dao;

import java.util.List;

import com.yudean.itc.dto.interfaces.sync.SyncConfBean;

/**
 * 人事数据同步查询方法
 * @company: gdyd
 * @className: EsbInterfaceDao.java
 * @author: kChen
 * @createDate: 2014-9-29
 * @updateUser: kChen
 * @version: 1.0
 */
public interface EsbInterfaceDao {
	/**
	 * 查询指定人事数据
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-9-29
	 * @param bean
	 * @return:
	 */
	List<SyncConfBean> getConf(SyncConfBean bean);
}
