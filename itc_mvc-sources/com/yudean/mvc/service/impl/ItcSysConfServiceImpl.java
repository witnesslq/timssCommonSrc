package com.yudean.mvc.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.yudean.itc.code.LogType;
import com.yudean.itc.code.StatusCode;
import com.yudean.itc.dao.support.SystemConfigurationMapper;
import com.yudean.itc.dto.Page;
import com.yudean.itc.dto.support.AppLog;
import com.yudean.itc.dto.support.Configuration;
import com.yudean.itc.dto.support.ConstructLogVo;
import com.yudean.itc.manager.sec.IAuthorizationManager;
import com.yudean.itc.manager.support.IConfigurationManager;
import com.yudean.itc.manager.support.LogTempService;
import com.yudean.itc.util.map.MapHelper;
import com.yudean.mvc.bean.userinfo.UserInfo;
import com.yudean.mvc.bean.userinfo.UserInfoScope;
import com.yudean.mvc.service.ItcMvcService;
import com.yudean.mvc.service.ItcSysConfService;

@Service
public class ItcSysConfServiceImpl implements ItcSysConfService {
    private static final Logger LOG = Logger.getLogger( ItcSysConfServiceImpl.class );
    final static private String S_SENDFLAG = "hop_notify_send_flag";
    final static private String S_EMAIL_CONF_NAME = "mailNotice";
    final static private String S_SMS_CONF_NAME = "smsNotice";
    final static private String S_DEFAULT_SITE = "NaN";

    @Autowired
    ItcMvcService itcMvcService;

    @Autowired
    IAuthorizationManager authorizationManager;

    @Autowired
    IConfigurationManager configurationManager;

    @Autowired
    SystemConfigurationMapper systemConfigurationMapper;

    @Autowired
    LogTempService logTempService;

    @Override
    public boolean isUserSendSmsSite(String userId, String curSiteId, UserInfo userInfo) {
        return getUserFlagBySite( userId, curSiteId, S_SMS_CONF_NAME );
    }

    @Override
    public boolean isUserSendEmailSite(String userId, String curSiteId, UserInfo userInfo) {
        return getUserFlagBySite( userId, curSiteId, S_EMAIL_CONF_NAME );
    }

    @Override
    public boolean isSiteSend(String curSiteId, UserInfo userInfo) {
        return getSiteSendFlag( curSiteId );
    }

    @Override
    public boolean isUserSendSms(String userId, String curSiteId, UserInfo userInfo) {
        return getUserFlag( userId, curSiteId, S_SMS_CONF_NAME );
    }

    @Override
    public boolean isUserSendEmail(String userId, String curSiteId, UserInfo userInfo) {
        return getUserFlag( userId, curSiteId, S_EMAIL_CONF_NAME );
    }

    /*
     * 获取站点的配置信息
     */
    private boolean getSiteSendFlag(String curSiteId) {
        Configuration conf = configurationManager.query( S_SENDFLAG, curSiteId, S_DEFAULT_SITE );
        StatusCode sysSendConf = StatusCode.NO;
        try {
            sysSendConf = StatusCode.valueOf( conf.getVal() );
        } catch (IllegalArgumentException e) {
            LOG.error( "获取配置变量异常，默认关闭变量", e );
        }
        boolean isSend = false;
        switch (sysSendConf) {
            case Y:
            case YES: {
                isSend = true;
                break;
            }
            case N:
            case NO:
            default: {
                // TODO
            }
        }
        return isSend;
    }

    /*
     * 获取用户的配置信息，不包括站点
     */
    private boolean getUserFlag(String userId, String curSiteId, String flag) {
        boolean isSend = false;
        try {
            String curflag = authorizationManager.retrieveUserConfig( userId, flag );
            curflag = null == curflag ? StatusCode.N.name() : curflag;
            isSend = StatusCode.Y.equals( StatusCode.valueOf( curflag ) );
        } catch (IllegalArgumentException e) {
            LOG.error( "获取用户配置变量异常，默认关闭变量。 userId:" + userId, e );
        }
        return isSend;
    }

    /*
     * 获取用户的配置信息，优先检查站点
     */
    private boolean getUserFlagBySite(String userId, String curSiteId, String flag) {
        if ( getSiteSendFlag( curSiteId ) ) {
            return getUserFlag( userId, curSiteId, flag );
        } else {
            return false;
        }
    }

    /**
     * 查询枚举子类列表
     * 
     * @throws Throwable
     */
    @Override
    public Page<Configuration> queryBSysConfList(UserInfoScope userInfo, Configuration sysConf) throws Throwable {
        UserInfoScope scope = userInfo;
        Page<Configuration> page = scope.getPage();
        // 查询参数处理
        Map<String, String[]> params = userInfo.getParamMap();
        Map<String, String> propertyColumnMap = new HashMap<String, String>( 0 );
        propertyColumnMap.put( "conf", "CONF" );
        propertyColumnMap.put( "val", "VAL" );
        propertyColumnMap.put( "updatedBy", "UPDATED_BY" );
        propertyColumnMap.put( "updateTime", "UPDATE_TIME" );
        propertyColumnMap.put( "desp", "DESP" );
        propertyColumnMap.put( "siteId", "SITEID" );
        if ( params.containsKey( "search" ) ) {
            String fuzzySearchParams = userInfo.getParam( "search" );
            LOG.info( "查询系统配置参数的条件：" + fuzzySearchParams );
            // 调用工具类将jsonString转为HashMap
            Map<String, Object> fuzzyParams = MapHelper.jsonToHashMap( fuzzySearchParams );
            // 如果Dao中使用resultMap来将查询结果转化为bean，则需要调用此工具类将Map中的key做转换，变成数据库可以识别的列名
            fuzzyParams = MapHelper.fromPropertyToColumnMap( (HashMap<String, Object>) fuzzyParams, propertyColumnMap );
            // 自动的会封装模糊搜索条件
            page.setFuzzyParams( fuzzyParams );

        }
        // 设置排序内容
        if ( params.containsKey( "sort" ) ) {
            String sortKey = userInfo.getParam( "sort" );
            // 如果Dao中使用resultMap来将查询结果转化为bean，则需要将Map中的key做转换，变成数据库可以识别的列名
            sortKey = propertyColumnMap.get( sortKey );
            page.setSortKey( sortKey );
            page.setSortOrder( userInfo.getParam( "order" ) );
        } else {
            // 设置默认的排序字段
            page.setSortKey( "updateTime" );
            page.setSortOrder( "desc" );
        }
        try {
            List<Configuration> ret = systemConfigurationMapper.querySysList( page );
            page.setResults( ret );
        } catch (Exception e) {
            throw new Exception( "----------------- SysConfServiceImpl 的  queryBSysConfList 方法抛出异常 ----------------- ",
                    e );
        }
        return page;
    }

