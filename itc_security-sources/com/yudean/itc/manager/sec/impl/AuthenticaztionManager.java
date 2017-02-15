package com.yudean.itc.manager.sec.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yudean.itc.OrgTreeUtil;
import com.yudean.itc.code.StatusCode;
import com.yudean.itc.dao.sec.PrivilegeMapper;
import com.yudean.itc.dao.sec.RoleMapper;
import com.yudean.itc.dao.sec.SecureUserGroupMapper;
import com.yudean.itc.dao.sec.SecureUserMapper;
import com.yudean.itc.dao.sec.SiteMapper;
import com.yudean.itc.dto.sec.Organization;
import com.yudean.itc.dto.sec.Role;
import com.yudean.itc.dto.sec.SecureUser;
import com.yudean.itc.dto.sec.SecureUserGroup;
import com.yudean.itc.dto.sec.Site;
import com.yudean.itc.exception.sec.AuthenticationException;
import com.yudean.itc.ldap.ADAuthSpring;
import com.yudean.itc.manager.sec.IAuthenticationManager;
import com.yudean.itc.manager.sec.ISecurityMaintenanceManager;
import com.yudean.itc.util.MD5;

@Service
public class AuthenticaztionManager implements IAuthenticationManager {

	private static final Logger log = Logger.getLogger(AuthenticaztionManager.class);
	
	@Autowired
	private SecureUserMapper secureUserMapper;
	@Autowired
	private PrivilegeMapper privilegeMapper;
	@Autowired
	private RoleMapper roleMapper;
	@Autowired
	private ISecurityMaintenanceManager secManager;
	@Autowired
	private SecureUserGroupMapper groupMapper;
	@Autowired
	private SiteMapper siteMapper;
	@Autowired
	private ADAuthSpring adAuthSpring;

	static private Cache<String, SecureUser> userCache;
	static{
		userCache = CacheBuilder.newBuilder().expireAfterWrite(3600, TimeUnit.SECONDS).maximumSize(1000).build();
	}

	public SecureUser signIn(String userId) throws AuthenticationException {
		boolean isTempCrossSite = false;
		String tempSiteId = null;
		if(userId.contains("@")){
			isTempCrossSite = true;
			String[] sArr = userId.split("@");
			tempSiteId = sArr[1];
			userId = sArr[0];
		}
		userId = userId.trim();
		SecureUser user = secureUserMapper.selectUserSecurityProfile(userId);
		if(user == null)
			throw new AuthenticationException(userId, "账号不存在");
		if(StatusCode.NO == user.getActive())
			throw new AuthenticationException(userId, "账号已禁用");	
		
		assembleRoles(user, null);
		assembleGroups(user, null);
		
		if(user.getRoles() == null || user.getRoles().isEmpty())
			throw new AuthenticationException(userId, "用户尚未获授权，联系管理员");	
		
		//无论是跨站与否 都优先使用数据库的设置 否则使用站点的第一个（无密码还要考虑临时的跨站）
		String defSite = isTempCrossSite?tempSiteId:secureUserMapper.selectUserConfig(user.getId(), "defaultSite");
		if(defSite == null){
			Site site = user.getFirstSite();
			defSite = site.getId();
			secureUserMapper.insertUserConfig(userId, "defaultSite", defSite);
		}
		user.setCurrentSite(defSite);
		Site currSite = siteMapper.selectSingleSite(defSite);
		if(currSite == null){
			throw new AuthenticationException(userId, "默认站点已失效，联系管理员");	
		}
		user.setCurrSiteName(currSite.getName());

		assemblePrivileges(user, user.getCurrentSite());
	
		if(user.getOrganizations().size()==0){
			log.warn("用户" + userId + "在站点" + user.getCurrentSite() + "下没有任何组织机构，请联系管理员修正用户资料");
		}
		
		secureUserMapper.updateUserSignTime(userId, new Date());
		log.info("[通过无密码登陆]系统用户成功登录：" + userId);
		
		return user;
	}

	private void assembleGroups(SecureUser user,String siteId) {
		List<SecureUserGroup> groups = new ArrayList<SecureUserGroup>();
		if(user.isSuperAdmin() || siteId == null){
			groups = groupMapper.selectBelongingGroups(user.getId(), null);
		}
		else{
			groups = groupMapper.selectBelongingGroups(user.getId(), new String[]{siteId});
		}
		user.setGroups(groups);
	}

