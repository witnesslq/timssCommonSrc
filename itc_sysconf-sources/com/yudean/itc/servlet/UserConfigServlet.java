package com.yudean.itc.servlet;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.yudean.itc.dao.sec.SecureUserMapper;
import com.yudean.itc.SecurityBeanHelper;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.yudean.itc.code.StatusCode;
import com.yudean.itc.dto.Page;
import com.yudean.itc.dto.sec.Organization;
import com.yudean.itc.dto.sec.Role;
import com.yudean.itc.dto.sec.SecureUser;
import com.yudean.itc.dto.sec.SecureUserGroup;
import com.yudean.itc.manager.sec.ISecurityMaintenanceManager;
import com.yudean.itc.util.Constant;
import com.yudean.itc.util.MD5;

@SuppressWarnings("serial")
public class UserConfigServlet extends BaseServlet {
	private ISecurityMaintenanceManager secManager;
	private static Logger logger = Logger.getLogger(UserServlet.class);
	private SecureUserMapper secUserMapper;

	@Override
    public void init() throws ServletException {             
		super.init();     
		secManager = getMtManager();
		SecurityBeanHelper helper = SecurityBeanHelper.getInstance();
		secUserMapper = helper.getBean(SecureUserMapper.class);
	} 
	
	@Override
    protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String method = request.getParameter("method");
		long t1 = System.currentTimeMillis();
		if (method == null) {
			return;
		}
		HttpSession session = request.getSession();
		if (method.equals("getusers")) {
			getUsers(request, response);
		} else if (method.equals("getuser")) {
			getUser(request, response, session);
		} else if (method.equals("edit")) {
			editOrNewUser(request, response);
		} else if (method.equals("deluser")) {
			deleteUser(request, response);
		} else if (method.equals("create")) {
			editOrNewUser(request, response);
		} else if (method.equals("userorg")) {
			showSelectOrgDialog(request, response);
		} else if (method.equals("setstat")) {
			setUserActiveStatus(request, response);
		} else if (method.equals("exist")) {
			isUserCodeExist(request, response);
		}
		long t2 = System.currentTimeMillis();
		long delta = t2 - t1;
		if(delta>100){
			logger.info("servlet=UserServlet,method = " + method + ",timecost = " + delta);
		}
	}

	/**
	 * 列出某一站点（管理页面大表）下的用户
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void getUsers(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String filter = trimStrToNull(request.getParameter("filter"));
		String filterType = trimStrToNull(request.getParameter("filterType"));
		String onlyActive = trimStrToNull(request.getParameter("onlyActive"));
		// 分页器
		Page<SecureUser> page = getPager(request);
		SecureUser operator = (SecureUser) request.getSession().getAttribute(
				Constant.secUser);
		Page<SecureUser> qResult = null;
		
		if(onlyActive!=null){
			page.setParameter("userStatus", StatusCode.YES);
		}
		
		if(filter!=null&&filterType!=null){
			if(filterType.equals("org")){
				//按组织过滤
				page.setParameter("orgCode", filter);
			}
			else if(filterType.equals("person")){
				//按姓名搜索
				filter = URLDecoder.decode(filter, "UTF-8");
				page.setParameter("searchBy", filter);
			}
		}
		qResult = secManager.retrieveUniqueUsers(page, operator);
		
		// JSON输出结果
		HashMap<String, Object> result = wrapResultWithPage(qResult);
		// 取出查询结果的一部分显示在表格中
		List<SecureUser> objResult = qResult.getResults();
		ArrayList<HashMap<String,Object>> rows = new ArrayList<HashMap<String,Object>>();
		for (int i = 0; i < objResult.size(); i++) {
			SecureUser o = objResult.get(i);
			HashMap<String,Object> row = new HashMap<String, Object>();
			row.put("uid", o.getId());
			row.put("name", o.getName());
			row.put("job", o.getJob());
			row.put("mail", o.getEmail());
			row.put("type", o.getSyncInd());
			row.put("status", o.getActive());
			rows.add(row);
		}
		result.put("rows", rows);
		outputJson(response, result);
	}

	/**
	 * 获取某一 UID对应的用户
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void getUser(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		String uid = request.getParameter("uid");
		String mode = request.getParameter("mode");
		if (uid == null && !mode.equals("create")) {
			return;
		}
		request.setAttribute("mode", mode);
		if (mode.equals("edit") || mode.equals("view")) {
			// 只有编辑和浏览模式下才需要读取被编辑用户的信息
			SecureUser user = secManager.retrieveUserWithDetails(uid,
					(SecureUser) session.getAttribute(Constant.secUser));
			// 用户基本信息
			HashMap<String,Object> g = new HashMap<String, Object>(); 
			g.put("uid", user.getId());
			g.put("email", user.getEmail());
			g.put("name", user.getName());
			g.put("title", user.getTitle());
			g.put("mobile", user.getMobile());
			g.put("microtel", user.getMicroTel());
			g.put("officetel", user.getOfficeTel());
			g.put("job", user.getJob());
			g.put("status", user.getActive());
			g.put("type", user.getSyncInd());
			g.put("arrdate",user.getArrivalDateAsLong());
			g.put("resdate", user.getResignDateAsLong());
			g.put("officeaddr", user.getOfficeAddr());
			// 角色
			List<Role> roles = user.getRoles();
			HashMap<String, String> hmRoles = new HashMap<String, String>();
			for (int i = 0; i < roles.size(); i++) {
				Role r = roles.get(i);
				if(r.getIsInherited()==StatusCode.NO){
					hmRoles.put(r.getId(), r.getName());
				}
				else{
					hmRoles.put(r.getId(), r.getName() + "(继承)");
				}
			}
			g.put("roles", hmRoles);
			// 用户组
			List<SecureUserGroup> groups = user.getGroups();
			HashMap<String, String> hmGroups = new HashMap<String, String>();
			for (int i = 0; i < groups.size(); i++) {
				SecureUserGroup grp = groups.get(i);
				hmGroups.put(grp.getId(), grp.getName());
			}
			g.put("groups", hmGroups);
			// 组织机构列表
			List<Organization> orgs = user.getOrganizations();
			HashMap<String, String> hmOrgs = new HashMap<String, String>();
			for (int i = 0; i < orgs.size(); i++) {
				Organization o = orgs.get(i);
				hmOrgs.put(o.getCode(), o.getName());
			}
			g.put("orgs", hmOrgs);
			request.setAttribute("g", JSONObject.fromObject(g).toString());
		}
		RequestDispatcher dispatcher = request
				.getRequestDispatcher(Constant.jspPath + "/edit_user.jsp");
		dispatcher.forward(request, response);
	}

	/**
	 * 从post中获取用户基本信息
	 * @param request
	 * @return
	 */
	private SecureUser getUserData(HttpServletRequest request) {
		SecureUser user = null;
		// 基础信息
		String uid = trimStrToNull(request.getParameter("uid"));
		String method = request.getParameter("method");
		ISecurityMaintenanceManager manager = null;
		if (uid != null && !method.equals("create")) {
			manager = getMtManager();
			user = manager.retrieveUserWithDetails(uid, (SecureUser) request
					.getSession().getAttribute(Constant.secUser));
			if (user == null) {
				return null;
			}
		} else {
			user = new SecureUser();
		}
		user.setId(uid);
		user.setName(request.getParameter("name"));
		user.setTitle(request.getParameter("title"));
		user.setEmail(request.getParameter("email"));
		user.setMobile(request.getParameter("mobile"));
		user.setOfficeTel(request.getParameter("officetel"));
		user.setMicroTel(request.getParameter("microtel"));
		user.setJob(request.getParameter("job"));
		user.setArrivalDate(trimStrToNull(request.getParameter("arrdate")));
		user.setResignDate(trimStrToNull(request.getParameter("resdate")));
		user.setOfficeAddr(request.getParameter("officeaddr"));
		//2016.5.4加入同步状态更新
		String syncCode = trimStrToNull(request.getParameter("type"));
		user.setSyncInd(StatusCode.valueByCode(syncCode != null ? syncCode : "NO"));
		return user;
	}

	/**
	 * 删除用户（实际执行）
	 * 
	 * @param uid
	 *            要删除用户的ID
	 * @param request
	 * @param response
	 * @param noDirectOutput
	 *            不直接输出结果（批量删除时使用）
	 * @return
	 */
	private boolean delUser(String uid, HttpServletRequest request,
			HttpServletResponse response, Boolean noDirectOutput) {
		ISecurityMaintenanceManager manager = getMtManager();
		SecureUser opUser = (SecureUser) request.getSession().getAttribute(
				Constant.secUser);
		if (opUser.getId().equals(uid)) {
			if (noDirectOutput == false) {
				outputStatus(response, -1, "无法删除当前登陆的用户！");
			}
			return false;
		}
		SecureUser delUser = manager.retrieveUserById(uid);
		if (delUser == null) {
			if (noDirectOutput == false) {
				outputStatus(response, -1, "用户不存在，可能该用户已经被删除");
			}
			return false;
		}
		manager.deleteUser(uid, opUser);
		return true;
	}

	private void deleteUser(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String uid = request.getParameter("uid");
		if (uid == null) {
			return;
		}
		if(delUser(uid, request, response, false)){
			outputStatus(response, 1, "用户删除成功");
		}
		
	}

	/**
	 * 新建或者保存用户信息
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void editOrNewUser(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String act = request.getParameter("method");
		if (!act.equals("create") && !act.equals("edit")) {
			return;
		}
		SecureUser user = getUserData(request);
		if (user == null) {
			return;
		}
		SecurityBeanHelper helper = SecurityBeanHelper.getInstance();
		ISecurityMaintenanceManager manager = helper
				.getBean(ISecurityMaintenanceManager.class);
		SecureUser operator = (SecureUser) request.getSession().getAttribute(
				Constant.secUser);
		if (act.equals("create")) {
			try {
				user.setActive(StatusCode.YES);
				user.setPassword(MD5.GetMD5Code(user.getId()));
				manager.createUserWithDetails(user, operator);
			} catch (Exception e) {
				logger.error("Error when create user:", e);
				outputStatus(response, -1, "用户创建失败");
				return;
			}
		} else if (act.equals("edit")) {
			try {
				manager.updateUser(user, operator);
			} catch (Exception e) {
				logger.error("Error when edit user:", e);
				outputStatus(response, -1, "用户资料修改失败");
				return;
			}
		}
		//---------用户组、角色、组织-------------
		//判断是否需要将用户加入某组织中
		String orgNew = trimStrToNull(request.getParameter("orgs_add"));
		if(orgNew!=null){
			String[] orgAdd = orgNew.split(",");
			try{
				secManager.addUserToOrganizations(user.getId(), orgAdd, operator);
			}
			catch(Exception ex){
				logger.error("Error when update org info(1)", ex);
				outputStatus(response, -1, "在更新用户组织机构时出现异常");
				return;
			}
		}
		String orgDel = trimStrToNull(request.getParameter("orgs_del"));
		if(orgDel!=null){
			String[] orgRmv = orgDel.split(",");
			try{
				secManager.removeUserFromOrganizations(user.getId(), orgRmv, operator);
			}
			catch(Exception ex){
				logger.error("Error when update org info(2)", ex);
				outputStatus(response, -1, "在更新用户组织机构时出现异常");
				return;
			}
		}
		//为用户加入/删除 角色
		String roleNew = trimStrToNull(request.getParameter("roles_add"));
		if(roleNew!=null){
			String[] roleSplit = roleNew.split(",");
			try{
				for(String role:roleSplit){
					secManager.assignRoleToUser(role, user.getId(), operator);
				}
			}
			catch(Exception ex){
				logger.error("Error when update role info", ex);
				outputStatus(response, -1, "在更新用户角色信息时出现异常");
				return;
			}
		}
		String roleDel = trimStrToNull(request.getParameter("roles_del"));
		if(roleDel!=null){
			String[] roleSplit = roleDel.split(",");
			try{
				for(String role:roleSplit){
					secManager.removeRoleFromUser(role, user.getId(), operator);
				}
			}
			catch(Exception ex){
				logger.error("Error when update role info", ex);
				outputStatus(response, -1, "在更新用户角色信息时出现异常");
				return;
			}
		}
		//为用户加入/删除用户组
		String grpNew = trimStrToNull(request.getParameter("groups_add"));
		if(grpNew!=null){
			String[] grpSplit = grpNew.split(",");
			try{
				for(String grp:grpSplit){
					secManager.addUserToGroup(user.getId(), grp, operator);
				}
			}
			catch(Exception ex){
				logger.error("Error when update group info", ex);
				outputStatus(response, -1, "在更新用户组信息时出现异常");
				return;
			}
		}
		String grpDel = trimStrToNull(request.getParameter("groups_del"));
		if(grpDel!=null){
			String[] grpSplit = grpDel.split(",");
			try{
				for(String grp:grpSplit){
					secManager.removeUserFromGroup(user.getId(), grp, operator);
				}
			}
			catch(Exception ex){
				outputStatus(response, -1, "在更新用户组信息时出现异常");
				logger.error("Error when update group info", ex);
				return;
			}
		}
		if (act.equals("create")) {
			outputStatus(response, 1, "新建用户成功");
		} else if (act.equals("edit")) {
			outputStatus(response, 1, "编辑用户成功");
		}
	}

	
	/**
	 * 显示选择组织的对话框
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void showSelectOrgDialog(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String uid = request.getParameter("uid");
		if (uid == null) {
			return;
		}
		RequestDispatcher dispatcher = request
				.getRequestDispatcher("jsp/select_user_org.jsp?uid=" + uid);
		dispatcher.forward(request, response);
	}

	/**
	 * 设置一组用户的状态为启用或者禁用
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void setUserActiveStatus(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String ids = trimStrToNull(request.getParameter("ids"));
		StatusCode statCode = request.getParameter("stat").equals("1") ? StatusCode.YES
				: StatusCode.NO;
		if (ids == null) {
			outputStatus(response, -1, "操作失败，参数非法");
			return;
		}
		String[] idArray = ids.trim().split(" ");
		ISecurityMaintenanceManager manager = getMtManager();
		for(String id:idArray){
			manager.updateUserStatus(id, statCode);
		}
		outputStatus(response, 1, "操作成功");
	}

	/**
	 * 判断某个用户编码是否可用
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void isUserCodeExist(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String uid = trimStrToNull(request.getParameter("uid"));
		if(uid==null){
			return;
		}
		Map result = secUserMapper.selectDelInd(uid);
		if(result == null){
			outputMsg(response, "true");
		}else{
			String delInd = (String)result.get("DELIND");
			if(delInd!=null && delInd.equals("Y")){
				outputMsg(response, "{\"msg\":\"该用户已被删除，请联系管理员恢复其账号\"}");
			}
			else{
				
				outputMsg(response, "{\"msg\":\"该用户名已被占用\"}");
			}
		}
	}
	
}
