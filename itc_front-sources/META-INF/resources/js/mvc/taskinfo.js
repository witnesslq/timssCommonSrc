var _TASK = {
	witeTimeLong : 200000,
	maxWaiteTimeLong : 3600000,
	lasttaskCount : 0,
	lastdraftCount: 0,
	waitCount : 0,
	refrashCount : function(){
		$.ajax({
			type : "GET",
			url : basePath + "homepage/Info/ProcessTaskCount.do",
			data : {},
			dataType : "json",
			success : function(data) {
				if ("undefined" != typeof data.flag) {
					if ('SUC' == data.flag) {
						if(data.count == _TASK.lasttaskCount){
							_TASK.witeTimeLong += _TASK.witeTimeLong;
							_TASK.witeTimeLong > _TASK.maxWaiteTimeLong ? _TASK.witeTimeLong = _TASK.maxWaiteTimeLong : false;
						}else{
							_TASK.lasttaskCount = data.count;
							_TASK.lastdraftCount = data.draftCount;
						}
						_ITC.updateTreeTag({"id":"homepage_waitmession","tagValue":data.count,"tagClass":"badge-warning"});
						_ITC.updateTreeTag({"id":"homepage_savemession","tagValue":data.draftCount,"tagClass":"badge-warning"});
					} else {
						
					}
				}
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				
			}
		});
	},
	polling : function(){
		if(null != _ITC && null != _ITC.navTree && null != _ITC.navTree.ITCUI_NavTree){
			_TASK.refrashCount();
			setTimeout(_TASK.polling, _TASK.witeTimeLong);
		}else{
			if(10 > _TASK.waitCount++){
				setTimeout(_TASK.polling, 500);
			}
		}
	}
};
$(document).ready(function(){
	_TASK.polling();
});