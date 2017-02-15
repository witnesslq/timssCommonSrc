package com.yudean.itc.dao.sec;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yudean.itc.dto.sec.SecureMenu;

public interface SecureMenuMapper {
	

	/**
	 * 获取用户有权查看的菜单
	 * @param userId
	 * @param menutype
	 * @param parentId
	 * @return
	 */
	List<SecureMenu> selectAuthorizedMenus(@Param("userId") String userId,
			@Param("category") String menutype,
			@Param("parentId") String parentId, 
			@Param("siteId") String siteId);
	
	List<SecureMenu> selectSubMenus(@Param("menuId")String menuId,@Param("category")String category,@Param("frameobj")Integer frameObj,@Param("onlyActive") Boolean onlyActive);
	
	void updateMenuStatus(@Param("ids") List<String> ids,@Param("status")Boolean status);
	
	void updateMenuName(@Param("mnuId")String menuId,@Param("mnuName")String menuName);
}
