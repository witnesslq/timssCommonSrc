package com.yudean.itc.manager.sec.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yudean.itc.OrgTreeUtil;
import com.yudean.itc.annotation.Operator;
import com.yudean.itc.annotation.Secured;
import com.yudean.itc.code.MenuType;
import com.yudean.itc.code.StatusCode;
import com.yudean.itc.dao.sec.OrganizationMapper;
import com.yudean.itc.dao.sec.RoleMapper;
import com.yudean.itc.dao.sec.SecureFunctionMapper;
import com.yudean.itc.dao.sec.SecureMenuMapper;
import com.yudean.itc.dao.sec.SecureUserGroupMapper;
import com.yudean.itc.dao.sec.SecureUserMapper;
import com.yudean.itc.dto.Page;
import com.yudean.itc.dto.sec.Organization;
import com.yudean.itc.dto.sec.Role;
import com.yudean.itc.dto.sec.SecureFunction;
import com.yudean.itc.dto.sec.SecureMenu;
import com.yudean.itc.dto.sec.SecureUser;
import com.yudean.itc.dto.sec.SecureUserGroup;
import com.yudean.itc.manager.sec.IAuthenticationManager;
import com.yudean.itc.manager.sec.IAuthorizationManager;
import com.yudean.itc.manager.sec.ISecurityMaintenanceManager;
import com.yudean.itc.util.MD5;

@Service
public class AuthorizationManager implements IAuthorizationManager {

    private static final Logger log = Logger.getLogger( AuthorizationManager.class );

    @Autowired
    private SecureMenuMapper secureMenuMapper;
    @Autowired
    private OrganizationMapper organizationMapper;
    @Autowired
    private SecureUserMapper secureUserMapper;
    @Autowired
    private ISecurityMaintenanceManager secManager;
    @Autowired
    private IAuthenticationManager authManager;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private SecureFunctionMapper secFuncMapper;
    @Autowired
    private SecureUserGroupMapper groupMapper;

    public List<SecureMenu> getAuthorizedMenu(String userId, String siteId, MenuType menuType, String parentMenuId) {
        if ( "".equals( parentMenuId ) )
            parentMenuId = null;
        List<SecureMenu> menus = secureMenuMapper.selectAuthorizedMenus( userId,
                menuType == null ? null : menuType.toString(), parentMenuId, siteId );
        return menus;
    }

    @Secured
    public List<Organization> retrieveAuthorizedOrg(String parentOrgId, @Operator SecureUser operator,
            boolean ignoreSiteWhenSA) {
        List<Organization> l = null;
        if ( operator.isSuperAdmin() && ignoreSiteWhenSA )
            l = organizationMapper.selectOrgByParentID( parentOrgId, null );
        else
            l = organizationMapper.selectOrgByParentID( parentOrgId, new String[] { operator.getCurrentSite() } );
        return l;
    }

    @SuppressWarnings("deprecation")
    @Secured
    public List<SecureUser> retriveActiveUsersOfGivenOrg(String orgId, boolean includeSubOrgs,
            @Operator SecureUser operator) {
        List<SecureUser> l = null;
        //retriveActiveUsersOfGivenOrg 是暴露给业务的接口 不能随便加减参数 这里通过operator加标志位，然后在orgId上加特殊记号的方式传值
        if(operator.getSpecFlag() !=null && operator.getSpecFlag().equals("showAllUsers")){
            orgId = "*_" + orgId;
        }
        l = retriveActiveUsersOfGivenOrg( orgId, includeSubOrgs );
        return l;
    }

    public List<SecureUser> retriveActiveUsersOfGivenOrg(String orgId, boolean includeSubOrgs) {
        List<SecureUser> l = null;
        boolean showAllUsers = false;
        if(orgId.indexOf("*_") == 0){
            showAllUsers = true;
            orgId = orgId.substring(2);
        }
        if ( includeSubOrgs )
            l = secureUserMapper.selectAllUsersInGivenOrg( orgId, showAllUsers );
        else
            l = secureUserMapper.selectUsersInGivenOrg( orgId, showAllUsers );
        return l;
    }

    private SecureUser createOperator(String siteId) {
        SecureUser guest = new SecureUser();
        guest.setId( "SYS-GUEST" );
        guest.setCurrentSite( "GLOBAL" );
        // 在没有指定siteId时 让虚构operator成为管理员 以便获取全站点数据
        if ( siteId == null ) {
            guest.setSuperAdminInd( StatusCode.YES );
        } else {
            guest.setCurrentSite( siteId );
        }
        return guest;
    }

