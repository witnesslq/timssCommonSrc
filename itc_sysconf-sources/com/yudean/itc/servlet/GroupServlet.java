package com.yudean.itc.servlet;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yudean.itc.dto.Page;
import com.yudean.itc.dto.sec.SecureUser;
import com.yudean.itc.dto.sec.SecureUserGroup;
import com.yudean.itc.exception.sec.AuthorizationException;
import com.yudean.itc.manager.sec.ISecurityMaintenanceManager;
import com.yudean.itc.security.service.impl.GroupService;
import com.yudean.itc.util.Constant;
import com.yudean.itc.util.DateHelper;

public class GroupServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;

	private ISecurityMaintenanceManager secManager;

	@Override
    public void init() throws ServletException {
		secManager = getMtManager();
	}
	
	@Override
    protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String method = request.getParameter("method");
		if (method == null) {
			return;
		}
		if (method.equals("getgroups")) {
			getGroupsBySite(request, response);
		} else if (method.equals("getgroup")) {
			getGroupById(request, response);
		} else if(method.equals("create")||method.equals("edit")){
			editOrNewGroup(request, response);
		}  else if (method.equals("delgroups")) {
			deleteGroups(request, response);
		} else if(method.equals("exist")){
			exist(request, response);
		} else if(method.equals("listgroup")){
			listGroupWithFilter(request, response);
		}
	}

		/**
	 * 获得当前站点下所有的用户组
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked"})
	private void getGroupsBySite(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Page<SecureUserGroup> page = getPager(request);
		SecureUser operator = (SecureUser) request.getSession().getAttribute(
				Constant.secUser);
		String kw = trimStrToNull(request.getParameter("filter"));
		if(kw!=null){
			kw = URLDecoder.decode(kw, "UTF-8");
			page.setParameter("searchBy", kw.toLowerCase());
		}
		Page<SecureUserGroup> qResult = secManager.retrieveGroups(page, operator);
		HashMap<String, Object> result = wrapResultWithPage(qResult);
		ArrayList<HashMap> rows = new ArrayList<HashMap>();
		List<SecureUserGroup> groups = qResult.getResults();
		for (int i = 0; i < groups.size(); i++) {
			HashMap row = new HashMap();
			SecureUserGroup group = groups.get(i);
			row.put("id",group.getId());
			row.put("name",group.getName());
			row.put("sid",group.getSiteId());
			row.put("updatetime",DateHelper.formatTimeAtMin(group.getUpdateTime()==null?group.getCreateTime():group.getUpdateTime()));
			row.put("updateby",group.getUpdatedBy() == null ? group.getCreatedBy() : group.getUpdatedBy());
			rows.add(row);
		}
		result.put("rows", rows);
		result.put("total", qResult.getTotalRecord());
		outputJson(response, result);
	}

	/**
	 * 编辑或者新建一个用户组
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void editOrNewGroup(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String method = request.getParameter("method");
		String gid = request.getParameter("gid");
		if(!method.equals("create") && gid==null){
			outputStatus(response, -1, "参数错误");
		}
		SecureUser operator = (SecureUser)request.getSession().getAttribute(Constant.secUser);
		SecureUserGroup group = GroupService.parseGroupForm(request, response, operator, secManager);
		group.setSiteId(operator.getCurrentSite());
		if(method.equals("edit")){
			try{
				secManager.updateGroup(group, operator);
			}
			catch(Exception ex){
				ex.printStackTrace();
				outputStatus(response, -1, "在更新用户组基本信息时出现错误");
				return;
			}
		}
		else if(method.equals("create")){
			try{
				secManager.createGroupWithDetails(group, operator);
			}
			catch(Exception ex){
				ex.printStackTrace();
				outputStatus(response, -1, "在更新用户组基本信息时出现错误");
				return;
			}			
		}
		if(!GroupService.parseGroupForm2(request, response, operator, secManager)){
			outputStatus(response, -1, "在更新用户组扩展信息时出现错误");
		}
		if(method.equals("create")){
			outputStatus(response, 1, "用户组新建成功");
		}
		else{
			outputStatus(response, 1, "用户组信息修改成功");
		}
	}

	/**
	 * 删除用户组
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void deleteGroups(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String gids = trimStrToNull(request.getParameter("gids"));
		if (gids == null) {
			return;
		}
		String[] ridSplit = gids.split(" ");
		ISecurityMaintenanceManager manager = getMtManager();
		SecureUser operator = (SecureUser) request.getSession().getAttribute(
				Constant.secUser);
		try {
			manager.deleteGroups(ridSplit, operator);
		} catch (AuthorizationException e) {
			outputStatus(response, -1, "您没有删除用户组的权限");
		}
		outputStatus(response, 1, "所选用户组删除成功");
	}
	
	/**
	 * 判断一个用户组是否存在
	 * @param request
	 * @param response
	 */
	private void exist(HttpServletRequest request,
			HttpServletResponse response) {
		SecureUser operator = (SecureUser) request.getSession().getAttribute(
				Constant.secUser);
		String gid = trimStrToNull(request.getParameter("gid"));
		if(gid == null){
			outputStatus(response, -1, "非法的参数");
			return;
		}
		SecureUserGroup group = secManager.retriveGroupById(gid, operator);
		if(group != null){
			outputMsg(response, null);
			return;
		}
		outputMsg(response, "true");
	}
	
	/**
	 * 编辑或者新建一个用户组
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void getGroupById(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String gid = request.getParameter("gid");
		String mode = request.getParameter("mode");
		if (gid == null && !mode.equals("create")) {
			return;
		}
		request.setAttribute("mode", mode);// 执行save时还需要method
		SecureUser operator = (SecureUser) request.getSession().getAttribute(
				Constant.secUser);
		if (mode.equals("edit")) {
			GroupService.wrapGroupForm(gid, request, response, operator, secManager);
		}
		RequestDispatcher dispatcher = request
				.getRequestDispatcher(Constant.jspPath + "edit_group.jsp");
		dispatcher.forward(request, response);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void listGroupWithFilter(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Page<SecureUserGroup> page = new Page<SecureUserGroup>();
		page.setPageSize(9999);
		SecureUser operator = (SecureUser) request.getSession().getAttribute(
				Constant.secUser);
		Page<SecureUserGroup> qResult = secManager.retrieveGroups(page, operator);
		HashMap<String, Object> result = wrapResultWithPage(qResult);
		ArrayList<HashMap> rows = new ArrayList<HashMap>();
		List<SecureUserGroup> groups = qResult.getResults();
		String filterStr = trimStrToNull(request.getParameter("filter"));
		HashMap<String,Boolean> filter = new HashMap<String, Boolean>();
		if(filterStr!=null){
			String[] filterArr = filterStr.split(",");
			for(String s:filterArr){
				filter.put(s, true);
			}
		}
		for (int i = 0; i < groups.size(); i++) {			
			HashMap row = new HashMap();
			SecureUserGroup group = groups.get(i);
			if(filter.containsKey(group.getId())){
				continue;
			}
			row.put("id",group.getId());
			row.put("name",group.getName());			
			rows.add(row);
		}
		result.put("rows", rows);
		result.put("total", rows.size());
		outputJson(response, result);
	}
}