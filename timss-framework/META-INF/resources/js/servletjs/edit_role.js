var currSearchUser = null;
var currExpandNode = null;
var expandArr = null;
var privToAdd = {};
$(document).ready(function(){
	respond.update();
	var form=[
	  		{
	  			title : "角色ID", 
	  			id : "rid",
	  			type:mode=="create"?"text":"label",
	  			rules : {
	  				required : true,
	  				maxlength : 30,
	  				remote : {
			            "url": basePath + "role?method=exist",
			            type: "post",
			            data: {
			            	rid: function() {
			            		return $("#f_rid").val();
			            	}
			            }
	  				}
	  			}
  			},
	  		{
  				title : "角色名",
  				id : "name",
  				rules : {
	  				required : true,
	  				maxlength : 30
	  			}
  			},
	  		{title : "最后修改人", id : "lastmodifyuser",type:"label",nouse:mode=="create"},
	  		{title : "最后修改时间", id : "lastmodify",type:"label",nouse:mode=="create"},
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
	$("#role_form").ITC_Form({fixLabelWidth:true,validate:true},form);
	$("#roleUser").ITCUI_Foldable();
	$("#roleFunc").ITCUI_Foldable();
	if(mode=="edit"){
		$("#role_form").ITC_Form("loaddata",g);
		$("#role_form").ITC_Form("readonly");
	}
	else{
		initBtns();
	}
	initUserTree(mode);
	initPrivTree(mode);

});

function initBtns(){
	$("#btnDel").hide();
	$("#btnUser").show();
	$("#btnGroup").show();
	$("#btnPriv").show();
	$("#btnEdit").hide();
	$("#btnSave").show();
}

function beginEdit(){
	initBtns();
	$("#role_form").ITC_Form("beginedit");
	initPrivTree("create");
}


function initPrivTree(mode){
	if(mode=="edit"){
		if(objLen(g.privs)==0){
			$("#roleFunc").ITCUI_Foldable("hide");
		}
		else{
			$("#roleFunc").ITCUI_Foldable("show");
		}			
	}
	else{
		$("#roleFunc").ITCUI_Foldable("show");
	}
	$.ajax({
		url : basePath + "tree?method=privend",
		data : {
			filter : JSON.stringify(g.privs),
		},
		type:"POST",
		dataType:"JSON",
		success : function(data){
			var opts = {"data":data};			
			$("#privTree").tree(opts);
		}
	});	
}

function checkInher(node){
	if(node.id.indexOf("user_")>=0){
		var id = node.id.replace("user_","");
		if(g.users[id]){
			if(g.users[id].indexOf("(继承)")>0){
				node.text += "(继承)";
			}
		}
	}
	if(node.children){
		for(var i=0;i<node.children.length;i++){
			checkInher(node.children[i]);
		}
	}
}

function initUserTree(mode){
	if(mode=="edit"){
		if(objLen(g.users)==0){
			$("#roleUser").ITCUI_Foldable("hide");
		}
		else{
			$("#roleUser").ITCUI_Foldable("show");
		}
	}
	else{
		$("#roleUser").ITCUI_Foldable("show");
	}
	$.ajax({
		url : basePath + "tree?method=extendall",
		data : {
			orgFilter : JSON.stringify(g.relatedorgs),
			personFilter : JSON.stringify(g.users)
		},
		type:"POST",
		dataType:"JSON",
		success : function(data){
			for(var i=0;i<data.length;i++){
				checkInher(data[i]);
			}
			var opts = {
				"data":data
			};			
			$("#userTree").tree(opts);
		}
	});	
}

function save(){
	if(!$("#role_form").valid()){
		return;
	}
	$("#btnSave").children("button").attr('disabled','disabled');
	f = $("#role_form").ITC_Form("getdata");
	diffForm(f,"groups");
	diffForm(f,"users");
	diffForm(f,"privs");
	if(mode!="create"){
		f["rid"] = g.rid; 
	}
	$.ajax({
		url : "role?method=" + mode,
		data : f,
		type : "post",
		dataType : "json",
		success : function(data){
			if(data.status==1){
				_parent().Notice.successTopNotice(data.msg);
				window.location.href = basePath + jspPath + "role_management.jsp";
			}
			else{
				_parent().Notice.errorTopNotice(data.msg);
			}
		}
	});
}

function selectpriv(){
	var src =  basePath + jspPath + "select_role_func.jsp";
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
            	var p = _parent().window.document.getElementById(_c.FRAME_DLG).contentWindow;
            	g.privs = p.getChecked();
            	initPrivTree("create");
            	return true;
            }
        }
    ];
    var dlgOpts = {
        width : 450,
        height: 400,
        closed : false,
        title:"选择该角色拥有的权限",
        modal:true
    };
    Notice.dialog(src,dlgOpts,btnOpts);
}

function selectuser(){
	var src =  basePath + jspPath + "select_role_user.jsp?from=role";
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
            	var p = _parent().window.document.getElementById(_c.FRAME_DLG).contentWindow;
            	g.users = p.getChecked();
            	var lst = [];
            	for(var k in g.users){
            		lst.push(k);
            	}
            	$.ajax({
            		url : basePath + "role?method=userorg",
            		dataType : "json",
            		type : "post",
            		data : {
            			"ids" : lst.join(",")
            		},
            		success : function(data){
            			g.relatedorgs = data;
            			initUserTree("create");            			
            			_parent().$("#itcDlg").dialog("close");
            		}
            	});            	            	
            }
        }
    ];
    var dlgOpts = {
        width : 450,
        height: 400,
        closed : false,
        title:"选择拥有该角色的用户",
        modal:true
    };
    Notice.dialog(src,dlgOpts,btnOpts);
}

function selectgroup(){
	var src =  basePath + jspPath + "select_role_group.jsp?from=role";
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
            	var p = _parent().window.document.getElementById(_c.FRAME_DLG).contentWindow;
            	g.groups = p.getChecked();
            	$("#role_form").ITC_Form("loaddata",g);
            	return true;
            }
        }
    ];
    var dlgOpts = {
        width : 450,
        height: 400,
        closed : false,
        title:"选择包含该角色的用户组",
        modal:true
    };
    Notice.dialog(src,dlgOpts,btnOpts);
}

function delRole(){
	Notice.confirm("确认删除|是否要删除角色“" + g.name + "”？该操作无法撤销。",function(){
		$.ajax({
			url : basePath + "role?method=delroles&rids=" + g.rid,
			dataType : "json",
			type : "post",
			success : function(data){
				if(data.status==1){
					_parent().Notice.successTopNotice(data.msg);
					window.location.href = basePath + jspPath + "role_management.jsp";
				}
				else{
					_parent().Notice.errorTopNotice(data.msg);
				}
			}
		});
	});
}

function goBack(){
	window.location.href = basePath + jspPath + "role_management.jsp";
}