package com.yudean.mvc.view.viewResolver;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.servlet.View;

import com.yudean.itc.bean.exception.ExceptionData;
import com.yudean.mvc.handler.ThreadLocalHandler;
import com.yudean.mvc.service.impl.FrameWorkServiceImpl;
import com.yudean.mvc.util.FormTokenUtil;
import com.yudean.mvc.util.ReflectUtil;

/**
 * TIMSS异步请求返回处理View
 * 
 * @title: {title}
 * @description: {desc}
 * @company: gdyd
 * @className: JsonView.java
 * @author: kChen
 * @createDate: 2014-7-15
 * @updateUser: kChen
 * @version: 1.0
 */
public class JsonView implements View {
    private static String S_JsonContentType = "application/json";
    private static String S_JsonHeaderKye = "Cache-Control";
    private static String S_JsonHeaderValue = "no-cache";
    private static String S_JsonEncoding = "UTF-8";

    private static final Logger log = Logger.getLogger( JsonView.class );

    private enum statusType {
        /**
         * 以0开头的状态
         */
        s0xx,
        /**
         * 以2开头的状态
         */
        s2xx,
        /**
         * 以3开头的状态
         */
        s3xx,
        /**
         * 以4开头的状态
         */
        s4xx,
        /**
         * 以5开头的状态
         */
        s5xx
    }

    @Override
    public String getContentType() {
        return "application/json; charset=utf-8";
    }

    @Override
    public void render(Map<String, ?> model, HttpServletRequest req, HttpServletResponse res) throws Exception {
        Object data = model.get( FrameWorkServiceImpl.getRunEnvironmentData().getAjaxJsonDataflag() );
        if ( null == data ) {// 如果没有定义转义对象，则直接使用model本身作为参数传递回前端
            data = model;
        }
        ObjectMapper om = new ObjectMapper();

        res.setContentType( S_JsonContentType );
        res.setHeader( S_JsonHeaderKye, S_JsonHeaderValue );
        res.setCharacterEncoding( S_JsonEncoding );

        // res.setContentType("appplication/json");
        // res.setHeader("Cache-Control", "no-cache");
        // res.setCharacterEncoding("utf-8");

        // 判断异常状态
        try {
            Integer iStatus = res.getStatus();
            StringBuffer sbStatus = new StringBuffer();
            sbStatus.append( "s" );
            sbStatus.append( iStatus.toString().substring( 0, 1 ) );
            sbStatus.append( "xx" );
            statusType type = statusType.valueOf( sbStatus.toString() );
            switch (type) {
                case s4xx:
                case s5xx: {
                    excStatus( res, iStatus );
                    break;
                }
            }
        } catch (Exception e) {
            log.error( "视图处理异常", e );
        }

        Object _tokenObj = req.getAttribute( FormTokenUtil.TOKEN_ATTRIBUTE_NAME );
        if ( null == _tokenObj ) {
            FormTokenUtil.bulidFormToken( req, null );
            _tokenObj = req.getAttribute( FormTokenUtil.TOKEN_ATTRIBUTE_NAME );
        }
        String _token = String.valueOf( _tokenObj );
        res.setHeader( FormTokenUtil.TOKEN_ATTRIBUTE_NAME, _token );

        PrintWriter out = res.getWriter();
        String jsonData = om.writeValueAsString( data );

        /************************** 动态表单查询 create by yuanzh 2015-7-16 **************************/
        String formSwitch = req.getParameter( "formSwitch" );
        if ( "on".equals( formSwitch ) ) {
            Map<String, Object> paramMap = new HashMap<String, Object>();

            String sheetId = req.getParameter( "sheetId" ) == null ? "" : String
                    .valueOf( req.getParameter( "sheetId" ) );
            String pageCode = req.getParameter( "pageCode" ) == null ? "" : String.valueOf( req
                    .getParameter( "pageCode" ) );
            String formId = req.getParameter( "formId" ) == null ? "" : String.valueOf( req.getParameter( "formId" ) );

            paramMap.put( "sheetId", sheetId );
            paramMap.put( "pageCode", pageCode );
            paramMap.put( "formId", formId );

            Object[] args = new Object[2];
            args[0] = jsonData;
            args[1] = paramMap;
            Object obj = FrameWorkServiceImpl.getBean( "BDynamicFormDataServiceImpl" );
            Method method = ReflectUtil.getReflectMethod( "integrateJSONData", args, obj.getClass() );
            Object reObj = method.invoke( obj, args );
            jsonData = String.valueOf( reObj );
        }
        /************************** 动态表单查询 create by yuanzh 2015-7-16 **************************/
        out.print( jsonData );
        out.close();
    }

    @SuppressWarnings("deprecation")
    private void excStatus(HttpServletResponse res, Integer iStatus) throws Exception {
        ExceptionData excep = ThreadLocalHandler.getVariable().getExceptionData();
        String errData = excep.getData().get( FrameWorkServiceImpl.getRunEnvironmentData().getAjaxDataName() );
        // res.sendError(iStatus, errData);
        res.setStatus( iStatus, errData );
    }
}
