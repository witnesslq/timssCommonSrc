<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.yudean.itc.code.ParamConfig"%>
<%@ page import="com.yudean.itc.bean.exception.ExceptionData.pageType"%>
<%@ page import="com.yudean.mvc.util.LogUtil"%>
<%@ page import="com.yudean.itc.bean.exception.ExceptionData"%>
<%@ page import="com.yudean.mvc.bean.handler.ThreadLocalVariable"%>
<%@ page import="com.yudean.mvc.handler.ThreadLocalHandler"%>
<% 
	ThreadLocalHandler ThreadlocIns = ThreadLocalHandler.getInstance();
	ThreadLocalVariable ThreadlocData = ThreadlocIns.getVariableIns();
	ExceptionData excData = null;
	pageType type = pageType.page;
	String location = "/page/mvc/exception/pageNoExistsException.jsp";
	try{
		if(null != ThreadlocData){
			excData = ThreadlocData.getExceptionData();
			if(null != excData){
				type = excData.getType();
				location = excData.getPage();
			}
		}
	}catch(Exception e){
		LogUtil.error("404 page Exception.use" + location, e);
	}
	if(pageType.page == type){	
 		String basePath = (String)request.getAttribute(ParamConfig.S_BasePathKey);
		request.getRequestDispatcher("../exception/pageNoExistsException.jsp").forward(request,response) ;
		//response.sendRedirect(basePath + location);
	}
%>