package com.yudean.mvc.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.yudean.itc.dao.support.AppEnumMapper;
import com.yudean.itc.dto.Page;
import com.yudean.itc.dto.support.AppEnum;
import com.yudean.itc.dto.support.AppEnumCat;
import com.yudean.itc.dto.support.AppEnumCatVO;
import com.yudean.mvc.bean.userinfo.UserInfoScope;
import com.yudean.mvc.service.ItcEnumConfService;
import com.yudean.mvc.util.UtilInitProcess;

/**
 * @title: 枚举子类Service实现类
 * @description: 枚举子类Service实现类
 * @company: gdyd
 * @className: BEnumServiceImpl.java
 * @author: yuanzh
 * @createDate: 2015-8-27
 * @updateUser: yuanzh
 * @version: 1.0
 */
@Service
public class ItcEnumConfServiceImpl implements ItcEnumConfService {

    private static final Logger LOG = Logger.getLogger( ItcEnumConfServiceImpl.class );

    @Autowired
    private AppEnumMapper appEnumMapper;

    /**
     * 查询枚举类型列表实现方法
     * 
     * @throws Throwable
     * @throws Throwable,Exception
     */
    @Override
    public Page<AppEnumCatVO> queryBEnumCatList(UserInfoScope userInfo, AppEnumCatVO becv) throws Exception {
        UserInfoScope scope = userInfo;
        Page<AppEnumCatVO> page = scope.getPage();
        page.setParameter( "siteid", userInfo.getSiteId() );
        try {
            String sort = String.valueOf( scope.getParam( "sort" ) == null ? "" : scope.getParam( "sort" ) );
            String order = String.valueOf( scope.getParam( "order" ) == null ? "" : scope.getParam( "order" ) );
            if ( !"".equals( sort ) && !"".equals( order ) ) {
                page.setSortKey( sort );
                page.setSortOrder( order );
            } else {
                page.setSortKey( "MODULE_CODE" );
                page.setSortOrder( "ASC" );
            }

            if ( null != becv ) {
                page.setParameter( "moduleCode", becv.getModuleCode() );
                page.setParameter( "ecatCode", becv.getEcatCode() );
                page.setParameter( "cat", becv.getCat() );
            }
            List<AppEnumCatVO> ret = appEnumMapper.queryBEnumCatList( page );
            page.setResults( ret );
        } catch (Exception e) {
            throw new Exception(
                    "----------------- BEnumCatServiceImpl 的  queryBEnumCatList 方法抛出异常 ----------------- ", e );
        }
        return page;
    }

    /**
     * 枚举类型表单查询
     * 
     * @throws Throwable
     */
    @Override
    public AppEnumCat queryBEnumCatByCode(UserInfoScope userInfo, String ecatCode) throws Exception {
        AppEnumCat bec = null;
        AppEnumCatVO becv = new AppEnumCatVO();
        becv.setEcatCode( ecatCode );
        List<AppEnumCatVO> becvList = queryBEnumCatList( userInfo, becv ).getResults();
        if ( null != becvList && !becvList.isEmpty() ) {
            AppEnumCatVO becVO = becvList.get( 0 );
            bec = new AppEnumCat();
            bec.setEcatCode( becVO.getEcatCode() );
            bec.setCat( becVO.getCat() );
        }
        return bec;
    }

    /**
     * 删除枚举变量
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean deleteEnum(UserInfoScope userInfo, String ecatCode) throws Exception {
        boolean flag = false;

        Map<String, String> map = new HashMap<String, String>();
        map.put( "ecatCode", ecatCode );
        map.put( "siteid", userInfo.getSiteId() );
        int counter = appEnumMapper.deleteEnums( map );
        if ( counter > 0 ) {

            UserInfoScope scope = userInfo;
            Page<AppEnum> page = scope.getPage();
            page.setParameter( "code", ecatCode );
            List<AppEnum> beList = appEnumMapper.queryBEnumList( page );
            if ( null == beList || beList.isEmpty() ) {
                appEnumMapper.deleteBEnumCat( map );
            }
            flag = true;
        }
        return flag;
    }

    /**
     * 保存枚举变量
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean saveEnumInfo(UserInfoScope userInfo, AppEnumCat bec, List<AppEnum> beList) {
        LOG.debug( "------------------ 进入saveEnumInfo方法 ------------------" );
        int counter = 0;
        boolean flag = false;
        try {
            AppEnumCat becTmp = queryBEnumCatByCode( userInfo, bec.getEcatCode() );
            if ( null == becTmp ) {
                counter = appEnumMapper.insertBEnumCat( bec );
            } else {
                counter = appEnumMapper.updateBEnumCat( bec );
            }

            if ( counter > 0 ) {
                Map<String, String> map = new HashMap<String, String>();
                map.put( "ecatCode", bec.getEcatCode() );
                map.put( "siteid", userInfo.getSiteId() );
                appEnumMapper.deleteEnums( map );
                for ( AppEnum be : beList ) {
                    be.setCategoryCode( bec.getEcatCode() );
                    be.setSiteId( userInfo.getSiteId() );
                    appEnumMapper.insertEnums( be );
                }

                // 将数据刷新加入到内存
                List<AppEnum> enums = appEnumMapper.selectEnumsByCat( bec.getEcatCode() );
                if ( null != enums && !enums.isEmpty() ) {
                    for ( AppEnum ae : enums ) {
                        UtilInitProcess.RemoveTimssEnumUtil( ae );
                    }
                    UtilInitProcess.InitTimssEnumUtil( enums );
                }
            }
            flag = true;
        } catch (Exception e) {
            LOG.debug( "------------------ BEnumCatServiceImpl 的 saveEnumInfo 方法抛出异常 ：------------------", e );
        }
        return flag;
    }

    /**
     * 查询枚举子类列表
     * 
     * @throws Throwable
     */
    @Override
    public Page<AppEnum> queryBEnumList(UserInfoScope userInfo, String ecatCode, String enumType) throws Throwable {
        UserInfoScope scope = userInfo;
        Page<AppEnum> page = scope.getPage();

        try {
            page.setParameter( "siteId", enumType );
            page.setParameter( "code", ecatCode );
            page.setSortKey( "SORT_NUM" );
            page.setSortOrder( "ASC" );

            List<AppEnum> ret = appEnumMapper.queryBEnumList( page );
            page.setResults( ret );
        } catch (Exception e) {
            throw new Exception( "----------------- BEnumServiceImpl 的  queryBEnumList 方法抛出异常 ----------------- ", e );
        }
        return page;
    }
}
