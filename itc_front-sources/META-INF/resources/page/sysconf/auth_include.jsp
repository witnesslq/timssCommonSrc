<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.yudean.itc.code.ParamConfig"%>
<%@ page import="com.yudean.itc.util.Constant" %>
<%@ page import="com.yudean.itc.util.UserConfigHelper" %>
<%@ page import="com.yudean.itc.util.UserConfig" %>
<%
  	String path = request.getContextPath();
 	String basePath = (String)request.getAttribute(ParamConfig.S_BasePathKey);
 	String iVersion = (String)request.getAttribute(ParamConfig.S_iVersionKey);
  	UserConfig conf = (UserConfig)UserConfigHelper.getPagerConfig(request);
  	String pageSize = conf.rows;
  	String theme = conf.theme;
  	String resBase = Constant.resBase;
  	String jspPath = Constant.jspPath;
  	request.setAttribute("resBase", resBase);
%>
<script type="text/javascript" src="<%=basePath%><%=resBase %>js/jquery-1.10.2.min.js"></script>
<script type="text/javascript" src="<%=basePath%><%=resBase %>itcui/js/itcui.min.js?ver=${iVersion}"></script>
<script type="text/javascript" src="<%=basePath%><%=resBase %>itcui/js/jquery.validate.min.js"></script>
<script type="text/javascript" src="<%=basePath%><%=resBase %>js/servletjs/const.js?ver=${iVersion}"></script>
<link rel="stylesheet" type="text/css"
	href="<%=basePath%><%=resBase %>css/public_background.css" media="all" />
<link rel="stylesheet" type="text/css"
	href="<%=basePath%><%=resBase %>itcui/css/itcui.min.css" media="all" />
<link rel="stylesheet" type="text/css"
	href="<%=basePath%><%=resBase %>css/public_background.css" media="all" />
<%if(theme.equals( "midleTheme" )) {%>
<link rel="stylesheet" type="text/css" href="<%=basePath%><%=resBase %>itcui/css/itc_dg_mid.css" media="all" itc_tbl=true/>
<%}else if(theme.equals("bigTheme")) {%>
<link rel="stylesheet" type="text/css" href="<%=basePath%><%=resBase %>itcui/css/itc_dg_big.css" media="all" itc_tbl=true/>
<%} %>
<style>
.toolbar {
	width: 100%;
	height: 33px;
	margin-bottom: 1px;
	border-bottom-color: #5aa3d6;
	border-bottom-width: 1px;
	border-bottom-style: solid;
}
</style>
<script>
	var basePath = "<%=basePath%>";
	var resBase = "<%=resBase%>";
	var jspPath = "<%=jspPath%>";
	var cssMapping = {
			bigTheme : "<%=basePath%><%=resBase %>itcui/css/itc_dg_big.css",
			midleTheme : "<%=basePath%><%=resBase %>itcui/css/itc_dg_mid.css",
			normalTheme : "<%=basePath%><%=resBase %>itcui/css/itc_dg_small.css"
	};
	var rowLen = <%=pageSize%>;
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
			$(".eu-datagrid").datagrid("resize");
			if("<%=conf.type%>"=="TIMSS"){
				$.post("<%=basePath%>UserConfig.data",{action:'updateConfigData', param:JSON.stringify({showLine:rowLen,showMode:styleOpt.defStyle})});
			}
			else{
				$.post("<%=basePath%>user?method=setconf",{attr:"rowstyle",val:id});
			}
		},
		onChangePageSize:function(pgSize){
			rowLen = pgSize;
			if("<%=conf.type%>"=="TIMSS"){
				$.post("<%=basePath%>UserConfig.data",{action:'updateConfigData', param:JSON.stringify({showLine:rowLen,showMode:styleOpt.defStyle})});
			}
			else{
				$.post("<%=basePath%>user?method=setconf",{attr:"pagesize",val:pgSize});
			}
		}
	};
</script>