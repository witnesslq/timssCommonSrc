package com.yudean.homepage.interfaces.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.yudean.homepage.bean.WorktaskBean;
import com.yudean.mvc.bean.userinfo.UserInfo;
import com.yudean.mvc.configs.MvcWebConfig;
import com.yudean.mvc.util.LogUrlUtil;

/**
 * 流程扭转时发送 邮件通知等消息
 * 
 * @title: {title}
 * @description: {desc}
 * @company: gdyd
 * @className: HomepageSendWhenProcess.java
 * @author: kChen
 * @createDate: 2014-7-24
 * @updateUser: kChen
 * @version: 1.0
 */

@Component("HomepageSendWhenProcess")
public class HomepageSendWhenProcess extends HomepageNotifyCommon {
    private static final Logger log = Logger.getLogger( HomepageSendWhenProcess.class );
    static private String s_EmailTempleteName = "hop_TaskDoing";
    static private String s_SmsTempleteName = "hop_TaskDoing";
    private static final String SMSNOTICE = "hop_notify_send_sms_flag";

    @Override
    public void notify(String flow, String processInstId, String typeName, String name, String statusName, String url,
            List<String> userList, UserInfo userInfo, WorktaskBean taskBean) {
        List<UserInfo> recipients = new ArrayList<UserInfo>();
        List<UserInfo> recipientsSms = new ArrayList<UserInfo>();
        if ( itcSiteService.isMailSend( userInfo.getSiteId() ) ) {
            log.info( "current site " + userInfo.getSiteId() + " smssetting is true." );
            for ( String userCode : userList ) {
                if ( sysConfService.isUserSendEmail( userCode, userInfo.getSiteId(), userInfo ) ) {
                    log.info( "current user smssetting is true." );
                    UserInfo user = itcMvcService.getUserInfoById( userCode );
                    recipients.add( user );
                    Map<String, Object> bindMap = new HashMap<String, Object>();
                    bindMap.put( "task_createusername", taskBean.getCreateusername() );
                    bindMap.put( "task_type", taskBean.getTypename() );
                    bindMap.put( "task_name", taskBean.getName() );
                    bindMap.put( "task_createdeptname", taskBean.getDeptname() );
                    bindMap.put( "task_createusercode", taskBean.getCreateuser() );
                    String loginUrl = MvcWebConfig.serverBasePath;
                    try {
                        loginUrl = LogUrlUtil.loginWithOpenWorkflow( MvcWebConfig.serverBasePath, taskBean.getUrl(),
                                taskBean.getFlowno(), taskBean.getSiteid(), userCode,
                                authorizationManager.getPassword( userCode ) );
                    } catch (Exception e) {
                        log.error( "用户" + user.getUserId() + "生成登陆URL异常", e );
                    }
                    bindMap.put( "task_url", loginUrl );
                    bindMap.put( "task_realurl", MvcWebConfig.serverBasePath );

                    itcMsgService.SendMailImm( s_EmailTempleteName, bindMap, recipients, userInfo );
                } else {
                    log.info( "current user " + userCode + " smssetting is false." );
                }
            }
        } else {
            log.info( "current site " + userInfo.getSiteId() + " smssetting is false." );
        }
        if ( itcSiteService.isSMSSend( userInfo.getSiteId() ) ) {
            // 发送短信
            for ( String userCode : userList ) {
                if ( sysConfService.isUserSendSms( userCode, userInfo.getSiteId(), userInfo ) ) {
                    UserInfo user = itcMvcService.getUserInfoById( userCode );
                    recipientsSms.add( user );
                } else {
                    log.info( userCode + "没有配置短信发送功能。" );
                }
            }
            Map<String, Object> bindMap = new HashMap<String, Object>();
            bindMap.put( "task_createusername", taskBean.getCreateusername() );
            bindMap.put( "task_type", taskBean.getTypename() );
            bindMap.put( "task_name", taskBean.getName() );
            bindMap.put( "task_createdeptname", taskBean.getDeptname() );
            bindMap.put( "task_createusercode", taskBean.getCreateuser() );
            itcMsgService.SendSms( s_SmsTempleteName, bindMap, recipientsSms, userInfo );
        } else {
            log.info( userInfo.getSiteId() + "站点没有配置短信发送功能。" );
        }

    }

