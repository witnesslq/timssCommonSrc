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

import org.apache.log4j.Logger;

import com.yudean.itc.SecurityBeanHelper;
import com.yudean.itc.dao.sec.OrganizationMapper;
import com.yudean.itc.dto.Page;
import com.yudean.itc.dto.sec.Role;
import com.yudean.itc.dto.sec.SecureUser;
import com.yudean.itc.exception.sec.AuthorizationException;
import com.yudean.itc.manager.sec.ISecurityMaintenanceManager;
import com.yudean.itc.security.service.impl.BaseService;
import com.yudean.itc.security.service.impl.RoleService;
import com.yudean.itc.util.Constant;
import com.yudean.itc.util.DateHelper;

public class RoleServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(RoleServlet.class);
	private ISecurityMaintenanceManager secManager;
	private OrganizationMapper orgMapper;
	public RoleServlet() {
		super();

	}
	
	@Override
    public void init() throws ServletException {
		secManager = getMtManager();
		orgMapper = SecurityBeanHelper.getInstance().getBean(OrganizationMapper.class);
	}

	@Override
    protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String method = request.getParameter("method");
		long t1 = System.currentTimeMillis();
		if (method.equals("userrole")) {
			getAvaliableRolesByUser(request, response);
		} else if (method.equals("getroles")) {
			getRolesBySite(request, response);
		} else if (method.equals("getrole")) {
			getRoleById(request, response);
		} else if (method.equals("edit")) {
			saveOrCreateRole(request, response);
		} else if (method.equals("create")){
			saveOrCreateRole(request, response);
		} else if(method.equals("delroles")){
			deleteRoles(request,response);
		} else if(method.equals("exist")){
			exist(request,response);
		} else if(method.equals("listroles")){
			listAllRolesWithFilter(request, response);
		} else if(method.equals("userorg")){
			getUsersOrg(request,response);
		}
		long t2 = System.currentTimeMillis();
		long delta = t2 - t1;
		if(delta>100){
			logger.info("servlet=RoleServlet,method = " + method + ",timecost = " + delta);
		}
	}

	/**
	 * 获取用户可以添加的角色（已有的不会显示）
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws ServletException
	 */
	private void getAvaliableRolesByUser(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// 用户已经拥有的角色
		HashMap<String, Boolean> hm = buildOwndMap(request);
		// manager初始化
		ISecurityMaintenanceManager manager = getMtManager();
		// 获取当前站点下可用的角色
		SecureUser operator = (SecureUser) request.getSession().getAttribute(
				Constant.secUser);
		Page<Role> page = new Page<Role>();
		// 这里需要手动设置每页的记录数 否则会取默认一页20条数据
		page.setPageSize(99999);
		Page<Role> rResult = manager.retrieveRoles(page, operator);
		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put("totalCount", rResult.getTotalRecord());
		result.put("currPage", 0);
		// 取得角色列表可以显示的信息
		ArrayList<ArrayList<Object>> rows = new ArrayList<ArrayList<Object>>();
		List<Role> roles = rResult.getResults();
		for (int i = 0; i < roles.size(); i++) {
			Role r = roles.get(i);
			ArrayList<Object> row = new ArrayList<Object>();
			String roleId = r.getId();
			if (hm.containsKey(roleId)) {
				continue;
			}
			row.add(roleId);
			row.add(r.getName());
			rows.add(row);
		}
		result.put("data", rows);
		outputJson(response, result);
	}

	/**
	 * 列出所有的角色（带有用户过滤）
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void listAllRolesWithFilter(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String filterStr = trimStrToNull(request.getParameter("filter"));
		SecureUser operator = (SecureUser) request.getSession().getAttribute(Constant.secUser);
		HashMap<String,Boolean> filter = new HashMap<String, Boolean>();
		if(filterStr!=null){
			String[] filterArr = filterStr.split(",");
			for(String s:filterArr){
				filter.put(s, true);
			}
		}
		Page<Role> roles = new Page<Role>();
		roles.setPageSize(9999);
		roles = secManager.retrieveRoles(roles, operator);
		List<Role> roleList = roles.getResults();
		ArrayList<HashMap<String,String>> result = new ArrayList<HashMap<String,String>>();
		for(Role role:roleList){
			if(filter.containsKey(role.getId())){
				continue;
			}
			HashMap<String,String> row = new HashMap<String, String>();
			row.put("name",role.getName());
			row.put("id", role.getId());
			result.add(row);
		}
		HashMap<String,Object> fResult = new HashMap<String, Object>();
		fResult.put("rows", result);
		fResult.put("total", roles.getTotalRecord());
		outputJson(response, fResult);
	}

	/**
	 * 获取某站点下的所有角色
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void getRolesBySite(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Page<Role> page = getPager(request);
		SecureUser operator = (SecureUser) request.getSession().getAttribute(
				Constant.secUser);
		String kw = trimStrToNull(request.getParameter("filter"));
		if(kw!=null){
			kw = URLDecoder.decode(kw, "UTF-8");
			page.setParameter("searchBy", kw.toLowerCase());
		}
		Page<Role> qResult = secManager.retrieveRoles(page, operator);
		List<Role> roles = qResult.getResults();
		HashMap<String, Object> result = wrapResultWithPage(qResult);
		ArrayList<HashMap> data = new ArrayList<HashMap>();
		for (int i = 0; i < roles.size(); i++) {
			HashMap row = new HashMap();
			Role role = roles.get(i);
			row.put("id",role.getId());
			row.put("name",role.getName());
			row.put("updateby", role.getUpdatedBy()==null?role.getCreatedBy():role.getUpdatedBy());
			row.put("updatetime", DateHelper.formatTimeAtMin(role.getUpdateTime()==null?role.getCreateTime():role.getUpdateTime()));
			data.add(row);
		}
		result.put("total", qResult.getTotalRecord());
		result.put("rows", data);
		outputJson(response, result);
	}

	/**
	 * 根据ID获取角色的信息（用于显示或者编辑）
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void getRoleById(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String rid = request.getParameter("rid");
		String mode = request.getParameter("mode");
		request.setAttribute("mode", mode);
		if (rid == null && !mode.equals("create")) {
			return;
		}
		if (!mode.equals("create")) {
			SecureUser operator = (SecureUser) request.getSession()
					.getAttribute(Constant.secUser);
			if(!RoleService.wrapRoleForm(rid, request, response, operator, secManager)){
				outputMsg(response, "在获取角色信息时出现错误，请联系管理员处理");
				return;
			}
			
		}
		RequestDispatcher dispatcher = request
				.getRequestDispatcher(Constant.jspPath + "edit_role.jsp");
		dispatcher.forward(request, response);
	}

	/**
	 * 保存或者创建一个角色
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void saveOrCreateRole(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String rid = request.getParameter("rid");
		String method = request.getParameter("method");
		if (rid == null && !method.equals("create")) {
			outputStatus(response, -1, "参数不完整或者错误");
			return;
		}
		SecureUser operator = (SecureUser) request.getSession().getAttribute(
				Constant.secUser);
		Role role = RoleService.parseRoleForm(request, response, operator,secManager);
		if(role==null){
			outputMsg(response, "在处理角色时发生错误，请联系管理员处理");
			return;
		}
		role.setSiteId(operator.getCurrentSite());
		//具体执行创建还是更新动作
		if (method.equals("create")) {
			try {
				secManager.createRoleWithDetails(role, operator);
			} catch (AuthorizationException e) {
				outputStatus(response, -1, "您没有权限创建新角色");
				return;
			}
		} else if (method.equals("edit")) {
			try {
				secManager.updateRole(role, operator);
			} catch (AuthorizationException e) {
				outputStatus(response, -1, "您没有权限修改角色信息");
				return;
			}
		}
		RoleService.parseRoleForm2(request, response, operator,secManager);
		if(method.equals("create")){
			outputStatus(response, 1, "角色新建成功");
		}
		else{
			outputStatus(response, 1, "角色信息修改成功");
		}
	}

	private void deleteRoles(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String rids = trimStrToNull(request.getParameter("rids"));
		if (rids == null) {
			return;
		}
		String[] ridSplit = rids.split(" ");
		ISecurityMaintenanceManager manager = getMtManager();
		SecureUser operator = (SecureUser) request.getSession().getAttribute(
				Constant.secUser);
		try {
			manager.deleteRoles(ridSplit, operator);
		} catch (AuthorizationException e) {
			outputStatus(response, -1, "您没有删除角色的权限");
		}
		outputStatus(response, 1, "角色删除成功");
	}
	
	/**
	 * 判断一个角色是否存在
	 * @param request
	 * @param response
	 */
	private void exist(HttpServletRequest request,
			HttpServletResponse response) {
		SecureUser operator = (SecureUser) request.getSession().getAttribute(
				Constant.secUser);
		String rid = trimStrToNull(request.getParameter("rid"));
		if(rid == null){
			outputStatus(response, -1, "非法的参数");
			return;
		}
		Role role = secManager.retrieveRoleById(rid, operator);
		if(role != null){
			outputMsg(response, null);
			return;
		}
		outputMsg(response, "true");
	}

	private void getUsersOrg(HttpServletRequest request,
			HttpServletResponse response) {
		String ids = trimStrToNull(request.getParameter("ids"));
		if(ids==null){
			outputMsg(response, "[]");
			return;
		}
		String[] arr = ids.split(",");
		List<Map> result = orgMapper.selectOrgsRelatedToUsers(arr);
		outputJson(response, BaseService.getParentOrgs(result, secManager));
	}
}