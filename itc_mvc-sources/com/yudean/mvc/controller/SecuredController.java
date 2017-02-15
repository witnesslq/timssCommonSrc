package com.yudean.mvc.controller;

public class SecuredController {
	public boolean hasRole(String role){
		return true;
	}
	
	public boolean hasGroup(String group){
		return true;
	}
	
	public boolean hasPrivilege(String priv){
		return true;
	}
	
	public boolean siteIs(String site){
		return true;
	}
	
	public boolean inOrg(String org){
		return true;
	}
	
	public boolean idIs(String id){
		return true;
	}
	
	public void assignPrivilege(String privs){
		
	}
}
