package com.yudean.itc.dao.sec;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yudean.itc.annotation.MapperMethod;
import com.yudean.itc.annotation.MapperMethod.Type;
import com.yudean.itc.dto.Page;
import com.yudean.itc.dto.sec.SecureOrgUser;
import com.yudean.itc.dto.sec.SecureUser;

/**
 * SEC_USER的DAO类
 * 
 * @author yushujiang
 */
public interface SecureUserMapper {

    /**
     * 创建用户
     * 
     * @param user
     */
    void insertUser(SecureUser user);

    /**
     * 更新用户资料，不包含登录密码
     * 
     * @param user
     */
    void updateUser(SecureUser user);

    /**
     * 更新用户删除标记
     * 
     * @param userId
     * @param toStatus
     */
    void updateUserDelIndicator(@Param("userId") String userId, @Param("status") String toStatus);

    /**
     * 更新用户状态
     * 
     * @param userId
     * @param toStatus
     */
    void updateUserStatus(@Param("userId") String userId, @Param("status") String toStatus);

    /**
     * 物理删除用户
     * 
     * @param userId
     */
    void deleteUser(String userId);

    /**
     * 去重分页查询用户资料
     * 
     * @param page
     * @return
     */
    List<SecureUser> selectUniqueUsersInOrg(Page<SecureUser> page);

    /**
     * 在整个所有站点下搜索用户（没有org的层次查询 性能好）
     * 
     * @param page
     * @return
     */
    List<SecureUser> selectUsersInAllSites(@Param("params") Map<String, Object> params);
    
    /**
     * @description:根据角色过来，名字模糊查找
     * @author: 王中华
     * @createDate: 2016-8-17
     * @param params
     * @return:
     */
    List<SecureUser> selectUsersByRole(@Param("params") Map<String, Object> params);

    /**
     * 根据工号查询用户，与selectUser的不同之处在于结果包含了用户的密码
     * 
     * @param userId 工号
     * @return
     */
    SecureUser selectUserSecurityProfile(String userId);

    /**
     * 根据工号查询用户资料
     * 
     * @param userId
     * @return
     */
    SecureUser selectUser(String userId);

    /**
     * 查询指定用户组中的用户(不包含超级管理员)
     * 
     * @param groupId
     * @return
     */
    List<SecureUser> selectUsersInGivenGroup(String groupId);

    /**
     * 查询指定角色分配给了哪些用户（不包含超级管理员，包含通过用户组继承的）
     * 
     * @param siteId 如果赋值则只查询组织关系上隶属该站点的用户
     * @Param roleId查询的角色
     * @return
     */
    List<SecureUser> selectUsersOfGivenRole(@Param("roleId") String roleId, @Param("siteId") String siteId);

    /**
     * 获取指定组织机构下的有效用户（不包含超级管理员）
     * 
     * @deprecated
     * @param orgId
     * @param showAllUsers 是否显示被禁用的用户（管理员专属功能）
     * @return
     */
    @Deprecated
    List<SecureUser> selectUsersInGivenOrg(@Param("orgId") String orgId, @Param("showAllUsers") Boolean showAllUsers);

    /**
     * 获取指定组织机构下的用户（含子机构，不包含超级管理员）
     *
     * @deprecated
     * @param orgCode
     * @param showAllUsers 是否显示被禁用的用户（管理员专属功能）
     * @return
     */
    @Deprecated
    List<SecureUser> selectAllUsersInGivenOrg(@Param("orgId")String orgCode, @Param("showAllUsers") Boolean showAllUsers);

    /**
     * @deprecated
     * @param page
     * @return
     */
    @Deprecated
    List<SecureUser> selectActiveUsersPageInGivenOrg(Page<SecureUser> page);



    void updateUserPassword(@Param("username") String username, @Param("password") String password);

    String selectUserPassword(String username);

    /**
     * 更新用户密码（TIMSS1保留）
     * 
     * @param user
     */
    void updateUserPassword(SecureUser user);

    void insertUserConfig(@Param("username") String username, @Param("attribute") String attribute,
            @Param("v") String value);

    void deleteUserConfig(@Param("username") String username, @Param("attribute") String attribute);

    /**
     * 获取用户配置
     * 
     * @param username
     * @param attribute
     * @return
     */
    @MapperMethod(excuteType = Type.Interceptor_OFF)
    String selectUserConfig(@Param("username") String username, @Param("attribute") String attribute);

    Map selectDelInd(@Param("userId") String userId);

    /**
     * 根据用户和部门信息查询用户部门信息
     * 
     * @param orgCode
     * @param userId
     * @return
     */
    List<SecureOrgUser> selectOrgUser(@Param("orgCode") String orgCode, @Param("userID") String userId);

    /**
     * 查询组织机构下制定用户组或角色的拥有者，角色ID或用户ID只能二选一
     * 
     * @param roleId 角色ID
     * @param groupId 用户组ID
     * @param orgCode 可以为空
     * @param queryOrgType NULL= 只查询指定部门，U=向上递归，D=向下递归
     * @return
     */
    List<SecureUser> selectActiveUsersInOrgsWithRoleOrGroup(@Param("roleId") String roleId,
            @Param("groupId") String groupId, @Param("orgCode") String orgCode,
            @Param("queryOrgType") String queryOrgType);

    /**
     * @description:删除用户审计表中一个月之外的数据
     * @author: yuanzh
     * @createDate: 2016-3-10
     * @return:
     */
    int deleteAdtSecUserJustKeepOneMonth();

    /**
     * @description:同步数据后将更新一下只有一个站点的用户默认站点信息
     * @author: yuanzh
     * @createDate: 2016-6-21
     * @return:
     */
    void updateUserConfigSite();

    /**
     * 更新用户最后尝试登录的时间为提供的当前时间
     * 
     * @param userId
     * @param timeToUpdate 提供这个参数是为了兼容不同的数据库
     * @author YU
     * @since 2.8
     */
    void updateUserSignTime(@Param("userId") String userId, @Param("updateTime") Date timeToUpdate);

    /**
     * 设置用户登录失败次数
     * 
     * @param userId
     * @param updateSignFailedCount >0的整数
     * @author YU
     * @since 2.8
     */
    void updateSignFailedCount(@Param("userId") String userId, @Param("numberTobeSet") Integer updateSignFailedCount);
}
