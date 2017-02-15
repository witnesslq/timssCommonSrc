package com.yudean.mvc.util;

import java.io.InputStream;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import com.yudean.itc.bean.environment.RuntimeEnvironmentData;
import com.yudean.mvc.bean.branch.BranchViewData;
import com.yudean.mvc.bean.userinfo.UserInfo;
import com.yudean.mvc.interfaces.InitClassAfterContextBuildInterface;
import com.yudean.mvc.service.ItcMvcService;

/**
 * 处理TIMSS分支版本工具类，TIMSS框架包私有工具
 * 
 * @author kChen
 * 
 */
public class FrameworkViewBranchUtil implements InitClassAfterContextBuildInterface {
	private static final Logger log = Logger.getLogger(FrameworkViewBranchUtil.class);
	
	/**
	 * 探查分支版本对应的方法,如果无分支版本，返回BranchMethodData.isHasBranch =
	 * fasle。如果有分支版本，BranchMethodData.isHasBranch = true，今后加入Exception 框架，
	 * 传递参数中必须带着UserInfo接口的参数，默认都被视为无分支版本，因为UserInfo包含了每个用户的站点信息。
	 * 
	 * @param targetclass
	 *            被调用的对象
	 * @param targetargsList
	 *            被调用的参数列表
	 * @param methodName
	 *            被调用的方法名称
	 * @return 返回数据对象方法
	 * @throws Exception
	 */
	
	static private ItcMvcService itcMvcService;
	/**
	 * 探查分支页面是否存在
	 * 
	 * @param path
	 *            当前访问页面路径
	 * @param userinfo
	 * @param context
	 * @return
	 * @throws Exception
	 */
	static public BranchViewData serviceBranchViewCheck(String path, String sModuleName, ServletContext context) throws Exception {
		BranchViewData branchViewdata = itcMvcService.getBeans("core_service_framework_ToolBranchViewData", BranchViewData.class);
		RuntimeEnvironmentData env = itcMvcService.getRunEnvironmentDatas();
		UserInfo userinfo = itcMvcService.getUserInfoScopeDatas();
		// 获取运行环境参数
		try {
			if (null != userinfo) {
				final String packageWildcard = env.getPackageWildcard();
				final String subPathWildcard = env.getSubPathwildcard();
				final String web = env.getWebRoot();
				final String root = env.getViewRoot();
				final String location = env.getViewLocation();

				String corepath = root.replace(packageWildcard, sModuleName) + path;
				branchViewdata.isHasBranchView = true;
				branchViewdata.BranchViewPath = corepath;
				
				String siteid = userinfo.getSiteId();// 获取站点信息
				if(null != siteid && !"".equals(siteid)){
					siteid = siteid.toLowerCase();
					path = path.startsWith(ParseStrUtil.SERVLETPATH_SEPARATOR) ? path.substring(1) : path;// 替换前缀

					String locpath = location.replace(packageWildcard, sModuleName);
					locpath = locpath.replace(subPathWildcard, siteid + ParseStrUtil.SERVLETPATH_SEPARATOR + path);
					String sCheckPath = web + (locpath.startsWith(ParseStrUtil.SERVLETPATH_SEPARATOR) ? locpath.substring(1) : locpath);
					int pos = sCheckPath.indexOf(ParseStrUtil.SERVLETPATH_PARAMTOR);
					if (pos > 0) {
						sCheckPath = sCheckPath.substring(0, pos);
					}
					InputStream input = context.getResourceAsStream(sCheckPath);// 由于页面资源文件可能存在jar包中，需要直接使用资源访问才能探查jar中文件是否存在
					if (null != input) {
						branchViewdata.isHasBranchView = true;
						branchViewdata.BranchViewPath = locpath;
						input.close();
					}
				}else{
					log.error("用户站点信息为空，探查分支版失败,使用默认版本处理");
				}
			}
		} catch (Exception e) {
			log.error("探查分支版本出现异常,使用默认版本处理", e);
		}
		return branchViewdata;
	}

	@Override
	public void initClass(ApplicationContext context) throws Exception {
		ItcMvcService itcMvcService = context.getBean(ItcMvcService.class);
		FrameworkViewBranchUtil.initItcMvcService(itcMvcService);
	}
	
	static public void initItcMvcService(ItcMvcService ts){
		FrameworkViewBranchUtil.itcMvcService = ts;
	}
}
