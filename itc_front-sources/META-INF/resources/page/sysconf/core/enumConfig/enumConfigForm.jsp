<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" style="width:100%;height:100%">
<head>
<link rel="shortcut icon" href="${basePath}favicon.ico" type="image/x-icon" />
<title>枚举子类表单</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<!-- 由于在本地web工程中，这个路径不存在，会编译异常 -->
<script>_useLoadingMask = true</script>
<jsp:include page="/page/mvc/mvc_include.jsp" flush="false" />

<script>
	var operFlag = true;
	var tmpid = 0;
	var ecatCode = '${ecatCode}';
	var enumType = '${enumType}';
	/************************判断页面是否修改************************/
	var initFormStatus = null;
	var currFormStatus = null;
	var initListStatus = null;
	var currListStatus = null;
	var formName = "autoform";
	var listName = "enumdetail_grid";
	/************************判断页面是否修改************************/
	
	$(document).ready(function() {
		initForm();
		initBEnumList();
		//初始化页面按钮
		enumConfigBtn.init();
		//初始化页面按钮虚拟权限
		enumConfigPriv.init();
		FW.fixToolbar("#toolbar");
	});
	
	//编辑表单加载数据（通用方法）
	function initForm(){
		var form = [
		    		{title : "枚举类型编码", id : "ecatCode", rules :  {required:true,remote:{
		    			url: basePath + "sysconf/sysConfig/verifyEnumExist.do",
		    			type: "POST",
		    			data: {
		    				ecatCode: function() {
		    			        return $("#f_ecatCode").val();
		    			    }
		    			}}},messages:{remote:"枚举类型编码已存在"}},
		    		{title : "枚举类型名称", id : "cat", rules : {required:true}}
		    ];
		
		$("#autoform").iForm("init",{"fields":form,"options":{validate:true}});
		//加载用户表单数据
		$.ajax({
			type : "POST",
			async:false,
			url: basePath+"sysconf/sysConfig/queryBEnumCatForm.do",
			data: {"ecatCode":ecatCode},
			dataType : "json",
			success : function(data) {
				if("" != ecatCode){
					var	loaddata = {
							"ecatCode" : data.ecatCode,
							"cat" : data.cat
						};
					$("#autoform").iForm("setVal",loaddata);
					$("#autoform").iForm("endEdit",["ecatCode"]);
					initFormStatus = $("#autoform").iForm("getVal");
				}
				addFormCloseEvent();
			}
		});
	}
	
	//查看枚举子类列表
	function initBEnumList(){
		//查看出库记录
		var columns = [[
			{field:'tmpid',hidden:true,fixed:true,formatter:function(value,row){
				row.tmpid=tmpid;
				return tmpid++;
			}},
			{field:'code',width:100,edit:true,editor:{type:'text'},title:'枚举编码'},
			{field:'label',width:100,edit:true,editor:{type:'text'},title:'枚举值'},
			{field:'remarks',width:150,edit:true,editor:{type:'text'},title:'备注'},
			{field:'sortingOrder',title:'排序',width:90,fixed:true,edit:true,
				editor:{
					type:'text',
					options:{
						align:"right",
						dataType:"number"
					}
				}
			},
			{title:'',align:'center',field:'del',width:40,fixed:true,formatter:function(value,row){
				if("NaN" != enumType){
					return "<img class='btn-delete btn-garbage' onclick='delRow(\""+row.tmpid+"\");' src='"+basePath+"img/inventory/btn_garbage.gif'/>";
				}
			}}
		]];
		
		$("#enumdetail_list").iFold("init");
		$("#enumdetail_grid").datagrid({
			singleSelect:true,
			fitColumns : true,
			columns:columns,
			idField:'tmpid',
			url : basePath+"sysconf/sysConfig/queryBEnumList.do",
			queryParams: {
					"ecatCode": ecatCode,
					"enumType": enumType
					},
			onLoadSuccess: function(data){
	        	 if(data && data.total>0){
	        		 var listData =$("#enumdetail_grid").datagrid("getRows");
	        		 initListStatus = FW.stringify(listData);
	        		 gridStrutsChange("beginEdit");
	        		 $("#btn_add").text("继续添加枚举子项");
	        	 }
	        	 setTimeout(function(){
	             	$("#enumdetail_grid").datagrid("resize");
	             },200);
	        	 
	        },
	        onAfterEdit:function(rowIndex, rowData){
	        	var listData =$("#enumdetail_grid").datagrid("getRows");
	        	for(var i in listData){
	        		if(listData[i].code == rowData.code && i!=rowIndex){
	        			FW.error("枚举编码已经存在");
	        			gridStrutsChange("beginEdit");
	        			return;
	        		}
	        	}
	        }
		});
	}
	
	//按钮初始化
	var enumConfigBtn={
		init:function(){
			enumConfigBtn.close();
			enumConfigBtn.save();
			enumConfigBtn.del();
			enumConfigBtn.add();
		},
		//关闭按钮	
		close:function(){
			closePage();
		},
		//暂存按钮
		save:function(){
			saveEnum();
		},
		//删除按钮
		del:function(){
			$("#btn_delete").click(function(){
				operFlag = false;
				delEnum(ecatCode,"form");
			});
		},
		//添加枚举按钮
		add:function(){
			addNewEnums();
		}
	};
	
	//按钮权限初始化
	var enumConfigPriv={
		init:function(){
			enumConfigPriv.set();
			enumConfigPriv.apply();
		},
		set:function(){//定义权限
			//保存
			Priv.map("privMapping.enum_save","enum_save");
			//删除
			Priv.map("privMapping.enum_delete","enum_delete");
			
			if("NaN" == enumType){
				$("#btn_delete").hide();
			}
		},
		apply:function(){//应用权限
			//应用
			Priv.apply();
		}
	};
	
	//关闭页面
	function closePage(){
		$("#btn_close").click(function(){
			FW.deleteTabById(FW.getCurrentTabId());
		});
	}
	
	//保存页面信息
	function saveEnum(){
		$("#btn_save").click(function(){
			if(!$("#autoform").valid()){
				return ;
			}
			gridStrutsChange("endEdit");
			var formData =$("#autoform").ITC_Form("getdata");
			var listData =$("#enumdetail_grid").datagrid("getRows");
			
			if(listData.length == 0){
				FW.error("请添加枚举信息 ");
				return;
			}
			
			//加载用户表单数据
			$.ajax({
				type : "POST",
				async: false,
				url: basePath+"sysconf/sysConfig/saveEnumVal.do",
				data: {
					"formData":FW.stringify(formData),
					"listData":FW.stringify(listData),
					"enumType":enumType
					},
				dataType : "json",
				success : function(data) {
					operFlag = false;
					if( data.result == "success" ){
						FW.success("保存成功 ");
					}else{
						FW.error("保存失败 ");
					}
					FW.deleteTabById(FW.getCurrentTabId());
				}
			});
		});
	}
	
	//列表页面的删除按钮
	function delEnum(obj,type){
		$.ajax({
			type : "POST",
			url: basePath+"sysconf/sysConfig/deleteEnum.do",
			data: {"ecatCode":obj},
			dataType : "json",
			success : function(data) {
				if(data.result=='success'){
					FW.success("枚举变量删除成功");
					$("#sysenum_grid").datagrid("reload");
					if("form" == type){
						FW.deleteTabById(FW.getCurrentTabId());
					}
				}else{
					FW.error("枚举变量删除失败");
				}
			}
		});
	}
	
	//添加新的枚举子类
	function addNewEnums(){
		$("#btn_add").click(function(){
			var row = {};
			row["enumCode"] = "";
			row["enumVal"] = "";
			row["enumRemarks"] = "";
			row["sortNum"] = "";
			$("#enumdetail_grid").datagrid("appendRow",row );
			gridStrutsChange("beginEdit");
			$("#btn_add").text("继续添加枚举子项");
		});
	}
	
	//增加关闭页面事件
	function addFormCloseEvent(){
		var evtMap = _parent()._ITC.navTab.getEventMap();
	    var tabId = FW.getCurrentTabId();
	    evtMap[tabId].beforeClose = function(){
	    	
			if(null != formName){
				currFormStatus = $("#"+formName).iForm("getVal");
			}
			
			if(null != listName){
				gridStrutsChange("endEdit");
				currListStatus = FW.stringify($("#"+listName).datagrid("getRows"));
				gridStrutsChange("beginEdit");
			}
			if(operFlag && null != ecatCode){
				if((JSON.stringify(initFormStatus)!=JSON.stringify(currFormStatus)) || 
						(initListStatus!=currListStatus)){
					FW.confirm("关闭？|确定关闭当前页面？关闭后未保存信息将丢失。",function(){
			            delete(evtMap[tabId].beforeClose);
			            FW.deleteTabById(tabId);
			        });
				}else{
					return true;
				}
			}else{
				delete(evtMap[tabId].beforeClose);
	            FW.deleteTabById(tabId);
			}
	    };
	}
	
	//改变列表状态
	function gridStrutsChange(type){
		var rows = $("#enumdetail_grid").datagrid("getRows");
		for(var i=0;i<rows.length;i++){
			$("#enumdetail_grid").datagrid(type,i);
		}
	}
	
	//删除记录
	function delRow(itemid){
		Notice.confirm("删除？|确定删除所选项吗？该操作无法撤消。",function(){
			$('#enumdetail_grid').datagrid('deleteRow',$('#enumdetail_grid').datagrid('getRowIndex',itemid));
			var listData =$("#enumdetail_grid").datagrid("getRows");
			if(listData.length == 0){
				$("#btn_add").text("添加枚举子项");
			}else{
				$("#btn_add").text("继续添加枚举子项");
			}
		},null,"info");
	}
