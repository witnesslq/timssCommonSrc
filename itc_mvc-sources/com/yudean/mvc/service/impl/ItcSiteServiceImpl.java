package com.yudean.mvc.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yudean.itc.dao.sec.SiteMapper;
import com.yudean.itc.dto.Page;
import com.yudean.itc.dto.sec.Site;
import com.yudean.itc.util.map.MapHelper;
import com.yudean.mvc.bean.userinfo.UserInfoScope;
import com.yudean.mvc.service.ItcSiteService;

@Service
public class ItcSiteServiceImpl implements ItcSiteService {

	private static final Logger LOGGER = Logger.getLogger(ItcSiteServiceImpl.class);
	@Autowired
	private SiteMapper siteMapper;
	
	@Override
	public Page<Site> selectSite(Site site, UserInfoScope userinfo) throws Exception {
		// TODO Auto-generated method stub
		Page<Site> page = userinfo.getPage();
		Map<String, String[]> params = userinfo.getParamMap();
		Map<String, String> propertyColumnMap = new HashMap<String, String>();
		
		propertyColumnMap.put("siteId", "SITE_ID");
		propertyColumnMap.put("siteName","SITE_NAME");
		if(params != null && params.containsKey("search")){
			String fuzzySearchParams = userinfo.getParam("search");
			LOGGER.info("查询系统配置参数的条件：" + fuzzySearchParams);
			// 调用工具类将jsonString转为HashMap
			Map<String, Object> fuzzyParams = MapHelper
					.jsonToHashMap(fuzzySearchParams);
			// 如果Dao中使用resultMap来将查询结果转化为bean，则需要调用此工具类将Map中的key做转换，变成数据库可以识别的列名
			fuzzyParams = MapHelper.fromPropertyToColumnMap(
					(HashMap<String, Object>) fuzzyParams, propertyColumnMap);
			// 自动的会封装模糊搜索条件
			page.setFuzzyParams(fuzzyParams);
		}
		 // 设置排序内容
        if ( params != null && params.containsKey( "sort" ) ) {
            String sortKey = userinfo.getParam( "sort" );
            // 如果Dao中使用resultMap来将查询结果转化为bean，则需要将Map中的key做转换，变成数据库可以识别的列名
            sortKey = propertyColumnMap.get( sortKey );
            page.setSortKey( sortKey );
            page.setSortOrder( userinfo.getParam( "order" ) );
        } else {
            // 设置默认的排序字段
            page.setSortKey( "updateTime" );
            page.setSortOrder( "desc" );
        }
		
        try {
            List<Site> ret = siteMapper.selectSiteList(page);
            page.setResults( ret );
        } catch (Exception e) {
            throw new Exception( "----------------- ItcSiteServiceImpl 的  selectSite 方法抛出异常 ----------------- ",
                    e );
        }
        return page;
	}

	@Override
	public Site selectSingleSite(String siteId) {		
		return siteMapper.selectSingleSite(siteId);
	}

	@Override
	public List<Map<String, Object>> selectSiteOrg(String orgCode, String siteId) {
		return siteMapper.selectSiteOrg(orgCode, siteId);
	}

	@Override
	public int updateSiteById(Site site,String orgCode) {
		
		siteMapper.updateSiteById(site);
		if(null != orgCode){
			List<Map<String, Object>> list = siteMapper.selectSiteOrg(null, site.getId());
			if(null != list && list.size()>0){
				siteMapper.updateSiteOrg(site.getId(), orgCode);
			}else{
				siteMapper.insertSiteOrg(site.getId(),orgCode);
			}
			
		}
		return 1;
	}

	@Transactional
	@Override
	public int insertSite(Site site,String orgCode) {	
		siteMapper.insertSite(site);
		siteMapper.insertSiteOrg(site.getId(),orgCode);
		return 1;
	}

	@Override
	public int insertSiteOrg(String orgCode, String siteId) {
		return siteMapper.insertSiteOrg(siteId,orgCode);
	}

	@Override
	public boolean isSMSSend(String siteId) {
		Site site = selectSingleSite(siteId);
		String isSend = site.getSmsIsSend();
		if("Y".equals(isSend)){
			LOGGER.info("current site:当前站点配置可发送短信.");
			return true;
		}else {
			LOGGER.info("current site:当前站点配置不可发送短信.");
			return false;
		}
	}

	@Override
	public boolean isMailSend(String siteId) {
		Site site = selectSingleSite(siteId);
		String isSend = site.getMailIsSend();
		if("Y".equals(isSend)){
			LOGGER.info("current site:当前站点配置可发送email.");
			return true;
		}else {
			LOGGER.info("current site:当前站点配置不可发送email.");
			return false;
		}
	}

}
