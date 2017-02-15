package com.yudean.itc.servlet;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.yudean.itc.dto.sec.SecureUser;
import com.yudean.itc.security.service.impl.ConfigService;
import com.yudean.itc.util.Constant;

/**
 * 用于将数据库/session中的对象转化为缓存过的数据库脚本
 * @author murmur
 *
 */
public class ResourceServlet extends BaseServlet{

	private static final long serialVersionUID = 3600153374821921483L;
	public static String frameConfig = null;
	private ConfigService configService;
	
	@Override
    public void init() throws ServletException {
		ServletContext servletContext = this.getServletContext();     
		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(servletContext); 
		configService = ctx.getBean(ConfigService.class);
		if(frameConfig==null){
			frameConfig = configService.wrapITCFrameConfig();
		}
	}
	
	@Override
    protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String file = request.getParameter("f");
		if(file == null){
			return;
		}
		if(file.equals("privilege.js")){
			downloadUserPrivilege(request, response);
		} else if(file.equals("config.js")){
			downloadFrameworkConfig(request, response);			
		} else if(file.equals("tabs")){
			getOwnTabs(request, response);
		} else if(file.equals("route.js")){
			getFrontRoute(request, response);
		}
	}
	
	private void downloadFrameworkConfig(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		//生成的变量名，如v=conf，则js中内容为conf = {.....};
		String v = trimStrToNull(request.getParameter("v"));
		//加了这个参数连document.ready里的东西都给你写好
		String withInit = trimStrToNull(request.getParameter("withinit"));
		if(v==null){
			v = "opts";
		}		
		response.setContentType("application/x-javascript;charset=UTF-8");
		StringBuilder sb = new StringBuilder("var " + v);
		sb.append("=").append(frameConfig).append(";");
		if(withInit!=null){
			sb.append("\n").append("$(document).ready(function(){\n");
			sb.append("_ITC.init(").append(v).append(");\n");
			sb.append("_ITC.switchDefaultTab()\n");
			sb.append("});");
		}
		outputMsg(response, sb.toString());
	}
	
	private void downloadUserPrivilege(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		SecureUser secUser = (SecureUser) request.getSession().getAttribute(Constant.secUser);
		StringBuilder sb = new StringBuilder("var privMapping = ");
		if(secUser!=null){
			sb.append(configService.wrapUserAuthConfig(secUser));
		}
		else{
			sb.append("{}");
		}
		sb.append(";");
		outputMsg(response, sb.toString());
	}
	
	/**
	 * 获取可以显示的选项卡 用于个性化配置
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void getOwnTabs(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		outputMsg(response, configService.getOwnedTabs());
	}

	private void getFrontRoute(HttpServletRequest request, HttpServletResponse response) throws ServletException{
		SecureUser secUser = (SecureUser) request.getSession().getAttribute(Constant.secUser);
		if(secUser == null){
			outputMsg(response, "");
			return;
		}
		String route = configService.wrapFrontRouteConfig(secUser);
		outputMsg(response, route);
	}
}
