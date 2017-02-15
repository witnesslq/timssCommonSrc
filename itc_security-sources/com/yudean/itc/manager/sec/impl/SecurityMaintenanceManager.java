package com.yudean.itc.manager.sec.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.yudean.itc.dao.sec.*;
import com.yudean.itc.dto.sec.*;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.yudean.itc.annotation.CUDTarget;
import com.yudean.itc.annotation.Operator;
import com.yudean.itc.annotation.Secured;
import com.yudean.itc.code.PrivilegeType;
import com.yudean.itc.code.StatusCode;
import com.yudean.itc.dto.Page;
import com.yudean.itc.exception.SequenceExistsException;
import com.yudean.itc.exception.sec.AuthorizationException;
import com.yudean.itc.manager.sec.ISecurityMaintenanceManager;

@Service
public class SecurityMaintenanceManager implements ISecurityMaintenanceManager {

	private static final Logger log = Logger.getLogger(SecurityMaintenanceManager.class);
	@Autowired
	private SecureUserMapper secureUserMapper;
	@Autowired
	private PrivilegeMapper privilegeMapper;
	@Autowired
	private SecureMenuMapper secureMenuMapper;
	@Autowired
	private SecureFunctionMapper secureFunctionMapper;
	@Autowired
	private RoleMapper roleMapper;
	@Autowired
	private SecureUserGroupMapper secureUserGroupMapper;
	@Autowired
	private OrganizationMapper organizationMapper;
	@Autowired
	private FrontRouteMapper frontRouteMapper;


	@Secured
	public Page<SecureUser> retrieveUniqueUsers(Page<SecureUser> queryPage, @Operator SecureUser operator) {
		if(!operator.isSuperAdmin() || !queryPage.getParams().containsKey("ignoreSite")){
			queryPage.setParameter("site", operator.getCurrentSite());
		}
		List<SecureUser> userList = secureUserMapper.selectUniqueUsersInOrg(queryPage);
		queryPage.setResults(userList);
		return queryPage;
	}

	// 创建用户完整资料
	@Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
	@Secured
	public SecureUser createUserWithDetails(SecureUser newUser, @Operator SecureUser operator) throws SequenceExistsException, AuthorizationException {

		// 自动设置信息
		newUser.setSuperAdminInd(StatusCode.NO);
		// Save
		secureUserMapper.insertUser(newUser);
		log.debug("-- user profile created");

		createUserSecurityProfile(newUser, operator);

		return newUser;
	}

	@Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
	@Secured
	public SecureUser updateUserWithDetails(@CUDTarget SecureUser userTobeSaved, @Operator SecureUser operator) throws SequenceExistsException,
			AuthorizationException {

		updateUser(userTobeSaved, operator);

		// 先清除所有关系
		if (operator.isSuperAdmin()) {
			// 如果是超级管理员，更新所有的组织关系
			organizationMapper.deleteAllOrgMapping(userTobeSaved.getId());
			// 如果是超级管理员，更新所有的角色关系
			roleMapper.deleteAllRolesFromUser(userTobeSaved.getId());
		} else {
			// 站点管理员，只能更新所在站点的组织关系
			organizationMapper.deleteOrgMappingInGivenSite(userTobeSaved.getId(), operator.getCurrentSite());
			// 站点管理员只能更新所在站点的角色关系
			roleMapper.deleteRolesFromUser(userTobeSaved.getId(), operator.getCurrentSite());
		}

		// 清除所在站点的用户组关系，因为用户组不能跨站点操作，所以此处不作operator的识别
		secureUserGroupMapper.deleteGivenUserFromAllGroups(userTobeSaved.getId(), operator.getCurrentSite());

		// 重建关系
		createUserSecurityProfile(userTobeSaved, operator);

		return userTobeSaved;
	}

