<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.yudean.itc.code.ParamConfig"%>
<%@ page import="com.yudean.itc.util.Constant"%>
<%
	String g = (String)request.getAttribute("g");
 	String basePath = (String)request.getAttribute(ParamConfig.S_BasePathKey);
	String from = request.getParameter("from");
	String resBase = Constant.resBase;
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<script type="text/javascript"
	src="<%=basePath%><%=resBase %>js/jquery-1.10.2.js"></script>
<script type="text/javascript"
	src="<%=basePath%><%=resBase %>js/servletjs/const.js"></script>
<script type="text/javascript"
	src="<%=basePath%><%=resBase %>itcui/js/itcui.dev.js"></script>
<link rel="stylesheet" type="text/css"
	href="<%=basePath%><%=resBase %>itcui/css/itcui.dev.css" media="all" />
<link rel="stylesheet" type="text/css"
	href="<%=basePath%><%=resBase %>css/public_background.css" media="all" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>选择用户属于的组织</title>
<script>
	var chkOrg = {};
	
	$(document).ready(function(){
		var p = _parent().window.document.getElementById(_c.FRAME_USER).contentWindow.document.getElementById("user_div").contentWindow;
		var orgs = p.g.orgs;
		for(var k in orgs){
			chkOrg[k] = orgs[k];
		}
		$("#orgtree").tree({
			url : "<%=basePath%>tree?method=org&onlyorg=1",
			checkbox : true,
			cascadeCheck : false,
			onCheck : function(node, checked){
				var nid = node.id.split("_")[1];
				if(checked){
					chkOrg[nid] = node.text;
				}
				else{
					delete(chkOrg[nid]);
				}
			},
			loadFilter : function(data,parent){
				for(var i=0;i<data.length;i++){
					var node = data[i];
					var nid = node.id.split("_")[1];
					if(orgs[nid]){
						data[i].checked = true;
					}
					if(node.children){
						for(var j=0;j<data[i].children.length;j++){
							var cnode = data[i].children[j];
							var cnid = cnode.id.split("_")[1];
							if(orgs[cnid]){
								data[i].children[j].checked = true;
							}
						}
					}
				}
				return data;
			} 
		});
	});
	
	function getChecked(){
		return chkOrg;
	}
	
</script>
<style>
</style>
</head>
<body style="padding:6px">
	<div style="width:100%" id="orgtree"></div>
</body>
</html>