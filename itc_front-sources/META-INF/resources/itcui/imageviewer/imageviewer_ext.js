/**
 * Created by 890157 on 2016/4/27.
 */
function ImageViewer(){
    var cfg = {};
    var defOpts = {
        imgSize: "md",
        imgType: "png"
    }
    var folderStack = [];
    var currentParent = null;
    /*---------------------template--------------------------*/
    var tmplArchive = '<ul class="archive-wrap">' +
        '{{each fileList as item}}' +
        '<li class="itc_upload_wrap bbox{{if item.fileStatus == 3}} prev-ok{{else if item.fileType==4}} folder-click{{/if}}" ' +
        '{{if item.fileStatus == 3}}onclick="_imageViewer._viewDoc(\'{{item.fileId}}\', \'{{item.innerId}}\',\'{{item.fileName}}\')"' +
        '{{else if item.fileType==4}}onclick="_imageViewer._enter(\'{{item.fileId}}\', \'{{item.innerId}}\')"{{/if}}>' +
        '<div class="itc_upload_icon icon_{{item.extName}}"></div>' +
        '<div class="itc_upload_detail_wrap">' +
        '<div class="itc_upload_filename"><span class="itc_filename" title="">{{item.fileName}}</span></div>' +
        '<div class="itc_upload_progress_txt">{{if item.fileStatus == 3}}<span class="fr">可以预览</span>{{/if}}</div>' +
        '</div>' +
        '</li>' +
        '{{/each}}' +
        '</ul>';
    var renderArchive = template.compile(tmplArchive);
    /*--------------------private methods--------------------------*/
    var getFileStatus = function(opts, callback){
        if(!opts.innerId){
            $.ajax({
                url: opts.basePath + "attchmentPreview?method=info",
                data : {
                    ids: opts.fileId
                },
                dataType: "json",
                success: function(xhr){
                    if(xhr && xhr.retCode > 0 && xhr.items.length){
                        callback(xhr.items[0], opts);
                    }else if(xhr.retCode < 0){
                        FW.error(xhr.errorInfo);
                    }

                }
            });
        }else{
            $.ajax({
                url: opts.basePath + "attchmentPreview?method=archivefileinfo",
                data : {
                    fileId: opts.fileId,
                    pubId: opts.pubId,
                    innerId: opts.innerId
                },
                dataType: "json",
                success: function(xhr){
                    if(xhr && xhr.retCode > 0){
                        callback(xhr.data, opts);
                    }else if(xhr.retCode < 0){
                        FW.error(xhr.errorInfo);
                    }
                }
            });
        }
    };

    var viewArchive = function(opts, pid){
        $.ajax({
            url: opts.basePath + "attchmentPreview?method=listarchive",
            data: {
                fileId: opts.fileId,
                pid: pid,
            },
            dataType: "json",
            success: function(xhr){
                if(!xhr.retCode || xhr.retCode < 0){
                    FW.error("无法获取压缩文件信息");
                }
                for(var i=0;i<xhr.fileList.length;i++){
                    var file = xhr.fileList[i];
                    //判断文件的扩展名 以用于显示图标
                    if(file.fileType === getEnum("FILE_TYPE", "FOLDER")){
                        file.extName = "folder"
                    }else{
                        file.extName = _itc_getextname(file.fileName);
                        file.formatSize = _itc_getfilesize(file * 1024);
                    }
                    file.fileId = opts.fileId;
                }
                //如果是子级目录 人为添加一个虚假的项表示返回操作
                if(pid){
                    xhr.fileList.unshift({
                        fileName: "返回上级",
                        extName: "back",
                        fileId: opts.fileId,
                        innerId: "__prev__",
                        fileType: getEnum("FILE_TYPE", "FOLDER")
                    });
                }
                if($.fancybox.isOpen){
                    $(".fancybox-inner").html(renderArchive(xhr));
                }else{
                    var w = $(window);
                    $.fancybox.open("<p></p>",{
                        autoSize: false,
                        padding: 6,
                        beforeLoad: function() {
                            this.width = w.width() * 0.6;
                            this.height = w.height() * 0.6;
                        },
                        afterLoad: function() {
                            this.content = renderArchive(xhr);
                        }
                    });
                }
            }
        });
    };

    var parseCfg = function(opts){
        opts = opts || {};
        var defOpts = $.extend(defOpts, cfg);
        opts = $.extend(defOpts, opts);
        if(opts.basePath){
            if(!opts.basePath.indexOf("/") !== opts.basePath.length - 1){
                opts.basePath += "/";
            }
        }
        return opts;
    };
    
    var openFancyBox = function(imgList){
        $.fancybox.close();
        $.fancybox.open(imgList, {
            padding : 0,
            type:"image",
            closeBtn  : false,
            helpers : {
                title : {
                    type : 'float'
                },
                buttons: {

                }
            },
        });
    };

    /*--------------------public methods--------------------------*/
    this._viewDoc = function(fileId, innerId, fileName){        
        window._imageViewer.viewDoc({
            fileId: fileId,
            innerId: innerId,
            fileName: fileName
        });
    };

    this._enter = function(fileId, innerId){
        var opts = parseCfg({
            fileId: fileId
        });
        if(innerId !== "__prev__"){
            folderStack.push(currentParent);
            currentParent = innerId;
        }else{
            currentParent = folderStack.pop();
            innerId = currentParent;
        }
        viewArchive(opts, innerId);
    }

    this.config = function(opts){
        cfg = opts;
    };

    this.viewDoc = function(opts){
        opts = parseCfg(opts);
        folderStack = [];
        currentParent = null;
        var self = this;
        getFileStatus.apply(this, [opts, function(xhr, opts){
            if(xhr.fileStatus !== getEnum("FILE_STATE", "SUCCESS")){
                FW.error("文档尚未转换完成，无法预览");
                return false;
            }
            if(xhr.fileType === getEnum("FILE_TYPE", "ARCHIVE")){
                viewArchive(opts);
                return;
            }
            var pageCount = xhr.pageCount || 0;
            var imgList = [];
            var extName = null;
            if(opts.fileName){
            	var n = opts.fileName.lastIndexOf(".");
            	extName = opts.fileName.substring(n+1).toLowerCase();
            	if(extName === "jpeg"){
            		extName = "jpg";
            	}else if(!/(jpg|png|gif|bmp)/.test(extName)){
            		extName = "png";
            	}
            }
            for(var i=1;i<=pageCount;i++){
                var api = "attchmentPreview?method=image";
                var url = "&fileId=" +  opts.fileId;
                if(xhr.innerId){
                    url += "&innerId=" + xhr.innerId;
                }
                if(extName){
                	url += "&imgType=" + extName;
                }
                url += "&page=" + i;
                imgList.push({
                    href : opts.basePath + api + url,
                    title : xhr.fileName + "(" + i + "/" + pageCount + ")"
                });
            }
            openFancyBox(imgList);
        }]);
    };
    
    this.viewImage = function(opts){
        var imgList = [{
           href: opts.url,
           title: opts.fileName
        }];
        openFancyBox(imgList);
    };
};

window._imageViewer = new ImageViewer();

/**
 * Created by 890157 on 2016/4/7.
 */

(function(){
    var CONST = {
        FILE_STATE: {
            IN_QUEUE: 1,
            TRANSFORMING: 2,
            SUCCESS: 3,
            ERROR_TRANSFORM: -1,
            ERROR_FETCH: -2
        },
        FILE_TYPE: {
            NORMAL: 1,
            ARCHIVE: 2,
            UNSUPPORTED: 3,
            FOLDER: 4
        }
    };

    var CONST_CH = {
        FILE_STATE : {
            "1": "等待转换",
            "2": "转换中",
            "3": "转换成功",
            "-1": "转换失败",
            "-2": "抓取失败"
        },
        FILE_TYPE: {
            1: "普通文件",
            2: "归档文件",
            3: "无法转换",
            4: "文件夹"
        }
    }
    window.getEnum = function(group, key){
        var enumGroup = CONST[group];
        if(!enumGroup){
            return null;
        }
        return enumGroup[key] || null;
    };

    window.enum2ch = function(group, val, defText){
        var enumGroup = CONST_CH[group]
        if(!enumGroup){
            return defText || "";
        }
        return enumGroup[val + ""] || defText || "";
    };
})();
