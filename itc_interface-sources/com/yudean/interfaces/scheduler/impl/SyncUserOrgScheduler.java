package com.yudean.interfaces.scheduler.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.yudean.interfaces.scheduler.ISyncUserOrgScheduler;
import com.yudean.interfaces.service.impl.SynchronizedService;
import com.yudean.itc.code.ParamConfig;

@Component
@Lazy(false)
public class SyncUserOrgScheduler implements ISyncUserOrgScheduler {
    static private Logger log = Logger.getLogger( SyncUserOrgScheduler.class );
    static private final Boolean synclock = true;
    @Autowired
    SynchronizedService syncService;

    @Scheduled(cron = "0 0 4 * * ?")
    @Override
    public void sync() {
        log.info( "同步数据定时任务执行！" );
        synchronized (synclock) {
            syncService.syncEsbUserOrg( ParamConfig.SYNC_USERORG_USERID );
        }
        log.info( "同步数据定时执行完毕！" );
    }

}
