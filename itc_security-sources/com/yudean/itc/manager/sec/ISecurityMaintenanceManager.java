package com.yudean.itc.manager.sec;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yudean.itc.annotation.CUDTarget;
import com.yudean.itc.annotation.Operator;
import com.yudean.itc.code.StatusCode;
import com.yudean.itc.dto.Page;
import com.yudean.itc.dto.sec.Organization;
import com.yudean.itc.dto.sec.Privilege;
import com.yudean.itc.dto.sec.Role;
import com.yudean.itc.dto.sec.SecureFunction;
import com.yudean.itc.dto.sec.SecureMenu;
import com.yudean.itc.dto.sec.SecureUser;
import com.yudean.itc.dto.sec.SecureUserGroup;
import com.yudean.itc.dto.sec.FavRoute;
import com.yudean.itc.exception.SequenceExistsException;
import com.yudean.itc.exception.sec.AuthorizationException;

/**
 * 维护权限设置模块的服务类
 * @author yushujiang
 *
 */
public interface ISecurityMaintenanceManager {
	
	//用户操作相关

	/**
	 * 去重分页查询用户档案, 如果是SA，会查询系统内的所有用户，否者只查询当前站点或者选中组织下的用户
	 * @param queryPage
	 * @return
	 */
	Page<SecureUser> retrieveUniqueUsers(Page<SecureUser> queryPage, @Operator SecureUser operator);
	
	/**
	 * 根据用户ID查询用户档案，不包含附加信息
	 * @param userID
	 * @return
	 */
	SecureUser retrieveUserById(String userID);

	/**
	 * 根据用户ID查询完整的用户档案（包含隶属用户组、拥有的角色、隶属组织机构信息）
	 * <br/>
	 * 附加信息将启用数据权限
	 * 
	 * @param userID
	 * @param operator
	 * @return
	 */
	SecureUser retrieveUserWithDetails(String userID, @Operator SecureUser operator);
	/**
	 * 创建完整用户档案（包含隶属用户组、拥有的角色、隶属组织机构信息）
	 * @param userTobeSaved 用户信息，用户拥有的角色，隶属的用户组、组织机构应该同时包含在传入的对象中
	 * @param operator 必须提供调用者信息
	 * @return 保存成功的档案
	 * @throws SequenceExistsException 用户ID或其他关键信息重复
	 * @throws AuthorizationException 跨站数据访问
	 */
	SecureUser createUserWithDetails(@CUDTarget SecureUser userTobeSaved, @Operator SecureUser operator)  throws SequenceExistsException, AuthorizationException;
	/**
	 * 更新完整用户档案（包含隶属用户组、拥有的角色、隶属组织机构信息）
	 * @param userTobeSaved 用户信息，用户拥有的角色，隶属的用户组、组织机构应该同时包含在传入的对象中
	 * @param operator 必须提供调用者信息
	 * @return 保存成功的档案
	 * @throws SequenceExistsException 用户ID或其他关键信息重复
	 * @throws AuthorizationException 跨站数据访问
	 */
	SecureUser updateUserWithDetails(@CUDTarget SecureUser userTobeSaved, @Operator SecureUser operator)  throws SequenceExistsException, AuthorizationException;
	/**
	 * 更新用户档案(不含密码信息)
	 * @param userTobeSaved
	 * @param operator
	 * @return
	 * @throws SequenceExistsException
	 * @throws AuthorizationException
	 */
	SecureUser updateUser(@CUDTarget SecureUser userTobeSaved, @Operator SecureUser operator) throws SequenceExistsException, AuthorizationException;
	/**
	 * 逻辑删除用户档案
	 * @param userTobeDeleted 需要删除的用户id
	 * @param operator 必须提供调用者信息
	 */
	void deleteUser(String userTobeDeleted,  @Operator  SecureUser operator);
	
	/**
	 * 逻辑删除多个用户档案
	 * @param userTobeDeleted
	 * @param operator
	 */
	void deleteUsers(String[] userTobeDeleted, @Operator  SecureUser operator);
	
	/**
	 * 更新用户状态
	 * @param userId
	 * @param updateTo
	 */
	void updateUserStatus(String userId, StatusCode updateTo);
	
