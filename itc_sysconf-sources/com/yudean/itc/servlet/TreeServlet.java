package com.yudean.itc.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.yudean.itc.bean.PrivEuTreeNode;
import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.yudean.itc.dto.sec.SecureUser;
import com.yudean.itc.manager.sec.IAuthenticationManager;
import com.yudean.itc.manager.sec.IAuthorizationManager;
import com.yudean.itc.security.service.impl.TreeService;
import com.yudean.itc.util.Constant;
import com.yudean.itc.bean.EuTreeNode;

public class TreeServlet extends BaseServlet{
	private static final long serialVersionUID = 5014168379745550261L;
	private static Logger logger = Logger.getLogger(TreeServlet.class);
	private IAuthorizationManager authManager;
	private IAuthenticationManager autzManager;
	private TreeService treeService;

	@Override
    public void init() throws ServletException {             
		super.init();     
		ServletContext servletContext = this.getServletContext();  
		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(servletContext);
		autzManager = ctx.getBean(IAuthenticationManager.class);
		treeService = ctx.getBean(TreeService.class);
		authManager = ctx.getBean(IAuthorizationManager.class);
	} 
	
	@Override
    public void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String method = request.getParameter("method");
		long t1 = System.currentTimeMillis();
		if (method == null) {
			return;
		}
		if(method.equals("org")){
			getOrgTreeData(request,response);
		}else if(method.equals("priv")){
			getPrivilegeTreeData(request, response);
		}else if(method.equals("privchildren")){
			getAllPrivilegesInNode(request, response);
		}else if(method.equals("subusers")){
			getAllUsersInOrg(request, response);
		}else if(method.equals("extendall")){
			extendTreeToEndWithFilter(request, response);
		}else if(method.equals("privend")){
			extendPrivTreeToEndWithFilter(request, response);
		}else if(method.equals("userpriv")){
			getPrivTreeForUser(request, response);
		}
		long t2 = System.currentTimeMillis();
		long delta = t2 - t1;
		if(delta>100){
			logger.info("servlet=TreeSerlvet,method = " + method + ",timecost = " + delta);
		}
	}
	
	private void getOrgTreeData(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String parent = trimStrToNull(request.getParameter("id"));
		String onelevel = trimStrToNull(request.getParameter("onelevel"));
		SecureUser operator = (SecureUser) request.getSession().getAttribute(Constant.secUser);
		boolean ignoreSite = trimStrToNull(request.getParameter("ignoresite"))!=null && operator.isSuperAdmin();
		if(parent==null){
			parent = "1";
			if(onelevel==null){			
				get2LevelTree(request, response);
				return;
			}
		}
		else if(parent.startsWith("org")){
			parent = parent.replace("org_", "");
		}
		String onlyOrgStr = trimStrToNull(request.getParameter("onlyorg"));
		boolean onlyOrg = (onlyOrgStr!=null);
		List<EuTreeNode> nodes = treeService.getOrgTreeData(parent, operator, onlyOrg,ignoreSite);
		if(nodes!=null){
			JSONArray jArray = JSONArray.fromObject(nodes);
			outputMsg(response, jArray.toString());
		}		
	}
	
	
	private void getPrivilegeTreeData(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String id = trimStrToNull(request.getParameter("id"));
		JSONArray jArray;
		if(id==null){
			jArray = JSONArray.fromObject(treeService.getPrivTreeRoot());
		}
		else{
			ArrayList<EuTreeNode> nodes = treeService.getPrivTreeNodes(id);
			ArrayList<EuTreeNode> filteredNodes = new ArrayList<EuTreeNode>();
			HttpSession session = request.getSession();
			SecureUser operator = (SecureUser) session.getAttribute(Constant.secUser);
			//因为某些登陆形式没有走signin方法 这里需要重新为这部分用户拼接权限（也许是我多虑了？）
			if(operator.getPrivileges()==null){
				autzManager.assemblePrivileges(operator, operator.getCurrentSite());
				session.setAttribute(Constant.secUser, operator);
			}
			//过滤掉用户不拥有的权限
			for(int i=0;i<nodes.size();i++){
				EuTreeNode node = nodes.get(i);
				String pid = node.getId().replace("MNU_", "").replace("FUN_", "");
				if(operator.isPrivOwnedByUser(pid)){
					filteredNodes.add(node);
				}
			}
			jArray = JSONArray.fromObject(filteredNodes);
		}
		outputMsg(response, jArray.toString());
	}
	
	/**
	 * 获取某一节点下所有的权限
	 * @param request
	 * @param response
	 */
	private void getAllPrivilegesInNode(HttpServletRequest request,
			HttpServletResponse response) {
		String id = trimStrToNull(request.getParameter("id"));
		if(id==null){
			return;			
		}
		HttpSession session = request.getSession();
		SecureUser operator = (SecureUser) session.getAttribute(Constant.secUser);
		List<String> result = new ArrayList<String>();
		treeService.getPrivInNode(result, id,operator);
		
		outputMsg(response, JSONArray.fromObject(result).toString());
	}

	private void getAllUsersInOrg(HttpServletRequest request,
			HttpServletResponse response){
		String id = trimStrToNull(request.getParameter("id"));
		if(id==null){
			return;			
		}
		SecureUser operator = (SecureUser)request.getSession().getAttribute(Constant.secUser);
		List<SecureUser> users = authManager.retriveActiveUsersOfGivenOrg(id, true, operator);
		List<String> result = new ArrayList<String>();
		for(SecureUser user:users){
			result.add(user.getId() + "," + user.getName());
		}
		outputMsg(response, JSONArray.fromObject(result).toString());
	}
	
	/**
	 * 将权限树展开到底，同时过滤掉用户没有的权限
	 * @param request
	 * @param response
	 */
	private void extendPrivTreeToEndWithFilter(HttpServletRequest request,
			HttpServletResponse response){
		String filterStr = trimStrToNull(request.getParameter("filter"));
		if(filterStr==null){
			outputMsg(response, "[]");
			return;
		}
		JSONObject filter = JSONObject.fromObject(filterStr);
		List<EuTreeNode> root = treeService.getPrivTreeRoot();
		List<EuTreeNode> fNodes = new ArrayList<EuTreeNode>();
		for(int i=0;i<root.size();i++){
			EuTreeNode node = root.get(i);
			extendPrivTree(node, filter);
			if(node.getChildren()!=null && node.getChildren().size()>0){
				fNodes.add(node);
			}
		}
		outputMsg(response, JSONArray.fromObject(fNodes).toString());
	}
	
	/**
	 * 递归展开权限树
	 * @param node
	 * @param filter
	 */
	private void extendPrivTree(EuTreeNode node,JSONObject filter){
		node.setState("open");
		List<EuTreeNode> children = treeService.getPrivTreeNodes(node.getId());
		List<EuTreeNode> fChildren = new ArrayList<EuTreeNode>();
		for(int i=0;i<children.size();i++){
			EuTreeNode child = children.get(i);
			if(filter.containsKey(child.getId())){
				fChildren.add(child);
				extendPrivTree(child, filter);
			}
		}
		node.setChildren(fChildren);
	}
	
	/**
	 * 展开组织机构树到底（过滤掉用户组/角色不涉及的用户）
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void extendTreeToEndWithFilter(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		boolean ignoreSite = trimStrToNull(request.getParameter("ignoresite"))!=null;
		String orgFilter = trimStrToNull(request.getParameter("orgFilter"));
		String personFilter = trimStrToNull(request.getParameter("personFilter"));
		if(orgFilter==null || personFilter==null || orgFilter.equals("{}") || personFilter.equals("{}")){
			outputMsg(response, "[]");
			return;
		}
		JSONObject orgF = JSONObject.fromObject(orgFilter);
		JSONObject personF = JSONObject.fromObject(personFilter);
		SecureUser operator = (SecureUser) request.getSession().getAttribute(Constant.secUser);
		List<EuTreeNode> nodes = treeService.getOrgTreeData("1", operator, true,ignoreSite);
		List<EuTreeNode> filteredNodes = new ArrayList<EuTreeNode>();
		for(int i=0;i<nodes.size();i++){
			EuTreeNode node = nodes.get(i);			
			String nid = node.getId().replace("org_", "");
			if(orgF.containsKey(nid)){
				filteredNodes.add(node);
				extendTreeNode(node,orgF,personF,operator,ignoreSite);
			}
		}
		outputMsg(response, JSONArray.fromObject(filteredNodes).toString());
		
	}
	
	private void extendTreeNode(EuTreeNode node,JSONObject orgFilter,JSONObject personFilter,SecureUser operator,boolean ignoreSite){
		node.setState("open");//这里注意要设置状态 否则默认的树虽然加载了数据还是闭合的
		String nid = node.getId().replace("org_", "");
		
		List<EuTreeNode> nodes = treeService.getOrgTreeData(nid, operator, false,ignoreSite);
		List<EuTreeNode> filteredNodes = new ArrayList<EuTreeNode>();
		for(int i=0;i<nodes.size();i++){
			EuTreeNode nc = nodes.get(i);
			if(nc.getId().startsWith("org_")){
				String ncid = nc.getId().replace("org_", "");
				if(orgFilter.containsKey(ncid)){
					filteredNodes.add(nc);
					extendTreeNode(nc,orgFilter,personFilter,operator,ignoreSite);
				}
			}
			else{
				String ncid = nc.getId().replace("user_", "");
				if(personFilter.containsKey(ncid)){
					filteredNodes.add(nc);
				}
			}
		}
		node.setChildren(filteredNodes);
	}
	
	/**
	 * 获得2层组织机构树 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void get2LevelTree(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		SecureUser operator = (SecureUser) request.getSession().getAttribute(Constant.secUser);
		String onlyOrgStr = trimStrToNull(request.getParameter("onlyorg"));
		boolean ignoreSite = trimStrToNull(request.getParameter("ignoresite"))!=null;
		boolean onlyOrg = (onlyOrgStr!=null);
		List<EuTreeNode> nodes = treeService.getOrgTreeData("1", operator, true,ignoreSite);
		for(EuTreeNode node:nodes){
			List<EuTreeNode> children1 = treeService.getOrgTreeData(node.getId().replace("org_", ""), operator, onlyOrg,ignoreSite);
			if(children1.size()>0){
				node.setState("open");
			}
			node.setChildren(children1);
		}
		JSONArray jArray = JSONArray.fromObject(nodes);
		outputMsg(response, jArray.toString());
	}

	private void getPrivTreeForUser(HttpServletRequest request,
									HttpServletResponse response) throws ServletException, IOException {
		String uid = trimStrToNull(request.getParameter("uid"));
		if(uid == null){

		}
		SecureUser operator = (SecureUser) request.getSession().getAttribute(Constant.secUser);
		List<PrivEuTreeNode> tree = treeService.getPrivTreeWithOrigin(uid, operator.getCurrentSite());
		JSONArray jArray = JSONArray.fromObject(tree);
		outputMsg(response, jArray.toString());
	}
}
