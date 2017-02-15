<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.yudean.itc.dto.sec.SecureMenu"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>前端框架 自动配置演示</title>
<script type="text/javascript" src="<%=basePath %>js/jquery-1.10.2.js"></script>

<script type="text/javascript" src="<%=basePath %>itcui/js/itcui.dev.js"></script>
<script type="text/javascript"
	src="<%=basePath %>itcui/js/itcui_frame.dev.js"></script>

<link rel="stylesheet" type="text/css"
	href="<%=basePath %>itcui/css/itcui.dev.css" media="all" />
<link rel="stylesheet" type="text/css"
	href="<%=basePath %>itcui/css/itcui_frame.dev.css" media="all" />
<script>
		var basePath = "<%=basePath%>";
	</script>
<script type="text/javascript"
	src="<%=basePath %>res?withinit=1&f=config.js"></script>
<style>
.title_username {
	font-weight: bold;
	font-size: 12px;
	float: left;
	height: 50px;
	line-height: 50px;
	vertical-align: middle;
	margin-top: 3px;
	color: rgb(180, 180, 180);
}

.head_link {
	line-height: 26px;
	height: 20px;
	display: inline-block;
	margin-top: 12px;
	float: right;
	margin-right: 12px
}

.head_link li {
	float: right;
	list-style: none;
	font-size: 12px;
	padding-left: 6px;
	color: rgb(102, 102, 102);
}

.system_logo {
	background-image: url('images/system_logo.png');
	margin-top: 3px;
	float: left;
	width: 255px;
	height: 50px;
}
</style>
</head>

<body>
	<div style="width: 100%;height:90px;position:absolute">
		<span class="system_logo"></span> <span class="title_username">TEST/DEMO</span>
		<ul class="head_link">
			<li class="li_username"><a class="itcui_link"
				href="<%=basePath %>login?method=logout">退出</a>
			</li>
			<li>|</li>
			<li><a class="itcui_link" id="link_setting">设置</a>
			</li>
			<li>|</li>
			<li><a class="itcui_link" href="#">帮助</a>
			</li>
		</ul>
		<div class="itcui_nav_tab_container" id="itcui_nav_tab_container"
			style="clear:both;height: 36px;width: 100%"></div>
	</div>
	<div class="mainframe_bottom" id="mainframe_bottom"
		style="width:100%;height:100%;position:absolute;top:90px">
		<div id="mainframe_navtree"
			style="width:200px;height:400px;float:left;overflow:hidden;background-color:#F5F5F5"
			class="cbox"></div>
		<div id="mainframe_content" style="float:left"></div>
	</div>
</body>
</html>
