var itcMvcJSObjectSequenceObj = new function() {
	var prefix = "_timssJsObject";
	var count = 0;
	var instanceMap = {};
	this.getOneInstance = function(clazz) {
		var id = "";
		var instanceName = "";
		var instance = null;
		while (true) {
			id = prefix + count++;
			instanceName = "timssFrameJsObj_instanceObj_" + id;
			eval(instanceName + " = new clazz()");
			instance = eval(instanceName);
			instanceMap[instanceName] = instance;
			break;
		}
		return {
			instance : instance,
			instanceName : instanceName,
			id : id
		};
	};
};

itcMvcJsFrameworkBase = function() {
	var instance = null;
	var instanceName = null;
	var id = null;

	this.privateInit = function(Obj) {
		init(Obj);
	};

	this.getInstance = function() {
		return instance;
	};

	this.instanceName = function() {
		return instanceName;
	};

	this.getid = function() {
		return id;
	};

	var init = function(Obj) {
		instance = Obj.instance;
		instanceName = Obj.instanceName;
		id = Obj.id;
	};
};

itcMvcJsFrameWorkServiceImpl = function() {
	itcMvcJsFrameworkBase.call();
	this.getEnumParams = function(list, callback) {
		var dourl = initPath("/enumParam.do");
		var json = null;
		if (isArray(list)) {
			json = "";
			for ( var i = 0; i < list.length; i++) {
				if (0 != i) {
					json += ",";
				}
				json += list[i];
			}
		} else if (typeof list == "string") {
			json = list;
		} else {
			return "argument type type is err";
		}
		$.post(dourl, {
			"data" : json
		}, function(data) {
			callback(data);
		}, "json");
	};

	this.getCurrentUserInfo = function(callback) {
		var dourl = initPath("/userInfo.do");
		$.post(dourl, function(data) {
			callback(data);
		}, "json");
	};
	
	this.getCurrentUserId = function(callback) {
		var dourl = initPath("/userId.do");
		$.post(dourl, function(data) {
			callback(data);
		}, "json");
	};
	
	this.getCurrentUserName = function(callback) {
		var dourl = initPath("/userName.do");
		$.post(dourl, function(data) {
			callback(data);
		}, "json");
	};

	this.getPath = function(path){
		return initPath(path);
	};
	
	var initPath = function(path) {
		return basePath + "framework/itcMvcService" + path;
	};
};
itcMvcJsFrameWorkServiceImpl.prototype = new itcMvcJsFrameworkBase();
itcMvcJsFrameWorkServiceImpl.build = function() {
	var initObj = itcMvcJSObjectSequenceObj.getOneInstance(this);
	initObj.instance.privateInit(initObj);
	return initObj.instance;
};

/**
 * Mvc组件前端接口
 */
var ItcMvcService = {
	/**
	 * 获取用户信息同步方法，从主框架获取数据
	 */
	getUser : function(){
		return window.parent._ItcMvc_ApplicationGlobal.userInfo;
	},
	user :{
		/**
		 * 同步获取用户ID
		 * @returns
		 */
		getUserId : function() {
			return window.parent._ItcMvc_ApplicationGlobal.userInfo.userId;
		},
		/**
		 * 同步获取用户名称
		 * @returns
		 */
		getUserName : function() {
			return window.parent._ItcMvc_ApplicationGlobal.userInfo.userName;
		}
	},
	/**
	 * 前端获取枚举变量的方法
	 * 
	 * @param list
	 *            传递枚举分组参数 可以是 Array、,号分割的字符按传、单个字符串 callback 查询数据后的回调
	 * @return 枚举参数js对象
	 */
	getEnum : function(list, callback) {
		return ItcMvcService.instance.getEnumParams(list, callback);
	},
	/**
	 * 前端获取枚举变量的后端路径
	 * 
	 * @return 路径
	 */
	getEnumPath : function(){
		return ItcMvcService.instance.getPath("/enumParam.do");
	},
	/**
	 * 获取当前登录用户的相关信息。
	 * 
	 * @param callback 异步返回数据回调
	 * @returns 用户的js对象
	 */
	getUserInfo : function(callback) {
		return ItcMvcService.instance.getCurrentUserInfo(callback);
	},
	UserInfo : {
		/**
		 * 获取用户ID
		 * @param callback 异步返回数据回调
		 * @returns
		 */
		getUserId : function(callback) {
			return ItcMvcService.instance.getCurrentUserId(callback);
		},
		/**
		 * 获取用户名称
		 * @param callback 异步返回数据回调
		 * @returns
		 */
		getUserName : function(callback) {
			return ItcMvcService.instance.getCurrentUserName(callback);
		}
	},
	getEnumParams : function(list, callback) {
		return ItcMvcService.instance.getEnumParams(list, callback);
	},
	instance : itcMvcJsFrameWorkServiceImpl.build()
};

function isArray(obj) {
	return Object.prototype.toString.call(obj) === '[object Array]';
}

$(document).ajaxSend(function(event,xhr,options){
	 if("POST"==options.type){
	 _token = "-1";
	 }
});
$(document).ajaxSuccess(function(event,xhr,options){
	 if("POST"==options.type){ 
	 var curToken = xhr.getResponseHeader("_token");
	 _token = curToken;
	 }
});
$(document).ajaxError(function(event,xhr,options,exc){
	 if("POST"==options.type){ 
	 var curToken = xhr.getResponseHeader("_token");
	 _token = curToken;
	 }
});