	@Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
	@Secured
	public SecureUser updateUser(@CUDTarget SecureUser userTobeSaved, @Operator SecureUser operator) throws SequenceExistsException,
			AuthorizationException {
		// TODO 唯一性判断
		
		// kchen modify 2015-4-16 解决更新用户信息时，状态枚举量为YES字符串过长的问题。
		//zhouhx 2016-04-21 解决更新用户信息时，状态枚举量为NO字符串过长的问题。
		if(StatusCode.YES.equals(userTobeSaved.getSyncInd())){
			userTobeSaved.setSyncInd(StatusCode.Y);
		}else if(StatusCode.NO.equals(userTobeSaved.getSyncInd())){
			userTobeSaved.setSyncInd(StatusCode.N);
		}
		// 2015-4-16
		secureUserMapper.updateUser(userTobeSaved);
		log.debug("-- user profile updated");
		return userTobeSaved;
	}

	@Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
	private void createUserSecurityProfile(SecureUser newUser, SecureUser operator) throws AuthorizationException {

		// 创建组织机构关系
		if (newUser.getOrganizations() != null) {
			for (Organization org : newUser.getOrganizations()) {
				organizationMapper.insertOrgUserMap(org.getCode(), newUser.getId());
			}
			log.debug("-- user organization mapping created");
		}
		// 创建用户组关系
		if (newUser.getGroups() != null) {
			for (SecureUserGroup group : newUser.getGroups()) {
				this.addUserToGroup(newUser.getId(), group.getId(), operator);
			}
			log.debug("-- user group mapping created");
		}
		// 创建角色
		if (newUser.getRoles() != null) {
			for (Role role : newUser.getRoles()) {
				this.assignRoleToUser(role.getId(), newUser.getId(), operator);
			}
			log.debug("-- user role mapping created");
		}
	}

	// 删除用户
	@Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
	@Secured
	public void deleteUser(String userTobeDeleted, @Operator SecureUser operator) {
		// secureUserMapper.deleteUser(userTobeDeleted);
		secureUserMapper.updateUserDelIndicator(userTobeDeleted, StatusCode.YES.toString());
		log.debug("-- user deleted: " + userTobeDeleted);
	}

	@Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
	public void updateUserStatus(String userId, StatusCode updateTo) {
		secureUserMapper.updateUserStatus(userId, updateTo.toString());

	}

	@Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
	@Secured
	public void deleteUsers(String[] userTobeDeleted, @Operator SecureUser operator) {
		if (userTobeDeleted != null) {
			for (String userId : userTobeDeleted) {
				deleteUser(userId, operator);
			}
		}
	}

	// 根据工号查找用户
	public SecureUser retrieveUserById(String userID) {
		SecureUser user = secureUserMapper.selectUser(userID);
		return user;
	}

	@Secured
	@Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
	public SecureUser retrieveUserWithDetails(String userID, @Operator SecureUser operator) {
		SecureUser user = retrieveUserById(userID);
		// retrieve organizations
		// TODO 区分operator？
		List<Organization> org = selectOrgUserBelongsTo(userID);
		user.setOrganizations(org);

		// retrieve groups
		List<SecureUserGroup> groups = secureUserGroupMapper.selectBelongingGroups(userID, new String[] { operator.getCurrentSite() });
		user.setGroups(groups);

		// retireve roles
		List<Role> roles = null;
		if (operator.isSuperAdmin()) {
			roles = roleMapper.selectRolesOfGivenUser(userID, null);
		} else {
			roles = roleMapper.selectRolesOfGivenUser(userID, new String[] { operator.getCurrentSite() });
		}
		user.setRoles(roles);

		return user;
	}

	@Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
	@Secured
	public void addUserToOrganizations(String userId, String orgCode[], @Operator SecureUser operator) {
		for (String code : orgCode) {
			organizationMapper.insertOrgUserMap(code, userId);
		}
	}

	@Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
	@Secured
	public void removeUserFromOrganizations(String userId, String orgCode[], @Operator SecureUser operator) {
		for (String code : orgCode) {
			organizationMapper.deleteOrgUserMap(code, userId);
		}
	}

	/************************************* 用户组 *************************************/

