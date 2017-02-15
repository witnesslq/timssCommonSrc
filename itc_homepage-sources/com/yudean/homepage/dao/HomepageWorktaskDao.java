package com.yudean.homepage.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yudean.homepage.bean.WorktaskBean;
import com.yudean.homepage.bean.WorktaskUserBean;

/**
 * 首页工作任务相关持久层
 * 
 * @title: {title}
 * @description: 工作任务相关的数据库操作
 * @company: gdyd
 * @className: HomepageWorktaskDao.java
 * @author: kChen
 * @createDate: 2014-6-30
 * @updateUser: kChen
 * @version: 1.0
 */
public interface HomepageWorktaskDao {

	/**
	 * 增加一条记录
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-6-30
	 * @param work
	 * @return:
	 */
	int insertWorktask(WorktaskBean task);

	/**
	 * 根据条件查询工作任务
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-6-30
	 * @param work
	 * @return:
	 */
	List<WorktaskBean> queryWorktask(WorktaskBean task);

	/**
	 * 根据条件查询工作任务
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-6-30
	 * @param work
	 * @return:
	 */
	List<WorktaskBean> queryWorktaskByUser(@Param("task") WorktaskBean task,@Param("user") WorktaskUserBean user);
	
	/**
	 * 修改一条记录
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-6-30
	 * @param work
	 * @return:
	 */
	int updateWorktask(WorktaskBean task);

	/**
	 * 删除一条记录
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-6-30
	 * @param id
	 * @return:
	 */
	int deleteWorktask(Integer id);

	/**
	 * 查询序列号
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-7-28
	 * @return:
	 */
	Integer queryMainSeq();
}
