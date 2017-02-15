/*packaged at 2014-08-08 15:14:38*/
/*
File:D:\javalib\apache-tomcat-7.0.47\webapps\itc_uispec\trunk\prototype_ui\nightly\source\itcui\js\itc_core.js
*/
var privMapping = window.privMapping || _parent().privMapping || null;
var itcui = itcui || {};
itcui.combo_displayed = false;
function ITC_GetAbsPos(obj)
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

function _parent(){
	if(typeof(window)=="undefined"){
		FW.debug("window未定义，为什么？在IE下调调看，是不是又执行了已释放的代码");
		return null;
	}
	if(window._xssEmbbed || !window.parent || window.parent.document==document){
		return window;
	}
	else{
		return window.parent;
	}
}

function ITC_Len(str){
	var len = str.length;
	var reLen = 0; 
	    for (var i = 0; i < len; i++) {        
	        if (str.charCodeAt(i) < 27 || str.charCodeAt(i) > 126) { 
	            // 全角    
	            reLen += 2; 
	        } else { 
	            reLen++; 
	        } 
	    } 
	return reLen;  
}

function ITC_Substr(str, startp, endp) {
    var i=0; c = 0; unicode=0; rstr = '';
    var len = str.length;
    var sblen = ITC_Len(str);

    if (startp < 0) {
        startp = sblen + startp;
    }

    if (endp < 1) {
        endp = sblen + endp;// - ((str.charCodeAt(len-1) < 127) ? 1 : 2);
    }
    // 寻找起点
    for(i = 0; i < len; i++) {
        if (c >= startp) {
            break;
        }
	    var unicode = str.charCodeAt(i);
		if (unicode < 127) {
			c += 1;
		} else {
			c += 2;
		}
	}
	// 开始取
	for(i = i; i < len; i++) {
	    var unicode = str.charCodeAt(i);
		if (unicode < 127) {
			c += 1;
		} else {
			c += 2;
		}
		rstr += str.charAt(i);
		if (c >= endp) {
		    break;
		}
	}
	return rstr;
}

function isArray(o) {
  return Object.prototype.toString.call(o) === '[object Array]'; 
}

function priv(isDel){
	if(!privMapping){
		return;
	}
	$("div,span,button").each(function(){
		var _this = $(this);
		var priv = _this.attr("privilege"); 
		if(priv){
			if(!privMapping[priv]){
				if(!isDel){
					_this.css("display","none");
				}
				else{
					_this.remove();
				}
			}
		}
	});
}

Date.prototype.format =function(format){
	var o = {
	"M+" : this.getMonth()+1, //month
	"d+" : this.getDate(), //day
	"h+" : this.getHours(), //hour
	"m+" : this.getMinutes(), //minute
	"s+" : this.getSeconds(), //second
	"q+" : Math.floor((this.getMonth()+3)/3), //quarter
	"S" : this.getMilliseconds() //millisecond
	}
	if(/(y+)/.test(format)) format=format.replace(RegExp.$1,
	(this.getFullYear()+"").substr(4- RegExp.$1.length));
	for(var k in o)if(new RegExp("("+ k +")").test(format))
	format = format.replace(RegExp.$1,
	RegExp.$1.length==1? o[k] :
	("00"+ o[k]).substr((""+ o[k]).length));
	return format;
}
/*
File:D:\javalib\apache-tomcat-7.0.47\webapps\itc_uispec\trunk\prototype_ui\nightly\source\itcui\js\itc_163frame.js
*/
var privMapping = privMapping || null;
var _ITC = _ITC || _parent().window._ITC || {};
var TREE_HALF_WIDTH = 20;
var firstHideHint = true;
_ITC.navTab = _ITC.navTab || null;
_ITC.navTree = _ITC.navTree || null;
_ITC.container = _ITC.container || null;
_ITC.specTreeState = {};
_ITC.isTreeButtonShow = false;
_ITC.isButtonMouseOut = true;
_ITC.isTreeMouseOut = true;
_ITC.switchTab = function(id){
	_ITC.opts.tabSwitchFunc(id);
};

