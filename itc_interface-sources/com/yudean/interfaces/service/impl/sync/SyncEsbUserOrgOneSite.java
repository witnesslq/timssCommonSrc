package com.yudean.interfaces.service.impl.sync;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.yudean.interfaces.service.IEsbInterfaceService;
import com.yudean.itc.code.OrganizationType;
import com.yudean.itc.code.ParamConfig;
import com.yudean.itc.code.StatusCode;
import com.yudean.itc.code.SyncLogType;
import com.yudean.itc.dto.interfaces.esb.CompBean;
import com.yudean.itc.dto.interfaces.esb.DeptBean;
import com.yudean.itc.dto.interfaces.esb.EmpBean;
import com.yudean.itc.dto.interfaces.sync.SyncConfBean;
import com.yudean.itc.dto.sec.Organization;
import com.yudean.itc.dto.sec.Role;
import com.yudean.itc.dto.sec.SecureOrgUser;
import com.yudean.itc.dto.sec.SecureUser;
import com.yudean.itc.dto.sec.Site;
import com.yudean.itc.manager.sec.ISecResourceSyncManager;
import com.yudean.itc.util.StringHelper;

/**
 * 同步单个站点数据的数据结构
 * 
 * @company: gdyd
 * @className: EsbSyncService.java
 * @author: kChen
 * @createDate: 2014-9-29
 * @updateUser: kChen
 * @version: 1.0
 */
@Component
@Scope("prototype")
public class SyncEsbUserOrgOneSite {

    private static final Logger LOG = Logger.getLogger( SyncEsbUserOrgOneSite.class );

    private SyncConfBean syncConfBean;

    @Autowired
    ISecResourceSyncManager secResourceSyncManager;

    @Autowired
    IEsbInterfaceService esbInterfaceService;

    @Autowired
    SyncUserOrgNotifySite notifySite;

    public void setSyncConfBean(SyncConfBean syncConfBean) {
        this.syncConfBean = syncConfBean;
        notifySite.init( syncConfBean.getSiteid() );
    }

