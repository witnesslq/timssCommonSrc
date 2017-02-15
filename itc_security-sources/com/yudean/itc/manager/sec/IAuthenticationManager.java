package com.yudean.itc.manager.sec;

import com.yudean.itc.dto.sec.SecureUser;
import com.yudean.itc.exception.sec.AuthenticationException;

/**
 * 提供访问者身份验证的服务类
 * @author yushujiang
 *
 */
public interface IAuthenticationManager {

	/**
	 * 通过工号获取授权用户资料，仅对从统一平台下载的用户生效，
	 * 如果是系统自己创建的用户，必须提供用户名密码进行校验<br/>
	 * 对于跨站点用户，此处不会组装Privilege，调用者必须在返回的SecureUser中获取其站点信息，然后调用switchSite方法登录一个站点<br/>
	 * 对于单站点用户，系统会组装Privilege以及站点信息，无需其他操作。
	 * 
	 * @param userId 工号
	 * @return 授权用户资料
	 * @throws AuthenticationException 用户不存在
	 */
	SecureUser signIn(String userId) throws AuthenticationException;
	
	/**
	 * 通过工号和登录密码获取授权用户资料<br/>
	 * 对于跨站点用户，此处不会组装Privilege，调用者必须在返回的SecureUser中获取其站点信息，然后调用switchSite方法登录一个站点<br/>
	 * 对于单站点用户，系统会组装Privilege以及站点信息，无需其他操作。<br/>
	 * 密码错误次数过多会被锁账号
	 * @param userId 工号
	 * @param password 密码
	 * @return
	 * @throws AuthenticationException 用户不存在/密码错误/账户已冻结
	 */
	SecureUser signIn(String userId, String password) throws AuthenticationException;
	
	/**
	 * 切换或登录一个指定的站点，用户信息中的角色、权限和用户组信息会相应更新
	 * @param currentUser
	 * @param site 将登录的站点ID
	 * @return
	 */
	SecureUser switchSite(SecureUser currentUser, String site);
	
	/**
	 * 组装权限 暴露出的API
	 * @param user
	 * @param currentSiteId
	 */
	void assemblePrivileges(SecureUser user, String currentSiteId);
	
	/**
	 * 通过工号和登录密码获取授权用户资料<br/>
	 * 内核和signIn(userId, password)一样，因为EIP的单点登录策略不完备，用户忘记修改密码后会导致其定时任务用错误的信息来登录取待办，造成用户被锁定。该API用于避免此问题。
	 * @param userId
	 * @param password
	 * @param preventPasswordGaming 是否启用阻止密码尝试的策略
	 * @return
	 * @throws AuthenticationException
	 */
	SecureUser signIn(String userId, String password, boolean preventPasswordGaming) throws AuthenticationException;

	String cachedSignIn(String userId, String password, String siteId) throws AuthenticationException;

	SecureUser signWithToken(String token);
	
	/**
	 * 使用域帐号登录
	 * 域帐号登录失败后，会转向使用系统帐号登录signIn(String userId, String password) 
	 * @param userId
	 * @param password
	 * @return
	 * @throws AuthenticationException
	 */
	SecureUser signInAD(String userId, String password) throws AuthenticationException;
}