_ITC._switchTab = function(id,realId){	
	var opts = _ITC.opts;
	var prevTab = null;
	if(!opts.tabMapping){
		return;
	}
	var mapping = opts.tabMapping[id];
	if(!mapping){
		return;
	}
	//公用树情况
	if(mapping.tab){		
		_ITC._switchTab(mapping.tab,id);
		_ITC.useSameTree = mapping.tab;
		_ITC.container.switchTo(id,mapping.url,{cache:true,id:mapping.id});
		return;
	}
	else{
		_ITC.useSameTree = null;
	}
	//切换导航树时保存导航树的状态
	if(_ITC.navTree){
		if(!_ITC.currTab){
			_ITC.currTab = id;
		}
		else{
			prevTab = _ITC.currTab;
			_ITC.currTab = id;
			_ITC.tabTreeStat[prevTab] = _ITC.navTree.ITCUI_NavTree('getstate');
		}
	}
	//根据选项卡参数判断是否要显示导航树
	if(mapping.tree){
		_ITC.noTree = false;
		_ITC.navTree = $("#" + opts.treeId).ITCUI_NavTree("init",mapping.tree,{expandOnlyOne:true});
		$("#" + opts.treeId).click(function(){
			if($(this).hasClass("tree-dark")){
				_ITC.mannalHideTree();
			}
		});
		$("#" + _ITC.opts.treeId).show();
		$("#" + _ITC.opts.bottomId).addClass("tree-width-p");
	}
	else{
		_ITC.noTree = true;
		$("#" + _ITC.opts.treeId).hide();
		$("#" + _ITC.opts.bottomId).removeClass("tree-width-p");
	}	
	if(_ITC.initedTab[id]){
		//如果选项卡已经初始化直接切过去就可以了
		_ITC.container.switchTo(id,"",{"cache":true});		
	}	
	else{
		//找到树上一个有权限的节点
		var sOpt = {"cache":true};
		_ITC.initedTab[id] = true;
		if(opts.tabMapping[id].id){
			sOpt["id"] = opts.tabMapping[id].id;
		}
		if(mapping.tree){
			var flag = true;
			for(var i=0;i<mapping.tree.length;i++){
				if(!flag){
					break;
				}
				var node = mapping.tree[i];
				if(!privMapping || !node.privilege || privMapping[node.privilege]){
					if(!node.id && node.items){
						for(var j=0;j<node.items.length;j++){
							var subNode = node.items[j];
							if(!privMapping || !subNode.privilege || privMapping[subNode.privilege]){								
								_ITC.container.switchTo(id,opts.treeMapping[subNode.id],sOpt);
								_ITC.navTree.ITCUI_NavTree("highlight",subNode.id);
								flag = false;
								break;
							}
						}
					}
					else if(node.id){
						_ITC.container.switchTo(id,opts.treeMapping[node.id],sOpt);
						_ITC.navTree.ITCUI_NavTree("highlight",node.id);
						flag = false;
						break;
					}
				}
			}
		}
		else{
			if(mapping.url){
				_ITC.container.switchTo(id,mapping.url,sOpt);
			}
		}
	}
	_ITC.adjustFrame();
	if(_ITC.tabTreeStat[id]){
		_ITC.navTree.ITCUI_NavTree("loadstate",_ITC.tabTreeStat[id]);
	}
	//切换树时还需要保持半隐状态
	if(!_ITC.noTree){
		var tid = realId || id;
		if(_ITC.opts.treeDisplayState=="ALWAYS_OFF" || _ITC.specTreeState[tid]=="fold"){
			//$("#" + _ITC.opts.treeId).children(".itc_navtree").addClass("half-hide");
			_ITC.halfDisplayTree(true,true);
		}
		else{
			_ITC.halfDisplayTree(false,true);	
		}
	}	
	FW.triggerEvent("onAfterTabSwitch");
};

_ITC.toggleNavTree = function(state){
	_ITC.specTreeState[_ITC.navTab.getCurrentTab()] = state;
	if(state=="fold"){
		_ITC.halfDisplayTree(true);
	}
	else if(state=="expand"){
		_ITC.halfDisplayTree(false);
	}
}

_ITC.switchTreeItem = function(id){
	_ITC.opts.treeSwitchFunc(id)
};

_ITC._switchTreeItem = function(id){
	var opts = _ITC.opts;
	if(!opts.treeMapping){
		return;
	}
	if(_ITC.useSameTree){
		_ITC.navTab.activeById(_ITC.useSameTree);
		_ITC.useSameTree = null;
		_ITC.navTree.switchTo(id);		
		return;
	}
	_ITC.container.navigate(opts.treeMapping[id]);
};

_ITC.addTab = function(tabOpt,mappingOpt){
	var opts = _ITC.opts;
	if(tabOpt==null){
		return;
	}
	if(!tabOpt.id || !tabOpt.name){
		return;
	}
	mappingOpt = mappingOpt || {};
	mappingOpt.cache = mappingOpt.cache || false;
	if(!opts.tabMapping[tabOpt.id]){
		opts.tabMapping[tabOpt.id] = mappingOpt;
		_ITC.navTab.insert(tabOpt);
	}
};

_ITC.addTabWithTree = function(opts){
	var tabOpt = opts.tabOpt || {};
	var iopts = _ITC.opts;
	tabOpt.id = opts.id;
	tabOpt.name = opts.name;
	var mappingOpt = {
		tab : opts.tab || _ITC.navTab.getCurrentTab(),
		url : opts.url,
		id : opts.id
	};
	if(!iopts.tabMapping[opts.id]){
		iopts.tabMapping[opts.id] = mappingOpt;
		_ITC.navTab.insert(tabOpt);
		_ITC.navTab.activeById(opts.id);
	}
};

_ITC.deleteTab = function(id){
	var opts = _ITC.opts;
	delete(opts.tabMapping[id]);
	_ITC.container.remove(id);
	delete(_ITC.initedTab[id]);
	delete(_ITC.specTreeState[id]);
};

