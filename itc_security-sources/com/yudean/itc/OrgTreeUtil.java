package com.yudean.itc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.yudean.itc.bean.OrgTreeNode;
import com.yudean.itc.dao.sec.OrganizationMapper;
import com.yudean.itc.dao.sec.SiteMapper;
import com.yudean.itc.dto.sec.Organization;
import com.yudean.itc.dto.sec.SecureUser;
import com.yudean.itc.dto.sec.Site;

public class OrgTreeUtil {
	private static Map<String, String> siteChMapping;//系统中所有站点ID-站点中文的映射
	public static OrgTreeNode rootNode;//组织机构树的根节点
	private static Map<String, OrgTreeNode> nodeMap;//通过编号找组织节点
	private static SecurityBeanHelper helper = SecurityBeanHelper.getInstance();
	private static Logger logger = Logger.getLogger(OrgTreeUtil.class);	
	private static SiteMapper siteMapper = helper.getBean(SiteMapper.class);
	private static OrganizationMapper orgMapper = helper.getBean(OrganizationMapper.class);
	
	/**
	 * 获取组织对应的站点ID 如果当前组织没有设置站点则返回最近一个有站点的父节点的站点ID
	 * @param orgCode
	 * @return
	 */
	public static String getOrgSite(String orgCode){
		if(!nodeMap.containsKey(orgCode)){
			return null;
		}
		OrgTreeNode node = nodeMap.get(orgCode);
		while(node.getOrg().getSiteId() == null && node.getParentNode() != null){
			node = node.getParentNode();
		}
		return node.getOrg().getSiteId();
	}
	
	public static String getSiteChName(String siteId){
		return siteChMapping.containsKey(siteId) ? siteChMapping.get(siteId) : "";
	}
	
	/**
	 * 获取一个组织下所有的子节点
	 * @param orgCode
	 * @return
	 */
	public static List<String> getAllChildren(String orgCode){
		List<String> result = new ArrayList<String>();
		OrgTreeNode startNode = nodeMap.get(orgCode);		
		if(startNode != null){
			List<String> orgArr = new ArrayList<String>();
			orgArr.add(startNode.getOrg().getCode());
			while(orgArr.size() > 0){
				List<String> tmpArr = new ArrayList<String>();
				for(String org: orgArr){
					result.add(org);
					OrgTreeNode currNode = nodeMap.get(org);
					List<OrgTreeNode> children = currNode.getChildren();
					if(children != null){
						for(OrgTreeNode cNode: children){
							tmpArr.add(cNode.getOrg().getCode());
						}
					}
				}
				orgArr = tmpArr;
			}
		}
		return result;
	}
	
	/**
	 * 获取一个用户在当前站点下的隶属所有部门（包括部门的子部门）
	 * @param user
	 * @return
	 */
	public static List<String> getChildOrgsInSite(SecureUser user){
		Set<String> result = new HashSet<String>();
		List<Organization> orgs = user.getOrganizations();
		for(Organization org: orgs){
			List<String> subOrgs = getAllChildren(org.getCode());
			for(String s: subOrgs){
				if(!result.contains(s)){
					result.add(s);
				}
			}
		}
		List<String> tmp = new ArrayList<String>();
		for(String t:result){
			tmp.add(t);
		}
		return tmp;
	}
	
	public static void buildOrgTree(){
		logger.info("Now building organization tree......");
		//虚拟的根节点 用于放集团下所有的一层节点  约定所有一层节点的parentCode=1
		nodeMap = new HashMap<String, OrgTreeNode>();
		rootNode = new OrgTreeNode();
		Organization orgRoot = new Organization();
		orgRoot.setCode("1");
		orgRoot.setName("广东粤电集团有限公司");
		rootNode.setOrg(orgRoot);		
		nodeMap.put("1", rootNode);
		List<Organization> orgs = orgMapper.selectAllOrgs();		
		//构建组织机构树
		for(int i=0;i<orgs.size();i++){
			Organization org = orgs.get(i);
			String parentCode = org.getParentCode();
			String code = org.getCode();
			//当前节点
			OrgTreeNode node = null;			
			if(nodeMap.containsKey(code)){
				node = nodeMap.get(code);				
			}else{
				node = new OrgTreeNode();
				nodeMap.put(code, node);
			}
			node.setOrg(org);
			//当前节点的父节点
			OrgTreeNode parentNode = null;
			if(nodeMap.containsKey(parentCode)){
				parentNode = nodeMap.get(parentCode);					
			}else{
				parentNode = new OrgTreeNode();
				nodeMap.put(parentCode, parentNode);
			}
			if(parentNode.getChildren() == null){
				parentNode.setChildren(new ArrayList<OrgTreeNode>());
			}
			parentNode.getChildren().add(node);
			node.setParentNode(parentNode);
		}
		updateOrgSite();
		logger.info("Building organization tree success");
	}
	
	private static void updateOrgSite(){
		//将所有站点的中文名字存起来 主要是用户在登录的时候要给他一个中文显示
		siteChMapping = new HashMap<String, String>();		
		Site siteCon = new Site();
		List<Site> sites = siteMapper.selectSite(siteCon, SiteMapper.Oper.Precise);
		for(Site site : sites){
			siteChMapping.put(site.getId(), site.getName());
		}
		List<Map> parentMapping = orgMapper.selectAllOrgSiteMapping();
		for (Map map : parentMapping) {
			String orgCode = (String) map.get("ORGCODE");
			String siteId = (String) map.get("SITEID");
			OrgTreeNode orgNode = nodeMap.get(orgCode);
			orgNode.getOrg().setSiteId(siteId);
		}
	}
	
}
