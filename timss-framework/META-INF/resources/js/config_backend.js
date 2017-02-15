opts = {};
opts.tabs = [
    {"name":"系统管理","id":"nav1"}
];

var treeData = [
    {
        "grouptitle" : "权限管理",
        "items" : [
            {"title":"用户管理", "id":"nav1_11"},
            {"title":"用户组管理","id":"nav1_12"},
            {"title":"角色管理","id":"nav1_13"}
        ]
    }
];

opts.tabMapping = {
    "nav1":{
        "cache":true,
        "tree":treeData,
        "id":"iframe_sys"
    }
};

opts.treeMapping = {
    "nav1_11":basePath + "jsp/user_management.jsp",
    "nav1_12":basePath + "jsp/group_management.jsp",
    "nav1_13":basePath + "jsp/role_management.jsp"
};