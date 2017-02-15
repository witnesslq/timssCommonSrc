_audit.init = function(){
	_audit.add("DES005","文档头（doctype）检查","init",function(){
		if(document.doctype && document.documentElement.previousSibling){
			_audit.fatal("页面缺少文档头doctype，这会导致某些组件行为异常");
		}
	});
};

_audit.init();