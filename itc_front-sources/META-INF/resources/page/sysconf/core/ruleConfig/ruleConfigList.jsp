<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" style="width:100%;height:99%">
<head>
<link rel="shortcut icon" href="${basePath}favicon.ico" type="image/x-icon" /> 
<title>权限例外列表</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<!-- 由于在本地web工程中，这个路径不存在，会编译异常 -->
<jsp:include page="/page/mvc/mvc_include.jsp" flush="false" />

<script>_useLoadingMask = true;</script>
<script>
	var isSearchLineShow=false;
	var isSearchMode = false;
	$(document).ready(function() {
		ruleConfigBtn.initList();
		initSecRuleList();
		
		//页面初始化的时候将保存和返回按钮隐藏掉
		$("#btn_save").hide();
		$("#btn_back").hide();
		
		FW.fixToolbar("#toolbar1");
	});
	
	//初始化按钮
	var ruleConfigBtn={
		initList:function(){
			ruleConfigBtn.news();
			ruleConfigBtn.search();
			ruleConfigBtn.save();
			ruleConfigBtn.back();
		},
		//新建按钮
		news:function(){
			addNewRow();
		},
		//查询按钮
		search:function(){
			clickSearchBtn();
		},
		//保存按钮
		save:function(){
			$("#btn_save").click(function(){
				endEditAll();
				
				var listData = $("#sysrule_grid").datagrid("getRows");
				var listDataTemp = listData.slice(0);
				//检查相同的规则
				for(var i = 0;i<listData.length;i++){
					for(var j=i+1;j<listDataTemp.length;j++){
						if (listData[i].ruleId === listDataTemp[j].ruleId){  
						  	FW.error( "列表中存在两个相同的规则id,相同的规则id为："+listData[i].ruleId);
						  	$("#sysrule_grid").datagrid("beginEdit",i);
							return;
						}  
					}
					
					
				}
				
				var flag = false;
				//将没有数据的规则删除
				for(var i = 0;i<listData.length;i++){
					if("" == listData[i].ruleId||"" == listData[i].roles){
						flag = true;
						$("#sysrule_grid").datagrid("deleteRow",i);
						listData = $("#sysrule_grid").datagrid("getRows");
					}
					
					if(flag){
						FW.success("排除掉列表为空的数据进行保存... ");
					}
				}
				
				$.ajax({
					type : "POST",
					async : false,
					url : basePath + "sysconf/sysConfig/saveSecExclusiveRule.do",
					data : {
						"listData" : FW.stringify(listData)
					},
					dataType : "json",
					success : function(data) {
						refCurPage();
						
						//按钮重现
						$("#btn_save").hide();
						$("#btn_back").hide();
						$("#btn_search").show();
						FW.fixToolbar("#toolbar1");
						
						FW.success("保存成功 ");
					}
				});
			});
		},
		//返回按钮
		back:function(){
			$("#btn_back").click(function(){
				window.location.href = basePath + "sysconf/sysConfig/ruleConfigPage.do";
			});
		}
	};

	
	
	//初始化枚举类型列表
	function initSecRuleList(){
		var columns = [[
	        {field:'ruleId',title:'规则id',width:140,fixed:true,
	         edit:true,
	         editor:{type:'text'}
			},
			{field:'roles',title:'角色',width:90,edit:true,editor:{type:'text'}}
		]];
	
		$("#sysrule_grid").iDatagrid("init",{
			singleSelect:true,
			pageSize:pageSize,
			columns:columns,
			idField:"ruleId",
			url: basePath+"sysconf/sysConfig/querySecExclusiveRuleList.do",
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
					$("#sysrule_grid").datagrid("resize"); 
				},200);
			},
			onDblClickRow : function(rowIndex, rowData) {
				$("#sysrule_grid").datagrid("beginEdit",rowIndex);
				
				$("#btn_save").show();
				$("#btn_back").show();
				
				$("#btn_search").hide();
			   	FW.fixToolbar("#toolbar1");
			}
		});
	}

	//列表刷新
	function refCurPage(){
	 	$("#sysrule_grid").datagrid("reload");
	}
	
	//新建页面
	function addNewRow(){
		$("#btn_new").click(function(){
		   	
		   	//将遮住的div隐藏掉
		   	$("#noSearchResult").hide();
		   	$("#toolbar_wrap,#grid_wrap").show();
		    
		    var row = {};
			row["ruleId"]='';
			row["roles"] = '';
			$("#sysrule_grid").datagrid("appendRow",row );
			var listSize =$("#sysrule_grid").datagrid("getRows").length;
			$("#sysrule_grid").datagrid("beginEdit",listSize-1);
		   	
		   	$("#btn_save").show();
			$("#btn_back").show();
			
			$("#btn_search").hide();
		   	FW.fixToolbar("#toolbar1");
		});
	}
	
	//点击查询按钮
	function clickSearchBtn(){
		$("#btn_search").click(function(){
		    if(isSearchLineShow){
			    isSearchLineShow=false;
		        $("#sysrule_grid").iDatagrid("endSearch");
		    }
		    else{
		    	isSearchLineShow=true;
		       	$("#sysrule_grid").iDatagrid("beginSearch",{"remoteSearch":true,"onParseArgs":function(arg){
		       		isSearchMode = true;
					return {"search":JSON.stringify(arg)};
				}});
		    }
		});
	}
	
	//开始编辑所有行
	function startEditAll(){
		var rows = $("#sysrule_grid").datagrid("getRows");
		for(var i=0;i<rows.length;i++){
			$("#sysrule_grid").datagrid("beginEdit",i);
		}
	}
	
	//结束编辑所有行
	function endEditAll(){
		var rows = $("#sysrule_grid").datagrid("getRows");
		for(var i=0;i<rows.length;i++){
			$("#sysrule_grid").datagrid("endEdit",i);
		}
	}
</script>
</head>
<body style="height: 100%;" class="bbox">
	<div class="bbox toolbar-with-pager" id="toolbar_wrap">
		<!-- 这里可以在分页器的同排渲染一个按钮工具栏出来 在下面的toolbar1中 -->
	    <div id="toolbar1" class="btn-toolbar ">
	        <div class="btn-group btn-group-sm">
	        	<button type="button" id="btn_new" class="btn btn-success">添加规则</button>
	        </div>
	        
	        <div class="btn-group btn-group-sm">
	        	<button type="button" id="btn_back" class="btn btn-default">返回</button>
	        	<button type="button" id="btn_save" class="btn btn-default">保存</button>
	        </div>
	        
	        <div class="btn-group btn-group-sm">
	        	<button type="button" id="btn_search" data-toggle="button" class="btn btn-default">查询</button>
	        </div>
	    </div>
	    <!-- 上分页器部分 这里可以通过属性bottompager指定下分页器的DIV-->
	    <div id="pagination" class="toolbar-pager" bottompager="#bottomPager"></div>
	</div>
	
	<!--这里要清掉分页器的右浮动效果-->
	<div style="clear:both"></div>
	<div id="grid_wrap" style="width:100%">
	    <table id="sysrule_grid" pager="#pagination" class="eu-datagrid"></table>
	    <div id="noSearchResult" style="width: 100%;display:none">
			<span>没有找到符合条件的结果</span>
		</div>
	</div>
	<!--大表需要加下分页器-->
	<div id="bottomPager" style="width:100%">
	</div>
</body>
</html>