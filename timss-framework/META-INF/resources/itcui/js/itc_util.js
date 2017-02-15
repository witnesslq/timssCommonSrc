/**
* ITC实用工具集
*/
var ITCUtil = ITCUtil || {};
ITCUtil._currMenu = null;

/**
	创建菜单的主入口函数
*/
ITCUtil.createMenu = function(target,menu,opts){
	opts = opts || {};
	opts.direction = opts.direction || "right";
	opts.menuAlign = opts.menuAlign || "left";
	opts.offsetFunc = opts.offsetFunc || function(target){
		return $(target).offset();
	};
	var me = $(target);
	//bootstrap模式
	var menuHtml = ITCUtil._createUlHtml(menu,opts);
	if(me.hasClass("dropdown") || me.find(".dropdown-toggle").length>0){
		me.append(menuHtml);
		ITCUtil._bindCheckEvent(target);
	}
	//经典模式
	else{
		if(!opts.id){
			return;
		}
		me.data("targetmenu",opts.id);
		me.data("offsetfunc",opts.offsetFunc);
		$("body").append(menuHtml);
		me.click(function(e){
			e.stopPropagation();
			var mid = "#" + $(this).data("targetmenu");
			var menu = $(mid);
			if(menu.css("display")=="none"){
				menu.css("display","block");
				var offset = $(this).data("offsetfunc")(this);
				menu.css({
					left : offset.left,
					top : offset.top + $(this).height()
				});
				ITCUtil._currMenu = mid;
			}
			else{
				menu.css("display","none");	
			}			
		});
		$("body").click(function(){
			$(ITCUtil._currMenu).hide();
		});
	}
};

ITCUtil.getMenuCheckVal = function(id){
	return $("#" + id).find(".dropdown-checked").length>0
};

ITCUtil.getMenuSelectVal = function(target,group){
	return $(target).find(".dropdown-selected[group='" + group + "']").parents("li").attr("id");
};

ITCUtil._bindCheckEvent = function(target){
	$(target).find(".dropdown-selected,.dropdown-unselected").parent("a").click(function(e){
		e.stopPropagation();
		var _this = $(this);
		_this.parents("ul").find(".dropdown-selected").removeClass("dropdown-selected");
		_this.find(".dropdown-unselected").addClass("dropdown-selected");
	});

	$(target).find(".dropdown-checked,.dropdown-unchecked").parent("a").click(function(e){
		$(this).find(".dropdown-unchecked").toggleClass("dropdown-checked");
	});
};

ITCUtil._createUlHtml = function(menu,opts){
	if(!isArray(menu)){
		return;
	}
	var idStr = opts.id?" id='" + opts.id + "'":"";
	var h = "<ul class='dropdown-menu' role='menu' " + idStr + ">";
	//遍历一圈看有没有需要加图标或者加勾勾的
	var enableCheck = false;
	var enableIcon = false;
	for(var i=0;i<menu.length;i++){
		var o = menu[i];
		if(o.select||o.check){
			enableCheck = true;
		}
		if(o.iconCls){
			enableIcon = true;
		}
	}
	for(var i=0;i<menu.length;i++){
		var o = menu[i];
		var oid = o.id?' id="' + o.id + '"':'';
		if(o.title=="-"){
			h += '<li class="divider"></li>';
			continue;
		}
		//这里注意pull-right不是乱用的
		var pdStr = opts.direction=="left"?"pull-left":"";
		h += o.submenu?'<li class="dropdown-submenu ' + pdStr + '"' + oid :'<li ' + oid;
		h += '>';
		var chkStr = "";
		if(enableCheck){
			if(o.select){
				var selStr = o.selected?"dropdown-unselected dropdown-selected":"dropdown-unselected";
				var grpStr = o.group?' group="' + o.group + '"':'';
				chkStr = '<span class="' + selStr + '" ' + grpStr + '></span>';				
			}
			else if(o.check){
				var selStr = o.checked?"dropdown-unchecked dropdown-checked":"dropdown-unchecked";
				chkStr = '<span class="' + selStr + '"></span>';
			}
			else{
				chkStr = '<span class="dropdown-holder"></span>';
			}
		}
		if(enableIcon){
			
		}
		var clkStr = o.onclick?'onclick="' + o.onclick + '"':'';
		h += '<a ' + clkStr + '>' + chkStr + o.title + '</a>';
		if(o.submenu){
			h += ITCUtil._createUlHtml(o.submenu,opts);
		}
		h += '</li>';
	}
	h += "</ul>";
	return h;
};

ITCUtil.createBtn = function(target,opts){
	if(!opts){
		return;
	}
	var btnHtm = ITCUtil._cBasebtnHtm(opts);
	if(target){
		$(target).addClass("btn-group-sm").html(btnHtm);
	}
	else{
		return btnHtm;
	}
};

