package com.yudean.itc.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.yudean.itc.SecurityBeanHelper;
import com.yudean.itc.code.ParamConfig;
import com.yudean.itc.code.StatusCode;
import com.yudean.itc.dao.sec.SiteMapper;
import com.yudean.itc.dto.sec.SecureUser;
import com.yudean.itc.dto.sec.Site;
import com.yudean.itc.dto.support.Configuration;
import com.yudean.itc.exception.SimpleMessageException;
import com.yudean.itc.exception.StatusMessageException;
import com.yudean.itc.exception.sec.AuthenticationException;
import com.yudean.itc.manager.sec.IAuthenticationManager;
import com.yudean.itc.manager.sec.IAuthorizationManager;
import com.yudean.itc.manager.support.IConfigurationManager;
import com.yudean.itc.util.ClassCastUtil;
import com.yudean.itc.util.Constant;
import com.yudean.itc.util.MD5;
import com.yudean.itc.util.SecurityTools;
import com.yudean.itc.util.StringHelper;
import com.yudean.mvc.bean.userinfo.impl.UserInfoImpl;
import com.yudean.mvc.util.LogUtil;

@SuppressWarnings("serial")
public class LoginServlet extends BaseServlet {
	private SiteMapper siteMapper;
	private IAuthenticationManager authManager;
	private IAuthorizationManager autzManager;
	private IConfigurationManager iConfManager;

	private static final int LOGIN_TOKEN_EXPIRES = 10 * 24 * 3600;

	private static final Logger log = Logger.getLogger(LoginServlet.class);

	@Override
    public void init() throws ServletException {
		super.init();
		SecurityBeanHelper helper = SecurityBeanHelper.getInstance();
		authManager = helper.getBean(IAuthenticationManager.class);
		autzManager = helper.getBean(IAuthorizationManager.class);
		siteMapper = helper.getBean(SiteMapper.class);
		iConfManager = helper.getBean(IConfigurationManager.class);
	}

