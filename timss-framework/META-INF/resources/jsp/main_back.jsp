<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.yudean.itc.dto.sec.SecureMenu"%>
<%
	String username = (String)request.getSession().getAttribute("username");
	String currsite = (String)request.getSession().getAttribute("currsite");
	HashMap<String,Boolean> ids = (HashMap<String,Boolean>)request.getSession().getAttribute("privFunc");
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" style="width:100%;height:100%">
<head>
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>TIMSS</title>
<script type="text/javascript" src="<%=basePath %>js/jquery-1.10.2.js"></script>

<script type="text/javascript"
	src="<%=basePath %>res?f=privilege.js"></script>
<script type="text/javascript" src="http://10.0.250.52/uidev/beta/js/itcui.dev.js"></script>

<%--<script type="text/javascript" src="<%=basePath %>itcui/js/itcui_frame.dev.js"></script>--%>
<script type="text/javascript" src="http://10.0.250.52/uidev/beta/js/itcui_frame.dev.js"></script>

<link rel="stylesheet" type="text/css"
	href="http://10.0.250.52/uidev/beta/css/itcui.dev.css" media="all" />
<link rel="stylesheet" type="text/css"
	href="http://10.0.250.52/uidev/beta/css/itcui_frame.dev.css" media="all" />
<script>
		var basePath = "<%=basePath%>";
	</script>
<!-- <script type="text/javascript" src="<%=basePath %>js/config_backend.js"></script> -->
<script type="text/javascript"
	src="<%=basePath %>res?withinit=1&f=config.js"></script>
<style>
.title_username{
	font-weight: bold;font-size: 12px;float: left;height: 50px;line-height: 50px;vertical-align: middle;margin-top: 3px;color: rgb(180, 180, 180);
}
.head_link{
	line-height: 26px;
	height: 20px;
	display: inline-block;
	margin-top:12px;
	float:right;
	margin-right:12px
}

.head_link li{
	float: right;
	list-style: none;
	font-size: 12px;
	padding-left: 6px;
	color:rgb(102, 102, 102);
}

.system_logo{
	background-image:url('images/system_logo.png');
	margin-top: 3px;
	float: left;
	width:255px;
	height:50px;
}

.li_username{color:rgb(34, 34, 34)!important;}
.dropdown .open{z-index: 13333;}

#mainframe_navtree{
	height:100%;overflow:hidden;background-color:#F5F5F5;position:absolute;top:0px;left:0px;
}

#mainframe_bottom{
	width:100%;height:100%;position:relative;
}

.tree-width-p{
	padding-left:200px;
}

.tree-half-p{
	padding-left: 40px;
}

.tree-width{
	width:200px;
}

#mainframe_content{
	width:100%;
	height:100%;
	position: relative;
}

#itcui_nav_tab_container{
	clear:both;height: 36px;width: 100%;
}

.pleft230{
	padding-left:230px;
}
</style>
</head>
<body class="bbox" style="width:100%;height:100%;padding-top:89px!important">
	<div style="width: 100%;height:90px;overflow:hidden;position:absolute;top:0px">
		<span class="system_logo"></span>
		<span class="title_username"><%=username %><%=" 的桌面" %></span>
		<ul class="head_link">
			<li class="li_username"><a class="itcui_link" href="<%=basePath %>login?method=logout">退出</a></li>
			<li>|</li>
			<li><a class="itcui_link" id="link_setting">设置</a></li>
			<li>|</li>
			<li><a class="itcui_link" href="#">帮助</a></li>
		</ul>
		<div class="itcui_nav_tab_container" id="itcui_nav_tab_container">
			
		</div>
	</div>
	<div class="mainframe_bottom tree-width-p" id="mainframe_bottom">		
		<div id="mainframe_navtree" class="cbox tree-width">
			
		</div>
		<div id="mainframe_content">
			
		</div>
	</div>
</body>
</html>