	// 分页读取所有用户组
	@Secured
	public Page<SecureUserGroup> retrieveGroups(Page<SecureUserGroup> queryPage, @Operator SecureUser operator) {

		List<SecureUserGroup> results = secureUserGroupMapper.selectGroups(queryPage, new String[] { operator.getCurrentSite() });
		queryPage.setResults(results);

		return queryPage;
	}

	@Secured
	public SecureUserGroup retriveGroupWithDetails(String groupId, @Operator SecureUser operator) throws AuthorizationException {

		SecureUserGroup group = secureUserGroupMapper.selectGroup(groupId, new String[] { operator.getCurrentSite() });

		if (group == null)
			throw new AuthorizationException("无法访问该用户组：" + groupId);

		List<SecureUser> members = secureUserMapper.selectUsersInGivenGroup(groupId);
		group.setUsers(members);

		List<Role> roles = null;
		if (operator.isSuperAdmin()) {
			roles = roleMapper.selectRolesOfGivenGroup(groupId, null);
		} else {
			roles = roleMapper.selectRolesOfGivenGroup(groupId, new String[] { operator.getCurrentSite() });
		}
		group.setRoles(roles);

		return group;

	}

	@Secured
	public SecureUserGroup retriveGroupById(String groupId, @Operator SecureUser operator) {
		SecureUserGroup group = secureUserGroupMapper.selectGroup(groupId, new String[] { operator.getCurrentSite() });
		return group;
	}

	@Secured
	public SecureUserGroup retriveGroupByName(String groupName, @Operator SecureUser operator) {
		SecureUserGroup group = secureUserGroupMapper.selectGroupByName(groupName, new String[] { operator.getCurrentSite() });
		return group;
	}

	@Secured
	@Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
	public SecureUserGroup createGroupWithDetails(@CUDTarget SecureUserGroup group, @Operator SecureUser operator) throws AuthorizationException {

		secureUserGroupMapper.insertGroup(group);
		// 创建用户组与角色、用户的关联
		createGroupSecurityProfile(group, operator);

		return group;
	}

	@Secured
	@Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
	protected SecureUserGroup updateGroupWithDetails(@CUDTarget SecureUserGroup group, @Operator SecureUser operator) throws AuthorizationException {

		secureUserGroupMapper.updateGroup(group);

		// 清除现有关系
		secureUserGroupMapper.deleteUsersFromGivenGroup(group.getId());
		if (operator.isSuperAdmin()) {
			// 如果是超级管理员，清除该用户组的所有角色
			roleMapper.deleteAllRolesFromGroup(group.getId());
		} else {
			// 站点管理员只能更新所在站点的角色关系
			roleMapper.deleteRolesFromGroup(group.getId(), operator.getCurrentSite());
		}

		// 重建用户组与角色、用户的关联
		createGroupSecurityProfile(group, operator);
		return group;
	}

	@Secured
	@Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
	public SecureUserGroup updateGroup(@CUDTarget SecureUserGroup group, @Operator SecureUser operator) throws AuthorizationException {

		secureUserGroupMapper.updateGroup(group);
		return group;
	}

	@Secured
	@Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
	public void deleteGroup(String groupId, @Operator SecureUser operator) throws AuthorizationException {
		// TODO 优化此处，使用拦截器进行权限检查
		SecureUserGroup group = retriveGroupById(groupId, operator);
		if (group == null) {
			log.error("用户组不存在，或用户无权操作：" + groupId);
			return;
		}
		secureUserGroupMapper.deleteGroup(groupId);

	}

	@Secured
	@Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
	public void deleteGroups(String[] groups, @Operator SecureUser operator) throws AuthorizationException {
		if (groups != null) {
			for (String groupId : groups)
				deleteGroup(groupId, operator);
		}

	}

	// 增加用户到用户组, 不能跨站访问
	@Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
	@Secured
	public void addUserToGroup(String userId, String groupId, @Operator SecureUser operator) throws AuthorizationException {

		SecureUserGroup existingGroup = secureUserGroupMapper.selectGroup(groupId, new String[] { operator.getCurrentSite() });

		if (existingGroup == null)
			throw new AuthorizationException("角色不存在，或无权访问。GROUP_ID=" + groupId + ", USER_ID=" + operator.getId());

		createGroupUserMapper(groupId, userId);
	}

