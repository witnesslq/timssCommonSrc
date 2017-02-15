<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" style="width:100%;height:99%">
<head>
<link rel="shortcut icon" href="${basePath}favicon.ico" type="image/x-icon" /> 
<title>站点配置列表</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<!-- 由于在本地web工程中，这个路径不存在，会编译异常 -->
<jsp:include page="/page/mvc/mvc_include.jsp" flush="false" />
<script>_useLoadingMask = true;</script>
<script>
var isSuperAdmin = '${isSuperAdmin}';
$(document).ready(function() {
	FW.fixRoundButtons("#toolbar1");
	//此处一定要给dataGrid赋值。dataGrid为全局变量，不设置会导致不能resize
	dataGrid = $("#siteConf_grid").iDatagrid("init",{
	    singleSelect:true,
        pageSize:pageSize,//pageSize为全局变量，自动获取的
        url: basePath + "sysconf/siteConfig/getSiteList.do",	//basePath为全局变量，自动获取的
        onLoadSuccess: function(data){
            //远程无数据需要隐藏整个表格和对应的工具条，然后在无数据信息中指引用户新建
            if(data && data.total==0){
            	$('#noResult').show();
            }else{
            	$('#noResult').hide();
            	$("#grid1_wrap").show();
                $("#grid1_empty").hide();
            }
        },
        onDblClickRow:function(rowIndex, rowData){
	        	var siteId=rowData["id"];
	        	//var conf=rowData["conf"];
	        	var src=basePath+"sysconf/siteConfig/getSiteById.do?siteId="+siteId;
	        	var submiturl = "sysconf/siteConfig/updateSite.do";
	        	siteConfDiag(src,'编辑站点',submiturl);
        },
        columns : [ [
         			{field:'ck',checkbox:true},
         			{
         				field : 'id',
         				title : '站点id',
         				width : 250,
         				align : 'left',
         				fixed : true,
         				sortable:true
         			},{
         				field : 'name',
         				title : '站点名称',
         				width : 400,
         				align : 'left',
         				fixed : true,
         				sortable:true
         			}, {
         				field : 'active',
         				title : '状态',
         				align : 'left',
         				width : 1,
         				sortable:true
         			},{
         				field : 'updatedBy',
         				title : '更新人',
         				width : 80,
         				align : 'left',
         				fixed : true,
         				sortable:true
         			}, {
         				field : 'updateTime',
         				title : '更新时间',
         				width : 100,
         				align : 'left',
         				fixed : true,
         				sortable:true,
         				formatter: function(value,row,index){
         		        	return new Date(value).format("yyyy-MM-dd");
         		        }
         			}
         		] 
        ]

    });
	//表头搜索相关的
    $("#btn_search").click(function(){
	    if($(this).hasClass("active")){
	        $("#siteConf_grid").iDatagrid("endSearch");
	    }
	    else{
	        $("#siteConf_grid").iDatagrid("beginSearch",{"noSearchColumns":{5:true,6:true},"remoteSearch":true,"onParseArgs":function(args){
			    return {"search":FW.stringify(args)};
			}});
	    }
	});
	//新增
    $("#btn_new").click(function(){
    	var src=basePath + "page/sysconf/core/siteConfig/siteConfigForm.jsp";
    	var submiturl = basePath+"sysconf/siteConfig/addSite.do";
    	siteConfDiag(src,"新建站点",submiturl);
	});
	//禁用
	$("#btn_disable").click(function(){
		var selections = $("#siteConf_grid").datagrid("getSelections");
		var siteId= '';
		for(var i = 0 ;i<selections.length;i++){
			siteId = selections[i].id;
		}
		if(0==selections.length){
			return ;
		}
		var data = {"siteId":siteId,"active":"N"};
		$.ajax({
			type:"post",
			url:basePath+"sysconf/siteConfig/updateSiteSatus.do",
			data:data,
			complete:function(res){
				var response=res.responseJSON;
				var result= response && response.result;
				if(result.success){
					FW.success("禁用成功");
					$("#siteConf_grid").datagrid("reload");
				}else{
					//执行失败,则通知用户错误原因，同时不关闭对话框
					var msg=result && result.msg;
					if(msg){
						FW.error(msg);
					}else{
						FW.error("系统执行错误");
					}
				}
			},
			dataType:"json",
			async:false
		});
	});
	
	$("#btn_enable").click(function(){
		var selections = $("#siteConf_grid").datagrid("getSelections");
		var siteId= '';
		for(var i = 0 ;i<selections.length;i++){
			siteId = selections[i].id;
		}
		if(0==selections.length){
			return ;
		}
		var data = {"siteId":siteId,"active":"Y"};
		$.ajax({
			type:"post",
			url:basePath+"sysconf/siteConfig/updateSiteSatus.do",
			data:data,
			complete:function(res){
				var response=res.responseJSON;
				var result= response && response.result;
				if(result.success){
					FW.success("启用成功");
					$("#siteConf_grid").datagrid("reload");
				}else{
					//执行失败,则通知用户错误原因，同时不关闭对话框
					var msg=result && result.msg;
					if(msg){
						FW.error(msg);
					}else{
						FW.error("系统执行错误");
					}
				}
			},
			dataType:"json",
			async:false
		});
	});
});

