package com.yudean.interfaces.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.yudean.itc.dao.sec.SecureFunctionMapper;
import com.yudean.itc.dto.sec.FavRoute;
import com.yudean.itc.manager.sec.ISecurityMaintenanceManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yudean.homepage.bean.WorktaskBean;
import com.yudean.homepage.bean.WorktaskUserBean;
import com.yudean.homepage.service.HomepageFrontService;
import com.yudean.homepage.vo.WorktaskFlowViewObj;
import com.yudean.homepage.vo.WorktaskViewObj;
import com.yudean.interfaces.interfaces.EipMobileInterface;
import com.yudean.interfaces.service.IEipInterfaceService;
import com.yudean.itc.annotation.EipAnnotation;
import com.yudean.itc.bean.AnnotClass;
import com.yudean.itc.code.ParamConfig;
import com.yudean.itc.dto.Page;
import com.yudean.itc.dto.interfaces.eip.TaskListBean;
import com.yudean.itc.dto.interfaces.eip.WorkflowBean;
import com.yudean.itc.dto.interfaces.eip.WorkflowBeanModule;
import com.yudean.itc.dto.interfaces.eip.mobile.ParamDetailBean;
import com.yudean.itc.dto.interfaces.eip.mobile.ParamProcessBean;
import com.yudean.itc.dto.interfaces.eip.mobile.RetContentBean;
import com.yudean.itc.dto.interfaces.eip.mobile.RetContentInLineBean;
import com.yudean.itc.dto.interfaces.eip.mobile.RetDetailBean;
import com.yudean.itc.dto.interfaces.eip.mobile.RetKeyValue;
import com.yudean.itc.dto.interfaces.eip.mobile.RetProcessBean;
import com.yudean.itc.dto.interfaces.eip.mobile.SelfContentBean;
import com.yudean.itc.dto.sec.SecureUser;
import com.yudean.itc.dto.support.AppModule;
import com.yudean.itc.exception.sec.AuthenticationException;
import com.yudean.itc.interfaces.AnnotationConfigInterface;
import com.yudean.itc.manager.sec.IAuthenticationManager;
import com.yudean.itc.manager.sec.IAuthorizationManager;
import com.yudean.itc.manager.support.IAnnotationConfigManager;
import com.yudean.itc.manager.support.IModuleManager;
import com.yudean.itc.util.ClassCastUtil;
import com.yudean.itc.util.Constant;
import com.yudean.itc.util.StringHelper;
import com.yudean.mvc.bean.userinfo.impl.UserInfoImpl;
import com.yudean.mvc.configs.MvcWebConfig;
import com.yudean.mvc.handler.InitThreadHandler;
import com.yudean.mvc.util.LogUrlUtil;

@Service
public class EipInterfaceService implements IEipInterfaceService, AnnotationConfigInterface {
	static private String s_Empty = "Empty";
	static private String s_SUCCESS = "SUCCESS";
	static private String s_INFO_TASKION = "EIP接口获取待办列表，用户:";
	static private String s_INFO_TIME = ".时间:";
	static private String s_INFO_ENDTIME = "结束时间:";
	static private String s_INFO_MOBILEDETAIL = "EIPM接口获取待办表单详情，用户:";
	static private String s_INFO_PROCESSMOBILEDETAIL = "EIPM接口处理流程，用户:";
	static private String s_INFO_AuthenticationException = "用户登陆异常，原因：";

	private static final Logger log = Logger.getLogger(EipInterfaceService.class);



	@Autowired
	private HomepageFrontService homeService;
	@Autowired
	private IAuthenticationManager authenticationManager;
	@Autowired
	private IAnnotationConfigManager annotationConfigManager;
	@Autowired
	private IAuthorizationManager authorizationManager;
	@Autowired
	private IModuleManager moduleManager;
	@Autowired
	private ISecurityMaintenanceManager securityMaintenanceManager;
	@Autowired
	private SecureFunctionMapper secureFunctionMapper;

	/**
	 * 接口列表
	 */
	private List<EipMobileInterfaceData> interfaceList;