	@Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
	@Secured
	public void addUsersToGroup(String[] userIds, String groupId, @Operator SecureUser operator) throws AuthorizationException {

		SecureUserGroup existingGroup = secureUserGroupMapper.selectGroup(groupId, new String[] { operator.getCurrentSite() });

		if (existingGroup == null)
			throw new AuthorizationException("角色不存在，或无权访问。GROUP_ID=" + groupId + ", USER_ID=" + operator.getId());
		for (String userId : userIds)
			createGroupUserMapper(groupId, userId);
	}

	@Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
	@Secured
	public void addUsersToGroup(String[] userIds, String[] orgs, String groupId, @Operator SecureUser operator) throws AuthorizationException {

		SecureUserGroup existingGroup = secureUserGroupMapper.selectGroup(groupId, new String[] { operator.getCurrentSite() });

		if (existingGroup == null)
			throw new AuthorizationException("角色不存在，或无权访问。GROUP_ID=" + groupId + ", USER_ID=" + operator.getId());

		// 查询组织机构下的所有用户（包含子机构）

		if (orgs != null) {
			for (String orgCode : orgs) {
				List<SecureUser> usersOfOrg = secureUserMapper.selectAllUsersInGivenOrg(orgCode, false);
				for (SecureUser user : usersOfOrg)
					createGroupUserMapper(groupId, user.getId());
			}
		}

		if (userIds != null)
			for (String userId : userIds)
				createGroupUserMapper(groupId, userId);

	}

	@Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
	private void createGroupUserMapper(String groupId, String userId) {
		Integer count = secureUserGroupMapper.selectGroupUserMapping(groupId, userId);
		if (count == null || count == 0)
			secureUserGroupMapper.insertGroupUserMapping(groupId, userId);
	}

	@Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
	@Secured
	public void removeUserFromGroup(String userId, String groupId, @Operator SecureUser operator) throws AuthorizationException {

		SecureUserGroup existingGroup = secureUserGroupMapper.selectGroup(groupId, new String[] { operator.getCurrentSite() });

		if (existingGroup == null)
			throw new AuthorizationException("角色不存在，或无权访问。GROUP_ID=" + groupId + ", USER_ID=" + operator.getId());

		secureUserGroupMapper.deleteGroupUserMapping(groupId, userId);
	}

	@Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
	@Secured
	public void removeUsersFromGroup(String[] userIds, String groupId, @Operator SecureUser operator) throws AuthorizationException {

		SecureUserGroup existingGroup = secureUserGroupMapper.selectGroup(groupId, new String[] { operator.getCurrentSite() });

		if (existingGroup == null)
			throw new AuthorizationException("角色不存在，或无权访问。GROUP_ID=" + groupId + ", USER_ID=" + operator.getId());
		for (String userId : userIds)
			secureUserGroupMapper.deleteGroupUserMapping(groupId, userId);
	}

	@Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
	private void createGroupSecurityProfile(SecureUserGroup group, @Operator SecureUser operator) throws AuthorizationException {

		// 创建用户关系
		if (group.getUsers() != null) {
			for (SecureUser user : group.getUsers()) {
				this.addUserToGroup(user.getId(), group.getId(), operator);
			}
			log.debug("-- user group mapping created");
		}
		// 创建角色
		if (group.getRoles() != null) {
			for (Role role : group.getRoles()) {
				this.assignRoleToGroup(role.getId(), group.getId(), operator);
			}
			log.debug("-- group role mapping created");
		}
	}

	/************************************* 组织机构 *************************************/
	// 组织机构
	public List<Organization> selectOrgUserBelongsTo(String userId) {
		return organizationMapper.selectOrgUserBelongsTo(userId);
	}

	/************************************* 角色(role) *************************************/

