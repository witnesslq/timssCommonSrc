<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" style="width:100%;height:99%">
<head>
<title>系统日志</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<!-- 由于在本地web工程中，这个路径不存在，会编译异常 -->
<jsp:include page="/page/mvc/mvc_include.jsp" flush="false" />

<script type="text/javascript" src="${basePath}js/sysconf/logConfig/logConfigList.js?ver=${iVersion}"></script>
</head>
<body style="height: 100%;" class="bbox">
	<div class="bbox toolbar-with-pager" id="toolbar_wrap">
	    <div id="toolbar1" class="btn-toolbar ">
	    	<div style="width:400px;float:left;">
		    	 <form id="searchForm" class="autoform"></form>
			</div>
	    	
	        <div class="btn-group btn-group-sm" style="float:left;padding-left:30px;">
	            <button type="button" class="btn btn-default" id="btn_query" onclick="queryLogListBySearch()">查询</button>
	        </div>
	    </div>
	    <div id="pagination_1" class="toolbar-pager" bottompager="#bottomPager"></div>
	</div>
	
	<div style="clear:both"></div>
	<div id="grid_wrap" style="width:100%">
	    <table id="table_log" pager="#pagination_1" class="eu-datagrid">
		</table>
		<div id="noSearchResult"  style="display:none;margin-top:20px;font-size:14px;vertical-align:middle;text-align:center" >
			没有找到符合条件的结果
		</div>
	</div>

	<div id="bottomPager" style="width:100%"></div>
	
	
</body>
</html>