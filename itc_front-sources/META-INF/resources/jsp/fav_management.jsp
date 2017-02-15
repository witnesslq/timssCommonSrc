<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <jsp:include page="/page/sysconf/auth_include.jsp" flush="false" />
    <script type="text/javascript" src="${basePath}res?f=route.js"></script>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>管理收藏夹</title>
    <style>
        body{
            padding-top: 6px;
            padding-left: 6px!important;
            padding-right: 6px!important;
            overflow: hidden;
        }
        .tree-wrap, .fav-wrap{
            float: left;
            overflow: hidden;
            height: 305px;
        }
        .tree-wrap{
            width: 200px;
            overflow-y: auto;
            border-right: 1px solid #999;
            padding-right: 6px;
        }
        .fav-wrap{
            width: 370px;
            position: relative;
        }
        #no_item_hint{
            text-align: center;
            position: absolute;
            width: 100%;
            top: 50%;
            left: 0;
            margin-top: -24px;
            line-height: 24px;
            color: #666
        }
        #tag_list{
            padding-left: 8px;
        }
    </style>
</head>
<body>
    <div class="tree-wrap">
        <ul id="route_tree">

        </ul>
    </div>
    <div class="fav-wrap">
        <div id="no_item_hint" style="display: none">
            <span>您还没有收藏任何功能<br>双击左侧树中节点可以收藏对应功能</span>
        </div>
        <div id="tag_list">

        </div>
    </div>
    <script>
        var originalData = {};
        function getRandId(){
            return Math.abs(Math.floor(Math.random() * 100000));
        }
        function buildTree(){
            //构建第一层根节点 用的就是分类
            var rootNodes = {};
            for(var i=0; i<_route.length;i++){
                var group = _route[i].treeGroup;
                if(!rootNodes[group]){
                    var grpNode = {
                        children: [],
                        iconCls: "",
                        state: "closed",
                        text: group,
                        id: "grp_" + getRandId()
                    }
                    rootNodes[group] = grpNode;
                }
            }
            for(var i=0; i<_route.length;i++){
                var item = _route[i];
                var node = {
                    text: item.title,
                    id: "node_" + item.routeId
                };
                rootNodes[item.treeGroup].children.push(node);
            }
            var treeData = [];
            for(var k in rootNodes){
                treeData.push(rootNodes[k]);
            }
            $("#route_tree").tree({
                data: treeData,
                onDblClick: function(node){
                    if(node.id.indexOf("grp") === 0){
                        return;
                    }
                    $("#tag_list").iTags("append", {
                        id: node.id.substring(5),
                        title: node.text
                    });
                }
            });
        }

        function loadFav(){
            $.ajax({
                url: basePath + "user?method=getfavroute&rand=" + Math.random(),
                dataType: "json",
                method: "get",
                success: function(xhr){
                    if(!xhr || !xhr.data){
                        FW.error("加载已收藏功能失败");
                        return;
                    }
                    if(!xhr.data.length){
                        $("#no_item_hint").show();
                    }else{
                        $("#no_item_hint").hide();
                        for(var i=0; i<xhr.data.length;i++){
                            var item = xhr.data[i];
                            item.id = item.routeId;
                            originalData[item.id] = true;
                        }
                    }
                    $("#tag_list").iTags("init", {data: xhr.data})
                }
            });
        }

        window.saveFav = function(callback){
            //通过比对现在结果和
            var data = $("#tag_list").iTags("getVal");
            var currData = {};
            for(var i=0; i<data.length; i++){
                var id = data[i].id;
                currData[id] = true;
            }
            var toRemove = [];
            var toAdd = [];
            for(var k in currData){
                if(!originalData[k]){
                    toAdd.push(k);
                }
            }
            for(var k in originalData){
                if(!currData[k]){
                    toRemove.push(k);
                }
            }
            $.ajax({
                url: basePath + "user?method=editfavroute",
                method: "post",
                dataType: "json",
                data: {
                    toAdd: toAdd.join(","),
                    toRemove: toRemove.join(",")
                },
                success: function(xhr){
                    callback(xhr);
                }
            })
        };

        $(document).ready(function(){
            buildTree();
            loadFav();
        });
    </script>
</body>
</html>