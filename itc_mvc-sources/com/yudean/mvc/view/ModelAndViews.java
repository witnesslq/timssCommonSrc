package com.yudean.mvc.view;

import java.util.Map;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

/**
 * TIMSSview层处理对象
 * 
 * @author kChen
 * 
 */
public abstract class ModelAndViews extends ModelAndView {
	/**
	 * Default constructor for bean-style usage: populating bean properties
	 * instead of passing in constructor arguments.
	 * 
	 * @see #setView(View)
	 * @see #setViewName(String)
	 */
	public ModelAndViews() {
		super();
	}

	/**
	 * Convenient constructor when there is no model data to expose. Can also be
	 * used in conjunction with {@code addObject}.
	 * 
	 * @param viewName
	 *            name of the View to render, to be resolved by the
	 *            DispatcherServlet's ViewResolver
	 * @see #addObject
	 */
	public ModelAndViews(String viewName) {
		super(viewName);
	}

	/**
	 * Convenient constructor when there is no model data to expose. Can also be
	 * used in conjunction with {@code addObject}.
	 * 
	 * @param view
	 *            View object to render
	 * @see #addObject
	 */
	public ModelAndViews(View view) {
		super(view);
	}

	/**
	 * Creates new ModelAndView given a view name and a model.
	 * 
	 * @param viewName
	 *            name of the View to render, to be resolved by the
	 *            DispatcherServlet's ViewResolver
	 * @param model
	 *            Map of model names (Strings) to model objects (Objects). Model
	 *            entries may not be {@code null}, but the model Map may be
	 *            {@code null} if there is no model data.
	 */
	public ModelAndViews(String viewName, Map<String, ?> model) {
		super(viewName, model);
	}

	/**
	 * Creates new ModelAndView given a View object and a model. <emphasis>Note:
	 * the supplied model data is copied into the internal storage of this
	 * class. You should not consider to modify the supplied Map after supplying
	 * it to this class</emphasis>
	 * 
	 * @param view
	 *            View object to render
	 * @param model
	 *            Map of model names (Strings) to model objects (Objects). Model
	 *            entries may not be {@code null}, but the model Map may be
	 *            {@code null} if there is no model data.
	 */
	public ModelAndViews(View view, Map<String, ?> model) {
		super(view, model);
	}

	/**
	 * Convenient constructor to take a single model object.
	 * 
	 * @param viewName
	 *            name of the View to render, to be resolved by the
	 *            DispatcherServlet's ViewResolver
	 * @param modelName
	 *            name of the single entry in the model
	 * @param modelObject
	 *            the single model object
	 */
	public ModelAndViews(String viewName, String modelName, Object modelObject) {
		super(viewName, modelName, modelObject);
	}

	/**
	 * Convenient constructor to take a single model object.
	 * 
	 * @param view
	 *            View object to render
	 * @param modelName
	 *            name of the single entry in the model
	 * @param modelObject
	 *            the single model object
	 */
	public ModelAndViews(View view, String modelName, Object modelObject) {
		super(view, modelName, modelObject);
	}
}
