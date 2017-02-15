/*显示资产树*/
FW.joinKey = function(obj){
	var arr = [];
	for(var k in obj){
		arr.push(k);
	}
	return arr.join(",");
};

FW.joinVal = function(obj){
	var arr = [];
	for(var k in obj){
		arr.push(obj[k]);
	}
	return arr.join(",");
};

FW.showAssetDialog = function(opts){
	if(!opts){
		return;
	}
	opts.multiSelect = opts.multiSelect || false;
	opts.allowEdit = opts.allowEdit || false;
	opts.basePath = opts.basePath || window.basePath || "";
	opts.width = opts.width || "75%";
	opts.height = opts.height || "75%";
	var treeSrc = basePath + "page/asset/core/assetinfo/assetTree.jsp?embbed=1";
	var listSrc = basePath + "asset/location/locationList.do";
	if(opts.multiSelect){
		treeSrc += "&multi=1";
	}
	if(_parent().$("#itcDlgAssetTree").length==1){
		_parent().$("#itcDlgAsset").dialog("open");
		_parent().$("#itcDlgAssetTree").attr("src",treeSrc);
		_parent().$("#itcDlgAssetPage").attr("src",listSrc);
		return;
	}
	else{		
		var dlgHtml = '<div id="itcDlgAsset">' +
			'<div style="width:100%;height:100%;padding-left:240px;position:relative;overflow:hidden" class="bbox">' + 
			    '<iframe class="tree-iframe tree-border-right" frameborder="no" border="0" style="width:240px;height:100%;position:absolute;left:0px;top:0px;" id="itcDlgAssetTree" src="' + treeSrc +' ">' +
			    '</iframe>'+
			    '<iframe frameborder="no" border="0" style="width:100%;height:100%;" id="itcDlgAssetPage" src="' + listSrc + '">' +
			    '</iframe>' +
		    '</div>' + 
		'</div>' +
		'<div id="itcDlgAssetBtn" style="height:40px;display:none;padding-top:4px" class="bbox">' +
	    	'<div id="itcDlgAssetBtnWrap" style="width:100%;height:100%">' + 
	    	'</div>' +
		'</div>';
		_parent().$("body").append(dlgHtml);
	}
	var dlgOpts = {
		idSuffix : "Asset",
		width : opts.width,
		height : opts.height,
		title : opts.title || "选择设备"
	};
	var btnOpts = [{
            "name" : "取消",
            "onclick" : function(){
                return true;
            }
        },{
            "name" : "确定",
            "style" : "btn-success",
            "onclick" : function(){
                //itcDlgContent是对话框默认iframe的id
                var p = _parent().window.document.getElementById("itcDlgAssetTreeContent").contentWindow;
                var result = p.getSelected();
                if(!result){
                	FW.error("未选择资产");
                	return;
                }
                if(opts.idContainer){
                	$(opts.idContainer).val(FW.joinKey(result));
                }
                if(opts.nameContainer){
                	$(opts.nameContainer).val(FW.joinVal(result));
                }
                _parent().$("#itcDlgAsset").dialog("close");
            }
        }
    ];
	FW.dialog("init",{btnOpts:btnOpts,dlgOpts:dlgOpts});
};


FW.createAssetTree = function(opts){
	opts = opts || {};
	opts.basePath = opts.basePath || window.basePath || "";
	opts.multiSelect = opts.multiSelect || false;
	opts.bboxMode = opts.bboxMode  || true;
		
	var _body = $("body");
	if(!opts.bboxMode){
		_body.wrapInner("<div class='cbox' id='assetRightContainer' style='width:100%;height:100%'></div>");
	}
	var pl = parseInt(_body.css("padding-left"));
	_body.css({
		"padding-left":(pl + 230) + "px",
		height : "100%"
	}).addClass("bbox");
	
	var treeSrc = opts.basePath + "page/asset/core/assetinfo/assetTree.jsp?embbed=2";
	if(opts.multiSelect){
		treeSrc += "&multi=1";
	}
	treeSrc+="&forbidEdit="+opts.forbidEdit;
	var tfHtml = '<iframe frameborder="no" border="0" class="tree-iframe tree-border-right" style="width:230px;height:100%;position:absolute;left:0px;top:0px;" id="itcEmbbedAssetTree" src="' + treeSrc +' "></div>';
    _body.append(tfHtml);
    var mc = _parent().$("#mainframe_content");
    var mask = mc.children("#itc_mask");
    if(mask.length>0){
    	mc.css("left","230px");
    }
};

var _us_onselectfunc = null;
var _us_filterfunc = null;

FW.selectPersonByTree = function(opts){
	opts = opts || {};
	opts.basePath = opts.basePath || window.basePath || "";
	opts.src = opts.src || opts.basePath + "itcui/jsp/user_selector.jsp";
	opts.onSelect = opts.onSelect || function(){};
	_us_onselectfunc = opts.onSelect;
	opts.single = opts.single==false?false:true;
	opts.src += "?single=" + (opts.single?1:0);
	var dlgOpts = {
		idSuffix : "OrgPerson",
		width : opts.width,
		height : opts.height,
		title : opts.title || "选择人员"
	};
	var btnOpts = [{
        "name" : "取消",
        "onclick" : function(){
            return true;
        }
	    },{
	        "name" : "确定",
	        "style" : "btn-success",
	        "onclick" : function(){
	        	var p = _parent().window.document.getElementById("itcDlgOrgPersonContent").contentWindow;
	        	var result = p.getChecked();
	        	opts.onSelect(result);
	        	_parent().$("#itcDlgOrgPerson").dialog("close");
	        }
	    }    
	];
	 
	FW.dialog("init",{
		src : opts.src,
		btnOpts:btnOpts,
		dlgOpts:dlgOpts
	});
};