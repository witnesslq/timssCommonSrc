<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="com.yudean.itc.util.Constant" %> 
<%@ page import="com.yudean.itc.util.UserConfigHelper" %>
<%@ page import="com.yudean.itc.util.UserConfig" %> 
<%
	String path = request.getContextPath();
	String id = request.getParameter("id");
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	UserConfig conf = (UserConfig)UserConfigHelper.getPagerConfig(request);
  	String pageSize = conf.rows;
  	String theme = conf.theme;
  	String resBase = Constant.resBase;
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<script type="text/javascript" src="<%=basePath%><%=resBase %>js/jquery-1.10.2.js"></script>
<script type="text/javascript" src="<%=basePath%><%=resBase %>itcui/js/jquery.validate.js"></script>
<script type="text/javascript" src="<%=basePath%><%=resBase %>itcui/js/itcui.dev.js"></script>
<link rel="stylesheet" type="text/css"	href="<%=basePath%><%=resBase %>itcui/css/itcui.dev.css" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>修改密码</title>
<script>
	var basePath = "<%=basePath%>";
	var id = "<%=id%>";
	function go(){
		if(!$("#edit_lxuser").valid()){
			return null;
		}
		return {"password":$("#f_pass1").val()};
	}
	$(document).ready(function(){
		$("#edit_lxuser").validate({rules: {
			f_pass1: {
	            required: true,
	            minlength : 6,
	            maxlength : 15
	        },
	        f_pass2: {
	            required: true,
	            minlength : 6,
	            maxlength : 15,
	            equalTo : "#f_pass1"
	        }
	    },
	    messages:{
	    	f_pass1: {
	            required: "请输入密码",
	            minlength: "密码最少需要6位",
	            maxlength: "密码最长不能超过15位"
	        },
	        f_pass2: {
	            required: "请输入再次输入密码",
	            minlength: "密码最少需要6位",
	            maxlength: "密码最长不能超过15位",
	            equalTo : "两次输入密码不一致"
	        }
	    }
	    });
	});
</script>
<style>
.control-label{
	line-height:24px
}
.row{
	margin-top:8px;
}
.error{
	font-size:12px;
	color:#f00;
}
.reghint{
	font-size: 12px;
	color: #888;
	margin-left: 6px;
	line-height: 24px;
}
</style>
</head>
<body style="margin:6px">
	<form id="edit_lxuser">
		<div style="width:100%;font-size:12px" class="bbox">
			<div class="row">
				<label class="col-xs-3 control-label">用户名：</label>
				<div class="col-xs-8">
					<div class="input-group-sm col-xs-6 pure-label">
			    		<%=id %>
					</div>
				</div>
			</div>
			<div class="row">
				<label class="col-xs-3 control-label"><span style="color:#F00">*</span>输入新密码：</label>
				<div class="col-xs-8">
					<div class="input-group-sm col-xs-6">
			    		<input type="password" class="form-control" id="f_pass1" name="f_pass1">
					</div>
					<span class="pull-left reghint">8-15位字母或数字</span>
				</div>
			</div>
			<div class="row">
				<label class="col-xs-3 control-label"><span style="color:#F00">*</span>再次输入：</label>
				<div class="col-xs-8">
				    <div class="input-group-sm col-xs-6">
			    		<input type="password" class="form-control" id="f_pass2" name="f_pass2">
					</div>
				</div>
			</div>
		</div>			
	</form>
</body>
</html>