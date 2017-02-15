<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.util.HashMap"%>
<%@ page import="com.yudean.itc.code.ParamConfig"%>
<%@page import="com.yudean.itc.dto.sec.SecureMenu"%>
<%@page import="com.yudean.itc.util.Constant"%>
<%@page import="com.yudean.mvc.util.LogUtil"%>
<%@page import="com.yudean.mvc.bean.handler.ThreadLocalVariable"%>
<%@page import="com.yudean.mvc.handler.ThreadLocalHandler"%>
<%@page import="com.yudean.itc.dto.sec.SecureUser"%>
<%@page import="com.yudean.mvc.configs.MvcWebConfig"%>
<%
	SecureUser user = (SecureUser)request.getSession().getAttribute(Constant.secUser);
	String username = (String)request.getSession().getAttribute("username");
	String currSite = user.getCurrentSite();
	String currSiteName = user.getCurrSiteName();
	HashMap<String,Boolean> ids = (HashMap<String,Boolean>)request.getSession().getAttribute("privFunc");
	String path = request.getContextPath();
 	String basePath = (String)request.getAttribute(ParamConfig.S_BasePathKey);
	String resBase = Constant.resBase;
	String jspPath = Constant.jspPath;
	String userInfoJson = null;
	String openUrl = null;
	String authType = (String)request.getSession().getAttribute("authType");
	try{//同步写入用户信息
		ThreadLocalHandler ThreadlocIns = ThreadLocalHandler.getInstance();
		ThreadLocalVariable ThreadlocData = ThreadlocIns.getVariableIns();
		if(null != ThreadlocIns && ThreadlocData != null){
			userInfoJson = (String)ThreadlocData.getThreadLocalAttribute("UserInfoJsonStr");
			openUrl = (String)ThreadlocData.getThreadLocalAttribute("openTabModeMain");
		}
	}catch(Exception e){
		LogUtil.error("main.jsp get userInfo error", e);
	}
	String skin = (String)request.getAttribute("skin");
	Object cross = session.getAttribute("crossSite");
%>
<!DOCTYPE html>
<html style="width:100%;height:100%">
<head>
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>TIMSS</title>
<script type="text/javascript" src="${basePath}js/jquery-1.10.2.min.js"></script>

<script type="text/javascript" src="${basePath}res?f=privilege.js"></script>
<script type="text/javascript" src="${basePath}res?f=route.js"></script>
<script type="text/javascript" src="${basePath}<%=resBase %>itcui/skin/skin.js"></script>
<script type="text/javascript" src="${basePath}<%=resBase %>itcui/js/itcui.min.js?ver=${iVersion}"></script>

<script type="text/javascript" src="${basePath}<%=resBase %>itcui/js/itcui_frame.min.js?ver=${iVersion}"></script>
<script type="text/javascript" src="${basePath}js/mvc/core/mainPage.js?ver=${iVersion}"></script>
<script type="text/javascript" src="${basePath}<%=resBase %>js/servletjs/const.js?ver=${iVersion}"></script>
<script type="text/javascript" src="${basePath}<%=resBase %>js/mvc/taskinfo.js?ver=${iVersion}"></script>
<script type="text/javascript" src="${basePath}itcui/imageviewer/imageviewer_ext.js"></script>
<script type="text/javascript" src="${basePath}itcui/imageviewer/jquery.fancybox.js"></script>
<script type="text/javascript" src="${basePath}itcui/imageviewer/helpers/jquery.fancybox-buttons.js"></script>