	/**
	 * 将用户指定到多个组织
	 * @param userId
	 * @param orgCode
	 * @param operator
	 */
	void addUserToOrganizations(String userId, String orgCode[], @Operator SecureUser operator);
	/**
	 * 将用户从多个组织移除
	 * @param userId
	 * @param orgCode
	 * @param operator
	 */
	void removeUserFromOrganizations(String userId, String orgCode[], @Operator SecureUser operator);

	//************************* 角色相关 **************************
	/**
	 * 查询当前站点下的角色，如果operator是超级管理员，则可以在queryPage.
	 * param中指定siteId以查询当前站点之外的其他站点下的角色；否则，只会查询operator当前站点下的角色。
	 * 
	 * @param queryPage
	 *            可提供siteId过滤（仅对超级管理员有效）
	 * @param operator
	 * @return
	 */
	Page<Role> retrieveRoles(Page<Role> queryPage, @Operator SecureUser operator);
	/**
	 * 查询角色及关联的用户组及用户
	 * @param roleId
	 * @param operator
	 * @return
	 * @throws AuthorizationException
	 */
	Role retrieveRoleWithDetails(String roleId, @Operator SecureUser operator) throws AuthorizationException;
	/**
	 * 根据角色ID查询，不包含附加信息。超级管理员调用此方法不受站点限制。
	 * @param roleId
	 * @param operator
	 * @return
	 */
	Role retrieveRoleById(String roleId, @Operator SecureUser operator);
	/**
	 * 根据角色名称在当前站点范围查询(超级管理员也一样)
	 * @param roleName
	 * @param operator
	 * @return
	 */
	Role retrieveRoleByName(String roleName, @Operator SecureUser operator);
	/**
	 * 根据菜单/功能ID反向查询拥有它的角色
	 * @param privilegeId
	 * @param operator
	 * @return
	 */
	List<Role> retrieveRolesByPrivilege(String privilegeId,  @Operator SecureUser operator);
	/**
	 * 创建角色
	 * @param role
	 * @param operator
	 * @return
	 * @throws AuthorizationException
	 */
	Role createRoleWithDetails(@CUDTarget Role role, @Operator SecureUser operator) throws AuthorizationException;
	/**
	 * 更新角色基本信息
	 * @param role
	 * @param operator
	 * @return
	 * @throws AuthorizationException
	 */
	Role updateRole(@CUDTarget Role role, @Operator SecureUser operator) throws AuthorizationException;
	/**
	 * 给用户授予角色
	 * @param roleId
	 * @param userId
	 * @param operator
	 * @throws AuthorizationException 非超级管理员不能操作跨站用户
	 */
	void assignRoleToUser(String roleId, String userId, @Operator SecureUser operator) throws AuthorizationException;
	
	/**
	 * 给多个用户授予角色
	 * @param roleId
	 * @param userIds
	 * @param operator
	 * @throws AuthorizationException 非超级管理员不能操作跨站用户
	 */
	void assignRoleToUsers(String roleId, String[] userIds, @Operator SecureUser operator) throws AuthorizationException;
	/**
	 * 将角色授予多个用户以及组织机构下的所有用户（包含子机构）
	 * @param roleId
	 * @param userIds
	 * @param orgCodes
	 * @param operator
	 * @throws AuthorizationException
	 */
	void assignRoleToUsers(String roleId, String[] userIds, String[] orgCodes, @Operator SecureUser operator) throws AuthorizationException;
	/**
	 * 将多个角色授予用户组
	 * @param roleIds
	 * @param groupId
	 * @param operator
	 * @throws AuthorizationException
	 */
	void assignRolesToGroup(String[] roleIds, String groupId, @Operator SecureUser operator) throws AuthorizationException;
	
	/**
	 * 移除用户已授予的角色
	 * @param roleId
	 * @param userId
	 * @param operator
	 * @throws AuthorizationException 非超级管理员不能操作跨站用户
	 */
	void removeRoleFromUser(String roleId, String userId, @Operator SecureUser operator) throws AuthorizationException;
	/**
	 * 移除多个用户已授予的角色
	 * @param roleId
	 * @param userIds
	 * @param operator
	 * @throws AuthorizationException 非超级管理员不能操作跨站用户
	 */
	void removeRoleFromUsers(String roleId, String[] userIds, @Operator SecureUser operator) throws AuthorizationException;
	/**
	 * 将多个角色移除用户组的授权
	 * @param roleIds
	 * @param groupId
	 * @param operator
	 * @throws AuthorizationException
	 */
	void removeRolesFromGroup(String[] roleIds, String groupId, @Operator SecureUser operator) throws AuthorizationException;
	/**
	 * 给用户组授权角色
	 * @param groupId
	 * @param operator
	 * @throws AuthorizationException 非超级管理员不能操作跨站用户组
	 */
	void assignRoleToGroup(String roleId, String groupId, @Operator SecureUser operator) throws AuthorizationException;
	
