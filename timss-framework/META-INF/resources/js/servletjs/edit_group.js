var currNode = null;
var currSearchUser = null;
var currExpandNode = null;
var expandArr = null;
$(document).ready(function(){
	respond.update();
	var form=[
	  		{
	  			title : "用户组ID",
	  			id : "gid",
	  			type:mode=="create"?"text":"label",
	  			rules : {
	  				required : true,
	  				maxlength : 30,
	  				remote : {
			            "url": basePath + "group?method=exist",
			            type: "post",
			            data: {
			            	gid: function() {
			            		return $("#f_gid").val();
			            	}
			            }
			        }
	  			}
	  		},
	  		{
	  			title : "用户组名", 
	  			id : "name",
	  			rules : {
	  				required : true,
	  				maxlength : 30
	  			}
	  		},
	  		{title : "最后修改人", id : "lastmodifyuser",type:"label",nouse:mode=="create"},
	  		{title : "最后修改时间", id : "lastmodify",type:"label",nouse:mode=="create"},
	  		{
				title : "拥有角色",
				type : "label",
				id : "roles",
				linebreak:true,
				formatter : joinTags,
				wrapXsWidth : 12,
				wrapMdWidth : 12,
				breakAll:true
			}
	];
	$("#group_form").ITC_Form({fixLabelWidth:true,validate:true},form);
	$("#groupUser").ITCUI_Foldable();
	if(mode=="create"){
		initTree();
		initBtn();
	}
	else{
		$("#btnSave").hide();
		$("#group_form").ITC_Form("loaddata",g);		
		initTree();
		$("#group_form").ITC_Form("readonly");
	}
});

function selectuser(){
	var src =  basePath + jspPath + "select_role_user.jsp?from=group";
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
            			initTree("edit");            			
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
        title:"选择用户组包含的用户",
        modal:true
    };
    Notice.dialog(src,dlgOpts,btnOpts);
}

function initBtn(){
	$("#btnSave").show();
	$("#btnEdit").hide();
	$("#btnDel").hide();
	$("#btnUser").show();
	$("#btnRole").show();
}

function selectrole(){
	var src =  basePath + jspPath + "select_user_role.jsp?from=group";
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
            	g.roles = p.getChecked();
            	$("#group_form").ITC_Form("loaddata",g);
            	return true;
            }
        }
    ];
    var dlgOpts = {
        width : 450,
        height: 400,
        closed : false,
        title:"选择用户组拥有的角色",
        modal:true
    };
    Notice.dialog(src,dlgOpts,btnOpts);
}

function beginEdit(){
	initBtn();
	initTree("edit");
	$("#group_form").ITC_Form("beginedit");	
}

function selectAllNode(select){
	$.ajax({
		url : basePath + "tree?method=subusers&id=" + currNode.id.replace("org_",""),
		dataType : "json",
		success : function(data){
			for(var i=0;i<data.length;i++){
				var o = data[i].split(",");
				var uid = o[0];
				var name = o[1];
				//找到真实节点 如果树已经加载了部分
				var realNode = $("#userTree").tree("find","user_" + uid);
				if(realNode){
					if(select){
						$("#userTree").tree("check",realNode.target);
					}
					else{
						$("#userTree").tree("uncheck",realNode.target);
					}
				}
				if(select){
					g.users[uid] = name;
				}
				else{
					delete(g.users[uid]);
				}
			}
		}
	});
}

function initTree(method){
	if(!method){
		if(objLen(g.users)==0){
			$("#groupUser").ITCUI_Foldable("hide");
		}
	}
	else if(method=="edit"){
		$("#groupUser").ITCUI_Foldable("show");
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
			var opts = {"data":data};			
			$("#userTree").tree(opts);
		}
	});
}

function save(){
	if(!$("#group_form").valid()){
		return;
	}
	f = $("#group_form").ITC_Form("getdata");
	diffForm(f,"roles");
	diffForm(f,"users");
	if(mode!="create"){
		f["gid"] = g.gid; 
	}
	$.ajax({
		url : "group?method=" + mode,
		data : f,
		type : "post",
		dataType : "json",
		success : function(data){
			if(data.status==1){
				_parent().Notice.successTopNotice(data.msg);
				window.location.href = basePath + jspPath + "group_management.jsp";
			}
			else{
				_parent().Notice.errorTopNotice(data.msg);
			}
		}
	});
}

function goBack(){
	window.location.href = basePath + jspPath + "group_management.jsp";
}

function delGroup(){
	Notice.confirm("确认删除|是否要删除用户组“" + g.name + "”？该操作无法撤销。",function(){
		$.ajax({
			url : basePath + "group?method=delgroups&gids=" + g.gid,
			dataType : "json",
			type : "post",
			success : function(data){
				if(data.status==1){
					_parent().Notice.successTopNotice(data.msg);
					window.location.href = basePath + jspPath + "group_management.jsp";
				}
				else{
					_parent().Notice.errorTopNotice(data.msg);
				}
			}
		});
	});
}