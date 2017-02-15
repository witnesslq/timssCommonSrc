package com.yudean.mvc.service.impl;

import java.lang.reflect.Method;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.yudean.itc.annotation.MethodCacheWeb;
import com.yudean.itc.exception.support.datacachemanager.DataCacheProviderException;
import com.yudean.itc.manager.support.IDataCacheManager;
import com.yudean.mvc.bean.userinfo.UserInfoScope;
import com.yudean.mvc.handler.ThreadLocalHandler;

@Service
public class CacheMethodAnnotationService {
	private static final Logger LOG = Logger.getLogger(CacheMethodAnnotationService.class);

	@Autowired
	IDataCacheManager dataCacheManager;

	public Object doCache(Method method, ProceedingJoinPoint pjp) throws Throwable {
		Object methodRet = null;
		boolean isCacheError = false;
		CacheGetRetData<?> retGetData = null;
		UserInfoScope scopeData = ThreadLocalHandler.getVariable().getUserInfoScope();
		String oper = null;
		if(null != scopeData){
			oper = scopeData.getUserId();
		}
		MethodCacheWeb annotation = method.getAnnotation(MethodCacheWeb.class);
		if (null != annotation) {
			try {
				retGetData = getCache(method, annotation, oper, method.getReturnType());
				if (retGetData.isHasCache) {
					methodRet = retGetData.cacheData;
				}
			} catch (Exception e) {
				isCacheError = true;
				LOG.error("执行缓存方法异常", e);
			}
			if (null == methodRet || isCacheError) {
				methodRet = pjp.proceed();
			}
			if (!retGetData.isHasCache && !isCacheError) {
				try {// 添加缓存值
					int ret = modifyCache(method, methodRet, annotation, oper, retGetData.extType);
					if (ret < 1) {
						LOG.warn("添加缓存数据失败");
					}
				} catch (DataCacheProviderException e) {
					LOG.error("执行缓存方法异常", e);
				}
			}
		} else {
			methodRet = pjp.proceed();
		}
		return methodRet;
	}

	private <T> CacheGetRetData<T> getCache(Method method, MethodCacheWeb annotation, String oper, Class<T> retClassType) throws Exception {
		CacheGetRetData<T> retData = new CacheGetRetData<T>();
		switch (annotation.type()) {
		case Application:
		case Permanent: {
			retData.extType = "NaN";
			break;
		}
		case Session: {
			HttpSession session = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
			retData.extType = session.getId();
			break;
		}
		case User: {
			retData.extType = oper;
			break;
		}
		default: {
			retData.extType = "NaN";
			break;
		}
		}
		T retDate = dataCacheManager.getCache(method, annotation.type(), oper, retData.extType);
		if (null != retDate) {
			retData.cacheData = retDate;
			retData.isHasCache = true;
		}
		return retData;
	}

	private int modifyCache(Method method, Object obj, MethodCacheWeb annotation, String oper, String extType) throws DataCacheProviderException {
		return dataCacheManager.modifyCache(method, obj, annotation.type(), oper, extType);
	}
}

class CacheGetRetData<E> {
	E cacheData;
	String extType;
	boolean isHasCache;

	CacheGetRetData() {
		isHasCache = false;
	}
}
