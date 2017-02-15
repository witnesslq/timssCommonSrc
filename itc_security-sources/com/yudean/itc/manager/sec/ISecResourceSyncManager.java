package com.yudean.itc.manager.sec;

import java.util.List;

import com.yudean.itc.dto.sec.Organization;
import com.yudean.itc.dto.sec.Role;
import com.yudean.itc.dto.sec.SecureOrgUser;
import com.yudean.itc.dto.sec.SecureUser;
import com.yudean.itc.dto.sec.Site;

/**
 * 人事数据同步接口，站点查询
 * 
 * @company: gdyd
 * @className: ISecResourceSyncManager.java
 * @author: kChen
 * @createDate: 2014-9-29
 * @updateUser: kChen
 * @version: 1.0
 */
public interface ISecResourceSyncManager {

    /**
     * 同步站点信息
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-9-29
     * @return: > 1 同步成功
     */
    int syncSite(Site site, Organization org) throws Exception;

    /**
     * 同步部门信息
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-9-29
     * @return: > 1 同步成功
     */
    int syncOrg(Organization org) throws Exception;

    /**
     * 同步角色信息
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-9-29
     * @return: > 1 同步成功
     */
    int syncRole(Role role) throws Exception;

    /**
     * 同步人员信息
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-9-29
     * @return: > 1 同步成功
     */
    int syncUser(SecureUser secUser, Role role, Organization org) throws Exception;

    /**
     * 根据父部门信息获取子部门信息
     * 
     * @param orgCode
     * @return
     * @throws NullPointerException
     */
    List<Organization> getDeptByParents(String orgCode) throws NullPointerException;

    /**
     * 获取部门下所有人员编码
     * 
     * @param orgCode
     * @return
     * @throws NullPointerException
     */
    List<SecureOrgUser> getDeptUser(String orgCode) throws NullPointerException;

    /**
     * 移除用户组织关系
     * 
     * @param orgCode
     * @param userId
     * @return
     * @throws NullPointerException
     */
    int deleteOrgUser(String orgCode, String userId) throws NullPointerException;

    /**
     * 删除无效部门
     * 
     * @param orgCode
     * @return
     * @throws NullPointerException
     */
    int deleteOrg(String orgCode) throws NullPointerException;

    /**
     * 获取站点管理员列表
     * 
     * @param SiteId 站点，每个站点下管理员的编码都是固定的(ITC_Site_Admin)
     * @return
     * @throws NullPointerException
     */
    List<SecureUser> getSiteAdmin(String SiteId) throws NullPointerException;

    /**
     * 删除用户审计表中一个月之外的数据
     * 
     * @description:
     * @author: yuanzh
     * @createDate: 2016-3-10
     * @throws NullPointerException:
     */
    void deleteAdtSecUserJustKeepOneMonth() throws NullPointerException;

    /**
     * 同步数据后将更新一下只有一个站点的用户默认站点信息
     * 
     * @throws Exception
     */
    void updateUserConfigSite() throws NullPointerException;
}
