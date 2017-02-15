package com.yudean.itc.servlet;

import com.yudean.itc.SecurityBeanHelper;
import com.yudean.itc.bean.ImageRequest;
import com.yudean.itc.dao.support.AttachmentMapper;
import com.yudean.itc.dto.support.ArchiveInfo;
import com.yudean.itc.dto.support.Attachment;
import com.yudean.itc.manager.support.IAttachmentManager;
import com.yudean.itc.util.Constant;
import org.apache.commons.lang3.ObjectUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 890157 on 2016/6/6.
 */
public class AttachmentPreviewServlet extends BaseServlet {
    private IAttachmentManager attachManager;
    private AttachmentMapper attachMapper;

    public void init() throws ServletException {
        attachManager = SecurityBeanHelper.getInstance().getBean(IAttachmentManager.class);
        attachMapper = SecurityBeanHelper.getInstance().getBean(AttachmentMapper.class);
    }

    protected void service(HttpServletRequest request,
                           HttpServletResponse response) throws ServletException, IOException {
        //这里的API命名是为了兼容原版接口 因为原版有restful接口 所以会有同一个接口两种格式的
        String method = trimStrToNull(request.getParameter("method"));
        if(method == null){
            return;
        }
        if(method.equals("listarchive")){
            listArchive(request, response);
        }else if(method.equals("image")){
            getImage(request, response);
        }else if(method.equals("archiveimage")){
            getImage(request, response);
        }else if(method.equals("info")){
            getFileInfo(request, response);
        }else if(method.equals("archivefileinfo")){
            getArchiveInfo(request, response);
        }
    }

    private void getImage(HttpServletRequest request, HttpServletResponse response){
        ImageRequest imgReq = new ImageRequest();
        imgReq.setFileId(trimStrToNull(request.getParameter("fileId")));
        String innerId = trimStrToNull(request.getParameter("innerId"));
        if(innerId != null) {
            imgReq.setInnerId(Integer.parseInt(innerId));
        }
        imgReq.setImgSize(ObjectUtils.firstNonNull(trimStrToNull(request.getParameter("imgSize")), "md"));
        imgReq.setImgType(ObjectUtils.firstNonNull(trimStrToNull(request.getParameter("imgType")), "png"));
        imgReq.setPage(Integer.parseInt(ObjectUtils.firstNonNull(trimStrToNull(request.getParameter("page")), "1")));
        imgReq.setBasePath(Constant.basePath);
        outputImage(attachManager.getPreviewImage(imgReq), response);
    }

    private void getFileInfo(HttpServletRequest request, HttpServletResponse response){
        String fileId = trimStrToNull(request.getParameter("ids"));
        Attachment attach = attachMapper.selectById(fileId);
        Map<String, Object> ret = new HashMap<String, Object>();
        if(attach == null){
            ret.put("retCode", -1);
        }else{
            ret.put("retCode", 1);
            String fn = attach.getOriginalFileName();
            String extFn = fn.substring(fn.lastIndexOf(".")+1).toLowerCase();
            //这里是模拟原版api的返回
            List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
            Map<String, Object> item = new HashMap<String, Object>();
            if(extFn.equals("zip") || extFn.equals("7z") || extFn.equals("rar")){
                item.put("fileType", 2);
            }else{
                item.put("fileType", 1);
            }
            item.put("pageCount", attach.getPageCount());
            item.put("fileStatus", attach.getOperStauts().equals("RECEIVE_SUCCESS") ? 3 : -1);
            item.put("fileName", attach.getOriginalFileName());
            items.add(item);
            ret.put("items", items);
        }
        outputJson(response, ret);
    }

    private void listArchive(HttpServletRequest request, HttpServletResponse response){
        String fileId = trimStrToNull(request.getParameter("fileId"));
        String sPid = trimStrToNull(request.getParameter("pid"));
        Integer pid = sPid != null ? Integer.parseInt(sPid) : null;
        List<ArchiveInfo> fileList = attachMapper.selectArchiveInfo(fileId, pid);
        Map<String, Object> ret = new HashMap<String, Object>();
        ret.put("fileList", fileList);
        ret.put("retCode", 1);
        outputJson(response, ret);
    }

    private void getArchiveInfo(HttpServletRequest request, HttpServletResponse response){
        String fileId = trimStrToNull(request.getParameter("fileId"));
        Integer innerId = Integer.parseInt(request.getParameter("innerId"));
        ArchiveInfo info = attachMapper.selectSingleArchiveInfo(fileId, innerId);
        Map<String, Object> ret = new HashMap<String, Object>();
        if(info != null){
            ret.put("retCode", 1);
            ret.put("data", info);
        }else{
            ret.put("retCode", -1);
        }
        outputJson(response, ret);
    }
}
