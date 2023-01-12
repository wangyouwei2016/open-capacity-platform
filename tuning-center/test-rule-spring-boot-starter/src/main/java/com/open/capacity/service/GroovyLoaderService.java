package com.open.capacity.service;

import com.open.capacity.rule.EasyRulesTemplate;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Component;

import groovy.lang.GroovyClassLoader;

@Component
public class GroovyLoaderService implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	private static final String UTF_8 = "UTF-8";

	private static final GroovyClassLoader groovyClassLoader;

	
	@Autowired(required=false)
    private  EasyRulesTemplate easyRulesTemplate;
	
	
	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	static {
		CompilerConfiguration config = new CompilerConfiguration();
		config.setSourceEncoding(UTF_8);
		groovyClassLoader = new GroovyClassLoader(Thread.currentThread().getContextClassLoader(), config);

	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public Object getBean(String beanName, String javaScript) {

		Object bean = this.getInnerBean(beanName);

		if (bean != null) {
			return bean;
		}

		Class clazz = comple(beanName, javaScript);

		applyClazzSpring(beanName, clazz);

		return this.getInnerBean(beanName);

	}

	private Object getInnerBean(String beanName) {
		Object object  = null;
		try {
			object  = applicationContext.getBean(beanName) ;
		} catch (BeansException e) {
		 
		}
		return object;
	}

	private Class comple(String beanName, String javaScript) {

		return this.groovyClassLoader.parseClass(javaScript);
	}

	private void applyClazzSpring(String beanName, Class clazz) {
		AbstractBeanDefinition definition = BeanDefinitionBuilder.genericBeanDefinition(clazz).getRawBeanDefinition();

		((BeanDefinitionRegistry) (((AbstractApplicationContext) applicationContext).getBeanFactory()))
				.registerBeanDefinition(beanName, definition);
		try {
			easyRulesTemplate.afterPropertiesSet();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
