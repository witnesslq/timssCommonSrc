package com.yudean.homepage.scheduler.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.yudean.homepage.scheduler.HomepageScheduler;
import com.yudean.homepage.service.HomepagePortalService;

@Component
@Lazy(false)
public class HomepageSchedulerImpl implements HomepageScheduler {
	private static Logger LOG = Logger.getLogger(HomepageSchedulerImpl.class);

	@Autowired
	HomepagePortalService portalService;

	@Scheduled(cron = "0 0 2 * * ?")  //定时到每天凌晨2点扫描一次
	@Override
	public void siteActiveScheduler() {
		LOG.debug("scheduler siteActive start!");
		portalService.getSiteUserActiveInfo();
		LOG.debug("scheduler siteActive end!");
	}
}
