package com.yudean.homepage.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yudean.homepage.bean.NoticeBean;
import com.yudean.homepage.bean.SiteActiveData;
import com.yudean.itc.code.StatusCode;

public interface HomepageNoticeDao {

	/**
	 * 查询
	 * 
	 * @param userId
	 * @param siteId
	 * @return
	 */
	List<NoticeBean> queryNoticeByUser(@Param("userId") String userId, @Param("siteId") String siteId, @Param("active") StatusCode active, @Param("rownum") Integer rownum);

	/**
	 * 查询单条记录
	 * 
	 * @param code
	 * @param siteId
	 * @return
	 */
	NoticeBean queryOneNotice(@Param("code") String code, @Param("siteId") String siteId);

	
	/**
	 * 添加
	 * 
	 * @param noticeBean
	 * @return
	 */
	int insertNotice(NoticeBean noticeBean);

	/**
	 * 更新
	 * 
	 * @param noticeBean
	 * @return
	 */
	int updateNotic(NoticeBean noticeBean);
	
	/**
	 * 获取站点最近的登陆活跃日期
	 * @param count
	 * @return
	 */
	List<SiteActiveData> querySiteActiveDate(@Param("curDate") Date date, @Param("stCount") int count);
	
	/**
	 * 获取近期最大活跃数
	 * @param date
	 * @param count
	 * @return
	 */
	List<Map<String, BigDecimal>> querySiteActiveMaxCount(@Param("curDate") Date date, @Param("stCount") int count);
	
	/**
	 * 获取近期最小活跃度
	 * @param date
	 * @param count
	 * @return
	 */
	List<Map<String, BigDecimal>> querySiteActiveMinCount(@Param("curDate") Date date, @Param("stCount") int count);
}
