package com.yudean.itc.dao.sec;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yudean.itc.annotation.Secured;
import com.yudean.itc.dto.sec.Organization;

public interface OrganizationMapper {

    Organization selectOrgByID(String orgCode);

    /**
     * 获取指定组织机构的下一级机构
     * 
     * @param parentCode 如果为NULL，则返回一级组织机构
     * @param siteId 如果为NULL，则不考虑站点
     * @return
     */
    @Secured(functionId = "F-ORG-VIEW")
    List<Organization> selectOrgByParentID(@Param("parentOrgCode") String parentCode, @Param("FILTER") String[] filter);

    /**
     * 获取所有的组织节点 用于在内存中构建组织机构树
     * 
     * @return
     */
    List<Organization> selectAllOrgs();

    /**
     * 查询用户所属的组织
     * 
     * @param userId
     * @return
     */
    List<Organization> selectOrgUserBelongsTo(String userId);

    /**
     * 添加一条部门信息
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-9-30
     * @param org
     * @return:
     */
    int insertOrg(Organization org);

    /**
     * 更新部门信息
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-10-8
     * @param org
     * @return:
     */
    int updateOrg(Organization org);

    /**
     * 物理删除部门信息
     * 
     * @param orgCode
     * @return
     */
    int deleteOrgEx(@Param("orgCode") String orgCode);

    /**
     * 查询指定的榕湖组织关系是否存在
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-10-8
     * @param orgCode
     * @param userId
     * @return:
     */
    Integer selectOrgUserMap(@Param("orgCode") String orgCode, @Param("userID") String userId);

    /**
     * 增加组织与用户的联系
     * 
     * @param orgCode
     * @param userId
     */
    void insertOrgUserMap(@Param("orgCode") String orgCode, @Param("userID") String userId);

    /**
     * 新增添加组织用户信息新增方法
     * 
     * @param orgCode
     * @param userId
     * @param syncid 外部系统同步标志
     */
    int insertOrgUserMapEx(@Param("orgCode") String orgCode, @Param("userID") String userId,
            @Param("syncID") String syncid);

    /**
     * 更新组织用户信息
     * 
     * @param orgCode
     * @param userId
     * @param syncid
     * @return
     */
    // int updateOrgUserMapEx(@Param("orgCode") String orgCode, @Param("userID")
    // String userId, @Param("syncID") String syncid);

    /**
     * 解除组织与用户的联系
     * 
     * @param orgCode
     * @param userId
     */
    void deleteOrgUserMap(@Param("orgCode") String orgCode, @Param("userID") String userId);

    /**
     * 解除指定用户的所有组织关系
     * 
     * @param userId
     */
    void deleteAllOrgMapping(String userId);

    /**
     * 解除指定用户在指定站点下的所有组织关系
     * 
     * @param userId
     * @param siteId
     */
    void deleteOrgMappingInGivenSite(@Param("userId") String userId, @Param("siteId") String siteId);

    /**
     * 查找和角色相关的组织（第一层节点）
     * 
     * @param uids
     * @return
     */
    @SuppressWarnings("rawtypes")
    List<Map> selectOrgsRelatedToRole(String role);

    /**
     * 根据父节点查找组织
     * 
     * @param oids
     * @return
     */
    List<Organization> selectOrgsByParentIds(List<String> item);

    /**
     * 查找组织的一层父节点
     * 
     * @param oids
     * @return
     */
    @SuppressWarnings("rawtypes")
    List<Map> selectOrgsParents(List<String> item);

    /**
     * 查找跟用户组相关的组织（第一层节点）
     * 
     * @param group
     * @return
     */
    @SuppressWarnings("rawtypes")
    List<Map> selectOrgsRelatedToGroup(String group);

    /**
     * 查找所有组织-站点映射关系
     * 
     * @return
     */
    @SuppressWarnings("rawtypes")
    List<Map> selectAllOrgSiteMapping();

    /**
     * 查找和用户相关的组织（包括父节点，选择角色/用户组中用户树过滤需要）
     * 
     * @param uids
     * @return
     */
    @SuppressWarnings("rawtypes")
    List<Map> selectOrgsRelatedToUsers(String[] uids);

    List<Organization> selectOrgBySiteId(@Param("siteId") String siteId);
}
