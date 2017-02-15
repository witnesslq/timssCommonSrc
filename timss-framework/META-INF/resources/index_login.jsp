<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.yudean.itc.util.Constant" %> 
<%
	String g = (String)request.getAttribute("g");
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	String resBase = Constant.resBase;
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
	<meta content="IE=edge" http-equiv="X-UA-Compatible" />
	<title>TIMSS</title>
	<link rel="stylesheet" type="text/css" href="<%=basePath%><%=resBase %>css/login.css" media="all"/>
	<link rel="stylesheet" type="text/css" href="<%=basePath%><%=resBase %>itcui/css/itcui.dev.css" media="all" />
	<link rel="shortcut icon" href="<%=basePath %>favicon.ico" type="image/x-icon" /> 
	<script type="text/javascript" src="<%=basePath%><%=resBase %>js/jquery-1.10.2.js"></script>
	<script type="text/javascript" src="<%=basePath%><%=resBase %>itcui/js/itcui.dev.js"></script>	
	<script type="text/javascript" src="<%=basePath%><%=resBase %>js/public.js"></script>	
	<script type="text/javascript" src="<%=basePath%><%=resBase %>js/jquery.cookie.js"></script>
	<script type="text/javascript" src="<%=basePath%><%=resBase %>js/json.js"></script>
	<script>
	var current_tab = "left";
	var popup;
	var handle1,handle2;
	$(document).ready(function(){
		$("#ipt_username").focus(function(){
			$("#wrap_username").addClass("ipt_wrap_focus");
			$("#wrap_password").removeClass("ipt_wrap_focus");
		});
		$("#ipt_password").focus(function(){
			$("#wrap_password").addClass("ipt_wrap_focus");
			$("#wrap_username").removeClass("ipt_wrap_focus");
		});
		$("#tab_domain").mouseover(function(){
			handle1 = setTimeout("current_tab='left';switchTab(current_tab)",500);
		});
		$("#tab_domain").mouseout(function(){
			clearTimeout(handle1);
		});
		$("#tab_normal").mouseover(function(){
			handle2 = setTimeout("current_tab='right';switchTab(current_tab)",500);
		});
		$("#tab_normal").mouseout(function(){
			clearTimeout(handle2);
		});
		switchTab("left");
		$("#ipt_password_fake").focus(function(){
			$("#ipt_password_fake").hide();
			$("#ipt_password").show();
			$("#ipt_password").focus();
		});
		$("#ipt_password").blur(function(){
			if($(this).val()==""){
				$("#ipt_password_fake").show();
				$("#ipt_password").hide();
			}
		});
	});
	function switchTab(tab)
	{
		$("#ipt_username").val("");
		$("#ipt_password").val("");
		ipt_username.blur();
		ipt_password_fake.blur();
		if(tab=="left")
		{
			$(".tab_login").css("background-image","url('<%=basePath %><%=resBase %>images/tab_left.png')");
			$(".checkbox_wrap").html('<span class="itcui_chkbox" id="remember_id" style="float:left"></span><span class="login_chbox_text" style="float:left;margin-left:4px">记住工号</span><span class="login_chbox_text" style="float:right;margin-left:4px">使用指纹登录</span><span class="itcui_chkbox" style="float:right"></span>');
			var d1 = new $.ITCUI_DeafultText("#ipt_username","工号");
			var d2 = new $.ITCUI_DeafultText("#ipt_password_fake","密码");	
		}
		else
		{
			$(".tab_login").css("background-image","url('<%=basePath %><%=resBase %>images/tab_right.png')");
			$(".checkbox_wrap").html('<span class="itcui_chkbox" id="remember_username" style="float:left"></span><span class="login_chbox_text" style="float:left;margin-left:4px">记住用户名</span><a href="#" class="lnk_forget" style="margin-left:86px;">忘记密码？</a>');
			var d1 = new $.ITCUI_DeafultText("#ipt_username","用户名");
			var d2 = new $.ITCUI_DeafultText("#ipt_password_fake","密码");
		}	
		$("#ipt_password").keyup(function(e){
			var code = e.which;
			if(code==13){
				login();
			}
		});
		$(".itcui_chkbox").click(function(){
			var cls = $(this).attr("class");
			if(cls.indexOf("itcui_chkbox_checked")>0){
				$(this).removeClass("itcui_chkbox_checked");
			}
			else
			{
				$(this).addClass("itcui_chkbox_checked");
			}
		});
		$(".ipt_wrap").click(function(){
			$(this).children("input").focus();
		});
		$("#ipt_password_fake").show();
		$("#ipt_password").hide();
		var uid = $.cookie("itcauthority_username");
		if(uid){
			$("#remember_id").addClass("itcui_chkbox_checked");
			$("#remember_username").addClass("itcui_chkbox_checked");
			$("#ipt_username").val(uid);
			$("#ipt_username").removeClass("itcui_default_text");
		}
	}

	function go()
	{
		window.location.href = "<%=basePath %>login?method=index";
	}
	
	function set_cookie(uid){
		if($("#remember_id").hasClass("itcui_chkbox_checked")||$("#remember_username").hasClass("itcui_chkbox_checked")){
			$.cookie('itcauthority_username', uid,{"expires":60});
		}
		else{
			$.removeCookie('itcauthority_username');
		}
	}
	
	function login()
	{
		var lgbtn = $(".login_button");
		if(lgbtn.hasClass("login_button_disable")){
			//防止重复点击按钮
			return;
		}
		lgbtn.addClass("login_button_disable");
		lgbtn.removeClass("lg_btn");
		lgbtn.html("正在登录中......");
		var username = $("#ipt_username").val();
		var password = $("#ipt_password").val();
		if(username==""||password=="")
		{
			$(".error_box").show();
			$(".error_box").html("用户名和密码不能为空！");			
			lgbtn.removeClass("login_button_disable");
			lgbtn.html("登录");
			lgbtn.addClass("lg_btn");
			return;
		}
		$.ajax({
			url : "<%=basePath%>login",
			data : {
				method : "login",
				uid : username,
				password : password
			},
			cache : false,
			timeout: 10000,
			success : function(e,stat){
				e = eval("(" + e + ")");
				if(e.status==-1){
					$(".error_box").show();
					$(".error_box").html(e.msg);
					lgbtn.removeClass("login_button_disable");
					lgbtn.html("登录");
					lgbtn.addClass("lg_btn");
				}
				else if(e.status==1){
					window.location.href = "<%=basePath %>login?method=index";
					set_cookie(username);
				}
				else if(e.status==2){
					//多站点切换			
					//popup = new $.ITCUI_Popup();
					//popup.show(450,200,"站点选择","<%=basePath %><%=resBase %>jsp/select_org.jsp",{"mask":true,"nobuttons":true});
					var src = "<%=basePath %><%=resBase %>jsp/select_org.jsp";
					var dlgOpts = {
				        width : 450,
				        height: 200,
				        closed : false,
				        title:"选择要登陆的站点",
				        modal:true,
				        noButtons:true
				    };
					Notice.dialog(src,dlgOpts,null);
					set_cookie(username);
				}
			},
			error: function(jqXHR, textStatus, errorThrown) {
				$(".error_box").html("连接超时，请稍后再试");	
				$(".error_box").show();				
				lgbtn.removeClass("login_button_disable");
				lgbtn.html("登录");
				lgbtn.addClass("lg_btn");
			}
		});
	}
	</script>	
	<title>登录</title>
