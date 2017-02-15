package com.yudean.mvc.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import com.yudean.itc.bean.environment.RuntimeEnvironmentData;
import com.yudean.mvc.bean.branch.BranchMethodData;
import com.yudean.mvc.bean.userinfo.UserInfo;
import com.yudean.mvc.interfaces.InitClassAfterContextBuildInterface;
import com.yudean.mvc.service.ItcMvcService;

/**
 * 处理TIMSS分支版本工具类，TIMSS框架包私有工具
 * 
 * @author kChen
 * 
 */
public class FrameworkServiceBranchUtil implements InitClassAfterContextBuildInterface {
	private static final Logger LOG = Logger.getLogger(FrameworkServiceBranchUtil.class);
	
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
	
	static public BranchMethodData ServiceBranchMethodCheck(Class<?> targetclass, Object[] targetargsList, final String methodName) throws Exception {
		
		BranchMethodData branchMethoddata = null;
		branchMethoddata = itcMvcService.getBeans("core_service_framework_ToolBranchMethodData", BranchMethodData.class);
		RuntimeEnvironmentData env = itcMvcService.getRunEnvironmentDatas();
		UserInfo userinfo = itcMvcService.getUserInfoScopeDatas();
		if (env.getBranchFlag() && null != userinfo) {// 检查分支版本是否启用
			branchMethoddata.isHasBranch = false;
			String classPath = targetclass.getName();// 调用类的全路径
			final String packageWildcard = env.getPackageWildcard();
			final String subPathWildcard = env.getSubPathwildcard();
			final String serviceCorePath = env.getServiceCorePackageRoot();// 配置的service
																			// 基本版本路径
			final String serviceLocationPath = env.getServiceLocationPackageRoot();// 配置的service

			String[] coreSplite = serviceCorePath.split(packageWildcard);
			classPath = classPath.replace(coreSplite[0], "");// 替换前缀
			int iPos = classPath.indexOf(coreSplite[1]);
			String sModuleName = classPath.substring(0, iPos);
			String sSubClassName = classPath.replace(sModuleName + coreSplite[1], "");
			Class<?>[] methodClassTypes = null;// 调用参数类对象
			try {
				methodClassTypes = ReflectUtil.getReflectMethodTypeAbsolute(methodName, targetargsList, targetclass);
				classPath = serviceLocationPath.replace(packageWildcard, sModuleName);// 获取分支方法的路径(包)
				StringBuffer sbSubClassName = new StringBuffer();
				sbSubClassName.append(userinfo.getSiteId().toLowerCase());
				sbSubClassName.append(".");
				sbSubClassName.append(sSubClassName);// 组装类名称
				classPath = classPath.replace(subPathWildcard, sbSubClassName.toString());
				Class<?> branchClass;
				branchClass = Class.forName(classPath);
				Method _barchMethod = branchClass.getMethod(methodName, methodClassTypes);// 对象化被调用的方法
				// 探查成功，组装内部对象参数
				branchMethoddata.method = _barchMethod;
				branchMethoddata.classPackagePath = classPath;
				branchMethoddata.setClassBeanNaem(classPath);
				branchMethoddata.parameterTypes = methodClassTypes;
				branchMethoddata.parameterList = targetargsList;
				branchMethoddata.clazz = branchClass;
				branchMethoddata.isHasBranch = true;
			} catch (NoSuchMethodException e) {
				LOG.debug("分支版本方法不存在");
			} catch (ClassNotFoundException e) {
				LOG.debug("分支版本类不存在");
			}// 对象化分支版本类
		}
		return branchMethoddata;
	};

	/**
	 * 根据方法指定参数执行定义的方法。
	 * 
	 * @param methodData
	 * @return
	 */
	static public Object ServiceBranchMethodDual(BranchMethodData methodData) throws Throwable {
		Object ret = null;
		Object Obj = itcMvcService.addBeans(methodData.classBeanNaem, methodData.clazz);// 实例化分支版本类，注入到全局容器中

		Class<? extends Object> clazz = Obj.getClass();
		ret = null;
		if (Proxy.class.isAssignableFrom(clazz)) {
			// 处理代理类
			InvocationHandler hand = Proxy.getInvocationHandler(Obj);
			ret = hand.invoke(Obj, methodData.method, methodData.parameterList);
		} else {
			// 处理正常类
			ret = methodData.method.invoke(Obj, methodData.parameterList);// 调用分支版本方法
		}
		return ret;
	}

	@Override
	public void initClass(ApplicationContext context) throws Exception {
		ItcMvcService itcMvcService = context.getBean(ItcMvcService.class);
		FrameworkServiceBranchUtil.initItcMvcService(itcMvcService);
	}
	
	static public void initItcMvcService(ItcMvcService ts){
		FrameworkServiceBranchUtil.itcMvcService = ts;
	}
}
