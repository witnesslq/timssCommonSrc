package com.yudean.mvc.scheduler.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.yudean.itc.dto.support.Attachment;
import com.yudean.itc.dto.support.Configuration;
import com.yudean.itc.manager.support.IAttachmentManager;
import com.yudean.itc.manager.support.IConfigurationManager;

/**
 * 定时器先不开启，由于会造成文档转换系统的压力，所以要先观察没有发送或者转换没有成功的数据量有多少在决定是否开启定时任务
 * 
 * @author yuanzh
 */
@Component
@Lazy(false)
public class Attachment2TransDocumentScheduler {

    private static Logger LOG = Logger.getLogger( Attachment2TransDocumentScheduler.class );

    @Autowired
    private IAttachmentManager attachManager;

    @Autowired
    IConfigurationManager iConfigurationManager;

    @Scheduled(cron = "0 57 * * * ?")
    public void reissue2RemoteServer() {
        LOG.info( ">>>>>>>>>>>>>>>>>>>>>>>> 开始执行定时器发送没有发送成功的附件..." );
        try {
            // 获取接收图片的服务器方法与地址
            Configuration siteConfig = iConfigurationManager.query( "interface_TranDocument_notifyURL", "NaN", "NaN" );
            // 查询没有发送成功的附件
            List<Attachment> attachList = attachManager.queryAttachMentByStatus( "SEND_FALSE", 3, "SEND" );
            if ( null != attachList && !attachList.isEmpty() ) {
                for ( Attachment attach : attachList ) {
                    attachManager.uploadAttachment2TranSys( attach.getId(), siteConfig.getVal() );
                }
            }
            LOG.info( ">>>>>>>>>>>>>>>>>>>>>>>> 完成执行定时器发送没有发送成功的附件..." );
        } catch (Exception e) {
            LOG.info( "定时器执行失败：" + e.getStackTrace() );
        }
    }

    // @Scheduled(cron = "0 0/15 * * * ?")
    public void reacquire4RemoteServer() {
        LOG.info( ">>>>>>>>>>>>>>>>>>>>>>>> 开始执行定时器获取没有接收成功的附件..." );
        List<Attachment> attachList = attachManager.queryAttachMentByStatus( "'RECEIVE_FALSE','SEND_SUCCESS'", 0,
                "RECEIVE" );
        if ( null != attachList && !attachList.isEmpty() ) {
            for ( Attachment attach : attachList ) {
                attachManager.getPicFromArchiveInfo( attach.getFileId(), attach, null );
            }
        }
        LOG.info( ">>>>>>>>>>>>>>>>>>>>>>>> 完成执行定时器获取没有接收成功的附件..." );
    }
}
