<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.yudean.itc.code.ParamConfig"%>
<%@ page import="com.yudean.itc.util.Constant"%>
<%
	String g = (String)request.getAttribute("g");
 	String basePath = (String)request.getAttribute(ParamConfig.S_BasePathKey);
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
<title>选择要添加的权限</title>
<script>
	var basePath = "<%=basePath%>";
	var p = _parent().window.document.getElementById(_c.FRAME_ROLE).contentWindow;
	var chkPriv = {};
	var privs = p.g.privs;
	var isChecking = false;
	for(var k in privs){
		chkPriv[k] = privs[k];
	}
	$(document).ready(function(){
		$("#functree").tree({
			url : basePath + "tree?method=priv",
			cascadeCheck : false,
			loadFilter : function(data,parent){
				for(var i=0;i<data.length;i++){
					var node = data[i];
					var nid = node.id;
					if(chkPriv[nid]){
						data[i].checked = true;
					}
				}
				return data;
			},
			onCheck : function(node, checked){
				if(isChecking == true){
					//防止连贯动画再次触发onCheck事件
					return;
				}
				isChecking = true;
				var nid = node.id;
				if(checked){
					chkPriv[nid] = node.text;
				}
				else{
					delete(chkPriv[nid]);
				}
				//检查该节点的父级是否已经选择
				if(checked){
					var pnode = $("#functree").tree("getParent",node.target);
					while(pnode!=null && pnode.id!="MNU_0" && pnode.id!="FUN_F"){
						chkPriv[pnode.id] = node.text;
						$("#functree").tree("check",pnode.target);
						pnode = $("#functree").tree("getParent",pnode.target);
					}
				}
				if(node.children){
					$.ajax({
						url: basePath + "tree?method=privchildren&id=" + nid,
						dataType : "json",
						success : function(data){
							for(var i=0;i<data.length;i++){
								var nid = data[i].split(",")[0];
								var n = data[i].split(",")[1];
								if(checked){
									chkPriv[nid] = n;
								}
								else{
									delete(chkPriv[nid]);
								}
								//为了防止节点已经展开 还应该在已经展开的节点上做出变化
								var cnode = $("#functree").tree("find",nid);
								if(cnode){
									if(checked){
										$("#functree").tree("check",cnode.target);
									}
									else{
										$("#functree").tree("uncheck",cnode.target);
									}
								}
							}
							isChecking = false;
						},
						error : function(){
							isChecking = false;
						}
					});
				}
				else{
					isChecking = false;
				}
			},
			checkbox : true
		});
	});
	
	function getChecked(){
		return chkPriv;
	}
</script>
<style>
</style>
</head>
<body style="padding:6px">
	<div style="width:100%" id="functree"></div>
</body>
</html>