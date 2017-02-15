<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.yudean.itc.code.ParamConfig"%>
<%@ page import="com.yudean.itc.util.Constant" %>    
<%
	String g = (String)request.getAttribute("g");
 	String basePath = (String)request.getAttribute(ParamConfig.S_BasePathKey);
	String from = request.getParameter("from");
	String resBase = Constant.resBase;
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<script type="text/javascript" src="<%=basePath%><%=resBase %>js/jquery-1.10.2.js"></script>
<script type="text/javascript" src="<%=basePath%><%=resBase %>js/servletjs/const.js"></script>
<script type="text/javascript" src="<%=basePath%><%=resBase %>itcui/js/itcui.dev.js"></script>
<link rel="stylesheet" type="text/css"
	href="<%=basePath%><%=resBase %>itcui/css/itcui.dev.css" media="all" />
<link rel="stylesheet" type="text/css"
	href="<%=basePath%><%=resBase %>css/public_background.css" media="all" />	
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>选择包含该角色的用户组</title>
<script>
	var inited = false;
	var sRows = [];
	var checkFinish = true;
	var checkedItems = {};
	$(document).ready(function(){
		var fid = "<%=from%>"=="user"?_c.FRAME_USER:_c.FRAME_ROLE;
		var p = _parent().window.document.getElementById(fid).contentWindow;
		if("<%=from%>"=="user"){
			p = p.document.getElementById("user_div").contentWindow;
		}
		for(var k in p.g.groups){
			checkedItems[k] = p.g.groups[k];
		}
		$.ajax({
			url : "<%=basePath%>group?method=listgroup",
			dataType : "json",
			type : "post",
			success : function(data){
				if(data.rows.length==0){
					$("#nodata").show();
					$("#table_wrap").hide();
					$("#search_wrap").hide();
					return;
				}
				$("#table_group").datagrid({
					"data" : data,
					"_data" : data,
					"scrollbarSize":0,
					"pagination":true,
					"fitColumns" : true,
			        "pageSize":9999,
			        "pageList":[5,10,15,20,9999],
					"onRenderFinish":function(){
						 if(!inited){
						 	$("#table_group").ITCUI_Pagination("create","#nouse");
						 	inited = true;
						 	search();
						 }
						 checkFinish = false;
						 for(var i=0;i<sRows.length;i++){
							 $("#table_group").datagrid("checkRow",sRows[i]);
						 }
						 checkFinish = true;
						 $("#table_group").iFixCheckbox();
					},
					"loadFilter":function(d){
			        	sRows = [];
			        	for(var i=0;i<d.rows.length;i++){
							if(checkedItems[d.rows[i].id]){
								sRows.push(i);
							}
			        	}
			        	return d;
					},
					"onCheck":function(rowIndex,rowData){
						if(checkFinish){
							checkedItems[rowData.id] = rowData.name;
						}
					},
					"onUncheck":function(rowIndex,rowData){
						if(checkFinish){
							delete(checkedItems[rowData.id]);
						}
					}
				
				});
			}
		});
		
	});
	var searchStat = false;
	function search(){
		if(searchStat){
			$("#table_group").ITCUI_GridSearch("end");
		}
		else{
			$("#table_group").ITCUI_GridSearch("init");			
		}
		searchStat = !searchStat;
	}
	
	function getChecked(){
		return checkedItems;
	}
</script>
<style>
	
</style>
</head>
<body style="padding:6px">
	<div id="nouse" style="display:none">
	</div>
	<div id="nodata" class="nodata" style="display:none">
		目前无可添加的用户组
	</div>
	<div id="table_wrap">
		<table id="table_group" class="easyui-datagrid" style="height:280px">
			<thead>
		        <tr>
		            <th data-options="field:'ck',checkbox:'true'"></th>
		            <th data-options="field:'name',width:192">用户组名</th>
		            <th data-options="field:'id',width:192">用户组ID</th>
		        </tr>
		    </thead>
		</table>
	</div>
</body>
</html>