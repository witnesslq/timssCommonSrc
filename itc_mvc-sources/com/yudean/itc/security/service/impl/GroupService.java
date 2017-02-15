package com.yudean.itc.security.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import com.yudean.itc.dto.sec.Role;
import com.yudean.itc.dto.sec.SecureUser;
import com.yudean.itc.dto.sec.SecureUserGroup;
import com.yudean.itc.exception.sec.AuthorizationException;
import com.yudean.itc.manager.sec.ISecurityMaintenanceManager;
import com.yudean.itc.util.DateHelper;

public class GroupService extends BaseService{
	public static boolean wrapGroupForm(String gid,HttpServletRequest request,
			HttpServletResponse response, SecureUser operator,
			ISecurityMaintenanceManager manager) throws ServletException,
			IOException {
		SecureUserGroup group = null;
		try{
			group = manager.retriveGroupWithDetails(gid, operator);
		}
		catch(Exception ex){
			ex.printStackTrace();
			return false;
		}
		HashMap<String,Object> g = new HashMap<String,Object>();
		g.put("gid", group.getId());
		g.put("name", group.getName());
		g.put("lastmodify",
				DateHelper.formatTimeAtMin(group.getUpdateTime()==null?group.getCreateTime():group.getUpdateTime()));
		g.put("lastmodifyuser", group.getUpdatedBy()==null?group.getCreatedBy():group.getUpdatedBy());
		g.put("sid", group.getSiteId());
		g.put("reserved", group.getReserved());
		//用户组包含的角色
		List<Role> roles = group.getRoles();
		HashMap<String,String> rolesMap = new HashMap<String, String>();
		for(Role role:roles){
			rolesMap.put(role.getId(), role.getName());
		}
		g.put("roles", rolesMap);
		//用户组包含的用户
		List<SecureUser> users = group.getUsers();
		HashMap<String,String> usersMap = new HashMap<String, String>();//用于分析新增还是删除用户的过滤器
		for(SecureUser user:users){
			usersMap.put(user.getId(), user.getName());
		}
		g.put("users", usersMap);
		g.put("relatedorgs",GroupService.getOrgsReleatedToUsers(group.getId(), manager));
		request.setAttribute("g", JSONObject.fromObject(g).toString());
		return true;
	}
	
	public static SecureUserGroup parseGroupForm(HttpServletRequest request,
			HttpServletResponse response, SecureUser operator,
			ISecurityMaintenanceManager manager) throws ServletException,
			IOException {
		SecureUserGroup group = new SecureUserGroup();
		group.setId(request.getParameter("gid"));
		group.setName(request.getParameter("name"));
		group.setSiteId((String) request.getSession().getAttribute("currsite"));		
		return group;
	}
	
	public static boolean parseGroupForm2(HttpServletRequest request,
			HttpServletResponse response, SecureUser operator,
			ISecurityMaintenanceManager manager) throws ServletException,
			IOException {
		String gid = request.getParameter("gid");
		// 用户组包含的用户
		String idNew = trimStrToNull(request.getParameter("users_add"));
		String idDel = trimStrToNull(request.getParameter("users_del"));
		if (idNew != null) {
			String[] newSplit = idNew.trim().split(",");
			try {				
				manager.addUsersToGroup(newSplit, gid, operator);
			} catch (AuthorizationException e) {
				e.printStackTrace();
				return false;
			}
		}
		if (idDel != null) {
			String[] delSplit = idDel.trim().split(",");
			try {
				manager.removeUsersFromGroup(delSplit,gid, operator);
			} catch (AuthorizationException e) {
				e.printStackTrace();
				return false;
			}
		}
		// 用户组包含的角色
		String roleNew = trimStrToNull(request.getParameter("roles_add"));
		String roleDel = trimStrToNull(request.getParameter("roles_del"));
		if(roleNew!=null){
			String[] newSplit = roleNew.trim().split(",");
			try {
				manager.assignRolesToGroup(newSplit, gid, operator);
			} catch (AuthorizationException e) {
				e.printStackTrace();
				return false;
			}
		}
		if(roleDel!=null){
			String[] delSplit = roleDel.trim().split(",");
			try {
				manager.removeRolesFromGroup(delSplit, gid, operator);
			} catch (AuthorizationException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	@SuppressWarnings("rawtypes")
	public static HashMap<String,Boolean> getOrgsReleatedToUsers(String group,ISecurityMaintenanceManager manager){
		List<Map> result1 = manager.selectOrgsRelatedToGroup(group);		
		return getParentOrgs(result1, manager);
	}
}
