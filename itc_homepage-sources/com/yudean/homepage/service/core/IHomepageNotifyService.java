package com.yudean.homepage.service.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.yudean.homepage.service.HomepageNotifyService;

@Service
public class IHomepageNotifyService implements HomepageNotifyService {

	@Override
	public void notifySync(Method method, Object obj, Object... params) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		method.invoke(obj, params);
	}

	@Override
	@Async
	public void notifyAsync(Method method, Object obj, Object... params) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		method.invoke(obj, params);
	}
}
