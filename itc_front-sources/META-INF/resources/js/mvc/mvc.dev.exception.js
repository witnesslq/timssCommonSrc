//异常处理框架前端处理组件

$(document).ajaxError(function(event,xhr,options,exc){
	ExceptionProcess.process(xhr.status);
});

var ExceptionDualFunctionMap ={
	code520:{
		code:520,
		func:function(){
			var parentW = window;
			while(true){
				if(null !=  parentW && "undefined" != typeof parentW){
					var mvcGlobal = parentW._ItcMvc_ApplicationGlobal;
					if(null !=  parentW._ItcMvc_ApplicationGlobal && "undefined" != typeof parentW._ItcMvc_ApplicationGlobal){
						parentW._ItcMvc_ApplicationGlobal.reLoad();
	        	 		break;
        		 	}
        	 	}else{
        	 		break;
        	 	}
    		 	parentW =  parentW.parent;
    	 	}
		}
	},
	code521:null,
	code522:null
};

var ExceptionProcess = {
	config:{
		CodeStr:"code"
	},
	process : function(errorcode){
		var code = ExceptionProcess.config.CodeStr + errorcode;
		var map = ExceptionDualFunctionMap[code];
		if(null != map && "undefined" != typeof map){
			map.func();
		}
	}
}