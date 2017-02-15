package com.yudean.homepage.service.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.yudean.homepage.bean.DeleteDraftParam;
import com.yudean.homepage.service.HomepageAnnotService;
import com.yudean.homepage.service.HomepageNotifyService;
import com.yudean.itc.annotation.HopAnnotation;
import com.yudean.itc.bean.AnnotMethod;
import com.yudean.itc.dto.support.AppModule;
import com.yudean.itc.interfaces.AnnotationConfigInterface;
import com.yudean.itc.manager.support.IAnnotationConfigManager;
import com.yudean.itc.manager.support.IModuleManager;

/**
 * 首页注解服务器类
 * 
 * @company: gdyd
 * @className: IHomepageAnnotService.java
 * @author: kChen
 * @createDate: 2014-11-13
 * @updateUser: kChen
 * @version: 1.0
 */
@Service
@Lazy(false)
public class IHomepageAnnotService implements HomepageAnnotService, AnnotationConfigInterface {
	private static final Logger LOG = Logger.getLogger(IHomepageAnnotService.class);

	@Autowired
	private IAnnotationConfigManager annotationConfigManager;

	@Autowired
	IModuleManager moduleManager;

	@Autowired
	HomepageNotifyService homepageNotifyService;

	private List<ModuleAnnot> moduleAnnotList;

	/**
	 * 删除草稿
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-11-13
	 * @param deleteParam
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 *             :
	 */
	public void deleteDraftNotify(DeleteDraftParam deleteParam) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		AnnotMethod annotMethod = compAnnotMethod(deleteParam.getFlowId());
		if (null != annotMethod) {
			Method method = annotMethod.getMethod();
			Object obj = annotMethod.getInstace();
			HopAnnotation hopAnnot = annotMethod.getAnnot(HopAnnotation.class);
			if (hopAnnot.Sync()) {
				homepageNotifyService.notifySync(method, obj, deleteParam);
			} else {
				homepageNotifyService.notifyAsync(method, obj, deleteParam);
			}
		} else {
			LOG.warn("草稿" + deleteParam.getName() + "。流水号：" + deleteParam.getFlowId() + "。由于流水号为空或模块数据不存在，没有删除");
		}
	}

	/**
	 * 从列表获取注解接口方法
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-11-13
	 * @param flowId
	 * @return:
	 */
	private AnnotMethod compAnnotMethod(String flowId) {
		if (null != flowId) {
			for (ModuleAnnot moduleAnnot : moduleAnnotList) {
				if (flowId.startsWith(moduleAnnot.proSufix)) {
					return moduleAnnot.annotMethod;
				}
			}
		}
		return null;
	}

	@Override
	@PostConstruct
	// 在使用 applicationContext.getBean 时，数据还未被注入 ？
	public void initAnnotationConfigInter() {// 初始化注解
		initAnnotationConfig();
	}

	private void initAnnotationConfig() {
		if (null != annotationConfigManager) {
			List<AnnotMethod> annotMethodList = annotationConfigManager.getAnnotationMethod();

			Map<String, AnnotMethod> annotMethodMap = new HashMap<String, AnnotMethod>();
			LOG.debug("扫描首页模块注解");
			for (AnnotMethod annotMethod : annotMethodList) {
				HopAnnotation hopAnnot = annotMethod.getAnnot(HopAnnotation.class);
				if (null != hopAnnot) {
					annotMethodMap.put(hopAnnot.value(), annotMethod);
				}
			}
			try {
				List<AppModule> moduleList = moduleManager.retrieveAllModule();
				moduleAnnotList = new ArrayList<ModuleAnnot>();
				for (AppModule appModule : moduleList) {
					AnnotMethod annotMethod = annotMethodMap.get(appModule.getCode());
					if (null != annotMethod) {
						moduleAnnotList.add(new ModuleAnnot(appModule.getFlowPrefix(), annotMethod));
					}
				}
			} catch (Exception e) {
				LOG.error("初始化模块流程列表异常", e);
			}
			LOG.debug("扫描首页模块注解结束");
		}
	}

	/**
	 * 注解存储数据对象
	 * 
	 * @company: gdyd
	 * @className: IHomepageAnnotService.java
	 * @author: kChen
	 * @createDate: 2014-11-13
	 * @updateUser: kChen
	 * @version: 1.0
	 */
	class ModuleAnnot {
		String proSufix;
		AnnotMethod annotMethod;

		ModuleAnnot(String proSufix, AnnotMethod annotMethod) {
			this.proSufix = proSufix;
			this.annotMethod = annotMethod;
		}
	}
}
