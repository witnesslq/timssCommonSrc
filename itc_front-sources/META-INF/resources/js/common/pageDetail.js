var PageDetail={//basePath框架中已有
		objs:{
			data:{
				formJson:"",//可用于校验数据变更
				attachJson:""
			},
			isBackListAble:false,//是否支持本页面返回列表，需要配置url.list，否则直接关闭本页面tab
			namePrefix:"列表项",
			url:{
				list:"",//返回列表的url
				query:"",//获取表单数据的url
				create:basePath+"insertBean.do",//required
				update:basePath+"updateBean.do",//required
				del:basePath+"delBean.do",//required
				invalid:basePath+"invalidBean.do",
				commit:basePath+"commit.do",
				save:basePath+"save.do",
				updateStatus:basePath+"updateStatus.do",//required
				setWFParams:basePath+"setWFParams.do"
			},
			mode:"view",//默认，可选view/create/edit
			form:{
				id:"form_baseinfo",//required
				areaId:"base_info",//required
				obj:null,//$("#"+PageDetail.objs.form.id)
				fields:[],//required
				idField:"",//required
				nameField:"",//required
				nameInUrlParam:"jsonData",//在url中带form数据的参数名，后台从中取数，动态表单要用formData
				nameInUrlParamWithWorkFlow:"formData",//在工作流url中带form数据的参数名，handler从中取数
				opts:{
					fixLabelWidth:true,
					validate:true,
					labelFixWidth:120
				},
				queryData:null,//执行query返回的数据，可用于afterLoadData
				beanId:"",//用url获取bean时使用
				bean:{},//同步初始化使用，如果没有这个，就要用异步初始化，需要赋值url.query、form.beanId、form.blankBean
				blankBean:{
					//示例代码
					/*siteid:Priv.secUser.siteId,
					userName:Priv.secUser.userName,
					deptName:Priv.secUser.orgs&&Priv.secUser.orgs.length>0?Priv.secUser.orgs[0].name:""*/
				}//用于新建时填充表单（无初始化数据时）
			},
			withWorkFlow:false,//是否有工作流
			workFlow:{
				obj:null,//工作流对象
				status:"",//工作流状态,required
				editStatus:["草稿","提交申请"],//进入编辑模式的工作流状态
				isApplicant:false,//是否申请人,required
				isAudit:false,//是否审批人,required
				isCommited:false,//是否提交过了（是否启动了流程）
				isSetWFParams:false,//是否自动给工作流设置参数
				taskId:"",//任务Id
				instanceId:"",//流程实例ID
	    		flowDiagram:"atd_?_training"//流程图
			},
			withAttach:false,//是否有附件
			attach:{//附件参数
				divId:"attachDiv",
				sessId:"",
				valKey:"",
				fields:[{
					id:"attachment",title:" ",type:"fileupload",wrapXsWidth:12,wrapMdWidth:12,options:{
					    "uploader" : basePath + "upload?method=uploadFile&jsessionid=",
					    "delFileUrl" : basePath + "upload?method=delFile&key=",
					    "downloadFileUrl" :  basePath + "upload?method=downloadFile",
					    "swf" : basePath + "itcui/js/uploadify.swf",
					    "fileSizeLimit":10*1024,
					    "delFileAfterPost" : true
					}
				}],
				options:{
					labelFixWidth : 1,
				    labelColon : false
				},
				isEndEdit:false,
				formId:"attachForm",
				url:basePath+"attendance/atdAttach/query.do",
				itemType:"training"//插入附件使用
			},
			withItem:false,//是否有详情项列表
			item:{//详情项参数
				divId:"itemDiv",
				formId:"itemForm",
				datagridId:"itemDatagrid",
				addItemBtnId:"addItemBtn",
				addItemBtnName:"添加人员",
				addItemToolbarId:"addItemToolbar",
				isSaved:true,
				isEndEdit:false,
				itemName:"培训人员",
				fields:[],//required
				idField:"",//required
				delField:"",//required
				blankItem:{}//required
			},
			withPrint:true,//是否有打印
			print:{
				url:"",//非空会覆盖id配置的url
				id:"PXSQ_001",
				format:"pdf"
			},
			withDF:false,//是否使用动态表单
			df:{//动态表单参数
				
			}
		},
		
		isMode:function(mode){
			return PageDetail.objs.mode==mode;
		},
		setMode:function(mode){
			PageDetail.objs.mode=mode;
		},
		init:function(initParams){
			if(initParams){
				$.extend(true,PageDetail.objs,initParams);
			}else{
				FW.error("初始化信息缺失，请联系管理员！");
				return;
			}

			var modeObjs={
				pageName:PageDetail.objs.namePrefix,
				formId:PageDetail.objs.form.id,
				withWorkFlow:PageDetail.objs.withWorkFlow,
				withPrint:PageDetail.objs.withPrint,
				buttonDivId:{
					print:"btnPrint",
					toDel:"btnDel",
					cancel:"btnBack",
					
					save:"btnSave",//暂存
					commit:"btnCommit",//提交
					audit:"btnAudit",//审批
					invalid:"btnInvalid",//作废
					flow:"btnShowFlow",//流程信息
					
					create:"btnCreate",
					toEdit:"btnEdit",
					update:"btnUpdate"
				}
			};
			if(PageDetail.objs.withWorkFlow){
				PageDetail.objs.workFlow.obj=new WorkFlow();
			}
			PageMode.init(modeObjs);
			
			$("#"+PageDetail.objs.form.areaId).ITCUI_Foldable();
			PageDetail.objs.form.obj=$("#"+PageDetail.objs.form.id);
			if(PageDetail.objs.withDF){//动态表单
				//合并动态表单获取数据
				FW.dynamicForm(FW.stringify(PageDetail.objs.form.fields),PageDetail.objs.form.opts,PageDetail.getDataForQueryDF(),PageDetail.objs.url.query);
				//动态表单已自动获取值，合并原来的数据保存下来
				PageDetail.objs.form.bean=$.extend({},PageDetail.objs.form.obj.iForm("getVal"),PageDetail.objs.form.bean);
				PageDetail.queryDFCallback();
			}else{
				PageDetail.objs.form.obj.ITC_Form(PageDetail.objs.form.opts,PageDetail.objs.form.fields); //主卡片信息
			}
			
			if(PageDetail.objs.withItem){
				$("#"+PageDetail.objs.item.divId).iFold("init");
			}
			if(PageDetail.objs.withAttach){
				$("#"+PageDetail.objs.attach.divId).iFold("init");
			}
			
			if(PageDetail.isMode("create")&&!$.isEmptyObject(PageDetail.objs.form.blankBean)){//如果新建且有blankBean，用blankBean覆盖
				PageDetail.objs.form.bean=PageDetail.objs.form.blankBean;
				PageDetail.loadData();
			}else if($.isEmptyObject(PageDetail.objs.form.bean)){
				PageDetail.queryBean();
			}else{
				PageDetail.loadData();
			}
		},
		
		toEdit:function(){
			PageDetail.setMode("edit");
			PageDetail.beforeChangeShow();
			PageDetail.changeShow();
			PageDetail.afterChangeShow();
			if(PageDetail.objs.withAttach){
				PageDetail.queryAttach();
			}
		},
		toDelete:function(){
			var title=FW.specialchars(substr(PageDetail.objs.form.obj.iForm("getVal",PageDetail.objs.form.nameField),30));
			Notice.confirm("确认删除|是否确定要删除"+PageDetail.objs.namePrefix+(title?("“"+title+
					"”"):"")+"？该操作无法撤销。",PageDetail.delBean);
		},
		toBack:function(){//返回的操作
			if(PageDetail.objs.mode=="view"){
				PageDetail.toList();
			}else if(PageDetail.objs.mode=="create"){
				PageDetail.toList();
			}else if(PageDetail.objs.mode=="edit"){
				PageDetail.objs["mode"]="view";
				PageDetail.loadData();
			}
		},
		toList:function(){
			if(PageDetail.objs.toBackListAble){
				FW.navigate(PageDetail.objs.url.list);
			}else{
				PageDetail.toClose();
			}
		},
		toClose:function(){
			closeTab();
		},
		toShowFlow:function(){
			if(PageDetail.isMode("create")||!PageDetail.objs.workFlow.isCommited){
				PageDetail.objs.workFlow.flowDiagram=(PageDetail.objs.workFlow.flowDiagram&&PageDetail.objs.form.bean.siteid)
					?(PageDetail.objs.workFlow.flowDiagram.replace("?", PageDetail.objs.form.bean.siteid.toLowerCase())):"";
				if(PageDetail.objs.workFlow.flowDiagram){
					PageDetail.objs.workFlow.obj.showDiagram(PageDetail.objs.workFlow.flowDiagram);
				}else{
					FW.error("没有流程图信息");
				}
			}else{
				PageDetail.objs.workFlow.obj.showAuditInfo(PageDetail.objs.workFlow.instanceId,"",PageDetail.objs.workFlow.isAudit,PageDetail.toAudit);
			}
		},
		toPrint:function(){
			if(PageDetail.isMode("create")){//新建模式没有打印按钮
				return;
			}
			var url = PageDetail.objs.print.url?PageDetail.objs.print.url:
								(fileExportPath + "preview?__report=report/TIMSS2_"+PageDetail.objs.form.bean.siteid+
										"_"+PageDetail.objs.print.id+".rptdesign&__format="+PageDetail.objs.print.format+
										"&"+PageDetail.objs.form.idField+"=" + PageDetail.objs.form.beanId+
										"&siteId="+PageDetail.objs.form.bean.siteid);
			//window.open(url);
			FW.dialog("init",{src: url,
				btnOpts:[{
					"name" : "关闭",
					"float" : "right",
					"style" : "btn-default",
					"onclick" : function(){
					 _parent().$("#itcDlg").dialog("close");
				}}],
				dlgOpts:{ width:800, height:650, closed:false, title:"打印"+PageDetail.objs.namePrefix, modal:true }
			});
		},
		toInvalid:function(){
			FW.confirm("确定作废本条数据吗？该操作无法恢复。", function() {
				PageDetail.invalidBean();
			});
		},
		toAudit:function(){
			PageDetail.audit();
		},
		toCommit:function(){
			if(PageDetail.objs.workFlow.instanceId){//非新建的提交则审批
				PageDetail.audit();
			}else{//新建和启动流程
				PageDetail.commit();
			}
		},
		toSave:function(){
			PageDetail.save();
		},
		
		beforeChangeShow:function(){
			
		},
		changeShow:function(){//切换模式
			if(PageDetail.objs.withAttach){
				PageDetail.objs.attach.isEndEdit=false;//默认附件可编辑
			}
			if(PageDetail.objs.withItem){
				PageDetail.objs.item.isEndEdit=false;//默认详情项可编辑
			}
			if(PageDetail.objs.withItem&&!PageDetail.objs.item.isSaved){//每次刷新前确定一遍数据修改
				PageDetail.toAcceptItem();
			}
			
			if(PageDetail.objs.mode=="view"){
				if(PageDetail.objs.withWorkFlow&&PageDetail.objs.workFlow.isApplicant
						&&$.inArray(PageDetail.objs.workFlow.status,PageDetail.objs.workFlow.editStatus)>-1){
					//工作流下申请人在可编辑状态
					PageMode.objs.isCommited=PageDetail.objs.workFlow.isCommited;
					PageMode.changeMode("edit");
				}else{
					if(PageDetail.objs.withWorkFlow){
						PageMode.objs.isAudit=PageDetail.objs.workFlow.isAudit;
					}
					PageMode.changeMode("view");
					if(PageDetail.objs.withItem){
						PageDetail.objs.item.isEndEdit=true;
					}
					if(PageDetail.objs.withAttach){
						PageDetail.objs.attach.isEndEdit=true;
					}
				}
			}else if(PageDetail.objs.mode=="create"){
				PageMode.changeMode("add");
				if(!PageDetail.objs.isBackListAble){
					$("#"+PageMode.objs.buttonDivId["cancel"]).hide();//不支持返回列表时，隐藏返回按钮
				}
			}else if(PageDetail.objs.mode=="edit"){
				PageMode.changeMode("edit");
			}
			
			if(PageDetail.objs.withItem){
				PageDetail.toEditItem(!PageDetail.objs.item.isEndEdit);
			}
			
			if(PageDetail.objs.withWorkFlow){//工作流里隐藏掉取消按钮
				$("#"+PageMode.objs.buttonDivId["cancel"]).hide();//不支持返回列表时，隐藏返回按钮
			}
			
			$(".btn-toolbar div.btn-group").show();
			FW.fixRoundButtons(".btn-toolbar");
		},
		afterChangeShow:function(){
			
		},
		
		beforeLoadData:function(){
			
		},
		afterLoadData:function(){
			
		},
		loadData:function(ignorePart){//加载数据，可指定忽略部分form/item/attach/workFlow/changeShow:true用做数据部分刷新
			PageDetail.beforeLoadData();
			
			if(!(ignorePart&&ignorePart.form)){//加载主表数据
				PageDetail.objs.form.obj.ITC_Form("loaddata",PageDetail.objs.form.bean);
				if(PageDetail.objs.form.bean[PageDetail.objs.form.idField]&&!PageDetail.objs.form.beanId){
					PageDetail.objs.form.beanId=PageDetail.objs.form.bean[PageDetail.objs.form.idField];
				}
				//把加载的数据保存起来，提交时校验变更
				PageDetail.objs.data.formJson=JSON.stringify(PageDetail.objs.form.obj.iForm("getVal"));
			}
			
			if(PageDetail.objs.withItem&&!(ignorePart&&ignorePart.item)){//加载子项数据
				PageDetail.setItem(PageDetail.objs.form.bean.itemList);
			}
			if(PageDetail.objs.withAttach&&!(ignorePart&&ignorePart.attach)){//加载附件
				if(PageDetail.isMode("create")){
					PageDetail.setAttach(null);
				}else{
					PageDetail.queryAttach();
				}
			}
			if(PageDetail.objs.withWorkFlow&&!(ignorePart&&ignorePart.workFlow)){//加载工作流
				//设置工作流的参数
				PageDetail.setPageWFParams();
			}
			
			if(!(ignorePart&&ignorePart.changeShow)){
				PageDetail.beforeChangeShow();
				PageDetail.changeShow();
				PageDetail.afterChangeShow();
			}
			
			PageDetail.afterLoadData();
		},
		checkDataChanged:function(){//校验数据是否发生变更，用于决定是否保存数据/设置工作流参数
			return PageDetail.checkFormDataChanged()||
				(PageDetail.objs.withItem&&PageDetail.checkItemDataChanged())||
				(PageDetail.objs.withAttach&&PageDetail.checkAttachDataChanged());
		},
		checkFormDataChanged:function(){//校验数据是否发生变更，用于决定是否保存数据/设置工作流参数
			return !(PageDetail.objs.data.formJson===JSON.stringify(PageDetail.objs.form.obj.iForm("getVal")));
		},
		checkItemDataChanged:function(){//校验数据是否发生变更，用于决定是否保存数据/设置工作流参数
			return !("{\"addRows\":\"[]\",\"delRows\":\"[]\",\"updateRows\":\"[]\"}"===JSON.stringify(PageDetail.getDataForSubmitItem(PageDetail.objs.item.isEndEdit)));
		},
		checkAttachDataChanged:function(){//校验数据是否发生变更，用于决定是否保存数据/设置工作流参数
			return !(PageDetail.objs.data.attachJson===JSON.stringify(PageDetail.getDataForSubmitAttach()));
		},
		
		getDataForQueryDF:function(){
			var params={"sheetId":PageDetail.objs.form.beanId,"formSwitch":"on"};
			params[PageDetail.objs.form.idField]=PageDetail.objs.form.beanId;
			return params;
		},
		queryDFCallback:function(){
			
		},
		getDataForQuery:function(){
			var params={};
			params[PageDetail.objs.form.idField]=PageDetail.objs.form.beanId;
			return params;
		},
		queryCallback:function(data){
			PageDetail.objs.form.queryData=data;
			PageDetail.objs.form.bean=data.bean;
			PageDetail.loadData();
		},
		queryBean:function(){//异步获取数据加载
			$.get(
				PageDetail.objs.url.query,
				PageDetail.getDataForQuery(),
				PageDetail.queryCallback,
				"json"
			);
		},
		
		toValid:function(){
			return PageDetail.objs.form.obj.valid()&&(PageDetail.objs.withItem?PageDetail.toValidItem():true);
		},
		toValidItem:function(){
			var rowDatas = $("#"+PageDetail.objs.item.datagridId).datagrid('getRows');
			if( rowDatas.length <= 0 ){
				FW.error( "请添加"+PageDetail.objs.item.itemName);
				return false;
			}else{
				return $("#"+PageDetail.objs.item.formId).valid();
			}
		},
		
		getDataForSubmit:function(isEndEditItem,isOnlyUpdateItem){
			var result={};
			
			var form={};
			if(PageDetail.objs.withWorkFlow){
				form[PageDetail.objs.form.nameInUrlParamWithWorkFlow]=JSON.stringify(PageDetail.objs.form.obj.iForm("getVal"));
			}else{
				form[PageDetail.objs.form.nameInUrlParam]=JSON.stringify(PageDetail.objs.form.obj.iForm("getVal"));
			}
			$.extend(result,form);
			
			if(PageDetail.objs.withItem){
				$.extend(result,PageDetail.getDataForSubmitItem(isEndEditItem,isOnlyUpdateItem));
			}
			if(PageDetail.objs.withAttach){
				$.extend(result,PageDetail.getDataForSubmitAttach());
			}
			return result;
		},
		
		getDataForCreate:function(){
			return PageDetail.getDataForSubmit();
		},
		createCallback:function(data){
			if(data.result=="success"){
				FW.success("新建"+PageDetail.objs.namePrefix+"成功");
				PageDetail.objs.form.bean=data.bean;
				PageDetail.objs["mode"]="view";
				PageDetail.loadData();
			}else{
				FW.error("新建"+PageDetail.objs.namePrefix+"失败，请稍后重试或联系管理员");
			}
			PageDetail.changeBtnsLoading("create", false);
			PageDetail.afterCreateCallback(data);
		},
		afterCreateCallback:function(data){
			
		},
		createBean:function(){
			if(!PageDetail.toValid()){
				//FW.error("提交的内容有错误的地方，请修改后重试");
				return;
			}
			PageDetail.changeBtnsLoading("create", true);
			$.post(
				PageDetail.objs.url.create,
				PageDetail.getDataForCreate(),
				PageDetail.createCallback,
				"json"
			);
		},
		
		getDataForUpdate:function(){
			return PageDetail.getDataForSubmit();
		},
		updateCallback:function(data){
			if(data.result=="success"){
				FW.success("更新"+PageDetail.objs.namePrefix+"成功");
				PageDetail.objs.form.bean=data.bean;
				PageDetail.toBack();
			}else{
				FW.error("更新"+PageDetail.objs.namePrefix+"失败，请稍后重试或联系管理员");
			}
			PageDetail.changeBtnsLoading("update", false);
			PageDetail.afterUpdateCallback(data);
		},
		afterUpdateCallback:function(data){
			
		},
		updateBean:function(){
			if(!PageDetail.toValid()){
				//FW.error("提交的内容有错误的地方，请修改后重试");
				return;
			}
			PageDetail.changeBtnsLoading("update", true);
			$.post(
				PageDetail.objs.url.update,
				PageDetail.getDataForUpdate(),
				PageDetail.updateCallback,
				"json"
			);
		},
		
		getDataForDel:function(){
			return PageDetail.getDataForSubmit();
		},
		delCallback:function(data){
			if(data.result=="success"){
				FW.success("删除"+PageDetail.objs.namePrefix+"成功");
				PageDetail.toList();
			}else{
				FW.error("删除"+PageDetail.objs.namePrefix+"失败，请稍后重试或联系管理员");
			}
			PageDetail.changeBtnsLoading("del", false);
		},
		delBean:function(){
			PageDetail.changeBtnsLoading("del", true);
			$.post(
				PageDetail.objs.url.del,
				PageDetail.getDataForDel(),
				PageDetail.delCallback,
				"json"
			);
		},
		
		getDataForInvalid:function(){
			return PageDetail.getDataForSubmit();
		},
		invalidCallback:function(data){
			if(data.result=="success"){
				FW.success("作废"+PageDetail.objs.namePrefix+"成功");
				PageDetail.toClose();
			}else{
				FW.error("作废"+PageDetail.objs.namePrefix+"失败，请稍后重试或联系管理员");
			}
			PageDetail.changeBtnsLoading("invalid", false);
		},
		invalidBean:function(){
			if(!PageDetail.toValid()){
				//FW.error("提交的内容有错误的地方，请修改后重试");
				return;
			}
			PageDetail.changeBtnsLoading("invalid", true);
			$.post(
				PageDetail.objs.url.invalid,
				PageDetail.getDataForInvalid(),
				PageDetail.invalidCallback,
				"json"
			);
		},
		
		getDataForUpdateStatus:function(isFirstCommit,status){
			var params={
				isFirstCommit:isFirstCommit,
				status:status
			};
			params[PageDetail.objs.form.idField]=PageDetail.objs.form.beanId;
			
			return params;
		},
		updateStatusCallback:function(data){
			PageDetail.afterUpdateStatusCallback(data);
			PageDetail.toClose();
		},
		afterUpdateStatusCallback:function(data){
			
		},
		updateStatus:function(isFirstCommit,status){
			$.post(
				PageDetail.objs.url.updateStatus,
				PageDetail.getDataForUpdateStatus(isFirstCommit,status),
				PageDetail.updateStatusCallback,
				"json"
			);
		},
		
		setPageWFParams:function(){//设置页面的工作流参数
			//设置工作流的参数
			$.extend(true,PageDetail.objs.workFlow,{
				status:PageDetail.objs.form.bean.status,//工作流状态,required
				isApplicant:PageDetail.objs.form.bean.createuser==Priv.secUser.userId,//是否申请人,required
				isAudit:PageDetail.objs.form.bean.isAudit,//是否审批人,required
				isCommited:PageDetail.objs.form.bean.status!="草稿",//是否提交过了（是否启动了流程）
				taskId:PageDetail.objs.form.bean.taskId,//任务Id
	    		instanceId:PageDetail.objs.form.bean.instanceId//流程实例ID
			});
		},
		getDataForSetWFParams:function(){
			var paramByName={};
			var paramByCode={};
			$.each(PageDetail.objs.form.obj.data("fieldObjs"),function(key,obj){
				paramByCode[key]=obj.getVal();
				paramByName[obj.fieldOption.title]=paramByCode[key];
			});
			return {
				instanceId:PageDetail.objs.workFlow.instanceId,
				wfParams:JSON.stringify({
					paramByName:paramByName,
					paramByCode:paramByCode
				})
			};
		},
		setWFParams:function(successFunc,failFunc){//设置工作流的参数
			if(PageDetail.objs.workFlow.isSetWFParams){
				$.post(
					PageDetail.objs.url.setWFParams,
					PageDetail.getDataForSetWFParams(),
					function(data){
						if(data.result=="success"){
							if(successFunc&&typeof(successFunc)=="function"){
								successFunc(data);
							}
						}else{
							FW.error("设置"+PageDetail.objs.namePrefix+"工作流参数失败，请稍后重试或联系管理员");
							if(failFunc&&typeof(failFunc)=="function"){
								failFunc(data);
							}
						}
					},
					"json"
				);
			}else{
				if(successFunc&&typeof(successFunc)=="function"){
					successFunc();
				}
			}
		},
		
		getDataForAudit:function(){
			return JSON.stringify(PageDetail.getDataForSubmit(PageDetail.objs.item.isEndEdit));
		},
		audit:function(){
			if(!PageDetail.toValid()){
				//FW.error("提交的内容有错误的地方，请修改后重试");
				return;
			}
			PageDetail.changeBtnsLoading("audit,commit", true);
			if(PageDetail.checkDataChanged()){//数据有变动
				$.post(//先保存一下数据
					PageDetail.objs.url.update,
					PageDetail.getDataForSubmit(PageDetail.objs.item.isEndEdit),
					function(data){
						if(data.result=="success"){
							//FW.success("更新"+PageDetail.objs.namePrefix+"成功");
							//update的返回值不完整，因此不刷新bean
							//设置工作流参数
							PageDetail.setWFParams(PageDetail.showAudit,function(data){
								PageDetail.changeBtnsLoading("audit,commit", false);
							});
						}else{
							FW.error("更新"+PageDetail.objs.namePrefix+"失败，请稍后重试或联系管理员");
							PageDetail.changeBtnsLoading("audit,commit", false);
						}
					},
					"json"
				);
			}else{
				PageDetail.showAudit();
			}
		},
		showAudit:function(){
			PageDetail.objs.workFlow.obj.showAudit(PageDetail.objs.workFlow.taskId,PageDetail.getDataForAudit(),PageDetail.auditAgreeCallback,PageDetail.auditRollbackCallback,PageDetail.auditStopCallback,"",0,PageDetail.closeAuditCallback);
		},
		auditAgreeCallback:function(data){
			PageDetail.updateStatus("N",data.taskName);
		},
		auditRollbackCallback:function(data){
			PageDetail.updateStatus("N",data.taskName);
		},
		auditStopCallback:function(data){
			PageDetail.updateStatus("N","终止");
		},
		closeAuditCallback:function(){
			if(PageDetail.objs.withItem&&!PageDetail.objs.item.isSaved){//每次刷新前确定一遍数据修改
				PageDetail.toAcceptItem();
				PageDetail.toEditItem(!PageDetail.objs.item.isEndEdit);
			}
			PageDetail.changeBtnsLoading("audit,commit", false);
		},

		getDataForSubmitApply:function(data){
			return JSON.stringify({
				notSave:"Y",//因为前面保存过了，这是标识无需再保存
				instanceId:data.bean.instanceId
			});
		},
		getDataForCommit:function(){
			return PageDetail.getDataForSubmit();
		},
		commitAgreeCallback:function(data){
			PageDetail.updateStatus("Y",data.taskName);
		},
		commitCancelCallback:function(data){
			PageDetail.toClose();
		},
		commitCallback:function(data){
			if(data.result=="success"){
				FW.success("提交"+PageDetail.objs.namePrefix+"成功");
				PageDetail.objs.form.bean=data.bean;
				PageDetail.loadData({attach:true});//不刷新附件
				var taskId = data.bean.taskId;
				if(taskId){
					PageDetail.setWFParams(function(){//设置工作流参数
						PageDetail.objs.workFlow.obj.submitApply(taskId,PageDetail.getDataForSubmitApply(data),PageDetail.commitAgreeCallback,PageDetail.commitCancelCallback,0,PageDetail.closeCommitCallback);
					},function(){
						PageDetail.changeBtnsLoading("commit", false);
					});
				}
			}else{
				FW.error("提交"+PageDetail.objs.namePrefix+"失败，请稍后重试或联系管理员");
				PageDetail.changeBtnsLoading("commit", false);
			}
		},
		commit:function(){
			if(!PageDetail.toValid()){
				//FW.error("提交的内容有错误的地方，请修改后重试");
				return;
			}
			PageDetail.changeBtnsLoading("commit", true);
			$.post(
				PageDetail.objs.url.commit,
				PageDetail.getDataForCommit(),
				PageDetail.commitCallback,
				"json"
			);
		},
		closeCommitCallback:function(){
			PageDetail.changeBtnsLoading("commit", false);
		},
		
		getDataForSave:function(){
			return PageDetail.getDataForSubmit();
		},
		saveCallback:function(data){
			if(data.result=="success"){
				FW.success("暂存"+PageDetail.objs.namePrefix+"成功");
				PageDetail.objs.form.bean=data.bean;
				PageDetail.objs["mode"]="edit";
				PageDetail.loadData();
			}else{
				FW.error("暂存"+PageDetail.objs.namePrefix+"失败，请稍后重试或联系管理员");
			}
			PageDetail.changeBtnsLoading("save", false);
		},
		save:function(){
			if(!PageDetail.toValid()){
				//FW.error("提交的内容有错误的地方，请修改后重试");
				return;
			}
			PageDetail.changeBtnsLoading("save", true);
			$.post(
				PageDetail.objs.url.save,
				PageDetail.getDataForSave(),
				PageDetail.saveCallback,
				"json"
			);
		},
		
		refreshAttachFields:function(){
			PageDetail.objs.attach.fields[0].options.uploader=basePath+"upload?method=uploadFile&jsessionid="+PageDetail.objs.attach.sessId;
			PageDetail.objs.attach.fields[0].options.delFileUrl=basePath + "upload?method=delFile&key="+PageDetail.objs.attach.valKey;
		},
		setAttach:function( fileMaps ){
			PageDetail.objs.attach.fields[0]["options"]["initFiles"]=fileMaps;
			PageDetail.refreshAttachFields();
			var formObj=$("#"+PageDetail.objs.attach.formId);
			formObj.iForm('init',{"fields":PageDetail.objs.attach.fields,"options":PageDetail.objs.attach.options});
			
			if(PageDetail.objs.attach.isEndEdit){
				formObj.iForm('endEdit');
				if(!(fileMaps)){
					$("#"+PageDetail.objs.attach.divId).iFold("hide");
				}
			}else{
				$("#"+PageDetail.objs.attach.divId).iFold("show");
			}
			
			PageDetail.objs.data.attachJson=JSON.stringify(PageDetail.getDataForSubmitAttach());
		},
		getDataForQueryAttach:function(){
			return {
				itemType:PageDetail.objs.attach.itemType,
				itemId:PageDetail.objs.form.beanId
			};
		},
		queryAttachCallback:function(data){
			if( data.result == "success"){
				PageDetail.setAttach(data.fileMap);
			}else{
				PageDetail.setAttach(null);//隐藏附件
			}
		},
		queryAttach:function(){
			$.post(
				PageDetail.objs.attach.url,
				PageDetail.getDataForQueryAttach(),
				PageDetail.queryAttachCallback,
				"json"
			);
		},
		getDataForSubmitAttach:function(){
			return {
				fileIds:$("#"+PageDetail.objs.attach.formId).iForm("getVal").attachment
			};
		},
		
		getDataForSubmitItem:function(isEndEdit,isOnlyUpdate){
			PageDetail.objs.item.isSaved=false;
			PageDetail.toEditItem(false);
			var obj=$("#"+PageDetail.objs.item.datagridId);
			var addRows=obj.datagrid('getChanges','inserted');
			var delRows=obj.datagrid('getChanges','deleted');
			var updateRows=obj.datagrid('getChanges','updated');
			PageDetail.toEditItem(!isEndEdit,false,isOnlyUpdate);
			
			return {
				addRows:JSON.stringify(addRows),
				delRows:JSON.stringify(delRows),
				updateRows:JSON.stringify(updateRows)
			};
		},
		toEditItem:function(isToEdit,isOnlyLast,isOnlyUpdate){
			var type=(isToEdit?"begin":"end")+"Edit";
			var obj=$("#"+PageDetail.objs.item.datagridId);
			var rowSize=obj.datagrid('getRows').length;
			if(isOnlyLast){
				obj.datagrid(type, rowSize-1);
			}else{
				for( var i = 0 ; i < rowSize; i++ ){
					obj.datagrid(type, i);
				}
			}
			if(isToEdit&&!isOnlyUpdate){
				PageDetail.toChangeAddItemBtn(rowSize);
				$("#"+PageDetail.objs.item.addItemToolbarId).show();
				obj.datagrid("showColumn",PageDetail.objs.item.delField);
			}else{
				$("#"+PageDetail.objs.item.addItemToolbarId).hide();
				obj.datagrid("hideColumn",PageDetail.objs.item.delField);
			}
		},
		toAddItem:function(){
			$("#"+PageDetail.objs.item.datagridId).datagrid('appendRow',$.extend({},PageDetail.objs.item.blankItem));
			PageDetail.toEditItem(true,true);
		},
		toDelItem:function(rowIndex,field,value) {
			FW.confirm("删除？<br/>确定删除该"+PageDetail.objs.item.itemName+"吗？该操作无法撤销。", function() {
				$("#"+PageDetail.objs.item.datagridId).datagrid("deleteRow",rowIndex);
				PageDetail.toChangeAddItemBtn();
			});
		},
		toAcceptItem:function(){
			$("#"+PageDetail.objs.item.datagridId).datagrid('acceptChanges');
			PageDetail.objs.item.isSaved=true;
		},
		toChangeAddItemBtn:function(num){
			if(!num){
				num=$("#"+PageDetail.objs.item.datagridId).datagrid('getRows').length;
			}
			var textVal=(num>0?"继续":"")+PageDetail.objs.item.addItemBtnName;
			$("#"+PageDetail.objs.item.addItemBtnId).text(textVal);
		},
		setItem:function(dataArray){
			var data={rows:[],total:0};
			if(dataArray&&dataArray.length>0){
				data={rows:dataArray,total:dataArray.length};
			}
			
			$("#"+PageDetail.objs.item.datagridId).datagrid($.extend({
		        pagination:false,
		        singleSelect:true,
		        fitColumns:true,
		        nowrap:false,
		        data:data,
		        columns:[PageDetail.objs.item.fields],
				onLoadError:PageDetail.errorSetItemCallback,
		        onLoadSuccess:PageDetail.successSetItemCallback,
		        onClickCell : function(rowIndex,field,value) {
		        	if(field==PageDetail.objs.item.delField){
						PageDetail.toDelItem(rowIndex,field,value);
					}
				}
		    },PageDetail.objs.item));
		},
		successSetItemCallback:function(data){
			
		},
		errorSetItemCallback:function(data){
			
		},
		
		changeBtnsLoading:function(btnsStr,toLoading){
			var btns=PageDetail.getBtnsObj(btnsStr);
			if(btns){
				btns.button(toLoading?"loading":"reset");
			}
		},
		getBtnsObj:function(btnsStr){
			if(btnsStr){
				var btns=btnsStr.split(",");
				var btnIds="";
				for(var i=0;i<btns.length;i++){
					btnIds+="#"+PageMode.objs["buttonDivId"][btns[i]]+",";
				}
				if(btnIds){
					btnIds=btnIds.substr(0,btnIds.length-1);
				}
				return $(btnIds);
			}
		}
};