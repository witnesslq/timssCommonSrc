(function(){
    window.UEDITOR_HOME_URL = basePath + "ueditor/";
    var ue = null;
    var fields = [{
        title: "描述",
        id: "itemDesc",
        rules: {
            required: true
        }
    },{
        title: "截止时间",
        id: "expireAt",
        type: "datetime",
        dataType: "datetime",
        rules: {
            required: true
        }
    },{
        title: "目标站点",
        id: "sites",
        rules: {
            required: true
        }
    },{
        title: "窗口宽度",
        id: "popupW",
        rules: {
            required: true
        }
    },{
        title: "窗口高度",
        id: "popupH",
        rules: {
            required: true
        }
    }];

    function saveAnnounce(){
        var data = $("#announce_form").iForm("getVal");
        data.content = ue.getContent();
        //注意这里data.sites必须是,ABC,DEF,的形式 逗号
        var sites = data.sites;
        if(sites !== "*"){
            if(sites.charAt(0) !== ","){
                sites = "," + sites;
            }
            if(sites.charAt(sites.length - 1) !== ","){
                sites += ",";
            }
        }
        data.sites = sites;
        data.itemId = id;
        $.ajax({
            url: basePath + "announce?method=upsert",
            data: data,
            dataType: "json",
            type: "post",
            success: function(xhr){
                if(xhr.status && xhr.status > 0){
                    FW.success("公告修改成功");
                    location.href = basePath + "announce?method=listPage";
                }
            }
        });
    }

    $(document).ready(function(){
        window.UEDITOR_CONFIG.initialFrameWidth = $("body").width() * 0.95;
        $("#announce_form").iForm("init", {fields: fields});
        ue = UE.getEditor('editor',{
            initialFrameHeight: 400,
            autoHeightEnabled: false
        });
        $("#btnSave").on("click", saveAnnounce);
        $("#btnBack").on("click", function(){
            location.href = basePath + "announce?method=listPage";
        });
        $("#btnDel").on("click", function(){
            FW.confirm("确认删除该公告？", function(){
                $.ajax({
                    url: basePath + "announce?method=delete",
                    data: {
                        id: id
                    },
                    dataType: "json",
                    success: function(xhr){
                        if(xhr && xhr.status > 0){
                            FW.success("公告已删除");
                            location.href = basePath + "announce?method=listPage"
                        }
                    }
                });
            });
        });
        if(id === 0){
            $("#announce_form").iForm("setVal", {
                sites: "*",
                popupW: "80%",
                popupH: "80%"
            });
            $("#btnDel").parent().hide();
        }else if(id > 0){
            $.ajax({
                url: basePath + "announce?method=getDetail&id=" + id + "&_rand=" + Math.random(),
                dataType: "json",
                success: function(xhr){
                    var sites = xhr.data.sites;
                    if(sites !== "*"){
                        sites = sites.substring(1, sites.length - 1);
                    }
                    xhr.data.sites = sites;
                    $("#announce_form").iForm("setVal", xhr.data);
                    ue.ready(function(){
                        ue.setContent(xhr.data.content);
                    });
                }
            })
        }
    });
})()