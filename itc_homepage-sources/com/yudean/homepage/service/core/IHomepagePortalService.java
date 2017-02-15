package com.yudean.homepage.service.core;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yudean.homepage.bean.NoticeBean;
import com.yudean.homepage.bean.SiteActiveData;
import com.yudean.homepage.dao.HomepageNoticeDao;
import com.yudean.homepage.exception.HomepageNoticeModifyException;
import com.yudean.homepage.exception.HomepageNoticeQueryException;
import com.yudean.homepage.service.HomepagePortalService;
import com.yudean.homepage.vo.SiteActiveInfoDataVo;
import com.yudean.homepage.vo.SiteActiveInfoSeriesVo;
import com.yudean.homepage.vo.SiteActiveInfoVo;
import com.yudean.itc.annotation.MethodCache;
import com.yudean.itc.code.CacheType;
import com.yudean.itc.code.ParamConfig;
import com.yudean.itc.code.StatusCode;
import com.yudean.itc.dto.support.Configuration;
import com.yudean.itc.manager.support.IConfigurationManager;
import com.yudean.mvc.bean.userinfo.UserInfo;

@Service
public class IHomepagePortalService implements HomepagePortalService {
    private static final Logger LOG = Logger.getLogger( IHomepagePortalService.class );

    @Autowired
    HomepageNoticeDao noticeDao;

    @Autowired
    IConfigurationManager configManager;

    @Override
    public List<NoticeBean> getUserNotice(UserInfo userInfo, int colunmNum) throws HomepageNoticeQueryException {
        List<NoticeBean> retInfo = null;
        if ( null != userInfo ) {
            if ( 1 > colunmNum ) {
                colunmNum = 20;
            }
            try {
                retInfo = noticeDao.queryNoticeByUser( userInfo.getUserId(), userInfo.getSiteId(), StatusCode.Y,
                        colunmNum );
            } catch (RuntimeException re) {
                throw new HomepageNoticeQueryException( "执行查询异常", re );
            }
        } else {
            throw new HomepageNoticeQueryException( "参数userInfo为空" );
        }
        return retInfo;
    }

    @Override
    @Transactional(rollbackFor = { Exception.class })
    public void modifyNotice(NoticeBean noticeBean, UserInfo userInfo) throws HomepageNoticeModifyException {
        if ( null == noticeBean ) {
            throw new HomepageNoticeQueryException( "传入参数为空" );
        }
        int ret = 0;
        try {
            if ( null == noticeBean.getContent() ) {
                noticeBean.setContent( "流程" );
            }
            ret = updateAndSave( noticeBean, userInfo );
        } catch (RuntimeException e) {
            throw new HomepageNoticeQueryException( "执行更新异常", e );
        }
        if ( 1 > ret ) {
            throw new HomepageNoticeQueryException( "更新数据失败" );
        }
    }

    @Override
    @Transactional(rollbackFor = { Exception.class })
    public void completeNotice(String code, String siteId, UserInfo userInfo) throws HomepageNoticeModifyException {
        int ret = 0;
        if ( null != userInfo ) {
            NoticeBean noticeBean = new NoticeBean();
            noticeBean.setCode( code );
            noticeBean.setSiteId( siteId );
            noticeBean.setActive( StatusCode.N );
            Date date = new Date();
            noticeBean.setStatusdate( date );
            noticeBean.setUpdatedBy( userInfo.getUserId() );
            noticeBean.setUpdateTime( date );
            ret = noticeDao.updateNotic( noticeBean );
        } else {
            throw new HomepageNoticeModifyException( "传入参数异常userInfo:" + userInfo );
        }
        if ( 1 > ret ) {
            throw new HomepageNoticeQueryException( "更新数据失败" );
        }
    }