    @Override
    public Configuration queryBSysById(String conf, String siteid) throws Throwable {
        Configuration sysConf = null;
        if ( StringUtils.isNotEmpty( conf ) && StringUtils.isNotEmpty( siteid ) ) {
            Configuration sysConfCondition = new Configuration();
            sysConfCondition.setConf( conf );
            sysConfCondition.setsiteId( siteid );
            List<Configuration> sysConfs = systemConfigurationMapper.querySysConfByCondition( sysConfCondition );
            if ( 1 == sysConfs.size() ) {
                sysConf = sysConfs.get( 0 );
            }
        }
        return sysConf;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
    public Map<String, Object> saveOrUpdateSysConf(UserInfoScope userInfo, Configuration sysConf,
            Configuration oldSysConf) throws Throwable {
        String updatedBy = userInfo.getUserId();
        Date updateTime = new Date();
        String siteId = userInfo.getSiteId();
        Map<String, Object> resultMap = new HashMap<String, Object>( 0 );
        int result = 0;
        if ( StringUtils.isEmpty( sysConf.getsiteId() ) ) {
            sysConf.setsiteId( "NaN" );
        }
        ConstructLogVo vo = new ConstructLogVo();
        vo.setModule( "sysconf" );
        vo.setSiteId( siteId );
        vo.setUserId( updatedBy );
        vo.setKeyWord( "conf" );
        vo.setPropertiesPath( "sysconf-properties.properties" );
        if ( null == oldSysConf ) {
            // 新增
            // 检查是否重复
            List<Configuration> sysConfs = systemConfigurationMapper.querySysConfByCondition( sysConf );
            if ( 0 < sysConfs.size() ) {
                resultMap.put( "msg", "该站点已有该配置项" );
                resultMap.put( "success", false );
                return resultMap;
            }
            sysConf.setUpdatedBy( updatedBy );
            sysConf.setUpdateTime( updateTime );
            result = systemConfigurationMapper.insertSys( sysConf );
            vo.setLogType( Integer.parseInt( LogType.CREATE.toString() ) );
            Configuration emptySysConf = new Configuration();
            logTempService.insertLogForDiffBean( vo, emptySysConf, sysConf );
        } else {
            // 更新
            // 检查是否重复
            List<Configuration> sysConfs = systemConfigurationMapper.querySysConfByCondition( sysConf );
            if ( 1 == sysConfs.size() && oldSysConf.getConf().equals( sysConf.getConf() )
                    && oldSysConf.getsiteId().equals( sysConf.getsiteId() ) ) {
                LOG.info( "站点、配置项名不变的修改" );
            } else if ( 0 < sysConfs.size() ) {
                resultMap.put( "msg", "该站点已有该配置项" );
                resultMap.put( "success", false );
                return resultMap;
            }
            sysConf.setUpdatedBy( updatedBy );
            sysConf.setUpdateTime( updateTime );
            result = systemConfigurationMapper.updateSys( sysConf, oldSysConf );
            vo.setLogType( Integer.parseInt( LogType.UPDATE.toString() ) );
            logTempService.insertLogForDiffBean( vo, oldSysConf, sysConf );
        }
        if ( 0 < result ) {
            resultMap.put( "success", true );
        } else {
            resultMap.put( "success", false );
        }
        return resultMap;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
    public Map<String, Object> deleteSysConf(UserInfoScope userInfo, List<Configuration> selectList) throws Throwable {
        String updatedBy = userInfo.getUserId();
        String siteId = userInfo.getSiteId();
        Map<String, Object> resultMap = new HashMap<String, Object>( 0 );
        int result = 0;
        for ( Configuration sysConf : selectList ) {
            systemConfigurationMapper.deleteSys( sysConf );
            result++;
        }
        if ( 0 < result ) {
            resultMap.put( "success", true );
            for ( Configuration sysConf : selectList ) {
                AppLog appLog = new AppLog();
                appLog.setAttr1( siteId );
                appLog.setAttr2( "sysconf" );
                appLog.setAttr3( sysConf.getConf() );
                appLog.setCategoryId( Integer.parseInt( LogType.DELETE.toString() ) );
                appLog.setDescription( "删除b_sysconf记录" );
                appLog.setOperator( updatedBy );
                appLog.setOperationTime( new Date() );
                logTempService.insertLog( appLog );
            }
        } else {
            resultMap.put( "success", false );
        }
        return resultMap;
    }
}
