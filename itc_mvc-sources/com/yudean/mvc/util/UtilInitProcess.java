package com.yudean.mvc.util;

import static com.yudean.mvc.util.EnumUtil.DbNullFlag;
import static com.yudean.mvc.util.EnumUtil.enumMap;
import static com.yudean.mvc.util.EnumUtil.enumMapSite;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.yudean.itc.bean.enums.EnumType;
import com.yudean.itc.dto.support.AppEnum;

/**
 * 工具类初始化类
 * 
 * @title: {title}
 * @description: {desc}
 * @company: gdyd
 * @className: UtilInitProcess.java
 * @author: kChen
 * @createDate: 2014-6-25
 * @updateUser: kChen
 * @version: 1.0
 */
public class UtilInitProcess {

    private static final Logger log = Logger.getLogger( UtilInitProcess.class );

    static public void InitTimssEnumUtil(List<AppEnum> enums) {
        log.debug( "开始初始化枚举变量参数" );
        int Count = 0, CountSite = 0;
        for ( AppEnum ae : enums ) {
            String siteId = ae.getSiteId();
            if ( DbNullFlag.equals( siteId ) ) {
                InitTimssEnumUtil( ae );
                Count++;
            } else {
                InitTimssEnumUtilSite( ae );
                CountSite++;
            }
        }
        log.debug( "完成初始化枚举变量参数, 站点相关枚举变量" + CountSite + "个。站点无关枚举变量" + Count );
    }

    static private void InitTimssEnumUtil(AppEnum ae) {
        String Ccode = ae.getCategoryCode();
        List<AppEnum> list = enumMap.get( Ccode );
        if ( null == list ) {
            list = new ArrayList<AppEnum>();
            enumMap.put( Ccode, list );
        }
        list.add( ae );
    }

    static private void InitTimssEnumUtilSite(AppEnum ae) {
        String Ccode = ae.getCategoryCode();
        String siteId = ae.getSiteId();
        EnumType enumType = EnumType.getInstace( Ccode, siteId );
        List<AppEnum> list = enumMapSite.get( enumType );
        if ( null == list ) {
            list = new ArrayList<AppEnum>();
            enumMapSite.put( enumType, list );
        }
        list.add( ae );
    }

    /**
     * @description: 删除对应的枚举信息
     * @author: yuanzh
     * @createDate: 2015-9-1
     * @param ae:
     */
    static public void RemoveTimssEnumUtil(AppEnum ae) {
        String Ccode = ae.getCategoryCode();
        String siteId = ae.getSiteId();
        if ( DbNullFlag.equals( siteId ) ) {
            enumMap.remove( Ccode );
        } else {
            EnumType enumType = EnumType.getInstace( Ccode, siteId );
            enumMapSite.remove( enumType );
        }
    }

}