	/**
	 * 给多个用户组授权角色
	 * @param groupIds
	 * @param operator
	 * @throws AuthorizationException 非超级管理员不能操作跨站用户组
	 */
	void assignRoleToGroups(String roleId, String[] groupIds, @Operator SecureUser operator) throws AuthorizationException;

	/**
	 * 移除用户组已授权的角色
	 * @param groupId
	 * @param operator
	 * @throws AuthorizationException 非超级管理员不能操作跨站用户组
	 */
	void removeRoleFromGroup(String roleId, String groupId, @Operator SecureUser operator) throws AuthorizationException;
	
	/**
	 * 移除多个用户组已授权的角色
	 * @param groupIds
	 * @param operator
	 * @throws AuthorizationException 非超级管理员不能操作跨站用户组
	 */
	void removeRoleFromGroups(String roleId, String[] groupIds, @Operator SecureUser operator) throws AuthorizationException;
	
	/**
	 * 删除角色
	 * @param roleId
	 * @param operator
	 * @throws AuthorizationException
	 */
	void deleteRole(String roleId, @Operator SecureUser operator) throws AuthorizationException;
	
	/**
	 * 删除多个角色
	 * @param roleId
	 * @param operator
	 * @throws AuthorizationException
	 */
	void deleteRoles(String[] roleId, @Operator SecureUser operator) throws AuthorizationException;
	/**
	 * 为角色授权
	 * @param privileges
	 * @param roleId
	 * @param operator
	 * @throws AuthorizationException
	 */
	void assignPrivilegesToRole(Privilege[] privileges, String roleId, @Operator SecureUser operator) throws AuthorizationException;
	/**
	 * 移除角色授权
	 * @param privileges
	 * @param roleId
	 * @param operator
	 * @throws AuthorizationException
	 */
	void removePrivilegesFromRole(Privilege[] privileges, String roleId, @Operator SecureUser operator) throws AuthorizationException;
	
	
	//***************************** 用户组相关 ****************
	/**
	 * 查询当前站点下的用户组
	 * @param queryPage
	 * @return
	 */
	Page<SecureUserGroup> retrieveGroups(Page<SecureUserGroup> queryPage, @Operator SecureUser operator);
	/**
	 * 查询指定用户组及附加信息（角色（可跨站）、用户）
	 * @param groupId
	 * @param operator
	 * @return
	 * @throws AuthorizationException 操作者不能访问跨站数据
	 */
	SecureUserGroup retriveGroupWithDetails(String groupId, @Operator SecureUser operator) throws AuthorizationException;
	/**
	 * 根据用户组ID查询指定用户组
	 * @param groupId
	 * @param operator
	 * @return
	 */
	SecureUserGroup retriveGroupById(String groupId, @Operator SecureUser operator);
	/**
	 * 根据用户组名称查询当前站点下匹配的用户组
	 * @param groupName
	 * @param operator
	 * @return
	 */
	SecureUserGroup retriveGroupByName(String groupName, @Operator SecureUser operator);

	/**
	 * 创建用户组
	 * @param group
	 * @param operator
	 * @return
	 * @throws SequenceExistsException
	 * @throws AuthorizationException
	 */
	SecureUserGroup createGroupWithDetails(@CUDTarget SecureUserGroup group,  @Operator SecureUser operator)  throws AuthorizationException;
	

	/**
	 * 更新用户组
	 * @param group
	 * @param operator
	 * @return
	 * @throws AuthorizationException
	 */
	SecureUserGroup updateGroup(@CUDTarget SecureUserGroup group,  @Operator SecureUser operator) throws AuthorizationException;
	
	
	/**
	 * 删除用户组
	 * @param groupId
	 * @param operator
	 * @throws AuthorizationException
	 */
	void deleteGroup(String groupId, @Operator SecureUser operator) throws AuthorizationException;
	
