<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	String g = (String)request.getAttribute("g");
	String mode = request.getParameter("mode");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<jsp:include page="/page/framework/auth_include.jsp" flush="false" />
<script type="text/javascript" src="${basePath}${resBase}js/servletjs/edit_role.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>编辑角色</title>
<style>
.layout-panel-east{
	border-left-color: #C6C6C6;
	border-left-width: 1px;
	border-left-style: solid;
}
</style>
<script>
	var g = <%=g%> || {privs:{},groups:{},users:{},relatedorgs:{}};
	var _g = <%=g%> || {privs:{},groups:{},users:{}};
	var mode = "<%=mode%>";
</script>
</head>
<body class='bbox'>
	<div class="btn-toolbar toolbar-with-pager bbox">
		<div class="btn-group btn-group-sm">
			<button class="btn-default btn" onclick="goBack()">返回</button>
		</div>
		<div class="btn-group btn-group-sm" id="btnEdit">
			<button class="btn-default btn" onclick="beginEdit();">编辑</button>
		</div>
		<div class="btn-group btn-group-sm" id="btnSave" style="display:none">
			<button type="button" class="btn-default btn" onclick="save()">保存</button>
		</div>
		<div class="btn-group-sm btn-group">
			<button class="btn-default btn" onclick="delRole()" id="btnDel">删除</button>
		</div>
		
		<div class="btn-group-sm btn-group">
			<button class="btn-default btn" id="btnUser" style="display:none" onclick="selectuser()">修改角色包含的用户</button>
			<button class="btn-default btn" id="btnGroup" style="display:none" onclick="selectgroup()">修改包含该角色的组</button>
			<button class="btn-default btn" id="btnPriv" style="display:none" onclick="selectpriv()">修改拥有的权限</button>
		</div>
	</div>
	<% if(mode.equals("edit")){%>
	<div class="inner-title">编辑角色</div>
	<%}else{ %>
	<div class="inner-title">新建角色</div>
	<%} %>
	<div id="baseInfo" style="margin-top:10px">
		<form id="role_form">
		</form>
	</div>
	<div style="clear:both"></div>
	<div id="roleUser" grouptitle="拥有该角色的用户">
		<div id="userTreeWrap" class="tree_wrap" style="clear:both">
			<div id="userTree">
			</div>
		</div>
	</div>

	<div id="roleFunc" grouptitle="拥有的权限">
		<div id="privTreeWrap" class="tree_wrap">
			<div id="privTree">
			</div>
		</div>		
	</div>
	
</body>
</html>