package com.yudean.mvc.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * timss类反射工具
 * 
 * @author kChen
 * 
 */
public class ReflectUtil {

	static private enum modeType {//匹配类型
		Absolute/*绝对匹配*/, prif/*数量匹配*/
	}
	
	/**
	 * 通过传递参数、名称、对象类获取对应方法
	 * @param methodName
	 * @param args
	 * @param targetClass
	 * @return
	 * @throws NoSuchMethodException
	 */
	static public Method getReflectMethod(final String methodName, Object[] args, Class<?> targetClass) throws NoSuchMethodException{
		 Class<?>[] argsTypes = getReflectMethodType(methodName, args, targetClass);
		 return targetClass.getMethod(methodName, argsTypes);
	}
	
	/**
	 * 匹配方法参数，如果对象中之后一个指定名称方法，则立即返回这个方法的参数
	 * @param methodName
	 * @param args
	 * @param targetClass
	 * @return
	 * @throws NoSuchMethodException
	 */
	static public Class<?>[] getReflectMethodType(final String methodName, Object[] args, Class<?> targetClass) throws NoSuchMethodException {
		return getReflectMethodTypePri(methodName, args, targetClass, modeType.prif);
	}
	
	/**
	 * 匹配方法参数,绝对匹配,即使在对象中只找到一个实现方法，也要对传递参数进行一次匹配
	 * 
	 * @param methodName
	 * @param args
	 * @param targetClass
	 * @return
	 * @throws NoSuchMethodException
	 */
	static public Class<?>[] getReflectMethodTypeAbsolute(final String methodName, Object[] args, Class<?> targetClass) throws NoSuchMethodException {
		return getReflectMethodTypePri(methodName, args, targetClass, modeType.Absolute);
	}

	/**
	 * 匹配传递参数类别
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-6-26
	 * @param methodName
	 * @param args
	 * @param targetClass
	 * @param mode
	 * @return
	 * @throws NoSuchMethodException:
	 */
	static private Class<?>[] getReflectMethodTypePri(final String methodName, Object[] args, Class<?> targetClass,modeType mode) throws NoSuchMethodException {
		Class<?>[] methodClassType = null;
		List<Method> list = getEqualNameMethod(methodName, targetClass);
		if(null == list || 0 == list.size()){
			throw new NoSuchMethodException();//如果没有找到指定的方法，则抛出异常
		}
		if(1 == list.size() && modeType.prif == mode){
			return list.get(0).getParameterTypes();
		}
		final int argsLen = args.length;// 传递参数长度
		if (null != list && 0 < list.size()) {
			for (int index = 0; index < list.size(); index++) {
				Method method = list.get(index);
				boolean isCheckRight = false;
				String _name = method.getName();
				if (methodName == _name) {// 匹配名称
					Class<?>[] _methodTypes = method.getParameterTypes();// 获取参数类别
					try {
						isCheckRight = true;
						for (int indx = 0; argsLen > indx; indx++) {
							Object argsOne = args[indx];
							if(null != argsOne){
								Class<?> paramClazz = parseMethodType(args[indx].getClass());//
								Class<?> cls = parseMethodType(_methodTypes[indx]);
								if (!cls.isAssignableFrom(paramClazz)) {// 判断接口继承、类继承、抽象继承或自身对象
									isCheckRight = false;
									break;
								}
							}
						}
						if (isCheckRight) {
							methodClassType = _methodTypes;// 确定参数类型
							break;
						}
					} catch (Exception e) {
						isCheckRight = false;
					}
				}
			}
		}
		return methodClassType;
	}
	
	
	/**
	 * 从指定类中获取指定名称的方法列表
	 * 
	 * @param methodName
	 * @param targetClass
	 * @return
	 */
	static private List<Method> getEqualNameMethod(final String methodName, Class<?> targetClass) {
		final Method[] methods = targetClass.getMethods();
		final int length = methods.length;
		List<Method> methodList = new ArrayList<Method>();
		for (int index = 0; index < length; index++) {
			Method method = methods[index];
			if (methodName == method.getName()) {
				methodList.add(method);
			}
		}
		return methodList;
	}
	
	/**
	 * 将传入的参数cast成输出的参数
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-6-26
	 * @param paramType
	 * @return:
	 */
	static Class<?> parseMethodType(Class<?> paramType) throws Exception{
		Class<?> retParamType = paramType;
		if(null != paramType){
			if(paramType.isAssignableFrom(int.class)){//当类型匹配为int，转换为Integer
				retParamType = Integer.class;
			}else if(paramType.isAssignableFrom(boolean.class)){//当类型匹配为boolean，转换为boolean
				retParamType = Boolean.class;
			}else if(paramType.isAssignableFrom(double.class)){
				retParamType = Double.class;
			}else if(paramType.isAssignableFrom(long.class)){
				retParamType = Long.class;
			}else if(paramType.isAssignableFrom(float.class)){
				retParamType = Float.class;
			}else if(paramType.isAssignableFrom(byte.class)){
				retParamType = Byte.class;
			}else if(paramType.isAssignableFrom(short.class)){
				retParamType = Short.class;
			}
		}
		return retParamType;
	}
}
