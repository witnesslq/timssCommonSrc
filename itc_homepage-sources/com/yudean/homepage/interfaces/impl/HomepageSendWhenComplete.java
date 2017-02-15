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

@Component("HomepageSendWhenComplete")
public class HomepageSendWhenComplete extends HomepageNotifyCommon {
    private final String s_EmailTempleteName = "hop_TaskComplete";
    static private String s_SmsTempleteName = "hop_TaskComplete";
    private static final String SMSNOTICE = "hop_notify_send_sms_flag";
    private static final Logger log = Logger.getLogger( HomepageSendWhenComplete.class );

    @Override
    public void notify(String flow, String processInstId, String typeName, String name, String statusName, String url,
            List<String> userList, UserInfo userInfo, WorktaskBean taskBean) {
    	if(userList == null){
    		log.info("用户列表为空，不需要发送邮件或短信");
    		return;
    	}
        if ( itcSiteService.isMailSend( userInfo.getSiteId() ) ) {
            if ( sysConfService.isUserSendEmail( taskBean.getCreateuser(), userInfo.getSiteId(), userInfo ) ) {
                UserInfo user = itcMvcService.getUserInfoById( taskBean.getCreateuser() );
                Map<String, Object> bindMap = new HashMap<String, Object>();
                List<UserInfo> recipients = new ArrayList<UserInfo>();
                recipients.add( user );
                bindMap.put( "task_createusername", taskBean.getCreateusername() );
                bindMap.put( "task_type", taskBean.getTypename() );
                bindMap.put( "task_name", taskBean.getName() );
                itcMsgService.SendMailImm( s_EmailTempleteName, bindMap, recipients, userInfo );
            } else {
                log.info( userInfo.getUserId() + "沒有配置发送邮件功能" );
            }
        } else {
            log.info( userInfo.getSiteId() + "站点没有配置发送邮件功能" );
        }

        if ( itcSiteService.isSMSSend( userInfo.getSiteId() ) ) {
            // 发送短信
            List<UserInfo> recipientsSms = new ArrayList<UserInfo>();
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

    @Override
    public void notifyWithSetUser(String flow, String processInstId, String typeName, String name, String statusName,
            String url, Set<UserInfo> operUser, UserInfo userInfo, WorktaskBean taskBean) throws RuntimeException {

        if ( itcSiteService.isMailSend( userInfo.getSiteId() ) ) {
            if ( sysConfService.isUserSendEmail( taskBean.getCreateuser(), userInfo.getSiteId(), userInfo ) ) {
                UserInfo user = itcMvcService.getUserInfoById( taskBean.getCreateuser() );
                Map<String, Object> bindMap = new HashMap<String, Object>();
                List<UserInfo> recipients = new ArrayList<UserInfo>();
                recipients.add( user );
                bindMap.put( "task_createusername", taskBean.getCreateusername() );
                bindMap.put( "task_type", taskBean.getTypename() );
                bindMap.put( "task_name", taskBean.getName() );
                itcMsgService.SendMailImm( s_EmailTempleteName, bindMap, recipients, userInfo );
            } else {
                log.info( userInfo.getUserId() + "沒有配置发送邮件功能" );
            }
        } else {
            log.info( userInfo.getSiteId() + "站点没有配置发送邮件功能" );
        }

        if ( itcSiteService.isSMSSend( userInfo.getSiteId() ) ) {
            // 发送短信
            List<UserInfo> recipientsSms = new ArrayList<UserInfo>();
            String userCode = null;
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
