package com.yudean.itc.facade.sec;

import java.util.List;

import com.yudean.itc.dto.sec.SecureUser;

/**
 * 重构Security的接口，与Service或Manager不同，Facade专注于向其他业务模块提供接口访问。
 * Facade中主要封装Service中的方法，也可以直接调用DAO的方法。
 * 
 * @author YU
 */
public interface ISecurityFacade {

    /**
     * 查询指定角色的用户列表，可以查询指定部门及往上或往下包含的部门<br/>
     * <B>如果用户通过用户组继承了指定角色，也会查询出来</B>
     * 
     * @param roleId 角色ID（必须）
     * @param orgCode 部门ID （可选），如果为NULL，则查询出所有拥有此角色的用户
     * @param organizationQueryType NULL= 只查询指定部门，U=向上递归，D=向下递归
     * @return 用户列表，包含SEC_USER表中的基本信息。
     */
    List<SecureUser> retriveActiveUsersWithSpecificRole(String roleId, String orgCode, String organizationQueryType);

    /**
     * 查询指定用户组的用户列表，可以查询指定部门及往上或往下包含的部门
     * 
     * @param roleId 用户组ID（必须）
     * @param orgCode 部门ID （可选），如果为NULL，则查询出所有拥有此用户组的用户
     * @param organizationQueryType NULL= 只查询指定部门，U=向上递归，D=向下递归
     * @return 用户列表，包含SEC_USER表中的基本信息。
     */
    List<SecureUser> retriveActiveUsersWithSpecificGroup(String groupId, String orgCode, String organizationQueryType);
}