	public SecureUser signIn(String userId, String password, boolean preventPasswordGaming) throws AuthenticationException {
		userId = StringUtils.trimToEmpty(userId);
		if(null == password){
			throw new AuthenticationException(userId, "传入密码为null");
		}
		SecureUser user = secureUserMapper.selectUserSecurityProfile(userId);
		if(user == null)
			throw new AuthenticationException(userId, "账号不存在");
		
		if(StatusCode.NO == user.getActive()){
			log.info("用户账号已经禁用：" + userId);
			throw new AuthenticationException(userId, "账号已禁用，请联系管理员");	
		}
		
		boolean passwordMatch = false;		
		
		//首先验证域密码
		//TODO 域登录
		// passwordMatch = validateADAccout(userId, password);
		if(passwordMatch == false){
			//本地账户验证
			passwordMatch = password.equals(user.getPassword());
		}
		
		//域和本地验证都失败时，计数
		if(passwordMatch == false){
			
			String info = "用户密码错误：" + userId;
			log.info(info);
			//根据策略决定是否计入错误次数
			if(preventPasswordGaming)
				preventAttemptingGuessPassword(user);
			else
				throw new AuthenticationException(userId, info);	
			
		}
		return setUserInfo(user);
	}
	
	public SecureUser signIn(String userId, String password) throws AuthenticationException{
		//默认是启用防止猜密码策略的
		return signIn(userId, password, true);
	}
	
	/**
	 * 使用域帐号登录
	 * @param userId
	 * @param password
	 * @return
	 * @throws AuthenticationException
	 */
	public SecureUser signInAD(String userId, String password) throws AuthenticationException{
		
		userId = StringUtils.trimToEmpty(userId);
		if(null == password){
			throw new AuthenticationException(userId, "传入密码为null");
		}
		SecureUser user = secureUserMapper.selectUserSecurityProfile(userId);
		if(user == null)
			throw new AuthenticationException(userId, "账号不存在");
		
		if(StatusCode.NO == user.getActive()){
			log.info("用户账号已经禁用：" + userId);
			throw new AuthenticationException(userId, "账号已禁用，请联系管理员");	
		}
		
		boolean passwordMatch = adAuthSpring.authenticate(userId, password);
        if(!passwordMatch){
        	return signIn(userId, MD5.GetMD5Code(password));
        }else{
        	user.setAutType("ad");
        	return setUserInfo(user);
        }		
		
	}
	
	
	
	//Added by YU: 当天内累计失败5次，账号设置为INACTIVE
	private void preventAttemptingGuessPassword(SecureUser user) throws AuthenticationException{
		
		final int MAX_ATTEMPT_SIGN_COUNT = 5; //最大密码尝试次数

		int loginFailedCount = user.getLoginFailedCount();
		loginFailedCount++;
		Date lastAttemptLoginTime = user.getLastAttemptLoginTime();
		String userId = user.getId();		
		boolean isInSameDay = lastAttemptLoginTime == null ? false : DateUtils.isSameDay(lastAttemptLoginTime, new Date());
		
		if(isInSameDay && loginFailedCount == MAX_ATTEMPT_SIGN_COUNT){
			//禁用用户
			secureUserMapper.updateUserStatus(userId, StatusCode.NO.toString());
			secureUserMapper.updateSignFailedCount(userId, 0);
			String msg = "账号已禁用：当日密码错误次数累计已达上限";
			log.info(msg + userId);
			throw new AuthenticationException(userId, msg);					
		}else{
			if(!isInSameDay){
				//如果不在同一日，重置计数器
				loginFailedCount = 1;
			}
			secureUserMapper.updateSignFailedCount(userId, loginFailedCount);
			log.info("用户当日登录失败次数+1：" + userId);			
			secureUserMapper.updateUserSignTime(userId, new Date());			
			throw new AuthenticationException(userId, "密码错误，还可尝试" + (MAX_ATTEMPT_SIGN_COUNT - loginFailedCount) + "次");
		}
	}
	
