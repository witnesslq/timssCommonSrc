package com.yudean.itc.servlet;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.yudean.itc.SecurityBeanHelper;
import com.yudean.itc.dto.sec.SecureUser;
import com.yudean.itc.dto.support.Attachment;
import com.yudean.itc.dto.support.Configuration;
import com.yudean.itc.exception.SimpleMessageException;
import com.yudean.itc.exception.StatusMessageException;
import com.yudean.itc.manager.support.IAttachmentManager;
import com.yudean.itc.manager.support.IConfigurationManager;
import com.yudean.itc.util.Constant;
import com.yudean.itc.util.FileUploadUtil;
import com.yudean.itc.util.SecurityTools;

public class FileUploadServlet extends BaseServlet {

    private static final long serialVersionUID = 2339079284354012077L;
    private static Logger logger = Logger.getLogger( FileUploadServlet.class );
    private IAttachmentManager attachManager;
    private IConfigurationManager iConfigurationManager;
    // 默认允许上传25mb以内的文件
    private static final int DEFAULT_FILE_THRESHOLD = 50 * 1024 * 1024;
    private static Pattern previewableFileTypes = Pattern.compile("(zip|rar|7z|doc|docx|xls|xlsx|ppt|pptx|wps|pdf|txt|tif|tiff)");

    @Override
    public void init() throws ServletException {
        attachManager = SecurityBeanHelper.getInstance().getBean( IAttachmentManager.class );
        iConfigurationManager = SecurityBeanHelper.getInstance().getBean( IConfigurationManager.class );
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String method = request.getParameter( "method" );
        String key = trimStrToNull( request.getParameter( "key" ) );
        if ( method == null ) {
            return;
        }
        try {
            SecureUser secUser = (SecureUser) request.getSession().getAttribute( Constant.secUser );
            if ( secUser == null && key == null && !method.equals( "downloadFile" ) ) {
                outputStatus( response, -1, "文件上传必须登录后才能操作" );
                return;
            }
            if ( method.equals( "uploadFile" ) ) {
                uploadFile( request, response );
            } else if ( method.equals( "downloadFile" ) ) {
                downloadFile( request, response );
            } else if ( method.equals( "delFile" ) ) {
                delFile( request, response );
            } else if ( method.equals( "tempKey" ) ) {
                getTempKey( request, response );
            }
        } catch (SimpleMessageException ex) {
            outputMsg( response, ex.getMessage() );
        } catch (StatusMessageException ex) {
            outputStatus( response, -1, ex.getMessage() );
        } catch (Exception ex) {
            ex.printStackTrace();
            outputMsg( response, "系统错误，暂时无法响应您的请求，请联系管理员" );
        }
    }

    private void uploadFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession();
        Integer sizeThreshold = (Integer) session.getAttribute( "sizeThreshold" );
        if ( sizeThreshold == null ) {
            sizeThreshold = DEFAULT_FILE_THRESHOLD;
        }
        List<String> resultArr = FileUploadUtil.uploadFile( request, Constant.basePath, sizeThreshold );
        // 这里默认是单文件单请求(虽然FileUploadUtil支持多文件同请求) 不需要判断单个文件的失败（前端封装也不支持）
        String fResult = resultArr.get( 0 );
        String[] rSplit = fResult.split( "," );
        // 如果用户登陆的当前站点开启了附件预览才请求预览
        Boolean canPreviewAttach = (Boolean)session.getAttribute("canPreviewAttach");
        if(canPreviewAttach != null && canTransformFile(rSplit[1])) {
            Configuration siteConfig = iConfigurationManager.query("interface_TranDocument_notifyURL", "NaN", "NaN");
            attachManager.uploadAttachment2TranSys(rSplit[0], siteConfig.getVal());
        }else{
            logger.info("文件 " + rSplit[1] + " 无法转换或者当前站点禁用附件预览功能");
        }
        outputStatus( response, 1, rSplit[0] );
    }

    /**
     * 获取一个临时的key用于下载文件，因为c#的控件不便于共享session，所以使用key验证合法性
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void getTempKey(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        SecureUser secUser = (SecureUser) request.getSession().getAttribute( Constant.secUser );
        String id = trimStrToNull( request.getParameter( "id" ) );
        Long ts = new Date().getTime();
        String s = id + "----" + ts + "----" + secUser.getId();
        outputStatus( response, 1, SecurityTools.encrypt( s ) );
    }

    private void downloadFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = trimStrToNull( request.getParameter( "id" ) );
        String isOpen = trimStrToNull( request.getParameter( "open" ) );
        // 鉴权码 c#控件打开专用参数
        String key = trimStrToNull( request.getParameter( "key" ) );
        // 双步下载支持
        String prepare = trimStrToNull( request.getParameter( "prepare" ) );
        if ( id == null ) {
            if ( key == null ) {
                return;
            } else {
                logger.info( "尝试下载文件（文件预览），key = " + key );
                String s = null;
                try {
                    s = SecurityTools.decrypt( key );
                } catch (Exception ex) {
                    throw new StatusMessageException( "鉴权码错误，无法下载文件" );
                }

                String[] arr = s.split( "----" );
                if ( arr.length != 3 ) {
                    throw new StatusMessageException( "鉴权码错误，无法下载文件" );
                }
                id = arr[0];

            }
        }
        if ( key == null ) {
            SecureUser secUser = (SecureUser) request.getSession().getAttribute( Constant.secUser );
            if ( prepare != null ) {
                logger.info( secUser.getId() + "尝试下载id = " + id + "的文件" );
            }
        }
        Attachment attachment = FileUploadUtil.getAttachment( Constant.basePath, id );
        if ( null != attachment ) {
            if ( attachment.getFile().exists() ) {
                // 二段下载的cookie支持
                String randKey = trimStrToNull( request.getParameter( "rand" ) );
                if ( null == prepare ) {
                    // 如果前端要求cookie控制按钮 则直接把randKey作为cookie的一个key
                    // 这样前端可以核对特殊的key来判断不同按钮的下载状态
                    if ( null != randKey ) {
                        Cookie cookie = new Cookie( randKey, "1" );
                        cookie.setMaxAge( -1 );
                        response.addCookie( cookie );
                    }
                    goDownload( attachment.getFile(), attachment.getOriginalFileName(), isOpen != null, request,
                            response );
                } else {
                    outputMsg( response, "true" );
                }
            } else {
                throw new StatusMessageException( "文件在物理磁盘被删除，请与系统管理员联系" );
            }
        } else {
            throw new StatusMessageException( "非法的文件ID，请确定文件来源合法" );
        }
    }

    private boolean canTransformFile(String fileName){
        int n = fileName.lastIndexOf(".");
        if(n < 0){
            return false;
        }
        String extName = fileName.substring(n + 1);
        Matcher matcher = previewableFileTypes.matcher(extName);
        return matcher.matches();
    }

    private void delFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession();
        String valKey = trimStrToNull( request.getParameter( "key" ) );
        String fileId = trimStrToNull( request.getParameter( "id" ) );
        if ( fileId == null ) {
            return;
        }
        SecureUser secUser = (SecureUser) session.getAttribute( Constant.secUser );
        FileUploadUtil.validateDel( valKey, fileId, secUser );
        logger.info( "用户" + secUser.getId() + "(来自" + request.getRemoteHost() + ")尝试删除文件 = " + fileId );
        try {
            attachManager.deleteAttachment( Constant.basePath, fileId );
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new StatusMessageException( "删除附件失败，请与管理员联系" );
        }
        outputStatus( response, 1, "删除成功" );
    }

}
