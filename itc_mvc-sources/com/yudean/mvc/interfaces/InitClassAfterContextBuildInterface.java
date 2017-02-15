package com.yudean.mvc.interfaces;


import org.springframework.context.ApplicationContext;

public interface InitClassAfterContextBuildInterface {
	void initClass(ApplicationContext context) throws Exception;
}
