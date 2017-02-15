<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>定时任务调度页面</title>
<%
	String operType = request.getParameter("opertype");
%>
<script>
	var _opertype = "<%=operType %>";
</script>
<jsp:include page="/page/mvc/mvc_include.jsp" flush="false" />
<script type="text/javascript" src="${basePath}js/sysconf/timedetail.js"></script>
<link rel="stylesheet" type="text/css"
	href="${basePath}css/homepage/hoplist.css" media="all" />
<script>
	
</script>
</head>
<body>
	<div class="bbox toolbar-with-pager" id="toolbar_wrap">
		<!-- 这里可以在分页器的同排渲染一个按钮工具栏出来 在下面的toolbar1中 -->
	    <div id="toolbar" class="btn-toolbar ">
	    	<div class="btn-group btn-group-sm">
	        	<button type="button" id="btn_back" class="btn btn-default" style="display:none">返回</button>
	        </div>
	        <div class="btn-group btn-group-sm">
	        	<button type="button" id="btn_save" class="btn btn-default" style="display:none">保存</button>
	        </div>
	        <div class="btn-group btn-group-sm">
	        	<button type="button" id="btn_edit" class="btn btn-default" style="display:none">编辑</button>
	        </div>
	    </div>
	</div>
	<div class="inner-title" id="pageTitle">
		定时调度任务
	</div>
	<form id="autoform" class="margin-form-title margin-form-foldable autoform"></form>

	<div id="equipment_detail" grouptitle="所属设备">
		<div id="equipmentGrid" class="margin-title-table">
			<table id="equipment_grid" class="eu-datagrid"></table>
		</div>
		<div class="btn-toolbar margin-foldable-button" role="toolbar">
			<div class="btn-group btn-group-xs" id="btnGroup">
			
    		</div>
    	</div>
	</div>
</body>
</html>