	/**
	 * 删除多个用户组
	 * @param operator
	 * @throws AuthorizationException
	 */
	void deleteGroups(String[] groups, @Operator SecureUser operator) throws AuthorizationException;
	/**
	 * 向用户组添加用户
	 * @param userId
	 * @param groupId
	 * @param operator
	 * @throws AuthorizationException
	 */
	void addUserToGroup(String userId, String groupId, @Operator SecureUser operator) throws AuthorizationException;
	/**
	 * 向用户组添加多个用户
	 * @param userIds
	 * @param groupId
	 * @param operator
	 * @throws AuthorizationException
	 */
	void addUsersToGroup(String[] userIds, String groupId, @Operator SecureUser operator) throws AuthorizationException;

	/**
	 * 将多个用户以及多个组织下的所有用户添加到用户组（包含子机构）
	 * @param userIds
	 * @param orgCodes
	 * @param groupId
	 * @param operator
	 * @throws AuthorizationException
	 */
	void addUsersToGroup(String[] userIds, String[] orgCodes, String groupId, @Operator SecureUser operator) throws AuthorizationException;

	/**
	 * 从用户组移除用户
	 * @param userId
	 * @param groupId
	 * @param operator
	 * @throws AuthorizationException
	 */
	void removeUserFromGroup(String userId, String groupId, @Operator SecureUser operator) throws AuthorizationException;
	/**
	 * 向用户组移除多个用户
	 * @param groupId
	 * @param operator
	 * @throws AuthorizationException
	 */
	void removeUsersFromGroup(String[] userId, String groupId, @Operator SecureUser operator) throws AuthorizationException;
	
	//***************************** MENU FUNCTION 相关 ****************
	/**
	 * 获取指定菜单下的子菜单，此方法将返回所有菜单，无论当前用户是否获得授权
	 * @param parentMenuId 如果传入NULL，则返回所有一级菜单
	 * @return
	 */
	List<SecureMenu> retrieveMenus(String parentMenuId);
	/**
	 * 获取指定菜单下的子功能，此方法将返回所有功能，无论当前用户是否获得授权
	 * @param parentMenuId
	 * @return
	 */
	List<SecureFunction> retrieveFunctions(String parentMenuId);
	/**
	 * 获取指定功能下的子功能，此方法将返回所有功能，无论当前用户是否获得授权
	 * @param parentFunctionId 如果传入NULL，则返回所有一级功能
	 * @return
	 */
	List<SecureFunction> retrieveSubFunctions(String parentFunctionId);
	

	/**
	 * 从数据库中获取用户的密码（不做处理）
	 * @param userName
	 * @return
	 */
	String getUserPassword(String userName);
	
	/**
	 * 显示和某个角色相关的组织（用于在组织机构树中过滤掉无关节点）
	 * @param role
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	List<Map> selectOrgsRelatedToRole(String role);
	
	/**
	 * 显示和某个用户组相关的组织（用于在组织机构树中过滤掉无关节点）
	 * @param group
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	List<Map> selectOrgsRelatedToGroup(String group);
	
	/**
	 * 获取父节点为指定参数的组织
	 * @param oids
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	List<Organization> selectOrgsByParentIds(@Param("oids") List<String> oids);
	
	/**
	 * 选出一组组织的父节点（用于树展开）
	 * @param oids
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	List<Map> selectOrgsParents(@Param("oids") List<String> oids);
	
	/**
	 * 根据组织ID返回组织信息
	 * @param orgId
	 * @return
	 */
	Organization selectOrgById(String orgId);

	/**
	 * 修改用户的密码（TIMSS1需要）
	 * @param user 被修改的用户，只需要赋值用户名和密码
	 * @param operator
	 * @throws IllegalAccessException 
	 * @throws Exception
	 * 注意这个函数要求用户是管理员（可以修改所有人的密码），或者是某个站点的管理员（可以修改当前站点下用户的密码）
	 */
	void updateUserPassword(@CUDTarget SecureUser user, @Operator SecureUser operator) throws IllegalAccessException;
	
	/**
	 * 获取用户的组织机构（下拉提示列表需要）
	 * @param userId
	 * @return
	 */
	List<Organization> selectOrgUserBelongsTo(String userId);

	/**
	 * 获取用户收藏的功能(只显示当前站点，按权限过滤)
	 * @param user
	 * @return
     */
	List<FavRoute> selectFavRoute(SecureUser user);

	void editFavRoute(SecureUser user, String toAdd, String toRemove);

	boolean isFavRouteExists(SecureUser user,String routeId);
}