	@Secured
	public Page<Role> retrieveRoles(Page<Role> queryPage, @Operator SecureUser operator) {
		List<Role> roles = null;
		if (operator.isSuperAdmin() && queryPage.getParams().get("siteId") != null) {
			roles = roleMapper.selectRoles(queryPage, null);
		} else {
			queryPage.getParams().remove("siteId");
			roles = roleMapper.selectRoles(queryPage, new String[] { operator.getCurrentSite() });
		}
		queryPage.setResults(roles);
		return queryPage;
	}

	@Secured
	public Role retrieveRoleWithDetails(String roleId, @Operator SecureUser operator) throws AuthorizationException {
		Role role = retrieveRoleById(roleId, operator);
		// 查询角色拥有那些权限
		List<Privilege> privileges = privilegeMapper.selectRolePrivileges(roleId);
		role.setPrivileges(privileges);

		// 查询角色分配给了哪些用户组
		List<SecureUserGroup> groups = secureUserGroupMapper.selectGroupsOfGivenRole(roleId, new String[] { operator.getCurrentSite() });
		role.setGroups(groups);
		// 查询角色分配给了哪些用户(包含通过用户组继承获得的）
		List<SecureUser> users = retrieveUsersHaveGivenRole(roleId, operator);
		role.setUsers(users);
		return role;
	}

	protected List<SecureUser> retrieveUsersHaveGivenRole(String roleId, SecureUser operator) {
		// TODO 是否需要分页查询？
		List<SecureUser> users = null;
		if (operator.isSuperAdmin())
			users = secureUserMapper.selectUsersOfGivenRole(roleId, null);
		else
			users = secureUserMapper.selectUsersOfGivenRole(roleId, operator.getCurrentSite());

		// 去重，如果用户有直接授权和通过用户组继承获得同一个权限的，优先显示直接授权？
		Map<String, SecureUser> uniqueUsers = new HashMap<String, SecureUser>();
		if (users != null && !users.isEmpty()) {
			for (SecureUser user : users) {
				if (uniqueUsers.containsKey(user.getId())) {
					SecureUser existingUser = uniqueUsers.get(user.getId());
					// 如果已经存在继承的角色，又有直接授权的角色，用后者覆盖前者
					if (existingUser.getHasInheritedRole() == StatusCode.YES && user.getHasInheritedRole() == StatusCode.NO)
						uniqueUsers.put(user.getId(), user);

				} else {
					uniqueUsers.put(user.getId(), user);
				}
			}
		}

		return new ArrayList<SecureUser>(uniqueUsers.values());
	}

	@Secured
	public Role retrieveRoleById(String roleId, @Operator SecureUser operator) {
		Role r = null;
		if (operator.isSuperAdmin())
			// 超级管理员读取角色不收站点限制。
			r = roleMapper.selectRole(roleId, null);
		else
			r = roleMapper.selectRole(roleId, new String[] { operator.getCurrentSite() });
		return r;
	}

	@Secured
	public Role retrieveRoleByName(String roleName, @Operator SecureUser operator) {
		Role role = roleMapper.selectRoleByName(roleName, new String[] { operator.getCurrentSite() });
		return role;
	}

	@Secured
	public List<Role> retrieveRolesByPrivilege(String privilegeId, @Operator SecureUser operator) {
		List<Role> roles = roleMapper.selectRolesByPrivilege(privilegeId, new String[] { operator.getCurrentSite() });
		return roles;
	}

	@Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
	@Secured
	public Role createRoleWithDetails(@CUDTarget Role role, @Operator SecureUser operator) throws AuthorizationException {
		roleMapper.insertRole(role);
		createRoleDetails(role, operator);
		return null;
	}

	@Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
	@Secured
	public Role updateRole(@CUDTarget Role role, @Operator SecureUser operator) throws AuthorizationException {
		roleMapper.updateRole(role);
		return role;
	}

