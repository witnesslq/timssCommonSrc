package com.yudean.itc.security.service.impl;

import java.util.*;

import com.yudean.itc.OrgTreeUtil;
import com.yudean.itc.bean.PrivEuTreeNode;
import com.yudean.itc.bean.PrivOrigin;
import com.yudean.itc.code.StatusCode;
import com.yudean.itc.dao.sec.PrivilegeMapper;
import com.yudean.itc.dto.sec.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yudean.itc.dao.sec.OrganizationMapper;
import com.yudean.itc.manager.sec.IAuthorizationManager;
import com.yudean.itc.manager.sec.ISecurityMaintenanceManager;
import com.yudean.itc.bean.EuTreeNode;

@Service
public class TreeService {
	@Autowired
	private IAuthorizationManager authManager;
	@Autowired
	private ISecurityMaintenanceManager secManager;
	@Autowired
	private OrganizationMapper orgMapper;
	@Autowired
	private PrivilegeMapper privMapper;

	private Logger logger = Logger.getLogger(TreeService.class);
	
	/**
	 * 获得组织机构树下某节点下的一层机构
	 * @param parent   根节点
	 * @param operator
	 * @param onlyOrg  是否只返回组织而不返回组织下的用户
	 * @param ignoreSite 是否无视登陆站点，用于角色加人，这要求用户必须是超管，同时业务上需求（主动传入参数）
	 * @return
	 */
	public List<EuTreeNode> getOrgTreeData(String parent,SecureUser operator,boolean onlyOrg,boolean ignoreSite){
		logger.debug("get org data,parent = " + parent + ",onlyorg=" + onlyOrg);
		List<EuTreeNode> nodes = new ArrayList<EuTreeNode>();
		List<Organization> orgs = null;
		List<String> pidList = new ArrayList<String>();		
		if(parent == null || parent.equals("1")){
			//这里注意 因为超管显示所有站点时必须用父节点为1的站点做初始列表 如果用站点-组织关联会把多经等站点列出来
			if(ignoreSite){
				pidList.add("1");
				orgs = orgMapper.selectOrgsByParentIds(pidList);
			}
			else{
				orgs = orgMapper.selectOrgBySiteId(operator.getCurrentSite());
			}
		}
		else{
			pidList.add(parent);
			orgs = orgMapper.selectOrgsByParentIds(pidList);
			if(!ignoreSite){
				List<Organization> filteredOrgs = new ArrayList<Organization>();
				for(Organization org: orgs){
					if(OrgTreeUtil.getOrgSite(org.getCode()).equals(operator.getCurrentSite())){
						filteredOrgs.add(org);
					}
				}
				orgs = filteredOrgs;
			}
		}
		
		for (int i = 0; i < orgs.size(); i++) {
			Organization org = orgs.get(i);
			String nodeName = StringUtils.trimToNull(org.getShortName())!=null?org.getShortName():org.getName();
			EuTreeNode node = new EuTreeNode(nodeName, "org_" + org.getCode());
			if((org.getNumberOfChildren()==null || org.getNumberOfChildren()==0) && onlyOrg==true){
				node.setState("open");
			}
			else{
				node.setState("closed");				
			}
			node.setIconCls("ico_org_open");
			nodes.add(node);
		}
		if(!onlyOrg){
			List<SecureUser> users = authManager.retriveActiveUsersOfGivenOrg(
					parent, false, operator);
			if (users != null) {
				for (int j = 0; j < users.size(); j++) {
					SecureUser user = users.get(j);
					String userName = user.getName();
					if(user.getActive() == StatusCode.NO){
						userName += "(已禁用)";
					}
					EuTreeNode node = new EuTreeNode(userName, "user_" + user.getId());
					node.setState("open");
					node.setIconCls("ico_person");
					nodes.add(node);
				}
			}
		}
		return nodes;
	}
	
	public List<EuTreeNode> getPrivTreeRoot(){
		/*
		EuTreeNode nodeB = new EuTreeNode("底部菜单", "MNU_B");
		nodeB.setState("closed");
		EuTreeNode nodeT = new EuTreeNode("顶部菜单", "MNU_T");
		nodeT.setState("closed");
		EuTreeNode nodeL = new EuTreeNode("左侧菜单", "MNU_L");
		nodeL.setState("closed");
		*/
		EuTreeNode nodeL = new EuTreeNode("所有菜单", "MNU_0");
		nodeL.setState("closed");
		EuTreeNode nodeG = new EuTreeNode("全局功能", "FUN_F-GLOBAL");
		nodeG.setState("closed");
		List<EuTreeNode> tree = new ArrayList<EuTreeNode>();
		//tree.add(nodeB);
		//tree.add(nodeT);
		tree.add(nodeL);
		tree.add(nodeG);		
		return tree;
	}
	
