package com.yudean.mvc.bean.logstash;

public class Logstash {
	String module;	//模块（必填）
	String methodName;	//方法（必填）
	String userId;	//用户ID
	String siteId;	//站点ID
	Long diffTime;	//用时
	Long diffMemory;	//内存差
	Integer intval1;	//整型参数1
	Integer intval2;	//整型参数2
	Integer intval3;	//整型参数3
	Float floatval1;	//浮点型参数1
	Float floatval2;	//浮点型参数2
	Float floatval3;	//浮点型参数3
	String wordval1;	//字符串型参数1
	String wordval2;	//字符串型参数2
	String userDefineInfo;	//用户自定义文本信息
	String methodArgs;	//方法参数
	
	/**
	 * @return the module
	 */
	public String getModule() {
		return module;
	}
	/**
	 * @param module the module to set
	 */
	public void setModule(String module) {
		this.module = module;
	}

	/**
	 * @return the methodName
	 */
	public String getMethodName() {
		return methodName;
	}
	/**
	 * @param methodName the methodName to set
	 */
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	/**
	 * @return the methodArgs
	 */
	public String getMethodArgs() {
		return methodArgs;
	}
	/**
	 * @param methodArgs the methodArgs to set
	 */
	public void setMethodArgs(String methodArgs) {
		this.methodArgs = methodArgs;
	}
	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	/**
	 * @return the diffTime
	 */
	public Long getDiffTime() {
		return diffTime;
	}
	/**
	 * @param diffTime the diffTime to set
	 */
	public void setDiffTime(Long diffTime) {
		this.diffTime = diffTime;
	}
	/**
	 * @return the userDefineInfo
	 */
	public String getUserDefineInfo() {
		return userDefineInfo;
	}
	/**
	 * @param userDefineInfo the userDefineInfo to set
	 */
	public void setUserDefineInfo(String userDefineInfo) {
		this.userDefineInfo = userDefineInfo;
	}
	/**
	 * @return the intval1
	 */
	public Integer getIntval1() {
		return intval1;
	}
	/**
	 * @param intval1 the intval1 to set
	 */
	public void setIntval1(Integer intval1) {
		this.intval1 = intval1;
	}
	/**
	 * @return the intval2
	 */
	public Integer getIntval2() {
		return intval2;
	}
	/**
	 * @param intval2 the intval2 to set
	 */
	public void setIntval2(Integer intval2) {
		this.intval2 = intval2;
	}
	/**
	 * @return the intval3
	 */
	public Integer getIntval3() {
		return intval3;
	}
	/**
	 * @param intval3 the intval3 to set
	 */
	public void setIntval3(Integer intval3) {
		this.intval3 = intval3;
	}
	/**
	 * @return the floatval1
	 */
	public Float getFloatval1() {
		return floatval1;
	}
	/**
	 * @param floatval1 the floatval1 to set
	 */
	public void setFloatval1(Float floatval1) {
		this.floatval1 = floatval1;
	}
	/**
	 * @return the floatval2
	 */
	public Float getFloatval2() {
		return floatval2;
	}
	/**
	 * @param floatval2 the floatval2 to set
	 */
	public void setFloatval2(Float floatval2) {
		this.floatval2 = floatval2;
	}
	/**
	 * @return the floatval3
	 */
	public Float getFloatval3() {
		return floatval3;
	}
	/**
	 * @param floatval3 the floatval3 to set
	 */
	public void setFloatval3(Float floatval3) {
		this.floatval3 = floatval3;
	}
	/**
	 * @return the wordval1
	 */
	public String getWordval1() {
		return wordval1;
	}
	/**
	 * @param wordval1 the wordval1 to set
	 */
	public void setWordval1(String wordval1) {
		this.wordval1 = wordval1;
	}
	/**
	 * @return the wordval2
	 */
	public String getWordval2() {
		return wordval2;
	}
	/**
	 * @param wordval2 the wordval2 to set
	 */
	public void setWordval2(String wordval2) {
		this.wordval2 = wordval2;
	}
	/**
	 * @return the siteId
	 */
	public String getSiteId() {
		return siteId;
	}
	/**
	 * @param siteId the siteId to set
	 */
	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	public Long getDiffMemory() {
		return diffMemory;
	}

	public void setDiffMemory(Long diffMemory) {
		this.diffMemory = diffMemory;
	}
}
