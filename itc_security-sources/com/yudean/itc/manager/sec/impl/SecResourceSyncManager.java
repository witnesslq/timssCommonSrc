package com.yudean.itc.manager.sec.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yudean.itc.code.StatusCode;
import com.yudean.itc.dao.sec.OrganizationMapper;
import com.yudean.itc.dao.sec.RoleMapper;
import com.yudean.itc.dao.sec.SecureUserMapper;
import com.yudean.itc.dao.sec.SiteMapper;
import com.yudean.itc.dto.sec.Organization;
import com.yudean.itc.dto.sec.Role;
import com.yudean.itc.dto.sec.SecureOrgUser;
import com.yudean.itc.dto.sec.SecureUser;
import com.yudean.itc.dto.sec.Site;
import com.yudean.itc.manager.sec.ISecResourceSyncManager;
import com.yudean.itc.util.ClassCastUtil;
import com.yudean.itc.util.MD5;

@Service
public class SecResourceSyncManager implements ISecResourceSyncManager {
    private static final Logger LOG = Logger.getLogger( SecResourceSyncManager.class );

    @Autowired
    SiteMapper siteMapper;

    @Autowired
    OrganizationMapper organizationMapper;

    @Autowired
    RoleMapper roleMapper;

    @Autowired
    SecureUserMapper secureUserMapper;

    @Override
    @Transactional(rollbackFor = { Exception.class })
    public int syncSite(Site site, Organization org) throws Exception {
        int ret = 0;
        // sec_organization表根节点操作
        Organization siteOrg = organizationMapper.selectOrgByID( org.getCode() );
        if ( null == siteOrg ) {
            ret = organizationMapper.insertOrg( org );
        } else {
            // 加多一层判断，若组织中的syncId为Y的时候可以更新组织基本信息
            if ( StatusCode.Y.equals( siteOrg.getSyncInd() ) ) {
                ret = organizationMapper.updateOrg( org );
            } else {
                ret = 1;
            }
        }
        checkRet( ret );

        // sec_site表根节点操作
        List<Site> siteList = siteMapper.selectSite( site, SiteMapper.Oper.Precise );
        if ( !siteList.isEmpty() ) {
            if ( StatusCode.Y.equals( siteOrg.getSyncInd() ) ) {
                ret = siteMapper.updateSite( site );
            } else {
                ret = 1;
            }
        } else {
            ret = siteMapper.insertSite( site );
        }
        checkRet( ret );

        // sec_site_orgnaization表只有在新增的时候需要做插入
        List<Map<String, Object>> siteOrgMap = siteMapper.selectSiteOrg( org.getCode(), site.getId() );
        if ( siteOrgMap.isEmpty() ) {
        	//20161031 zhx 原来的参数顺序有误 ret = siteMapper.insertSiteOrg( org.getCode(), site.getId() );
            ret = siteMapper.insertSiteOrg( site.getId(),org.getCode() );
        }
        checkRet( ret );

        return ret;
    }

    private boolean checkRet(int ret) throws Exception {
        if ( 0 >= ret ) {
            LOG.error( "更新站点信息失败，退出！" );
            throw new Exception( "更新信息失败！" );
        } else {
            return true;
        }
    }

    @Override
    @Transactional(rollbackFor = { Exception.class })
    public int syncOrg(Organization org) throws Exception {
        int ret = 0;
        Organization siteOrg = organizationMapper.selectOrgByID( org.getCode() );
        if ( null == siteOrg ) {
            ret = organizationMapper.insertOrg( org );
        } else {
            // 若organization中同步设置为N，则不更新
            if ( StatusCode.Y.equals( siteOrg.getSyncInd() ) ) {
                ClassCastUtil.castNoNullFieldSingle( org, siteOrg );
                siteOrg.setUpdatedBy( org.getUpdatedBy() );
                siteOrg.setUpdateTime( org.getUpdateTime() );
                ret = organizationMapper.updateOrg( siteOrg );
            } else {
                ret = 1;
            }
        }
        return ret;
    }

    @Override
    @Transactional(rollbackFor = { Exception.class })
    public int syncRole(Role role) throws Exception {
        Role siteRole = roleMapper.selectRole( role.getId(), null );
        if ( null == siteRole ) {
            roleMapper.insertRole( role );
        } else {
            ClassCastUtil.castNoNullFieldSingle( role, siteRole );
            siteRole.setUpdatedBy( role.getUpdatedBy() );
            siteRole.setUpdateTime( role.getUpdateTime() );
            roleMapper.updateRole( siteRole );
        }
        return 1;
    }

