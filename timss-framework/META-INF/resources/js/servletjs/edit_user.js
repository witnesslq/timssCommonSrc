var form = [];
$(document).ready(function(){
	respond.update();
	form= [
		{
			title : "工号",
			id : "uid",type:mode=="create"?"text":"label",
			rules : {
				required : true,
				alphanumeric : true,
				maxlength : 20,
				remote : {
		            "url": basePath + "user?method=exist",
		            type: "post",
		            data: {
		            	uid: function() {
		            		return $("#f_uid").val();
		            	}
		            }
		        }
			}
		},
		{title : "姓名",id : "name",rules : {	required : true,maxlength : 10}},
		{title : "职务",id : "job",rules : {maxlength : 128}},
		/*{title : "职位",id : "title",	rules : {maxlength : 30}},*/
		{title : "电子邮箱",id : "email",rules : {email : true,maxlength : 50}},
		{title : "手机号码",id : "mobile",	rules : {maxlength : 30}},
		{title : "办公电话",id : "officetel",rules : {maxlength : 30}},
		{title : "微波电话", id : "microtel",	rules : {maxlength : 20}},
		{
			title : "账户类型", 
			id : "type",
			type:"label",
			nouse : mode=="create",
			formatter : function(val){
				return val=="YES"?"域账号":"系统账号";
			}
		},
		{
			title : "状态", 
			id : "status",
			type : "label",
			nouse : mode=="create",
			formatter : function(val){
				return val=="YES"?"启用":"<span style='color:#f00'>禁用</span>";
			}
		},
		{
			title : "公司/部门",
			type : "label",
			id : "orgs",
			linebreak:true,
			formatter : joinTags,
			wrapXsWidth : 12,
			wrapMdWidth : 12
		},
		{
			title : "角色",
			type : "label",
			id : "roles",
			linebreak:true,
			formatter : joinTags,
			wrapXsWidth : 12,
			wrapMdWidth : 12,
			breakAll:true
		}
		,
		{
			title : "用户组",
			type : "label",
			id : "groups",
			linebreak:true,
			formatter : joinTags,
			wrapXsWidth : 12,
			wrapMdWidth : 12,
			breakAll:true
		}
	];
	
	$("#form_baseinfo").ITC_Form({fixLabelWidth:true,validate:true},form);
	if(mode!="create"){
		$("#form_baseinfo").ITC_Form("loaddata",g);
		$("#form_baseinfo").ITC_Form("readonly");
	}
	else{
		showEditBtn();
	}
	if(!g.status || g.status=='NO'){
		$("#btnDisable").html("启用");
	}
	if(g.type=="YES"){
		$("#btnResetPass").hide();
	}
});

function save(){
	if(objLen(g.orgs)==0){
		_parent().Notice.errorTopNotice("至少要选择一个组织机构");
		return;
	}
	if(objLen(g.roles)==0 && objLen(g.groups)==0){
		_parent().Notice.errorTopNotice("角色和用户组不能都为空，无权限的用户将无法登入系统");
		return;
	}
	if(!$("#form_baseinfo").valid()){
		return;
	}
	var toDiff = ["orgs","groups","roles"];
	f = $("#form_baseinfo").ITC_Form("getdata");
	for(var i=0;i<toDiff.length;i++){
		diffForm(f,toDiff[i]);
	}
	if(mode!="create"){
		f["uid"] = g.uid; 
	}
	$.ajax({
		url : basePath + "user?method=" + mode,
		data : f,
		type : "post",
		dataType : "json",
		success : function(data){
			if(data.status==1){
				_parent().Notice.successTopNotice(data.msg);
				goBack();
			}
			else{
				_parent().Notice.errorTopNotice(data.msg);
			}
		}
	});
}

function goBack(){
	window.location.href = basePath + jspPath + "user_list.jsp";
}

function delUser(){
	Notice.confirm("确认删除|是否要删除用户“" + g.name + "”？该操作无法撤销。",function(){
		$.ajax({
			url : basePath + "user?method=deluser&uid=" + g.uid,
			dataType : "json",
			type : "post",
			success : function(data){
				if(data.status==1){
					_parent().Notice.successTopNotice(data.msg);
					window.location.href = basePath + jspPath + "user_list.jsp";
				}
				else{
					_parent().Notice.errorTopNotice(data.msg);
				}
			}
		});
	});
}

function beignEdit(){
	$("#form_baseinfo").ITC_Form("beginedit");
	showEditBtn();
}

function showEditBtn(){
	$("#btnEdit").hide();
	$("#btnSave").show();
	$("#btnResetPass").hide();
	$("#btnDisable").hide();
	$("#btnDel").hide();
	$("#btnOrg").show();
	$("#btnRole").show();
	$("#btnGroup").show();
}

