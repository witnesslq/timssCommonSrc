<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>TIMSS系统运行异常页面</title>
<jsp:include page="/page/framework/timss_include.jsp" flush="false" />
<style type="text/css">
body,input {
	font-size: 12px;
	margin: 0;
	padding: 0;
	font-family: 'Microsoft Yahei', Helvetica, verdana;
	height: 88%;
}
.errMainArea {
	width: 400px;
	margin: 80px auto 0 auto;
	text-align: left;
	border: 1px solid #aaa;
}
.errTxtArea {
	padding-top: 30px;
	padding-left: 80px;
}
.errTxtArea .txt_title {
	font-size: 14px;
	font-weight: bolder;
	font-family: 'Microsoft Yahei', Helvetica, verdana;
}
.errBtmArea {
	padding: 10px 8px 25px 8px;
	background-color: #fff;
	text-align: center;
}
.title {
	background-color: #0A8745;
	font-weight: bold;
	color: #fff;
	padding: 6px 10px 8px 7px;
}
.btnFn1 {
	cursor: pointer !important;
	cursor: hand;
	height: 30px;
	width: 101px;
	padding: 3px 5px 0 0;
	font-weight: bold;
}
</style>
<script type="text/javascript">
	$(document).ready(function(e) {
		$("#reload").click(function(){
			location.reload()
		});
	});
</script>
</head>
<body>
	<div class="errMainArea">
		<div class="title">系统提示</div>
		<div class="errTxtArea">
			<p class="txt_title">对不起，系统读写文件失败!</p>
		</div>
		<div class="errBtmArea">
			<input id="reload" type="button" class="btnFn1" value="刷新页面" />
		</div>
	</div>
</body>
</html>
