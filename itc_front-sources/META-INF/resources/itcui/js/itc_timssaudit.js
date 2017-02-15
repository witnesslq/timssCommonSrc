var _audit = {
	currId : null,
	currDesc : null,
	currTriggerOn : null,
	audits : [],
	auditEvents : {},
	initMsgs : [],
	eventMsgs : [],
	usedStrings : {},
	serverPath : _audit_getServerPath(),
	serverMode : false,
	errCount : 0,
	objectHistory : {},
	recOperation : true
};

function _audit_getServerPath(){
	if(window._auditServerPath){
		return window._auditServerPath;
	}
	if(window.basePath){
		var path = basePath;
		if(basePath.length = basePath.lastIndexOf("/") + 1){
			path = path.substring(0,path.length-1);			
		}	
		var l = path.lastIndexOf("/");
		path = path.substring(0,l);
		path += "/uiaudit/";
		return path;
	}
	else{
		p = window.location.href;
		p = p.replace("http://","");
		p = p.split("/");
		return "http://" + p[0] + "/uiaudit/";
	}
}

$(document).ready(function(){
	setTimeout("_audit.start()",3000);
	if(_audit.recOperation){
		//绑定按钮点击
		setTimeout(function(){
			$("button").click(function(){
				if(window.FW){
					FW.pushEvent("按钮点击",$(this).text(),"");
				}
			});
			$("input,textarea").change(function(){
				var id = "无标识符";
				var me = $(this);
				if(me.attr("id")){
					id = "id=" + me.attr("id");
				}
				else if(me.attr("name")){
					id = "name=" + me.attr("name");
				}
				if(window.FW){
					FW.pushEvent("输入文字",id,me.val());
				}
			});
		},1000);
	}
	//绑定全局500错误
	$(document).ajaxError(function(event, jqXHR, ajaxSettings, thrownError){
		if(jqXHR.status == 404){
			return;//不处理404错误
		}
		_audit.dump("jquery自动错误收集，错误代码=" + jqXHR.status);
	});
});

_audit.markObject = function(objId,event){
	if(!_audit.objectHistory[objId]){
		_audit.objectHistory[objId] = {};
	}
	if(_audit.objectHistory[objId][event]){
		return false;
	}
	_audit.objectHistory[objId][event] = true;
	return true;
};



_audit.add = function(id,desc,freq,func){
	var data = {
		id : id,
		desc : desc,
		func : func,
		freq : freq
	};
	if(freq.indexOf("event:")==0){
		var evt = freq.replace("event:","");
		if(!_audit.auditEvents[evt]){
			_audit.auditEvents[evt] = [];			
		}
		_audit.auditEvents[evt].push(data);
	}
	else{
		_audit.audits.push(data);
	}
};

_audit.start = function(){
	if(!window.FW){
		return;
	}
	if(!window._ITC){
		//不检查大框架页
		return;
	}
	FW.debug("自动审计脚本开始执行");
	//初始执行一次
	for(var i=0;i<_audit.audits.length;i++){
		var ad = _audit.audits[i];
		if(/init/.test(ad.freq)){
			_audit.currId = ad.id;
			_audit.currDesc = ad.desc;
			_audit.currTriggerOn = ad.freq;
			ad.func();
		}
	}
	//发送惯用文字的统计数据
	setTimeout(function(){
		if(_audit.serverMode){
			var url = window.location.href;
			var p = url.lastIndexOf("?");
			if(p>0){
				url = url.substring(0,p);
			}
			formdata = {
				url : url,
				data : _audit.usedStrings,
				module : _audit.getModule()
			}
			$.post(_audit.serverPath + "audit?method=insertUsedWords",{
				data : JSON.stringify(formdata)
			});
		}
	},2000);
};

_audit.testTextLength = function(text,size){
	$("<span id='audit-testlength' style='font-size:" + size + "px'>" + text + "</span>").appendTo("body");
	var len =  $("#audit-testlength").width();
	$("#audit-testlength").remove();
	return len;
};

_audit.fatal = function(msg){
	_audit.rec(msg,"fatal");
};

_audit.advice = function(msg){
	_audit.rec(msg,"advice");
};

_audit.error = function(msg){
	_audit.rec(msg,"error");
};

_audit.getModule = function(){
	var module = "unknown";
	var _url = window.location.href;
	var p = _url.lastIndexOf("?");
	if(p>0){
		_url = _url.substring(0,p);
	}
	var arr = _url.replace("http://","").split("/");
	if(arr.length>=2){
		module = arr[2];
	}
	return module;
};

_audit.dump = function(desc,stacktrace){
	var curr_user = _parent()._ItcMvc_ApplicationGlobal?_parent()._ItcMvc_ApplicationGlobal.userInfo.userId:"无法获取";
	var url = window.location.href;
	var stacktrace = (stacktrace && isArray(stacktrace))?stacktrace.join(";;") : "非脚本错误，无堆栈信息";
	$.ajax({
		url  : _audit.serverPath + "events?method=insertDump",
		data : {
			url : url,
			user : curr_user,
			desc : desc,
			event_trace : _timss_eventqueue.join(";;"),
			stack_trace : stacktrace
		},
		type : "POST",
		success : function(){
			FW.debug("Event trace dumped success");
		}
	})
};

/*
window.onerror = function(message, file, line, col, error){
	var trace = "浏览器不支持堆栈信息打印";
	if(error){
		trace = printStackTrace({e:error});
	}
	else if(arguments.callee.caller){
		trace = printStackTrace({e:arguments.callee.caller});	
	}
	_audit.dump("[错误收集]错误信息:" + message,trace);
};


*/
if(!window.dump){
	var dump = _audit.dump;
}

_audit.rec = function(msg,level){
	var msgs = "";
	msgs += "[" + level + "]";
	msgs += "[" + _audit.currId + "]" + msg;
	
	if(!_audit.serverMode){
		FW.debug(msgs);
	}

	if(_audit.serverMode){
		if(_audit.errCount>2){
			return;
		}
		$.ajax({
			url :_audit.serverPath + "audit?method=insertAuditResult",
			data : {
				"msg" : msg,
				"level" : level,
				"url" : window.location.href,
				"module" : _audit.getModule(),
				"trigger_on" : _audit.currTriggerOn,
				"rule_id" : _audit.currId
			},
			type : "POST",
			error : function(){
				_audit.errCount += 1;
			}
		});
	}
};

//记录惯用字符，比如按钮名
_audit.recStrings = function(type,str){
	if(!str){
		return;
	}
	str = $.trim(str);
	if(!_audit.usedStrings[type]){
		_audit.usedStrings[type] = {};
	}
	_audit.usedStrings[type][str] = true;
};

_audit.event = function(evt,arg){
	if(_audit.auditEvents[evt]){
		for(var i=0;i<_audit.auditEvents[evt].length;i++){
			var ad = _audit.auditEvents[evt][i];
			_audit.currId = ad.id;
			_audit.currDesc = ad.desc;
			_audit.currTriggerOn = ad.freq;
			ad.func(arg);
		}
	}
};

_audit.send = function(){
	
};