	@Override
	public TaskListBean getWorkflowTaskList(String userid, String password, String url) {
		TaskListBean eipDoing = new TaskListBean();
		try {
			log.info(StringHelper.concat(s_INFO_TASKION, userid, s_INFO_TIME, ParamConfig.S_MSTIME_FORMATTER.format(new Date())));
			SecureUser secuser = authenticationManager.signIn(userid, password,false);
			if (null != secuser) {
				Page<WorktaskViewObj> page = new Page<WorktaskViewObj>();

				page.setPageNo(1);
				page.setPageSize(500);
				page.setParameter(ParamConfig.HOP_UserCode, userid);
				page.setParameter(ParamConfig.HOP_ClassType, WorktaskBean.WorkTaskClass.Processed);
				page.setParameter(ParamConfig.HOP_Flag, WorktaskUserBean.WorkTaskUserFlag.Cur);

				page = homeService.getDoingTaskList(page, null);
				List<WorktaskViewObj> worktaskList = page.getResults();
				if (worktaskList.isEmpty()) {
					eipDoing.setRetcode(0);
					eipDoing.setRetmsg(s_Empty);
				}
				List<WorkflowBean> items = new ArrayList<WorkflowBean>();
				for (WorktaskViewObj task : worktaskList) {
					WorkflowBean eipDoingOne = new WorkflowBean();
					eipDoingOne.setId(task.getFlowno());
					eipDoingOne.setTitle(task.getName());
					eipDoingOne.setDate(ParamConfig.S_SSTIME_FORMATTER.format(task.getStatusdate()));
					eipDoingOne.setLink(LogUrlUtil.loginWithOpenWorkflowNoPasswd(MvcWebConfig.serverBasePath, task.getUrl(), task.getFlowno(), task.getSiteid(), userid));
					eipDoingOne.setOriginalUser(task.getCreateusername());
					eipDoingOne.setPreviousUser(task.getCurusername());
					eipDoingOne.setStatus(task.getStatusname());
					EipMobileInterfaceData InterfaceData = getEipMobileInterfaceImpl(task.getFlowno());
					WorkflowBeanModule workflowBeanModule = new WorkflowBeanModule();
					if (null != InterfaceData) {
						workflowBeanModule.setCode(InterfaceData.prefix);
					}
					workflowBeanModule.setName(task.getTypename());

					eipDoingOne.setModule(workflowBeanModule);
					eipDoingOne.setSiteid(task.getSiteid());
					items.add(eipDoingOne);
				}
				eipDoing.setItems(items);
				eipDoing.setRetcode(1);
				eipDoing.setRetmsg(s_SUCCESS);
			} else {
				eipDoing.setRetcode(-1);
				eipDoing.setRetmsg("EIP接口获取待办失败，用户密码错误");
			}
			log.info(StringHelper.concat(userid, s_INFO_ENDTIME, ParamConfig.S_MSTIME_FORMATTER.format(new Date())));
		} catch (AuthenticationException ae) {
			// 如果是登陆异常日志，不输出堆载
			eipDoing.setRetcode(-1);
			eipDoing.setRetmsg(ae.getMessage());
			log.warn(StringHelper.concat(s_INFO_AuthenticationException, ae.getMessage()));
		} catch (Exception e) {
			eipDoing.setRetcode(-1);
			eipDoing.setRetmsg(e.getMessage());
			log.error("EIP接口获取获取待办列表异常：" + userid, e);
		}
		return eipDoing;
	}

	@Override
	public RetDetailBean getTaskDetailMobile(String userid, String password, String flowNo, String siteId) {
		RetDetailBean retDetailBean = new RetDetailBean();
		try {
			log.info(StringHelper.concat(s_INFO_MOBILEDETAIL, userid, s_INFO_TIME, ParamConfig.S_MSTIME_FORMATTER.format(new Date())));
			SecureUser secuser = authenticationManager.signIn(userid, password,false);
			if (null != secuser) {
				// 初始化登陆信息，模拟生成用户线程数据
				SecureUser secUser = authorizationManager.retriveUserById(userid, siteId);
				UserInfoImpl userInfo = ClassCastUtil.castAllField2Class(UserInfoImpl.class, secUser);
				InitThreadHandler.initScopeData(userInfo);

				WorktaskFlowViewObj worktaskviewObj = homeService.getWorkTaskInfoByFlowNo(flowNo);

				ParamDetailBean detailParamBean = new ParamDetailBean();
				detailParamBean.setFlowNo(flowNo);
				detailParamBean.setProcessId(worktaskviewObj.getProcessid());

				EipMobileInterface eipMobileinter = getEipMobileInterfaceImpl(flowNo).mobileInterface;
				RetContentBean retContentBean = eipMobileinter.retrieveWorkflowFormDetails(detailParamBean);

				checkretContent(retContentBean);

				SelfContentBean selfContentBean = new SelfContentBean();

				selfContentBean.setAttachments(retContentBean.getAttachments());
				selfContentBean.setFlows(retContentBean.getFlows());
				selfContentBean.setContent(retContentBean.getContent());
				selfContentBean.setUsualOpinions(retContentBean.getUsualOpinions());

				selfContentBean.setId(flowNo);
				selfContentBean.setTitle(worktaskviewObj.getName());
				selfContentBean.setDate(ParamConfig.S_TIME_FORMATTER.format(worktaskviewObj.getStatusdate()));
				selfContentBean.setAuthor(worktaskviewObj.getCreateusername());
				selfContentBean.setSiteid(siteId);
				retDetailBean.setRetcode(1);
				retDetailBean.setRetmsg(s_SUCCESS);
				retDetailBean.setContent(selfContentBean);
			} else {
				retDetailBean.setRetcode(-1);
				retDetailBean.setRetmsg("EIPM接口获取待办表单详情失败，用户密码错误");
			}
			log.info(StringHelper.concat(userid, s_INFO_ENDTIME, ParamConfig.S_MSTIME_FORMATTER.format(new Date())));
		} catch (AuthenticationException ae) {
			// 如果是登陆异常日志，不输出堆载
			retDetailBean.setRetcode(-1);
			retDetailBean.setRetmsg(ae.getMessage());
			log.warn(StringHelper.concat(s_INFO_AuthenticationException, ae.getMessage()));
		} catch (Exception e) {
			retDetailBean.setRetcode(-1);
			retDetailBean.setRetmsg(e.getMessage());
			log.error("EIPM接口获取表单详情异常：" + userid, e);
		}
		return retDetailBean;
	}