	@Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
	@Secured
	public void deleteRole(String roleId, @Operator SecureUser operator) throws AuthorizationException {
		Role r = this.retrieveRoleById(roleId, operator);
		if (r == null) {
			log.error("角色不存在，或用户无权操作：" + roleId);
			return;
		}
		roleMapper.deleteRole(roleId);

	}

	@Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
	@Secured
	public void deleteRoles(String[] roles, @Operator SecureUser operator) throws AuthorizationException {
		if (roles != null) {
			for (String roleId : roles)
				deleteRole(roleId, operator);
		}
	}

	@Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
	protected void createRoleDetails(Role role, SecureUser operator) {
		if (role.getGroups() != null) {
			for (SecureUserGroup group : role.getGroups()) {
				roleMapper.insertRoleGroupMapping(role.getId(), group.getId());
			}
		}
		if (role.getUsers() != null) {
			for (SecureUser user : role.getUsers())
				roleMapper.insertRoleUserMapping(role.getId(), user.getId());
		}
	}

	@Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
	@Secured
	public void assignRoleToUser(String roleId, String userId, @Operator SecureUser operator) throws AuthorizationException {
		Role r = this.retrieveRoleById(roleId, operator);
		if (r == null)
			throw new AuthorizationException("角色不存在，或无权访问。ROLE_ID=" + roleId + ", USER_ID=" + operator.getId());
		assignRoleToUser(roleId, userId);
	}

	@Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
	@Secured
	public void assignRoleToUsers(String roleId, String[] userIds, @Operator SecureUser operator) throws AuthorizationException {
		Role r = this.retrieveRoleById(roleId, operator);
		if (r == null)
			throw new AuthorizationException("角色不存在，或无权访问。ROLE_ID=" + roleId + ", USER_ID=" + operator.getId());
		for (String userId : userIds)
			assignRoleToUser(roleId, userId);
	}

	@Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
	@Secured
	public void assignRoleToUsers(String roleId, String[] userIds, String[] orgCodes, @Operator SecureUser operator) throws AuthorizationException {
		Role r = this.retrieveRoleById(roleId, operator);
		if (r == null)
			throw new AuthorizationException("角色不存在，或无权访问。ROLE_ID=" + roleId + ", USER_ID=" + operator.getId());

		// 查询组织机构下的所有用户（包含子机构）

		if (orgCodes != null) {
			for (String orgCode : orgCodes) {
				List<SecureUser> usersOfOrg = secureUserMapper.selectAllUsersInGivenOrg(orgCode, false);
				for (SecureUser user : usersOfOrg)
					assignRoleToUser(roleId, user.getId());
			}
		}

		if (userIds != null)
			for (String userId : userIds)
				assignRoleToUser(roleId, userId);
	}

	@Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
	@Secured
	public void assignRolesToGroup(String[] roleIds, String groupId, @Operator SecureUser operator) throws AuthorizationException {
		if (roleIds != null) {
			for (String roleId : roleIds)
				this.assignRoleToGroup(roleId, groupId, operator);
		}
	}

	@Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
	@Secured
	public void removeRoleFromUser(String roleId, String userId, @Operator SecureUser operator) throws AuthorizationException {
		Role r = this.retrieveRoleById(roleId, operator);
		if (r == null)
			throw new AuthorizationException("角色不存在，或无权访问。ROLE_ID=" + roleId + ", USER_ID=" + operator.getId());
		roleMapper.deleteRoleUserMapping(roleId, userId);
	}

	@Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
	private void assignRoleToUser(String roleId, String userId) {
		Integer count = roleMapper.selectRoleUserMapping(roleId, userId);
		if (count == null || count == 0)
			roleMapper.insertRoleUserMapping(roleId, userId);

	}

	@Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
	@Secured
	public void removeRoleFromUsers(String roleId, String[] userIds, @Operator SecureUser operator) throws AuthorizationException {
		Role r = this.retrieveRoleById(roleId, operator);
		if (r == null)
			throw new AuthorizationException("角色不存在，或无权访问。ROLE_ID=" + roleId + ", USER_ID=" + operator.getId());
		for (String userId : userIds)
			roleMapper.deleteRoleUserMapping(roleId, userId);
	}

