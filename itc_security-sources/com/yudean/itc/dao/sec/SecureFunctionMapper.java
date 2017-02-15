package com.yudean.itc.dao.sec;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yudean.itc.dto.sec.SecureFunction;
/**
 * Function的Dao类
 * @author henrydeng
 *
 */
public interface SecureFunctionMapper {
	
	List<SecureFunction> selectSubFunctionsOfGivenMenu(String menuId);
	
	List<SecureFunction> selectSubFunctions(String functionId);
	
	void updateFunctionStatus(@Param("ids") List<String> ids,@Param("status")Boolean status);

	/**
	 * 获取导出到外部系统的权限
	 * 主要是考虑完整权限列表在电厂带宽较小时比较占资源
	 * @param siteId
	 * @return
     */
	List<String> getExportedFunctions(@Param("siteId") String siteId);
}
