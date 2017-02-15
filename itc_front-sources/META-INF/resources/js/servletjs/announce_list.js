(function(){
    var columns = [{
        field: "itemId",
        hidden: true,
        title: "itemId"
    }, {
        field: "itemDesc",
        width: 200,
        title: "通知标题"
    },{
        field: "sites",
        width: 150,
        title: "有效站点",
        fixed: true
    },{
        field: "expireAt",
        title: "截止时间",
        width: 150,
        fixed: true,
        formatter: function(val){
            return FW.long2time(val);
        }
    }];
    $(document).ready(function(){
        $("#btnNew").on("click", function(){
            location.href = basePath + "announce?method=detailPage&id=0";
        });
        $("#table_announce").iDatagrid("init",{
            columns: [columns],
            url: basePath + "announce?method=getList",
            onDblClickRow : function(rowIndex, rowData) {
                location.href = basePath + "announce?method=detailPage&id=" + rowData.itemId
            }
        });
    });
})();