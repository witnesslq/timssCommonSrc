package com.yudean.itc.dao.sec;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yudean.itc.dto.sec.Privilege;

public interface PrivilegeMapper {
	
	
	List<String> selectUserPrivilege(@Param("userId")String userId, 
									@Param("siteId")String siteId);
	
	List<String> selectAllPrivilege();
	
	List<Privilege> selectRolePrivileges(String roleId);

	List<Map> selectUserPrivilegeByTree(@Param("userId")String userId,
										@Param("siteId")String siteId);
	
}