    @Override
    @Transactional(rollbackFor = { Exception.class })
    public int syncUser(SecureUser secUser, Role role, Organization org) throws Exception {
        // 更新用户信息
        // 因为会出现lync那边比我们这边多角色的时候，所以将insertRoleUserMapping分开两个来写，一个写在新增的时候，一个写在同步标识为Y的时候
        SecureUser siteSecUser = secureUserMapper.selectUser( secUser.getId() );
        if ( null == siteSecUser ) {
            final String s_DefaultPassword = "123456";
            secUser.setPassword( MD5.GetMD5Code( s_DefaultPassword ) );
            secureUserMapper.insertUser( secUser );

            // 插入角色与用户的关联关系
            roleMapper.insertRoleUserMapping( role.getId(), secUser.getId() );

            // 插入部门与用户关联关系
            organizationMapper.insertOrgUserMapEx( org.getCode(), secUser.getId(), StatusCode.YES.toString() );

        } else {

            // 更新用户部门信息
            if ( siteSecUser.getSyncInd().equals( StatusCode.YES ) ) {
                ClassCastUtil.castNoNullFieldSingle( secUser, siteSecUser );
                siteSecUser.setUpdatedBy( secUser.getUpdatedBy() );
                siteSecUser.setUpdateTime( secUser.getUpdateTime() );
                secureUserMapper.updateUser( siteSecUser );

                // 更新用户角色信息
                Integer iExists = roleMapper.selectRoleUserMapping( role.getId(), secUser.getId() );
                if ( 0 == iExists ) {
                    roleMapper.insertRoleUserMapping( role.getId(), secUser.getId() );
                }

                /*
                 * if ( 0 < exists ) { // 若存在组织跟用户绑定的数据，则将用户是否同步的标识赋值给绑定记录
                 * organizationMapper.updateOrgUserMapEx( org.getCode(),
                 * secUser.getId(), "Y" ); } else {
                 * organizationMapper.insertOrgUserMapEx( org.getCode(),
                 * secUser.getId(), StatusCode.YES.toString() ); }
                 */

                Integer exists = organizationMapper.selectOrgUserMap( org.getCode(), secUser.getId() );
                if ( 0 == exists ) {
                    // 若存在组织跟用户绑定的数据，则将用户是否同步的标识赋值给绑定记录
                    organizationMapper.insertOrgUserMapEx( org.getCode(), secUser.getId(), StatusCode.YES.toString() );
                }
            } /*
               * else { // 若存在组织跟用户绑定的数据，则将用户是否同步的标识赋值给绑定记录
               * organizationMapper.updateOrgUserMapEx( null, secUser.getId(),
               * "N" ); }
               */
        }

        return 1;
    }

    @Override
    public List<Organization> getDeptByParents(String orgCode) throws NullPointerException {
        List<String> orgCodeList = new ArrayList<String>();
        orgCodeList.add( orgCode );
        return organizationMapper.selectOrgsByParentIds( orgCodeList );
    }

    @Override
    public List<SecureOrgUser> getDeptUser(String orgCode) throws NullPointerException {
        return secureUserMapper.selectOrgUser( orgCode, null );
    }

    @Override
    public int deleteOrgUser(String orgCode, String userId) throws NullPointerException {
        organizationMapper.deleteOrgUserMap( orgCode, userId );
        return 1;
    }

    @Override
    public int deleteOrg(String orgCode) throws NullPointerException {
        return organizationMapper.deleteOrgEx( orgCode );
    }

    @Override
    public List<SecureUser> getSiteAdmin(String SiteId) throws NullPointerException {
        final String s_DefaultSiteAdminRole = "[REPSITE]_Site_Admin";
        String roleAdmin = s_DefaultSiteAdminRole.replace( "[REPSITE]", SiteId );
        return roleMapper.selectUserWithSpecificRole( roleAdmin, null, true );
    }

    @Override
    public void deleteAdtSecUserJustKeepOneMonth() throws NullPointerException {
        secureUserMapper.deleteAdtSecUserJustKeepOneMonth();
    }

    @Override
    public void updateUserConfigSite() throws NullPointerException {
        secureUserMapper.updateUserConfigSite();
    }
}
