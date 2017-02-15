/*显示资产树*/
FW.showAssetDialog = function(opts){
	if(!opts){
		return;
	}
	opts.multiSelect = opts.multiSelect || false;
	opts.allowEdit = opts.allowEdit || false;
	opts.basePath = opts.basePath || window.basePath || "";
	opts.width = opts.width || "75%";
	opts.height = opts.height || "75%";
	if(_parent().$("#itcDlgAssetTree").length==1){
		_parent().$("#itcDlgAsset").dialog("show");
	}
	else{
		var treeSrc = basePath + "page/asset/core/assetinfo/assetTree.jsp";
		var listSrc = basePath + "asset/location/locationList.do";
		var dlgHtml = '<div id="itcDlgAsset">' +
			'<div style="width:100%;height:100%;padding-left:200px" class="bbox">' + 
			    '<iframe style="width:200px;height:100%;position:absolute;left:0px;top:0px;" id="itcDlgAssetTree">' +
			    '</iframe>'+
			    '<iframe style="width:100%;height:100%;" id="itcDlgAssetPage">' +
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
                var p = _parent().window.document.getElementById("itcDlgAssetTree").contentWindow;
                var result = p.getSelected();
            }
        }
    ];

};