package com.yudean.itc.servlet;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yudean.itc.dto.sec.FavRoute;
import net.sf.json.JSONArray;

import org.apache.log4j.Logger;

import com.yudean.itc.OrgTreeUtil;
import com.yudean.itc.SecurityBeanHelper;
import com.yudean.itc.code.StatusCode;
import com.yudean.itc.dto.Page;
import com.yudean.itc.dto.sec.Organization;
import com.yudean.itc.dto.sec.SecureUser;
import com.yudean.itc.manager.sec.IAuthorizationManager;
import com.yudean.itc.manager.sec.ISecurityMaintenanceManager;
import com.yudean.itc.util.Constant;

@SuppressWarnings("serial")
public class UserServlet extends BaseServlet {
	private ISecurityMaintenanceManager secManager;
	private IAuthorizationManager authManager;
	private static Logger logger = Logger.getLogger(UserServlet.class);

	private static final String[] CONF_CENTER_DEFAULTS = new String[]{
		"mailNotice","N","smsNotice","N","operationTips","Y","defaultSite","","fixTabs",""
	};
	
	@Override
    public void init() throws ServletException {
		super.init();
		secManager = getMtManager();
		authManager = SecurityBeanHelper.getInstance().getBean(IAuthorizationManager.class);
	}

	@Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String method = request.getParameter("method");
		long t1 = System.currentTimeMillis();
		if (method == null) {
			return;
		}
		if (method.equals("setconf")) {
			updateLocalConfig(request, response);
		} else if (method.equals("editpswd")) {
			editUserPassword(request, response);
		} else if (method.equals("getconf")) {
			getLocalConfig(request, response);
		} else if (method.equals("hint")) {
			searchUserWithHint(request, response);
		} else if (method.equals("searchorg")) {
			searchUserOrg(request, response);
		} else if(method.equals("saveconfcenter")){
			saveConfigCenterData(request, response);
		} else if(method.equals("getconfcenter")){
			getConfigCenterData(request, response);
		} else if(method.equals("getfavroute")){
			getFavRoute(request, response);
		} else if(method.equals("editfavroute")){
			editFavRoute(request, response);
		} else if(method.equals("addfavroute")){
			addFavRoute(request, response);
		}
		long t2 = System.currentTimeMillis();
		long delta = t2 - t1;
		if (delta > 100) {
			logger.info("servlet=UserServlet,method = " + method + ",timecost = " + delta);
		}
	}

	private void getLocalConfig(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String attr = trimStrToNull(request.getParameter("attr"));
		SecureUser secUser = (SecureUser) request.getSession().getAttribute(Constant.secUser);
		if (attr == null || secUser == null) {
			return;
		}
		String val;
		try {
			val = authManager.retrieveUserConfig(secUser, attr);
		} catch (Exception ex) {
			outputStatus(response, -1, "在获取属性时出现异常");
			return;
		}
		outputStatus(response, 1, val);
	}

	/**
	 * 修改用户的本地设置
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void updateLocalConfig(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String attr = trimStrToNull(request.getParameter("attr"));
		String val = trimStrToNull(request.getParameter("val"));
		SecureUser secUser = (SecureUser) request.getSession().getAttribute(Constant.secUser);
		if (secUser == null) {
			return;
		}
		try {
			authManager.updateUserConfig(secUser, attr, val);
		} catch (Exception ex) {
			ex.printStackTrace();
			outputStatus(response, -1, "用户设置更新失败");
			return;
		}
		outputStatus(response, 1, "用户设置更新成功");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void editUserPassword(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String id = trimStrToNull(request.getParameter("id"));
		String password = trimStrToNull(request.getParameter("password"));
		SecureUser operator = (SecureUser) request.getSession().getAttribute(Constant.secUser);
		if (id == null || password == null || operator == null) {
			outputStatus(response, -1, "参数错误");
			return;
		}
		SecureUser user = secManager.retrieveUserById(id);
		if (user == null) {
			outputStatus(response, -1, "目标用户不存在");
			return;
		}
		try {
			logger.warn(operator.getId() + "尝试修改用户id为" + id + "的密码");
			Class clazz = null;
			Method method = null;
			Class[] params = new Class[1];
			params[0] = String.class;
			if (user.getSyncInd() == StatusCode.YES) {
				clazz = Class.forName(Constant.syncHashCls);
				method = clazz.getDeclaredMethod(Constant.syncHashMethod, params);

			} else {
				clazz = Class.forName(Constant.sysHashCls);
				method = clazz.getDeclaredMethod(Constant.sysHashMethod, params);
			}
			String hashPass = (String) method.invoke(clazz, new Object[] { password });
			user.setPassword(hashPass);
			secManager.updateUserPassword(user, operator);
			logger.warn(operator.getId() + "尝试修改用户id为" + id + "的密码，成功了");
			outputStatus(response, 1, "密码修改成功");
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.warn(operator.getId() + "尝试修改用户id为" + id + "的密码，但是失败了");
			outputStatus(response, -1, "系统错误，密码修改失败");
		}
	}

	@SuppressWarnings("rawtypes")
	private void searchUserWithHint(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String kw = trimStrToNull(request.getParameter("kw"));
		boolean ignoreSite = trimStrToNull(request.getParameter("ignoresite"))!=null;
		if(kw==null){
			outputMsg(response, "[]");
			return;
		}
		
		SecureUser operator = (SecureUser) request.getSession().getAttribute(Constant.secUser);
		Page<SecureUser> page = new Page<SecureUser>();
		page.setPageSize(11);
		page.setParameter("searchBy", kw);
		if(ignoreSite){
			page.setParameter("ignoreSite", true);
		}
		page = secManager.retrieveUniqueUsers(page, operator);
		List<SecureUser> sResult = page.getResults();
		logger.debug("search user with hint:" + kw + ",size = " + sResult.size());
		ArrayList<HashMap> userList = new ArrayList<HashMap>();
		for(SecureUser m:sResult){
			HashMap<String,Object> row = new HashMap<String, Object>();
			List<Organization> orgs = secManager.selectOrgUserBelongsTo(m.getId());
			if(orgs==null || orgs.size()==0){
				continue;//无组织的用户无法在树中展示
			}
			for(Organization org:orgs){				
				//需要注意 因为子节点组织不包含站点信息 需要用全局映射表Mapping.orgSiteMapping反查站点
				if(!ignoreSite || !operator.isSuperAdmin()){
					//普通用户只能找当前站点下的人
					if(operator.getCurrentSite().equals(OrgTreeUtil.getOrgSite(org.getCode()))){
						row.put("id",m.getId() + "_" + org.getCode());
						row.put("name", m.getName());
						userList.add(row);
						break;
					}
				}
				else{
					//超管随便找一个就可以了
					row.put("id",m.getId() + "_" + org.getCode());
					row.put("name", m.getName());
					userList.add(row);
					break;
				}
			}			
		}
		outputMsg(response, JSONArray.fromObject(userList).toString());
	}

	/**
	 * 获取个性化配置信息
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-11-6
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException:
	 */
	private void getConfigCenterData(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		SecureUser operator = (SecureUser) request.getSession().getAttribute(Constant.secUser);
		int len = CONF_CENTER_DEFAULTS.length/2;
		Map<String,String> formData = new HashMap<String, String>(); 
		for(int i=0;i<len;i++){
			String key = CONF_CENTER_DEFAULTS[2*i];
			String val = trimStrToNull(authManager.retrieveUserConfig(operator, key));
			if(val == null){
				val = CONF_CENTER_DEFAULTS[2*i + 1];				
			}
			formData.put(key, val);
		}
		outputMsg(response, obj2Str(formData));
	}
	
	/**
	 * 保存个性化设置对话框中的内容
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void saveConfigCenterData(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		SecureUser operator = (SecureUser) request.getSession().getAttribute(Constant.secUser);
		if(operator == null){
			outputStatus(response, -1, "设置保存失败，请重新登录后再试");
		}
		int len = CONF_CENTER_DEFAULTS.length/2;

		for(int i=0;i<len;i++){
			String key = CONF_CENTER_DEFAULTS[2*i];
			String val = trimStrToNull(request.getParameter(key));
			authManager.updateUserConfig(operator, key, val);
		}
		outputStatus(response, 1, "设置保存成功（默认身份修改需要重新登录后生效）");
	}
	
	private void searchUserOrg(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String id = trimStrToNull(request.getParameter("id"));
		ArrayList<String> orgs = new ArrayList<String>();
		orgs.add(id);
		while (true) {
			Organization org = secManager.selectOrgById(id);
			if (org == null) {
				break;
			} else if (org.getParentCode().equals("1")) {
				break;
			} else {
				id = org.getParentCode();
				orgs.add(id);
			}
		}
		outputMsg(response, JSONArray.fromObject(orgs).toString());
	}

	private void getFavRoute(HttpServletRequest request,
							 HttpServletResponse response) throws ServletException, IOException {
		SecureUser operator = (SecureUser) request.getSession().getAttribute(Constant.secUser);
		List<FavRoute> favRoutes = secManager.selectFavRoute(operator);
		Map<String, Object> outMap = new HashMap<String, Object>();
		outMap.put("data", favRoutes);
		outputJson(response, outMap);
	}

	private void editFavRoute(HttpServletRequest request,
							  HttpServletResponse response) throws ServletException, IOException {
		String toAdd = trimStrToNull(request.getParameter("toAdd"));
		String toRemove = trimStrToNull(request.getParameter("toRemove"));
		SecureUser operator = (SecureUser) request.getSession().getAttribute(Constant.secUser);
		try{
			secManager.editFavRoute(operator, toAdd, toRemove);
		}catch(Exception ex){
			logger.error("在修改用户收藏时出现错误", ex);
			outputStatus(response, -1, "更新收藏夹失败");
			return;
		}
		outputStatus(response, 1, "更新收藏夹成功");
	}

	private void addFavRoute(HttpServletRequest request,
							 HttpServletResponse response) throws ServletException, IOException {
		SecureUser operator = (SecureUser) request.getSession().getAttribute(Constant.secUser);
		String routeId = trimStrToNull(request.getParameter("routeId"));
		if(routeId == null){
			outputStatus(response, -1, "错误的参数");
			return;
		}
		boolean exists = secManager.isFavRouteExists(operator, routeId);
		if(exists){
			outputStatus(response, 1, "该功能已收藏");
			return;
		}
		secManager.editFavRoute(operator, routeId, null);
		outputStatus(response, 1, "该功能收藏成功");
	}
}
