package com.yudean.mvc.handler;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yudean.itc.manager.sec.IAuthenticationManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.yudean.itc.bean.exception.ExceptionData;
import com.yudean.itc.dto.Page;
import com.yudean.mvc.bean.branch.BranchViewData;
import com.yudean.mvc.bean.handler.BasedAccessInterceptorConfigData;
import com.yudean.mvc.bean.userinfo.UserInfo;
import com.yudean.mvc.exception.ExceptionFramework;
import com.yudean.mvc.service.impl.FrameWorkServiceImpl;
import com.yudean.mvc.util.ExceptionUtil;
import com.yudean.mvc.util.FormTokenUtil;
import com.yudean.mvc.util.FrameworkViewBranchUtil;
import com.yudean.mvc.util.LogUtil;
import com.yudean.mvc.view.ModelAndViewPage;
import com.yudean.mvc.view.viewResolver.JsonView;

/**
 * 拦截所有前端请求 Spring mvc的handler
 * 
 * @author kChen
 * 
 */

public class BasedAccessInterceptor extends HandlerInterceptorAdapter {
	private static final Logger LOG = Logger.getLogger(BasedAccessInterceptor.class);

	@Autowired
	BasedAccessInterceptorConfigData config;

	@Autowired
	ExceptionFramework excep;

	@Autowired
	IAuthenticationManager authenticationManager;

	static List<String> pageSufix = new ArrayList<String>();

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		try{
			InitThreadHandler.initRequestScopeData(request, response, authenticationManager);// 初始化线程数据
		}catch(Exception e){
			ExceptionData exceptionData = null;
			if(HandlerMethod.class.isAssignableFrom(handler.getClass())){
				HandlerMethod handlerMethod = (HandlerMethod) handler;
				try {
					exceptionData = ExceptionUtil.caseException(e, handlerMethod.getMethod(), excep);
				} catch (Exception e1) {
					LOG.error("容错框架工具异常", e1);
					throw e1;
				}
			}else{
				exceptionData = excep.TimssRunException(e);
			}
			InitThreadHandler.initExceptionData(exceptionData);
			throw e;
		}
		