	@Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
	@Secured
	public void removeRolesFromGroup(String[] roleIds, String groupId, @Operator SecureUser operator) throws AuthorizationException {
		if (roleIds != null) {
			for (String roleId : roleIds)
				this.removeRoleFromGroup(roleId, groupId, operator);
		}
	}

	@Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
	@Secured
	public void assignRoleToGroup(String roleId, String groupId, @Operator SecureUser operator) throws AuthorizationException {
		Role r = this.retrieveRoleById(roleId, operator);
		if (r == null)
			throw new AuthorizationException("角色不存在，或无权访问。ROLE_ID=" + roleId + ", USER_ID=" + operator.getId());
		roleMapper.insertRoleGroupMapping(roleId, groupId);
	}

	@Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
	@Secured
	public void assignRoleToGroups(String roleId, String[] groupIds, @Operator SecureUser operator) throws AuthorizationException {
		Role r = this.retrieveRoleById(roleId, operator);
		if (r == null)
			throw new AuthorizationException("角色不存在，或无权访问。ROLE_ID=" + roleId + ", USER_ID=" + operator.getId());
		for (String groupId : groupIds)
			roleMapper.insertRoleGroupMapping(roleId, groupId);
	}

	@Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
	@Secured
	public void removeRoleFromGroup(String roleId, String groupId, @Operator SecureUser operator) throws AuthorizationException {
		Role r = this.retrieveRoleById(roleId, operator);
		if (r == null)
			throw new AuthorizationException("角色不存在，或无权访问。ROLE_ID=" + roleId + ", USER_ID=" + operator.getId());
		roleMapper.deleteRoleGroupMapping(roleId, groupId);

	}

	@Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
	@Secured
	public void removeRoleFromGroups(String roleId, String[] groupIds, @Operator SecureUser operator) throws AuthorizationException {
		Role r = this.retrieveRoleById(roleId, operator);
		if (r == null)
			throw new AuthorizationException("角色不存在，或无权访问。ROLE_ID=" + roleId + ", USER_ID=" + operator.getId());
		for (String groupId : groupIds)
			roleMapper.deleteRoleGroupMapping(roleId, groupId);

	}

	@Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
	@Secured
	public void assignPrivilegesToRole(Privilege[] privileges, String roleId, @Operator SecureUser operator) throws AuthorizationException {
		Role r = this.retrieveRoleById(roleId, operator);
		if (r == null)
			throw new AuthorizationException("角色不存在，或无权访问。ROLE_ID=" + roleId + ", USER_ID=" + operator.getId());

		for (Privilege p : privileges) {
			if (p.getCategory() == PrivilegeType.FUNCTION)
				roleMapper.insertRoleFunctionMapping(roleId, p.getId());
			else if (p.getCategory() == PrivilegeType.MENU)
				roleMapper.insertRoleMenuMapping(roleId, p.getId());
			else
				throw new IllegalArgumentException("Privilege对象应声明Category");
		}
	}

	@Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
	@Secured
	public void removePrivilegesFromRole(Privilege[] privileges, String roleId, @Operator SecureUser operator) throws AuthorizationException {
		Role r = this.retrieveRoleById(roleId, operator);
		if (r == null)
			throw new AuthorizationException("角色不存在，或无权访问。ROLE_ID=" + roleId + ", USER_ID=" + operator.getId());
		for (Privilege p : privileges) {
			if (p.getCategory() == PrivilegeType.FUNCTION)
				roleMapper.deleteRoleFunctionMapping(roleId, p.getId());
			else if (p.getCategory() == PrivilegeType.MENU)
				roleMapper.deleteRoleMenuMapping(roleId, p.getId());
			else
				throw new IllegalArgumentException("Privilege对象应声明Category");
		}
	}

	public List<SecureMenu> retrieveMenus(String parentMenuId) {
		List<SecureMenu> menus = secureMenuMapper.selectSubMenus(parentMenuId, null, null, true);
		return menus;
	}

