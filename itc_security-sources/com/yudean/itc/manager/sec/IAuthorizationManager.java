package com.yudean.itc.manager.sec;

import java.util.List;

import com.yudean.itc.annotation.Operator;
import com.yudean.itc.code.MenuType;
import com.yudean.itc.dto.Page;
import com.yudean.itc.dto.sec.Organization;
import com.yudean.itc.dto.sec.SecureMenu;
import com.yudean.itc.dto.sec.SecureUser;

/**
 * 提供安全资源访问控制的服务类
 * 
 * @author yushujiang
 */
public interface IAuthorizationManager {

    /**
     * 根据用户权限获取指定节点下的子菜单列表，如果是超级管理员，则直接获取所有菜单
     * 
     * @param userId 用户ID
     * @param siteId 当前登录的Site
     * @param menuType 菜单类型, 如果为NULL，返回所有类型
     * @param parentMenuId 父节点，如果为NULL，系统返回根菜单
     * @return 提供父节点的下一级菜单，但不包含其子菜单
     */
    List<SecureMenu> getAuthorizedMenu(String userId, String siteId, MenuType menuType, String parentMenuId);

    /**
     * 查询指定节点下一层的组织机构，非SA只能查看到当前站点下的数据
     * 
     * @param parentOrgId 设置为NULL时，取一级
     * @param operator
     * @param ignoreSiteWhenSA 当用户为超级管理员时无视站点设置，仅用于特殊场景（如跨站赋角色）
     * @return
     */
    List<Organization> retrieveAuthorizedOrg(String parentOrgId, @Operator SecureUser operator, boolean ignoreSiteWhenSA);

    /**
     * 查询指定组织机构下的用户
     * 
     * @param orgId
     * @param includeSubOrgs 是否查询下属子机构
     * @param operator
     * @return 不包含禁用、已删除以及超级管理员
     */
    List<SecureUser> retriveActiveUsersOfGivenOrg(String orgId, boolean includeSubOrgs, @Operator SecureUser operator);

    /**
     * 查询指定组织机构下的用户，不传入调用者信息，用于工作流选人逻辑
     * 
     * @param orgId
     * @param includeSubOrgs 是否查询下属子机构
     * @return 不包含禁用、已删除以及超级管理员
     */
    List<SecureUser> retriveActiveUsersOfGivenOrg(String orgId, boolean includeSubOrgs);

    /**
     * 获取指定用户的详细资料
     * 
     * @param userId 用户ID
     * @param siteId 可选，如果设置了站点ID，则按站点过滤用户的权限、角色、用户组
     *            需要注意的是，由于某种需求，当不指定站点ID时，也不会返回用户的组织机构
     * @return
     */
    SecureUser retriveUserById(String userId, String siteId);

    /**
     * 获取拥有指定角色的用户
     * 
     * @deprecated 使用SecurityFacade.retriveActiveUsersWithSpecificRole()
     * @param roleId 角色ID（必须）
     * @param orgCode 部门ID，可选，为null时则返回所有拥有该角色的所有ID，否则则返回属于特定机构的用户
     * @param withSubOrg 是否包含下属机构，
     * @param onlyActive 是否只返回未被禁用的用户
     * @return SecureUser的列表，需要注意的是，其中只有id和name可用，如需要详细资料需要手动查询
     */
    List<SecureUser> retriveUsersWithSpecificRole(String roleId, String orgCode, boolean withSubOrg, boolean onlyActive);

    /**
     * 获取拥有指定用户组的用户
     * 
     * @deprecated 使用SecurityFacade.retriveActiveUsersWithSpecificGroup()
     * @param groupId 用户组ID（必须）
     * @param orgCode 部门ID，可选，为null时则返回所有拥有该用户组的所有ID，否则则返回属于特定机构的用户
     * @param withSubOrg 是否包含下属机构，
     * @param onlyActive 是否只返回未被禁用的用户
     * @return SecureUser的列表，需要注意的是，其中只有id和name可用，如需要详细资料需要手动查询
     */
    List<SecureUser> retriveUsersWithSpecificGroup(String groupId, String orgCode, boolean withSubOrg,
            boolean onlyActive);

    /**
     * 根据角色和站点信息获取用户列表
     * 
     * @param roleId
     * @param site
     * @return
     */
    List<SecureUser> retriveUsersWithSpecificRoleAndSite(String roleId, String site);

    /**
     * 获取某个组织的（所有）父节点或者组节点
     * 
     * @param orgCode
     * @param isParent 是否取父节点，取false则取子节点
     * @param visitToEnd 是否需要遍历到末端，当父节点遍历则取到orgCode=0的父节点，子节点遍历则取所有子节点
     * @return 组织列表，按从给定部门到总公司（或者到下属最深一层节点）排序
     */
    List<Organization> retriveOrgsByRelation(String orgCode, boolean isParent, boolean visitToEnd);

    /**
     * 设置菜单的有效性
     * 
     * @param menuId 菜单或功能的ID，注意菜单和功能ID不能重复，就算数据库支持前台大框架也不支持
     * @param isActive 设置的状态，取true则启用，false则禁用
     */
    void updateMenuStatus(String menuId, boolean isActive);

    /**
     * 更新某一个菜单的名称，注意只针对sec_menu中的条目
     * 
     * @param menuId
     * @param menuName
     */
    void updateMenuName(String menuId, String menuName);

    /**
     * 验证用户名和密码是否合法 建议使用AuthorizationManager提供的sign方法
     * 
     * @param userId
     * @param password
     * @return
     */
    @Deprecated
    boolean verifyPassword(String userId, String password);

    /**
     * 获取用户密码
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-11-21
     * @param userId
     * @return:
     */
    String getPassword(String userId);

    /**
     * 获取某项用户设置
     * 
     * @param user
     * @param attribute
     * @return
     */
    String retrieveUserConfig(SecureUser user, String attribute);

    /**
     * 获取用户配置
     * 
     * @param user
     * @param attribute
     * @return
     */
    String retrieveUserConfig(String userId, String attribute);

    /**
     * 更新某项用户设置
     * 
     * @param user
     * @param atribute
     * @param value
     * @return
     */
    void updateUserConfig(SecureUser user, String attribute, String value);

    /**
     * 获取用户列表（无视站点，用于工单等全集团业务）
     * 
     * @deprecated
     * @param page
     * @return
     */
    Page<SecureUser> retrieveUsersInAllSites(Page<SecureUser> page);
    
    /**
     * 获取用户列表（根据角色过滤，然后模糊查找，用于ITSM第二期的主管指派工程师）
     *  
     * @param page
     * @return
     */
    Page<SecureUser> retrieveUsersByRoleId(Page<SecureUser> page);

}