    @Override
    @MethodCache(type = CacheType.Permanent_Day)
    public SiteActiveInfoVo getSiteUserActiveInfo() {

        SiteActiveInfoVo retSiteActiveInfoVo = new SiteActiveInfoVo();// 创建返回的数据对象
        retSiteActiveInfoVo.setStatus( SiteActiveInfoVo.retStatus.err );// 设置状态

        try {
            retSiteActiveInfoVo.setData( new SiteActiveInfoDataVo() );// 创建数据结构

            final int queryLen = 15;// 查询数据数量
            final int countLen = 7;// 获取的数据数量

            Configuration site = configManager.query( "hop_site_list", "NaN", "NaN" );// 获取要统计的站点

            if ( null != site && null != site.getConf() ) {
                List<String> stSiteList = new ArrayList<String>();
                StringTokenizer token = new StringTokenizer( site.getVal(), "," );
                while (token.hasMoreTokens()) {
                    stSiteList.add( token.nextToken() );
                }
                List<SiteActiveData> siteActList = noticeDao.querySiteActiveDate( new Date(), queryLen );// 查询最近的可用统计数据

                List<Map<String, BigDecimal>> maxList = noticeDao.querySiteActiveMaxCount( new Date(), queryLen );// 查询当前统计的最大登陆次数
                BigDecimal maxBigDec = maxList.get( 0 ).get( "MAXCOUNT" );
                double max = maxBigDec.doubleValue();

                if ( max > 2000 ) {
                    max -= 1000;
                }

                List<Map<String, BigDecimal>> minList = noticeDao.querySiteActiveMinCount( new Date(), queryLen );// 查询当前统计的最小登陆次数
                BigDecimal minBigDec = minList.get( 0 ).get( "MINCOUNT" );
                double min = minBigDec.doubleValue();

                for ( SiteActiveData siteDate : siteActList ) {
                    procesSiteData( retSiteActiveInfoVo, min, max, siteDate, stSiteList );
                    if ( retSiteActiveInfoVo.getData().sizeOfXlable() == countLen ) {
                        break;
                    }
                }
                retSiteActiveInfoVo.getData().validata( stSiteList );
                retSiteActiveInfoVo.setStatus( SiteActiveInfoVo.retStatus.ok );
            }

        } catch (Exception e) {
            LOG.error( "get site active user count info error", e );
        }

        return retSiteActiveInfoVo;
    }

    /*
     * 向SiteActiveInfoVo中添加数据
     */
    private void procesSiteData(SiteActiveInfoVo retSiteActiveInfoVo, double min, double max, SiteActiveData siteDate,
            List<String> siteList) {
        if ( siteList.contains( siteDate.getSiteId() ) ) {
            SiteActiveInfoDataVo data = retSiteActiveInfoVo.getData();// 获取数据对象
            if ( !data.containsSeries( siteDate.getSiteId() ) ) {// 数据对象中不包含所定的数据则新建
                SiteActiveInfoSeriesVo serieVo = new SiteActiveInfoSeriesVo();
                serieVo.setName( siteDate.getSiteId() );
                data.addSerie( serieVo );
            }

            String subCdate = siteDate.getCdate().substring( 4 );

            int countSite = siteDate.getSitecount();
            int computSite = countSite > max ? countSite - 1000 : countSite;
            data.addXrData( siteDate.getSiteId(), subCdate, countSite );// 增加真实数据

            SiteActiveInfoSeriesVo serieVo = data.retSerie( siteDate.getSiteId() );
            double countResult = (computSite - min) / (max - min) * 100;
            DecimalFormat df = new DecimalFormat( "00.00" );
            serieVo.addData( siteDate.getCdate(), Double.valueOf( df.format( countResult ) ) );

            data.addXlable( siteDate.getCdate() );
        }
    }

    private int updateAndSave(NoticeBean noticeBean, UserInfo userInfo) throws RuntimeException {
        int ret = 0;
        if ( null != userInfo ) {
            if ( null == noticeBean.getSiteId() ) {
                noticeBean.setSiteId( userInfo.getSiteId() );
            }
            noticeBean.setStatus( noticeBean.getStatus() );
            Date date = new Date();
            noticeBean.setStatusdate( date );
            if ( null != noticeBean.getCode() && !ParamConfig.S_NULL.equals( noticeBean.getCode() )
                    && isExists( noticeBean.getCode(), noticeBean.getSiteId() ) ) {
                noticeBean.setCreatedBy( null );
                noticeBean.setCreateTime( null );
                noticeBean.setUpdatedBy( userInfo.getUserId() );
                noticeBean.setUpdateTime( date );
                ret = noticeDao.updateNotic( noticeBean );
            } else {
                noticeBean.setCreatedBy( userInfo.getUserId() );
                noticeBean.setCreateTime( date );
                noticeBean.setUpdatedBy( userInfo.getUserId() );
                noticeBean.setUpdateTime( date );
                ret = noticeDao.insertNotice( noticeBean );
            }
        } else {
            throw new HomepageNoticeModifyException( "传入参数异常userInfo:" + userInfo );
        }
        return ret;
    }

    private boolean isExists(String code, String siteId) throws RuntimeException {
        if ( null == siteId || ParamConfig.S_NULL.equals( siteId ) ) {
            siteId = ParamConfig.S_NaN;
        }
        NoticeBean bean = noticeDao.queryOneNotice( code, siteId );
        if ( null != bean ) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @description: 通过Code查询Notice是否存在
     * @author: yuanzh
     * @createDate: 2015-9-9
     * @param code
     * @param siteId
     * @return:
     */
    public NoticeBean queryNoticeByCode(String code, String siteId) {
        if ( null == siteId || ParamConfig.S_NULL.equals( siteId ) ) {
            siteId = ParamConfig.S_NaN;
        }
        return noticeDao.queryOneNotice( code, siteId );
    }
}