function selectrole(){
	var src =  basePath + jspPath + "select_user_role.jsp?from=user";
    var btnOpts = [{
            "name" : "取消",
            "float" : "right",
            "style" : "btn-default",
            "onclick" : function(){
                return true;
            }
        },{
            "name" : "确定",
            "float" : "right",
            "style" : "btn-success",
            "onclick" : function(){
            	var p = _parent()._parent().window.document.getElementById(_c.FRAME_DLG).contentWindow;
            	g.roles = p.getChecked();
            	$("#form_baseinfo").ITC_Form("loaddata",g);
            	return true;
            }
        }
    ];
    var dlgOpts = {
        width : 450,
        height: 400,
        closed : false,
        title:"选择用户拥有的角色",
        modal:true
    };
    _parent().Notice.dialog(src,dlgOpts,btnOpts);
}

function setUserStat(){
	var stat = null;
	var pstat = 1;
	if(g.status=="YES"){
		stat = "NO";
		pstat = 0;
	}
	else{
		stat = "YES";
		pstat = 1;
	}
	$.ajax({
		url : basePath + "user?method=setstat&ids=" + g.uid + "&stat=" + pstat,
		dataType : "json",
		success : function(data){
			if(data.status==1){
				g.status = stat;
				var btn = (stat=="YES")?"禁用":"启用";
				var msg = (stat=="YES")?"启用":"禁用";
				var newVal = (stat=="YES")?"YES":"NO";
				$("#btnDisable").html(btn);
				_parent().Notice.successTopNotice(msg + "用户成功");
				$("#form_baseinfo").ITC_Form("setfieldval",form[8],newVal);
			}
		}
	});
}

function selectorg(){
	var src =  basePath + jspPath + "select_user_org.jsp?from=user";
    var btnOpts = [{
            "name" : "取消",
            "float" : "right",
            "style" : "btn-default",
            "onclick" : function(){
                return true;
            }
        },{
            "name" : "确定",
            "float" : "right",
            "style" : "btn-success",
            "onclick" : function(){
            	var p = _parent()._parent().window.document.getElementById(_c.FRAME_DLG).contentWindow;
            	g.orgs = p.getChecked();
            	$("#form_baseinfo").ITC_Form("loaddata",g);
            	return true;
            }
        }
    ];
    var dlgOpts = {
        width : 450,
        height:400,
        closed : false,
        title:"选择用户属于的组织机构",
        modal:true
    };
    _parent().Notice.dialog(src,dlgOpts,btnOpts);
}

function showEditPass(){
	var src =  basePath + jspPath + "edit_pass.jsp?id=" + g.uid;
    var btnOpts = [{
            "name" : "取消",
            "float" : "right",
            "style" : "btn-default",
            "onclick" : function(){
                return true;
            }
        },{
            "name" : "确定",
            "float" : "right",
            "style" : "btn-success",
            "onclick" : function(){
            	var p = _parent()._parent().window.document.getElementById(_c.FRAME_DLG).contentWindow;
            	var form = p.go();
            	if(!form){
            		return false;
            	}
            	$.ajax({
            		url : basePath + "user?method=editpswd",
            		data : {
            			password : form["password"],
            			id : g.uid
            		},
            		type : "POST",
            		dataType : "json",
            		success : function(data){
            			if(data.status==1){
            				_parent()._parent().Notice.successTopNotice("密码修改成功");
            			}
            			else{
            				_parent()._parent().Notice.errorTopNotice(data.msg);
            			}
            			_parent()._parent().$("#itcDlg").dialog("close");
            		}
            	})
            	return true;
            }
        }
    ];
    var dlgOpts = {
        width : 450,
        height:200,
        closed : false,
        title:"修改密码",
        modal:true
    };
    _parent().Notice.dialog(src,dlgOpts,btnOpts);
}

function selectgroup(){
	var src =  basePath + jspPath + "select_role_group.jsp?from=user";
    var btnOpts = [{
            "name" : "取消",
            "float" : "right",
            "style" : "btn-default",
            "onclick" : function(){
                return true;
            }
        },{
            "name" : "确定",
            "float" : "right",
            "style" : "btn-success",
            "onclick" : function(){
            	var p = _parent()._parent().window.document.getElementById(_c.FRAME_DLG).contentWindow;
            	g.groups = p.getChecked();
            	$("#form_baseinfo").ITC_Form("loaddata",g);
            	return true;
            }
        }
    ];
    var dlgOpts = {
        width : 450,
        height:400,
        closed : false,
        title:"选择用户属于的用户组",
        modal:true
    };
    _parent().Notice.dialog(src,dlgOpts,btnOpts);
}
