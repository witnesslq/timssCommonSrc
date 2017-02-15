package com.yudean.homepage.web;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yudean.homepage.bean.NoticeBean;
import com.yudean.homepage.service.HomepagePortalService;
import com.yudean.homepage.vo.SiteActiveInfoVo;
import com.yudean.mvc.bean.userinfo.UserInfo;
import com.yudean.mvc.bean.userinfo.UserInfoScope;
import com.yudean.mvc.service.ItcMvcService;
import com.yudean.mvc.util.ViewUtil;
import com.yudean.mvc.view.ModelAndViewAjax;

/**
 * 用于Portal卡片的相关controller。
 * 
 * @author kchen
 */
@Controller
@RequestMapping(value = "homepage/noticeInfo")
public class HomepagePortalController {
    private static final Logger LOG = Logger.getLogger( HomepagePortalController.class );

    private static final int columnNum = 9;

    @Autowired
    private HomepagePortalService noticeService;

    @Autowired
    private ItcMvcService mvcService;

    @RequestMapping(value = "/noticeList")
    /**
     * 获取通知信息列表
     * @return
     */
    public Map<String, Object> noticeListPage() {
        boolean isGenSuc = false;
        JSONArray json = new JSONArray();
        int compare = 0;
        SimpleDateFormat format = null;
        try {
            UserInfo userInfo = mvcService.getUserInfoScopeDatas();
            List<NoticeBean> list = noticeService.getUserNotice( userInfo, columnNum );
            if ( null != list && list.size() > 0 ) {
                for ( NoticeBean bean : list ) {
                    JSONObject object = new JSONObject();
                    object.put( "content", "\"" + bean.getContent() + "\" 转为\"" + bean.getStatusName() + "\"状态" );
                    object.put( "status", bean.getStatus() );
                    object.put( "statusname", bean.getStatusName() );

                    compare = daysBetween( bean.getStatusdate(), new Date() );
                    if ( compare == 0 ) {
                        format = new SimpleDateFormat( "今天  HH:mm" );
                    } else if ( compare == -1 ) {
                        format = new SimpleDateFormat( "明天  HH:mm" );
                    } else if ( compare == 1 ) {
                        format = new SimpleDateFormat( "昨天  HH:mm" );
                    } else {
                        format = new SimpleDateFormat( "MM.dd  HH:mm" );
                    }
                    object.put( "statusdate", format.format( bean.getStatusdate() ) );

                    object.put( "url", bean.getOperUrl() );
                    object.put( "name", bean.getName() );
                    object.put( "id", bean.getCode() );
                    json.add( object );
                }
            }
            isGenSuc = true;
        } catch (Exception e) {
            LOG.error( "获取最新动态信息异常", e );
        }
        Map<String, Object> retMap = new HashMap<String, Object>();
        if ( isGenSuc ) {
            retMap.put( "status", "ok" );
            retMap.put( "data", json );
        } else {
            retMap.put( "status", "error" );
        }
        return retMap;
    }

    @RequestMapping(value = "/noActiveNotice")
    public Map<String, Object> noActiveNotice() {
        boolean isGenSuc = false;
        try {
            UserInfoScope scope = mvcService.getUserInfoScopeDatas();
            String id = scope.getParam( "ID" );
            noticeService.completeNotice( id, scope.getSiteId(), scope );
            isGenSuc = true;
        } catch (Exception e) {
            LOG.error( "处理通知信息异常", e );
        }
        Map<String, Object> retMap = new HashMap<String, Object>();
        if ( isGenSuc ) {
            retMap.put( "status", "ok" );
        } else {
            retMap.put( "status", "error" );
        }
        return retMap;
    }

    @RequestMapping(value = "/siteUserActiveInfoTest")
    public ModelAndViewAjax siteUserActiveInfoTest() {
        JSONObject retJsonObj = new JSONObject();
        retJsonObj.put( "status", "ok" );

        JSONArray seriesJson = new JSONArray();

        JSONObject city1 = new JSONObject();
        city1.put( "name", "ITC" );
        JSONArray city1Data = new JSONArray();
        city1Data.add( 40 );
        city1Data.add( 66 );
        city1Data.add( 55 );
        city1Data.add( 44 );
        city1Data.add( 67 );
        city1Data.add( 36 );

        JSONObject point = new JSONObject();
        point.put( "y", 78 );
        point.put( "xAxisLable", "3/28" );

        city1Data.add( point );
        city1.put( "data", city1Data );
        seriesJson.add( city1 );

        JSONObject city2 = new JSONObject();
        city2.put( "name", "SBS" );
        JSONArray city2Data = new JSONArray();
        city2Data.add( 50 );
        city2Data.add( 20 );
        city2Data.add( 15 );
        city2Data.add( 15 );
        city2Data.add( 20 );
        city2Data.add( 23 );
        city2Data.add( 64 );
        city2.put( "data", city2Data );
        seriesJson.add( city2 );

        JSONObject city3 = new JSONObject();
        city3.put( "name", "HYC" );
        JSONArray city3Data = new JSONArray();
        city3Data.add( 23 );
        city3Data.add( 12 );
        city3Data.add( 4 );
        city3Data.add( 6 );
        city3Data.add( 9 );
        city3Data.add( 7 );
        city3Data.add( 2 );
        city3.put( "data", city3Data );
        seriesJson.add( city3 );

        JSONObject dataJson = new JSONObject();
        dataJson.put( "series", seriesJson );

        JSONArray xAxisLableJson = new JSONArray();
        xAxisLableJson.add( "3/8" );
        xAxisLableJson.add( "3/9" );
        xAxisLableJson.add( "3/10" );
        dataJson.put( "xAxisLable", xAxisLableJson );

        retJsonObj.put( "data", dataJson );

        int count = 0;
        Calendar curCal = Calendar.getInstance();
        curCal.setTime( new Date() );
        int curDay = 0;
        while (true) {
            curDay = curCal.get( Calendar.DAY_OF_YEAR ) - 1;
            curCal.set( Calendar.DAY_OF_YEAR, curDay );
            curCal.getTime();
            curDay = curCal.get( Calendar.DAY_OF_YEAR );
            if ( ++count > 7 ) {
                break;
            }
        }

        return ViewUtil.Json( retJsonObj );
    }

    @RequestMapping(value = "/siteUserActiveInfo")
    public SiteActiveInfoVo siteUserActiveInfo() {
        SiteActiveInfoVo vo = noticeService.getSiteUserActiveInfo();
        return vo;
    }

    /*
     * 截断字符串
     */
    /*
     * private String subContent(String content) { final int strLen = 40; try {
     * if ( content.length() > strLen ) { content = content.substring( 0, strLen
     * ) + "..."; } } catch (Exception e) { } return content; }
     */

    /**
     * @description: 两个日期相差天数
     * @author: yuanzh
     * @createDate: 2015-9-10
     * @param smdate
     * @param bdate
     * @return
     * @throws ParseException:
     */
    private static int daysBetween(Date smdate, Date bdate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd" );
        smdate = sdf.parse( sdf.format( smdate ) );
        bdate = sdf.parse( sdf.format( bdate ) );
        Calendar cal = Calendar.getInstance();
        cal.setTime( smdate );
        long time1 = cal.getTimeInMillis();
        cal.setTime( bdate );
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt( String.valueOf( between_days ) );
    }
}