	public List<SecureFunction> retrieveFunctions(String parentMenuId) {
		List<SecureFunction> functions = secureFunctionMapper.selectSubFunctionsOfGivenMenu(parentMenuId);
		return functions;
	}

	public List<SecureFunction> retrieveSubFunctions(String parentFunctionId) {
		List<SecureFunction> functions = secureFunctionMapper.selectSubFunctions(parentFunctionId);
		return functions;
	}

	@Secured
	public void updateUserPassword(@CUDTarget SecureUser user, @Operator SecureUser operator) throws IllegalAccessException {
		boolean hasPrivilege = false;
		if(operator.isSuperAdmin()){
			//超管可以改所有人的密码
			hasPrivilege = true;
		}else if(operator.getId().equals(user.getId())){
			//用户可以修改自己的密码
			hasPrivilege = true;
		}else{
			//一般的管理员只能改当前站点下的密码
			//首先判断管理员是否有"*site*admin"的权限
			boolean hasAdminRole = false;
			List<Role> roleOperator = operator.getRoles(); 
			for(int i=0;i<roleOperator.size();i++){
				String roleName = roleOperator.get(i).getId().toLowerCase();
				if(roleName.indexOf("admin") >=0 && roleName.indexOf("site") >=0){
					hasAdminRole = true;
					break;
				}
			}
			if(hasAdminRole){
				//判断当前用户是否跟管理员同站点
				List<Role> userRole = roleMapper.selectOwnActiveRoles(user.getId(), operator.getCurrentSite());
				if(userRole.size() > 0){
					hasPrivilege = true;
				}
			}
			
		}
		if(!hasPrivilege){
			throw new IllegalAccessException("您没有当前站点的管理员权限");
		}
		secureUserMapper.updateUserPassword(user);
	}

	public String getUserPassword(String userName) {
		return secureUserMapper.selectUserPassword(userName);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<Map> selectOrgsRelatedToRole(String role) {
		return organizationMapper.selectOrgsRelatedToRole(role);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<Organization> selectOrgsByParentIds(List<String> oids) {
		return organizationMapper.selectOrgsByParentIds(oids);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<Map> selectOrgsRelatedToGroup(String group) {
		return organizationMapper.selectOrgsRelatedToGroup(group);
	}

	@Override
	public Organization selectOrgById(String orgId) {
		return organizationMapper.selectOrgByID(orgId);
	}

	@Override
	public List<Map> selectOrgsParents(List<String> oids) {
		return organizationMapper.selectOrgsParents(oids);
	}

	@Override
	public List<FavRoute> selectFavRoute(SecureUser user) {
		List<FavRoute> data = frontRouteMapper.selectFavRoute(user.getId(), user.getCurrentSite());
		List<FavRoute> filteredData = new ArrayList<FavRoute>();
		for(int i=0;i<data.size();i++){
			FavRoute route = data.get(i);
			//权限过滤
			String privilege = route.getRequirePrivilege();
			boolean matchPriv = true;
			if(!privilege.equals("*")){
				String[] privArr = privilege.split("\\s+");
				for(String priv: privArr){
					if(!user.isPrivOwnedByUser(priv)){
						matchPriv = false;
						break;
					}
				}
			}
			if(matchPriv){
				filteredData.add(route);
			}
		}
		return filteredData;
	}

	@Override
	@Transactional
	public void editFavRoute(SecureUser user, String toAdd, String toRemove) {
		if(toRemove != null) {
			String[] arrToRemove = toRemove.split(",");
			frontRouteMapper.deleteFavRoute(user.getId(), arrToRemove);
		}
		if(toAdd != null) {
			String[] arrToAdd = toAdd.split(",");
			frontRouteMapper.insertFavRoute(user.getId(), arrToAdd, user.getCurrentSite());
		}
	}

	@Override
	public boolean isFavRouteExists(SecureUser user, String routeId) {
		return frontRouteMapper.selectIsRouteExist(user.getId(), routeId, user.getCurrentSite()) != null;
	}
}
