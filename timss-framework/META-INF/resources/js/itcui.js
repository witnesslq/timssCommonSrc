/**
 * ITCUI
 * author:murmur
 * 新浪微博：默语1
 * version:0.3（去除iframe支持）
 * 
 * 请注意，所有的函数请于document.ready中执行，否则会出现选择器无法选择对象的问题！！
 */
var _current_menu;
var _last_mouse_over_menu;
/*
	公用函数
*/
//获取某的对象相对于<HTML>的绝对坐标
function get_abs_position(obj)
{
	var o = $(obj);
	var obj_x = o.offset().left;
	var obj_y = o.offset().top;
	while(o.attr("parentNode"))
	{
		o = o.parent();
		obj_x += o.offset().left;
		obj_y += o.offset().top;
	}
	var p = {"left":obj_x,"top":obj_y};
	return p;
}
//调整日期选择框（jquery-ui）的位置
function adjDatepicker(div_id)
{
	var abs_pos = get_abs_position(div_id);
	var o_top = parseInt($(div_id).css("height"));
	$("#ui-datepicker-div").css("top",abs_pos.top + o_top +4);
	$("#ui-datepicker-div").css("left",abs_pos.left);
}

function join_dict_keys(items,delimiter)
{
	var str = "";
	var first = true;
	for(var k in items){
		if(first==false){
			str += delimiter + k;
		}
		else{
			str += k;
			first = false;
		}
	}
	return str;
}

