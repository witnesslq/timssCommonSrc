var _Main = {
	showUpdateInfo : function() {
		var currTabId = FW.getCurrentTabId();
		var opts = {
			id : "showUpdateInfo",
			name : "版本信息",
			url : basePath + "page/mvc/core/updateInfor.html",
			tabOpt : {
				closeable : true,
				afterClose : "FW.deleteTab('$arg');FW.activeTabById('" + currTabId + "|homepage')"
			}
		};
		_parent()._ITC.addTabWithTree(opts);
	}
};

//修改密码
function showEditPass(){
	var src =  basePath + jspPath + "edit_pass.jsp?id=" + _ItcMvc_ApplicationGlobal.userInfo.userId;
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
            	var p = _parent().window.document.getElementById(_c.FRAME_DLG).contentWindow;
            	var form = p.go();
            	if(!form){
            		return false;
            	}
            	$.ajax({
            		url : basePath + "user?method=editpswd",
            		data : {
            			password : form["password"],
            			id : _ItcMvc_ApplicationGlobal.userInfo.userId
            		},
            		type : "POST",
            		dataType : "json",
            		success : function(data){
            			if(data.status==1){
            				FW.success("密码修改成功");
            			}
            			else{
            				FW.error(data.msg);
            			}
            			_parent().$("#itcDlg").dialog("close");
            		}
            	})
            	return true;
            }
        }
    ];
    var dlgOpts = {
        width : 450,
        height:authType == "ad"?230:200,
        closed : false,
        title:"修改密码",
        modal:true
    };
    Notice.dialog(src,dlgOpts,btnOpts);
}


$(document).ready(function(){
	$.ajax({
		type : "POST",
		url : basePath + "mvc/main/logMainInit.do",
		data : {
			msg : "initsuccess",
			useId : _ItcMvc_ApplicationGlobal.userInfo.userId
		},
		dataType : "json",
		success : function(data) {
			
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			
		}
	});
});

var checkIsFavorable = UiCommon.debounce(function(){
	var src = $(".ITCUI_Iframe_" + FW.getCurrentTabId()).attr("src");
	var i,rgx,item,title;
	if(!iRouteBeta.cachedPatterns){
		iRouteBeta.cachedPatterns = [];
		for(i=0;i<_route.length;i++){
			item = _route[i];
			item.rgx = new RegExp(item.pattern);
			iRouteBeta.cachedPatterns.push(item);
		}
	}
	var isFavorable = false;
	for(i=0;i<iRouteBeta.cachedPatterns.length;i++){
		item = iRouteBeta.cachedPatterns[i];
		rgx = item.rgx;
		if(rgx.test(src)){
			title = item.title ? ("“" + item.title + "”") : "当前页面";
			$("#link_add_fav").attr("data-id", item.routeId).html("收藏" + title).parent().removeClass("disabled");
			isFavorable = true;
			break;
		}
	}
	if(!isFavorable){
		$("#link_add_fav").html("当前页面无法收藏").removeAttr("data-id").parent().addClass("disabled");
	}
}, 300);

function popFavManagement(){
	FW.dialog("init", {
		src : basePath + jspPath + "fav_management.jsp",
		dlgOpts : {
			title:"管理收藏夹",
			width: "600",
			height: "400"
		},
		btnOpts: [{
			name : "取消",
			float : "right",
			style : "btn-default",
			onclick : function(){
				return true;
			}
		},{
			name : "确定",
			float : "right",
			style : "btn-success",
			onclick : function(){
				var win = _parent().window.document.getElementById("itcDlgContent").contentWindow;
				win.saveFav(function(xhr){
					if(xhr && xhr.status > 0){
						FW.success(xhr.msg);
						_parent().$("#itcDlg").dialog("close");
						_parent().updateFavMenu();
					}else{
						FW.error("更新收藏夹失败");
					}
				});
				//一直返回false，手动关闭对话框
				return false;
			}
		}],
	});
}

var tmplFavMenu = '<li class="divider fav-item"></li>\
{{each data as item}}\
<li><a href="javascript:iRouteBeta.go(\'{{item.routeId}}\')" class="fav-item">{{item.title}}</a></li>\
{{/each}}';
var cmplFavMenu = template.compile(tmplFavMenu);

function updateFavMenu(){
	$.ajax({
		url: basePath + "user?method=getfavroute&rand=" + Math.random(),
		method: "get",
		dataType: "json",
		success: function(xhr){
			if(!xhr || !xhr.data){
				return;
			}
			$("#menu_fav").find(".fav-item").remove();
			if(xhr.data.length){
				$("#menu_fav").append(cmplFavMenu(xhr));
			}
		}
	});
}

function registerTabEvent(){
	if(window.iRouteBeta && window._route){
		FW.registerEvent("onAfterTabSwitch", checkIsFavorable);
		FW.registerEvent("navTreeItemClick", checkIsFavorable);
	}
}

function addToFav(){
	var routeId = $("#link_add_fav").attr("data-id");
	if(!routeId){
		return;
	}
	$.ajax({
		url: "user?method=addfavroute",
		data: {
			routeId: routeId
		},
		method: "post",
		dataType: "json",
		success: function(xhr){
			if(xhr){
				if(xhr.status > 0){
					FW.success(xhr.msg);
					updateFavMenu();
				}else{
					FW.error(xhr.msg);
				}
			}else{
				FW.error("添加收藏失败，请稍后再试");
			}
		}
	})
}