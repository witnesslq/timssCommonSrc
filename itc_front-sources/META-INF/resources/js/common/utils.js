/**
 * 打开选项卡
 * @param pageId 选项卡id
 * @param pageName 选项卡名称
 * @param pageUrl 选项卡url
 * @param sourceTabId 来源选项卡id
 * @param reloadTableId 刷新列表id
 * @param beforeCloseFunc 关闭前的回调，列表刷新之后
 * @param afterCloseFunc 关闭后的回调
 */
function addTab( pageId, pageName, pageUrl,sourceTabId,reloadTableId,beforeCloseFunc,afterCloseFunc ){
	sourceTabId=sourceTabId||"homepage";
	var params={
        id : pageId ,
        url : pageUrl,
        name :pageName,
        tabOpt : {
            closeable : true,
            afterClose : "FW.deleteTab('$arg');FW.activeTabById('" + sourceTabId + "');"+(afterCloseFunc?("FW.getFrame('"+sourceTabId+"')."+afterCloseFunc+"();"):"")
        }
    };
	if(reloadTableId)
		params.tabOpt["beforeClose"]="FW.getFrame('"+sourceTabId+"').$('#" + reloadTableId + "').datagrid('reload');";
	if(beforeCloseFunc)
		params.tabOpt["beforeClose"]=(params.tabOpt["beforeClose"]||"")+"FW.getFrame('"+sourceTabId+"')."+beforeCloseFunc+"();";
	
    FW.addTabWithTree(params);
    FW.activeTabById(pageId);
}

//关闭Tab by huangliwei
function closeTab(){
	FW.deleteTabById(FW.getCurrentTabId());
}

/**
 * 用后缀截断字符串，默认为...
 * @param str
 * @param length 截断的长度
 * @param suffix 指定后缀
 * @returns {String}
 */
function substr(str,length,suffix){
	suffix=suffix||"...";
	if(str){
		var tmp=str.substr(0,length);
		if(tmp<str){
			str=tmp+suffix;
		}
	}else{
		str="";
	}
	return str;
}

var DataFormat={//数据格式化
		formatDoubleDays:function(value){//格式化天数的小数位
			var formatDayNumReg = /^-?\d+\.?\d{4,}$/;//如果有超过3位的小数，则四舍五入为3位小数
			return formatDayNumReg.test( value )?parseFloat(value).toFixed(3):value;
		}
	}