    /**
     * 批量修改时新增方法 created by yuanzh 2016-6-23
     */
    @Override
    public void notifyWithSetUser(String flow, String processInstId, String typeName, String name, String statusName,
            String url, Set<UserInfo> operUser, UserInfo userInfo, WorktaskBean taskBean) throws RuntimeException {

        List<UserInfo> recipients = new ArrayList<UserInfo>();
        List<UserInfo> recipientsSms = new ArrayList<UserInfo>();
        String userCode = null;

        if ( itcSiteService.isMailSend( userInfo.getSiteId() ) ) {
            log.info( "current site " + userInfo.getSiteId() + " smssetting is true." );

            for ( UserInfo userI : operUser ) {
                userCode = userI.getUserId();
                if ( sysConfService.isUserSendEmail( userCode, userInfo.getSiteId(), userInfo ) ) {
                    log.info( "current user smssetting is true." );
                    // modify by yuanzh 2016-6-23
                    // 传入的Userinfo中已经有用户信息了就不需要重新查询了
                    // UserInfo user = itcMvcService.getUserInfoById( userCode
                    // );
                    // recipients.add( user );
                    recipients.add( userI );
                    Map<String, Object> bindMap = new HashMap<String, Object>();
                    bindMap.put( "task_createusername", taskBean.getCreateusername() );
                    bindMap.put( "task_type", taskBean.getTypename() );
                    bindMap.put( "task_name", taskBean.getName() );
                    bindMap.put( "task_createdeptname", taskBean.getDeptname() );
                    bindMap.put( "task_createusercode", taskBean.getCreateuser() );
                    String loginUrl = MvcWebConfig.serverBasePath;
                    try {
                        loginUrl = LogUrlUtil.loginWithOpenWorkflow( MvcWebConfig.serverBasePath, taskBean.getUrl(),
                                taskBean.getFlowno(), taskBean.getSiteid(), userCode,
                                authorizationManager.getPassword( userCode ) );
                    } catch (Exception e) {
                        log.error( "用户" + userCode + "生成登陆URL异常", e );
                    }
                    bindMap.put( "task_url", loginUrl );
                    bindMap.put( "task_realurl", MvcWebConfig.serverBasePath );

                    itcMsgService.SendMailImm( s_EmailTempleteName, bindMap, recipients, userInfo );
                } else {
                    log.info( "current user " + userCode + " smssetting is false." );
                }
            }
        } else {
            log.info( "current site " + userInfo.getSiteId() + " smssetting is false." );
        }
        if ( itcSiteService.isSMSSend( userInfo.getSiteId() ) ) {
            // 发送短信
            for ( UserInfo userI : operUser ) {
                userCode = userI.getUserId();
                if ( sysConfService.isUserSendSms( userCode, userInfo.getSiteId(), userInfo ) ) {
                    UserInfo user = itcMvcService.getUserInfoById( userCode );
                    recipientsSms.add( user );
                } else {
                    log.info( userCode + "没有配置短信发送功能。" );
                }
            }
            Map<String, Object> bindMap = new HashMap<String, Object>();
            bindMap.put( "task_createusername", taskBean.getCreateusername() );
            bindMap.put( "task_type", taskBean.getTypename() );
            bindMap.put( "task_name", taskBean.getName() );
            bindMap.put( "task_createdeptname", taskBean.getDeptname() );
            bindMap.put( "task_createusercode", taskBean.getCreateuser() );
            itcMsgService.SendSms( s_SmsTempleteName, bindMap, recipientsSms, userInfo );
        } else {
            log.info( userInfo.getSiteId() + "站点没有配置短信发送功能。" );
        }
    }
}
