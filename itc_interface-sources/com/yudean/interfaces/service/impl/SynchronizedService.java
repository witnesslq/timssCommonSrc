package com.yudean.interfaces.service.impl;

import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.yudean.interfaces.dao.EsbInterfaceDao;
import com.yudean.interfaces.service.impl.sync.SyncEsbUserOrgOneSite;
import com.yudean.itc.code.StatusCode;
import com.yudean.itc.dto.interfaces.sync.SyncConfBean;

/**
 * 统一数据交换平台人事数据同步
 * 
 * @company: gdyd
 * @className: FrameworkScheduler.java
 * @author: kChen
 * @createDate: 2014-7-29
 * @updateUser: kChen
 * @version: 1.0
 */
@Component
public class SynchronizedService implements ApplicationContextAware {
    @Autowired
    EsbInterfaceDao esbInterfaceDao;

    @Autowired
    EsbInterfaceService esbInterfaceService;

    private ApplicationContext ctx;

    /**
     * 同步人事数据
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-7-29:
     */
    public void syncEsbUserOrg(String operUserId) {
        SyncConfBean syncConfBean = new SyncConfBean();
        List<SyncConfBean> syncConfBeanlist = esbInterfaceDao.getConf( syncConfBean );
        for ( SyncConfBean bean : syncConfBeanlist ) {
            if ( 0 == StatusCode.YES.compareTo( bean.getActive() ) ) {
                syncEsbUserOrgOneSite( bean, operUserId );
            }
        }
        // 2016-03-10 modify by yuanzh 全部站点都执行完同步之后执行一次批量删除过期的数据
        SyncEsbUserOrgOneSite syncDel = ctx.getBean( SyncEsbUserOrgOneSite.class );
        syncDel.deleteAdtSecUserJustKeepOneMonth();

        // 2016-06-21 modify by yuanzh 同步数据后将更新一下只有一个站点的用户默认站点信息
        syncDel.updateUserConfigSite();
    }

    /**
     * 同步单个站点的人事数据
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-9-29
     * @param syncConfBean :
     */
    private void syncEsbUserOrgOneSite(SyncConfBean syncConfBean, String operUserId) {
        SyncEsbUserOrgOneSite site = ctx.getBean( SyncEsbUserOrgOneSite.class );
        site.setSyncConfBean( syncConfBean );
        site.sync( operUserId );
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx = applicationContext;
    }
}