    @Override
    public SecureUser retriveUserById(String userId, String siteId) {
        if ( userId == null ) {
            return null;
        }
        SecureUser guest = createOperator( siteId );
        SecureUser secUser = secManager.retrieveUserWithDetails( userId, guest );
        if ( siteId == null ) {
            secUser.setSuperAdminInd( StatusCode.YES );
        } else {
            secUser.setCurrentSite( siteId );
        }

        authManager.assemblePrivileges( secUser, secUser.getCurrentSite() );
        if ( siteId != null && secUser != null ) {
            // 按站点过滤用户的用户组、角色、机构（权限好像过滤了没用？也不好过滤）
            List<Role> fRoles = new ArrayList<Role>();
            List<Role> origRoles = secUser.getRoles();
            if ( origRoles != null ) {
                for ( Role role : origRoles ) {
                    if ( role.getSiteId().equals( siteId ) ) {
                        fRoles.add( role );
                    }
                }

            }
            secUser.setRoles( fRoles );

            List<SecureUserGroup> fGroups = new ArrayList<SecureUserGroup>();
            List<SecureUserGroup> origGroups = secUser.getGroups();
            if ( origGroups != null ) {
                for ( SecureUserGroup group : origGroups ) {
                    if ( group.getSiteId().equals( siteId ) ) {
                        fGroups.add( group );
                    }
                }
            }

            secUser.setGroups( fGroups );
            if ( OrgTreeUtil.rootNode == null ) {
                log.warn( "没有执行Mapping.buildOrgTree函数，无法按站点过滤组织" );
            } else {
                List<Organization> fOrgs = new ArrayList<Organization>();
                List<Organization> origOrgs = secUser.getOrganizations();
                if ( origOrgs != null ) {
                    for ( Organization org : origOrgs ) {
                        if ( siteId.equals( OrgTreeUtil.getOrgSite( org.getCode() ) ) ) {
                            fOrgs.add( org );
                        }
                    }
                }
                secUser.setOrganizations( fOrgs );
            }
        }
        return secUser;
    }

    @Override
    public List<SecureUser> retriveUsersWithSpecificRole(String roleId, String orgCode, boolean withSubOrg,
            boolean onlyActive) {
        List<String> orgs = null;
        if ( orgCode != null ) {
            orgs = new ArrayList<String>();
            if ( withSubOrg ) {
                ArrayList<Organization> orgas = retrieveSubOrgs( orgCode, withSubOrg );
                for ( Organization org : orgas ) {
                    orgs.add( org.getCode() );
                }

            }
            orgs.add( orgCode );
        }
        List<SecureUser> result = roleMapper.selectUserWithSpecificRole( roleId, orgs, onlyActive );
        return result;
    }

    @Override
    public List<SecureUser> retriveUsersWithSpecificRoleAndSite(String roleId, String site) {
        List<SecureUser> retUsersList = secureUserMapper.selectUsersOfGivenRole( roleId, site );
        for ( SecureUser secuser : retUsersList ) {
            secuser.setCurrentSite( site );
        }
        return retUsersList;
    }

    @Override
    public List<Organization> retriveOrgsByRelation(String orgCode, boolean isParent, boolean visitToEnd) {
        ArrayList<Organization> orgs = new ArrayList<Organization>();
        Organization org = secManager.selectOrgById( orgCode );
        if ( org == null ) {// 组织代码不合法
            return null;
        }
        String orgPCode = org.getParentCode();
        if ( isParent ) {
            // 从当前节点取到父节点
            while (true) {
                org = secManager.selectOrgById( orgPCode );
                if ( org != null ) {
                    orgs.add( org );
                    if ( !visitToEnd || org.getParentCode() == null || org.getCode().equals( "1" ) ) {
                        break;
                    }
                    orgPCode = org.getParentCode();
                } else {
                    break;
                }
            }
        } else {
            // 取当前节点的所有子节点
            orgs = retrieveSubOrgs( orgCode, visitToEnd );
        }
        return orgs;

    }

    private ArrayList<Organization> retrieveSubOrgs(String orgCode, boolean visitToEnd) {
        ArrayList<Organization> orgs = new ArrayList<Organization>();
        ArrayList<String> ids = new ArrayList<String>();
        ids.add( orgCode );
        while (ids.size() > 0) {
            List<Organization> children = organizationMapper.selectOrgsByParentIds( ids );
            ids.clear();
            for ( Organization org : children ) {
                String childCode = org.getCode();
                if ( visitToEnd ) {
                    ids.add( childCode );
                }
                orgs.add( org );
            }
        }
        return orgs;
    }

    @Override
    public void updateMenuStatus(String menuId, boolean isActive) {
        ArrayList<String> secMenus = new ArrayList<String>();
        ArrayList<String> secFuncs = new ArrayList<String>();
        getMenuAndFunctionsInSpecficMenu( menuId, true, secMenus, secFuncs );
        log.debug( "Find : menu=" + secMenus.size() + ",funcs=" + secFuncs.size() );
        if ( !isEmpty( secMenus ) ) {
            log.debug( "updating menu status -> " + isActive );
            secureMenuMapper.updateMenuStatus( secMenus, isActive );
        } else {
            log.debug( "no menu find for parent id->" + menuId );
        }
        if ( !isEmpty( secFuncs ) ) {
            log.debug( "updating function status -> " + isActive );
            secFuncMapper.updateFunctionStatus( secFuncs, isActive );
        } else {
            log.debug( "no function find for parent id->" + menuId );
        }
    }

