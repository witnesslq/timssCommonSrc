<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html style="height:99%;">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<jsp:include page="/page/sysconf/auth_include.jsp" flush="false" />
<title>设置中心</title>
<script>
var formOpts = [{id:"mailNotice",title:"邮件通知","type":"switch"},
                {linebreak:true,id:"smsNotice",title:"短信通知","type":"switch"},
	            /*{linebreak:true,id:"operationTips",title:"操作提示","type":"switch"},*/
	            {linebreak:true,id:"defaultSite",title:"默认身份","type":"combobox",options:{url:"${basePath}login?method=listsites",remoteLoadOn:"init"}}/*,
	            {linebreak:true,id:"fixTabs",title:"固定选项卡","type":"checkbox",helpMsg:"当选项卡数量过多时，被设置为固定的选项卡不会被隐藏，最多可以同时固定5个选项卡。",wrapXsWidth:12,wrapMdWidth:12}*/
];

$(document).ready(function(){
	$.ajax({
		url : "${basePath}res?f=tabs",
		dataType : "json",
		success : function(data){
			//formOpts[4].data = data;
			$("#main_form").iForm("init",{"fields":formOpts,options:{xsWidth:6,mdWidth:6}});
			<%if((Boolean)session.getAttribute("crossSite")!=true){ %>
				$("#main_form").iForm("hide","defaultSite");
			<%}%>
			$.ajax({
				url : "${basePath}user?method=getconfcenter&rand=" + new Date().getTime(),
				dataType : "json",
				success : function(data){
					$("#main_form").iForm("setVal",data);	
				}
			});
		}
	});
});

function getData(){
	var formData = $("#main_form").iForm("getVal");
	//将空值的选项覆盖为N 以区分新用户的默认值还是用户选择了否
	formData.mailNotice = formData.mailNotice || "N";
	formData.smsNotice = formData.smsNotice || "N";
	formData.operationTips = formData.operationTips || "N";
	return formData;
}
</script>

</head>
<body style="padding-top:6px;height:99%">
	<form style="width:550px" id="main_form">
	</form>
</body>
</html>