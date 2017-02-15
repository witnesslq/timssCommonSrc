<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.timss.framework.util.TimssLogUtil"%>
<%@ page import="com.yudean.itc.util.Constant"%>
<%@ page import="com.yudean.itc.util.UserConfigHelper"%>
<%@ page import="com.yudean.itc.util.UserConfig"%>
<%@page import="com.timss.framework.bean.handler.ThreadLocalVariable"%>
<%@page import="com.timss.framework.mvc.handler.TimssThreadLocalHandler"%>
<%
	String enums = null;
	try{
		TimssThreadLocalHandler ThreadlocIns = TimssThreadLocalHandler.getInstance();
		ThreadLocalVariable ThreadlocData = ThreadlocIns.getVariableIns();
		if(null != ThreadlocIns && ThreadlocData != null){
			enums = (String)ThreadlocData.getThreadLocalAttribute("Frame_Enums_Type");
		}
	}catch(Exception e){
		TimssLogUtil.error("get enums error", e);
	}
 	String path = request.getContextPath();
 	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	request.getSession().getServletContext().setAttribute("basePath", basePath);
 	String resBase = Constant.resBase;
 	UserConfig conf = (UserConfig)UserConfigHelper.getPagerConfig(request);
  	String pageSize = conf.rows;
  	String theme = conf.theme; 
 %>
<%-- <script type="text/javascript" src="<%=basePath %><%=resBase %>js/jquery-1.10.2.js"></script>
<script type="text/javascript" src="<%=basePath%><%=resBase %>itcui/js/itcui.dev.js"></script>
<link rel="stylesheet" type="text/css" href="<%=basePath%><%=resBase %>itcui/css/itcui.dev.css" media="all" />
<link rel="stylesheet" type="text/css" href="<%=basePath%><%=resBase %>css/public_background.css" media="all" />  --%>
<base href="<%=basePath%>">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>

<script type="text/javascript" src="<%=basePath %><%=resBase %>js/jquery-1.10.2.js"></script>
<script type="text/javascript" src="http://10.0.250.52/uidev/nightly/js/itcui.dev.js"></script>
<script type="text/javascript" src="http://10.0.250.52/uidev/nightly/js/jquery.validate.js"></script>
<script type="text/javascript" src="http://10.0.250.52/uidev/nightly/js/jquery.uploadify.js"></script>
<script type="text/javascript" src="<%=basePath %><%=resBase %>js/json.js"></script>
<script type="text/javascript" src="<%=basePath %><%=resBase %>js/framework/timss.dev.com.js"></script>
<script type="text/javascript" src="<%=basePath %>js/itcui_timssutil.js"></script>

<link rel="stylesheet" type="text/css" href="http://10.0.250.52/uidev/nightly/css/itcui.dev.css" media="all" />
<link rel="stylesheet" type="text/css" href="http://10.0.250.52/uidev/nightly/css/uploadify.css" media="all" />
<%if(theme.equals( "midleTheme" )) {%>
<link rel="stylesheet" type="text/css" href="<%=basePath%><%=resBase %>itcui/css/itc_dg_mid.css" media="all" itc_tbl=true/>
<%}else if(theme.equals("bigTheme")) {%>
<link rel="stylesheet" type="text/css" href="<%=basePath%><%=resBase %>itcui/css/itc_dg_big.css" media="all" itc_tbl=true/>
<%} %>

<script>
	var _enum = null;
	<%
		if(null != enums){
	%>
	_enum = eval(<%=enums %>);
	<%
		}
	%>
	var dataGrid = null;
	var basePath = '<%=basePath%>';
	var pageSize =  <%=pageSize%>;
	var cssMapping = {
		bigTheme : "<%=basePath%><%=resBase %>itcui/css/itc_dg_big.css",
		midleTheme : "<%=basePath%><%=resBase %>itcui/css/itc_dg_mid.css",
		normalTheme : "<%=basePath%><%=resBase %>itcui/css/itc_dg_small.css"
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