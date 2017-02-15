package com.yudean.itc.dao.sec;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yudean.itc.annotation.Secured;
import com.yudean.itc.dto.Page;
import com.yudean.itc.dto.sec.SecureUser;
import com.yudean.itc.dto.sec.SecureUserGroup;

/**
 * CRUD operator on SEC_GROUP
 * @author yushujiang
 *
 */
public interface SecureUserGroupMapper {

	/**
	 * Create a new user group
	 * @param group
	 */
	void insertGroup(SecureUserGroup group);
	/**
	 * update an user group
	 * @param group
	 */
	void updateGroup(SecureUserGroup group);
	/**
	 * delete an user group
	 * @param groupId
	 */
	void deleteGroup(String groupId);	
	/**
	 * retrieve an user group by id
	 * @param id
	 * @param siteFilter 数据过滤规则，[0]传入当前站点ID
	 * @return
	 */
	@Secured(functionId="F-GRP-VIEW")
	SecureUserGroup selectGroup(@Param("groupId")String id, @Param("FILTER") String[] siteFilter);

	/**
	 * 
	 * @param id
	 * @param siteFilter 数据过滤规则，[0]传入当前站点ID
	 * @return
	 */
	@Secured(functionId="F-GRP-VIEW")
	SecureUserGroup selectGroupByName(@Param("groupName")String groupName, @Param("FILTER") String[] siteFilter);
	/**
	 * 获取指定用户所属的用户组
	 * @param userId
	 * @param siteFilter 数据过滤规则，[0]传入当前站点ID
	 * @return
	 */
	@Secured(functionId="F-GRP-VIEW")
	List<SecureUserGroup> selectBelongingGroups(@Param("userId")String userId, 
			@Param("FILTER") String[] siteFilter);
	
	/**
	 * 分页查询用户组
	 * @param page
	 * @param siteFilter 数据过滤规则，[0]传入当前站点ID
	 * @return
	 */
	@Secured(functionId="F-GRP-VIEW")
	List<SecureUserGroup> selectGroups(@Param("PAGE") Page<SecureUserGroup> page, 
			@Param("FILTER") String[] siteFilter);
	/**
	 * Create relationship between a group and an user
	 * @param groupId
	 * @param userId
	 */
	void insertGroupUserMapping(@Param("groupId")String groupId  , @Param("userId") String userId);
	/**
	 * 查询用户是否已经存在于用户组
	 * @param groupId
	 * @param userId
	 * @return
	 */
	Integer selectGroupUserMapping(@Param("groupId")String groupId  , @Param("userId") String userId);
	
	/**
	 * 删除指定站点下指定用户的所有用户组
	 * @param groupId
	 * @param roleId
	 */
	void deleteGivenUserFromAllGroups(@Param("userId")String userId, @Param("siteId") String siteId);

	/**
	 * 移除指定用户组的所有用户
	 * @param groupId
	 */
	void deleteUsersFromGivenGroup(String groupId);
	/**
	 * remove relationship between a group and an user
	 * @param groupId
	 * @param userId
	 */
	void deleteGroupUserMapping(@Param("groupId")String groupId  , @Param("userId") String userId);
	/**
	 * 查询指定角色赋予了哪些用户组
	 * @param roleId
	 * @param siteFilter
	 * @return
	 */
	@Secured(functionId="F-GRP-VIEW")
	List<SecureUserGroup> selectGroupsOfGivenRole(@Param("roleId") String roleId, 
			@Param("FILTER") String[] siteFilter);
	
	/**
	 * 查询属于特定用户组的用户
	 * @param groupId
	 * @param orgs
	 * @param onlyActive
	 * @return
	 */
	List<SecureUser> selectUserWithSpecificGroup(@Param("groupId") String groupId,@Param("orgs") List<String> orgs,@Param("onlyActive") boolean onlyActive);
	
}
