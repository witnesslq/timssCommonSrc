package com.yudean.itc.servlet;

import com.yudean.itc.SecurityBeanHelper;
import com.yudean.itc.bean.Announcement;
import com.yudean.itc.dao.support.AnnouncementMapper;
import com.yudean.itc.dto.Page;
import com.yudean.itc.dto.sec.SecureUser;
import com.yudean.itc.manager.support.IAnnounceManager;
import com.yudean.itc.util.Constant;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.log4j.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 890157 on 2016/6/12.
 */
public class AnnouncementServlet extends BaseServlet{
    AnnouncementMapper announcementMapper = null;
    IAnnounceManager announceManager = null;
    private Logger logger = Logger.getLogger(AnnouncementServlet.class);
    private static final String ANNOUNCE_COOKIE = "announce_id";
    private static final int COOKIE_LIFETIME = 365 * 24 * 3600;

    public void init() throws ServletException {
        SecurityBeanHelper helper = SecurityBeanHelper.getInstance();
        announcementMapper = helper.getBean(AnnouncementMapper.class);
        announceManager = helper.getBean(IAnnounceManager.class);
    }

    protected void service(HttpServletRequest request,
                           HttpServletResponse response) throws ServletException, IOException {
        String method = trimStrToNull(request.getParameter("method"));
        if(method == null){
            return;
        }
        if(!method.equals("getNew") &&  !method.equals("getContent")){
            SecureUser user = (SecureUser) request.getSession().getAttribute(Constant.secUser);
            if(!user.isSuperAdmin()){
                outputMsg(response, "只有超级管理员可以使用此功能");
                return;
            }
        }
        if(method.equals("listPage")){
            listPage(request, response);
        }else if(method.equals("getList")){
            getList(request, response);
        }else if(method.equals("detailPage")){
            detailPage(request, response);
        }else if(method.equals("getDetail")){
            getDetail(request, response);
        }else if(method.equals("upsert")){
            upsert(request, response);
        }else if(method.equals("delete")){

        }else if(method.equals("getNew")){
            getNew(request, response);
        }else if(method.equals("getContent")){
            getContent(request, response);
        }
    }

    private void listPage(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException{
        RequestDispatcher dispatcher = request
                .getRequestDispatcher(Constant.jspPath + "announce_list.jsp");
        dispatcher.forward(request, response);
    }

    private void detailPage(HttpServletRequest request,
                            HttpServletResponse response) throws ServletException, IOException{
        String idStr = ObjectUtils.firstNonNull(request.getParameter("id"), "0");
        Integer id = Integer.parseInt(idStr);
        request.setAttribute("id", id);
        RequestDispatcher dispatcher = request
                .getRequestDispatcher(Constant.jspPath + "edit_announce.jsp");
        dispatcher.forward(request, response);
    }

    private void upsert(HttpServletRequest request,
                        HttpServletResponse response) throws ServletException, IOException{
        Announcement obj = new Announcement();
        obj.setItemId(Integer.parseInt(ObjectUtils.firstNonNull(request.getParameter("itemId"), "0")));
        obj.setItemDesc(request.getParameter("itemDesc"));
        obj.setPopupH(ObjectUtils.firstNonNull(request.getParameter("popupH"), "-1"));
        obj.setPopupW(ObjectUtils.firstNonNull(request.getParameter("popupW"), "-1"));
        obj.setSites(ObjectUtils.firstNonNull(request.getParameter("sites"), "*"));
        obj.setExpireAt(Long.parseLong(ObjectUtils.firstNonNull(request.getParameter("expireAt"), "-1")));
        obj.setContent(request.getParameter("content"));
        String mode = obj.getItemId() > 0 ? "编辑" : "新建";
        try {
            announceManager.upsert(obj);
        }catch (Exception ex){
            logger.error("在修改公告数据时出现异常", ex);
            outputStatus(response, -1, "公告数据更新失败");
            return;
        }
        outputStatus(response, 1, "公告" + mode +  "成功");
    }

    private void getNew(HttpServletRequest request,
                        HttpServletResponse response) throws ServletException, IOException{
        HttpSession session = request.getSession();
        SecureUser secUser = (SecureUser) session.getAttribute(Constant.secUser);
        if(secUser == null){
            return;
        }
        String site = secUser.getCurrentSite();
        Announcement announcement = announcementMapper.selectNew(site, System.currentTimeMillis());
        if(announcement == null || getCookiesAnnounceVer(request) >= announcement.getItemId()){
            outputStatus(response, -1, "没有最新的公告");
        }else{
            outputStatusData(response, 1, "找到公告", announcement);
        }
    }

    private void getList(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException{
        int page = Integer.parseInt(ObjectUtils.firstNonNull(request.getParameter("page"), "1"));
        int rows = Integer.parseInt(ObjectUtils.firstNonNull(request.getParameter("rows"), "15"));
        Page<Announcement> result = announceManager.getList(page, rows);
        Map<String, Object> ret = new HashMap<String, Object>();
        ret.put("total", result.getTotalRecord());
        ret.put("rows", result.getResults());
        outputJson(response, ret);
    }

    private void getContent(HttpServletRequest request,
                            HttpServletResponse response) throws ServletException, IOException{
        HttpSession session = request.getSession();
        SecureUser secUser = (SecureUser) session.getAttribute(Constant.secUser);
        if(secUser == null){
            return;
        }
        int id = Integer.parseInt(request.getParameter("id"));
        Announcement announcement = announcementMapper.selectContent(id);
        Cookie cookie = new Cookie(ANNOUNCE_COOKIE, id + "");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(COOKIE_LIFETIME);
        response.addCookie(cookie);
        request.setAttribute("content", announcement.getContent());
        RequestDispatcher dispatcher = request
                .getRequestDispatcher(Constant.jspPath + "announce_tmpl.jsp");
        dispatcher.forward(request, response);
    }

    private void getDetail(HttpServletRequest request,
                            HttpServletResponse response) throws ServletException, IOException{
        int id = Integer.parseInt(request.getParameter("id"));
        if(id <= 0){
            return;
        }
        Announcement announcement = announcementMapper.selectDetail(id);
        outputStatusData(response, announcement != null ? 1 : -1, "", announcement);
    }

    private int getCookiesAnnounceVer(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie: cookies){
            if(cookie.getName().equals(ANNOUNCE_COOKIE)){
                return Integer.parseInt(cookie.getValue());
            }
        }
        return -1;
    }

}

