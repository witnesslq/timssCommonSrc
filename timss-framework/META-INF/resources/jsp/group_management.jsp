<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<jsp:include page="/page/framework/auth_include.jsp" flush="false" />

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>用户组管理</title>
<script>
var filter = null;
var firstInit = false;
var tbGroup = null;

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
		if(tbGroup){
			tbGroup.datagrid("resize");
		}
	});
});
function delGroup(){
	var selRows = tbGroup.datagrid("getChecked");
	if(selRows&&selRows.length>0){
		Notice.confirm("是否删除选择的用户组？该操作无法撤销。",function(){
			actDelGroup();
		},null);
	}
}

function refresh(){
	filter = "";
	$("#search").val("");
	firstInit = true;
	reloadDatagrid();
}

function actDelGroup(){
	var selRows = tbGroup.datagrid("getChecked");
	var ids = [];
	for(var i=0;i<selRows.length;i++){
		var row = selRows[i];
		ids.push(row.id);
	}
	$.post("${basePath}group?method=delgroups",{"gids":ids.join(",")},function(result){
		window.location.href = "${basePath}pages/int/sys/sec/group_management.jsp?rand=" + Math.random();
	});
}
function search(){
	filter = encodeURIComponent($("#search").val());
	firstInit = true;
	reloadDatagrid();
}
firstInit = false;
function reloadDatagrid(){	
    tbGroup = $("#table_group").datagrid({
        fitColumns:true,
        url : "${basePath}group?method=getgroups",
        pagination :true,
        singleSelect:true,
        scrollbarSize:0,
        queryParams:{
        	"filter":filter
        },
        pageSize:rowLen,
        pageList :[10,15,20,30,100],
        onLoadSuccess : function(){
            if(firstInit==false){
            	if($("#table_group").datagrid("getRows").length==0){
            		$("#table_wrap,#inputWrap").hide();
            		$("#nodata").show();
            		return;
            	}
                firstInit = true;
                $("#table_group").ITCUI_Pagination("create", "#pager",styleOpt);
			}
		},
		onDblClickRow : function(rowIndex, rowData) {
			window.location.href = "${basePath}group?method=getgroup&mode=edit&gid=" + rowData.id; 
		}
	});
}

function newGroup(){
	window.location.href = "${basePath}group?method=getgroup&mode=create";
}
</script>

</head>
<body>
	<div class="toolbar-with-pager bbox">
		<div class="btn-toolbar" role="toolbar">
			<div privilege="F-GRP-ADD" class="btn-group btn-group-sm">
				<button type="button" class="btn btn-success" onclick="newGroup()">新建</button>
			</div>
			<div class="btn-group btn-group-sm">
 				<button type="button" class="btn btn-default" onclick="refresh()">刷新</button>
 			</div>
			<div id="inputWrap" class="input-group input-group-sm" style="width:150px;float:left;margin-left:7px;margin-top:1px">
		        <input type="text" id="search" icon="itcui_btn_mag" style="width:150px;" placeholder="请输入组名或ID"/>     
		    </div>		    
			<div id="pager"></div>
		</div>
	</div>
	<div style="clear:both"></div>
	<div id="nodata" class="nodata" style="display:none">
		目前该站点下无用户组
	</div>
	<div id="table_wrap" style="width:100%">
		<table class="eu-datagrid" id="table_group">
			<thead>
				<tr>
					<th data-options="field:'id',width:150,fixed:true">用户组编码</th>
					<th data-options="field:'name',width:150,fixed:true">用户组名称</th>
					<th data-options="field:'updatetime',width:140,fixed:true">更新时间</th>
					<th data-options="field:'updateby',width:100">更新者</th>
				</tr>
			</thead>
			<tbody></tbody>
		</table>
	</div>
	<div id="bottomPager" style="width:100%"></div>
</body>
</html>