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
})(jQuery);

$(document).ready(function(){
	//不允许选中
	$(".itcui_btn_gray,.itcui_btn_green,.tab_item").each(function(){
		 $(this).attr('unselectable', 'on').css({
                   '-moz-user-select':'none',
                   '-webkit-user-select':'none',
                   'user-select':'none'
         });
	}).each(function(){
		this.onselectstart = function() { return false; };
	});
	//保证点击圆形按钮里面的东西也有反应
	$(".itcui_btn_circle_icon").mousedown(function(){
		$(this).parent().addClass("itcui_btn_circle_active");
	});
	$(".itcui_btn_circle_icon").mouseup(function(){
		$(this).parent().removeClass("itcui_btn_circle_active");
	});
	//单选框
	$(".itcui_chkbox").each(function(e){
		var o = $(this);
		if(!o.hasClass("itcui_chkbox_noctrl")){
			var cls = o.attr("class");
			if(cls.indexOf("itcui_chkbox_checked")>0){
				o.removeClass("itcui_chkbox_checked");
				o.parent().parent("tr").removeClass("itcui_tr_selected");
			}
			else
			{
				o.addClass("itcui_chkbox_checked");
				o.parent().parent("tr").addClass("itcui_tr_selected");
			}
		}
	});
		

	//小选项卡
	$(".tab_sm").click(function(){
		$(".tab_sm").removeClass("itcui_tab_small_active");
		$(".tab_sm").addClass("itcui_tab_small");
		$(".itcui_tab_small_text").removeClass("itcui_tab_small_text_active");
		$(this).removeClass("itcui_tab_small");
		$(this).addClass("itcui_tab_small_active");
		$(this).children(".itcui_tab_small_text").addClass("itcui_tab_small_text_active");
		//旁边的一个选项卡也要去除右边线
		$(this).prev().children(".itcui_tab_small_text").addClass("itcui_tab_small_text_active");
	});
});