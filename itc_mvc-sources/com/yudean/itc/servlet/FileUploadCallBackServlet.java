package com.yudean.itc.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yudean.itc.SecurityBeanHelper;
import com.yudean.itc.manager.support.IAttachmentManager;

@SuppressWarnings("serial")
public class FileUploadCallBackServlet extends BaseServlet {

    private IAttachmentManager attachManager;

    @Override
    public void init() throws ServletException {
        attachManager = SecurityBeanHelper.getInstance().getBean( IAttachmentManager.class );
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        receiveNotify( request, response );
    }

    public void receiveNotify(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        String id = String.valueOf( request.getParameter( "id" ) );
        int status = Integer.valueOf( String.valueOf( request.getParameter( "status" ) ) );
        String args = String.valueOf( request.getParameter( "args" ) );
        attachManager.callBack2ReceiveNotify( id, status, args );
    }
}
