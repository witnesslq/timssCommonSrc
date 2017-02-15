package com.yudean.homepage.service;

import java.util.List;

import com.yudean.homepage.bean.NoticeBean;
import com.yudean.homepage.exception.HomepageNoticeModifyException;
import com.yudean.homepage.exception.HomepageNoticeQueryException;
import com.yudean.homepage.vo.SiteActiveInfoVo;
import com.yudean.mvc.bean.userinfo.UserInfo;

/**
 * 首页，最新动态，通知消息接口
 * 
 * @author kchen
 */
public interface HomepagePortalService {

    /**
     * 获取当前用户的最新通知信息，
     * 
     * @param userInfo 当前用户信息
     * @param colunmNum 显示行数
     * @return
     * @throws NullPointerException
     */
    List<NoticeBean> getUserNotice(UserInfo userInfo, int colunmNum) throws HomepageNoticeQueryException;

    /**
     * 添加、更新通知动态信息
     * 
     * @param noticeBean
     * @param userInfo
     * @return
     * @throws HomepageNoticeModifyException
     */
    void modifyNotice(NoticeBean noticeBean, UserInfo userInfo) throws HomepageNoticeModifyException;

    /**
     * 完成通知信息，将通知从动态中移除
     * 
     * @param code
     * @param siteId
     * @param userInfo
     * @throws HomepageNoticeModifyException
     */
    void completeNotice(String code, String siteId, UserInfo userInfo) throws HomepageNoticeModifyException;

    /**
     * 获取站点活跃度信息
     * 
     * @return
     */
    SiteActiveInfoVo getSiteUserActiveInfo();

    /**
     * @description:通过Code查询Notice是否存在
     * @author: yuanzh
     * @createDate: 2015-9-9
     * @param code
     * @param siteId
     * @return:
     */
    NoticeBean queryNoticeByCode(String code, String siteId);
}
