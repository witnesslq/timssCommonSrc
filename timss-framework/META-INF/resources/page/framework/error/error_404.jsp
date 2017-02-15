<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.timss.framework.bean.exception.ExceptionData.pageType"%>
<%@page import="com.timss.framework.util.TimssLogUtil"%>
<%@page import="com.timss.framework.bean.exception.ExceptionData"%>
<%@page import="com.timss.framework.bean.handler.ThreadLocalVariable"%>
<%@page import="com.timss.framework.mvc.handler.TimssThreadLocalHandler"%>
<% 
	TimssThreadLocalHandler ThreadlocIns = TimssThreadLocalHandler.getInstance();
	ThreadLocalVariable ThreadlocData = ThreadlocIns.getVariableIns();
	ExceptionData excData = null;
	pageType type = pageType.page;
	String location = "/page/framework/exception/pageNoExistsException.jsp";
	try{
		if(null != ThreadlocData){
			excData = ThreadlocData.getExceptionData();
			if(null != excData){
				type = excData.getType();
				location = excData.getPage();
			}
		}
	}catch(Exception e){
		TimssLogUtil.error("404 page Exception.use" + location, e);
	}
	if(pageType.page == type){	
 		String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath();
		response.sendRedirect(basePath + location);
	}
%>