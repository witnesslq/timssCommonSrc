package com.yudean.itc.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.yudean.itc.SecurityBeanHelper;
import com.yudean.itc.dto.sec.SecureUser;
import com.yudean.itc.dto.support.Attachment;
import com.yudean.itc.exception.StatusMessageException;
import com.yudean.itc.manager.support.IAttachmentManager;
import com.yudean.itc.manager.support.IImageManager;

public class FileUploadUtil {
    public static IImageManager imgManager;
    public static IAttachmentManager attachManager;
    private static Logger logger = Logger.getLogger( FileUploadUtil.class );

    public static final int DEL_ALL = 3;
    public static final int DEL_OWNED = 2;

    static {
        SecurityBeanHelper helper = SecurityBeanHelper.getInstance();
        attachManager = helper.getBean( IAttachmentManager.class );
    }

    private static final Logger log = Logger.getLogger( FileUploadUtil.class );

    /**
     * 获取一个路径中的文件名部分
     * 
     * @param fn
     * @return
     */
    private static String getFileName(String fn) {
        int n = fn.lastIndexOf( '\\' );
        return fn.substring( n + 1 );
    }

    /**
     * 生成一个用于删除文件的鉴权码，如果不提供鉴权码则无法删除文件（前端不提供鉴权码则不显示删除按钮） 鉴权码的组成为操作者 + 生成时间
     * +删除级别，删除级别包括任意删除（管理员）和只能删除自己的文件 由开发者根据流程决定
     * 
     * @param operator
     * @param delLevel
     * @return
     */
    public static String getValidateStr(SecureUser operator, int delLevel) {
        long d = new Date().getTime();
        String orgi = operator.getId() + "," + d + "," + delLevel;
        return SecurityTools.encrypt( orgi );
    }

    /**
     * 验证用户是否有删除特定文件的权限
     * 
     * @param str 鉴权字符串，由用户ID、时间戳、权限编码而成
     * @param fileId
     * @param operator
     * @throws Exception
     */
    public static void validateDel(String str, String fileId, SecureUser operator) throws Exception {
        String decoded = null;
        if ( str == null ) {
            throw new StatusMessageException( "非法的验证字符串" );
        }
        try {
            decoded = SecurityTools.decrypt( str );
        } catch (Exception ex) {
            throw new StatusMessageException( "非法的验证字符串" );
        }
        Attachment attach = attachManager.retrieveAttachment( Constant.basePath, fileId );
        if ( attach == null ) {
            throw new StatusMessageException( "目标文件不存在，无法删除" );
        }
        String[] arr = decoded.split( "," );
        if ( arr.length != 3 ) {
            throw new StatusMessageException( "非法的验证字符串" );
        }
        int delLevel = Integer.parseInt( arr[2] );
        long ts = Long.parseLong( arr[1] );
        // 验证KEY是否过期
        if ( new Date().getTime() - ts > Constant.DEL_KEY_EXPIRES * 1000 ) {
            throw new StatusMessageException( "操作超时，请刷新页面后再尝试删除" );
        }
        // 当删除级别为2（只能删自己的文件）时，要保证KEY中的用户ID和当前用户ID一致，同时附件的拥有者是当前登录人
        if ( delLevel == 3
                || (delLevel == 2 && (operator.getId().equals( arr[0] ) && attach.getCreatedBy().equals( arr[0] ))) ) {
            // 可以删除文件
        } else {
            throw new StatusMessageException( "您没有权限删除这个文件" );
        }
    }

    /**
     * @param request
     * @param basePath 文件保存的根路径
     * @param sizeThreshold 文件大小限制（每次）
     * @return 错误信息，没有全局错误则返回null
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static List<String> uploadFile(HttpServletRequest request, String basePath, int sizeThreshold)
            throws Exception {
        List<String> resultArr = new ArrayList<String>();
        SecureUser operator = (SecureUser) request.getSession().getAttribute( "user" );
        logger.info( "用户" + operator.getId() + "(来自" + request.getRemoteHost() + ")尝试发起一次文件上传" );
        String tmpPath = basePath + File.separator + "temp";
        if ( !new File( tmpPath ).exists() ) {
            try {
                new File( tmpPath ).mkdirs();
            } catch (Exception ex) {
                throw new StatusMessageException( "无法创建文件上传目录，请确认服务器磁盘有写权限" );
            }
        }
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold( sizeThreshold );
        factory.setRepository( new File( tmpPath ) );
        ServletFileUpload upload = new ServletFileUpload( factory );
        upload.setFileSizeMax( sizeThreshold );
        upload.setHeaderEncoding( "UTF-8" );
        List fileList = null;
        try {
            fileList = upload.parseRequest( request );
        } catch (FileSizeLimitExceededException e) {
            log.warn( operator.getId() + "试图上传过大的文件，但是失败了" );
            throw new StatusMessageException( "文件不能超过" + (sizeThreshold / 1024 / 1024) + "mb" );
        } catch (FileUploadException e) {
            log.warn( operator.getId() + "上传文件失败" );
            e.printStackTrace();
            throw new StatusMessageException( "处理文件出现错误，请确保文件格式和大小合法" );
        }

        Iterator<FileItem> it = fileList.iterator();
        while (it.hasNext()) {
            FileItem item = it.next();
            if ( !item.isFormField() && item.getName() != null ) {
                String fn = getFileName( item.getName() );
                try {
                    String reserved = StringUtils.trimToNull( request.getParameter( "attachreserved" ) );
                    String fileId = attachManager.saveAttachment( item.getInputStream(), basePath, fn, operator,
                            reserved );
                    log.info( operator.getId() + "成功上传文件" + fn + "，id = " + fileId );
                    resultArr.add( fileId + "," + fn + "," + item.getSize() );
                } catch (Exception ex) {
                    log.warn( operator.getId() + "上传文件" + fn + "失败" );
                    ex.printStackTrace();
                    throw new StatusMessageException( "文件" + fn + "上传失败" );
                }
            }
        }
        return resultArr;
    }

    /**
     * 获得一系列文件ID对应的json数据表示
     * 
     * @param basePath
     * @param fileIds
     * @return
     */
    public static List<Map<String, Object>> getJsonFileList(String basePath, List<String> fileIds) {
        if ( fileIds == null ) {
            return null;
        }
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for ( int i = 0; i < fileIds.size(); i++ ) {
            String id = fileIds.get( i );
            Attachment attach = attachManager.retrieveAttachment( basePath, id );
            if ( attach != null ) {
                Map<String, Object> f = new HashMap<String, Object>();
                f.put( "fileID", attach.getId() );
                f.put( "fileName", attach.getOriginalFileName() );
                f.put( "fileSize", attach.getFilesize() );
                f.put( "creator", attach.getCreatedBy() );
                f.put( "reserved", attach.getReserved() );
                f.put( "status", attach.getOperStauts() );
                result.add( f );
                log.info( ">>>>>>>>>>>>>>>>fileID: " + attach.getId() + " | fileName: " + attach.getOriginalFileName()
                        + " | fileSize: " + attach.getFilesize() + " | creator: " + attach.getCreatedBy()
                        + " | reserved: " + attach.getReserved() + " | status: " + attach.getOperStauts() );
            }
        }
        return result;
    }

    public static Attachment getAttachment(String basePath, String fileId) {
        return attachManager.retrieveAttachment( basePath, fileId );
    }
}