_ITC.init = function(opts){
	if(!_event_handler){
		FW.debug("没有找到_event_handler,ITC前端框架初始化失败");
		return;
	}
	opts = opts || {};
	opts.tabId = opts.tabId || 'itcui_nav_tab_container';
	opts.bottomId = opts.bottomId || 'mainframe_bottom';
	opts.contentId = opts.contentId || 'mainframe_content';
	opts.treeId = opts.treeId || 'mainframe_navtree';	
	$("#mainframe_bottom").append("<div class='tree-button-div tree-btn-left' id='btn_treefold' onclick='_ITC.mannalHideTree()'><div class='tree-hide-button'></div></div>");
	opts.tabs = opts.tabs || null;
	opts.tabMapping = opts.tabMapping || null;
	opts.tabSwitchFunc = opts.tabSwitchFunc || _ITC._switchTab;
	opts.treeWidth = $("#" + opts.treeId).width() + 1;
	opts.treeSwitchFunc = opts.treeSwitchFunc || _ITC._switchTreeItem;
	opts.treeDisplayState = "AUTO";//状态有ALWAYS_ON,ALWAYS_HALF,AUTO
	//树半隐动作
	_ITC.opts = opts;
	//用于状态的变量
	_ITC.noTree = false;
	_ITC.treeState = 1;
	_ITC.initedTab = {};
	_ITC.currTab = null;
	_ITC.tabTreeStat = {};
	//选项卡组初始化
	if(opts.tabs){
		_ITC.navTab = new ITCUI_Navigation("#" + opts.tabId,opts.tabs,{});
		_ITC.navTab.init();		
	}
	_ITC.container = new ITCUI_Container("#" + opts.contentId);
	_event_handler.registerEvent("tabSwitch", _ITC.switchTab);
	_event_handler.registerEvent("navTreeItemClick", _ITC.switchTreeItem);
	_ITC.adjustFrame();
	$(window).resize(function(){
		_ITC.adjustFrame();
	});
	//注册双树事件
	_ITC.sideFrameOpts = {};
	FW.registerEvent("onAfterTabSwitch",function(id){
		_ITC.ctrlSideFrame();
	});
	FW.registerEvent("navTreeItemClick",function(id){
		_ITC.ctrlSideFrame();
	});
	//树隐藏按钮悬停显示
	$("#mainframe_navtree").mouseover(function(e){
		e.stopPropagation();
		if(!_ITC.isTreeButtonShow){
			_ITC.isTreeButtonShow = true;
			$("#btn_treefold").show();			
		}
		_ITC.isTreeMouseOut = false;
	});	
	$("#btn_treefold").mouseover(function(e){
		e.stopPropagation();
		_ITC.isButtonMouseOut = false;
	});
	$("#mainframe_navtree").mouseout(function(){
		_ITC.isTreeMouseOut = true;
		_ITC.tryHideTreeButton();
	});
	$("#btn_treefold").mouseout(function(){
		_ITC.isButtonMouseOut = true;
		_ITC.tryHideTreeButton();
	});

};

_ITC.ctrlSideFrame = function(){
	$(".sideAppendFrame").hide();
	$("#mainframe_content").attr("class","");
	var id = _ITC.navTab.getCurrentTab();
    var tid = _ITC.navTree.ITCUI_NavTree("getactive");
	for(var k in _ITC.sideFrameOpts){
		var o = _ITC.sideFrameOpts[k];
		if(o && o.conditions && isArray(o.conditions)){
			for(var i=0;i<o.conditions.length;i++){
				var isTabOk = false;
				var isTreeOk = false;
				var cond = o.conditions[i];
				if(cond.tree){
					var reg = new RegExp(cond.tree);							
					if(reg.test(tid)){
						isTreeOk = true;
					}
				}
				else{
					isTreeOk = true;
				}
				if(cond.tab){
					var reg = new RegExp(cond.tab);							
					if(reg.test(id)){
						isTabOk = true;
					}
				}
				else{
					isTabOk = true;
				}
				if(isTreeOk && isTabOk){
					$("#" + o.id).show();
					$("#mainframe_content").addClass(o.containerCls);
					break;
				}
			}

		}
	}
};

var _hideHandle = null;
_ITC.tryHideTreeButton = function(){
	if(_ITC.isTreeButtonShow==false || $("#btn_treefold").hasClass("tree-btn-right")){
		return;
	}
	_hideHandle = setTimeout(function(){
		if(_ITC.isTreeMouseOut && _ITC.isButtonMouseOut){
			$("#btn_treefold").hide();
			_ITC.isTreeButtonShow = false;
		}	
	},150);
};

//切换到默认有权限的选项卡
_ITC.switchDefaultTab = function(){
	var opts = _ITC.opts;
	for(var i=0;i<opts.tabs.length;i++){
		var tabId = opts.tabs[i].id;
		if(!privMapping || privMapping[opts.tabMapping[tabId].privilege]){
			_ITC.navTab.activeById(tabId);
			return;
		}
	}
};

_ITC.mannalHideTree = function(){
	var state = $("#btn_treefold").hasClass("tree-btn-right");
	if(state){//显示
		_ITC.opts.treeDisplayState = "ALWAYS_ON";
		_ITC.halfDisplayTree(false,true);
	}
	else{
		_ITC.opts.treeDisplayState = "ALWAYS_OFF";	
		_ITC.halfDisplayTree(true,true);
	}
}

_ITC._halfDisplayTree = function(state){
	if(state){
		$("#btn_treefold").addClass("tree-btn-right").removeClass("tree-btn-left").attr("title","单击展开导航菜单").css("left","19px");
		$("#" + _ITC.opts.treeId).children(".itc_navtree").addClass("half-hide");
		$("#" + _ITC.opts.bottomId).addClass("tree-width-p").addClass("tree-half-p");
		$("#btn_treefold").show();
	}
	else{
		$("#btn_treefold").removeClass("tree-btn-right").addClass("tree-btn-left").attr("title","单击收起导航菜单").css("left","199px");;
		$("#" + _ITC.opts.treeId).children(".itc_navtree").removeClass("half-hide");
		$("#" + _ITC.opts.bottomId).addClass("tree-width-p").removeClass("tree-half-p");
	}
};

_ITC.halfDisplayTree = function(state,noAnimate){	
	if(_ITC.noTree){
		return;
	}
	/*if(!noAnimate){
		if(state){
			$("#" + _ITC.opts.treeId).animate({
				"width" : TREE_HALF_WIDTH + "px"
			},function(){
				_ITC._halfDisplayTree(state);
			});
		}
		else{
			$("#" + _ITC.opts.treeId).animate({
				"width" : _ITC.opts.treeWidth + "px"
			},function(){
				_ITC._halfDisplayTree(state);
			});
		}
	}
	else{*/
		if(state){
			$("#" + _ITC.opts.treeId).css({"width" : TREE_HALF_WIDTH + "px"});
			_ITC._halfDisplayTree(state);
		}
		else{
			$("#" + _ITC.opts.treeId).css({"width" : _ITC.opts.treeWidth + "px"});
			_ITC._halfDisplayTree(state);
		}
	/*}*/

};

