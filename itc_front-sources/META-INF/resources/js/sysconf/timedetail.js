var quzform = {
	formList : [
	    		{title : "id", id : "id", type:"hidden"},
	        	{title : "任务名称", id : "jobName"},
	        	{title : "开始时间", id : "startTime",type:"datetime",options:{"startDate":"1980-01-01"}},
	        	{title : "结束时间", id : "endTime",type:"datetime",options:{"startDate":"1980-01-01"}},
	        	{title : "启动延迟(秒)", id : "startDelay",dataType:"number"},
	        	{title : "运行间隔(秒)", id : "repeatInterVal",dataType:"number"},
	        	{title : "Corn表达式", id : "repeatInterVal",dataType:"number"},
	    	    {title : "运行类型", id : "jobType",type:"radio",data:[["Bean","JavaClass",true],["Class","SpringBean"]],render:function(){
	    	    	
	    	    }},
	    	    {title : "运行类/Bean", id : "jobClass"},
	    		{title : "站点ID", id : "siteId", type:"hidden"},
	        	{title : "所属站点", id : "siteName", render:
	        		 function(id){
	        			var opts = {
	        					"datasource":basePath + "sysconf/systemConfig/sitefuzzyinfo.do",
	        					"getDataOnKeyPress":true,
	        					"clickEvent":function(id,name){
	        						$("#f_siteId").val(id);
	        						$("#f_siteName").val(name);
	        					},
	        					"showOn":"input",
	        					"highlight":true
	        				};
	        				$("#" + id).iHint("init",opts);
	        		}}
	    ],
}
$(document).ready(function() {
	var backbtnSelector = $("#toolbar #btn_back");
	var savebtnSelector = $("#toolbar #btn_save");
	var editbtnSelector = $("#toolbar #btn_edit");
	
	var quzformSelector = $("#autoform");
	quzformSelector.iForm("init",{"fields":quzform.formList, "options":{initAsReadonly:false}});
	quzformSelector.iForm("setVal",{updatetime:1403228450704,dob:614828450704});
	if("new" == _opertype){
		backbtnSelector.show();
		savebtnSelector.show();
	}
});
