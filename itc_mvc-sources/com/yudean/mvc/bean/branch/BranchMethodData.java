package com.yudean.mvc.bean.branch;

import java.lang.reflect.Method;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * TIMSS内部工具传递对象类，只在处理分支版本是作为数据POJO存在
 * @author kChen
 *
 */
@Component("core_service_framework_ToolBranchMethodData")
@Scope("prototype")
public class BranchMethodData {
	/**
	 * 标记是否存在分支版本
	 */
	public boolean isHasBranch;
	
	/**
	 * 类对象包全称路径
	 */
	public String classPackagePath;
	
	/**
	 * 类对象包全称路径
	 */
	public String classBeanNaem;

	/**
	 * 类对象
	 */
	public Class<?> clazz;
	/**
	 * 被调用方法名称
	 */
	public Method method;
	/**
	 * 被调用参数类对象
	 */
	public Class<?>[] parameterTypes;
	/**
	 * 参数值
	 */
	public Object[] parameterList;
	
	public void setClassBeanNaem(String classBenaName){
		this.classBeanNaem = classBenaName;
	}
}
