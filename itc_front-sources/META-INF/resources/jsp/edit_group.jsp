<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	String g = (String)request.getAttribute("g");
	String mode = request.getParameter("mode");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<jsp:include page="/page/sysconf/auth_include.jsp" flush="false" />
<script type="text/javascript" src="${basePath}${resBase}js/servletjs/edit_group.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>编辑用户组</title>
<style>
</style>
<script>
	var g = <%=g%> || {roles:{},users:{}};
	var _g = <%=g%> || {roles:{},users:{}};
	var mode = "<%=mode%>";
</script>
</head>
<body class="bbox">
	<div class="btn-toolbar toolbar-with-pager bbox">
		<div class="btn-group btn-group-sm">
			<button class="btn-default btn" onclick="goBack()">返回</button>
		</div>
		<div class="btn-group btn-group-sm" id="btnEdit">
			<button class="btn-default btn" onclick="beginEdit()">编辑</button>
		</div>
		<div class="btn-group btn-group-sm" id="btnSave" style="display:none">
			<button class="btn-default btn" onclick="save()">保存</button>
		</div>
		<div class="btn-group-sm btn-group" id="btnDel">
			<button class="btn-default btn" onclick="delGroup()">删除</button>
		</div>		
		<div class="btn-group-sm btn-group">
			<button class="btn-default btn" id="btnRole" style="display:none" onclick="selectrole()">修改拥有的角色</button>
			<button class="btn-default btn" id="btnUser" style="display:none" onclick="selectuser()">修改包含的用户</button>
		</div>
	</div>
	<% if(mode.equals("edit")){%>
	<div class="inner-title">编辑用户组</div>
	<%}else{ %>
	<div class="inner-title">新建用户组</div>
	<%} %>
	<div id="baseInfo" style="margin-top:10px">
		<form id="group_form">
		</form>
	</div>
	<div style="clear:both">
	</div>
	<div id="groupUser" grouptitle="该用户组包含的用户" >
		<div id="userTreeWrap" class="tree_wrap" style="clear:both">
			<div id="userTree">
			</div>
		</div>
	</div>
</body>
</html>