package com.yudean.mvc.service;

import java.util.List;
import java.util.Map;

import com.yudean.itc.dto.Page;
import com.yudean.itc.dto.support.Configuration;
import com.yudean.mvc.bean.userinfo.UserInfo;
import com.yudean.mvc.bean.userinfo.UserInfoScope;

public interface ItcSysConfService {
    /**
     * 获取用户发送短信的标志,会先检查是否站点配置可发送
     * 
     * @param userId
     * @param userInfo
     * @return
     */
    boolean isUserSendSmsSite(String userId, String curSiteId, UserInfo userInfo);

    /**
     * 获取用户发送邮件标记,会先检查是否站点配置可发送
     * 
     * @param userId
     * @param userInfo
     * @return
     */
    boolean isUserSendEmailSite(String userId, String curSiteId, UserInfo userInfo);

    /**
     * 获取当前站点的发送配置
     * 
     * @param curSiteId
     * @param userInfo
     * @return
     */
    boolean isSiteSend(String curSiteId, UserInfo userInfo);

    /**
     * 获取用户的短信发送配置信息、不检查站点配置
     * 
     * @param userId
     * @param curSiteId
     * @param userInfo
     * @return
     */
    boolean isUserSendSms(String userId, String curSiteId, UserInfo userInfo);

    /**
     * 获取用户的邮件发送配置、不检查站点配置
     * 
     * @param userId
     * @param curSiteId
     * @param userInfo
     * @return
     */
    boolean isUserSendEmail(String userId, String curSiteId, UserInfo userInfo);

    /**
     * @description: 查询系统配置列表
     * @author: gucw
     * @createDate: 2015-9-06
     * @param userInfo
     * @param sysConf
     * @return:
     * @throws Throwable
     */
    Page<Configuration> queryBSysConfList(UserInfoScope userInfo, Configuration sysConf) throws Throwable;

    /**
     * @description: 根据conf和站点id查询系统配置
     * @author: gucw
     * @createDate: 2015-9-06
     * @param conf
     * @param siteId
     * @return:
     * @throws Throwable
     */
    Configuration queryBSysById(String conf, String siteId) throws Throwable;

    /**
     * @description: 新增或保存系统配置
     * @author: gucw
     * @createDate: 2015-9-06
     * @param sysConf
     * @return:
     * @throws Throwable
     */
    Map<String, Object> saveOrUpdateSysConf(UserInfoScope userInfo, Configuration sysConf, Configuration oldSysConf)
            throws Throwable;

    /**
     * @description: 删除系统配置
     * @author: gucw
     * @createDate: 2015-9-07
     * @param sysConf
     * @return:
     * @throws Throwable
     */
    Map<String, Object> deleteSysConf(UserInfoScope userInfo, List<Configuration> selectList) throws Throwable;
}
