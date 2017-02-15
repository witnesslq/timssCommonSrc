/**
	ITC前端组件 文件上传（纯HTML5版）
*/
if(!window._agentIE){
(function($){
	$.fn.extend({		
		ITCUI_FileUpload : function(options,arg){
			var _this = $(this);
			var _t = this;
			var itemTemplate = '<div class="itc_upload_wrap bbox ${preview}" fileid="${fileID}" unique-id="${uniqueID}">' + 
				'<div class="itc_upload_icon icon_${fileExt} ml8">' +
				'</div>' +
				'<div class="itc_upload_detail_wrap ml4">' +
					'<div class="itc_upload_filename">' +
						'<span class="itc_filename" title="${fileName}">${fileName}</span>' +
					'</div>' +
					'<div style="clear:both" class="itc_upload_progress_line">' +
						'<div class="itc_upload_progress_txt" style="margin-left:-4px">' +
							'<span class="fl">${fileSize}</span>' + 
							'<a class="itc_upload_cancel itc_link fr">取消</a>' +
							'<a class="itc_upload_delfail itc_link_del itc_link fr" style="display:none" reserved="${reserved}" creator="${creator}">删除</a>' +
							'<a class="itc_upload_top itc_link fr" style="margin-right:6px">置顶</a>' + 
							'<a class="itc_upload_down itc_link fr" style="display:none;margin-right:6px">下载</a>' +
						'</div>' +
					'</div>' +
				'</div> ' +
			'</div>';
			var progressHtml = '<div class="itc_upload_progess_wrap"><div class="itc_upload_progress_bar" style="width:0px"></div></div>';
			
			_t.parseFileItem = function(file){
				var obj = {
					fileExt : _itc_getextname(file.name),
					fileSize : _itc_getfilesize(file.size),
					fileName : file.name,
					creator : file.creator || "",
					reserved : file.reserved || false,
					fileID : file.fileID || -1,
					uniqueID : file.uniqueIdentifier, //flow.js生成的文件唯一标识符
					preview: ""
				};
				return _t.applyTemplate(itemTemplate, obj);
			};
			
			_t.applyTemplate = function(template,obj){
				for(var k in obj){
					var v = obj[k];
					re = new RegExp("\\$\\{" + k + "\\}","g");
					template = template.replace(re,v);
				}
				return template;
			};
			
			_t.cancelFile = function(flow,target){
				var item = target.parents(".itc_upload_wrap");
				var uniqueId = item.attr("unique-id");
				var file = flow.getFromUniqueIdentifier(uniqueId);
				if(!file){
					return;
				}
				file.cancel();
				item.remove();
			};						
			
			_t.delUploadedFile = function(target){
				//删除上传成功的文件
				var item = target.parents(".itc_upload_wrap");
				var queue = item.parent();
				var fileId = item.attr("fileid");
				var msg = item.data("ajaxMsg") || fileId;
		        if(!msg){
		            return;
		        }
		        var opts = queue.data("options");
				if(!opts){
					//这里有点遗留问题 删除已经上传的文件会触发两次delUploadFile 第二次的时候opts为空
					return;
				}
		        if(opts.delFileAfterPost && item.attr("init")){
		            item.remove();
		            if(opts.singleFile){
		            	
		            }
		        }
		        else{
		        	Notice.confirm("确认删除|是否删除该文件？该操作无法恢复。",function(){
						var flowFiles = queue.data("flow").files;
						var uniqueId = item.attr("unique-id");						
	                    var ajaxOpts = {
	                        type : "GET",
	                        dataType : "json",
	                        error : function(){
	                            FW.error("文件删除失败");
	                        },
	                        success : function(data){
	                            if(!data || data.status==1){
	                                FW.success("文件删除成功");
									for(var i=0;i<flowFiles.length;i++){
										var file = flowFiles[i]
										if(file.uniqueIdentifier == uniqueId){
											flowFiles.splice(i, 1);
											break;
										}
									}
	                                item.remove();
	                                if(opts.singleFile){
	                                    
	                                }
	                            }
	                            else{
	                                FW.error(data.msg || "文件删除失败");
	                            }
	                        }
	                    };
	                    if(opts.delFileUrl){
	                        ajaxOpts.url = opts.delFileUrl;
	                        ajaxOpts.data = {"id":msg};
	                    }
	                    else{
	                        ajaxOpts.url = msg;
	                    }
	                    $.ajax(ajaxOpts);
	                });
		        }
			};

			/*初始化代码*/
			_t.init = function(){
				if(!_this.attr("id")){
					return;
				}
				if(!options && typeof(options)!="object"){
					return;
				}
				/*沿用uploadify的前端结构*/
				var realId = _this.attr("id") + "_real";
				var queueId = _this.attr("id") + "_queue";
				$("#" + realId).data("isUploadFinish",true);
				$("<div></div>").appendTo(_this).attr("id", realId);
				$("<div></div>").appendTo(_this).attr("id", queueId).css({
					"width":"100%","clear":"both"
				}).addClass("upload-queue");					
				var btnText = options.buttonText || "添加附件";
				var btnCls = options.buttonClass || "itc_link_add";
				options.itemTemplate = options.itemTemplate || itemTemplate;
				var realBtnId = realId + "-button";
				var realBtnHtml = "<div id='" + realBtnId + "' class='uploadify-button " + btnCls + "' style='height: 20px; line-height: 16px; width: 72px;'>" + 
				                  "<span class='uploadify-button-text' style='text-decoration: none;'>" + btnText +  "</span>" +
				                  "</div>";
				$("#" + realId).addClass("uploadify").css({
					height : "20px",
					width : "72px",
					float : "left"
				}).html(realBtnHtml);
				
				var queue = $("#" + queueId); 
				queue.data("options",options);
				
				//初始化flow.js
				var flow = new Flow({
					target : options.uploader, 
					chunkSize : 100*1024*1024,
					testChunks : false,
					allowDuplicateUploads : true
				});
				
				queue.data("flow", flow);
				
				//直接在外层框架上接收事件
				queue.on("click",function(e){
					var target = $(e.target);
					if(target.hasClass("itc_link_del")){
						if(target.hasClass("itc_upload_delfail")){
							//删除失败的文件
						}
						else{			
							//删除成功的文件
					        _t.delUploadedFile(target);
						}
					}else if(target.hasClass("itc_upload_cancel")){
						//取消正在上传的文件
						_t.cancelFile(flow,target);
					}else if(target.hasClass("itc_upload_top")){
						//置顶功能
						var el = target.parents(".itc_upload_wrap");
						var q = el.parents(".upload-queue");
						el.detach().prependTo(q);
					}
				});								
				
				flow.on('filesAdded',function(array, event){
					//因为flow.js在处理队列的时候有延迟 而且没有自动开始功能 这里使用延时开始上传
					setTimeout(function(){
						flow.upload();
					},200);					
				});
				
				flow.on('fileAdded',function(file,event){
					if(options.fileSizeLimit && (file.size) / 1000 > options.fileSizeLimit){
						var sizeErrMsg = file.name + "过大";
						FW.error(sizeErrMsg);
						return false;
					}
					$("#" + queueId).append(_t.parseFileItem(file));
					//flow.js不提供单文件upload start 只能在这里创建空的进度条
					queue.children("[unique-id='" + file.uniqueIdentifier + "']").find(".itc_upload_progress_txt").prepend(progressHtml);
					var handle = setInterval(function(){
						var _file = file;
						var _handle = handle;
						var _queueId = queueId;
						var progressWidth = (_file.size==0)?46:parseInt(_file.sizeUploaded()/_file.size*46);
						$("#" + _queueId).children("[unique-id='" + _file.uniqueIdentifier + "']").
							find(".itc_upload_progress_bar").css("width",progressWidth + "px");
						if(file.isComplete()){
							console.log("file upload finish");
							clearTimeout(_handle);
						}
					},200);
				});
				
				flow.on('fileProgress',function(file, chunk){
					
				});
				
				flow.on('fileError', function(file, message){
					var item = queue.children("[unique-id='" + file.uniqueIdentifier + "']");
					item.find(".itc_upload_progess_wrap").remove();
					item.find(".itc_upload_progress_txt").append('<span class="itc_upload_fail">上传失败</span>');
				});
				
				flow.on('fileSuccess',function(file, message, chunk){
					var items = queue.children(".itc_upload_wrap");
					for(var i=0;i<items.length;i++){
						var item = $(items[i]);
						var uniId = item.attr("unique-id");
						if(uniId == file.uniqueIdentifier){
							data = JSON.parse(message);
							if(typeof(data) === "object"){
								item.find(".itc_upload_progess_wrap").remove();
								if(data.status > 0){
									item.find(".itc_upload_progress_txt").append('<span class="itc_upload_finish">完成</span>');
									//取消按钮
									item.find(".itc_upload_cancel").remove();
									//删除按钮
									item.find(".itc_upload_delfail").show().removeClass("itc_upload_delfail");
									if(data.msg){
										//将文件的ID写到属性中
										item.data("ajaxMsg",data.msg);
										if(options.downloadFileUrl || options.delFileUrl){
											item.attr("fileID",data.msg);
										}
									}
								}
								else{
									item.find(".itc_upload_progress_txt").append('<span class="itc_upload_fail">' + data.msg + '</span>');
									item.find(".itc_upload_cancel").hide();
									item.find(".itc_upload_delfail").show();
								}
							}
						}
					}
				});				
				//绑定上传按钮
				flow.assignBrowse($("#" + realBtnId));

				//显示已经上传的文件
				if(options.initFiles && isArray(options.initFiles)){
					_t.createInitList(options.initFiles);
				}
			};
			
			
			/*获取当前已上传的文件列表*/
			_t.getFileIds = function(){
				var queueId = _this.attr("id") + "_queue";
				var ids = [];
				var objs = $("#" + queueId).find(".itc_upload_wrap");
				for(var i=0;i<objs.length;i++){
					var id = $(objs[i]).attr("fileID");
					if(id.indexOf("SWF")<0){
						ids.push(id);
					}
				}
				return ids.join(",");
			};
			
			_t.createInitList = function(initFiles){
				var q = _this.children(".upload-queue");
				var options = q.data('options');
				var fileHtml = "";
				for(var i=0;i<initFiles.length;i++){
					var file = initFiles[i];
					var blk = options.itemTemplate;
					blk = blk.replace(new RegExp("<a.*?取消</a>"),"")
					var fnStr;
					var fn = file.fileName;
					if(!options.openFileUrl){
						fnStr = fn;
					}
					else{
						var oUrl = (options.openFileUrl.indexOf("?")>0)?options.openFileUrl + "&":options.openFileUrl + "?";
						var p = file.fileName.lastIndexOf(".");
						oUrl += "id=" + file.fileID + "&name=a" + file.fileName.substring(p);
						fnStr = "<a class='upload-viewdoc' id='" + file.fileID + "' ext='" +  _itc_getextname(file.fileName) + "'>" + fn + "</a>";

					}
                    var fileName = file.fileName;
					blk = blk.replace(new RegExp("\\$\{fileID\}"),file.fileID);
					blk = blk.replace(new RegExp("\\$\{fileName\}","g"),fnStr);
					blk = blk.replace(new RegExp("\\$\{fileExt\}"), _itc_getextname(fileName));
					blk = blk.replace(new RegExp("\\$\{fileSize\}"),_itc_getfilesize(file.fileSize));
					blk = blk.replace(new RegExp("\\$\{creator\}"),file.creator);
					blk = blk.replace(new RegExp("\\$\{reserved\}"),file.reserved);
					//2016.6.6 文件预览功能只支持旧记录 不支持新上传的文件
                    var t = UiCommon.isPreviewableImg(fileName) || (UiCommon.isPreviewableFile(fileName) && file.status === "RECEIVE_SUCCESS");
					blk = blk.replace(new RegExp("\\$\{preview\}"), t ? "preview" : "");
					fileHtml += blk;
				}
				if(options.singleFile){
					if(initFiles.length>0){
						q.prev("div").hide();
					}
				}
				q.children(".itc_upload_wrap").remove();
				q.append(fileHtml);
				
				if(options.delFileUrl){
					var delLinks = q.find(".itc_upload_delfail");
					for(var i=0;i<delLinks.length;i++){
						var lnk = $(delLinks[i]);
						lnk.parents(".itc_upload_wrap").attr("init",true);
						if(!options.canUserDelete || options.canUserDelete(initFiles[i])){
							lnk.removeClass("itc_upload_delfail").show();
							_itc_binddel(lnk);
						}
					}
				}
				if(options.downloadFileUrl){
					q.find(".itc_upload_down").each(function(){
						$(this).removeClass("itc_upload_down").show();
						_itc_binddownload(this);
					});
				}
			};

			if(typeof(options)=="object"){
				_t.init();
			}
			else if(options=="getVal"){
				return _t.getFileIds();
			}
			else if(options=="isUploadFinish"){
				return _t.isUploadFinish();
			}
			else if(options=="setInitList"){
				_t.createInitList(arg);
			}

			return _this;
		}
	});
})(jQuery);
}

/*文件预览*/
$(document).ready(function(){
	top._imageViewer.config({
		basePath : "."
	});
	$("body").on("click", ".itc_upload_wrap.preview", function(e){
		var wrap = $(e.currentTarget);
		var target = $(e.target);
		if(target.hasClass("itc_link")){
			return;
		}
		var fileId = wrap.attr("fileid");
		var fileName = wrap.find(".itc_filename").text();
        if(UiCommon.isPreviewableImg(fileName)){
            //对于图片直接预览而不是调用docx
            var queue = wrap.parents(".upload-queue");
            var options = queue.data("options");
            if(options){
                var url = options.downloadFileUrl + "&id=" + fileId;
                top._imageViewer.viewImage({
                    url: url,
                    fileName: fileName
                });
            }
        }else{
            top._imageViewer.viewDoc({
                fileId: fileId,
                fileName: fileName
            });
        }
	});
});
