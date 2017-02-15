<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>定时任务调度页面</title>

<script>
	_useLoadingMask = true;
</script>
<jsp:include page="/page/mvc/mvc_include.jsp" flush="false" />
<script type="text/javascript" src="${basePath}js/sysconf/list.js"></script>
<script type="text/javascript" src="${basePath}js/sysconf/timelist.js"></script>
<link rel="stylesheet" type="text/css"
	href="${basePath}css/homepage/hoplist.css" media="all" />
<script>
	var columns = [ [ {
		field : 'typename',
		title : '任务名字',
		width : 80,
		sortable : true,
		fixed : true,
		editor : {
			type : 'text'
		}
	}, {
		field : 'flowno',
		title : '开始时间',
		width : 140,
		sortable : true,
		fixed : true,
		editor : {
			type : 'text'
		}
	}, {
		field : 'name',
		title : '结束时间',
		width : 140,
		sortable : true,
		fixed : false,
		editor : {
			type : 'text'
		}
	}, {
		field : 'createusername',
		title : '启动间隔(秒)',
		width : 70,
		sortable : true,
		fixed : true,
		editor : {
			type : 'text'
		}
	}, {
		field : 'deptname',
		title : '运行间隔(秒)',
		width : 90,
		sortable : true,
		fixed : true,
		editor : {
			type : 'text'
		}
	}, {
		field : 'statusdate',
		title : '办理日期',
		width : 95,
		sortable : true,
		fixed : true,
		editor : {
			type : 'datebox',
			options : {
				dataType : "long2date"
			}
		},
		formatter : function(value, row, index) {
			return FW.long2date(value);
		}
	}, {
		field : 'statusname',
		title : '运行状态',
		width : 115,
		sortable : true,
		fixed : true,
		editor : {
			type : 'text'
		}
	}, ] ];
	var url = basePath + "sysconf/systemConfig/timelistInfo.do";
</script>
</head>
<body>
	<div id="grid_wrap" style="width:100%">
		<div class="bbox toolbar-with-pager" id="toolbar_wrap">
			<div id="toolbar1" class="btn-toolbar ">
				<div class="btn-group btn-group-sm">
					<button type="button" class="btn btn-success" id="btn_new">新建</button>
				</div>
				<div class="btn-group btn-group-sm">
					<button type="button" id="btn_advSearch" data-toggle="button"
						class="btn btn-default">查询</button>
				</div>
			</div>
			<div id="pagination_1" class="toolbar-pager"
				bottompager="#bottomPager"></div>
		</div>
		<div style="clear:both"></div>
		<table id="wait_grid" class="eu-datagrid" pager="#pagination_1">
		</table>
		<div id="bottomPager" style="width:100%;margin-top:6px"></div>
	</div>
	<div id="grid_empty" class="row homepage_mi" style="display:none;">
		没有调度任务
		<div id="toolbar2" class="btn-toolbar ">
			<div class="btn-group btn-group-sm">
				<button type="button" class="btn btn-success" id="btn_new">新建</button>
			</div>
		</div>
	</div>
</body>
</html>