    public void sync(String operUserId) {
        String siteId = syncConfBean.getSiteid();
        LOG.info( StringHelper.concat( "开始同步站点", siteId, "信息。启动时间：", ParamConfig.S_SSTIME_FORMATTER.format( new Date() ) ) );
        CompBean compBean = null;
        try {
            compBean = esbInterfaceService.getCompByShortCode( siteId );
            // 设置站点信息
            Site site = new Site();
            site.setId( compBean.getTargetCode() );
            site.setName( compBean.getShortName() );
            site.setUpdatedBy( operUserId );
            site.setUpdateTime( new Date() );
            site.setCreatedBy( operUserId );
            site.setCreateTime( new Date() );

            // 设置站点对应的部门信息
            Organization org = new Organization();
            org.setCode( compBean.getSourceCode() );
            org.setName( compBean.getTargetName() );
            org.setShortName( compBean.getShortName() );
            org.setRank( 2 );// 不知道是什么含义
            org.setSortNum( 0 );
            org.setParentCode( ParamConfig.SYNC_USERORG_COMPPARENTCODE );// 无父节点
            org.setUpdatedBy( operUserId );
            org.setUpdateTime( new Date() );
            org.setCreatedBy( operUserId );
            org.setCreateTime( new Date() );
            org.setSyncInd( StatusCode.Y );
            org.setType( OrganizationType.Comp.toString() );

            // 设置站点基本角色
            Role role = new Role();
            role.setId( syncConfBean.getRoleid() );
            role.setName( syncConfBean.getRolename() );
            role.setActive( StatusCode.YES );
            role.setSiteId( siteId );
            role.setUpdatedBy( operUserId );
            role.setUpdateTime( new Date() );

            int ret = secResourceSyncManager.syncSite( site, org );
            if ( 1 > ret ) {
                LOG.error( "更新站点 " + siteId + "的信息失败，停止同步该站点信息" );
            } else {
                ret = secResourceSyncManager.syncRole( role );
                if ( 1 > ret ) {
                    LOG.error( "更新站点 " + siteId + "的信息失败，停止同步该站点信息" );
                } else {
                    List<DeptBean> deptList = esbInterfaceService.getChildDeptByComp( compBean.getSourceCode()
                            .replaceAll( "a", "" ) );
                    Map<String, Organization> existsOrgMap = getExistsChildOrg( compBean.getSourceCode() );
                    for ( DeptBean deptBean : deptList ) {
                        String deptCode = deptBean.getDepartmentcode();
                        Organization existsOrg = existsOrgMap.get( deptCode );
                        try {
                            if ( deptBean.getEnable() ) {// 更新部门信息
                                Organization modifyOrg = syncDept( deptBean, role, operUserId );
                                if ( null == existsOrg ) {
                                    notifySite.modifyDept( modifyOrg, null, SyncLogType.Add, null );
                                } else {
                                    notifySite.modifyDept( modifyOrg, existsOrg, SyncLogType.Modify, null );
                                    existsOrgMap.remove( deptCode );
                                }
                            } else {
                                LOG.info( "更新部门 " + deptCode + "已被停用." );
                            }
                        } catch (Exception e) {
                            LOG.error( "更新站点 " + siteId + "的信息异常，停止同步该站点信息", e );
                            existsOrgMap.remove( deptCode );
                            notifySite.modifyDept( null, existsOrg, SyncLogType.Exception, getExceptionInfo( e ) );
                        }
                    }
                    if ( !existsOrgMap.isEmpty() ) {
                        Set<Entry<String, Organization>> setList = existsOrgMap.entrySet();
                        for ( Entry<String, Organization> entry : setList ) {
                            Organization noExistsOrg = entry.getValue();
                            if ( StatusCode.Y.equals( noExistsOrg.getSyncInd() ) ) {
                                secResourceSyncManager.deleteOrg( entry.getKey() );
                                notifySite.modifyDept( null, noExistsOrg, SyncLogType.Delete, null );
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOG.error( "获取站点 " + siteId + " 的信息异常，停止同步该站点信息", e );
        }
        notifySite.notifyLog( secResourceSyncManager.getSiteAdmin( siteId ) );
        LOG.info( StringHelper.concat( "站点", siteId, "信息的部门、人事信息同步完毕,同步日志 SEC_SYNC_LOG。结束时间",
                ParamConfig.S_SSTIME_FORMATTER.format( new Date() ) ) );
    }

    public void deleteAdtSecUserJustKeepOneMonth() {
        secResourceSyncManager.deleteAdtSecUserJustKeepOneMonth();
        LOG.info( "批量删除adt_sec_user表中一个月之前的数据" );
    }

    public void updateUserConfigSite() {
        secResourceSyncManager.updateUserConfigSite();
        LOG.info( "同步数据后将更新一下只有一个站点的用户默认站点信息" );
    }

    private Organization syncDept(DeptBean dept, Role role, String operUserId) throws Exception {
        Organization orgOne = new Organization();// 新建当前部门实例
        try {
            String deptCode = dept.getDepartmentcode();// 获取部门编码
            orgOne.setCode( deptCode );
            orgOne.setName( dept.getDepartmentname() );
            orgOne.setShortName( "" );
            orgOne.setSortNum( subSortNum( Integer.toString( dept.getSortingnum() ) ) );
            orgOne.setParentCode( dept.getFatherdeptcode() );
            orgOne.setType( OrganizationType.Dept.toString() );
            orgOne.setSyncInd( StatusCode.Y );
            orgOne.setUpdatedBy( operUserId );
            orgOne.setUpdateTime( new Date() );
            orgOne.setCreatedBy( operUserId );
            orgOne.setCreateTime( new Date() );
            secResourceSyncManager.syncOrg( orgOne );

            List<EmpBean> empList = esbInterfaceService.getEmpByDepCode( dept.getDepartmentcode() );
            Map<String, SecureOrgUser> orgExitsUsrMap = getExistsDeptUser( dept.getDepartmentcode() );
            for ( EmpBean empBean : empList ) {// 更新人员信息
                SecureOrgUser existOrgUser = orgExitsUsrMap.get( empBean.getEmployeenumber() );
                try {
                    SecureUser secUser = new SecureUser();
                    secUser.setId( empBean.getEmployeenumber() );
                    secUser.setName( empBean.getEmployeename() );
                    secUser.setJob( empBean.getEmployeejob() );
                    String sTitle = empBean.getEmployeetitle();
                    secUser.setTitle( null != sTitle ? sTitle.replaceAll( " ", "" ) : "" );
                    secUser.setEmail( empBean.getEmail() );
                    secUser.setMobile( empBean.getMobilephone() );
                    secUser.setOfficeTel( empBean.getOfficeTelephone() );
                    secUser.setMicroTel( empBean.getMicroTelephone() );
                    secUser.setSortNum( empBean.getSortnumoa() );
                    secUser.setSortSubNum( empBean.getSortnum() );
                    secUser.setActive( StatusCode.Y );
                    secUser.setSyncInd( StatusCode.Y );
                    secUser.setUpdateTime( new Date() );
                    secUser.setUpdatedBy( operUserId );
                    secResourceSyncManager.syncUser( secUser, role, orgOne );
                    if ( null != existOrgUser ) {// 如果用户存在，则需要记录用户更新信息
                        notifySite.modifyUser( orgOne, secUser, existOrgUser, SyncLogType.Modify, null );
                        orgExitsUsrMap.remove( secUser.getId() );
                    } else {// 否则记录用户新增信息
                        notifySite.modifyUser( orgOne, secUser, null, SyncLogType.Add, null );
                    }
                } catch (Exception e) {
                    LOG.info( "更新人员" + empBean.getEmployeenumber() + "信息失败.", e );

                    notifySite.modifyUser( orgOne, null, existOrgUser, SyncLogType.Add, getExceptionInfo( e ) );
                    continue;
                }
            }

            Set<Entry<String, SecureOrgUser>> orgUserSet = orgExitsUsrMap.entrySet();
            for ( Entry<String, SecureOrgUser> entry : orgUserSet ) {
                SecureOrgUser existsOrgUser = entry.getValue();
                if ( (StatusCode.Y.equals( existsOrgUser.getSyncInd() ) || StatusCode.YES.equals( existsOrgUser
                        .getSyncInd() ))
                        && (StatusCode.Y.equals( existsOrgUser.getOrgSyncInd() ) || StatusCode.YES
                                .equals( existsOrgUser.getOrgSyncInd() )) ) {
                    secResourceSyncManager.deleteOrgUser( existsOrgUser.getOrgCode(), existsOrgUser.getId() );
                    notifySite.modifyUser( orgOne, null, existsOrgUser, SyncLogType.Delete, null );
                }
            }
        } catch (Exception e) {
            LOG.error( "更新部门 " + dept.getDepartmentcode() + "的信息异常，停止同步该部门及其子部门的人事信息和部门信息", e );
        }
        List<DeptBean> deptList = esbInterfaceService.getChildDeptByDept( dept.getDepartmentcode() );
        Map<String, Organization> existsOrgMap = getExistsChildOrg( dept.getDepartmentcode() );
        for ( DeptBean deptChild : deptList ) {
            Organization existsOrg = existsOrgMap.get( deptChild.getDepartmentcode() );
            try {
                if ( dept.getEnable() ) {// 更新部门信息
                    Organization modifyOrg = syncDept( deptChild, role, operUserId );
                    if ( null == existsOrg ) {
                        notifySite.modifyDept( modifyOrg, null, SyncLogType.Add, null );
                    } else {
                        existsOrgMap.remove( deptChild.getDepartmentcode() );
                        notifySite.modifyDept( modifyOrg, existsOrg, SyncLogType.Modify, null );
                    }
                } else {
                    LOG.info( "更新部门 " + dept.getDepartmentcode() + "已被停用." );
                }
            } catch (Exception e) {
                LOG.error( "更新部门 " + deptChild.getDepartmentcode() + "的信息异常，停止同步该部门及其子部门的人事信息和部门信息", e );
                notifySite.modifyDept( null, existsOrg, SyncLogType.Exception, getExceptionInfo( e ) );
                continue;
            }
        }
        if ( !existsOrgMap.isEmpty() ) {
            Set<Entry<String, Organization>> setList = existsOrgMap.entrySet();
            for ( Entry<String, Organization> entry : setList ) {
                Organization noExistsOrg = entry.getValue();
                if ( StatusCode.Y.equals( noExistsOrg.getSyncInd() ) ) {
                    secResourceSyncManager.deleteOrg( entry.getKey() );
                    notifySite.modifyDept( null, noExistsOrg, SyncLogType.Delete, null );
                }
            }
        }
        return orgOne;
    }

    /**
     * 获取当前子部门列表
     * 
     * @param parentOrgCode
     * @return
     */
    private Map<String, Organization> getExistsChildOrg(String parentOrgCode) {
        List<Organization> orgLisl = secResourceSyncManager.getDeptByParents( parentOrgCode );
        Map<String, Organization> orgMap = new HashMap<String, Organization>();
        if ( null != orgLisl && !orgLisl.isEmpty() ) {
            for ( Organization org : orgLisl ) {
                orgMap.put( org.getCode(), org );
            }
        }
        return orgMap;
    }

    /**
     * 获取当前部门已经存在的人员列表
     * 
     * @param parentOrgCode
     * @return
     */
    private Map<String, SecureOrgUser> getExistsDeptUser(String orgCode) {
        List<SecureOrgUser> secureOrgUserList = secResourceSyncManager.getDeptUser( orgCode );
        Map<String, SecureOrgUser> orgMap = new HashMap<String, SecureOrgUser>();
        if ( null != secureOrgUserList && !secureOrgUserList.isEmpty() ) {
            for ( SecureOrgUser orgUser : secureOrgUserList ) {
                orgMap.put( orgUser.getId(), orgUser );
            }
        }
        return orgMap;
    }

    /**
     * 根据排序字段的长度需求，修改排序字段长度
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-9-30
     * @param sortNum
     * @return:
     */
    private int subSortNum(String sortNum) {
        final int len = sortNum.length();
        if ( 6 < len ) {
            sortNum = sortNum.substring( sortNum.length() - 6 );
        }
        return Integer.valueOf( sortNum );
    }

    /**
     * 获取异常信息
     * 
     * @return
     */
    private String getExceptionInfo(Exception e) {
        StringBuffer sbuff = new StringBuffer( e.getMessage() );
        StackTraceElement[] stacks = e.getStackTrace();
        for ( StackTraceElement stack : stacks ) {
            sbuff.append( "|" );
            sbuff.append( stack );
        }
        return sbuff.toString();
    }
}
