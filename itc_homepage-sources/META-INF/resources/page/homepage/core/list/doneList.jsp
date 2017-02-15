<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
<title>工作任务-已办</title>
<script>_useLoadingMask = true</script>
<jsp:include page="/page/mvc/mvc_include.jsp" flush="false" />
<script type="text/javascript" src="${basePath}js/homepage/hoplist.js"></script>
<link rel="stylesheet" type="text/css" href="${basePath}css/homepage/hoplist.css" media="all" />
<style>
</style>
<script>
	var columns = [[
		{field:'typename',title:'类别',width:95,sortable:true,fixed:true,editor:{type:'text'}},
		{field:'flowno',title:'编号',width:140,sortable:true,fixed:true,editor:{type:'text'}},
		{field:'name',title:'名称',width:140,sortable:true,fixed:false,editor:{type:'text'},
		loadFilter: function(data){
 	                    var rows = data.rows;
 	                    for(var i=0; i<rows.length; i++){
 	                        rows[i].content = rows[i].content.replace(/<.*?>/g, "");
                        }
                        return data;
       }},
		{field:'createusername',title:'申请人',width:70,sortable:true,fixed:true,editor:{type:'text'}},
		{field:'deptname',title:'申请部门',width:110,sortable:true,fixed:true,editor:{type:'text'}},
		{field:'createdate',title:'申请日期',width:120,sortable:true,fixed:true,editor:{type:'datebox',options:{dataType:"long2date"}},
			formatter: function(value,row,index){
				return FW.long2date(value);
			}
		},
		{field:'curusername',title:'办理人',width:70,sortable:true,fixed:true,editor:{type:'text'},
			formatter: function(value,row,index){
			    if(value){
					return "<span title='"+value+"'>"+value+"</span>";
				}
				return "";
			}
		},
		{field:'statusname',title:'状态',width:125,sortable:true,fixed:true,editor:{type:'text'}},
	]];
	var url = basePath + "homepage/Info/DoneListInfo.do";
	function newDemoInfo(){
	}
</script>
</head>
<body class="bbox list-page">
	<div id="grid_wrap" style="width:100%">
	<div class="bbox toolbar-with-pager" id="toolbar_wrap">
	    <!-- 这里可以在分页器的同排渲染一个按钮工具栏出来 在下面的toolbar1中 -->
	    <div id="toolbar1" class="btn-toolbar ">
	        <div class="btn-group btn-group-sm">
	        	 <button type="button" class="dropdown-toggle btn btn-success" 
						onmouseup="javascript:this.blur();"
						data-toggle="dropdown">
						新建
						<span class="caret" style="margin-left:4px"></span>
					</button>
					<jsp:include page="pop.jsp" flush="true"/>
	        </div>
	        <div class="btn-group btn-group-sm">
	            <button type="button" id="btn_advSearch" data-toggle="button" class="btn btn-default">查询</button>
	        </div>
	    </div>
	    <!-- 上分页器部分 这里可以通过属性bottompager指定下分页器的DIV-->
	    <div id="pagination_1" class="toolbar-pager" bottompager="#bottomPager">        
	    </div>
	</div>
	<!--这里要清掉分页器的右浮动效果-->
	<div style="clear:both"></div>
	    <table id="wait_grid" class="eu-datagrid" pager="#pagination_1">
	    </table>
	<!-- 下页器部分-->
	<div id="bottomPager" style="width:100%;margin-top:6px">
	</div>
	</div>
	<div id="grid_empty" class="row homepage_mi" style="display:none">
	    没有已办任务
	</div>
</body>
</html>
