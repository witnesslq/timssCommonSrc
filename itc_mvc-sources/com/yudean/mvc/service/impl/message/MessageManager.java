package com.yudean.mvc.service.impl.message;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import com.yudean.itc.bean.MessageUtility;
import com.yudean.itc.util.Pair2;

public interface MessageManager {

	void doP2pAsyn(Object instance, Method method, MessageUtility msg) throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException;

	Object doP2pSync(Object instance, Method method, MessageUtility msg) throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException;

	void doBrodcast(List<Pair2<Object, Method>> methodList, MessageUtility msg);
}
