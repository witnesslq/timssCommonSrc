package com.yudean.homepage.facade;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.yudean.homepage.bean.HomepageWorkTask;
import com.yudean.homepage.bean.NoticeBean;
import com.yudean.homepage.bean.WorktaskBean;
import com.yudean.homepage.bean.WorktaskUserBean;
import com.yudean.homepage.dao.HomepageWorktaskDao;
import com.yudean.homepage.dao.HomepageWorktaskUserDao;
import com.yudean.homepage.exception.HomepageNoticeModifyException;
import com.yudean.homepage.service.HomepagePortalService;
import com.yudean.homepage.service.core.IHomepageServiceImpl;
import com.yudean.itc.code.StatusCode;
import com.yudean.itc.util.UUIDGenerator;
import com.yudean.mvc.bean.userinfo.UserInfo;
import com.yudean.mvc.service.ItcMvcService;

public class NoticeFacade implements INoticeFacade {

	private static final Logger LOG = Logger.getLogger( IHomepageServiceImpl.class );
    
    @Autowired
    private HomepagePortalService homepageNoticeService;

	@Override
	public void createNotice(String content, String url, String modelName,List<String> operUser,
			UserInfo userInfo) {
		
	    NoticeBean noticeBean = new NoticeBean();	    
        noticeBean.setCode( UUIDGenerator.getUUID());
        noticeBean.setSiteId( userInfo.getSiteId() );
        noticeBean.setContent( content );
        noticeBean.setActive( StatusCode.Y );
        noticeBean.setStatusdate( new Date() );
        noticeBean.setOperUrl( url );
        noticeBean.setStatusName( null );
        noticeBean.setName( modelName );
        

        if(operUser != null && operUser.size() != 0){
        	for(String userNo : operUser){
        		noticeBean.setUserid( userNo );
        		homepageNoticeService.modifyNotice(noticeBean, userInfo);
        	}
        }else{
        	throw new HomepageNoticeModifyException("发送人员为空");
        }
	}	
}