<link rel="stylesheet" type="text/css" href="${basePath}<%=resBase %>itcui/css/itcui.min.css" media="all" />
<link rel="stylesheet" type="text/css" href="${basePath}<%=resBase %>itcui/css/itcui_frame.min.css" media="all" />
<link id="skin-css" rel="stylesheet" type="text/css" href="${basePath}itcui/skin/<%=skin %>/theme.css" media="all"/>
<link rel="stylesheet" type="text/css" href="${basePath}itcui/css/font-awesome.min.css" media="all"/>
<link rel="stylesheet" type="text/css" href="${basePath}itcui/css/uploadify.css" media="all"/>
<link rel="stylesheet" type="text/css" href="${basePath}itcui/imageviewer/jquery.fancybox.css"/>
<link rel="stylesheet" type="text/css" href="${basePath}itcui/imageviewer/helpers/jquery.fancybox-buttons.css"/>
<link rel="shortcut icon" href="${basePath}favicon.ico" type="image/x-icon" /> 
<script>
	var basePath = "${basePath}";
	var jspPath = "<%=jspPath%>";
	var authType = "<%=authType%>";
	_ItcMvc_ApplicationGlobal = {
		userInfo:null,
		reLoad:function(){
			location.reload();
		}
	};
	<%if(request.getSession().getAttribute("canPreviewAttach") != null){%>
	FW.set("canPreviewAttach", true);
	<%}else{%>
	FW.set("canPreviewAttach", false);
	<%}%>
	<%if(null != userInfoJson){%>
	_ItcMvc_ApplicationGlobal.userInfo = eval(<%=userInfoJson %>);
	<%}%>
	<%if(null != openUrl){%>
		var _eipMode = "<%=basePath + openUrl %>";
	<%}%>
	var skinPage = basePath + "itcui/skin/skin.jsp";
	var defaultSkin = "<%=skin%>";
	function crossSite(id){
		FW.confirm("确认切换身份|切换身份将刷新整个页面，未保存的数据将丢失，是否继续？",function(){
			$.post("${basePath}login?method=switchsite&setdefault=0&sid=" + id,function(e){
				e = eval("(" + e + ")");
				if(e.status==1){
					location.reload();
				}
				else{
					FW.error(e.msg);
				}
			});	
		});
	}
	
	//门户设定对话框
	function showPortalConfig(){
		var src =  basePath + "portal/config.html";
		var btnOpts = [{
	            "name" : "关闭",
	            "float" : "right",
	            "style" : "btn-default",
	            "onclick" : function(){
	                return true;
	            }
	        },{
	            "name" : "重排卡片",
	            "float" : "left",
	            "style" : "btn-default",
	            "onclick" : function(){
	                document.getElementById("itcDlgPortalContent").contentWindow.resetLayout();
	            }
	        }
	    ];
	    var dlgOpts = {
	        width : 600,
	        height:400,
	        closed : false,
	        title:"门户设置",
	        modal:true,
	        idSuffix : "Portal"
	    };
	    Notice.dialog(src,dlgOpts,btnOpts);	    
	}

		
	//个性化设置对话框
	function showConfigCenter(){
		var src =  basePath + jspPath + "configcenter.jsp";
		var btnOpts = [{
	            "name" : "取消",
	            "float" : "right",
	            "style" : "btn-default",
	            "onclick" : function(){
	                return true;
	            }
	        },{
	            "name" : "确定",
	            "float" : "right",
	            "style" : "btn-success",
	            "onclick" : function(){
	            	var p = _parent().window.document.getElementById("itcDlgContent").contentWindow;
	            	var data = p.getData();
	            	$.ajax({
	            		url : "${basePath}user?method=saveconfcenter",
	            		dataType : "json",
	            		data : data,
						type: "post",
	            		success : function(data){
	            			if(data.status == 1){
	            				FW.success(data.msg);
	            				_parent().$("#itcDlg").dialog("close");
	            			}
	            			else{
	            				FW.error(data.msg);
	            			}
	            		}
	            	});
	            	return false;
	            }
	        }
	    ];
	    var dlgOpts = {
	        width : 600,
	        height:400,
	        closed : false,
	        title:"个性化设置",
	        modal:true
	    };
	    Notice.dialog(src,dlgOpts,btnOpts);	    
	}

	//站内通知检查

	function checkAnnounce(){
		$.ajax({
			url: basePath + "announce?method=getNew",
			type: "post",
			dataType: "json",
			success: function(xhr){
				if(!xhr || !xhr.status){
					return;
				}
				if(xhr.status > 0){
					popAnnounce(xhr.data);
				}
			}
		});
	}

	function popAnnounce(data){
		FW.dialog("init",{
			src: basePath + "announce?method=getContent&id=" + data.itemId,
			dlgOpts: {
				width: data.popupW,
				height: data.popupH,
				title: " ",
				noButtons: true
			},
			btnOpts: []
		});
	}

	//跨站列表
	$(document).ready(function(){
		//门户测试功能 只对特殊用户可见
		if(!privMapping["homepage_portal"]){
			$("#menu_portal_config").hide();
		}
		//收藏夹
		if(privMapping["homepage_favorite"]){
			$(".priv-fav").show();
			updateFavMenu();
		}
		if(!window._eipMode){
			checkAnnounce();
		}
		registerTabEvent();		
		<%if(session.getAttribute("crossSite")!=null){ %>
			$.post("${basePath}login?method=listsites",{},function(e){
				e = eval("(" + e + ")");
				var siteHtml = "";
				for(var i=0; i<e.length;i++){
					var data = e[i];
					var v = data[1];
					var key = data[0];
					var optCls = "dropdown-unselected";
					if("${defaultSite}" == key){
						v += "(默认)";								
					}
					if("<%=currSite%>" == key){
						optCls = "dropdown-selected";
					}
					siteHtml += "<li>";
					siteHtml += "<a onclick=\"crossSite('" + key + "')\">" + "<span class='" + optCls + "'></span>" +  v + "</a></li>";
				}
				$("#menu_switchsite").html(siteHtml);
			});
		<%}else{%>
			$("#menu_switchsite").hide();
		<%}%>
	});
</script>
<!-- <script type="text/javascript" src="${basePath}js/config_backend.js"></script> -->
<script type="text/javascript"
	src="${basePath}res?withinit=1&f=config.js"></script>
