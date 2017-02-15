package com.yudean.itc.dao.sec;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yudean.itc.dto.Page;
import com.yudean.itc.dto.sec.Site;

public interface SiteMapper {
	/**
	 * 查询类型 精确查询,模糊查询
	 */
	public enum Oper{
		Precise,
		Fuzzy 
	}
	/**
	 * 查询站点信息
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-9-30
	 * @param site
	 * @return:
	 */
	List<Site> selectSite(@Param("site")Site site, @Param("oper") Oper oper);
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	Site selectSingleSite(@Param("id") String id);

	/**
	 * 查询站点部门映射关系表
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-9-30
	 * @param orgCode
	 *            公司编码 为空时不作为查询条件
	 * @param siteId
	 *            站点编码 为空时不作为查询条件
	 * @return:
	 */
	List<Map<String, Object>> selectSiteOrg(@Param("orgCode") String orgCode, @Param("siteId") String siteId);

	/**
	 * 更新站点信息
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-10-8
	 * @param site
	 * @return:
	 */
	int updateSite(Site site);
	
	/**
	 * 更新站点信息
	 * 
	 * @description:
	 * @author: zhouhx
	 * @createDate: 2014-10-8
	 * @param site
	 * @return:
	 */
	int updateSiteById(Site site);

	/**
	 * 更新站点信息
	 * 
	 * @description:
	 * @author: zhouhx
	 * @createDate: 2014-10-8
	 * @param site
	 * @return:
	 */
	int updateSiteOrg(@Param("siteId")String siteId,@Param("orgCode")String orgCode);
	/**
	 * 增加站点信息
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-9-30
	 * @param site
	 * @return:
	 */
	int insertSite(Site site);

	/**
	 * 增加站点~部门映射关系
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-9-30
	 * @param orgCode
	 * @param siteId
	 * @return:
	 */
	int insertSiteOrg(@Param("siteId") String siteId,@Param("orgCode") String orgCode);
	
	/**
	 * 查询站点列表
	 * @param site
	 * @return
	 */
	List<Site> selectSiteList(Page<?> page);
}