	private void checkretContent(RetContentBean retContentBean) {
		try {
			List<RetContentInLineBean> retContList = retContentBean.getContent();
			if (null != retContList && !retContList.isEmpty()) {
				for (RetContentInLineBean contentBean : retContList) {
					if (RetContentInLineBean.Type.KeyValue.equals(contentBean.getType())) {
						List<Object> objList = contentBean.getValue();
						for (Object obj : objList) {
							RetKeyValue keyValue = (RetKeyValue) obj;
							String key = keyValue.getKey();
							if (5 < key.length()) {
								log.info("表头字段大于5");
							}
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("测试表单异常", e);
		}
	}

	@Override
	public RetProcessBean processTaskDetailMobile(String userid, String password, String itemid, String opinion, String siteId, String flowid, String taskKey, String nextUsers,
			String url,HttpServletRequest request) {
		RetProcessBean retProcessBean = null;
		boolean isProcessSuc = false;
		String errInfo = "流程执行异常";
		try {
			log.info(StringHelper.concat(s_INFO_PROCESSMOBILEDETAIL, userid, s_INFO_TIME, ParamConfig.S_MSTIME_FORMATTER.format(new Date())));
			SecureUser secuser = authenticationManager.signIn(userid, password, false);
			if (null != secuser) {
				// 初始化登陆信息，模拟生成用户线程数据
				
				SecureUser secUser = authorizationManager.retriveUserById(userid, siteId);
				UserInfoImpl userInfo = ClassCastUtil.castAllField2Class(UserInfoImpl.class, secUser);
                //20160719 zhx  这里生成的线程数据有不足，以后如果eip改成可以修改数据，这里就不适用了
//				InitThreadHandler.initScopeData(userInfo);
				HttpSession session = request.getSession();
				session.setAttribute(Constant.secUser, userInfo);
				session.setAttribute("username", userInfo.getName());
				// 初始化线程数据，请求的生命周期内，都可以获取到request的数据
				InitThreadHandler.initRequestScopeData(request, null, authenticationManager);

				WorktaskFlowViewObj viewObj = homeService.getWorkTaskInfoByFlowNo(itemid);

				ParamProcessBean processParamBean = new ParamProcessBean();
				processParamBean.setFlowID(flowid);
				processParamBean.setFlowNo(itemid);// 设置流水号
				processParamBean.setProcessId(null == viewObj ? null : viewObj.getProcessid());// 设置流程实例编号
				processParamBean.setOpinion(opinion);// 设置审批意见
				processParamBean.setTaskKey(taskKey);
				List<String> userList = new ArrayList<String>();
				userList.add(nextUsers);
				processParamBean.setNextUser(userList);
				processParamBean.setHost(url);

				EipMobileInterface eipMobileinter = getEipMobileInterfaceImpl(itemid).mobileInterface;

				retProcessBean = eipMobileinter.processWorkflow(processParamBean);
				isProcessSuc = true;
			} else {
				errInfo = "用户密码错误";
			}
			log.info(StringHelper.concat(userid, s_INFO_ENDTIME, ParamConfig.S_MSTIME_FORMATTER.format(new Date())));
		} catch (AuthenticationException ae) {
			// 如果是登陆异常日志，不输出堆载
			log.warn(StringHelper.concat(s_INFO_AuthenticationException, ae.getMessage()));
			errInfo = ae.getMessage();
		} catch (Exception e) {
			log.error("获取表单详情异常：" + userid, e);
			errInfo = e.getMessage();
		}
		if (!isProcessSuc) {
			retProcessBean = new RetProcessBean();
			retProcessBean.setRetcode(-1);
			retProcessBean.setRetmsg(errInfo);
		}
		return retProcessBean;
	}

	/**
	 * 获取表单业务模块实现类
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-9-22
	 * @return
	 * @throws Exception
	 *             :
	 */
	private EipMobileInterfaceData getEipMobileInterfaceImpl(String flowNo) throws Exception {
		EipMobileInterfaceData eipMobileinter = null;
		if (null != interfaceList) {
			for (EipMobileInterfaceData interfacee : interfaceList) {
				if (flowNo.startsWith(interfacee.prefix)) {
					eipMobileinter = interfacee;
					break;
				}
			}
			if (null == eipMobileinter) {
				log.warn("EIP相关的处理业务模块结构未找到,表单流水号" + flowNo + "。返回NULL");
			}
		}
		return eipMobileinter;
	}

	@Override
	public void initAnnotationConfigInter() {
		try {
			// init module
			List<AppModule> moduleList = moduleManager.retrieveAllModule();

			// init annotation
			Map<String, EipMobileInterface> map = new HashMap<String, EipMobileInterface>();
			List<AnnotClass> annotList = annotationConfigManager.getAnnotationClass();
			for (AnnotClass anntoClass : annotList) {
				EipAnnotation eipAnnot = anntoClass.getAnnot(EipAnnotation.class);
				if (null != eipAnnot) {
					eipAnnot.value();
					map.put(eipAnnot.value(), (EipMobileInterface) anntoClass.getInstace());
				}
			}
			log.info("初始化模块定义配置已成功加载:"+annotList.size()+"条");
			// init interface
			interfaceList = new ArrayList<EipMobileInterfaceData>();
			for (AppModule module : moduleList) {
				EipMobileInterface interfacee = map.get(module.getCode());
				if (null != interfacee) {
					interfaceList.add(new EipMobileInterfaceData(module.getFlowPrefix(), module.getModulecode(), module.getModulename(), module.getCode(), interfacee));
				}
			}
		} catch (Exception e) {
			log.error("初始化模块定义配置异常，无法执行EIP接口", e);
		}
	}

	@Override
	public Map<String, Object> getUserFavouriteRoute(SecureUser user) {
		Map<String, Object> ret = new HashMap<String, Object>();
		try{
			List<FavRoute> favRoutes = securityMaintenanceManager.selectFavRoute(user);
			ret.put("items", favRoutes);
			ret.put("retcode", 1);
			ret.put("retmsg", "success");
		}catch(Exception ex){
			log.error("在获取用户收藏时出现错误", ex);
			ret.put("retcode", -1);
			ret.put("retmsg", "系统错误，请稍后再试");
		}
		return ret;
	}

	@Override
	public Map<String, Object> cachedSignIn(String userId, String password, String siteId) throws AuthenticationException {
		Map<String, Object> ret = new HashMap<String, Object>();
		String token = authenticationManager.cachedSignIn(userId, password, siteId);
		List<String> funList = new ArrayList<String>();
		if(token != null){
			SecureUser user = authenticationManager.signWithToken(token);
			List<String> exporedFunctions = secureFunctionMapper.getExportedFunctions(user.getCurrentSite());
			List<String> ownedFunctions = user.getPrivileges();
			Map<String, Boolean> funMap = new HashMap<String, Boolean>();
			for(String s: exporedFunctions){
				funMap.put(s, true);
			}
			for(String s: ownedFunctions){
				if(funMap.containsKey(s)){
					funList.add(s);
				}
			}
		}
		ret.put("token", token);
		ret.put("functions", funList);
		return ret;
	}

	@Override
	public SecureUser signInWithKey(String key) {
		return authenticationManager.signWithToken(key);
	}

	/**
	 * 存储接口的数据对象
	 * 
	 * @company: gdyd
	 * @className: EipInterfaceService.java
	 * @author: kChen
	 * @createDate: 2014-9-23
	 * @updateUser: kChen
	 * @version: 1.0
	 */
	class EipMobileInterfaceData {
		String prefix;
		String moduleCode;
		String moduleName;
		String code;
		EipMobileInterface mobileInterface;

		EipMobileInterfaceData(String prefix, String moduleCode, String moduleName, String code, EipMobileInterface mobileInterface) {
			this.prefix = prefix;
			this.moduleCode = moduleCode;
			this.moduleName = moduleName;
			this.code = code;
			this.mobileInterface = mobileInterface;
		}
	}
}
