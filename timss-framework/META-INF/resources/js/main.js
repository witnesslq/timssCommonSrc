var tab_man = {"width":"170px","items":
		[{"name":"关闭全部","icon":"images/close_tab.png"},{"name":"-"},{"name":"选项卡切换部分"}]
	};
var nav_tree_show = true;
function adjust()
{
	var scn_height = document.documentElement.clientHeight;
	var scn_width = parseInt(document.documentElement.clientWidth);

	if(scn_width<740)
	{
		scn_width = 740;
		if(!$("body").hasClass("ovfx-s"))
		{
			$("body").addClass("ovfx-s");
		}
		$(".mainframe_bottom").css("width",scn_width);
		$(".mainframe_header").css("width",scn_width);
	}
	else
	{
		$("body").removeClass("ovfx-s");
		$(".mainframe_bottom").css("width","100%");
		$(".mainframe_header").css("width","100%");
	}

	var navtree_width = parseInt($(".mainframe_navtree").css("width"));	
	$(".mainframe_bottom").css("height",scn_height - 87);
	$(".mainframe_navtree").css("height",scn_height - 87);
	$("#btn_tree_fold").css("top",(scn_height - 87-60)/2+87);	
	if(scn_width<940&&nav_tree_show==true)
	{
		$('.mainframe_navtree').css("width","0px");
		$('#btn_tree_fold').css("left","1px");
		$('#btn_tree_fold').html("<img src=\"images/nav_arrow_expand.png\" />");
		$(".mainframe_content").css("width",scn_width-1);
		curr_nav_state = 0;
		nav_tree_show = false;
	}
	else if(scn_width>=940&&nav_tree_show==false)
	{
		$('.mainframe_navtree').css("width","200px");
		$('#btn_tree_fold').css("left","201px");
		$('#btn_tree_fold').html("<img src=\"images/nav_arrow_fold.png\" />");
		var navtree_width = parseInt($(".mainframe_navtree").css("width"));
		$(".mainframe_content").css("width",scn_width-201);
		curr_nav_state = 1;
		nav_tree_show = true;
	}
	$(".mainframe_content").css("width",scn_width-navtree_width-1);
}
$(window).resize(function() {
	adjust();
});
$(document).ready(function(){
	adjust();
	var input2 = new $.ITCUI_Input("#search_box",true);
	var d1 = new $.ITCUI_DeafultText("#itcui_input_search_box","搜索全局信息");
	$("#mainframe_content").load("jsp/user_management.jsp");
});
function go(url)
{
	$("#mainframe_content").load(url);
}
var msgbox = new $.ITCUI_MessageBox();
var topmsg = new $.ITCUI_ScreenTopMessage();
function showdlg(arg1,arg2,arg3,arg4)
{
	msgbox.show(arg1,arg2,arg3,arg4);
}
function succ(msg)
{
	topmsg.show(msg,"success");
}
function loading(msg)
{
	topmsg.show(msg,"loading");
}
function err(msg)
{
	topmsg.show(msg,"error");
}
var curr_nav_state = 1;
function nav_fold(act)
{
	var scn_width = parseInt(document.documentElement.clientWidth);
	if(curr_nav_state ==1||act=="fold")
	{
		curr_nav_state = 0;
		setTimeout("$('.mainframe_navtree').animate({'width':'0px'});",10);
		setTimeout("$('#btn_tree_fold').animate({'left':'1px'});",10);
		$('#btn_tree_fold').html("<img src=\"images/nav_arrow_expand.png\" />");
		$(".mainframe_content").css("width",scn_width-1);
	}
	else if(curr_nav_state ==0||act=="expand")
	{
		curr_nav_state = 1;
		setTimeout("$('.mainframe_navtree').animate({'width':'200px'});",10);
		setTimeout("$('#btn_tree_fold').animate({'left':'201px'});",10);
		$('#btn_tree_fold').html("<img src=\"images/nav_arrow_fold.png\" />");
		var navtree_width = parseInt($(".mainframe_navtree").css("width"));
		$(".mainframe_content").css("width",scn_width-201);
	}
}
$(document).ready(function(){
	$(".itcui_nav_tab").click(function(){
		if($(this).attr("id")!="mainframe")
		{
			$("#btn_tree_fold").hide();
		}
		else
		{
			$("#btn_tree_fold").show();
		}
		$(".itcui_nav_tab").removeClass("itcui_nav_tab_selected");
		$(this).addClass("itcui_nav_tab_selected");
		$(".mainframe_bottom").hide();
		$("#" + $(this).attr("id") + "_bottom").show();
	});
	$(".itcui_navtree_item").click(function(){
		$(".itcui_navtree_item").removeClass("itcui_navtree_item_selected");
		$(this).addClass("itcui_navtree_item_selected");
	});
	$(".itcui_navtree_grouptitle").click(function(){
		var box_id = this.id + "_subitem";
		var is_fold = $("#" + box_id).css("display")=="none"?true:false;
		if(is_fold)
		{
			$("#" + box_id).slideDown();
			$(this).addClass("itcui_navtree_grouptitle_expand");
		}
		else
		{
			$("#" + box_id).slideUp();
			$(this).removeClass("itcui_navtree_grouptitle_expand");
		}
	});
	$(".itcui_tab_mgmt_arrow").mousedown(function(){
		$("#tab_management").css("background-image","none");
		$("#tab_management").css("background-color","#a0b0c1");
	})
	$(".itcui_tab_mgmt_arrow").mouseup(function(){
		$("#tab_management").css("background-image","url('images/tab_hover.png')");
	})
	var menu = new $.ITCUI_DropDownMenu();	
	menu.create_menu(tab_man,"#tab_management","right");
});