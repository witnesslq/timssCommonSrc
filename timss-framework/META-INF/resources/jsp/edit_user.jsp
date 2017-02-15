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
<script type="text/javascript" src="${basePath}${resBase}js/servletjs/edit_user.js"></script>


<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>编辑用户</title>
<script>
	var g = <%=g%> || {roles:{},orgs:{},groups:{}};
	var _g = <%=g%> || {roles:{},orgs:{},groups:{}};
	var mode = "<%=mode%>";	
</script>
<style>
	
</style>
</head>
<body class="bbox">
	<div class="toolbar-with-pager btn-toolbar">
		<div class="btn-group btn-group-sm">
			<button class="btn-default btn" onclick="goBack()">返回</button>
		</div>
		<div class="btn-group btn-group-sm" id="btnEdit">
			<button class="btn-default btn" onclick="beignEdit()">编辑</button>
		</div>
		<div class="btn-group btn-group-sm" id="btnSave" style="display:none">
			<button class="btn-default btn" onclick="save();">保存</button>
		</div>
		<div class="btn-group btn-group-sm">
			<button class="btn-default btn" id="btnResetPass" onclick="showEditPass()">重设密码</button>
		</div>
		<div class="btn-group-sm btn-group">
			<button class="btn-default btn" id="btnDisable" onclick="setUserStat()">禁用</button>
			<button class="btn-default btn" onclick="delUser()" id="btnDel">删除</button>
		</div>
		
		<div class="btn-group-sm btn-group">
			<button class="btn-default btn" id="btnOrg" style="display:none" onclick="selectorg()">修改公司/部门</button>
			<button class="btn-default btn" id="btnRole" style="display:none" onclick="selectrole()">修改角色</button>
			<button class="btn-default btn" id="btnGroup" style="display:none" onclick="selectgroup()">修改用户组</button>
		</div>
	</div>
	<% if(mode.equals("edit")){%>
	<div class="inner-title">编辑用户</div>
	<%}else{ %>
	<div class="inner-title">新建用户</div>
	<%} %>
	<div style="clear:both"></div>
	<div id="base_info" style="margin-top:10px">
		<form id="form_baseinfo">
		</form>
	</div>	
</body>
</html>