	@Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String method = request.getParameter("method");
		if (method == null) {
			return;
		}
		try {
			if (method.equals("login")) {
				login(request, response);
			} else if (method.equals("listsites")) {
				listSites(request, response);
			} else if (method.equals("switchsite")) {
				switchSite(request, response);
			} else if (method.equals("index")) {
				getMainPage(request, response);
			} else if (method.equals("logout")) {
				logout(request, response);
			} else if (method.equals("sso")) {
				displaySSOPage(request, response);
			} else if (method.equals(ParamConfig.LoginMethod)) {
				interfaceImmediacylogin(request, response);
			}else if(method.equals("ImmediacyInterfaceloginPwdDef")){
				interfaceImmediacylogin(request, response, true);
			}
		} catch (SimpleMessageException ex) {
			outputMsg(response, ex.getMessage());
		} catch (StatusMessageException ex) {
			outputStatus(response, -1, ex.getMessage());
		} catch (Exception ex) {
			ex.printStackTrace();
			outputMsg(response, "系统错误，暂时无法响应您的请求，请联系管理员");
		}
	}

	/**
	 * 用户名+密码登陆
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void login(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String userid = request.getParameter("uid");
		log.info("用户" + userid + "登陆系统。 登陆时间:" + ParamConfig.S_MSTIME_FORMATTER.format(new Date()) + ".Session:" + request.getSession().getId());
		String password = request.getParameter("password");
		String from = trimStrToNull(request.getParameter("from"));
		String savePass = request.getParameter("savepass");
		if (userid == null || password == null) {
			throw new StatusMessageException("用户名或者密码为空");
		}
		SecureUser secUser = doLoginProcess(request, response, userid, password, from);
		// 自动登录
		if (savePass != null) {
			Date now = new Date();
			String loginToken = SecurityTools.encrypt(userid);
			// cookie只保存加密后的用户名 sec_userconfig里保存有效期和密码
			Cookie cookie = new Cookie("logintoken", loginToken);
			cookie.setMaxAge(LOGIN_TOKEN_EXPIRES);
			response.addCookie(cookie);
			autzManager.updateUserConfig(secUser, "loginToken", MD5.GetMD5Code(password) + " " + now.getTime());
		}

		outputStatus(response, 1, "登陆成功");
	}

	/**
	 * 具体登录流程
	 * 
	 * @param request
	 * @param response
	 * @param userid
	 * @param password
	 * @param from
	 *            来源，值为sso时不进行MD5
	 * @return
	 * @throws StatusMessageException
	 */
	private SecureUser doLoginProcess(HttpServletRequest request, HttpServletResponse response, String userid, String password, String from) throws StatusMessageException {

		SecureUser user = null;
		try {
			//String pswd = ("sso".equals(from)) ? password : MD5.GetMD5Code(password);
			if("sso".equals(from)){
				user = authManager.signIn(userid, password);
			}else{
				user = authManager.signInAD(userid, password);
			}
		} catch (AuthenticationException ex) {
			String msg = ex.getMessage();
			// 由于首页错误框长度有限 这里需要去掉"XXX登陆失败:原因"中冒号前面的东西
			if (msg.contains(":")) {
				String[] msgSplit = msg.split(":");
				if (msgSplit.length == 2) {
					msg = msgSplit[1];
				}
			}
			throw new StatusMessageException(msg);
		}
		if (user.getActive() == null || user.getActive() == StatusCode.NO) {
			throw new StatusMessageException("该用户被设为无效，无法登陆");
		}

		HttpSession session = request.getSession();
		if(user.getCurrentSite() != null){
			updateAttachPreviewStatus(request, user.getCurrentSite());
		}
		UserInfoImpl userInfo = null;
		try {
			// 将权限管理模块的SecureUser对象数据注入到 timss UserInfo接口中
			userInfo = ClassCastUtil.castAllField2Class(UserInfoImpl.class, user);
		} catch (Exception ex) {
			LogUtil.error("转换用户类型数据异常 secUser->userInfo,数据类容:" + user, ex);
		}

		session.setAttribute(Constant.secUser, userInfo);
		session.setAttribute("username", user.getName());
		//设置登录方式，用于前端控制
		session.setAttribute("authType", user.getAutType());
		if (user.getAuthorizedSites().size() > 1) {
			session.setAttribute("crossSite", true);
			// 需要把当前站点复制一份为默认站点 否则跨站选择后无法正确指示哪个是默认的
			session.setAttribute("defaultSite", user.getCurrentSite());
		} else {
			session.setAttribute("crossSite", false);
		}
		return user;
	}

	/**
	 * 列出当前用户可用的站点列表（需要先login）
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void listSites(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Object user = request.getSession().getAttribute(Constant.secUser);
		if (user == null) {
			outputMsg(response, "[]");
			return;
		}
		SecureUser sUser = (SecureUser) user;
		String defSite = (String) request.getSession().getAttribute("defaultSite");
		List<String[]> result = new ArrayList<String[]>();
		Collection<Site> sites = sUser.getAuthorizedSites();
		if (sites == null) {
			outputMsg(response, "[]");
			return;
		}
		for (Site site : sites) {
			String[] row = null;
			if (site.getId().equals(defSite)) {
				row = new String[3];
				row[2] = "true";
			} else {
				row = new String[2];
			}
			row[0] = site.getId();
			row[1] = site.getName();
			result.add(row);
		}
		outputMsg(response, JSONArray.fromObject(result).toString());
	}

	/**
	 * 切换站点，多站点和管理员使用
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void switchSite(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String sid = request.getParameter("sid");
		if (sid == null) {
			return;
		}
		HttpSession session = request.getSession();
		SecureUser secUser = (SecureUser)session.getAttribute(Constant.secUser);
		if (secUser == null) {
			outputStatus(response, -2, "登陆超时，请重新登陆");
			return;
		}
		log.info("用户" + secUser.getId() + "尝试切换默认站点");

		// 判断站点ID是否合法
		Site site = siteMapper.selectSingleSite(sid);
		if (site == null) {
			outputStatus(response, -1, "站点不存在");
			return;
		}

		// 如果选择保存默认值 则更改系统设置 否则使用用户名@站点的形式告诉API这是一次临时跨站
		boolean isDef = request.getParameter("setdefault").equals("1");
		String uid = secUser.getId();
		if (isDef) {
			autzManager.updateUserConfig(secUser, "defaultSite", sid);
		} else {
			uid += "@" + sid;
		}
		// 使用无密码重新登录完成跨站动作
		try {
			secUser = authManager.signIn(uid);
		} catch (Exception ex) {
			log.error("站点切换再登陆失败，原因：" + ex.getMessage());
			ex.printStackTrace();
			outputStatus(response, -3, "切换站点时登陆失败，请尝试从主界面登陆");
			return;
		}
		// timss专属功能 个别站点开启附件预览
		updateAttachPreviewStatus(request, site.getId());
		UserInfoImpl userInfo = null;
		try {
			// 将权限管理模块的SecureUser对象数据注入到 timss UserInfo接口中
			userInfo = ClassCastUtil.castAllField2Class(UserInfoImpl.class, secUser);
		} catch (Exception ex) {
			LogUtil.error("转换用户类型数据异常 secUser->userInfo,数据类容:" + secUser, ex);
		}
		request.getSession().setAttribute(Constant.secUser, userInfo);
		outputStatus(response, 1, "站点切换成功");
	}

	private void updateAttachPreviewStatus(HttpServletRequest request, String site){
		HttpSession session = request.getSession();
		session.removeAttribute("canPreviewAttach");
		Configuration previewSitesConf = iConfManager.query("preview_attachment_sites", "NaN", "NaN");
		if(previewSitesConf != null) {
			String previewSites = previewSitesConf.getVal();
			if(previewSites.indexOf(site) >= 0 || previewSites.equals("*")){
				session.setAttribute("canPreviewAttach", true);
			}
		}
	}

	/**
	 * 跳转到首页
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void getMainPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		SecureUser user = (SecureUser) request.getSession().getAttribute(Constant.secUser);
		if (user == null) {
			// 判断是否有自动登录的cookie
			Cookie[] cookies = request.getCookies();
			if (null != cookies) {
				for (Cookie cookie : cookies) {
					if (cookie.getName().equals("logintoken")) {
						String[] validResult = validLoginToken(cookie.getValue());
						if (validResult != null) {
							try {
								log.info("尝试从cookies登录，用户名 = " + validResult[0]);
								// 最后一个参数设置为sso表示不再次MD5
								user = doLoginProcess(request, response, validResult[0], validResult[1], "sso");
							} catch (Exception ex) {
								// 这里不需要做错误处理 反正都是cookie失效 走正常文本框登录即可
							}
						}
						break;
					}
				}
			}
			if (user == null) {
				logout(request, response);
				return;
			}
		}

		String skin = autzManager.retrieveUserConfig(user, "theme");
		if (skin == null) {
			skin = "tiankonglan";
		}
		request.setAttribute("skin", skin);

		String path = "mvc/main/mainpage.do";
		if (user.getOrganizations().size() > 0) {
			request.setAttribute("currOrg", user.getOrganizations().get(0).getName());
		} else {
			request.setAttribute("currOrg", user.getCurrentSite());
		}
		RequestDispatcher dispatcher = request.getRequestDispatcher(path);
		dispatcher.forward(request, response);
	}

	private void logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		// 删除数据库里的token
		SecureUser user = (SecureUser) request.getSession().getAttribute(Constant.secUser);
		if (null != user) {
			autzManager.updateUserConfig(user, "loginToken", null);
		}
		session.removeAttribute(Constant.secUser);
		// 删除cookies
		Cookie cookie = new Cookie("logintoken", null);
		response.addCookie(cookie);

		// 销毁session
		if (null != user) {
			log.info("用户" + user.getName() + "操作退出");
			session.invalidate();
		}

		RequestDispatcher dispatcher = request.getRequestDispatcher("index_login.jsp");
		dispatcher.forward(request, response);
	}

	private void displaySSOPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = "index_login.jsp";
		try {
			String uid = trimStrToNull(request.getParameter("uid"));
			String password = trimStrToNull(request.getParameter("password"));
			if (uid == null || password == null) {
				outputStatus(response, -1, "uid或者password缺失");
				return;
			}
			request.setAttribute("uid", uid);
			request.setAttribute("password", password);
			path = "jsp/cross_site.jsp";
		} catch (Exception e) {
			log.error("直接登录异常，返回登陆页面", e);
			path = "index_login.jsp";
		}
		RequestDispatcher dispatcher = request.getRequestDispatcher(path);
		dispatcher.forward(request, response);
	}

	private void interfaceImmediacylogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		SecurityBeanHelper helper = SecurityBeanHelper.getInstance();
		IAuthenticationManager manager = helper.getBean(IAuthenticationManager.class);
		IAuthorizationManager authManager = SecurityBeanHelper.getInstance().getBean(IAuthorizationManager.class);
		SecureUser secUser = null;
		UserInfoImpl userInfo = null;
		String uid = trimStrToNull(request.getParameter(ParamConfig.UserIDName));
		log.info("用户" + uid + "直接登陆系统。 登陆时间:" + ParamConfig.S_MSTIME_FORMATTER.format(new Date()) + ".Session:" + request.getSession().getId());
		String pwd = trimStrToNull(request.getParameter(ParamConfig.PasswordIDName));
		String sid = trimStrToNull(request.getParameter(ParamConfig.SiteID));
		String interfacemode = trimStrToNull(request.getParameter(ParamConfig.ModeName));
		String interfacedata = trimStrToNull(request.getParameter(ParamConfig.DataName));
		String redirect = trimStrToNull(request.getParameter("redirect"));
		boolean isProcessRight = false;
		if (uid != null && pwd != null) {
			try {
				secUser = manager.signIn(uid, pwd);
				if (null != secUser) {
					userInfo = ClassCastUtil.castAllField2Class(UserInfoImpl.class, secUser);
					if (userInfo.getActive() != null && userInfo.getActive() == StatusCode.YES) {
						HttpSession session = request.getSession();
						session.setAttribute(Constant.secUser, userInfo);
						session.setAttribute("username", userInfo.getName());
						int siteLen = userInfo.getAuthorizedSites().size();
						if (siteLen > 1) {
							manager.switchSite(userInfo, sid);
						}
						session.setAttribute("currsite", sid);
						updateAttachPreviewStatus(request, sid);
						String skin = authManager.retrieveUserConfig(userInfo, "theme");
						if (skin == null) {
							skin = "yuzhouchenqu";
						}
						if (secUser.getOrganizations().size() > 0) {
							request.setAttribute("currOrg", secUser.getOrganizations().get(0).getName());
						} else {
							request.setAttribute("currOrg", secUser.getCurrentSite());
						}
						request.setAttribute("skin", skin);
						isProcessRight = true;
					}
				}
			} catch (Exception ex) {
				LogUtil.error("用户直接登录异常，弹出登录页面", ex);
			}
		}
		if (isProcessRight) {
			String url = "";
			if(redirect == null) {
				url = StringHelper.concat("mvc/main/mainpage.do?interfacemode=", interfacemode, "&interfacedata=", interfacedata);
				RequestDispatcher dispatcher = request.getRequestDispatcher(url);
				dispatcher.forward(request, response);
			}else{
				//路由模式
				url =  "login?method=index#" + redirect;
				response.sendRedirect(url);
			}
		} else {
			RequestDispatcher dispatcher = request.getRequestDispatcher("index_login.jsp");
			dispatcher.forward(request, response);
		}
	}

	private void interfaceImmediacylogin(HttpServletRequest request, HttpServletResponse response, boolean userinf) throws ServletException, IOException {
		SecurityBeanHelper helper = SecurityBeanHelper.getInstance();
		IAuthenticationManager manager = helper.getBean(IAuthenticationManager.class);
		IAuthorizationManager authManager = helper.getBean(IAuthorizationManager.class);
		IConfigurationManager config = helper.getBean(IConfigurationManager.class);
		final Configuration infcof = config.query("framework_ImdePwd", "NaN", "NaN");

		SecureUser secUser = null;
		UserInfoImpl userInfo = null;
		String uid = trimStrToNull(request.getParameter(ParamConfig.UserIDName));
		log.info("用户" + uid + "直接登陆系统。 登陆时间:" + ParamConfig.S_MSTIME_FORMATTER.format(new Date()) + ".Session:" + request.getSession().getId());
		String pwd = trimStrToNull(request.getParameter(ParamConfig.PasswordIDName));
		String sid = trimStrToNull(request.getParameter(ParamConfig.SiteID));
		boolean isProcessRight = false;
		if (uid != null && pwd != null && userinf) {
			try {
				String md5 = MD5.GetMD5Code(pwd).trim();
				if (md5.equals(infcof.getVal().trim())) {
					secUser = manager.signIn(uid);
					if (null != secUser) {
						userInfo = ClassCastUtil.castAllField2Class(UserInfoImpl.class, secUser);
						if (userInfo.getActive() != null && userInfo.getActive() == StatusCode.YES) {
							HttpSession session = request.getSession();
							session.setAttribute(Constant.secUser, userInfo);
							session.setAttribute("username", userInfo.getName());
							int siteLen = userInfo.getAuthorizedSites().size();
							if (siteLen > 1) {
								manager.switchSite(userInfo, sid);
							}
							session.setAttribute("currsite", sid);
							updateAttachPreviewStatus(request, sid);
							String skin = authManager.retrieveUserConfig(userInfo, "theme");
							if (skin == null) {
								skin = "yuzhouchenqu";
							}
							if (secUser.getOrganizations().size() > 0) {
								request.setAttribute("currOrg", secUser.getOrganizations().get(0).getName());
							} else {
								request.setAttribute("currOrg", secUser.getCurrentSite());
							}
							request.setAttribute("skin", skin);
							isProcessRight = true;
						}
					}
				}
			} catch (Exception ex) {
				LogUtil.error("用户直接登录异常，弹出登录页面", ex);
			}
		}
		if (isProcessRight) {
			RequestDispatcher dispatcher = request.getRequestDispatcher("mvc/main/mainpage.do");
			dispatcher.forward(request, response);
		} else {
			RequestDispatcher dispatcher = request.getRequestDispatcher("index_login.jsp");
			dispatcher.forward(request, response);
		}
	}

	/**
	 * 校验自动登录保存cookie的合法性
	 * 
	 * @param token
	 * @return String[2] 元素分别为用户名和hash过的密码
	 */
	private String[] validLoginToken(String token) {
		if (StringUtils.trimToNull(token) == null) {
			// 这里要小心java设置token为null后 这里的token是个空字符串而不是null
			return null;
		}
		String decryptedToken = null;
		try {
			decryptedToken = SecurityTools.decrypt(token);
		} catch (Exception ex) {
			LogUtil.warn("token " + token + "是非法的鉴权码！");
			return null;
		}
		SecureUser secUser = new SecureUser();
		secUser.setId(decryptedToken);
		String passAndTime = autzManager.retrieveUserConfig(secUser, "loginToken");
		if (null == passAndTime) {
			return null;
		}
		String[] tokenArr = passAndTime.split(" ");
		Long ts = Long.parseLong(tokenArr[1]);
		Long now = new Date().getTime();
		Long expires = (now - ts) / 1000 - LOGIN_TOKEN_EXPIRES;
		if (expires > 0) {
			LogUtil.info(decryptedToken + "的token已经过期，需要重新输入密码登陆（已过期" + expires + ")");
			autzManager.updateUserConfig(secUser, "loginToken", null);
			return null;
		}
		tokenArr[1] = tokenArr[0];
		tokenArr[0] = decryptedToken;
		return tokenArr;
	}
}
