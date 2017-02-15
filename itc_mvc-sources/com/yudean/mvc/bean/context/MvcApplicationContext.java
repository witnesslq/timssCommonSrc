package com.yudean.mvc.bean.context;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class MvcApplicationContext implements ApplicationContext  {
	private ApplicationContext context;

	public void setTimssContext(ApplicationContext context) {
		this.context = context;
	}

	public ApplicationContext getTimssContext() {
		return this.context;
	}

	@Override
    public String getId() {
		// TODO Auto-generated method stub
		return context.getId();
	}

	@Override
    public String getApplicationName() {
		// TODO Auto-generated method stub
		return context.getApplicationName();
	}

	@Override
    public String getDisplayName() {
		// TODO Auto-generated method stub
		return context.getDisplayName();
	}

	@Override
    public long getStartupDate() {
		// TODO Auto-generated method stub
		return context.getStartupDate();
	}

	@Override
    public ApplicationContext getParent() {
		// TODO Auto-generated method stub
		return context.getParent();
	}

	@Override
    public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
		// TODO Auto-generated method stub
		return context.getAutowireCapableBeanFactory();
	}

	@Override
    public Environment getEnvironment() {
		// TODO Auto-generated method stub
		return context.getEnvironment();
	}

	@Override
    public boolean containsBeanDefinition(String arg0) {
		// TODO Auto-generated method stub
		return context.containsBeanDefinition(arg0);
	}

	@Override
    public <A extends Annotation> A findAnnotationOnBean(String arg0, Class<A> arg1) {
		// TODO Auto-generated method stub
		return context.findAnnotationOnBean(arg0, arg1);
	}

	@Override
    public int getBeanDefinitionCount() {
		// TODO Auto-generated method stub
		return context.getBeanDefinitionCount();
	}

	@Override
    public String[] getBeanDefinitionNames() {
		// TODO Auto-generated method stub
		return context.getBeanDefinitionNames();
	}

	@Override
    public String[] getBeanNamesForType(Class<?> arg0) {
		// TODO Auto-generated method stub
		return context.getBeanNamesForType(arg0);
	}

	@Override
    public String[] getBeanNamesForType(Class<?> arg0, boolean arg1, boolean arg2) {
		// TODO Auto-generated method stub
		return context.getBeanNamesForType(arg0, arg1, arg2);
	}

	@Override
    public <T> Map<String, T> getBeansOfType(Class<T> arg0) throws BeansException {
		// TODO Auto-generated method stub
		return context.getBeansOfType(arg0);
	}

	@Override
    public <T> Map<String, T> getBeansOfType(Class<T> arg0, boolean arg1, boolean arg2) throws BeansException {
		// TODO Auto-generated method stub
		return context.getBeansOfType(arg0, arg1, arg2);
	}

	@Override
    public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> arg0) throws BeansException {
		// TODO Auto-generated method stub
		return context.getBeansWithAnnotation(arg0);
	}

	@Override
    public boolean containsBean(String arg0) {
		// TODO Auto-generated method stub
		return context.containsBean(arg0);
	}

	@Override
    public String[] getAliases(String arg0) {
		// TODO Auto-generated method stub
		return context.getAliases(arg0);
	}

	@Override
    public Object getBean(String arg0) throws BeansException {
		// TODO Auto-generated method stub
		return context.getBean(arg0);
	}

	@Override
    public <T> T getBean(Class<T> arg0) throws BeansException {
		// TODO Auto-generated method stub
		return context.getBean(arg0);
	}

	@Override
    public <T> T getBean(String arg0, Class<T> arg1) throws BeansException {
		// TODO Auto-generated method stub
		return context.getBean(arg0, arg1);
	}

	@Override
    public Object getBean(String arg0, Object... arg1) throws BeansException {
		// TODO Auto-generated method stub
		return context.getBean(arg0, arg1);
	}

	@Override
    public Class<?> getType(String arg0) throws NoSuchBeanDefinitionException {
		// TODO Auto-generated method stub
		return context.getType(arg0);
	}

	@Override
    public boolean isPrototype(String arg0) throws NoSuchBeanDefinitionException {
		// TODO Auto-generated method stub
		return context.isPrototype(arg0);
	}

	@Override
    public boolean isSingleton(String arg0) throws NoSuchBeanDefinitionException {
		// TODO Auto-generated method stub
		return context.isSingleton(arg0);
	}

	@Override
    public boolean isTypeMatch(String arg0, Class<?> arg1) throws NoSuchBeanDefinitionException {
		// TODO Auto-generated method stub
		return context.isTypeMatch(arg0, arg1);
	}

	@Override
    public boolean containsLocalBean(String arg0) {
		// TODO Auto-generated method stub
		return context.containsLocalBean(arg0);
	}

	@Override
    public BeanFactory getParentBeanFactory() {
		// TODO Auto-generated method stub
		return context.getParentBeanFactory();
	}

	@Override
    public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
		// TODO Auto-generated method stub
		return context.getMessage(code, args, defaultMessage, locale);
	}

	@Override
    public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
		// TODO Auto-generated method stub
		return context.getMessage(code, args, locale);
	}

	@Override
    public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
		// TODO Auto-generated method stub
		return context.getMessage(resolvable, locale);
	}

	@Override
    public void publishEvent(ApplicationEvent event) {
		// TODO Auto-generated method stub
		context.publishEvent(event);
	}

	@Override
    public Resource[] getResources(String arg0) throws IOException {
		// TODO Auto-generated method stub
		return context.getResources(arg0);
	}

	@Override
    public ClassLoader getClassLoader() {
		// TODO Auto-generated method stub
		return context.getClassLoader();
	}

	@Override
    public Resource getResource(String arg0) {
		// TODO Auto-generated method stub
		return context.getResource(arg0);
	}

}
