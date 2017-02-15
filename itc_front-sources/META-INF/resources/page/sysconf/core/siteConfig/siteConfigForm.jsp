<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" style="width:100%;height:100%">
<head>
<link rel="shortcut icon" href="${basePath}favicon.ico" type="image/x-icon" />
<title>系统配置表单</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<!-- 由于在本地web工程中，这个路径不存在，会编译异常 -->
<jsp:include page="/page/mvc/mvc_include.jsp" flush="false" />
<script>
var _dialogEmmbed = true;
	var sysConfFormField=[

	{
		id : "id",
		title : "站点id",
		wrapXsWidth : 6,
		wrapMdWidth : 6,
		rules : {
			required : true,
			maxChLength : 66
		}
	}, {
		id : "name",
		title : "站点名称",
		wrapXsWidth : 6,
		wrapMdWidth : 6,
		rules : {
			required : true,
			maxChLength : 330
		}
	}, {
		id : "smsHost",
		title : "短信主机地址",
		wrapXsWidth : 6,
		wrapMdWidth : 6,
		rule : {
			maxChLength : 330
		}
	}, {
		id : "smsName",
		title : "短信用户名",
		wrapXsWidth : 6,
		wrapMdWidth : 6,
		rule : {
			maxChLength : 330
		}
	}, {
		id : "smsPassword",
		title : "密码",
		wrapXsWidth : 6,
		wrapMdWidth : 6,
		rule : {
			maxChLength : 330
		}
	}, {
		id : "smsApiId",
		title : "ApiId",
		wrapXsWidth : 6,
		wrapMdWidth : 6,
		rule : {
			maxChLength : 330
		}
	}, {
		id : "smsDBName",
		title : "DBName",
		wrapXsWidth : 6,
		wrapMdWidth : 6,
		
		rule : {
			maxChLength : 330
		}
	}, {
		id : "smsIsSend",
		title : "是否发送短信",
		type:"radio",
		data : [
               ['N','否',true],
               ['Y','是']
               ],
		wrapXsWidth : 6,
		wrapMdWidth : 6
	}, {
		id : "mailIsSend",
		title : "是否发送邮件",
		type:"radio",
		data : [
               ['N','否',true],
               ['Y','是']
               ],
		wrapXsWidth : 6,
		wrapMdWidth : 6,
		linebreak:true
	} , {
		id : "siteOrg",
		title : "挂接部门",
		type:"combobox",
		wrapXsWidth : 6,
		wrapMdWidth : 6,
		rule : {
			maxChLength : 330
		},
	    options:{
	        url:basePath + "sysconf/siteConfig/getSites.do",
	    	remoteLoadOn:"init",
	    	allowSearch:true,
	    	allowEmpty:true
	    }
	},{
		id : "updatedBy",
		type : "hidden"
	}

	];
	$(document).ready(function() {
		var site = '${site}';
		var orgCode = '${orgCode}';
		$("#form1").iForm("init", {
			fields : sysConfFormField
		});
		//initRemoteField("#form1");
		if(''!= site){
		   $("#form1").iForm("setVal",$.parseJSON(site));
		   $("#form1").iForm("endEdit","id");
		   $("#form1").iForm("setVal",{"siteOrg":orgCode});
		   console.log(site);
		   console.log(orgCode);
		}
	});
	
	/**
 * 初始化从远程数据源获取数据的控件
 */
function initRemoteField($form) {
	var $planInput = $('#f_siteOrg');
	var planInit = {
		datasource : basePath + "sysconf/siteConfig/getSites.do",
		clickEvent : function(id, name) {
			$planInput.val(name);
			$.post(basePath + 'pms/plan/queryPlanById.do', {
				id : id
			}, function(result) {
				result = result.data;
				var values = {
					'planId' : result["id"],
					'planName' : result['planName'],
					'pyear' : result['year'],
					'ptype' : result['type'],
					'property' : result['property'],
					'projectLeader' : result["projectLeader"],
					'customManager' : result['customManager'],
					'startTime' : result['startTime'],
					'endTime' : result['endTime'],
					'command' : result['command']
				};
				$form.iForm("setVal", values);
			});
		}
	};
	$planInput.iHint('init', planInit);
}
</script>
</head>
<body>
	<!--这里要清掉分页器的右浮动效果-->
	<div style="clear:both"></div>
	<input type="hidden" id="oldSysConf"/>
	<form id="form1"  class="margin-form-title margin-form-foldable"></form>
</body>
</html>