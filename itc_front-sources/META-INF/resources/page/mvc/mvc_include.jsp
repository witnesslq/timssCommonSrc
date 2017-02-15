<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.lang.Math"%>
<%@ page import="com.yudean.itc.code.ParamConfig"%>
<%@ page import="com.yudean.mvc.util.LogUtil"%>
<%@ page import="com.yudean.itc.util.Constant"%>
<%@ page import="com.yudean.itc.util.UserConfigHelper"%>
<%@ page import="com.yudean.itc.util.UserConfig"%>
<%@ page import="com.yudean.mvc.bean.handler.ThreadLocalVariable"%>
<%@ page import="com.yudean.mvc.handler.ThreadLocalHandler"%>
<%@ page import="com.yudean.mvc.configs.MvcWebConfig"%>
<%@ page import="com.yudean.itc.code.LoadOnAuditJS"%>
<%
	String enums = null;
	try{
		ThreadLocalHandler ThreadlocIns = ThreadLocalHandler.getInstance();
		ThreadLocalVariable ThreadlocData = ThreadlocIns.getVariableIns();
		if(null != ThreadlocIns && ThreadlocData != null){
			enums = (String)ThreadlocData.getThreadLocalAttribute("Frame_Enums_Type");
		}
	}catch(Exception e){
		LogUtil.error("get enums error", e);
	}
 	String path = request.getContextPath();
 	String basePath = (String)request.getAttribute(ParamConfig.S_BasePathKey);
 	String iVersion = (String)request.getAttribute(ParamConfig.S_iVersionKey);
 	String resBase = Constant.resBase;
 	UserConfig conf = (UserConfig)UserConfigHelper.getPagerConfig(request);
  	String pageSize = conf.rows;
  	String theme = conf.theme;
  	double rand = Math.random();
  	request.setAttribute("rand", rand + "");
 %>
<base href="${basePath}">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
<meta name="format-detection" content="telephone=no"/>
<script type="text/javascript" src="${basePath}<%=resBase %>js/jquery-1.10.2.min.js"></script>
<script type="text/javascript" src="${basePath}<%=resBase %>itcui/js/itcui.min.js?ver=${iVersion}"></script>
<script type="text/javascript" src="${basePath}<%=resBase %>itcui/js/jquery.validate.min.js?ver=${iVersion}"></script>
<script type="text/javascript" src="${basePath}<%=resBase %>js/mvc/mvc.dev.com.js?ver=${iVersion}"></script>
<script type="text/javascript" src="${basePath}<%=resBase %>js/mvc/mvc.dev.exception.js?ver=${iVersion}"></script>
<script type="text/javascript" src="${basePath}js/itcui_mvcutil.js?ver=${iVersion}"></script>
<!--[if lte IE 9 ]>
<script type="text/javascript" src="${basePath}<%=resBase %>itcui/js/jquery.uploadify.min.js?rand=<%=rand%>"></script>
<script>var _agentIE = true;</script>
<![endif]-->
<script type="text/javascript" src="${basePath}<%=resBase %>itcui/js/flow.min.js"></script>
<script type="text/javascript" src="${basePath}<%=resBase %>itcui/js/flow.ext.min.js?ver=${iVersion}"></script>
<link rel="stylesheet" type="text/css" href="${basePath}<%=resBase %>itcui/css/itcui.min.css?ver=${iVersion}" media="all" />
<link rel="stylesheet" type="text/css" href="${basePath}<%=resBase %>itcui/css/uploadify.css" media="all" />
<%if(theme.equals( "midleTheme" )) {%>
<link rel="stylesheet" type="text/css" href="${basePath}<%=resBase %>itcui/css/itc_dg_mid.css" media="all" itc_tbl=true/>
<%}else if(theme.equals("bigTheme")) {%>
<link rel="stylesheet" type="text/css" href="${basePath}<%=resBase %>itcui/css/itc_dg_big.css" media="all" itc_tbl=true/>
<%} %>
<!--[if lt IE 9]>
	<script>
		$(document).ready(function(){
		    FW.debug("IE8 respond update");
			respond.update();
		});
	</script>
<![endif]-->
<script>
	var _enum = null;
	<%
		if(null != enums){
	%>
	_enum = eval(<%=enums %>);
	<%
		}
	%>
	var _token = "${_token}";
	var dataGrid = null;
	var basePath = '${basePath}';
	var fileExportPath = '${birtServicePath}';
	var pageSize =  <%=pageSize%>;
	var cssMapping = {
		bigTheme : "${basePath}<%=resBase %>itcui/css/itc_dg_big.css",
		midleTheme : "${basePath}<%=resBase %>itcui/css/itc_dg_mid.css",
		normalTheme : "${basePath}<%=resBase %>itcui/css/itc_dg_small.css"
	};
	var styleOpt = {
		styles : [
			{
				"id":"bigTheme",
				"title":"宽松显示"
			},{
			
				"id":"midleTheme",
				"title":"适中显示"
			},{

				"id":"normalTheme",
				"title":"紧凑显示"
			}
		],
		defStyle : "<%=theme%>",
		onChangeStyle : function(id){
			FW.loadCss(cssMapping[id]);
			styleOpt.defStyle = id;
			if(dataGrid != null && !dataGrid.hasClass("eu-datagrid")){
				dataGrid.addClass("eu-datagrid");
			}
			// $(".eu-datagrid").datagrid("resize");
			$(".eu-datagrid").each(function(){
				var me = $(this);
				if(me.data("datagrid")){
					//me.datagrid({});
					me.datagrid("resize");
				}
			});
			if("<%=conf.type%>"=="TIMSS"){
				$.post("${basePath}UserConfig.data",{action:'updateConfigData', param:JSON.stringify({showLine:rowLen,showMode:styleOpt.defStyle})});
			}
			else{
				$.post("${basePath}user?method=setconf",{attr:"rowstyle",val:id});
			}
		},
		onChangePageSize:function(pgSize){
			rowLen = pgSize;
			if("<%=conf.type%>"=="TIMSS"){
				$.post("${basePath}UserConfig.data",{action:'updateConfigData', param:JSON.stringify({showLine:rowLen,showMode:styleOpt.defStyle})});
			}
			else{
				$.post("${basePath}user?method=setconf",{attr:"pagesize",val:pgSize});
			}
		}
	};
</script>

<!-- 动态表单加入 created by yuanzh 20150723 -->
<script type="text/javascript" src="${basePath}<%=resBase%>js/dynamicform/dynamicform.js?ver=${iVersion}"></script>
<!-- 
	<script type="text/javascript" src="${basePath}<%--=resBase --%>js/dynamicform/dynamicform_dd.js?ver=${iVersion}"></script>
	<script type="text/javascript" src="${basePath}<%--=resBase --%>js/jquery-ui.min.js"></script>
-->
<script type="text/javascript" src="${basePath}<%=resBase %>js/dynaform/dyna_form_dd.js?ver=${iVersion}"></script>
<script type="text/javascript">
var forVer = ${formVer};
var mvcService = _parent().ItcMvcService || ItcMvcService ;
var forSiteId = mvcService.getUser().siteId;
var forResBase = '<%=resBase %>';
document.write('<script src="${basePath}'+forResBase+'dynamicform/'+forSiteId.toLowerCase()+'/dynamicformstruts.js?ver='+forVer+'"><\/script>');
</script>
