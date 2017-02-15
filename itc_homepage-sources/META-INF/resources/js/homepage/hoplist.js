var homepageServiceImpl = {
	refresh : null,
	deleteProcess : null,
	newProcessTab : null,
	isinit : true
};
$(document).ready(function() {
	var dataGrid = $("#wait_grid").iDatagrid("init", {
		columns : columns,
		pageSize : pageSize,
		url : url, 
		singleSelect:"undefined" == typeof singleSelect? true : singleSelect,
		onLoadSuccess : function(data) {
			if(homepageServiceImpl.isinit){
				if (data && data.total == 0) {
					$("#grid_wrap").hide();
					$("#grid_empty").show();
				} else {
					$("#grid_wrap").show();
					$("#grid_empty").hide();
				}
				homepageServiceImpl.isinit = false;
			}
		},
		onDblClickRow : function(rowIndex, rowData) {
			var currTabId = FW.getCurrentTabId();
			var opts = {
				id : rowData.flowno,
				name : rowData.typename,
				url : basePath + rowData.url,
				tabOpt : {
					closeable : true,
					afterClose :"FW.deleteTab('$arg');FW.activeTabById('" + currTabId + "');FW.getFrame('homepage').homepageServiceImpl.refresh();"
				}
			};
			_parent()._ITC.addTabWithTree(opts);
		}
	});
	homepageServiceImpl.refresh = function() {
		$("#wait_grid").datagrid("reload");
		_parent()._TASK.refrashCount();
		homepageServiceImpl.isinit = true;
	};
	homepageServiceImpl.deleteProcess = function() {
		var rows = $("#wait_grid").datagrid("getChecked");
		if (rows.length > 0) {
			FW.confirm("确定删除草稿?", function() {
				$.ajax({
					type : "POST",
					url : basePath + "homepage/Info/DraftDelete.do",
					data : {
						deleteRows : JSON.stringify(rows)
					},
					dataType : "json",
					success : function(data) {
						if ("undefined" != typeof data.operCount) {
							if (0 < data.operCount) {
								FW.success(data.msg);
								homepageServiceImpl.refresh();
							} else {
								FW.error(data.msg);
							}
						}
					},
					error : function(XMLHttpRequest, textStatus, errorThrown) {
						FW.error("删除草稿失败");
					}
				});
			});
		}
	};
	homepageServiceImpl.newProcessTab = function(url, id, name){
		var currTabId = FW.getCurrentTabId();
		var opts = {
			id : id,
			name : name,
			url : basePath + url,
			tabOpt : {
				closeable : true,
				afterClose : function(id) {
					FW.deleteTab(id);
					FW.activeTabById(currTabId);
				}
			}
		};
		_parent()._ITC.addTabWithTree(opts);
	};
	$("#btn_advSearch").click(function() {
		if ($(this).hasClass("active")) {
			$("#wait_grid").iDatagrid("endSearch");
		} else {
			$("#wait_grid").iDatagrid("beginSearch", {
				"remoteSearch" : true,
				"onParseArgs" : function(args) {
					return {
						"search" : JSON.stringify(args)
					};
				}
			});
		}
	});
});