<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="com.yudean.itc.util.Constant" %>    
<%
	String g = (String)request.getAttribute("g");
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
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
<title>选择用户拥有的角色</title>
<script>
	var inited = false;
	var sRows = [];
	var rmvChkRows = [];
	var checkFinish = true;
	var checkedItems = {};
	$(document).ready(function(){
		var fid = "<%=from%>"=="user"?_c.FRAME_USER:_c.FRAME_GROUP;
		var p = _parent().window.document.getElementById(fid).contentWindow;
		if("<%=from%>"=="user"){
			p = p.document.getElementById("user_div").contentWindow;
		}
		for(var k in p.g.roles){
			checkedItems[k] = p.g.roles[k];
		}
		var columns = [{field:'ck',checkbox:'true'},
		{field:'name',width:192,title:"角色名"},
		{field:'id',width:192,title:"角色ID"}];       
		$.ajax({
			url : "<%=basePath%>role?method=listroles",
			dataType : "json",
			type : "post",						
			success : function(data){
				if(data.rows.length==0){
					$("#nodata").show();
					$("#table_wrap").hide();
					$("#search_wrap").hide();
					return;
				}
				for(var i=0;i<data.rows.length;i++){
					var row = data.rows[i];
					var tmp = p.g.roles[row.id];
					if(tmp && tmp.indexOf("(继承)")>0){
						data.rows[i].name += "(继承)";
					}
				}
				$("#table_role").datagrid({
					"data" : data,
					"_data" : data,
					"pagination":true,
					"scrollbarSize":0,
					"fitColumns" : true,
			        "pageSize":9999,
			        "checkOnSelect" : false,
			        "columns" :[columns],
			        "pageList":[5,10,15,20,9999],
			        "rowStyler": function(index,row){
			        	if(row.name.indexOf("(继承)")>0){
			        		return {style:"background-color:#FFFFD5"};
			        	}
			        },
			        "loadFilter":function(d){
			        	sRows = [];
			        	rmvChkRows = [];
			        	for(var i=0;i<d.rows.length;i++){
							if(checkedItems[d.rows[i].id]){
								sRows.push(i);
							}
							if(d.rows[i].name.indexOf("(继承)")>0){
								rmvChkRows.push(i);
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
					},
					"onRenderFinish":function(){
						 if(!inited){
						 	$("#table_role").ITCUI_Pagination("create","#nouse");
						 	inited = true;
						 	search();
						 }
						 checkFinish = false;
						 for(var i=0;i<sRows.length;i++){
							 $("#table_role").datagrid("checkRow",sRows[i]);
						 }
						 var o = $("#table_role").prev(".datagrid-view2").find(".datagrid-body");
						 for(var i=0;i<rmvChkRows.length;i++){
							 o.find("#datagrid-row-r2-2-" + rmvChkRows[i]).find("input").attr("disabled",true);
						 }
						 checkFinish = true;
						 $("#table_role").iFixCheckbox();
					}
				});
			}
		});
		
	});
	var searchStat = false;
	function search(){
		if(searchStat){
			$("#table_role").ITCUI_GridSearch("end");
		}
		else{
			$("#table_role").ITCUI_GridSearch("init");			
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
		目前无可添加的角色
	</div>
	<div id="table_wrap">
		<table id="table_role" class="easyui-datagrid" style="height:284px">
			<thead>
		        <tr>
		            
		        </tr>
		    </thead>
		</table>
	</div>
</body>
</html>