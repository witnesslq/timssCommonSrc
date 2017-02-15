package com.yudean.mvc.view;

import java.util.Map;

import org.springframework.web.servlet.View;


/**
 * 专门用来处理AJAX异步数据的model
 * @author kChen
 *
 */
public class ModelAndViewAjax extends ModelAndViews {
	public ModelAndViewAjax() {
		super();
	}

	public ModelAndViewAjax(String viewName) {
		super(viewName);
	}

	public ModelAndViewAjax(View view) {
		super(view);
	}

	public ModelAndViewAjax(String viewName, Map<String, ?> model) {
		super(viewName, model);
	}

	public ModelAndViewAjax(View view, Map<String, ?> model) {
		super(view, model);
	}

	public ModelAndViewAjax(String viewName, String modelName, Object modelObject) {
		super(viewName, modelName, modelObject);
	}

	public ModelAndViewAjax(View view, String modelName, Object modelObject) {
		super(view, modelName, modelObject);
	}
}
