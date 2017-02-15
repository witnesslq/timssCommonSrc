package com.yudean.mvc.service.impl.message.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.yudean.mvc.service.impl.message.MessageManager;
import com.yudean.itc.bean.MessageUtility;
import com.yudean.itc.util.Pair2;

@Service
public class MessageManagerImpl implements MessageManager {
	private static final Logger log = Logger.getLogger(MessageManagerImpl.class);

	@Override
	@Async
	public void doP2pAsyn(Object instance, Method method, MessageUtility msg) throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		doMethod(instance, method, msg);
	}

	@Override
	public Object doP2pSync(Object instance, Method method, MessageUtility msg) throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		return doMethod(instance, method, msg);
	}

	@Override
	@Async
	public void doBrodcast(List<Pair2<Object, Method>> methodList, MessageUtility msg) {
		for (Pair2<Object, Method> pair : methodList) {
			try {
				doMethod(pair.first, pair.second, msg);
			} catch (IllegalArgumentException e) {
				log.error("执行方法:" + pair.second + "时实例化类:" + pair.first + "异常", e);
			} catch (IllegalAccessException e) {
				log.error("执行方法:" + pair.second + "时实例化类" + pair.first + "异常", e);
			} catch (InvocationTargetException e) {
				log.error("执行方法" + pair.second + "时实例化类" + pair.first + "异常", e);
			}
		}
	}

	/**
	 * 反射实现被调用的方法
	 */
	private Object doMethod(Object instance, Method method, MessageUtility msg) throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		// 获取方法列表
		Class<?>[] methodParams = method.getParameterTypes();
		final int length = methodParams.length;
		// 实例化一个被传递参数的类表，
		// 并向第一个类型为MessageUtility的参赛注入msg实例
		Object[] objs = new Object[length];
		for (int i = 0; i < length; i++) {
			Class<?> methodParam = methodParams[i];
			objs[i] = methodParam.isAssignableFrom(MessageUtility.class) ? msg : null;
		}
		// 执行被实例化的方法
		return method.invoke(instance, objs);
	}
}
