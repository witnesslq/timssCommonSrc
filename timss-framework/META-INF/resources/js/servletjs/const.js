var _c = {
	FRAME_USER : "navtab_L-SYS",
	FRAME_ROLE : "navtab_L-SYS",
	FRAME_GROUP : "navtab_L-SYS",
	FRAME_DLG : "itcDlgContent"
};

function genTags(id,mode){
	var gHtml = "";	
	var inherHtml = "";
	if(mode=="edit" || mode=="create"){
		gHtml += '<span class="label label-success" style="cursor:pointer" onclick="select' + id + '()">添加...</span>';
	}
	if(mode!="create"){
		for(var k in g[id + "s"]){
			var name = g[id + "s"][k];
			var tagId = id + "__" + k;
			var delStr = mode=="edit"?"<span class='label_del' onclick='deltag(\"" + tagId + "\")'>×</span>":"";
			var clsStr = name.indexOf("(继承)")>=0?"label-inherit":"label-primary";
			if(name.indexOf("(继承)")>=0){
				inherHtml += '<span class="label ' + clsStr + '" id="' + tagId + '">' + name + '</span>';
			}
			else{
				gHtml += '<span class="label ' + clsStr + '" id="' + tagId + '">' + name + delStr + '</span>';
			}
		}
		gHtml += inherHtml;
	}
	if(gHtml.length>0){
		$("#" + id + "_readonly").html(gHtml).show();
	}
	else{
		$("#" + id + "_readonly").hide();
	}
	if(mode!="edit"){
		$("#" + id + "_info").ITCUI_Foldable();
	}
	else{
		$("#" + id + "_info").ITCUI_Foldable("show");
	}
}

function deltag(id){
	idArr = id.split("__");
	var type = idArr[0];
	var objId = idArr[1];
	delete(g[type + "s"][objId]);
	$("#" + id).remove();
}

function diffForm(f,o){
	f[o + "_add"] = [];
	f[o + "_del"] = [];
	for(var k in _g[o]){
		//原来有现在没有 删除
		if(!g[o][k]){
			f[o + "_del"].push(k);
		}		
	}
	for(var k in g[o]){
		if(!_g[o][k]){
			f[o + "_add"].push(k);
		}
	}
	f[o + "_add"] = f[o + "_add"].join(",");
	f[o + "_del"] = f[o + "_del"].join(",");
}

function objLen(obj){
	var l = 0;
	for(k in obj){
		l++;
	}
	return l;
}

function joinTags(o){
	var l = [];
	var lInh = [];
	for(var k in o){
		if(o[k].indexOf("(继承)")>0){
			lInh.push("<span style='margin-right:12px'>" + o[k] + "</span>");
		}
		else{
			l.push("<span style='margin-right:12px'>" + o[k] + "</span>");
		}
	}
	if(l.length==0 && lInh.length==0){
		return "暂无";
	}
	return l.join("") + lInh.join("");
}
