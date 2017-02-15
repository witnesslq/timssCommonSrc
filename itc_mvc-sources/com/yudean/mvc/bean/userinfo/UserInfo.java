package com.yudean.mvc.bean.userinfo;

import java.util.List;

import com.yudean.itc.dto.sec.Organization;
import com.yudean.itc.dto.sec.Role;
import com.yudean.itc.dto.sec.SecureUser;

/**
 * User类接口
 * @author kChen
 *
 */
public interface UserInfo {
	/**
	 * 获取站点信息
	 * @return
	 */
	String getSiteId();
	
	/**
	 * 获取用户编号
	 * @return
	 */
	String getUserId();
	
	/**
	 * @description: 获取用户名称
	 * @author: kChen
	 * @createDate: 2014-6-19
	 * @return:用户姓名
	 */
	String getUserName();

    /**
     * 获取部门编码，如果在该站点下，用户属于多个部门，则返回第一个。
     * @description:
     * @author: kChen
     * @createDate: 2014-6-19
     * @return:
     */
	
    String getOrgId();
        
	/**
	 * 获取部门名称，如果在该站点下，用户属于多个部门，则返回第一个。
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-6-19
	 * @return:
	 */
	String getOrgName();
	
	/**
	 * 返回部门列表
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-6-19
	 * @return:
	 */
	List<Organization> getOrgs();
	
	/**
	 * 获取角色ID，如果有多个角色则返回第一个
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-6-19
	 * @return:
	 */
	String getRoleId();
	
	/**
	 * 获取角色名称，如果有抖个角色则发挥第一个
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-6-19
	 * @return:
	 */
	String getRoleName();
	
	/**
	 * 获取角色列表
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-6-19
	 * @return:
	 */
	List<Role> getRoles();
	
	/**
	 * 获取ITC组件包中的SecureUser对象
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-6-20
	 * @return:
	 */
	SecureUser getSecureUser();
}