	/**
	 * 获取用户拥有的权限(e.g. Function)
	 * @param user
	 * @param currentSiteId
	 */
	public void assemblePrivileges(SecureUser user, String currentSiteId){
		List<String> privileges = Collections.emptyList();
		if(user.isSuperAdmin()){
			//如果是超级管理员，就取出所有权限
			privileges = privilegeMapper.selectAllPrivilege();			
		}else{
			privileges = privilegeMapper.selectUserPrivilege(user.getId(), currentSiteId);
			
		}
		user.setPrivileges(privileges);
		//设置用户所属的组织机构
		List<Organization> orgAll =  secManager.selectOrgUserBelongsTo(user.getId());
		user.setOrganizations(new ArrayList<Organization>());
		if(orgAll == null || orgAll.size() == 0){
			log.error("用户" + user.getId() + "不属于任何组织（所有站点），这不符合系统设计要求");
			return;
		}		
		for(int i=0;i<orgAll.size();i++){
			Organization org = orgAll.get(i);
//			String orgCode = org.getCode();
			String orgMappingRe = OrgTreeUtil.getOrgSite(org.getCode());
//			String curSite = user.getCurrentSite();
			if(null != orgMappingRe && orgMappingRe.equals(user.getCurrentSite())){
				user.getOrganizations().add(org);
			}
		}		
	}
	
	/**
	 * 通过查询用户拥有的角色，可同时判断用户是否拥有跨站功能
	 * @param user
	 * @param currentSiteId
	 */
	private void assembleRoles(SecureUser user, String currentSiteId){
		List<Role> roles = Collections.EMPTY_LIST;		
		if(user.isSuperAdmin()){			
			//如果是超级管理员，就取出所有角色，如果未提供站点ID，则取出所有角色
			roles = roleMapper.selectAllActiveRoles(currentSiteId);
		}else{
			roles = roleMapper.selectOwnActiveRoles(user.getId(), currentSiteId);
		}
		user.setRoles(roles);
	}
	
	
	

	public SecureUser switchSite(SecureUser currentUser, String site) {
		currentUser.setCurrentSite(site);
		assembleRoles(currentUser, site);
		assembleGroups(currentUser, site);
		assemblePrivileges(currentUser, site);		
		log.info("用户 " + currentUser + " 切换站点：" + site);
		return currentUser;
	}


	@Override
	public String cachedSignIn(String userId, String password, String siteId) throws AuthenticationException {
		SecureUser user = null;
		try {
			user = signIn(userId, password, false);
		}catch(Exception ex){
			log.error("用户" + userId + "获取token失败", ex);
			throw new AuthenticationException("用户名或密码错误");
		}
		if(siteId != null && !siteId.equals(user.getCurrentSite())){
			boolean siteAuthorized = false;
			for(Site site: user.getAuthorizedSites()){
				if(site.getId().equals(siteId)){
					siteAuthorized = true;
					break;
				}
			}
			if(!siteAuthorized){
				throw new AuthenticationException("您在站点" + siteId + "下无授权");
			}
			switchSite(user, siteId);
		}
		String ret = RandomStringUtils.randomAlphanumeric(32);
		userCache.put(ret, user);
		return ret;
	}

	@Override
	public SecureUser signWithToken(String token) {
		SecureUser user = userCache.getIfPresent(token);
		if(user == null){
			return null;
		}
		return user;
	}
	
	/**
	 * 设置用户的角色、用户组、默认站点等信息
	 * @param user
	 * @return
	 * @throws AuthenticationException
	 */
	private SecureUser setUserInfo(SecureUser user) throws AuthenticationException{
		
		String userId = user.getId();
		
		assembleRoles(user, null);
		assembleGroups(user, null);
		
		if(user.getRoles() == null || user.getRoles().isEmpty())
			throw new AuthenticationException(userId, "用户尚未获授权，请联系管理员");	
		
		//无论是跨站与否 都优先使用数据库的设置 否则使用站点的第一个
		String defSite = secureUserMapper.selectUserConfig(user.getId(), "defaultSite");		
		if(defSite == null){
			Site site = user.getFirstSite();
			defSite = site.getId();
			secureUserMapper.insertUserConfig(userId, "defaultSite", defSite);
		}
		user.setCurrentSite(defSite);
		Site currSite = siteMapper.selectSingleSite(defSite);
		if(currSite == null){
			throw new AuthenticationException(userId, "默认站点已失效，请联系管理员");	
		}
		user.setCurrSiteName(currSite.getName());

		assemblePrivileges(user, user.getCurrentSite());
	
		if(user.getOrganizations().size()==0){
			log.error("用户" + userId + "在站点" + user.getCurrentSite() + "下没有任何组织机构，请联系管理员修正用户资料");
		}
		
		secureUserMapper.updateUserSignTime(userId, new Date());
		//重置登录次数，避免账号被冻结
		secureUserMapper.updateSignFailedCount(userId, 0);
		
		log.info("系统用户成功登录：" + userId);
		return user;
	}
}