</head>
<body>
<div id="main" class="main">
	<div id="header" class="header">
		<span class="yd_logo"></span>
		<span class="system_name"></span>
		<!--<SPAN style="width:250px;height:38px;color:#a6a6a6;font-size:24px;font-family:SimHei;line-height: 38px;">企业资产管理系统</SPAN>-->
	</div>
	<div class="login_box">
		<div class="login_pannel">
			<div class="tab_login">
				<div class="tab_sub" id="tab_domain" onclick="switchTab('left');">
					<span>域帐号登录</span>
				</div>
				<div class="tab_sub" id="tab_normal" onclick="switchTab('right');">
					<span>系统帐号登录</span>
				</div>
			</div>
			<!--错误提示-->
			<div class="error_box_wrap">
				<div class="error_box">
				</div>
			</div>
			<!--用户名和密码输入框-->
			<span class="ipt_wrap" id="wrap_username" style="margin-top:13px">
				<input type="text" name="username" id="ipt_username" class="ipt_login ipt_noborder"/>
			</span>
			<span class="ipt_wrap" id="wrap_password">
				<input type="password" name="password" id="ipt_password" class="ipt_login ipt_noborder" style="display:none"/>
				<input type="text" name="password" id="ipt_password_fake" class="ipt_login ipt_noborder" />
			</span>			
			<div class="checkbox_wrap">				
				
			</div>
			<span class="login_button lg_btn" style="letter-spacing:0.5em" onclick="login();">登录</span>
		</div>
	</div>
	<div class="footer" style="margin-bottom:12px">
	       浏览器以IE10及以上版本为佳，技术支持电话：020-85138922<br/>
		&nbsp;&nbsp;Copyright &copy; 2013 广东粤电信息科技有限公司，保留所有权利。
	</div>
</div>	
</body>
</html>