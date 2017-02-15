<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" style="width:100%;height:99%">
<head>
<link rel="shortcut icon" href="${basePath}favicon.ico" type="image/x-icon" /> 
<title>枚举列表</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<!-- 由于在本地web工程中，这个路径不存在，会编译异常 -->
<jsp:include page="/page/mvc/mvc_include.jsp" flush="false" />

<script>_useLoadingMask = true;</script>
<script>
	var enumType = null;
	var isSearchLineShow=false;
	var isSearchMode = false;
	$(document).ready(function() {
		enumConfigBtn.initList();
		//初始化页面按钮虚拟权限
		enumConfigPriv.init();
		initBEnumCatList();
		FW.fixToolbar("#toolbar1");
	});
	
	//初始化按钮
	var enumConfigBtn={
		initList:function(){
			enumConfigBtn.news();
			enumConfigBtn.search();
			enumConfigBtn.delList();
		},
		//新建按钮
		news:function(){
			createNewPage();
		},
		//删除按钮
		delList:function(){
			$("#btn_delete").click(function(){
				var row=$("#sysenum_grid").datagrid("getSelected");
				delEnum(row.ecatCode,"list");
			});
		},
		//查询按钮
		search:function(){
			clickSearchBtn();
		}
	};

	//按钮权限初始化
	var enumConfigPriv={
		init:function(){
			enumConfigPriv.set();
			enumConfigPriv.apply();
		},
		set:function(){//定义权限
			//新建
			Priv.map("privMapping.enum_new","enum_new");
		},
		apply:function(){//应用权限
			//应用
			Priv.apply();
		}
	};
	
	//初始化枚举类型列表
	function initBEnumCatList(){
		$("#sysenum_grid").iDatagrid("init",{
			singleSelect:true,
			pageSize:pageSize,
			url: basePath+"sysconf/sysConfig/queryBEnumCatList.do",
			onLoadSuccess:function(data){
				if(isSearchMode){
	            	 if(data && data.total==0){
	            		 $("#noSearchResult").show();
	            	 }
	            	 else{
	            		 $("#noSearchResult").hide();
	            	 }
	            }else{
		            if(data && data.total==0){
		                $("#grid_wrap,#toolbar_wrap").hide();
		                $("#grid_error").show();
		            }else{
		            	$("#toolbar_wrap,#grid_wrap").show();
		                $("#grid_error").hide();
		            }
		            $("#noSearchResult").hide();
				}
				isSearchMode = false;
				setTimeout(function(){ 
					$("#sysenum_grid").datagrid("resize"); 
				},200);
			},
			onDblClickRow : function(rowIndex, rowData) {
				var url = basePath+ "sysconf/sysConfig/bEnumForm.do?ecatCode="+rowData.ecatCode+"&enumType="+rowData.enumType;
		    	var prefix = new Date().getTime() + "" + Math.floor(Math.abs(Math.random()*100));
			    FW.addTabWithTree({
			        id : "editEnumForm" + prefix,
			        url : url,
			        name : "枚举配置信息",
			        tabOpt : {
			            closeable : true,
			            afterClose : "FW.deleteTab('$arg');FW.activeTabById('L-SYS');FW.getFrame(FW.getCurrentTabId()).refCurPage();"
			        }
			    });
			},
			onClickRow : function (rowIndex, rowData){
				if("NaN" == rowData.enumType){
					$("#btn_delete").hide();
				}else{
					$("#btn_delete").show();
				}
			}
		});
	}

	//列表刷新
	function refCurPage(){
	 	$("#sysenum_grid").datagrid("reload");
	}
	
	//新建页面
	function createNewPage(){
		$("#btn_new,.btn_new").click(function(){
		   	var url = basePath+ "sysconf/sysConfig/bEnumForm.do?ecatCode=&enumType=";
		    var prefix = new Date().getTime() + "" + Math.floor(Math.abs(Math.random()*100));
		    FW.addTabWithTree({
		        id : "editEnumForm" + prefix,
		        url : url,
		        name : "枚举配置信息",
		        tabOpt : {
		            closeable : true,
		            afterClose : "FW.deleteTab('$arg');FW.activeTabById('L-SYS');FW.getFrame(FW.getCurrentTabId()).refCurPage();"
		        }
		    });
		});
	}
	
	//点击查询按钮
	function clickSearchBtn(){
		$("#btn_search").click(function(){
		    if(isSearchLineShow){
			    isSearchLineShow=false;
		        $("#sysenum_grid").iDatagrid("endSearch");
		    }
		    else{
		    	isSearchLineShow=true;
		       	$("#sysenum_grid").iDatagrid("beginSearch",{"remoteSearch":true,"onParseArgs":function(arg){
		       		isSearchMode = true;
					return {"search":JSON.stringify(arg)};
				}});
		    }
		});
	}
</script>
</head>
<body style="height: 100%;" class="bbox">
	<div class="bbox toolbar-with-pager" id="toolbar_wrap">
		<!-- 这里可以在分页器的同排渲染一个按钮工具栏出来 在下面的toolbar1中 -->
	    <div id="toolbar1" class="btn-toolbar ">
	        <div class="btn-group btn-group-sm">
	        	<button type="button" id="btn_new" class="btn btn-success priv" privilege="enum_new">新建</button>
	        </div>
	        <div class="btn-group btn-group-sm">
	        	<button type="button" id="btn_search" data-toggle="button" class="btn btn-default">查询</button>
	        </div>
	        <div class="btn-group btn-group-sm">
	        	<button type="button" id="btn_delete" class="btn btn-default priv" privilege="enum_delete">删除</button>
	        </div>
	    </div>
	    <!-- 上分页器部分 这里可以通过属性bottompager指定下分页器的DIV-->
	    <div id="pagination" class="toolbar-pager" bottompager="#bottomPager"></div>
	</div>
	
	<!--这里要清掉分页器的右浮动效果-->
	<div style="clear:both"></div>
	<div id="grid_wrap" style="width:100%">
	    <table id="sysenum_grid" pager="#pagination" class="eu-datagrid">
	    	<thead>
				<tr>
					<th data-options="field:'moduleCode',width:130,fixed:true">模块编码</th>
					<th data-options="field:'ecatCode',width:70,sortable:true">枚举编码</th>
					<th data-options="field:'cat',width:60,sortable:true">枚举名称</th>
					<th data-options="field:'enumType',width:130,formatter:function(value,row,index){
							if(value == 'NaN'){ 
								return '公共'
							}else{ 
								return value
							}
						}">枚举站点</th>
				</tr>
			</thead>
	    </table>
	    <div id="noSearchResult" style="width: 100%;display:none">
			<span>没有找到符合条件的结果</span>
		</div>
	</div>
	
	<!-- 无数据 -->
	<div id="grid_error" style="display:none;width:100%;height:62%">
		<div style="height:100%;display:table;width:100%">
			<div style="display:table-cell;vertical-align:middle;text-align:center">
			    <div style="font-size:14px">没有查询到相关数据</div>
			    <div class="btn-group btn-group-sm margin-element">
		        	<button type="button" class="btn btn-success btn_new" privilege="enum_new">新建</button>
			    </div>
			</div>
		</div>
	</div>
	<!--大表需要加下分页器-->
	<div id="bottomPager" style="width:100%">
	</div>
</body>
</html>