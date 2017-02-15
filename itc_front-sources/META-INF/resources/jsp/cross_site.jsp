<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.yudean.itc.code.ParamConfig"%>
<%@ page import="com.yudean.itc.util.Constant" %>
<%
 	String basePath = (String)request.getAttribute(ParamConfig.S_BasePathKey);
	String g = (String)request.getAttribute("g");
	String mode = request.getParameter("mode");
	String jspPath = Constant.jspPath;
	String resBase = Constant.resBase;
	String uid = (String)request.getAttribute("uid");
	String password = (String)request.getAttribute("password");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<script type="text/javascript" src="<%=basePath %><%=resBase %>js/jquery-1.10.2.js"></script>
<script type="text/javascript" src="<%=basePath%><%=resBase %>itcui/js/itcui.dev.js"></script>
<link rel="stylesheet" type="text/css"
	href="<%=basePath%><%=resBase %>itcui/css/itcui.dev.css" media="all" />
<link rel="stylesheet" type="text/css"
	href="<%=basePath%><%=resBase %>css/public_background.css" media="all" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>站点选择</title>
<style>
</style>
<script>
	var basePath = "<%=basePath%>";
	var jspPath = "<%=jspPath%>";
	
	function go(){
		window.location.href = "<%=basePath %>login?method=index";
	}
	
	$(document).ready(function(){
		$.ajax({
			url : "<%=basePath%>login?method=login",
			dataType : "json",
			type : "POST",
			data : {
				"uid" : "<%=uid%>",
				"password" : "<%=password%>",
				"from" : "sso"
			},
			error : function(){
				$("body").html("<p>服务器或者网络故障，请稍后再试。</p>");
			},
			success : function(data){
				if(data.status==1){
					window.location.href = "<%=basePath%>login?method=index";
				}
				else if(data.status==2){
					//跨站
					var src = "<%=basePath %><%=resBase %>jsp/select_org_new.jsp";
					var dlgOpts = {
				        width : 450,
				        height: 200,
				        closed : false,
				        title:"选择要登陆的站点",
				        modal:true,
				        noButtons:true
				    };
					Notice.dialog(src,dlgOpts,null);
				}
				else{
					$("body").html("<p>登陆失败，原因：" + data.msg + "</p>");
				}
			}
		});
	});
</script>
</head>
<body class="bbox">
	
</body>
</html>