function siteConfDiag(src,title,submiturl){
	
	var btnOpts = [{
		"name" : "取消",
		"float" : "right",
		"style" : "btn-default",
		"onclick" : function() {
			return true;
		}
	},{
		"name" : "确定",
		"float" : "right",
		"style" : "btn-success",
		"onclick" : function() {
			var conWin = _parent().window.document.getElementById("itcDlgContent").contentWindow;
			var siteForm=conWin.$("#form1").iForm('getVal');
			//var siteId=conWin.$("#form1").iForm('getVal','siteId');
			if(!conWin.$("#form1").valid()){
				return false;
			}
			var buttonReturnFlag=false;
			$.ajax({
				type:"post",
				url:submiturl,
				data:{
					siteForm:FW.stringify(siteForm),
					siteOrg:siteForm.siteOrg
				},
				dataType:"json",
				async:false,
				complete:function(res){
					var response=res.responseJSON;
					var result= response && response.result;
					if(result.success){
						FW.success("保存成功");
						$("#siteConf_grid").datagrid("reload");
						buttonReturnFlag = true;
					}else{
						//执行失败,则通知用户错误原因，同时不关闭对话框
						var msg=result && result.msg;
						if(msg){
							FW.error(msg);
						}else{
							FW.error("系统执行错误");
						}
					}
				}
			});
			return buttonReturnFlag;
		}
	}];
	//新建系统配置对话框
	var dlgOpts = {
		width : 750,
		height : 350,
		closed : false,
		title : title,
		modal : true
	};
	FW.dialog("init", {
		"src" : src,
		"dlgOpts" : dlgOpts,
		"btnOpts" : btnOpts
	});
}
</script>

</head>
<body style="height: 100%;" class="bbox">
	<div class="bbox toolbar-with-pager" id="toolbar_wrap">
		<!-- 这里可以在分页器的同排渲染一个按钮工具栏出来 在下面的toolbar1中 -->
	    <div id="toolbar1" class="btn-toolbar ">
	        <div class="btn-group btn-group-sm">
	        	<button type="button" id="btn_new" class="btn btn-success" >新建</button>
	        </div>
	        <div class="btn-group btn-group-sm">
	        	<button type="button" id="btn_search" data-toggle="button" class="btn btn-default">查询</button>
	        </div>
	        <div class="btn-group btn-group-sm">
	        	<button type="button" id="btn_disable" class="btn btn-default" >禁用</button>
	        </div>
	        <div class="btn-group btn-group-sm">
	        	<button type="button" id="btn_enable" class="btn btn-default" >启用</button>
	        </div>
	    </div>
	    <!-- 上分页器部分 这里可以通过属性bottompager指定下分页器的DIV-->
	    <div id="pagination_1" class="toolbar-pager" bottompager="#bottomPager"></div>
	</div>
	
	<!--这里要清掉分页器的右浮动效果-->
	<div style="clear:both"></div>
	<div id="grid1_wrap" style="width:100%">
	    <table id="siteConf_grid" pager="#pagination_1" class="eu-datagrid">
	        
	    </table>
	</div>
	<!-- 下页器部分-->
	<div id="bottomPager" style="width:100%;margin-top:6px">
	</div>
	
	<div id="noResult" style="display:none;width:100%;height:62%;margin:10px;">
		<div style="height:100%;display:table;width:100%">
			<div style="display:table-cell;vertical-align:middle;text-align:center">
			    <div style="font-size:14px">没有找到符合条件的结果</div>
			</div>
		</div>
	</div>
	<!-- 错误信息-->
	<div class="row" id="grid1_error" style="display:none">
	      无法从服务器获取数据，请检查网络是否正常
	</div>
	<!-- 无数据 -->
	<div class="row" id="grid1_empty" style="display:none">
	      没有系统配置数据，单击这里创建新的配置数据
	</div>
</body>
</html>