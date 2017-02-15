package com.yudean.mvc.filter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.yudean.itc.code.ParamConfig;
import com.yudean.mvc.configs.MvcConfig;
import com.yudean.mvc.configs.MvcWebConfig;

public class MvcFilter implements Filter {
    static public String		 S_localhostPath = "http://localhost";		 // 本地路径指向参数
    static public String		 S_PatternIp     = "\\d+\\.\\d+\\.\\d+\\.\\d*\\:\\d+"; // 本地路径指向参数

    // 动态表单版本map记录
    static public Map< String , String > formVerMap      = new HashMap< String , String >();

    @Override
    public void init ( FilterConfig filterConfig ) throws ServletException {

    }

    @Override
    public void doFilter ( ServletRequest request , ServletResponse response , FilterChain chain ) throws IOException ,
	    ServletException {
	setRequestAttribute( ( HttpServletRequest ) request );
	chain.doFilter( request , response );
    }

    @Override
    public void destroy () {

    }

    private void setRequestAttribute ( HttpServletRequest request ) {
	String rootPath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/";

	// 动态表单版本号设置
	String formVer = formVerMap.get( "formVer" );
	if ( null == formVer || "".equals( formVer ) ) {
	    SimpleDateFormat sdf = new SimpleDateFormat( "yyyyMMddHHmmss" );
	    formVer = sdf.format( new Date() );
	    formVerMap.put( "formVer" , formVer );
	}
	request.setAttribute( ParamConfig.S_formVersionKey , formVer );

	// 设置每次运行的版本号
	request.setAttribute( ParamConfig.S_iVersionKey , MvcWebConfig.curRunVersion );
	if ( rootPath.startsWith( S_localhostPath ) ) {// 本地请求 、
	    serverhostLocal( request , rootPath );
	} else if ( checkIp( rootPath ) ) {// ip请求
	    switch (MvcConfig.getCurRunMode()) {
		case Develop:
		case Test: {
		    serverhostRemote( request , rootPath );
		    break;
		}
		case Produce: { // 生产运行必须使用域名
		    serverhostRemote( request , rootPath );
		    break;
		}
		default: {
		    serverhostLocal( request , rootPath );
		    break;
		}
	    }
	} else {// 远端请求
	    serverhostRemote( request , rootPath );
	}
    }

    private boolean checkIp ( String rootPath ) {
	Pattern p = Pattern.compile( S_PatternIp );
	Matcher m = p.matcher( rootPath );
	return m.find();
    }

    private void serverhostLocal ( HttpServletRequest request , String rootPath ) {
	request.setAttribute( ParamConfig.S_BasePathKey , request.getScheme() + "://" + request.getServerName() + ":"
		+ request.getServerPort() + request.getContextPath() + "/" );
	request.setAttribute( ParamConfig.S_RootPathKey , rootPath );
	request.setAttribute( ParamConfig.S_BirtPathKey , request.getScheme() + "://" + request.getServerName() + ":"
		+ request.getServerPort() + "/itc_report/" );
    }

    private void serverhostRemote ( HttpServletRequest request , String rootPath ) {
	// 获取服务器运行基本路径。
	if ( ParamConfig.S_NaN.equals( MvcWebConfig.serverBasePath ) ) {
	    MvcWebConfig.serverBasePath = request.getScheme() + "://" + request.getServerName() + ":"
		    + request.getServerPort() + request.getContextPath() + "/";
	}
	request.setAttribute( ParamConfig.S_BasePathKey , MvcWebConfig.serverBasePath );

	// 获取服务器运行报表路径
	if ( ParamConfig.S_NaN.equals( MvcWebConfig.serverRootPath ) ) {
	    MvcWebConfig.serverRootPath = rootPath;
	}
	request.setAttribute( ParamConfig.S_RootPathKey , MvcWebConfig.serverRootPath );

	// 获取报表服务器根路径
	if ( ParamConfig.S_NaN.equals( MvcWebConfig.birtRootPath ) ) {
	    MvcWebConfig.birtRootPath = request.getScheme() + "://" + request.getServerName() + ":"
		    + request.getServerPort() + "/";
	}
	// request.setAttribute(ParamConfig.S_BirtRootPathKey,
	// MvcWebConfig.birtRootPath);

	// 获取报表服务器上下文服务路径
	if ( ParamConfig.S_NaN.equals( MvcWebConfig.birtContextPath ) ) {
	    MvcWebConfig.birtContextPath = "itc_report/";
	}
	// request.setAttribute(ParamConfig.S_BirtPathKey,
	// MvcWebConfig.birtRootPath);

	// 获取报表服务器路径
	if ( ParamConfig.S_NaN.equals( MvcWebConfig.birtServicePath ) ) {
	    MvcWebConfig.birtServicePath = MvcWebConfig.birtRootPath + MvcWebConfig.birtContextPath;
	}
	request.setAttribute( ParamConfig.S_BirtPathKey , MvcWebConfig.birtServicePath );
    }
}