</script>

<style type="text/css">.btn-garbage{ cursor:pointer;}</style>
</head>
<body>
	<div class="bbox toolbar-with-pager" id="toolbar_wrap">
		<!-- 这里可以在分页器的同排渲染一个按钮工具栏出来 在下面的toolbar1中 -->
	    <div id="toolbar" class="btn-toolbar ">
	    	<div class="btn-group btn-group-sm">
	        	<button type="button" id="btn_close" class="btn btn-default">关闭</button>
	        	<button type="button" id="btn_save" class="btn btn-default priv" privilege="enum_save">保存</button>
	        	<button type="button" id="btn_delete" class="btn btn-default priv" privilege="enum_delete">删除</button>
	        </div>
	    </div>
	</div>
	<div class="inner-title" id="pageTitle">枚举信息 </div>
	
	<form id="autoform" class="margin-form-title margin-form-foldable autoform"></form>
		
	<div id="enumdetail_list" grouptitle="枚举子类">
		<div id="enumdetailGrid" class="margin-title-table">
			<table id="enumdetail_grid" class="eu-datagrid"></table>
		</div>
		<div class="btn-toolbar margin-foldable-button" role="toolbar">
			<div class="btn-group btn-group-xs">
		        <button type="button" class="btn btn-success" id="btn_add">添加子类</button>
		    </div>
		</div>
	</div>
</body>
</html>