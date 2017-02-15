package com.yudean.itc.dao.sec;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yudean.itc.annotation.Secured;
import com.yudean.itc.dto.Page;
import com.yudean.itc.dto.sec.Role;
import com.yudean.itc.dto.sec.SecureUser;

public interface RoleMapper {

	/**
	 * 根据ID访问角色
	 * @param roleId
	 * @param filter 填入site id，如果不受数据权限限制，设置为NULL
	 * @return
	 */
	@Secured(functionId="F-ROLE-VIEW")
	Role selectRole(@Param("roleId") String roleId, @Param("FILTER") String[] filter);
	/**
	 * 根据角色名查询
	 * @param roleName
	 * @param filter
	 * @return
	 */
	@Secured(functionId="F-ROLE-VIEW")
	Role selectRoleByName(@Param("roleName") String roleName, @Param("FILTER") String[] filter);
	
	/**
	 * 根据权限查询
	 * @param roleName
	 * @param filter
	 * @return
	 */
	@Secured(functionId="F-ROLE-VIEW")
	List<Role> selectRolesByPrivilege(@Param("privilegeId") String privilegeId, @Param("FILTER") String[] filter);
	
	/**
	 * 新增角色
	 * @param r
	 */
	void insertRole(Role r);
	/**
	 * 更新角色
	 * @param r
	 */
	void updateRole(Role r);
	/**
	 * 物理删除角色
	 * @param roleId
	 */
	void deleteRole(String roleId);
	/**
	 * 授权角色给用户
	 * @param roleId
	 * @param userId
	 */
	void insertRoleUserMapping(@Param("roleId") String roleId, @Param("userId") String userId);

	/**
	 * 查询用户是否拥有指定角色
	 * @param roleId
	 * @param userId
	 * @return
	 */
	Integer selectRoleUserMapping(@Param("roleId") String roleId, @Param("userId") String userId);
	/**
	 * 删除用户-角色授权
	 * @param roleId
	 * @param userId
	 */
	void deleteRoleUserMapping(@Param("roleId") String roleId, @Param("userId") String userId);
	/**
	 * 删除指定用户的所有角色授权
	 * @param userId
	 */
	void deleteAllRolesFromUser(String userId);
	/**
	 * 删除指定用户在指定站点下的所有角色授权
	 * @param userId
	 * @param siteId
	 */
	void deleteRolesFromUser(@Param("userId") String userId, @Param("siteId") String siteId);
	
	/**
	 * 查询指定用户拥有的角色（include inactive, exclude roles inherited from groups）
	 * @param userId
	 * @param filter 填入site id，如果不受数据权限限制，设置为NULL
	 * @return
	 */
	@Secured(functionId="F-ROLE-VIEW")
	List<Role> selectRolesOfGivenUser(@Param("userId") String userId, @Param("FILTER") String[] filter);
	/**
	 * 授权角色给用户组
	 * @param roleId
	 * @param groupId
	 */
	void insertRoleGroupMapping(@Param("roleId") String roleId, @Param("groupId") String groupId);
	/**
	 * 删除指定用户组的所有角色授权
	 * @param groupId
	 */
	void deleteAllRolesFromGroup(String groupId);
	/**
	 * 删除指定用户组在指定站点下的所有角色授权
	 */
	void deleteRolesFromGroup(@Param("groupId") String groupId, @Param("siteId") String siteId);
	/**
	 * 删除用户组-角色授权
	 * @param roleId
	 * @param groupId
	 */
	void deleteRoleGroupMapping(@Param("roleId") String roleId, @Param("groupId") String groupId);
	/**
	 * 查询指定用户组拥有的角色（include inactive）
	 * @param groupId
	 * @param filter 填入site id，如果不受数据权限限制，设置为NULL
	 * @return
	 */
	@Secured(functionId="F-ROLE-VIEW")
	List<Role> selectRolesOfGivenGroup(@Param("groupId") String groupId, @Param("FILTER") String[] filter);	
	
	/**
	 * 查询指定用户在指定站点下拥有的有效角色
	 * @param userId
	 * @return
	 */
	List<Role> selectOwnActiveRoles(@Param("userId")String userId, @Param("siteId")String siteId);
	/**
	 * 查询指定站点下所有有效的角色
	 * @param siteId
	 * @return
	 */
	List<Role> selectAllActiveRoles(String siteId);
	/**
	 * 分页查询角色
	 * @param queryPage
	 * @param filter
	 * @return
	 */
	@Secured(functionId="F-ROLE-VIEW")
	List<Role> selectRoles(@Param("PAGE") Page<Role> queryPage, @Param("FILTER") String[] filter);
	
	void insertRoleFunctionMapping(@Param("roleId") String roleId, @Param("funcId") String funcId);
	
	void deleteRoleFunctionMapping(@Param("roleId") String roleId, @Param("funcId") String funcId);
	
	void insertRoleMenuMapping(@Param("roleId") String roleId, @Param("menuId") String menuId);
	
	void deleteRoleMenuMapping(@Param("roleId") String roleId, @Param("funcId") String funcId);
	
	/**
	 * 选出拥有特定角色的用户
	 * @param roleId	角色ID
	 * @param orgs	用户所属的用户组，可以为空
	 * @param onlyActive	是否只显示激活的用户
	 * @return
	 * @deprecated YU: use SecureUserMapper.selectUsersOfGivenRole()
	 */
	List<SecureUser> selectUserWithSpecificRole(@Param("roleId") String roleId,@Param("orgs") List<String> orgs,@Param("onlyActive") boolean onlyActive);
}
