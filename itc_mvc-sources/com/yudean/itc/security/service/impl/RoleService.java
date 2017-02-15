package com.yudean.itc.security.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import com.yudean.itc.code.PrivilegeType;
import com.yudean.itc.code.StatusCode;
import com.yudean.itc.dto.sec.Privilege;
import com.yudean.itc.dto.sec.Role;
import com.yudean.itc.dto.sec.SecureUser;
import com.yudean.itc.dto.sec.SecureUserGroup;
import com.yudean.itc.exception.sec.AuthorizationException;
import com.yudean.itc.manager.sec.ISecurityMaintenanceManager;
import com.yudean.itc.util.DateHelper;

public class RoleService extends BaseService {
	private static Privilege[] parsePrivString(String priv) {
		String[] privSplit = priv.split(",");
		Privilege[] pAdd = new Privilege[privSplit.length];
		for (int i = 0; i < privSplit.length; i++) {
			String s = privSplit[i];
			Privilege p = new Privilege();
			if (s.startsWith("MNU_")) {
				s = s.replace("MNU_", "");
				p.setCategory(PrivilegeType.MENU);
			} else if (s.startsWith("FUN_")) {
				s = s.replace("FUN_", "");
				p.setCategory(PrivilegeType.FUNCTION);
			}
			p.setId(s);
			pAdd[i] = p;
		}
		return pAdd;
	}

	public static Role parseRoleForm(HttpServletRequest request,
			HttpServletResponse response, SecureUser operator,
			ISecurityMaintenanceManager manager) throws ServletException,
			IOException {
		Role role = new Role();
		role.setId(trimStrToNull(request.getParameter("rid")));
		role.setName(trimStrToNull(request.getParameter("name")));
		role.setSiteId((String) request.getSession().getAttribute("currsite"));
		role.setActive(StatusCode.YES);
		return role;
	}
	
	public static void parseRoleForm2(HttpServletRequest request,
			HttpServletResponse response, SecureUser operator,
			ISecurityMaintenanceManager manager) throws ServletException,
			IOException {
		String rid = request.getParameter("rid");
		// 角色包含的用户
		String idNew = trimStrToNull(request.getParameter("users_add"));
		String idDel = trimStrToNull(request.getParameter("users_del"));
		if (idNew != null) {
			String[] newSplit = idNew.trim().split(",");
			try {				
				manager.assignRoleToUsers(rid, newSplit, operator);
			} catch (AuthorizationException e) {
				e.printStackTrace();
			}
		}
		if (idDel != null) {
			String[] delSplit = idDel.trim().split(",");
			try {
				manager.removeRoleFromUsers(rid, delSplit, operator);
			} catch (AuthorizationException e) {
				e.printStackTrace();
			}
		}
		// 角色包含的权限
		String privNew = trimStrToNull(request.getParameter("privs_add"));
		String privDel = trimStrToNull(request.getParameter("privs_del"));
		if (privNew != null) {
			Privilege[] pAdd = parsePrivString(privNew);
			try {
				manager.assignPrivilegesToRole(pAdd, rid, operator);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		if (privDel != null) {
			Privilege[] pDel = parsePrivString(privDel);
			try {
				manager.removePrivilegesFromRole(pDel, rid, operator);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		// 拥有该角色的用户组
		String grpNew = trimStrToNull(request.getParameter("groups_add"));
		String grpDel = trimStrToNull(request.getParameter("groups_del"));
		if(grpNew!=null){
			String[] grpNewArr = grpNew.split(","); 
			try{
				manager.assignRoleToGroups(rid, grpNewArr, operator);
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}
		if(grpDel!=null){
			String[] grpDelArr = grpDel.split(","); 
			try{
				manager.removeRoleFromGroups(rid, grpDelArr, operator);
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}
	
	public static boolean wrapRoleForm(String rid,HttpServletRequest request,
			HttpServletResponse response, SecureUser operator,
			ISecurityMaintenanceManager manager) throws ServletException,
			IOException {
		Role role = null;
		try {
			role = manager.retrieveRoleWithDetails(rid, operator);
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		if (role == null) {
			return false;
		}
		HashMap<String,Object> g = new HashMap<String, Object>();
		g.put("rid", role.getId());
		g.put("name", role.getName());
		g.put("lastmodify",
				DateHelper.formatTimeAtMin(role.getUpdateTime()==null?role.getCreateTime():role.getUpdateTime()));
		g.put("lastmodifyuser", role.getUpdatedBy()==null?role.getCreatedBy():role.getUpdatedBy());
		g.put("sid", role.getSiteId());
		g.put("reserved", role.getReserved());
		//获取该角色下拥有的用户
		List<SecureUser> users = role.getUsers();
		HashMap<String,String> usersMap = new HashMap<String, String>();//用于分析新增还是删除用户的过滤器
		for(SecureUser user:users){
			if(user.getHasInheritedRole()==StatusCode.YES){
				usersMap.put(user.getId(), user.getName() + "(继承)");
			}
			else{
				usersMap.put(user.getId(), user.getName());
			}
		}
		g.put("users", usersMap);
		//获取该角色拥有的权限
		List<Privilege> privs = role.getPrivileges();
		HashMap<String,String> privsMap = new HashMap<String, String>();
		for(Privilege priv:privs){
			if(priv.getCategory()==PrivilegeType.FUNCTION){
				privsMap.put("FUN_" + priv.getId(), priv.getName());
			}
			else{
				privsMap.put("MNU_" + priv.getId(), priv.getName());
			}
		}		
		g.put("privs", privsMap);
		//获取该角色拥有的用户组
		List<SecureUserGroup> groups = role.getGroups();
		HashMap<String,String> grpMap = new HashMap<String, String>();
		for(SecureUserGroup grp:groups){
			grpMap.put(grp.getId(), grp.getName());
		}
		g.put("groups", grpMap);
		Object o = getOrgsReleatedToUsers(role.getId(),manager);
		g.put("relatedorgs", o);
		request.setAttribute("g", JSONObject.fromObject(g).toString());
		return true;
	}
		
	
	@SuppressWarnings("rawtypes")
	public static HashMap<String,Boolean> getOrgsReleatedToUsers(String role,ISecurityMaintenanceManager manager){		
		List<Map> result1 = manager.selectOrgsRelatedToRole(role);
		return getParentOrgs(result1,manager);		
	}
}