    /**
     * 获取某菜单下的所有菜单和功能
     * 
     * @param menuId 父节点菜单Id
     * @param visitToEnd 是否遍历所有子节点，设置为false则只遍历一层
     * @param secMenus 子节点菜单ID
     * @param secFuncs 子节点功能ID
     */
    private void getMenuAndFunctionsInSpecficMenu(String menuId, boolean visitToEnd, ArrayList<String> secMenus,
            ArrayList<String> secFuncs) {
        ArrayList<String> pMenus = new ArrayList<String>();
        pMenus.add( menuId );
        ArrayList<String> pFuncs = new ArrayList<String>();
        while (pMenus.size() > 0 || pFuncs.size() > 0) {
            ArrayList<String> pnMenus = new ArrayList<String>();
            ArrayList<String> pnFuncs = new ArrayList<String>();
            // 菜单的子节点可能是菜单，也可能是功能
            for ( String id : pMenus ) {
                List<SecureFunction> funcs = secManager.retrieveFunctions( id );
                List<SecureMenu> menus = secureMenuMapper.selectSubMenus( id, null, null, false );
                if ( !isEmpty( funcs ) ) {
                    for ( SecureFunction func : funcs ) {
                        pnFuncs.add( func.getId() );
                        secFuncs.add( func.getId() );
                    }
                }
                if ( !isEmpty( menus ) ) {
                    for ( SecureMenu mnu : menus ) {
                        pnMenus.add( mnu.getId() );
                        secMenus.add( mnu.getId() );
                    }
                }
            }

            for ( String id : pFuncs ) {
                List<SecureFunction> funcs = secManager.retrieveFunctions( id );
                if ( !isEmpty( funcs ) ) {
                    for ( SecureFunction func : funcs ) {
                        pnFuncs.add( func.getId() );
                        secFuncs.add( func.getId() );
                    }
                }
            }

            pMenus = pnMenus;
            pFuncs = pnFuncs;
        }
    }

    @SuppressWarnings("rawtypes")
    private boolean isEmpty(List list) {
        if ( list == null || list.size() == 0 ) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void updateMenuName(String menuId, String menuName) {
        secureMenuMapper.updateMenuName( menuId, menuName );
    }

    @Override
    public List<SecureUser> retriveUsersWithSpecificGroup(String groupId, String orgCode, boolean withSubOrg,
            boolean onlyActive) {
        // TODO Auto-generated method stub
        List<String> orgs = null;
        if ( orgCode != null ) {
            orgs = new ArrayList<String>();
            if ( withSubOrg ) {
                ArrayList<Organization> orgas = retrieveSubOrgs( orgCode, withSubOrg );
                for ( Organization org : orgas ) {
                    orgs.add( org.getCode() );
                }

            }
            orgs.add( orgCode );
        }
        return groupMapper.selectUserWithSpecificGroup( groupId, orgs, onlyActive );
    }

    @Override
    public boolean verifyPassword(String userId, String password) {
        if ( userId == null || password == null ) {
            return false;
        }
        // TODO：这里以后要去掉MD5验证么？
        String pswd = secManager.getUserPassword( userId );
        if ( pswd == null ) {
            log.error( userId + "对应的用户不存在或者没有密码，无密码的用户无法验证通过" );
            return false;
        }
        if ( MD5.GetMD5Code( password ).equals( pswd ) || pswd.equals( password ) ) {
            return true;
        }
        return false;
    }

    @Override
    public String getPassword(String userId) {
        return secManager.getUserPassword( userId );
    }

    @Override
    public String retrieveUserConfig(SecureUser user, String attribute) {
        return secureUserMapper.selectUserConfig( user.getId(), attribute );
    }

    @Override
    public String retrieveUserConfig(String userId, String attribute) {
        return secureUserMapper.selectUserConfig( userId, attribute );
    }

    @Override
    @Transactional
    public void updateUserConfig(SecureUser user, String attribute, String value) {
        log.info( "[" + user.getId() + "]setting->" + attribute + " = " + value );
        secureUserMapper.deleteUserConfig( user.getId(), attribute );
        secureUserMapper.insertUserConfig( user.getId(), attribute, value );
        log.info( "[" + user.getId() + "]setting->" + attribute + " success" );
    }
    
    @Override
    public Page<SecureUser> retrieveUsersInAllSites(Page<SecureUser> page) {
        Map<String, Object> params = page.getParams();
        // 因为性能原因只返回前30条记录
        int pageSize = page.getPageSize() > 30 ? 30 : page.getPageSize();
        params.put( "pageSize", pageSize );
        List<SecureUser> userList = secureUserMapper.selectUsersInAllSites( params );
        page.setResults( userList );
        return page;
    }
    
    @Override
    public Page<SecureUser> retrieveUsersByRoleId(Page<SecureUser> page) {
        Map<String, Object> params = page.getParams();
        // 因为性能原因只返回前30条记录
        int pageSize = page.getPageSize() > 30 ? 30 : page.getPageSize();
        params.put( "pageSize", pageSize );
        List<SecureUser> userList = secureUserMapper.selectUsersByRole( params );
        page.setResults( userList );
        return page;
    }
}
