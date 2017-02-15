<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<jsp:include page="/page/framework/auth_include.jsp" flush="false" />
<title>角色管理</title>
<script>
var filter = null;
var firstInit = false;
var tbRole = null;

$(document).ready(function(){
	reloadDatagrid();
	$("#search").ITCUI_Input();
	$("#search").keypress(function(e) {
	    if(e.which == 13) {
	        search();
	    }
	});
	$("#search").next(".itcui_btn_mag").click(function(){
		search();
	});
	$(window).resize(function(){
		if(tbRole){
			tbRole.datagrid("resize");
		}
	});
});

function search(){
	firstInit = true;
	filter = encodeURIComponent($("#search").val());
	reloadDatagrid();
}

function delRole(){
	var selRows = tbRole.datagrid("getChecked");
	if(selRows&&selRows.length>0){
		Notice.confirm("是否删除选择的角色？该操作无法撤销。",function(){
			actDelRole();
		},null);
	}
}

function actDelRole(){
	var selRows = tbRole.datagrid("getChecked");
	var ids = [];
	for(var i=0;i<selRows.length;i++){
		var row = selRows[i];
		ids.push(row.id);
	}
	$.post("${basePath}role?method=delroles",{"rids":ids.join(",")},function(result){
		window.location.href = "${basePath}pages/int/sys/sec/role_management.jsp?rand=" + Math.random();
	});
}
function reloadDatagrid(){
	firstInit = false;
    tbRole = $("#table_role").datagrid({
        fitColumns:true,
        url:"${basePath}role?method=getroles",
        pagination :true,
        singleSelect:true,
        queryParams:{
        	"filter":filter
        },
        scrollbarSize:0,
        pageSize:rowLen,
        pageList :[10,15,20,30,100],
        onRenderFinish : function(){
            if(firstInit==false){
                firstInit = true;
				$("#table_role").ITCUI_Pagination("create", "#pager",styleOpt);
			}
		},
		onDblClickRow : function(rowIndex, rowData) {
			window.location.href = "${basePath}role?method=getrole&mode=edit&rid=" + rowData.id; 
		}
	});

}

function refresh(){
	filter = "";
	$("#search").val("");
	firstInit = true;
	reloadDatagrid();
}

function newRole(){
	window.location.href = "${basePath}role?method=getrole&mode=create";
}
</script>
</head>
<body>
	<div class="toolbar toolbar-with-pager bbox">
		<div class="btn-toolbar" role="toolbar">
			<div privilege="F-ROLE-ADD" class="btn-group btn-group-sm">
				<button type="button" class="btn btn-success" onclick="newRole()">新建</button>
			</div>
			<div class="btn-group btn-group-sm">
				<button type="button" class="btn btn-default" onclick="refresh()">刷新</button>
			</div>
			<div class="input-group input-group-sm" style="width:150px;float:left;margin-left:7px;margin-top:1px">
		        <input type="text" id="search" icon="itcui_btn_mag" style="width:150px;" placeholder="请输入角色名或ID"/>     
		    </div>		    
			<div id="pager" bottompager="#bottomPager"></div>
		</div>
	</div>
	<div style="clear:both"></div>
	<table class="eu-datagrid" id="table_role">
		<thead>
			<tr>
				<th data-options="field:'id',width:150,fixed:true">角色编码</th>
				<th data-options="field:'name',width:140,fixed:true">角色名称</th>
				<th data-options="field:'updatetime',width:140,fixed:true">更新时间</th>
				<th data-options="field:'updateby',width:100">更新者</th>
			</tr>
		</thead>
		<tbody></tbody>
	</table>
	<div id="bottomPager" style="width:100%"></div>
</body>
</html>