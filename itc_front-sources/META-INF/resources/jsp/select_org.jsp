<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.yudean.itc.code.ParamConfig"%>
<%@ page import="com.yudean.itc.util.Constant" %> 
<%
 	String basePath = (String)request.getAttribute(ParamConfig.S_BasePathKey);
	String resBase = Constant.resBase;
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
	<link rel="stylesheet" type="text/css" href="<%=basePath %>css/base.css" media="all"/>
	<link rel="stylesheet" type="text/css" href="<%=basePath%><%=resBase %>itcui/css/itcui.dev.css" media="all" />
	<script type="text/javascript" src="<%=basePath %>js/jquery-1.10.2.js"></script>
	<script type="text/javascript" src="<%=basePath%><%=resBase %>itcui/js/itcui.dev.js"></script>
	
	<title>选择站点</title>
	<script>
		$(document).ready(function(e){
			$('#set_default').iCheck({
		        checkboxClass: 'icheckbox_flat-blue',
		        radioClass: 'iradio_flat-blue',
		    });
			$.post("<%=basePath %>login?method=listsites",{},function(e){
				e = eval("(" + e + ")");
				if(e.status!=-1){
					var html = "";
					var i = 0;
					for(var key in e.data){
						var v = e.data[key];
						if(i==0){
							html += '<div class="row btn-group-sm"><button type="button" class="btn btn-default" style="width:180px;" onclick="switch_site(\'' + key + '\')">' + v + '</button></div>';
						}
						else{
							html += '<div class="row btn-group-sm"><button type="button" class="btn btn-default btn-group-sm" style="width:180px;" onclick="switch_site(\'' + key + '\')">' + v + '</button></div>';
						}
						i = i + 1;
					}
					$("#site_list").html(html);
				}
			});
		});
		function switch_site(id)
		{
			var checked = $("#set_default").parent().hasClass("checked")?1:0;
			$.post("<%=basePath %>login?method=switchsite&sid=" + id,{setdefault:checked},function(e){
				e = eval("(" + e + ")");
				if(e.status==1){
					_parent().window.location.reload();
				}
				else{
					FW.error(e.msg);
				}
			});			
		}
	</script>
</head>

<body style="margin:6px">
	<div style="width:100%;height:24px;">
		<input type="checkbox" id="set_default"></input>
		<label for="set_default ft12" class="pure-label">将此站点设置为默认登入的站点</label>
	</div>
	<div style="width:100%">		
		<div style="width:180px" class="middle" id="site_list">

		</div>
	</div>
</body>
</html>