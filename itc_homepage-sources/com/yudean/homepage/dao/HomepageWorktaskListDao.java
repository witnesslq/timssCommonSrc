package com.yudean.homepage.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yudean.homepage.bean.WorktaskBean;
import com.yudean.homepage.bean.WorktaskUserBean;
import com.yudean.homepage.vo.WorktaskFlowViewObj;
import com.yudean.homepage.vo.WorktaskViewObj;
import com.yudean.itc.annotation.MapperMethod;
import com.yudean.itc.annotation.MapperMethod.Type;
import com.yudean.itc.dto.Page;

/**
 * 任务管理模块Page绑定查询方法。
 * 
 * @title: {title}
 * @description: {desc}
 * @company: gdyd
 * @className: HomepageWorktaskListDao.java
 * @author: kChen
 * @createDate: 2014-7-10
 * @updateUser: kChen
 * @version: 1.0
 */
public interface HomepageWorktaskListDao {
	/**
	 * 查询待办列表
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-7-10
	 * @param page
	 * @return:
	 */
	@MapperMethod(excuteType = Type.Fuzzy_UpperLowerFix)
	List<WorktaskViewObj> queryDoingWorkTask(Page<WorktaskViewObj> page);

	/**
	 * 查询列表
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-7-10
	 * @param page
	 * @return:
	 */
	List<WorktaskViewObj> queryWorkTask(Page<WorktaskViewObj> page);
	
	/**
	 * 查询列表
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-7-10
	 * @param page
	 * @return:
	 */
	List<WorktaskViewObj> queryCompleteWorkTask(Page<WorktaskViewObj> page);

	/**
	 * 根据Flow查询待办信息
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-9-23
	 * @param viewObj
	 * @return:
	 */
	WorktaskFlowViewObj queryWorkTaskFlow(WorktaskFlowViewObj viewObj);

	/**
	 * 查询用户当前站点的待办信息
	 * @param userId
	 * @param siteId
	 * @param classType
	 * @param flag
	 * @return
	 */
	int queryWorkTakskCount(@Param("userid") String userId, @Param("siteid") String siteId, @Param("classtype") WorktaskBean.WorkTaskClass classType,
			@Param("flag") WorktaskUserBean.WorkTaskUserFlag flag);
}