		boolean isRight = false;
		UserInfo userinfo = ThreadLocalHandler.getVariable().getUserInfoScope();
		if (null != userinfo) {
			LogUtil.debug(userinfo.getSiteId());
		}
		try {
			isRight = super.preHandle(request, response, handler);
		} catch (Exception e) {
			isRight = false;
		}
		return isRight;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		try {
			String sViewPath = castModelAndView(request, response, handler, modelAndView);
			if (null != sViewPath) {
				Boolean branchflag = true;// 是否执行分支页面探查的标记
				Map<String, Object> modelmap = modelAndView.getModel();
				Boolean isBranch = (Boolean) modelmap.get("exceptionbranchflag");
				branchflag = null == isBranch ? branchflag : isBranch;
				if (branchflag) {// 是否执行分支页面探查的标记 只有标记为真时才探查
					String sModuleName = ThreadLocalHandler.getVariable().getModelName();
					BranchViewData _data = FrameworkViewBranchUtil.serviceBranchViewCheck(sViewPath, sModuleName, request.getServletContext());
					if (_data.isHasBranchView) {
						modelAndView.setViewName(_data.BranchViewPath);
					}
				}
			}
		} catch (Exception e) {
			LogUtil.error("处理视图层出现异常，采用默认处理", e);
		}
		super.postHandle(request, response, handler, modelAndView);
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		super.afterCompletion(request, response, handler, ex);
	}

	private String castModelAndView(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
			throws Exception {
		ExceptionData exceptionData = FrameWorkServiceImpl.getExceptionData();
		if (null != exceptionData) {
			Integer status = Integer.valueOf(exceptionData.getData().get("flag"));
			response.setStatus(status);
			return castException(request, response, handler, modelAndView, exceptionData);
		} else {
			return castRun(request, response, handler, modelAndView);
		}
	}

	/**
	 * 正常扭轉
	 * 
	 * @param request
	 * @param response
	 * @param handler
	 * @param modelAndView
	 * @return
	 * @throws Exception
	 */
	private String castRun(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		String sViewPath = modelAndView == null ? null : modelAndView.getViewName();
		HandlerMethod methodHandler = (HandlerMethod) handler;
		Class<?> returnType = methodHandler.getMethod().getReturnType();
		if (null == modelAndView || null == sViewPath || sViewPath.startsWith(config.getNoBranchPrefix())||startWithServletPrefix(sViewPath)) {
			return null;
		}
		if (String.class.isAssignableFrom(returnType) || ModelAndViewPage.class.isAssignableFrom(returnType)) {
			return sViewPath;
		} else {
			if (splitSufixCompo(sViewPath)) {
				return sViewPath;
			} else {
				Map<String, Object> model = modelAndView.getModel();
				Set<Entry<String, Object>> set = model.entrySet();
				Iterator<Entry<String, Object>> iter = set.iterator();
				while (iter.hasNext()) {
					Entry<String, Object> entry = iter.next();
					String key = entry.getKey();
					if (key.startsWith(config.getBindPrefix())) {
						Object obj = entry.getValue();
						BeanPropertyBindingResult binding = (BeanPropertyBindingResult) obj;// 从绑定值中获取数据
						Object injectData = binding.getTarget();
						Class<?> clazz = injectData.getClass();
						if (Page.class.isAssignableFrom(clazz)) {// 对page需要特殊处理
							Page<?> page = (Page<?>) injectData;
							List<?> list = page.getResults();

							Map<String, Object> json = new HashMap<String, Object>();// 返回数据字典，使用spring默认的jackson处理
							json.put(config.getBindTotlaPageFlag(), page.getTotalRecord());
							json.put(config.getBindRowsPageFlag(), list);

							modelAndView.setViewName("");
							modelAndView.setView(new JsonView());// 使用自定义view处理
							modelAndView.addObject(FrameWorkServiceImpl.getRunEnvironmentData().getAjaxJsonDataflag(), json);
							return null;
						} else if (JSONArray.class.isAssignableFrom(clazz) || JSONObject.class.isAssignableFrom(clazz)) {// 增加对net.sf.json的处理
							modelAndView.setViewName("");
							modelAndView.setView(new JsonView());
							modelAndView.addObject(FrameWorkServiceImpl.getRunEnvironmentData().getAjaxJsonDataflag(), injectData.toString());
							return null;
						} else {// 对javaBean进行处理，使用SPRING容器默认处理
							modelAndView.setViewName("");
							modelAndView.setView(new JsonView());
							modelAndView.addObject(FrameWorkServiceImpl.getRunEnvironmentData().getAjaxJsonDataflag(), injectData);
							return null;
						}
					}
				}
				modelAndView.setViewName("");// 构建基本数据雷响
				modelAndView.setView(new JsonView());
				return null;
			}
		}
	}

	/**
	 * 异常扭转
	 * 
	 * @param request
	 * @param response
	 * @param handler
	 * @param modelAndView
	 * @param exceptionData
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	private String castException(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView,
			ExceptionData exceptionData) throws Exception {
		String retView = null;
		if (null == modelAndView) {//如果无法获取modelAndView，则强制写入异常返回信息。
			LOG.warn("参数modelAndView为NULL，容错框架没有返回任何数据");
			String errData = exceptionData.getData().get(FrameWorkServiceImpl.getRunEnvironmentData().getAjaxDataName());
			response.setStatus(500, errData);
			Object _tokenObj = request.getAttribute(FormTokenUtil.TOKEN_ATTRIBUTE_NAME);
			if(null == _tokenObj){
				FormTokenUtil.bulidFormToken(request, null);
				_tokenObj = request.getAttribute(FormTokenUtil.TOKEN_ATTRIBUTE_NAME);
			}
			String _token = String.valueOf(_tokenObj);
			response.setHeader(FormTokenUtil.TOKEN_ATTRIBUTE_NAME, _token);
			
			PrintWriter out = response.getWriter();
			ObjectMapper om = new ObjectMapper();
			out.print(om.writeValueAsString("ERROR"));
			out.close();
		} else {
			if (ExceptionData.pageType.page == exceptionData.getType()) {
				modelAndView.setViewName(exceptionData.getPage());
				Object msgObj = exceptionData.getErrInfo();
				if (null != msgObj) {
					modelAndView.addObject("extData", msgObj);
				}
			} else {
				modelAndView.setViewName("");
				modelAndView.setView(new JsonView());
				Map<String, String> data = exceptionData.getData();
				Object msgObj = exceptionData.getErrInfo();
				if (null != msgObj) {
					String msg = "";
					if (Map.class.isAssignableFrom(msgObj.getClass())) {
						msg = JSONObject.fromObject(msgObj).toString();
					} else if (List.class.isAssignableFrom(msgObj.getClass())) {
						msg = JSONArray.fromObject(msgObj).toString();
					} else if (String.class.isAssignableFrom(msgObj.getClass())) {
						msg = (String) msgObj;
					}
					data.put("extData", msg);
				}
				modelAndView.addObject(FrameWorkServiceImpl.getRunEnvironmentData().getAjaxJsonDataflag(), data);
			}
		}
		return retView;
	}

	private boolean splitSufixCompo(String path) throws Exception {
		if (pageSufix.size() < 1) {
			String[] split = config.getSufix().split(",");
			for (int i = 0; i < split.length; i++) {
				pageSufix.add(split[i]);
			}
		}
		for (int i = 0; i < pageSufix.size(); i++) {
			if (path.endsWith(pageSufix.get(i)))
				return true;
		}
		return false;
	}
	
	private boolean startWithServletPrefix(String sViewPath){
		String[] servletPrefix = config.getServletPrefix();
		if(servletPrefix != null && servletPrefix.length != 0){
			for (int i = 0; i < servletPrefix.length; i++) {
				if(sViewPath.indexOf(servletPrefix[i])!= -1){
					LOG.info("sViewPath:" + sViewPath +"; 不需要拦截处理");
					return true;
				}
			}
		}
		return false;
	}
}
