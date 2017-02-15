package com.yudean.mvc.view;

import java.util.Map;

import org.springframework.web.servlet.View;

/**
 * 专门用来处理页面的model
 * 
 * @author kChen
 * 
 */
public class ModelAndViewPage extends ModelAndViews {
	public ModelAndViewPage() {
		super();
	}

	public ModelAndViewPage(String viewName) {
		super(viewName);
	}

	public ModelAndViewPage(View view) {
		super(view);
	}

	public ModelAndViewPage(String viewName, Map<String, ?> model) {
		super(viewName, model);
	}

	public ModelAndViewPage(View view, Map<String, ?> model) {
		super(view, model);
	}

	public ModelAndViewPage(String viewName, String modelName, Object modelObject) {
		super(viewName, modelName, modelObject);
	}

	public ModelAndViewPage(View view, String modelName, Object modelObject) {
		super(view, modelName, modelObject);
	}
}
