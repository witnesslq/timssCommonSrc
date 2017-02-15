<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.yudean.itc.code.ParamConfig"%>
<%@page import="com.yudean.itc.bean.exception.ExceptionData.pageType"%>
<%@page import="com.yudean.mvc.util.LogUtil"%>
<%@page import="com.yudean.itc.bean.exception.ExceptionData"%>
<%@page import="com.yudean.mvc.bean.handler.ThreadLocalVariable"%>
<%@page import="com.yudean.mvc.handler.ThreadLocalHandler"%>
<% 
	ThreadLocalHandler ThreadlocIns = ThreadLocalHandler.getInstance();
	ThreadLocalVariable ThreadlocData = ThreadlocIns.getVariableIns();
	ExceptionData excData = null;
	String errData = "";
	pageType type = pageType.page;
	String location = "/page/framework/exception/timssRunException.jsp";
	try{
		if(null != ThreadlocData){
			excData = ThreadlocData.getExceptionData();
			if(null != excData){
				type = excData.getType();
				location = excData.getPage();
				errData = excData.getData().get("msg");
			}
		}
	}catch(Exception e){
		LogUtil.error("500 page Exception.use" + location, e);
	}
	if(pageType.page == type){	
 		String basePath = (String)request.getAttribute(ParamConfig.S_BasePathKey);
		request.getRequestDispatcher("../exception/timssRunException.jsp").forward(request,response) ;
		//response.sendRedirect(basePath + location);
	}
%>
<%=errData %>