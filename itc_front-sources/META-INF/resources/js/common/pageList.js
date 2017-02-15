var PageList={
		objs:{
			isSearchMode:false,
			isSearchLineShow:false,
			datagrid:{
				id:"",//required
				detailPage:{
					url:"",//required
					createUrl:"",//required
					openTab:true,//是否用tab显示详情
					idPrefix:"listItem",//详情页面id前缀，后面加_详情项id
					namePrefix:"列表项"//详情页面名称前缀，后面加_详情项名称
				},
				params:{//datagrid使用的参数
					singleSelect:true,
					columns:[[]],//required
					pageSize:pageSize,
					fitColumns:true,
					idField:"id",//required
					nameField:"name",//required
					url:"getList.do",//required
					onRenderFinish:function(){
						datagrid.datagrid("resize");
			        },
					onLoadSuccess:function(data){
						PageList.changeShow(data);
					},
					onDblClickRow : function(rowIndex, rowData) {
						PageList.toShow(rowData);
					}
				}
			}
		},
		
		init:function(initParams){
			if(initParams){
				$.extend(true,PageList.objs,initParams);
			}else{
				FW.error("初始化信息缺失，请联系管理员！");
				return;
			}
			datagrid=$("#"+PageList.objs.datagrid.id).iDatagrid("init",PageList.objs.datagrid.params);
		},
		
		toShow:function(data){
			var dataId=data[PageList.objs.datagrid.params.idField];
			var url=PageList.objs.datagrid.detailPage.url+dataId;
			if(PageList.objs.datagrid.detailPage.openTab){
				addTab(PageList.objs.datagrid.detailPage.idPrefix+"_"+dataId, 
								PageList.objs.datagrid.detailPage.namePrefix,url,FW.getCurrentTabId(),PageList.objs.datagrid.id);
			}else{
				FW.navigate(url);
			}
		},
		
		changeShow:function(data){//切换模式，用于控制按钮和文字的改变
			if(PageList.objs.isSearchMode){
		        //搜索时的无数据信息
		        if(data && data.total==0){
		            $("#noSearchResult").show();
		        }
		        else{
		            $("#noSearchResult").hide();
		        }
		    } 
		    else{
		        //初始化时的无数据信息        
		    	if(data && data.total==0){
	                $("#grid_wrap,.toolbar-with-pager").hide();
	                $("#grid_empty").show();
	            }else{
	            	$("#grid_wrap,.toolbar-with-pager").show();
	                $("#grid_empty").hide();
	            }
		        //这句要有 否则弹起按钮时信息没法隐藏
		        $("#noSearchResult").hide();
		    }
		},
		
		toShowSearchLine:function(){
			if(PageList.objs.isSearchLineShow){
				PageList.objs.isSearchLineShow=false;
				datagrid.iDatagrid("endSearch");		        
		    }
		    else{
		    	PageList.objs.isSearchLineShow=true;
		    	datagrid.iDatagrid("beginSearch",{"remoteSearch":true,"onParseArgs":function(arg){
		       		PageList.objs.isSearchMode = true;
					return {"search":JSON.stringify(arg)};
				}});
		    }
		},
		
		toCreate:function(){
			var url=PageList.objs.datagrid.detailPage.createUrl;
			if(PageList.objs.datagrid.detailPage.openTab){
				addTab("create_"+PageList.objs.datagrid.detailPage.idPrefix, 
								PageList.objs.datagrid.detailPage.namePrefix,url,FW.getCurrentTabId(),PageList.objs.datagrid.id);
			}else{
				FW.navigate(url);
			}
		}
};