var lastAdjustTime  = 0;
_ITC.adjustFrame = function(){
	var now = new Date().getTime();
	if(now - lastAdjustTime>200){
		lastAdjustTime = now;
	}
	else{
		return;
	}
	var sWidth = parseInt(document.documentElement.clientWidth);	
	//将导航树变为半收状态
	if(_ITC.opts.treeDisplayState=="AUTO"){
		//这里需要判断树当前状态 防止动画后的延迟函数执行带来的问题
		var tDsp = $("#itc_navtree").hasClass("half-hide");
		if(sWidth<1000){
			if(!tDsp){
				_ITC.halfDisplayTree(true);
				/*
				if(firstHideHint){				
					firstHideHint = false;
					$("<div class='half-hide-mask tree-bottom' id='hide-hint'><img src='nightly/images/hidehint.png' style='display:block;position:absolute;bottom:0px'/></div>").appendTo("body");
					$("#hide-hint").click(function(){
						$(this).remove();
					});
				}
				*/
			}
		}
		else{
			if(tDsp){
				_ITC.halfDisplayTree(false);
			}
		}
	}	
};

_ITC.addSideFrame = function(opts){
	if(!opts.src){
		FW.debug("参数src缺失，必须指定附加iframe的url地址");
		return;
	}
	if(!opts.conditions){
		FW.debug("参数conditions缺失，至少应该指定显示在哪些选项卡下，这是一个list");
		return;
	}
	opts.id = opts.id || _ITC.navTab.getCurrentTab();
	if(_ITC.sideFrameOpts[opts.id]){
		return;
	}
	opts.containerCls = opts.containerCls || "pleft230";
	opts.frameCss = opts.frameCss || {
		width : "230px",
        height : "100%",
        position : "absolute",
        display : "block",
        top : "0px",
        left : "0px"
	};
	_ITC.sideFrameOpts[opts.id] = opts;
	var mc = _parent().$("#" + _ITC.opts.contentId);
	mc.addClass(opts.containerCls);
	$("<iframe id='" + opts.id + "' frameborder='no' border='0' class='sideAppendFrame tree-border-right'></iframe>").prependTo(mc).css(opts.frameCss).attr("src",opts.src);
};

_ITC.showSkinPage = function(){
	var currTab = _ITC.navTab.getCurrentTab();
	var tabOpt = {
		name : "更换皮肤",
		id : "changeSkin",
		afterClose : function(id){
		    _ITC.deleteTab(id);
		    _ITC.navTab.activeById(currTab);
		},
		closeable : true
	};
	var mappingOpt = {
		url : skinPage
	};
	_ITC.addTab(tabOpt,mappingOpt);
	_ITC.navTab.activeById("changeSkin");
};
/*
File:D:\javalib\apache-tomcat-7.0.47\webapps\itc_uispec\trunk\prototype_ui\nightly\source\itcui\js\itc_container.js
*/
/*
	多iframe容器
*/
function ITCUI_Container(targetId){
	var _targetId = targetId;
	var frames = {};
	var currFrame = null;

	this.switchTo = function(id,url,options){
		//判断上一个iframe是移除还是缓存
		var lastFrame = $(_targetId).children(".ITCUI_Iframe_" + currFrame);
		if(frames[currFrame]){
			if(frames[currFrame].cache){
				lastFrame.css("display","none");
			}
			else{
				lastFrame.remove();
				delete(frames[currFrame]);
			}
		}
		currFrame = id;
		if(frames[id]&&frames[id].cache){
			$(_targetId).children(".ITCUI_Iframe_" + id).css("display","block");
		}
		else{
			var idStr = "";
			if(options.id){
				idStr = " id='" + options.id + "' ";
			}
			$("<iframe frameborder='no'" + idStr + " border='0' style='width:99.7%;height:100%;' class='ITCUI_ContainerFrame ITCUI_Iframe_" + id + "' src='about:blank'></iframe>").appendTo(targetId);
			frames[id] = options;
			/*此处有回流 怎么解决呢？*/
			$(targetId).children(".ITCUI_Iframe_" + id).attr("src",url);				
		}

	}

	this.navigate = function(url){
		if(currFrame!=null){
			FW.triggerEvent("onContainerNavigate")
			var frame = $(_targetId).children(".ITCUI_Iframe_" + currFrame);
			frame.attr("src","about:blank");
			frame.attr("src",url);
		}
	};

	this.resize = function(){
		/*
		var _parent = $(_targetId);
		_parent.children(".ITCUI_Iframe_" + currFrame).css({
			width : _parent.css("width"),
			height : _parent.css("height")
		});
		*/
	};

	this.remove = function(id){
		delete(frames[id]);
		$(_targetId).children(".ITCUI_Iframe_" + id).remove();
	};
}
/*
File:D:\javalib\apache-tomcat-7.0.47\webapps\itc_uispec\trunk\prototype_ui\nightly\source\itcui\js\itc_eventhandler.js
*/
ITCUI_EventHandler  = function(){
	var events = {};
	var invMapping = {};
	var data = {};
	var frameMapping = {};
	
	this.get = function(key){
		return data[key];
	}

	this.set = function(key,val,deepcopy){
		if(deepcopy){
			data[key] = JSON.parse(JSON.stringify(val));
		}
		else{
			data[key] = val;
		}
	}

	this.registerEvent = function(eventId,f,isFromTab){
		var evtList = events[eventId];
		if(!evtList){
			events[eventId] = [];
			evtList = events[eventId];
		}
		var evtNo = (new Date().getTime()%100000) + "" + Math.abs(Math.round(Math.random()*1000));
		evtList.push([f,evtNo])
		invMapping[evtNo] = eventId;
		if(isFromTab){
			var tabId = FW.getCurrentTabId();
			if(!frameMapping[tabId]){
				frameMapping[tabId] = [];
			}
			frameMapping[tabId].push(evtNo);
		}
		return evtNo;
	};

	this.unregisterEvent = function(evtNo){
		var eventId = invMapping[evtNo];
		if(!eventId){
			return;
		}
		var evtList = events[eventId];
		if(!evtList){
			return;
		}		
		var i = 0;	
		for(i=0;i<evtList.length;i++){
			var evt = evtList[i];
			if(evt[1]==evtNo){
				break;
			}
		}
		var newEvents = [];
		for(var j=0;j<evtList.length;j++){
			if(j!=i){
				newEvents.push(evtList[i]);
			}
		}
		evtList = newEvents;
		delete(invMapping[evtNo]);
	};

	this.triggerEvent = function(eventId,args){
		var evtList = events[eventId];
		var rtnList = [];
		if(evtList){
			for(var i=0;i<evtList.length;i++){
				var evt = evtList[i];
				if(evt && evt[0]){
					rtnList.push(evt[0](args));
				}
			}
		}
		if(rtnList.length>0){
			return rtnList;
		}
		return -1;
	};
}