(function($){
	/*
	--------------------------------------
				输入框默认文字
	--------------------------------------
	*/
	$.ITCUI_DeafultText = function(input_id,default_text){
		var o = $(input_id);
		o.val(default_text);
		o.addClass("itcui_default_text");
		o.focus(function(){
			var v = $(input_id).val();
			if(v==default_text){
				$(input_id).val("");
				$(input_id).removeClass("itcui_default_text");
			}
		});
		o.blur(function(){
			var v = $(input_id).val();
			if(v==""){
				$(input_id).val(default_text);
				$(input_id).addClass("itcui_default_text");
			}
		});
	};
	/*
	--------------------------------------
				验证错误信息
	--------------------------------------
	*/
	$.ITCUI_Validator = function(){
		this.errormsg = function(target,left_offset,message)
		{
			if(document.getElementById("itcui_err_icon_" + target.substr(1))==null){
				var warn_html = "<span id='itcui_err_icon_" + target.substr(1) + "' class='itcui_icon_warn_mid' style='cursor:pointer;float:left' title='" + message + "'></span>";
				$(target).after(warn_html);
			}
			else{
				$("#itcui_err_icon" + target.substr(1)).attr("title",message);
			}
		};
		
		this.removemsg = function(target){
			$("#itcui_err_icon_" + target.substr(1)).remove();
		};
	};
	/*
	--------------------------------------
				上方提示信息
	--------------------------------------
	*/
	$.ITCUI_ScreenTopMessage = function(){
		this.show = function(msg,msgtype,parent){
			var p = document.body;
			var scn_width = parseInt(p.clientWidth);
			$("#itcui_screen_top_msg").remove();
			var msg_html = "";
			if(msgtype=="success"){
				msg_html = "<div class='itcui_tips_success";
			}
			else if(msgtype=="error"){
				msg_html = "<div class='itcui_tips_error";
			}
			else if(msgtype=="loading")
			{
				msg_html = "<div class='itcui_tips_loading";
			}
			else
			{
				return;
			}
			msg_html += " itcui_tips_top' style='left:" + (scn_width - 194)/2 + "px' id='itcui_screen_top_msg'>";
			msg_html += msg + "</div>";
			$("body").append(msg_html);
			$("#itcui_screen_top_msg").hide();
			$("#itcui_screen_top_msg").slideDown();
			setTimeout("$('#itcui_screen_top_msg').slideUp()",5000);
			setTimeout("$('#itcui_screen_top_msg').remove()",5500);
		};
	};
	/*
	--------------------------------------
			弹出式对话框（DIV能动）
	--------------------------------------
	*/
	$.ITCUI_Popup = function(){
		var pfix = "";
		var lvl = 0;
		this.close = function(){
			$("#itcui_popup_wrap" + pfix).remove();
			$(".itcui_background_mask").remove();
			curr_dialog_title[lvl] = "";
		};
		
		/**
			opts为参数，里面可选项为
			level - 对话框层级，只用于区分不同对话框，用于在对话框中弹出对话框
			zindex - 对话框叠放次序，此参数会自动影响灰色遮罩层
			nobuttons - 取true时将无按钮栏
			mask - 取true时将显示遮罩层此时对话框无法移动
			parent - 在iframe里向主页面弹窗时取true，否则取false
			extargs - 在显示html_src时附加的post
		*/
		this.show = function(dlg_width,dlg_height,title,html_src,opts){
			if(opts&&opts["level"]){
				lvl = opts["level"];
			}
			
			/*
			if(curr_dialog_title[lvl]){
				return;
			}	
			curr_dialog_title[lvl] = title;
			*/
			var prefix = (opts&&opts["level"])?opts["level"]:"";
			pfix = prefix;
			$("#itcui_popup_wrap" + prefix).remove();
			var p = document.body;
			pp = window.document;
			var scn_height = p.clientHeight;
			var scn_width = p.clientWidth;
			//当没有按钮时向上缩短32px
			dlg_height += (opts&&opts.nobuttons==true)?34:66;
			var dlg_top = (scn_height - dlg_height)/2;
			var dlg_left = (scn_width - dlg_width)/2;
			var zidx = (opts&&opts["zindex"])?opts["zindex"]:19999;
			var dlg_html = "<div id='itcui_popup_wrap" + prefix + "' class='itcui_messagebox_wrap' style='z-index:" + zidx + ";top:" + dlg_top + "px;left:" + dlg_left + "px;width:" + dlg_width + "px;height:" + dlg_height + "px;'>";
			//标题栏
			dlg_html += "<div id='itcui_popup_title' class='itcui_messagebox_title' width='100%'><span class='itcui_popup_title_text'>" + title + "</span><span id='itcui_popup_cross' class='itcui_btn_cross' style='margin-top:8px;float:right' onclick='$(\"#itcui_popup_wrap" + prefix + "\").remove();$(\".itcui_background_mask\").remove();curr_dialog_title[" + lvl +  "]=\"\"'></span></div>";
			//内层弹窗内容
			dlg_html += "<div id='itcui_messagebox_content" + prefix + "' name='itcui_messagebox_content" + prefix + "' style='overflow-y:auto;padding-left:8px;padding-right:8px;padding-top:8px;'></div>";
			
			if(!(opts&&opts.nobuttons==true))
			{
				dlg_html += "<div class='itcui_messagebox_button_wrap' style='width:" + (dlg_width-6) + "px'>";
				//对话框按钮组				
				if(opts&&opts.del)
				{
					dlg_html += "<span id='itcui_messagebox_cancel' onclick='$(\"#itcui_popup_wrap" + prefix + "\").remove()' class='itcui_btn_base itcui_btn_gray' style='width:60px;margin-left:6px;float:left'>删除</span>";
				}
				if(opts/*&&opts.cancel*/)//取消必须有
				{
					dlg_html += "<span id='itcui_messagebox_cancel' onclick='$(\"#itcui_popup_wrap" + prefix + "\").remove()' class='itcui_btn_base itcui_btn_gray' style='width:60px;margin-left:6px;float:right'>取消</span>";				
				}
				if(opts&&opts.ok)
				{
					var okfunction = (opts&&opts["okfunction"])?opts["okfunction"]:"$(\"#itcui_popup_wrap" + prefix + "\").remove();curr_dialog_title[" + lvl + "]=''";
					dlg_html += "<span id='itcui_messagebox_ok' class='itcui_btn_green itcui_btn_base' style='width:60px;margin-left:8px;float:right' onclick='" + okfunction + "'>确定</span>";
				}
				dlg_html += "</div>";
			}
			dlg_html += "</div>";
			//遮罩层
			if(opts&&opts.mask==true)
			{
				dlg_html += "<div class='itcui_background_mask' style='height:" + scn_height + ";width:" + scn_width + "px;z-index:" + (zidx-1) + "'></div>";
			}
			//根据参数判断是在当前框架还是父级框架显示对话框
			$("body").append(dlg_html);
			//添加随机码
			if(html_src.indexOf("?")>0){
				html_src += "&itcdlg_randcode=" + Math.random();
			}
			else{
				html_src += "?itcdlg_randcode=" + Math.random();
			}
			$("#itcui_messagebox_content" + prefix).load(html_src,opts["extargs"]);
			//这里计算的时候要注意把padding也计算进去
			$("#itcui_messagebox_content" + prefix).css("width",dlg_width-16);
			$("#itcui_messagebox_content" + prefix).css("height",dlg_height-74);
			if(!(opts&&opts.mask==true))
			{
				_set_mouse_move();
			}
		};
		var moving = 0; 
		var _x, _y; 
		function _set_mouse_move()
		{
		    $("#itcui_popup_title").mousedown(function(event){ 
		        //debugger; 
		        this.setCapture(); 
		        moving = 1; //开始移动标识 
		        _x = event.clientX; 
		        _y = event.clientY; 
		        //记录鼠标当前位置 
		    }); 
		    $("#itcui_popup_title").mouseup(function(event){ 
		        this.releaseCapture(); 
		        moving = 0;		        
		    }); 
		    $("#itcui_popup_title").mousemove(function(event){ 
		        if (moving == 1) { 
		            //获取鼠标移动中的位置 
		            var x = event.clientX; 
		            var y = event.clientY;          
		            //为窗体赋新位置 
		            var X0 = parseInt($("#itcui_popup_wrap" + pfix).css("left")); 
		            var Y0 = parseInt($("#itcui_popup_wrap" + pfix).css("top")); 
		            $("#itcui_popup_wrap" + pfix).css("top", (Y0 + y - _y)); 
		            $("#itcui_popup_wrap" + pfix).css("left", (X0 + x - _x)); 
		            _x = x; 
		            _y = y; 
		        } 
		    }); 
		}
	};
	/*
	--------------------------------------
				弹出式对话框
	--------------------------------------
	*/
	$.ITCUI_MessageBox = function(){
		var ok_func = function(){
			hide_dlg();
		};
		
		var close_func = function(){
			hide_dlg();
		};
		
		this.set_ok_func = function(f){
			ok_func = f;
		};
		
		this.set_close_func = function(f){
			close_func = f;
		};
		
		this.show = function(dlgwidth,dlgheight,title,message,parent){
			var p = document.body;
			var scn_height = p.clientHeight;
			var scn_width = p.clientWidth;
			var dlg_top = (scn_height - dlgheight)/2;
			var dlg_left = (scn_width - dlgwidth)/2;
			var inner_wrap_width = dlgwidth - 32;
			var inner_wrap_height = dlgheight - 32;
			var dlg_html = "<div class='itcui_messagebox_wrap' style='top:" + dlg_top + "px;left:" + dlg_left + "px;width:" + dlgwidth + "px;height:" + dlgheight + "px;'>";
			dlg_html += "<div class='itcui_messagebox_title' width='100%'><span id='itcui_messagebox_cross' class='itcui_btn_cross' style='margin-top:8px;float:right'></span></div>";//标题栏
			dlg_html += "<div style='height:" + (dlgheight-26-56) + "px;width:100%'>";
			dlg_html += "<div style='width:" + inner_wrap_width + "px;height:" + inner_wrap_height + "px;margin-top:16px;margin-left:16px'>";
			dlg_html += "<div style='width:32px;height:" + inner_wrap_height + "px;'><span class='itcui_icon_warn_big' style='float:left;margin-top:" + ((inner_wrap_height - 48-40-26)/2) + "px'></span>";//大号图标
			dlg_html += "<div style='width:" + (inner_wrap_width-48) + "px;height:32px;padding-top:6px;' class='itcui_messagebox_content_title'><span style='margin-left:16px;'>" + title + '</span></div>';//对话框标题
			dlg_html += "<div class='itcui_messagebox_content' style='width:" + (inner_wrap_width-48) + "px;'><span style='margin-left:16px'>" + message + "</span></div>";//对话框内容
			dlg_html += "</div>";//内层信息
			dlg_html += "</div>";//内层wrap
			dlg_html += "</div>";//中间内容
			dlg_html += "<div class='itcui_messagebox_button_wrap' style='width:" + (dlgwidth-6) + "px'>";
			dlg_html += "<span id='itcui_messagebox_cancel' class='itcui_btn_base itcui_btn_gray' style='width:60px;margin-left:6px;float:right'>取消</span><span id='itcui_messagebox_ok' class='itcui_btn_green itcui_btn_base' style='width:60px;margin-left:8px;float:right'>确定</span></div>";
			dlg_html += "</div>";//最外层
			dlg_html += "<div class='itcui_background_mask' style='height:" + scn_height + ";width:" + scn_width + "px'></div>";//外层遮罩		
			$("body").append(dlg_html);

			$("#itcui_messagebox_cross,#itcui_messagebox_cancel").click(function(){
				close_func();
			});
			
			$("#itcui_messagebox_ok").click(function(){
				ok_func();
			});
		};
		
		this.close = function(){
			hide_dlg();	
		};
		
		function hide_dlg()
		{
			$(".itcui_messagebox_wrap").fadeOut();
			$(".itcui_messagebox_wrap").remove();
			$(".itcui_background_mask").remove();
		}
	};
	/*
	--------------------------------------
				可以折叠的效果
	--------------------------------------
	*/
	$.ITCUI_Foldable = function(div_id){
		var sub_item_id = div_id.substr(1) + "_subitem";
		$(div_id).click(function(){
			if($("#" + sub_item_id).css("display")=="none")
			{
				$("#" + sub_item_id).slideDown();
				$(div_id + "_arrow").addClass("itcui_frm_grp_title_arrow_expand");
				if($(div_id).hasClass("itcui_form_group_last"))
				{
					$(div_id).css("border-bottom-style","solid");
				}
			}
			else
			{
				$("#" + sub_item_id).slideUp();
				$(div_id + "_arrow").removeClass("itcui_frm_grp_title_arrow_expand");
				if($(div_id).hasClass("itcui_form_group_last"))
				{
					$(div_id).css("border-bottom-style","none");
				}
			}
		});		
	};
	/*
	--------------------------------------
				单选框
	--------------------------------------
	*/
	$.ITCUI_Radio = function(div_id,group_name,selected){
		if(selected==true)
		{
			$(div_id).addClass("itcui_radio_select");
		}
		else
		{
			$(div_id).addClass("itcui_radio_unselect");
		}
		//自动居中
		var p = $(div_id).parent();
		var p_height = parseInt(p.css("height"));
		var new_top = (p_height - 14)/2 -1;//14px的选项框比文字略大 所以这里不是刚好居中
		$(div_id).css("margin-top",new_top);
		$(div_id).click(function(){
			$("[name='" + group_name + "']").removeClass("itcui_radio_select");
			$("[name='" + group_name + "']").addClass("itcui_radio_unselect");
			$(this).addClass("itcui_radio_select");
		});
	};
	/*
	--------------------------------------
				输入框
	--------------------------------------
	*/
	$.ITCUI_Input = function(div_id,large,password){
		//初始化部分
		var disabled = false;
		var ipt_name = div_id.substr(1);
		var ipt_id = "itcui_input_" + ipt_name;
		var ipt_width = parseInt($(div_id).css("width"));
		$(div_id).addClass("itcui_input_wrap");
		if(password){
			$(div_id).html("<input type='password' class='itcui_input' type='text' id='" + ipt_id + "' name='" + ipt_name + "' style='width:" + ipt_width + "px'/>");
		}
		else{
			$(div_id).html("<input class='itcui_input' type='text' id='" + ipt_id + "' name='" + ipt_name + "' style='width:" + ipt_width + "px'/>");
		}
		if(large==true)
		{
			$(div_id).addClass("itcui_input_wrap_large");
		}
		$("#" + ipt_id).focus(function(){
			if(disabled==false)
			{
				$(div_id).addClass("itcui_input_wrap_hover");
			}
		});
		$("#" + ipt_id).blur(function(){
			if(disabled==false)
			{
				$(div_id).removeClass("itcui_input_wrap_hover");
			}
		});

		this.disable = function(){
			$("#" + ipt_id).attr("readOnly",true);
			$("#" + ipt_id).addClass("itcui_disable_mask");
			$(div_id).addClass("itcui_disable_mask");
			disabled = true;
		};

		this.enable = function(){
			$("#" + ipt_id).attr("readOnly",false);
			$("#" + ipt_id).removeClass("itcui_disable_mask");	
			$(div_id).removeClass("itcui_disable_mask");
			disabled = false;
		};
		
		this.onlyLabel = function()
		{
			$(div_id).removeClass("itcui_input_wrap");
			$(div_id).addClass("itcui_input_wrap_onlylabel");
			$("#" + ipt_id).attr("readOnly",true);
		};

		this.removeOnlyLabel = function()
		{
			$(div_id).addClass("itcui_input_wrap");
			$(div_id).removeClass("itcui_input_wrap_onlylabel");
			$("#" + ipt_id).attr("readOnly",false);
		};
		
		this.turnDatePicker = function(in_table){
			$("#" + ipt_id).css("width",ipt_width-26);
			var icon_html = "";
			if(in_table)
			{
				icon_html = "<span class='itcui_btn_calander' style='display:inline-block;'></span>";
			}
			else
			{
				icon_html = "<span class='itcui_btn_calander fr mt4' style='display:inline-block'></span>";
			}
			$(div_id).append(icon_html);
			$("#" + ipt_id).datepicker($.datepicker.regional["zh-CN"]);
			$("#" + ipt_id).focus(function(){
				setTimeout("adjDatepicker('" + div_id + "')",20);
			});
		};

		this.getValue = function(){
			return $("#" + ipt_id).val();
		};

		this.setValue = function(v){
			if(v==null||v=="null"){
				return;
			}
			$("#" + ipt_id).val(v);
		};

		this.getDiv = function(){
			return div_id;
		};
	};
	/*
	--------------------------------------
				Combo框体
	--------------------------------------
	*/
	$.ITCUI_Combo = function(name,items,div_id,multi){
		
		$(div_id).addClass("itcui_combo");
		var combo_html = "<span class='itcui_combo_text'>" + name + "</span><span class='itcui_combo_arrow_wrap'><b class='itcui_combo_arrow'></b></span>";
		$(div_id).html(combo_html);
		if(multi==true)
		{
			$(div_id).click(function(e){
				e.stopPropagation();
				$("#itcui_combo_dropdown").remove();
				$(".itcui_dropdown_menu").remove();
				var abs_pos = get_abs_position(div_id);
				var real_top = abs_pos.top + parseInt($(div_id).css("height")) + 2;
				var cb_width = parseInt($(div_id).css("width")) + 10;
				var cb_height = items.length *25+6;
				var menu_html = "<div id='itcui_combo_dropdown' class='itcui_dropdown_menu' style='position:absolute;width:" + cb_width + "px;height:" + cb_height + "px;top:" + real_top + "px;left:" + abs_pos.left + "px'>";
				for(var i=0;i<items.length;i++)
				{
					menu_html += "<div id='itcui_dropdown_item_" + i + "' class='itcui_dropdown_item' style='width:" + cb_width + ";height:25px;'>";
					menu_html += "<span class='itcui_chkbox chkbox_combo' id='chkbox_combo_" + i + "' style='margin-left:4px;float:left;margin-top:5px'></span>";
					menu_html += "<span class='itcui_dropdown_text' style='float:left'>" + items[i] + "</span>";
					menu_html += "</div>";
				}			
				menu_html += "</div>";
				$("body").append(menu_html);
				$("#itcui_combo_dropdown").hide();
				$("#itcui_combo_dropdown").slideDown();
				$(".chkbox_combo").click(function(){
					e.stopPropagation();
					var cls = $(this).attr("class");
					if(cls.indexOf("itcui_chkbox_checked")>0){
						$(this).removeClass("itcui_chkbox_checked");
					}
					else
					{
						$(this).addClass("itcui_chkbox_checked");;
					}
				});
				$(document).click(function(e){
					e.stopPropagation();
					$("#itcui_combo_dropdown").remove();
				});
			});
		}
	};

	/*
	--------------------------------------
               	多层下拉菜单            
	--------------------------------------
	*/
	$.ITCUI_DropDownMenu = function(arg){
		this.create_menu = function(menu,button,direction){
			$(button).click(function(e){
				e.stopPropagation();
				if ($(".itcui_dropdown_menu").length>0)
				{
					$(".itcui_dropdown_menu").remove();
					if(_current_menu==menu)
					{
						return;
					}
				}
				var abs_pos = get_abs_position(this);
				var real_top = abs_pos.top + parseInt($(this).css("height")) + 2;
				if(direction!="right")
				{
					create_sub_menu(abs_pos.left,real_top,menu,1);
				}
				else
				{
					var new_left = abs_pos.left - (parseInt(menu.width)-parseInt($(button).css("width")));
					create_sub_menu(new_left,real_top,menu,1);
				}
				$(document).bind("click",function(e){
					$(".itcui_dropdown_menu").remove();
				});
				_current_menu = menu;
				$(".itcui_dropdown_menu").hide();
				$(".itcui_dropdown_menu").slideDown();
			});
		};
		
		function create_sub_menu(pos_x,pos_y,menu,level)
		{
			var menu_height = 6;
			for(var i=0;i<menu.items.length;i++)
			{
				if(menu.items[i].name=="-")
				{
					menu_height += 16;
				}
				else
				{
					menu_height += 25;
				}
			}
			var menu_html = "<div id='itcui_dropdown_menu_" + level + "' class='itcui_dropdown_menu' style='width:" + menu.width + ";height:";
			menu_html += menu_height + "px;left:" + pos_x + "px;top:" + pos_y + "px'>";
			for(var i=0;i<menu.items.length;i++)
			{
				if(menu.items[i].name!="-")
				{
					menu_html += "<div id='itcui_dropdown_item_" + level + "_" + i + "' class='itcui_dropdown_item' style='width:" + menu.width + ";height:25px;'>";
					if(menu.items[i].icon)
					{
						menu_html += "<img style='margin-top:5px' class='itcui_dropdown_icon' src='" + menu.items[i].icon + "' />";
					}
					else
					{
						menu_html += "<span class='itcui_dropdown_icon'></span>";
					}
					menu_html += "<span class='itcui_dropdown_text'>" + menu.items[i].name + "</span>";
					if(menu.items[i].submenu)
					{
						menu_html += "<img src='images/tree_arrow_fold.png' style='float:right;margin-right:8px' />";
					}
					menu_html += "</div>";
				}
				else
				{
					menu_html += "<div class='itcui_dropdown_split' style='width:" + menu.width + "'></div>";
				}
			}
			menu_html += "</div>";
			$("body").append(menu_html);
			//在鼠标滑动时删除所有该级菜单以下的子菜单
			$(".itcui_dropdown_item").mouseover(function(){
				if(_last_mouse_over_menu==$(this).attr("id"))
				{
					return;
				}
				var this_id = $(this).parent().attr("id");
				var new_pos_y = get_abs_position("#" + $(this).attr("id")).top;
				var this_num = parseInt(this_id.substr(20));
				$("div[id^='itcui_dropdown_menu_']").each(function(){
					var sub_id = $(this).attr("id");
					var sub_num = parseInt(sub_id.substr(20));
					if(sub_num>this_num)
					{
						$(this).remove();
					}
				});
				var t = $(this).attr("id").split("_");
				var this_seq = t[t.length-1];
				if(menu.items[this_seq].submenu)
				{
					create_sub_menu(pos_x+parseInt(menu.width),new_pos_y,menu.items[this_seq].submenu,level+1);
				}
				_last_mouse_over_menu=$(this).attr("id");
			});
		}		
	};
	/*
	--------------------------------------
               	标签式列表            
	--------------------------------------
	*/
	$.ITCUI_TagList = function(div_id,opts){
		var items = {};	
		var added_items = {};//新添加的数据 用于增量修改
		var deleted_items = {};//被删除的数据 用于增量修改
		//添加按钮
		$(div_id).append("<div id='" + div_id.substr(1) + "_add_button' class='itcui_taglist_add_btn'>添加...</div>");
		
		$(div_id + "_add_button").click(function(){
			opts["add_action"]();
		});
		
		this.get_added_data = function(delimiter){
			return join_dict_keys(added_items,delimiter);
		};
		
		this.get_deleted_data = function(delimiter){
			return join_dict_keys(deleted_items,delimiter);
		};
		
		this.add_all = function(j){
			for(var i=0;i<j.length/2;i++){
				this.add(j[i*2], j[i*2+1]);
			}
		};
		
		this.add_all_kv = function(m){
			for(var k in m){
				this.add(k,m[k]);
			}
		};
		
		this.add = function(k,v){
			if(items[k]){
				return;
			}
			items[k] = v;
			var did = div_id.substr(1);
			if(v.indexOf("(继承)")>0){
				$(div_id).prepend("<div class='itcui_taglist_item'><span style='margin-left:10px'>" + v + "</span></div>");
			}
			else{
				$(div_id).prepend("<div class='itcui_taglist_item'><span style='margin-left:10px'>" + v + "</span><span class='itcui_taglist_del' id='del_cross_" + did + "_" + k + "' style='margin-left:6px;cursor:pointer'>x</span></div>");
			}			
			$("#del_cross_" + did + "_" + k).click(function(){
				var id = $(this).attr("id").replace("del_cross_" + did + "_","");
				delete items[id];
				$(this).parent().remove();
				deleted_items[id] = true;
				if(added_items[id]==true){
					delete added_items[id];
				}
			});
			added_items[k]=true;
			if(deleted_items[k]){
				delete deleted_items[k];
			}
		};
		
		this.get_items = function(){
			return items;
		};
		
		this.get_keys_str = function(delimiter){
			return join_dict_keys(items,delimiter);
		};
	};
	/*
	--------------------------------------
               	Datagrid
     opts选项：
     mapping -  列映射关系，array，每个元素格式为{"field":字段名,"fieldname":显示的字段名称,"fieldtype":字段类型,"width":宽度}
     head - 取null时无表头，注意mapping不能省略，否则grid无法知道表格宽度
     source - 数据源，返回值为json格式，具体返回格式为{"totalCount":M,"currPage":N,data:[]}，url形如/getdata.action?pg=<pg>&cnt=<cnt>，其中<pg>是显示的页面数，<cnt>为每页显示的结果数，自动替换
     data - 本地数据，直接赋值json对象
     selectable - 取true时每行出现多选框
     nextbtn - 下一页按钮对应的div，只要设置该选项会自动关联相关动作，使用的是圆形按钮
     prevbtn - 上一页按钮对应的div，使用的是圆形按钮
     fixwidth - 取true时将自动添加空白列以保证表格定宽（但是表格线不定宽）
     width - 表格宽度，默认取100%
     rowaction - 表单每行的动作，显示在每行最后，array，每个元素格式为{"name":"显示名称",action:单击时运行的函数,"id":函数标识符（必须，否则无法绑定动作）}
     listmode - 取true的时候将作为列表显示，此时将无法使用翻页功能，同时数据源也必须指定为本地json数据，但是可以使用添加行、删除行等功能
     idnum - 数据中id一项
	--------------------------------------
	*/
	$.ITCUI_Datagrid = function(div_id,opts){
		var options = opts;
		var t_width = opts["width"]?opts["width"]:"100%"; //表格宽度
		var naming_prefix = div_id.replace("#",""); //DIV前缀
		var action_onload_finish = null; //加载完成后执行的动作
		var action_onload_finish_arg = null;
		var curr_page = 0;
		var curr_row_num = null;
		var cnt_per_pg = 15;
		var total_rec_count = 0;
		var inited = false;
		var tmp_data = [];//本次加载所得到的临时数据
		var selected_data = {};//已经选择的数据
		var added_items = [];//新添加的数据 用于增量修改
		var deleted_items = {};//被删除的数据 用于增量修改
	
		this.bind_action = function(o,f,args){
			_bind_action(o,f,args);
		};
		
		this.onload_finish = function(f){
			action_onload_finish = f;
		};
		
		this.set_new_source = function(source){
			curr_page = 0;
			options["source"] = source;
		};
		
		this.set_page_size = function(s){
			cnt_per_pg = s;
		};
		//表格html追加完成后的后续动作
		function _do_finish(){
			if(action_onload_finish){
				action_onload_finish(action_onload_finish_arg);
			}
			//记录鼠标指向记录的行数
			$("[id^='" + naming_prefix + "_row_']").each(function(){
				$(this).mouseover(function(){
					var i = $(this).attr("id").replace(naming_prefix + "_row_","");
					curr_row_num = parseInt(i);
				});
			});
			//每行行首的选项框动作 这里注意需要限定div内的
			$(div_id + " .itcui_grid_linecheck").click(function(){
				var cls = $(this).attr("class");
				if(cls.indexOf("itcui_chkbox_checked")>0){
					$(this).removeClass("itcui_chkbox_checked");
					$(this).parent().parent("tr").removeClass("itcui_tr_selected");
					selected_data[curr_row_num] = false;
				}
				else
				{
					$(this).addClass("itcui_chkbox_checked");
					$(this).parent().parent("tr").addClass("itcui_tr_selected");
					selected_data[curr_row_num] = true;
				}
			});
			//每行最后的额外动作
			if(options["rowaction"]){
				$(".itcui_rowactions").click(function(){
					actid = parseInt($(this).attr("id").replace('itcui_rowaction_',""));
					act = options["rowaction"][actid];
					act["action"](_parse_args(act["arg"]));	
				});
			}
			_set_buttons();
		}
		
		//根据字符串获取表格数据作为参数（只能操作当前行）
		function _parse_args(args){
			if(typeof(args)=="string"){
				if(args.substr(0,1)=="#"){//选择第几列的元素
					var i = parseInt(args.substr(1));
					return tmp_data[curr_row_num][i];
				}
			}
			return args;
		}
		
		//绑定表格动作
		function _bind_action(o,f,args){
			if(o=="row_dblclick"){
				$("." + naming_prefix + "_row").dblclick(function(){
					f(_parse_args(args));
				});
			}
		}
		
		//生成单元格html
		function _gen_table_cell(data,type){
			if(data==null){
				data = "";
			}
			if(type=="checkbox"){
				if(data==true){
					return "<td><span class='itcui_chkbox itcui_chkbox_noctrl itcui_chkbox_checked'></span></td>";
				}
				else{
					return "<td><span class='itcui_chkbox itcui_chkbox_noctrl'></span></td>";
				}
			}
			else
			{
				return "<td>" + data + "</td>";
			}
		}
		
		function _gen_table_line(data,selected)
		{
			var l_html = "";
			if(options["selectable"]){
				if(selected==true){
					l_html += "<td><span class=\"itcui_grid_linecheck itcui_chkbox_noctrl itcui_chkbox itcui_chkbox_checked\"></span></td>";
				}
				else{
					l_html += "<td><span class=\"itcui_grid_linecheck itcui_chkbox_noctrl itcui_chkbox\"></span></td>";
				}
			}
			if(data.constructor === Array){//当数据为数组时 直接按照数组的顺序排布列内容
				for(var i=0;i<data.length;i++){
					if(!options["mapping"][i]["hide"]){
						l_html += _gen_table_cell(data[i],options["mapping"][i]["fieldtype"]);
					}
				}
			}
			else{
				for(var i=0; i < opts["mapping"].length;i++)
				{
					//当数据为json时，按照mapping的设定排布列内容
					m = options["mapping"][i];
					if(!m["hide"]){
						l_html += _gen_table_cell(data[m["field"]],m["fieldtype"]);
					}
				}
			}
			if(options["fixwidth"]||options["rowaction"]){
				if(!options["rowaction"]){
					l_html += "<td></td>";
				}
				else{
					l_html += "<td>";
					for(var i=0;i<options["rowaction"].length;i++){
						act = options["rowaction"][i];
						l_html += "[<a href='#' class='itcui_link itcui_rowactions' id='itcui_rowaction_" + i + "'>" + act["name"] + "</a>]";
					}
					l_html += "</td>";
				}
			}
			return l_html;
		}

		function _init()
		{
			if(inited==true){
				return;
			}
			if(options["nextbtn"]){
				$(options["nextbtn"]).click(function(e){
					if(!$(this).hasClass("itcui_btn_circle_disable")){
						curr_page += 1;
						eval(options["name"] + ".refresh()");
					}
				});
			}
			if(options["prevbtn"]){
				$(options["prevbtn"]).click(function(e){
					if(!$(this).hasClass("itcui_btn_circle_disable")){
						curr_page -= 1;
						eval(options["name"] + ".refresh()");
					}
				});
			}
			inited = true;
		}
		
		function _set_buttons()
		{
			if(options["prevbtn"]){
				if(curr_page>0){
					$(options["prevbtn"]).addClass("itcui_btn_circle");
					$(options["prevbtn"]).removeClass("itcui_btn_circle_disable");
					$(options["prevbtn"]).html("<span class=\"itcui_btn_circle_prev itcui_btn_circle_icon\"></span>");
				}
				else
				{
					$(options["prevbtn"]).addClass("itcui_btn_circle_disable");
					$(options["prevbtn"]).removeClass("itcui_btn_circle");
					$(options["prevbtn"]).html("<span class=\"itcui_btn_circle_prev_disable itcui_btn_circle_icon_disable\"></span>");
				}
			}
			if(options["nextbtn"]){
				if(curr_page+1<Math.ceil(total_rec_count/cnt_per_pg)){					
					$(options["nextbtn"]).addClass("itcui_btn_circle");
					$(options["nextbtn"]).removeClass("itcui_btn_circle_disable");
					$(options["nextbtn"]).html("<span class=\"itcui_btn_circle_next itcui_btn_circle_icon\"></span>");
				}
				else
				{
					$(options["nextbtn"]).addClass("itcui_btn_circle_disable");
					$(options["nextbtn"]).removeClass("itcui_btn_circle");
					$(options["nextbtn"]).html("<span class=\"itcui_btn_circle_next_disable itcui_btn_circle_icon_disable\"></span>");
				}
			}
		}
		
		this.refresh = function(){			
			_init();
			selected_data = {};//刷新时记得清空已选内容		
			var tbl_html = "<table class='itcui_table' width='"+ t_width + "' cellspacing='0'>";
			//表头部分
			if(opts["mapping"]&&!opts["nohead"])
			{
				tbl_html += "<tr style='height:24px'>";
				if(opts["selectable"]){
					tbl_html += "<th width=\"26\"><span class=\"itcui_chkbox itcui_chkbox_noctrl\"></span></th>";
				}
				for(var i=0; i < opts["mapping"].length;i++)
				{
					m = opts["mapping"][i];
					if(!m["hide"]){
						tbl_html += "<th width='" + m["width"] + "'>" + m["fieldname"] + "</th>";
					}
				}
				if(opts["fixwidth"]||options["rowaction"]){
					//固定宽度或者有行动作时在表每行后加一个空格
					tbl_html += "<th></th>";
				}
				tbl_html += "</tr>";
			}
			//表项部分
			if(opts["data"]){
				//直接读取数据
				for(var i=0;i<opts["data"]["data"].length;i++){
					var rid = opts["data"]["data"][i][0];
					var d = opts["data"]["data"][i];
					var selected = opts["data"]["initSelected"][rid];
					if(selected==true){
						selected_data[i] = true;
					}
					tbl_html += "<tr class='" + naming_prefix + "_row' id='" + naming_prefix + "_row_" + i + "'>" +  _gen_table_line(d,selected) + "</tr>";
				}
				if(added_items.length>0){
					for(var i=0;i<added_items.length;i++){
						var d = added_items[i];
						tbl_html += "<tr class='" + naming_prefix + "_row' id='" + naming_prefix + "_row_" + (i+opts["data"].length) + "'>" +  _gen_table_line(d) + "</tr>";
					}
				}
				tbl_html += "</table>";
				$(div_id).html(tbl_html);
				_do_finish();
			}
			else if(opts["source"]){
				//post方式获取数据
				var url = opts["source"]; 
				url = url.replace("<pg>",curr_page);
				url = url.replace("<cnt>",cnt_per_pg);
				//添加随机码防止IE缓存
				if(url.indexOf("?")>0){
					url += "&itc_randcode=" + Math.random();
				}
				else{
					url += "?itc_randcode=" + Math.random();
				}
				$.post(url,{},function(e){
					e = eval("(" + e + ")");
					if(e.totalCount){
						total_rec_count = e.totalCount;
					}
					if(e.data){
						tmp_data = e.data;
						for(var i=0;i<e.data.length;i++){
							var d = e.data[i];
							tbl_html += "<tr class='" + naming_prefix + "_row' id='" + naming_prefix + "_row_" + i + "'>" +  _gen_table_line(d) + "</tr>";
						}						
					}
					tbl_html += "</table>";
					$(div_id).html(tbl_html);
					_do_finish();
				});
			}
			
		};
		
		this.get_selected_count = function(){
			var n = 0;
			for(var k in selected_data){
				if(selected_data[k]==true){
					n++;
				}
			}
			return n;
		};
		this.get_added_data = function(){
			return added_items;
		};
		
		this.get_deleted_data = function(){
			return deleted_items;
		};

		this.get_selected_data = function(){
			return selected_data;
		};	
		
		this.get_selected_ids = function(id_col,display_col){
			var rtn = [];
			var data = options["data"]?options["data"]["data"]:tmp_data;
			for(var k=0;k<data.length;k++){
				if(selected_data[k]==true){
					rtn.push(data[k][id_col]);
					if(display_col){
						rtn.push(data[k][display_col]);
					}
				}
			}
			return rtn;
		};
		
		this.add_row = function(row){
			added_items.push(row);
		};
	};
})(jQuery);