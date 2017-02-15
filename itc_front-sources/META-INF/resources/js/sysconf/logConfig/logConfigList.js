var fields = [
			{title : "日志类别", id : "category", type : "combobox"},
			{title : "关键字", id : "searchBy"}
		];


var columns = [[ {
	field : 'operationTime',
	title : '时间',
	width : 150,
	fixed:true,
	sortable : false,
	formatter: function(value,row,index){
		return FW.long2time(value)
	}

}, {
	field : 'operator',
	title : '操作者',
	width : 100,
	fixed:true,
	sortable : false
}, {
	field : 'category',
	title : '类别',
	width : 100,
	fixed:true,
	sortable : false
}, {
	field : 'description',
	title : '内容',
	width : 80,
	sortable : false
}]];


$(document).ready(function() {
	
		$("#searchForm").iForm("init",{"fields":fields,options:{labelFixWidth:80,mdWidth:6}});	
		//初始化列表
		$("#table_log").iDatagrid( "init",{
			columns : columns,
			pageSize : pageSize,//默认每页显示的数目 只能从服务器取得
	        url : basePath+"sysconf/sysConfig/queryLogList.do",
	        singleSelect : true,
	        onLoadSuccess : function(data){
	        	var total = data.rows.length;
	            setTimeout(function(){ 
	            	$("#table_log").datagrid("resize"); 
	            },200);
	        }
	    });
		
		//日志类别下拉框初始化
		$.ajax({
			type : "POST",
			url: basePath+"sysconf/sysConfig/queryLogCategory.do",
			dataType : "json",
			success : function(data) {
				var dataArr = [["-1","全部"]];
				for(var i=0;i<data.length;i++){
					dataArr[dataArr.length] = [data[i].categoryId,data[i].description];
				}
				$("#f_category").iCombo("init",{
				    data : dataArr
				});
			}
		});
	});

function queryLogListBySearch(){
	
	var searchVal = $("#searchForm").iForm("getVal");
	
	var category = searchVal.category;
	var searchBy = searchVal.searchBy;

	$("#table_log").datagrid({
			url:basePath+"sysconf/sysConfig/queryLogList.do",
			pagination :true,
	        singleSelect:true,
	        pageSize:10,
	        queryParams:{
	        	"category":category,
	        	"searchBy":searchBy
	        },
	        onRenderFinish : function(){
	            $("#table_log").ITCUI_Pagination("create","#pagination_1",styleOpt);       
	        },
			onLoadSuccess : function(data){
				if(data.total==0){
					$("#noSearchResult").show();
				}else{
					$("#noSearchResult").hide();
				}
			}
	});
	
}
