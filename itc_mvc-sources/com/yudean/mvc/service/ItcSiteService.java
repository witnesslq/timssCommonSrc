package com.yudean.mvc.service;

import java.util.List;
import java.util.Map;

import com.yudean.itc.dto.Page;
import com.yudean.itc.dto.sec.Site;
import com.yudean.mvc.bean.userinfo.UserInfoScope;

public interface ItcSiteService {

	/**
	 * 查询站点列表
	 * @param site
	 * @param userinfo
	 * @return
	 */
	Page<Site> selectSite(Site site, UserInfoScope userinfo) throws Exception;
	
	/**
	 * 查询站点明细
	 * @param siteId
	 * @return
	 */
	Site selectSingleSite(String siteId);
	
	/**
	 * 查询站点部门映射关系表
	 * 
	 * @description:
	 * @param orgCode
	 *            公司编码 为空时不作为查询条件
	 * @param siteId
	 *            站点编码 为空时不作为查询条件
	 * @return:
	 */
	List<Map<String, Object>> selectSiteOrg(String orgCode, String siteId);
	
	/**
	 * 更新站点信息
	 * 
	 * @description:
	 * @author: zhouhx
	 * @param site
	 * @return:
	 */
	int updateSiteById(Site site,String orgCode);
	
	/**
	 * 增加站点信息
	 * 
	 * @description:
	 * @author: zhouhx
	 * @param site
	 * @return:
	 */
	int insertSite(Site site,String orgCode);
	
	/**
	 * 增加站点~部门映射关系
	 * 
	 * @description:
	 * @author: zhouhx
	 * @param orgCode
	 * @param siteId
	 * @return:
	 */
	int insertSiteOrg(String orgCode, String siteId);
	
	/**
	 * 是否发送短信
	 * @param siteId
	 * @return
	 */
	boolean isSMSSend(String siteId);
	
	/**
	 * 是否发送邮件
	 * @param siteId
	 * @return
	 */
	boolean isMailSend(String siteId);
}