<style>
.title_username{
	font-size: 12px;
	float: left;
	height: 51px;
	line-height: 50px;
	vertical-align: middle;
	margin-left:59px;
}
.head_link{
	line-height: 26px;
	height: 20px;
	display: inline-block;
	margin-top:12px;
	float:right;
	margin-right:12px
}

.head_link>li{
	float: right;
	list-style: none;
	font-size: 12px;
	padding-left: 6px;
	color:rgb(102, 102, 102);
}

i{
	font-size:14px;
}

.system_logo{
	background-image:url('images/system_logo.png');
	float: left;
	width:149px;
	height:48px;
	margin-left:12px;
}

@media
(-webkit-min-device-pixel-ratio: 2),
(min-resolution: 192dpi) {
	.system_logo{
		background-image: url('images/system_logo@4x.png');
		background-size: 149px 48px;
	}
}

.li_username{color:rgb(34, 34, 34)!important;}
.dropdown .open{z-index: 13333;}

#mainframe_navtree{
	height:100%;overflow:hidden;position:absolute;top:0px;left:0px;
	z-index: 22;
}

#mainframe_bottom{
	width:100%;height:100%;position:relative;
}

.tree-width-p{
	padding-left:200px;
}

.tree-width{
	width:200px;
}

#mainframe_content{
	width:100%;
	height:100%;
	position: relative;
}

#itcui_nav_tab_container{
	clear:both;height: 36px;width: 100%;
	position:absolute;
	bottom:0px;
}

.pleft230{
	padding-left:230px;
}
.header{
	width: 100%;
	height:86px;
	position:absolute;
	top:0px;
	box-shadow: 0 1px 2px 0 RGBA(0,0,0,0.3);
	z-index:33;
	padding-top:5px;
}
</style>
</head>
<body class="bbox" style="width:100%;height:100%;padding-top:86px!important;min-width:950px;overflow-x:auto;">
	<div class="header">
		<span class="system_logo"></span>
		<span class="title_username">			
			<%if(null != cross && true == (Boolean)cross) {%>
			<ul style="position:relative;padding:0px;" class="head_link">
				<li class="dropdown">
					<a data-toggle="dropdown" class="itcui_link" id="link_help"><%=username %><%=" 的桌面" %><span class="caret"></span></a>
					<ul class="dropdown-menu pull-right" role="menu" id="menu_switchsite">
						<li><a>科技公司（默认）</a></li>
					</ul>
				</li>
			</ul>
			<%} else{%>
			<%=username %><%=" 的桌面" %>
			<%}%>
		</span>
		<ul class="head_link">
			<li class="li_username"><a class="itcui_link" href="${basePath}login?method=logout">退出</a></li>
			<li>|</li>
			<li class="dropdown">
				<a data-toggle="dropdown" class="itcui_link" id="link_help">帮助<span class="caret"></span></a>
				<ul class="dropdown-menu pull-right" role="menu">
					<!--<li><a href="#"><i class="icon-book"></i>如何使用</a></li>
					<li><a href="#"><i class="icon-key" style="visibility:hidden"></i>版本信息</a></li>-->
					<li><a onclick="_Main.showUpdateInfo()">版本信息</a></li>
				</ul>
			</li>
			<li>|</li>
			<li class="dropdown">
				<a data-toggle="dropdown" class="itcui_link" id="link_setting">设置<span class="caret"></span></a>
				<ul class="dropdown-menu pull-right" role="menu">
					<li><a onclick="_ITC.showSkinPage()"><i class="icon-key" style="visibility:hidden"></i>换肤</a></li>
					<li><a onclick="showEditPass()"><i class="icon-key"></i>修改密码</a></li>
					<li><a onclick="showConfigCenter()"><i class="icon-cog"></i>个性化</a></li>
					<li id="menu_portal_config"><a onclick="showPortalConfig()"><i class="icon-key" style="visibility:hidden"></i>门户设定</a></li>					
				</ul>
			</li>
			<li class="priv-fav" style="display:none">|</li>
			<li class="dropdown priv-fav" style="display:none">
				<a data-toggle="dropdown" class="itcui_link" id="link_fav">收藏夹<span class="caret"></span></a>
				<ul class="dropdown-menu pull-right" role="menu" id="menu_fav">
					<li class="disabled"><a href="javascript:addToFav()" id="link_add_fav">当前页面无法收藏</a></li>
					<li><a href="#" onclick="popFavManagement()">管理收藏夹</a></li>
				</ul>
			</li>
		</ul>
		<div class="itcui_nav_tab_container" id="itcui_nav_tab_container">
			
		</div>
	</div>
	<div class="mainframe_bottom tree-width-p" id="mainframe_bottom">		
		<div id="mainframe_navtree" class="cbox tree-width">
			
		</div>
		<div id="mainframe_content">
			
		</div>
	</div>
</body>
</html>
