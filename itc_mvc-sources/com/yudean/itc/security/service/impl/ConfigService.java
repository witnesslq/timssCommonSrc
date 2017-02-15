package com.yudean.itc.security.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.yudean.itc.dao.sec.FrontRouteMapper;
import com.yudean.itc.dto.sec.FrontRoute;
import net.sf.json.JSONArray;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yudean.itc.code.MenuType;
import com.yudean.itc.dao.sec.SecureMenuMapper;
import com.yudean.itc.dto.sec.SecureMenu;
import com.yudean.itc.dto.sec.SecureUser;
import com.yudean.itc.manager.sec.IAuthenticationManager;
//import com.yudean.itc.manager.sec.ISecurityMaintenanceManager;
import com.yudean.itc.util.json.JsonHelper;

@Service
public class ConfigService{
	// @Autowired
	// private ISecurityMaintenanceManager secManager;
	@Autowired
	private IAuthenticationManager authManager;
	@Autowired
	private SecureMenuMapper secMnuMapper;
	@Autowired
	private FrontRouteMapper frontRouteMapper;

	private Logger logger = Logger.getLogger(ConfigService.class);

	public String wrapITCFrameConfig() {
		logger.info("正在生成后台主框架js配置文件.....");
		// 0是所有菜单的根节点
		List<SecureMenu> mnus = secMnuMapper.selectSubMenus("0", null, SecureMenu.FRAMEOBJ_NAVTAB, true);
		// 选项卡组
		ArrayList<HashMap<String, Object>> tabs = new ArrayList<HashMap<String, Object>>();
		// 选项卡映射
		HashMap<String, Object> tabMapping = new HashMap<String, Object>();
		// 树跳转映射
		HashMap<String, String> treeMapping = new HashMap<String, String>();
		for (int i = 0; i < mnus.size(); i++) {
			SecureMenu navTab = mnus.get(i);
			HashMap<String, Object> tab = new HashMap<String, Object>();
			HashMap<String, Object> tabMap = new HashMap<String, Object>();
			tab.put("name", navTab.getName());
			tab.put("id", navTab.getId());
			tab.put("sortNum", navTab.getSortNum());
			tabMap.put("privilege", navTab.getId());
			tabMap.put("id", "navtab_" + navTab.getId());
			if (navTab.getNumberOfSubMenu() != null && navTab.getNumberOfSubMenu() > 0) {
				// 导航树
				ArrayList<HashMap<String, Object>> navTree = new ArrayList<HashMap<String, Object>>();
				List<SecureMenu> pNodes = secMnuMapper.selectSubMenus(navTab.getId(), MenuType.LEFT.toString(), SecureMenu.FRAMEOBJ_NAVTREE, true);
				for (int j = 0; j < pNodes.size(); j++) {
					SecureMenu pNode = pNodes.get(j);
					// 导航树根节点
					HashMap<String, Object> pNodeMap = new HashMap<String, Object>();
					pNodeMap.put("grouptitle", pNode.getName());
					pNodeMap.put("privilege", pNode.getId());
					if (pNode.getNumberOfSubMenu() != null && pNode.getNumberOfSubMenu() > 0) {
						// 二级节点
						pNodeMap.put("initexpand", true);
						ArrayList<HashMap<String, Object>> childrenNode = new ArrayList<HashMap<String, Object>>();
						List<SecureMenu> cNodes = secMnuMapper.selectSubMenus(pNode.getId(), MenuType.LEFT.toString(), SecureMenu.FRAMEOBJ_NAVTREE,
								true);
						for (int k = 0; k < cNodes.size(); k++) {
							SecureMenu cNode = cNodes.get(k);
							// 二级节点必须有一个URL 否则这个节点有什么用？
							if (cNode.getUrl() == null) {
								logger.warn("对于二级导航树节点“" + cNode.getName() + "”没有指定url，这可能会导致导航树行为异常");
							} else {
								String nodeId = navTab.getId() + "_" + cNode.getId();
								HashMap<String, Object> cNodeMap = new HashMap<String, Object>();
								cNodeMap.put("id", nodeId);
								// cNodeMap.put("privilege", nodeId);
								cNodeMap.put("privilege", cNode.getId());
								System.out.println(cNode.getId());
								cNodeMap.put("title", cNode.getName());
								treeMapping.put(nodeId, cNode.getUrl());
								childrenNode.add(cNodeMap);
							}
						}
						pNodeMap.put("items", childrenNode);
					} else {
						String nodeId = navTab.getId() + "_" + pNode.getId();
						pNodeMap.put("id", nodeId);
						treeMapping.put(nodeId, pNode.getUrl());
					}
					navTree.add(pNodeMap);
				}
				tabMap.put("tree", navTree);
			} else {
				if (navTab.getUrl() == null) {
					logger.error("对于导航选项卡节点“" + navTab.getName() + "”即没有导航树节点，也无URL，这会导致框架初始化异常");
				} else {
					tabMap.put("url", navTab.getUrl());
				}
			}
			tabMapping.put(navTab.getId(), tabMap);
			tabs.add(tab);
		}
		HashMap<String, Object> configObj = new HashMap<String, Object>();
		configObj.put("tabs", tabs);
		configObj.put("tabMapping", tabMapping);
		configObj.put("treeMapping", treeMapping);
		String str = JsonHelper.toJsonString(configObj);
		logger.info("配置文件生成完毕，长度=" + str.length());
		return str;
	}

	public String wrapUserAuthConfig(SecureUser user) {
		if (user == null) {
			return null;
		}
		logger.debug("动态获取用户权限，用户名 = " + user.getId());
		// 如果用户没有权限信息则需要重新装配 一般情况下不需要 拿到的SecUser已经装配好了 但是测试需要
		if (user.getPrivileges() == null) {
			authManager.assemblePrivileges(user, user.getCurrentSite());
		}
		HashMap<String, Integer> output = new HashMap<String, Integer>();
		for (String s : user.getPrivileges()) {
			output.put(s, 1);
		}
		return JsonHelper.toJsonString(output);
	}
	
	public String getOwnedTabs(){		
		List<SecureMenu> mnus = secMnuMapper.selectSubMenus("0", null,SecureMenu.FRAMEOBJ_NAVTAB,true);
		List<String[]> result = new ArrayList<String[]>();
		if(mnus != null){
			for(SecureMenu menu:mnus){
				String[] item = new String[2];
				item[0] = menu.getId();
				item[1] = menu.getName();
				result.add(item);
			}
		}
		return JSONArray.fromObject(result).toString();
	}

	public String wrapFrontRouteConfig(SecureUser user){
		List<FrontRoute> routes = frontRouteMapper.selectAllRoute(user.getCurrentSite());
		//在站点前后和站点列表前后加空格做匹配 如" ITC SJC "匹配" SJC "
		String currSite = " " + user.getCurrentSite() + " ";
		List<FrontRoute> filteredRoutes = new ArrayList<FrontRoute>();
		for(int i=0;i<routes.size();i++){
			FrontRoute route = routes.get(i);
			//权限过滤
			String privilege = route.getRequirePrivilege();
			boolean matchPriv = true;
			if(!privilege.equals("*")){
				String[] privArr = privilege.split("\\s+");
				for(String priv: privArr){
					if(!user.isPrivOwnedByUser(priv)){
						matchPriv = false;
						break;
					}
				}
			}
			if(matchPriv){
				filteredRoutes.add(route);
			}
		}
		String str = JsonHelper.toJsonString(filteredRoutes);
		return "window._route = JSON.parse('" + str + "');";
	}
}
