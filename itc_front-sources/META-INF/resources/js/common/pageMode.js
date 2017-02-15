/**
*	用来在页面模式间切换
*	默认是详情（view），可为空
*	可选：新建（add）编辑（edit）
**/
var PageMode={
	objs:{
		mode:"",
		pageName:"列表项",//required
		buttonDivId:{//这里操作的是button外面的div，如果直接操作button，需自行控制外层div
			toCreate:"",//新建
			create:"",//新建提交
			toEdit:"editButtonDiv",//编辑
			update:"updateButtonDiv",//编辑提交
			
			save:"saveButtonDiv",//暂存
			commit:"commitButtonDiv",//提交
			audit:"auditButtonDiv",//审批
			invalid:"invalidButtonDiv",//作废
			flow:"flowButtonDiv",//流程信息
			
			toDel:"deleteButtonDiv",//删除
			print:"printButtonDiv",//打印
			cancel:"cancelButtonDiv"//取消
		},
		withWorkFlow:false,//是否使用工作流
		withPrint:true,//是否使用工作流
		isCommited:false,//工作流使用，是否提交过了（是否启动了流程）
		isAudit:false,//工作流使用，是否能审批
		buttonShow:[],
		buttonHide:[],
		formId:"autoform"//required
	},
	
	init:function(initParams){
		if(initParams){
			$.extend(true,PageMode.objs,initParams);
		}else{
			FW.error("初始化信息缺失，请联系管理员！");
			return;
		}
	},
	
	changeTitle:function(type){
		var title=$(".inner-title");
		var name=PageMode.objs.pageName;
		if(type=="add"){
			title.html("新建"+name);
		}else if(type=="edit"){
			title.html("编辑"+name);
		}else{
			title.html(name+"详情");
		}
	},
	
	isMode:function(mode){
		return PageMode.objs.mode==mode;
	},
	
	changeMode:function(type){
		PageMode.objs.mode=type;
		var buttonForWorkFlow=["save","commit","audit","invalid","flow"];//工作流页面专有
		var buttonForNoWorkFlow=["toCreate","create","toEdit","update"];//非工作流页面专有
		if(type=="add"){
			PageMode.objs.buttonHide=["toDel","print"];
			PageMode.objs.buttonShow=["cancel"];
			if(PageMode.objs.withWorkFlow){
				PageMode.objs.buttonHide=PageMode.objs.buttonHide.concat(["audit","invalid"],buttonForNoWorkFlow);
				PageMode.objs.buttonShow=PageMode.objs.buttonShow.concat(["save","commit","flow"]);
			}else{
				PageMode.objs.buttonHide=PageMode.objs.buttonHide.concat(["toCreate","toEdit","update"],buttonForWorkFlow);
				PageMode.objs.buttonShow=PageMode.objs.buttonShow.concat(["create"]);
			}
			$("#"+PageMode.objs.formId).iForm("beginEdit");
		}else if(type=="edit"){
			PageMode.objs.buttonHide=[];
			PageMode.objs.buttonShow=["cancel"];
			if(PageMode.objs.withPrint){
				PageMode.objs.buttonShow.push("print");
			}else{
				PageMode.objs.buttonHide.push("print");
			}
			if(PageMode.objs.withWorkFlow){
				PageMode.objs.buttonHide=PageMode.objs.buttonHide.concat(["audit"],buttonForNoWorkFlow);
				PageMode.objs.buttonShow=PageMode.objs.buttonShow.concat(["save","commit","flow"]);
				if(PageMode.objs.isCommited){
					PageMode.objs.buttonHide=PageMode.objs.buttonHide.concat(["toDel"]);
					PageMode.objs.buttonShow=PageMode.objs.buttonShow.concat(["invalid"]);
				}else{
					PageMode.objs.buttonHide=PageMode.objs.buttonHide.concat(["invalid"]);
					PageMode.objs.buttonShow=PageMode.objs.buttonShow.concat(["toDel"]);
				}
			}else{
				PageMode.objs.buttonHide=PageMode.objs.buttonHide.concat(["toCreate","create","toEdit"],buttonForWorkFlow);
				PageMode.objs.buttonShow=PageMode.objs.buttonShow.concat(["update","toDel"]);
			}
			$("#"+PageMode.objs.formId).iForm("beginEdit");
		}else{
			PageMode.objs.buttonHide=["cancel","toDel"];
			PageMode.objs.buttonShow=[];
			if(PageMode.objs.withPrint){
				PageMode.objs.buttonShow.push("print");
			}else{
				PageMode.objs.buttonHide.push("print");
			}
			if(PageMode.objs.withWorkFlow){
				PageMode.objs.buttonHide=PageMode.objs.buttonHide.concat(["save","commit","invalid"],buttonForNoWorkFlow);
				PageMode.objs.buttonShow=PageMode.objs.buttonShow.concat(["flow"]);
				if(PageMode.objs.isAudit){
					PageMode.objs.buttonShow=PageMode.objs.buttonShow.concat(["audit"]);
				}else{
					PageMode.objs.buttonHide=PageMode.objs.buttonHide.concat(["audit"]);
				}
			}else{
				PageMode.objs.buttonHide=PageMode.objs.buttonHide.concat(["create","update"],buttonForWorkFlow);
				PageMode.objs.buttonShow=PageMode.objs.buttonShow.concat(["toCreate","toEdit"]);
			}
			$("#"+PageMode.objs.formId).iForm("endEdit");
		}
		PageMode.changeButton();
		PageMode.changeTitle(type);
	},
	
	changeButton:function(){
		$.each(PageMode.objs.buttonHide,function(name,value){
			var obj=$("#"+PageMode.objs.buttonDivId[value]);
			if(obj){obj.hide();}
		});
		$.each(PageMode.objs.buttonShow,function(name,value){
			var obj=$("#"+PageMode.objs.buttonDivId[value]);
			if(obj){obj.show();}
		});
	}
};