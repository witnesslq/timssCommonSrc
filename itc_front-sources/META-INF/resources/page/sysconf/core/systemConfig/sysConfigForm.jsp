<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" style="width:100%;height:100%">
<head>
<link rel="shortcut icon" href="${basePath}favicon.ico" type="image/x-icon" />
<title>系统配置表单</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<!-- 由于在本地web工程中，这个路径不存在，会编译异常 -->
<jsp:include page="/page/mvc/mvc_include.jsp" flush="false" />
<script>
	var sysConfFormField=[
			{id:"conf",title:"配置项",wrapXsWidth:12,wrapMdWidth:12,rules:{required:true,maxChLength:66}},
			{id:"val",title:"配置值",wrapXsWidth:12,wrapMdWidth:12,rules:{required:true,maxChLength:330}},
			{id:"siteId",title:"站点",wrapXsWidth:12,wrapMdWidth:12},
			{id:"desp",title:"配置描述",type:"textarea",wrapXsWidth:12,wrapMdWidth:12,rule:{maxChLength:330}},
			{id:"updatedBy",type : "hidden"}
			
	];
	$(document).ready(function() {
		var sysConf = '${sysConf}';
		$("#form1").iForm("init",{fields:sysConfFormField});
		if(''!=sysConf){
			$("#oldSysConf").val(sysConf);
			sysConf = ${sysConf};
			$("#form1").iForm("setVal",sysConf);
		}
	});
</script>
</head>
<body>
	<!--这里要清掉分页器的右浮动效果-->
	<div style="clear:both"></div>
	<input type="hidden" id="oldSysConf"/>
	<form id="form1"  class="margin-form-title margin-form-foldable"></form>
</body>
</html>