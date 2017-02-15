<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
  <head>
	<title>页面测试工具</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<jsp:include page="/page/mvc/mvc_include.jsp" flush="false" />
	<style>
		
	</style>
	<script>
		$(document).ready(function(){
			$.ajax({
					type : "POST",
					url : basePath + "workflow/process_inst/getHistory.do?processInstId=284960",
					data : {
						data : JSON.stringify("1")
					},
					dataType : "json",
					success : function(data) {
						alert(data);
					},
					error : function(XMLHttpRequest, textStatus, errorThrown) {
						alert(1);
						var s = XMLHttpRequest.responseText;
						alert(s);
					}
			});
			
			$.post(basePath+"workflow/process_inst/getHistory.do?processInstId=284960",function(data){
				alert(data);
		    });
		});
	</script>
  </head>
  <body>
    Timss-JSP-Template
  </body>
</html>
