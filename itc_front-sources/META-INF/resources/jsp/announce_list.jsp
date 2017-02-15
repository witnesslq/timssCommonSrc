<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <jsp:include page="/page/sysconf/auth_include.jsp" flush="false" />
    <script type="text/javascript" src="${basePath}${resBase}js/servletjs/announce_list.js"></script>
    <title>通知列表</title>
</head>
<body class="list-page">
<div class="toolbar-with-pager bbox">
    <div class="btn-toolbar" role="toolbar">
        <div class="btn-group btn-group-sm">
            <button type="button" class="btn btn-success" id="btnNew">新建</button>
        </div>
        <div id="pager" style="float:right;width:200px" bottompager="#bottomPager"></div>
    </div>
</div>
<div style="clear:both"></div>
<table id="table_announce" class="eu-datagrid">

</table>
</body>
</html>