ITCUtil._cBasebtnHtm = function(opts){
	opts.style = opts.style || "btn-default";
	var wStr = opts.width?"width:" + opts.width + "px;":"";
	wStr += opts.height?"height:" + opts.height + "px;":"";
	var tglStr = opts.toggle?' data-toggle="button"':'';
	var mnuTglStyle = (opts.menu && !opts.split)?'dropdown-toggle':'';
	var mnuTglStr = (opts.menu && !opts.split)?'data-toggle="dropdown"':'';
	var cartStr = (opts.menu && !opts.split)?'<span class="caret"></span>':'';
	var onclickStr = opts.onclick?' onclick="' + opts.onclick + '" ':'';
	return '<button onmouseup="javascript:this.blur();"" ' +  onclickStr + ' type="button" class="' + mnuTglStyle + ' btn ' + opts.style + '" style="' + wStr + '" ' + tglStr + mnuTglStr + '>'+
               opts.title + 
               cartStr + 
           '</button>';
};

ITCUtil.createBtnToolBar = function(target,opts){
	if(opts==null || !isArray(opts)){
		return;
	}
	var btnHtm = '<div class="btn-group btn-group-sm">';
	for(var i=0;i<opts.length;i++){
		if (privMapping && opts.privilege){
			if(!privMapping[opts.privilege]){
				continue;
			}
		}
		var opt = opts[i];
		if(opt["break"] && i>0){
			btnHtm += '</div>';
			btnHtm += '<div class="btn-group btn-group-sm">';
		}
		if(!opt.menu){
			btnHtm += ITCUtil._cBasebtnHtm(opt);
		}
		else{
			
			if(opt.split){				
				btnHtm += ITCUtil.createSpltMnuBtn(null,opt)				
			}
			else{
				btnHtm += '<div class="btn-group btn-group-sm">';
				btnHtm += ITCUtil.createMnuBtn(null,opt)	
				btnHtm += '</div>';
			}
			
		}
	}
	btnHtm += '</div>';
	var _target = $(target);
	_target.addClass("btn-toolbar").html(btnHtm);
	_target.children().each(function(){
		var _this = $(this);
		if(_this.children().length==1){
			_this.children().first().removeClass('btn-group');
		}
	});
};

ITCUtil.createSpltMnuBtn = function(target,opts){
	opts.split = true;
	var mOpts = opts.menu.length==1?{}:opts.menu[1];
	var btnHtm = ITCUtil._cBasebtnHtm(opts);
	btnHtm += '<div class="btn-group btn-group-sm">';
	btnHtm += '<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" style="height:28px;padding-left:6px;padding-right:6px">' +
		 	       '<span class="caret"></span>' + 
	     	   '</button>';
	btnHtm += ITCUtil._createUlHtml(opts.menu[0],mOpts);
	btnHtm += '</div>';
	if(target){
		var _target = $(target);
		if(_target.css("position")){
			_target.css({"position":"relative"});
		}
		_target.addClass("btn-group-sm btn-group").html(btnHtm);
	}
	else{
		return btnHtm;
	}
};

ITCUtil.createMnuBtn = function(target,opts){
	var mOpts = opts.menu.length==1?{}:opts.menu[1];
	var btnHtm = ITCUtil._cBasebtnHtm(opts) + ITCUtil._createUlHtml(opts.menu[0],mOpts);
	if(target){
		var _target = $(target);
		if(_target.css("position")){
			_target.css({"position":"relative"});
		}
		_target.addClass("btn-group-sm").html(btnHtm);
	}
	else{
		return btnHtm;

	}
};

/*-------------函数别名--------------*/
ITCUtil.addTab = _parent()._ITC.addTab;//添加选项卡
ITCUtil.deleteTab = _parent()._ITC.deleteTab;//删除选项卡
ITCUtil.initFrame = _parent()._ITC.init;//初始化框架
ITCUtil.switchDefaultTab = _parent()._ITC.switchDefaultTab;//切换到默认选项卡 
ITCUtil.adjustFrame = _parent()._ITC.adjustFrame;//重设框架大小
ITCUtil.navigate = _parent()._ITC.container.navigate;//在默认选项卡内加载页面
ITCUtil.activeTabById = _parent()._ITC.navTab.activeById;//根据id激活选项卡
ITCUtil.switchTreeItem = function(id){
	_parent()._ITC.navTree.ITCUI_NavTree("switchto",id);
};//激活导航树中的某项
ITCUtil.registerEvent = _event_handler.registerEvent;//注册事件
ITCUtil.unregisterEvent = _event_handler.unregisterEvent;//注销事件
ITCUtil.triggerEvent = _event_handler.triggerEvent;//触发事件
ITCUtil.confirm = Notice.confirm;//确认对话框
ITCUtil.input = Notice.input;//输入对话框
ITCUtil.dialog = Notice.dialog;//dialog封装
ITCUtil.successTopNotice = Notice.successTopNotice;//屏幕顶端显示成功信息
ITCUtil.errorTopNotice = Notice.errorTopNotice;//屏幕顶端显示失败信息
ITCUtil.getAbsPos = ITC_GetAbsPos;//获得某元素相对于body的绝对坐标
ITCUtil.isArray = isArray;//判断某原始是否为Array
ITCUtil.len = ITC_Len;//计算字符串的长度（中文按2个字符算）
ITCUtil.substr = ITC_Substr;//取字符串中的子串（中文按2个字符算）

