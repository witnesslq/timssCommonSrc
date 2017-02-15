<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <jsp:include page="/page/sysconf/auth_include.jsp" flush="false" />
    <script type="text/javascript" src="${basePath}${resBase}ueditor/ueditor.config.js"></script>
    <script type="text/javascript" src="${basePath}${resBase}ueditor/ueditor.all.min.js"></script>
    <script type="text/javascript" src="${basePath}${resBase}js/servletjs/edit_announce.js"></script>
    <script>var id = ${id};</script>
    <title>编辑公告</title>
</head>
<body>
    <div class="toolbar-with-pager btn-toolbar">
        <div class="btn-group btn-group-sm">
            <button class="btn-default btn" id="btnBack">返回</button>
        </div>
        <div class="btn-group btn-group-sm">
            <button class="btn-success btn" id="btnSave">保存</button>
        </div>
        <div class="btn-group btn-group-sm">
            <button class="btn-default btn" id="btnDel">删除</button>
        </div>
    </div>
    <div class="inner-title">编辑公告</div>
    <div id="baseInfo" style="margin:10px 0">
        <form id="announce_form">
        </form>
    </div>
    <script type="text/plain" id="editor"></script>
</body>
</html>
