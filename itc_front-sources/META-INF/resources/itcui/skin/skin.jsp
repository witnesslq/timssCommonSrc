<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.yudean.itc.code.ParamConfig"%>
<%
 	String basePath = (String)request.getAttribute(ParamConfig.S_BasePathKey);	
	String from = request.getParameter("from");//当from=local时，进入调试模式，不保存皮肤选项
	if(from==null){
		from = "online";
	}	
%>
<!DOCTYPE html>
<html>
<head>
	<title>选择皮肤</title>
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
	<script type="text/javascript" src="../../js/jquery-1.10.2.js"></script>
	<script type="text/javascript" src="../js/itcui.dev.js"></script>
	<script type="text/javascript" src="skin.js"></script>
	<link rel="stylesheet" type="text/css" href="../css/itcui.dev.css" media="all"/>
	<script type="text/javascript">
		var def = "tiankonglan";
		var from = "<%=from%>";
		var skinMap = {};
		function _itc_initskinlist(){
			var s = "";
			for(var k in _skins){
				s += "<div class='skin-container'>";
				s += "<div class='skin-title'>" + k + "</div>";
				var skins = _skins[k];
				for(var kk in skins){
					skinMap[skins[kk].id] = skins[kk];
					s += "<div skin='" + skins[kk].id + "' class='skin-wrap";
					if(skins[kk].id ==def){
						s += " skin-wrap-selected";
					}
					s += "' style='background-image:url(\"" + skins[kk].id + "/thumbnail.jpg\")'>";
					s += "<div class='skin-name'>" + skins[kk].title + "</div>";
					if(skins[kk].id ==def){
						s += "<div class='icon_yes_wrap'><div class='icon_yes'></div></div>";
					}
					s += "</div>";
				}
				s += "</div>";
			}
			$("body").append(s);
			$(".skin-wrap").click(function(){
				var me = $(this);
				$(".skin-wrap-selected").removeClass("skin-wrap-selected");
				$(".icon_yes_wrap").remove();
				me.addClass("skin-wrap-selected").append("<div class='icon_yes_wrap'><div class='icon_yes'></div></div>");
				var skinid = me.attr("skin");
				if(from=="online"){
					$.ajax({
						url : "<%=basePath%>user?method=setconf",
						data : {
							attr : "theme",
							val : skinid
						},
						dataType : "JSON",
						success : function(data){
							if(data && data.status==1){
								_parent().changeSkin(skinid,skinMap[skinid].options);
								FW.success("皮肤更换成功");
							}
							else{
								FW.success("皮肤更换失败");
							}
						},
						error : function(){
							FW.success("皮肤更换失败");
						}
					});
				}
				else{
					_parent().changeSkin(skinid,skinMap[skinid].options);
					FW.success("皮肤更换成功");
				}
			});
		}
		
		$(document).ready(function(){
			$.ajax({
				url : "<%=basePath%>user?method=getconf",
				data : {
					attr : "theme"
				},
				dataType : "JSON",
				success : function(data){
					if(data && data.status==1){
						if(data.msg){
							def = data.msg;
						}
					}
					_itc_initskinlist();
				},
				error : function(){
					_itc_initskinlist();
				}
			});
			
		});
	</script>
	<style>
		.skin-title{
			display: block;
			font-family: 'Microsoft Yahei', verdana;
			font-size: 14px;
			font-weight: bold;
			height: 34px;
			line-height: 34px;
			color:rgb(153, 153, 153);
			width:100%;
		}
		.skin-container{
			margin-bottom: 12px;
			margin-left: 10px;
			margin-right: 0px;
			margin-top: 0px;
			padding-bottom: 0px;
			padding-left: 10px;
			padding-right: 10px;
			padding-top: 0px;
			background-color: rgb(246, 246, 246);
			color: rgb(34, 34, 34);
			cursor: pointer;
		}
		.skin-wrap{
			display: inline-block;
			height: 120px;
			margin-bottom: 20px;
			margin-left: 0px;
			margin-right: 20px;
			margin-top: 0px;
			position: relative;
			width: 200px;
			border-width: 2px;
			border-color:rgb(246, 246, 246);
			border-style: solid;
		}
		.skin-wrap:hover{
			border-color:#0f6099;
		}
		
		.skin-wrap-selected{
			border-color:rgb(69, 129, 56);
		}
		
		.skin-name{
			position: absolute;
			bottom: 0;
			left: 0;
			width: 100%;
			line-height: 2;
			background: rgba(0,0,0,.5);
			color: #fff;
			text-align: center;
			font-size: 12px;
		}
		
		.icon_yes{
			background-image:url("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAMBAMAAACgrpHpAAAAA3NCSVQICAjb4U/gAAAAMFBMVEX///////////////////////////////////////////////////////////////9Or7hAAAAAEHRSTlMAESIzRFVmd4iZqrvM3e7/dpUBFQAAAAlwSFlzAAALEgAACxIB0t1+/AAAABx0RVh0U29mdHdhcmUAQWRvYmUgRmlyZXdvcmtzIENTNui8sowAAAAYdEVYdENyZWF0aW9uIFRpbWUAMjAxMy4xMi4xOPKESjIAAABQSURBVAiZY2AAAY6fYIoh/x+YYn7/BUzL/m8A0+t/MzCoKTCw/7/EwPD+MoP/fwcGhvl/Fd9/A8px/b/9fwFI0f7/f8GKZf4/BtOM5wKAJABnUBoGNMJ4CAAAAABJRU5ErkJggg==");
			display: inline-block;
			height: 12px;
			line-height: normal;
			margin-bottom: 0px;
			margin-left: 8px;
			margin-right: 0px;
			margin-top: 10px;
			width: 15px;
		}
		.icon_yes_wrap{
			background-attachment: scroll;
			background-color: rgb(69, 129, 56);
			bottom: 0px;
			color: rgb(34, 34, 34);
			display: block;
			height: 30px;
			position: absolute;
			right: 0px;
			width: 30px;
		}
	</style>

</head>
<body class="bbox">
	
</body>
</html>
