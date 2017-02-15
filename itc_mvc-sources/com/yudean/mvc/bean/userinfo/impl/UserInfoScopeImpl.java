package com.yudean.mvc.bean.userinfo.impl;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import com.yudean.itc.annotation.VaildParam;
import com.yudean.itc.bean.environment.RuntimeEnvironmentData;
import com.yudean.itc.dto.Page;
import com.yudean.itc.util.JavaBeanHelper;
import com.yudean.itc.util.json.JsonHelper;
import com.yudean.mvc.bean.handler.ThreadLocalVariable;
import com.yudean.mvc.bean.userinfo.UserInfoScope;
import com.yudean.mvc.context.MvcContext;
import com.yudean.mvc.exception.MvcRuntimeException;
import com.yudean.mvc.handler.ThreadLocalHandler;

@SuppressWarnings("serial")
/**
 * 用户数据区域对象，在每次用户请求时创建，继承自UserInfoImpl(secUser),实现UserInfoScope接口。在运行时可视作UserInfo(secUser)对象，并动态注入 每次访问数据
 * @author kChen
 *
 */
public class UserInfoScopeImpl extends UserInfoImpl implements UserInfoScope {
	private static final Logger LOG = Logger.getLogger(UserInfoScopeImpl.class);
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected HttpSession session;

	@Override
	public void setRequest(HttpServletRequest request) throws RuntimeException {
		this.request = request;
	}

	@Override
	public void setSession(HttpSession session) throws RuntimeException {
		this.session = session;
	}

	@Override
    public HttpSession getSession() {
		return session;
	}

	@Override
	public String getParam(String paramName) throws RuntimeException {
		if (null != request) {
			return request.getParameter(paramName);
		} else {
			return null;
		}
	}

	@Override
	public <T> T getJavaBeanParam(String paramName, Class<T> clazz) throws NullPointerException {
		String json = this.getParam(paramName);
		T object = null;
		try {
			object = JsonHelper.toObject(json, clazz);
		} catch (JsonParseException e) {
			LOG.error("JsonHelper JsonParseException error , throw MvcRuntimeException ", e);
			throw new MvcRuntimeException("转换JSON字符串异常");
		} catch (JsonMappingException e) {
			LOG.error("JsonHelper JsonMappingException error , throw MvcRuntimeException ", e);
			throw new MvcRuntimeException("转换JSON字符串异常");
		}
		return getValidParamValue(paramName, object);
	}

	private <T> T getValidParamValue(String paramName, T t) throws NullPointerException {
		VaildParam vaildParam = (VaildParam) ThreadLocalHandler.getVariable().getThreadLocalAttribute(
				ThreadLocalVariable.GlobalVarableScopeType.Frame_Annotation_VaildParam.toString());
		String[] validParams = vaildParam.paramName();
		try {
			for (String params : validParams) {
				if (params.equals(paramName)) {
					t = JavaBeanHelper.transDefineMuliteCode(t);
					break;
				}
			}
		} catch (Exception e) {
			LOG.error("转换数据异常，返回t", e);
		}
		return t;
	}

	@Override
	public Map<String, String[]> getParamMap() throws RuntimeException {
		if (null != request) {
			return request.getParameterMap();
		} else {
			return null;
		}
	}

	@Override
	public Integer getRows() throws RuntimeException {
		return getIntegerParam(request, getRuntimeEnvironmentData().getPageFlagPage(), 1);
	}

	@Override
	public Integer getCurrentPage() throws RuntimeException {
		return getIntegerParam(request, getRuntimeEnvironmentData().getPageFlagRows(), 15);
	}

	@Override
	public <T> Page<T> getPage() throws RuntimeException {
		Integer pgNum = getIntegerParam(request, getRuntimeEnvironmentData().getPageFlagPage(), 1);
		Integer pgCnt = getIntegerParam(request, getRuntimeEnvironmentData().getPageFlagRows(), 15);
		Page<T> page = new Page<T>();
		page.setPageNo(pgNum);
		page.setPageSize(pgCnt);
		return page;
	}

	private RuntimeEnvironmentData getRuntimeEnvironmentData() {
		return MvcContext.getCoreContext().getBean("RunEnvironmentData", RuntimeEnvironmentData.class);
	}

	private Integer getIntegerParam(HttpServletRequest request, String pname, Integer defVal) {
		if (null != request) {
			String s = request.getParameter(pname);
			try {
				return Integer.parseInt(s);
			} catch (Exception ex) {
				return defVal;
			}
		} else {
			return defVal;
		}
	}
}
