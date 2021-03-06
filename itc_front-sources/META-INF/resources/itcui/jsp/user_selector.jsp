<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.yudean.itc.code.ParamConfig"%>
<%@ page import="com.yudean.itc.util.Constant" %>
<%
 	String basePath = (String)request.getAttribute(ParamConfig.S_BasePathKey);	
	String resBase = Constant.resBase;
	String single = request.getParameter("single");//是否只允许单选
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<script type="text/javascript" src="<%=basePath%><%=resBase %>js/jquery-1.10.2.js"></script>
<script type="text/javascript" src="<%=basePath%><%=resBase %>js/servletjs/const.js"></script>
<script type="text/javascript" src="<%=basePath%><%=resBase %>itcui/js/itcui.dev.js"></script>
<link rel="stylesheet" type="text/css"
	href="<%=basePath%><%=resBase %>itcui/css/itcui.dev.css" media="all" />
<link rel="stylesheet" type="text/css"
	href="<%=basePath%><%=resBase %>css/public_background.css" media="all" />	
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>选择角色中包含的用户</title>
<script>
	var currSearchUser = null;
	var currExpandNode = null;
	var currNode = null;
	var expandArr = null;
	var chkUsers = {};
	var basePath = "<%=basePath%>";
	var single = <%=single%>;
	var p;
	$(document).ready(function(){
		p = typeof(FW.getCurrentTabId)!="undefined"?FW.getFrame(FW.getCurrentTabId()):_parent();
		if(p._userFilterFunc && typeof(p._userFilterFunc)=="function"){
			//这里的对话框不限于从选项卡中弹出 也可以是任意页面			
			chkUsers = p._userFilterFunc();
		}
		initTree();
		initHintList();			
	});
	
	function initTree(){
		var opts = {
			url : basePath + "tree?method=org&onelevel=true"	
		};
		opts["onContextMenu"] = function(e, node){
			e.preventDefault();
			if(node.id.indexOf("org_")>=0){
				$("#menu_tree").css({
					left:e.pageX,
					top:e.pageY
				});
				$("#menu_tree_toggle").dropdown("toggle");
				currNode = node;
			}
		};
		if(!single){
			opts["checkbox"] = true;
			opts["onlyLeafCheck"] = true;
			opts["loadFilter"] = function(data,parent){
				for(var i=0;i<data.length;i++){
					var node = data[i];
					if(node.id.indexOf("user_")>=0){
						var uid = node.id.replace('user_',"");
						if(chkUsers[uid]){
							node.checked = true;
						}
					}				
				}
				return data;
			};
		}
		else{
			opts["onSelect"] = function(node){
				var id = node.id;
				if(node.id.indexOf('user_')==0){
					id = id.replace("user_","");
					chkUsers[id] = node.text;
				}
			}
			opts["onDblClick"] = function(node){
				var id = node.id;
				if(node.id.indexOf('user_')==0){
					id = id.replace("user_","");
					var obj = {};
					obj[id] = node.text;
					p._us_onselectfunc(obj);
					_parent().$("#itcDlgOrgPerson").dialog("close");
				}
			}
		}
		opts["onExpand"] = function(node){
			if(expandArr!=null && expandArr.length>0){
				var id = expandArr.pop();
				var node = $("#userTree").tree("find","org_" + id);
				$("#userTree").tree("expand",node.target);
			}
			else if(currSearchUser!=null){
				var node = $("#userTree").tree("find","user_" + currSearchUser);
				currSearchUser = null;
				$("#userTree").tree("select",node.target);
			}
		};
		if(!single){
			opts["onCheck"] = function(node, checked){
				var id = node.id.replace("user_","");
				if(checked){
					chkUsers[id] = node.text;
				}
				else{
					delete(chkUsers[id]);
				}
			};
		}
		$("#userTree").tree(opts);
	}
	
	function getChecked(){
		return chkUsers;
	}	
	
	function initHintList(){
		$("#searchUserWrap").show();
		$("#hintUser").ITCUI_HintList({
			"datasource":basePath + "user?method=hint",
			"getDataOnKeyPress":true,
			"clickEvent":function(id,name){
				var arr = id.split("_");
				$.ajax({
					url : basePath + "user?method=searchorg&id=" + arr[1],
					dataType:"json",
					success : function(data){
						$("#userTree").tree("collapseAll");
						expandArr = data;
						currSearchUser = arr[0];
						var id = expandArr.pop();
						var node = $("#userTree").tree("find","org_" + id);
						$("#userTree").tree("expand",node.target);
					}
				});
			},
			"showOn":"input",
			"highlight":true
		});
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
						chkUsers[uid] = name;
					}
					else{
						delete(chkUsers[uid]);
					}
				}
			}
		});
	}
</script>
<style>
	
</style>
</head>
<body style="padding:6px">
	<span style="font-size:12px">提示：可以使用右键选择某机构下的所有用户</span>
	<div id="searchUserWrap" style="margin-top:4px;width:100%" class="bbox">
		<label class="ctrl-label pull-left" style="width:60px;">搜索用户：</label>
		<div class="input-group-sm pull-left">
		    <input type="text" class="form-control" style="width:200px" id="hintUser">
		</div>
	</div>
	<div style="width:100%;height:270px;margin-top:4px;overflow-y:auto" id="userTree">

	</div>
	<div class="dropdown" id="menu_tree" style="position:absolute">
		<a data-toggle="dropdown" id="menu_tree_toggle"></a>
		<ul class="dropdown-menu" role="menu">
			<li><a class="menuitem" onclick="selectAllNode(true)">选择该节点下所有用户</a></li>
			<li><a class="menuitem" onclick="selectAllNode(false)">删除该节点下所有用户</a></li>
		</ul>
	</div>
</body>
</html>