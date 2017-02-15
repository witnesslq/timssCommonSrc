function homepageData() {
	this.conPos = [];
	this.hasPos = false;
	this.realData = null;/*站点活跃度真实登陆数据*/
	this.setHighcharts = function(){
		setHighchartsText();
	};
	
	var timeoutCount = 0;
	var totalTimeoutCount = 5;
	
	var setHighchartsText = function(){
		var jq = $('#siteuserloginfohopcharts').find("text:contains('Highcharts')");
		jq.remove();
		if(timeoutCount++ < totalTimeoutCount){
			setTimeout(setHighchartsText, 5);
		}
	};
	this.getReSizeDate = function(dataRows, boxSize) {
		if (!this.hasPos) {
			for ( var i = 0; i < dataRows.length; i++) {
				var oneRow = dataRows[i];
				var cPos = null;
				var contents = null;
				for ( var j = 0; j < oneRow.length; j++) {
					var oneObject = oneRow[j];
					if ("content" == oneObject.id) {
						cPos = {
							i : i,
							j : j
						};
					} else if ("contents" == oneObject.id) {
						contents = oneObject.value;
					}
					if (null != cPos && null != contents) {
						break;
					}
				}
				this.conPos.push({
					pos : cPos,
					contents : contents
				});
			}
			this.hasPos = true;
		}
		var subflag = null;
		if (201 > boxSize.width) {
			subflag = {
				beg : 0,
				end : 15
			};
		} else if (201 < boxSize.width && boxSize.width < 440) {
			subflag = {
				beg : 0,
				end : 26
			};
		} else if (440 < boxSize.width && boxSize.width < 680) {
			subflag = {
				beg : 0,
				end : 50
			};
		} else if (680 < boxSize.width && boxSize.width < 910) {
			subflag = {
				beg : 0,
				end : 90
			};
		} else if (910 < boxSize.width && boxSize.width < 1146) {
			subflag = {
				beg : 0,
				end : 165
			};
		} else {
			subflag = {
				beg : 0,
				end : 170
			};
		}
		for ( var i = 0; i < this.conPos.length; i++) {
			var con = this.conPos[i];
			if (con.contents.length > subflag.end) {
				dataRows[con.pos.i][con.pos.j].value = con.contents.substr(
						subflag.beg, subflag.end)
						+ "...";
			} else {
				dataRows[con.pos.i][con.pos.j].value = con.contents;
			}
		}
	};
}

var homepageDataIns = new homepageData();

(function(_P) {
	/** *************************hop defMods begin************************** */

	_P.definedModules.hopList = {
		sizeX : 3,
		bordered : true,
		module : "hoplist",
		flag : true,
		sum : true,
		noPadding : "lr"
	};

	_P.definedModules.hopcharts = {
		sizeX : 6,
		sizeY : 6,
		noPadding : "lr",
		bordered : true,
		module : "highcharts",
		resizable : false
	};

	/** *************************hop defMods end************************** */

	/** *************************hop template begin************************** */
	_P.templates.list = '<div style="width:95%" class="homepage">'
			+ '<ul>'
			+ '<li ng-repeat="row in rowData" ng-style="calcStyle($index)" ng-dblclick="calcClick(row, $index)">'
			+ '<span id="{{field.id}}" ng-repeat="field in row" ng-style="{{field.style}}" width="{{field.w}}">{{field.value}}</span>'
			+ '</li>'
			+ '</ul>'
			+ '</div>'
			+ '<div style="width:95%;display:none;" class="homepage" id="hop-notice-none">'
			+ '<div style="margin-left: 41%;margin-top: 13%;font-size:12px;">无最新信息<div></div>';
	/** *************************hop template end************************** */

	/** *************************hop directive begin************************** */

	app.directive('highcharts', function($compile) {
		return {
			link : function(scope, element, attrs) {
				scope.$on('portal-data-loaded', function() {
					var id = scope.item.id;
					if ('siteuserloginfohopcharts' == id) {
						var data = scope.g;
						homepageDataIns.realData = data.xrData;
						for(var index = 0; index < data.xlable.length;index++ ){
							var chartConfig = scope.chartConfig;
							chartConfig.options.xAxis.categories[index] = data.xlable[index];
						}
						homepageDataIns.setHighcharts();
					}
				});
			}
		};
	});

	app
			.directive(
					'hoplist',
					function($compile) {
						return {
							link : function(scope, element, attrs) {
								var rowData = new Array();
								var item = scope.item;
								scope.options = item.options;
								scope.columns = item.columns;

								scope.calcStyle = function(index) {
									var row = scope.g[index];
									var cssVal = "";
									if("Notice" == row.status){
										cssVal = "url(../img/homepage/portal_forward.png)";
									}else if("Complete" == row.status){
										cssVal = "url(../img/homepage/portal_complete.png)";
									}else if("Info" == row.status){
										cssVal = "url(../img/homepage/portal_info.png)";
									}else if("Warning" == row.status){
										cssVal = "url(../img/homepage/portal_warning.png)";
									}
									return {
										"list-style-image" :cssVal
									};
								};

								scope.calcClick = function(row, index) {
									var curRow = scope.g[index];
									var _parent = window.parent;
									var url = _parent.basePath + curRow.url;
									var id = curRow.id;
									var name = curRow.name;
									var currTabId = _parent.FW
											.getCurrentTabId();
									var opts = {
										id : id,
										name : name,
										url : url,
										tabOpt : {
											closeable : true,
											afterClose : "FW.deleteTab('$arg');FW.activeTabById('"
													+ currTabId
													+ "');FW.getFrame('homepage').homepageServiceImpl.refresh();"
										}
									};
									window.parent._ITC.addTabWithTree(opts);
								};

								scope
										.$on(
												'portal-data-loaded',
												function() {
													var data = scope.g;
													var columns = scope.columns;
													for ( var i = 0; i < data.length; i++) {
														var row = data[i];
														var r = new Array();
														for ( var j = 0; j < columns.length; j++) {
															var column = columns[j];
															var fieldValue = row[column.field];
															if ("contents" == column.field) {
																fieldValue = row["content"];
															}
															fieldValue = typeof (fieldValue) !== "undefined" ? fieldValue
																	: "";
															r
																	.push({
																		id : column.field,
																		value : fieldValue,
																		w : column.width,
																		style : column.style
																	});
														}
														rowData.push(r);
													}
													scope.rowData = rowData;
													var boxSize = new Object();
													boxSize.width = parseInt(scope.infoBoxStyle.width);
													boxSize.height = parseInt(scope.infoBoxStyle.height);
													homepageDataIns
															.getReSizeDate(
																	scope.rowData,
																	boxSize);

													if ("undefined" == typeof data
															|| 0 == data.length) {
														$("#hop-notice-none")
																.show();
													}
												});

								scope.$on('infobox-size-changed', function(e,
										boxSize) {
									homepageDataIns.getReSizeDate(
											scope.rowData, boxSize);
								});

								scope.rowData = [];
								var el = $compile(_P.templates.list)(scope);
								element.replaceWith(el);
							}
						};
					});
	/** *************************hop directive end************************** */
})(_portal);