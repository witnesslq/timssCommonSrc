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
});