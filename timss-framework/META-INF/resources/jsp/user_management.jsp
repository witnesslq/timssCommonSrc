<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<jsp:include page="/page/framework/auth_include.jsp" flush="false" />

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>用户管理</title>
<style>
.toolbar {
	width: 100%;
	height: 36px;
	margin-bottom: 2px;
	border-bottom-color: #5aa3d6;
	border-bottom-width: 1px;
	border-bottom-style: solid;
}
.layout-panel-west{
	border-right-color: rgb(198, 198, 198);
	border-right-style: solid;
	border-right-width: 1px;
}
</style>
<script>
	var currNode = null;
	var privMapping = null;
	var filter = "";
	var filterType = "";
	function loadListPage(){
		$("#user_div").attr("src","${basePath}${resBase}jsp/user_list.jsp?filter=" + filter + "&filterType=" + filterType);
	}
	
	$(document).ready(function(){
		$(window).resize(function(){
			$("body").layout("resize");
		});
		loadListPage();
		$('#org_tree').tree({
			url : "${basePath}tree?method=org",
			onClick: function(node){
				var id = node.id;
				if(id.substr(0,3)=="org"){
					filterType = "org";
				}
				else{
					filterType = "person";
				}
				filter = id.split("_")[1];
				firstInit = true;
				if(filterType=="org"){
					loadListPage();
				}
				else{
					$("#user_div").attr("src","${basePath}user?method=getuser&mode=edit&uid=" + filter);
				}
			}
		});
	});
	
		
</script>
</head>
<body class="easyui-layout">
 	<div data-options="region:'west'" style="width:195px;overflow-x:hidden">
 		<div id="org_tree" style="width:185px">
 			
 		</div>
 	</div>
 	<div data-options="region:'center'" style="overflow-y:hidden">
 		<iframe id="user_div" frameborder="no" border=0 style="width:100%;height:100%;overflow-y:auto;overflow-x:hidden">
 		</iframe>
 	</div>	
</div>
</body>
</html>