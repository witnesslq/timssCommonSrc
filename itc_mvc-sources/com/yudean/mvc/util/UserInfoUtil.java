package com.yudean.mvc.util;

import java.util.ArrayList;
import java.util.List;

import com.yudean.itc.dto.sec.SecureUser;
import com.yudean.itc.util.ClassCastUtil;
import com.yudean.mvc.bean.userinfo.UserInfo;
import com.yudean.mvc.bean.userinfo.impl.UserInfoImpl;

/**
 * UserInfo相关工具类
 * 
 * @author kchen
 * 
 */
public class UserInfoUtil {
	/**
	 * 类型转换，将secureUsers批量转换为UserInfo
	 * 
	 * @param secureUsers
	 * @return
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 */
	public static List<UserInfo> castUserInfo(SecureUser... secureUsers) throws SecurityException, IllegalArgumentException, InstantiationException, IllegalAccessException,
			NoSuchFieldException {
		List<UserInfo> impls = null;
		if (null != secureUsers) {
			impls = new ArrayList<UserInfo>();
			for (int index = 0; index < secureUsers.length; index++) {
				impls.add(ClassCastUtil.castParent2Child(UserInfoImpl.class, secureUsers[index]));
			}
		}
		return impls;
	}
}