	/**
	 * 获取某一节点下所有的权限
	 * @param secManager
	 * @param id
	 */
	public void getPrivInNode(List<String> result,String id,SecureUser operator){
		List<EuTreeNode> nodes = getPrivTreeNodes(id);
		for(EuTreeNode node:nodes){
			String nid = node.getId().replace("MNU_", "").replace("FUN_", "");
			if(!operator.isPrivOwnedByUser(nid)){
				continue;
			}
			result.add(node.getId() + "," + node.getText());
			if(node.hasChild){
				getPrivInNode(result, node.getId(),operator);
			}
		}
	}
	
	public ArrayList<EuTreeNode> getPrivTreeNodes(String id){
		Boolean isMenu = false;
		if(id.startsWith("MNU_")){
			isMenu = true;
			id = id.replace("MNU_", "");
		}
		else{
			id = id.replace("FUN_", "");
		}
		ArrayList<EuTreeNode> tree = new ArrayList<EuTreeNode>();
		if(isMenu){
			//menu的下级可以是menu也可以是 function
			List<SecureFunction> funcs = secManager.retrieveFunctions(id);
			List<SecureMenu> menus = secManager.retrieveMenus(id);			
			if(funcs!=null){
				for(SecureFunction fun:funcs){
					EuTreeNode node = new EuTreeNode(fun.getName(), "FUN_" + fun.getId());
					if(fun.getNumberOfSubFunction()!=null && fun.getNumberOfSubFunction()>0){
						node.setState("closed");
						node.hasChild = true;
					}
					tree.add(node);
				}
			}
			if(menus!=null){
				for(SecureMenu mnu:menus){
					EuTreeNode node = new EuTreeNode(mnu.getName(), "MNU_" + mnu.getId());
					if((mnu.getNumberOfSubFunction()!=null && mnu.getNumberOfSubFunction()>0)||
							(mnu.getNumberOfSubMenu()!=null && mnu.getNumberOfSubMenu()>0)){
						node.setState("closed");
						node.hasChild = true;
					}
					tree.add(node);
				}
			}
		}
		else{
			//function的下级只能是function
			List<SecureFunction> funcs = secManager.retrieveSubFunctions(id);
			if(funcs!=null){
				for(SecureFunction fun:funcs){
					EuTreeNode node = new EuTreeNode(fun.getName(), "FUN_" + fun.getId());
					if(fun.getNumberOfSubFunction()!=null && fun.getNumberOfSubFunction()>0){
						node.setState("closed");
						node.hasChild = true;
					}
					tree.add(node);
				}
			}
		}
		return tree;
	}

	/**
	 * 列出用户在当前站点的所有权限以及权限来源
	 * @param uid
	 * @return
	 */
	public List<PrivEuTreeNode> getPrivTreeWithOrigin(String uid, String site){
		Map<String, PrivEuTreeNode> mapping = new HashMap<String, PrivEuTreeNode>();
		List<PrivEuTreeNode> ret = new ArrayList<PrivEuTreeNode>();
		List<Map> data = privMapper.selectUserPrivilegeByTree(uid, site);
		//创建2个默认的父级项
		PrivEuTreeNode pMenu = new PrivEuTreeNode("所有菜单", "MNU_0");
		//PrivEuTreeNode pFunc = new PrivEuTreeNode("全局功能", "FUN_F-GLOBAL");
		mapping.put("MNU_0", pMenu);
		ret.add(pMenu);
		//递归构建整个树
		List<String> pids = new ArrayList<String>();
		Collections.addAll(pids, new String[]{"MNU_0", "FUN_F-GLOBAL"});
		while(pids.size() > 0) {
			List<Map> tmpData = new ArrayList<Map>();
			List<String> tmpPids = new ArrayList<String>();
			for (Map item : data) {
				String privName = (String) item.get("PRIVNAME");
				String privId = (String) item.get("PRIVID");
				String privCat = (String) item.get("PRIVCAT");
				String privFrom = (String) item.get("PRIVFROM");
				String subId = (String) item.get("SUBID");
				String subName = (String) item.get("SUBNAME");
				String pMenuId = (String) item.get("PMENUID");
				String id = (privCat.equals("menu") ? "MNU_" : "FUN_") + privId;
				String pid = "MNU_" + pMenuId;
				if(pids.contains(pid)){
					PrivEuTreeNode pNode = mapping.get(pid);
					if(pNode == null){
						continue;
					}
					PrivEuTreeNode node = mapping.get(id);
					if(node == null) {
						node = new PrivEuTreeNode(privName, id);
						mapping.put(id, node);
						pNode.addChildren(node);
					}
					PrivOrigin origin = new PrivOrigin(privFrom, subId, subName);
					node.addOrigin(origin);
					tmpPids.add(id);
				}else{
					tmpData.add(item);
				}
			}
			data = tmpData;
			pids = tmpPids;
		}
		return ret;
	}
}
