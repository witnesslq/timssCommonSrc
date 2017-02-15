package com.yudean.mvc.util;

import com.yudean.mvc.service.impl.FrameWorkServiceImpl;
import com.yudean.mvc.view.ModelAndViewAjax;
import com.yudean.mvc.view.ModelAndViewPage;
import com.yudean.mvc.view.viewResolver.JsonView;

/**
 * 返回类型处理类
 * @author kChen
 *
 */
public class ViewUtil {
	static public String AjaxFlagName = null;
	static public String AjaxDataName = null;
	static public String AjaxSucFlag = null;
	/**
	 * 用以返回AJAX请求数据对象
	 * @param obj 数据对象，限定为字典、列表等基本数据类型
	 * @return
	 */
	public static ModelAndViewAjax Json(Object obj) throws RuntimeException {
		ModelAndViewAjax mv = new ModelAndViewAjax();
		mv.setView(new JsonView());
		mv.addObject(FrameWorkServiceImpl.getRunEnvironmentData().getAjaxJsonDataflag(), obj);
		return mv;
	}
	
	/**
	 * 用以返回页面
	 * @param page 页面路径
	 * @return
	 */
	public static ModelAndViewPage Page(String page) throws RuntimeException  {
		ModelAndViewPage mv = new ModelAndViewPage();
		mv.setViewName(page);
		return mv;
	}
	
	/**
	 * 用以返回页面，并通过obj传递数据
	 * @param page 页面路径
	 * @param modelName 返回数据在request域中的名称
	 * @param obj 数据
	 * @return
	 */
	public static ModelAndViewPage Page(String page, String modelName, Object obj) throws RuntimeException  {
		ModelAndViewPage mv = new ModelAndViewPage();
		mv.setViewName(page);
		mv.addObject(modelName, obj);
		return mv;
	}
	
	/**
	 * 用以返回页面，并通过obj传递数据
	 * @param page 页面路径
	 * @param obj 数据
	 * @return
	 */
	public static ModelAndViewPage Page(String page, Object obj) throws RuntimeException  {
		ModelAndViewPage mv = new ModelAndViewPage();
		mv.setViewName(page);
		mv.addObject(obj);
		return mv;
	}
	
	
}