var _event_handler = window._event_handler || _parent()._event_handler || new ITCUI_EventHandler();
/*
File:D:\javalib\apache-tomcat-7.0.47\webapps\itc_uispec\trunk\prototype_ui\nightly\source\itcui\js\itc_navtree.js
*/
(function($){
	$.fn.extend({
		/*
			导航树（只支持两层）
			data : [{"grouptitle":"组标题","items":[{"title":"项标题"}],"initexpand":true}]
			opts:
				expandOnlyOne - 每次只能展开一层
				width - 树宽度
				treeId - 树Id，如果需要在一个页面产生多个导航树需要修改这个
		*/
		ITCUI_NavTree:function(action,data,opts){
			var treeWidth = 200;
			var treeName = "itc_navtree";
			var _this = $(this);
			var _t = this;			
			var expandOnlyOne = true;


			_t.initTree = function(data,opts){
				$("#" + treeName).remove();
				var treeHtml = '<div class="itc_navtree" id="' + treeName + '">';
				for(var j=0;j<data.length;j++){
					treeGroup = data[j];
					var grpId = treeName + "_" + j;
					if(privMapping && _ITC && _ITC.opts && treeGroup.privilege){
						if(!privMapping[treeGroup.privilege]){
							continue;
						}
					}
					var grpTitle = treeGroup["grouptitle"] || treeGroup["title"];
					if(!treeGroup.items){
						treeHtml += '<div class="itc_navtree_nochildren" id="' + treeGroup.id + '">' + grpTitle + '</div>';
						continue;
					}
					var expStr = "";
					if(treeGroup["initexpand"]){
						expStr = " itc_navtree_grouptitle_expand";
					}
					treeHtml += '<div class="itc_navtree_grouptitle' + expStr + '" id="' + grpId + '"><div class="navtree-arrow"></div>' + grpTitle + '</div>';
					var subItemid = grpId + "_subitem";
					treeHtml += '<div id="' + subItemid + '" class="navtree_subitem"';
					if(!treeGroup["initexpand"]){
						treeHtml += ' style="display:none"';
					}
					else{
						_this.data("expandedNode", grpId);
					}
					treeHtml += '>';
					//tree sub item
					if(treeGroup["items"]){
						for(var i=0;i<treeGroup['items'].length;i++){
							var treeitem = treeGroup['items'][i];
							if(privMapping && _ITC && _ITC.opts){
								if(!privMapping[treeitem.privilege]){
									continue;
								}
							}
							var itemId = treeitem.id || treeName + "_item_" + j + "_" + i;
							treeHtml += '<div class="itc_navtree_item" id="' + itemId + '">';
							treeHtml += treeitem["title"] + "</div>";
						}
					}
					treeHtml += '</div>';//end of subitem
				}
				treeHtml += '</div>';//end of tree
				_this.append(treeHtml);
			};

			_t.canTreeSwitch = function(id){
				//判断导航树是否允许切换 当navTreeItemBeforeClick事件有一个返回false 将阻止切换
				if(_event_handler){
					var rtn = _event_handler.triggerEvent("navTreeItemBeforeClick",id);
					for(var i=0;i<rtn.length;i++){
						if(rtn[i]===false){
							return false;
						}
					}
				}
				return true;
			};

			_t.addEvents = function(data,opts){
				//鼠标点击时对子项高亮
				$("#" + treeName).find(".itc_navtree_item,.itc_navtree_nochildren").click(function(){
					var that = $(this);
					if(!_t.canTreeSwitch(this.id)){
						return;
					}
					that.parents(".itc_navtree").find(".itc_navtree_item_selected").removeClass("itc_navtree_item_selected");
					that.addClass("itc_navtree_item_selected");
					if(_event_handler){
						_event_handler.triggerEvent("navTreeItemClick",this.id);
					}
				});

				//树折叠效果
				$("#" + treeName + " .itc_navtree_grouptitle").click(function(){
					_t.expandNode(this);
				});
			};

			_t.expandNode = function(ptr){
				var id = $(ptr).attr("id");
				var box_id = id + "_subitem";
				var is_fold = $("#" + box_id).css("display")=="none"?true:false;
				var p = $(ptr).parents(".itc_navtree");
				//收起其他的选项卡
				if(expandOnlyOne){
					p.find(".itc_navtree_grouptitle_expand").each(function(){
						if(p.attr("id")!=id){
							$(this).removeClass("itc_navtree_grouptitle_expand");
							$(this).next(".navtree_subitem").hide();
						}
					});					
				}
				if(is_fold)
				{
					$("#" + box_id).slideDown();
					$(ptr).addClass("itc_navtree_grouptitle_expand");
				}
				else
				{
					$("#" + box_id).slideUp();
					$(ptr).removeClass("itc_navtree_grouptitle_expand");
				}
			};

			_t.switchTo = function(id){
				if(!_t.canTreeSwitch(id)){
					return;
				}
				_t.highlight(id);
				if(_event_handler){
					_event_handler.triggerEvent("navTreeItemClick",id);
				}
			};

			_t.highlight = function(id,noexpand){
				var tree = $("#" + treeName);
				tree.find(".itc_navtree_item_selected").removeClass("itc_navtree_item_selected");
				var cNode = tree.find("#" + id);
				cNode.addClass("itc_navtree_item_selected");
				//判断是否需要展开树
				if(!noexpand){
					var cNodeP = cNode.parent(); 
					if(cNodeP.hasClass("navtree_subitem")){
						var expNode = cNodeP.prev(".itc_navtree_grouptitle");
						if(!expNode.hasClass("itc_navtree_grouptitle_expand")){
							_t.expandNode(expNode);
						}
					}
				}
			};

			_t.getTreeState = function(){
				var states = {};
				var grpTitle = _this.find(".itc_navtree_grouptitle");
				for(var i=0;i<grpTitle.length;i++){
					var o = $(grpTitle[i]);
					states[o.attr("id")] = {"items" : {}};
					if(o.hasClass("itc_navtree_grouptitle_expand")){
						states[o.attr("id")]["expanded"]  = true;
						var subItems = o.next(".navtree_subitem").find(".itc_navtree_item");
						for(var j=0;j<subItems.length;j++){
							var oo = $(subItems[j]);
							if(oo.hasClass("itc_navtree_item_selected")){
								states[o.attr("id")]["items"][oo.attr("id")] = {"selected" : true};
							}
						}
					}					
				}
				var noTitle = _this.find(".itc_navtree_nochildren");
				for(var i=0;i<noTitle.length;i++){
					var o = $(noTitle[i]);
					if(o.hasClass("itc_navtree_item_selected")){
						states[o.attr("id")] = {"selected" : true};
					}
				}
				return states;
			};

			_t.loadTreeState = function(states){
				for(var k in states){
					var o = $("#" + k);
					if(states[k].expanded){
						o.addClass("itc_navtree_grouptitle_expand");
						o.next(".navtree_subitem").show();
					}
					else{
						o.removeClass("itc_navtree_grouptitle_expand");	
						o.next(".navtree_subitem").hide();
					}
					if(states[k].selected){
						o.addClass("itc_navtree_item_selected");
					}
					if(states[k].items){
						for(var kk in states[k].items){
							var o = states[k].items[kk];
							if(o.selected){
								$("#" + kk).addClass("itc_navtree_item_selected");
							}
						}
					}
				}
			};

			_t.getActiveItemId = function(){
				return _this.find(".itc_navtree_item_selected").attr("id");
			};

			if(action=="init"){
				if(opts){
					if(opts["width"] && opts["width"]>100){
						treeWidth = opts["width"];
					}
					if(opts["expandOnlyOne"]){
						expandOnlyOne = true;
					}
				}			
				this.initTree(data,opts);
				this.addEvents(data,opts);
			}
			else if(action=="highlight"){
				_t.highlight(data,opts);
			}
			else if(action=="switchto"){
				_t.switchTo(data);
			}
			else if(action=="getstate"){
				return _t.getTreeState();
			}
			else if(action=="loadstate"){
				_t.loadTreeState(data);
			}
			else if(action="getactive"){
				return _t.getActiveItemId();
			}
			return _this;
		}
	});
})(jQuery);
/*
File:D:\javalib\apache-tomcat-7.0.47\webapps\itc_uispec\trunk\prototype_ui\nightly\source\itcui\js\itc_navigation.js
*/
ITCUI_Navigation = function(renderTo,items,options){	
	var currTabCount = 0;
	this.handle = {};
	this._items = items;
	this._options = options || {};
	this.MAX_TAB = options.maxTab || 10;
	this._renderTo = renderTo;
	var _events = {};
	var _this = this;
	var hidTabs = [];
	
	
	this.init = function(){
		_handle = this.handle;
		_handle["container"] = $(this._renderTo).addClass("bbox").html("");
		_handle["navigation"] = $('<div></div>').addClass('itcui-navigation')
                .appendTo(_handle["container"]);
        _handle["navigation"].data("obj",this);
		_handle["ul"] = $('<ul></ul>').addClass('itcui-navigation-ul')
			.appendTo(_handle["navigation"]);
			
		_handle["separator"] = $('<div></div>').addClass('itcui-navigation-separator')
                .appendTo(_handle["navigation"]);
				
		var navHtml = "";
		for(var i=0;i<items.length;i++){			
			var item = items[i];
			//检查权限
			if(privMapping && _ITC && _ITC.opts){
				if(!privMapping[_ITC.opts.tabMapping[item.id].privilege]){
					continue;
				}
			}
			navHtml += this._makeTabHtml(item);
			if(item.id){
				_events[item.id] = {};
				if(item.click) _events[item.id]["click"] = item.click;
				if(item.beforeClose) _events[item.id]["beforeClose"] = item.beforeClose;
				if(item.afterClose) _events[item.id]["afterClose"] = item.afterClose;
				if(item.beforeClick) _events[item.id]["beforeClick"] = item.beforeClick;
			}
		}
		//配置菜单
		navHtml += '<li class="itcui-nav-menu-btn tA0 rC0 itcui-navigation-item" style="float:left">';
		navHtml += '<b></b></li>';
		menuHtml = '<div class="dropdown" style="position:absolute" id="itcui_nav_toggler_wrap">';
		menuHtml += '<a data-toggle="dropdown" id="itcui_nav_toggler"></a>';
		menuHtml += '<ul class="dropdown-menu" role="menu">';
		menuHtml += '</ul>';
		menuHtml += '</div>';		
		_handle["ul"].html(navHtml);
		$("body").append(menuHtml);
		this._bindEvent();
		_this.arrangeTab();
	};
	
	this._makeTabHtml = function(item){
		var tabHtml = "";
		if(!typeof(item)=="object"||!item.name){
			//参数必须是json对象 而且得有一个名字
			return "";
		}
		if(!item.front){
			item.front = "";
		}		
		if(item.id){
			tabHtml += "<li class='itcui-navigation-item tA0 oZ0' style='width:120px' id='itcui_nav_tab_" + item.id + "'>";	
		}
		else{
			tabHtml += "<li class='itcui-navigation-item tA0 oZ0' style='width:120px'>";		
		}
		tabHtml += "<span class='itcui-navigation-item-front'>" + item.front + "</span>";
		tabHtml += "<span class='itcui-navigation-item-name'>" + item.name + "</span>";
		
		tabHtml += "<span class='itcui-navigation-item-rear' style='display:inline'>";
		if(item.closeable){
			tabHtml += "<a class='itcui-navigation-item-close-button'><b>x</b></a>";
		}
		else{
			tabHtml += "<a class='itcui-navigation-item-close-button' style='display:none'><b>x</b></a>";
		}
		tabHtml += "</span>";
		
		tabHtml += "</li>";	
		currTabCount += 1;	
		return tabHtml;
	};
	
	this._bindEvent = function(){
		_handle = this.handle;
		_handle["container"].find(".itcui-navigation-item").each(function(e){
			_this._bindSingleEvent($(this));
		});
	};
	
	this._bindSingleEvent = function(obj){
		obj.on('mouseenter', function(e){
            if(!$(this).hasClass('itcui-navigation-item-selected')){
				$(this).removeClass('itcui-navigation-item').addClass('itcui-navigation-item-hover');
			}
        }).on('mouseleave', function(e){
			if(!$(this).hasClass('itcui-navigation-item-selected')){
				$(this).addClass('itcui-navigation-item');
			}
			$(this).removeClass('itcui-navigation-item-hover')
        }).on('click',function(e){
        	e.stopPropagation();
        	var _this = $(this);
        	var ptr = _this.parents(".itcui-navigation").data("obj");			
			if(_this.attr("id")){								
				var id = _this.attr("id").replace("itcui_nav_tab_","");
				ptr.activeById(id);
			}
			if(_this.hasClass("itcui-nav-menu-btn")){
				menuHtml = '<li targettab="_all"><a class="menuitem"><i class="itcui_close_tab"></i>关闭所有选项卡</a></li>';
				if(hidTabs.length>0){
					menuHtml += '<li class="divider"></li>';
					for(var i=0;i<hidTabs.length;i++){
						menuHtml += '<li targettab="' + hidTabs[i][0] + '"><a class="menuitem"><i class="itcui_menu_icon"></i>' + hidTabs[i][1] + '</a></li>';
					}
				}
				$("#itcui_nav_toggler_wrap ul").html(menuHtml);
				$("#itcui_nav_toggler_wrap").data("ptr",ptr);
				//选项卡管理菜单事件绑定
				$("#itcui_nav_toggler_wrap ul li").click(function(){
					var _this = $(this);
					var ptr = _this.parent().parent().data("ptr");
					var target = _this.attr("targettab");
					if(target=="_all"){
						ptr._closeAll();
					}
					else{
						ptr.activeById(target);
					}
				});
				var pos = _this.offset();
				$("#itcui_nav_toggler_wrap").css({
					"top":pos.top + 32,
					"left":pos.left + 3
				});
				$("#itcui_nav_toggler").dropdown("toggle");
			}
		});			
		obj.find('.itcui-navigation-item-close-button').on('click',function(e){
			e.stopPropagation();
		}).on('mouseover',function(e){
			$(this).addClass("itcui-navigation-item-close-button-hover");
		}).on('mouseleave',function(e){
			$(this).removeClass("itcui-navigation-item-close-button-hover");
		}).on('click',function(e){
			var _this = $(this);
			var id = _this.parent().parent().attr("id").replace("itcui_nav_tab_","");
			var ptr = _this.parents(".itcui-navigation");
			ptr.data("obj").removeById(id);
		});
	};

	//options中的position只支持after:id/before:id/first/last四个选项，默认为last
	this.insert = function(item,options){
		options = options || {};
		var position = options.position || "last";
		if(!item){
			return;
		}
		var tabHtml = this._makeTabHtml(item);
		if(item.id){
			_events[item.id] = {};
			if(item.click) _events[item.id]["click"] = item.click;
			if(item.beforeClose) _events[item.id]["beforeClose"] = item.beforeClose;
			if(item.afterClose) _events[item.id]["afterClose"] = item.afterClose;
			if(item.beforeClick) _events[item.id]["beforeClick"] = item.beforeClick;
		}
		if(position=="first"){
			_handle["ul"].prepend(tabHtml);
			_this._bindSingleEvent(_handle["ul"].find(".itcui-navigation-item").first());
		}
		else if(position=="last"){
			_this._bindSingleEvent(_handle["ul"].children(".itcui-nav-menu-btn").
				before(tabHtml).prev(".itcui-navigation-item"));
		}
		else if(/before/.test(position)){
			var targetid = "#itcui_nav_tab_" + position.replace("before:","");
			_this._bindSingleEvent(_handle["ul"].find(targetid).before(tabHtml).prev(".itcui-navigation-item"));
		}
		else if(/after/.test(position)){
			var targetid = "#itcui_nav_tab_" + position.replace("after:","");
			_this._bindSingleEvent(_handle["ul"].find(targetid).after(tabHtml).next(".itcui-navigation-item"));
		}
		_this.arrangeTab();
	};
	
	this.arrangeTab = function(){
		if(currTabCount<=_this.MAX_TAB){
			_handle["ul"].find(".itcui-navigation-item").first().css('margin-left',"0px");
			return;
		}
		var firstTab = _handle["ul"].find(".itcui-nav-menu-btn");
		var flag = true;
		var findCnt = 0;
		var findOneMore = false;
		hidTabs = [];
		while(flag){
			firstTab = firstTab.prev("li");
			if(firstTab.length>0){
				findCnt ++;
				//一共找N个tab 前N-1个可以随便找 但是找最后1个的时候一定要有一个已激活的
				if(findCnt<_this.MAX_TAB){
					if(firstTab.css("display")=="none"){
						firstTab.css({"display":"block"}).addClass("itcui-navigation-item");
					}
					if(firstTab.hasClass("itcui-navigation-item-selected")){
						//在前N-1个里已经有一个激活的 后面还可以显示一个
						findOneMore = true;
					}
				}
				else{
					if(findOneMore){
						if(firstTab.css("display")=="none"){
							firstTab.css({"display":"block"}).addClass("itcui-navigation-item");
						}	
						findOneMore = false;
					}
					else{
						if(!firstTab.hasClass("itcui-navigation-item-selected")){
							firstTab.css("display","none").removeClass("itcui-navigation-item");
							hidTabs.push([firstTab.attr("id").replace("itcui_nav_tab_",""),firstTab.children(".itcui-navigation-item-name").html()]);
						}
						else{
							firstTab.addClass("itcui-navigation-item").css("display","inline-block");
						}
					}
				}
			}
			else{
				flag = false;
			}
			firstTab.css('margin-left',"-1px");
		}
		_handle["ul"].find(".itcui-navigation-item").first().css('margin-left',"0px");
	};

	this.activeById = function(id){
		var tabId = "#itcui_nav_tab_" + id;
		if(!this._run(_events[id]["beforeClick"],id)){
			return;
		}
		var _this = $(this._renderTo).find(tabId);
		if(_this.length==0){
			return false;
		}
		if(_this.hasClass("itcui-nav-menu-btn")){
    		return;
    	}
		_this.parent().find("li").removeClass("itcui-navigation-item-selected");
		_this.addClass("itcui-navigation-item-selected").addClass("itcui-navigation-item");
		if(_event_handler){
			_event_handler.triggerEvent("tabSwitch",id);
		}
		var fun2 = _events[id]["click"];
		if(fun2){
			this._run(fun2,id);
		}
		if(hidTabs.length>0){
			this.arrangeTab();
		}
		return true;
	};
	
	this._run = function(fun,arg){
		if(!fun){
			return true;
		}
		if(typeof(fun)=="string"){
			eval(fun);
			return true;
		}
		else{
			return fun(arg);
		}
	};

	this.removeById = function(id){
		if(!this._run(_events[id].beforeClose,id)){
			return;
		}
		var tabId = "#itcui_nav_tab_" + id;
		var needActive = _handle["ul"].find(tabId).hasClass('itcui-navigation-item-selected');
		_handle["ul"].find(tabId).remove();
		//如果正在激活的选项卡被关闭 需要再激活一个 否则重拍选项卡会出错
		/*
		if(needActive){
			var tab = _handle.ul.find(".itcui-navigation-item ").first();
			var _id = tab.attr('id').replace("itcui_nav_tab_","");
			this.activeById(_id);
		}
		if(hidTabs.length>0){
			this.arrangeTab();
		}
		*/
		currTabCount -= 1;
		this._run(_events[id].afterClose,id);
	};

	this.updateById = function(item){
		if(!item || !item.id){
			return;
		}
		var tabId = "#itcui_nav_tab_" + item.id;
		if(item.name){
			_handle["ul"].find(tabId).find(".itcui-navigation-item-name").html(item.name);
		}
	};


	this.getCurrentTab =  function(){
		return _handle["ul"].children(".itcui-navigation-item-selected").attr("id").replace("itcui_nav_tab_","");
	};

	this._closeAll = function(){
		_handle["ul"].children("li").each(function(){
			var closeBtn = $(this).find(".itcui-navigation-item-close-button");
			if(closeBtn.length>0 && closeBtn.css("display")!="none"){
				var id = $(this).attr("id").replace("itcui_nav_tab_","");
				_this.removeById(id);
			}
		});
	};
}
/*
File:D:\javalib\apache-tomcat-7.0.47\webapps\itc_uispec\trunk\prototype_ui\nightly\source\itcui\js\itc_skinner.js
*/
function _itc_initskinner(){
	if($("#itc_skin").length>0){
		return;
	}
	var skin = '<footer class="skin">' + 
		'<div class="skin-item skin-top">' + 
		'    <div class="skin-top-inner"></div>' + 
		'    <div class="skin-top-inner2"></div>' + 
		'</div>' + 
		'<div class="skin-item skin-top-left"></div>' + 
		'<div class="skin-item skin-top-right"></div>' + 
		'<div class="skin-item skin-left" id="themeLeft"></div>' + 
		'<div class="skin-item skin-left-top" id="themeLeftTop"></div>' + 
		'<div class="skin-item skin-left-bottom" id="themeLeftBottom"></div>' + 
		'<div class="skin-item skin-right"></div>' + 
		'<div class="skin-item skin-right-top"></div>' + 
		'<div class="skin-item skin-right-bottom"></div>' + 
		'<div class="skin-item skin-bottom"></div>' + 
		'<div class="skin-item skin-fullScreen" id="themeFullScreen"></div>' + 
		'<div class="skin-imgProxy" id="themeImgProxy"></div>' + 
		'</footer>';
	$("body").prepend(skin);
}

function _itc_initskinlist(){
	
}

function _itc_selectskin(){

}