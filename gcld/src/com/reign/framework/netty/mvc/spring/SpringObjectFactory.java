package com.reign.framework.netty.mvc.spring;

import com.reign.framework.netty.mvc.*;
import org.springframework.beans.factory.config.*;
import org.apache.commons.logging.*;
import org.springframework.beans.*;
import org.springframework.beans.factory.*;
import org.springframework.context.*;

public class SpringObjectFactory extends ObjectFactory implements ApplicationContextAware
{
    private static final Log log;
    protected ApplicationContext context;
    protected AutowireCapableBeanFactory autowireCapableBeanFactory;
    protected int autowireStrategy;
    protected boolean alwaysRespectAutowireStrategy;
    
    static {
        log = LogFactory.getLog(SpringObjectFactory.class);
    }
    
    public SpringObjectFactory() {
        this.autowireStrategy = 1;
        this.alwaysRespectAutowireStrategy = false;
    }
    
    @Override
	public void setApplicationContext(final ApplicationContext context) throws BeansException {
        this.context = context;
        this.autowireCapableBeanFactory = this.findAutoWiringBeanFactory(this.context);
    }
    
    @Override
    public Object buildBean(final Class<?> clazz) throws InstantiationException, IllegalAccessException {
        Object o = null;
        try {
            o = this.context.getBean(clazz.getName());
        }
        catch (NoSuchBeanDefinitionException e) {
            o = this._buildBean(clazz);
        }
        return o;
    }
    
    private Object _buildBean(final Class<?> clazz) throws InstantiationException, IllegalAccessException {
        try {
            if (this.alwaysRespectAutowireStrategy) {
                final Object bean = this.autowireCapableBeanFactory.createBean(clazz, this.autowireStrategy, false);
                return bean;
            }
            Object bean = this.autowireCapableBeanFactory.autowire(clazz, 3, false);
            bean = this.autowireCapableBeanFactory.applyBeanPostProcessorsBeforeInitialization(bean, bean.getClass().getName());
            bean = this.autowireCapableBeanFactory.applyBeanPostProcessorsAfterInitialization(bean, bean.getClass().getName());
            return this.autoWireBean(bean, this.autowireStrategy);
        }
        catch (UnsatisfiedDependencyException e) {
            SpringObjectFactory.log.error("error build bean", e);
            return this.autoWireBean(clazz.newInstance(), this.autowireStrategy);
        }
    }
    
    private Object autoWireBean(final Object bean, final int autowireStrategy) {
        if (this.autowireCapableBeanFactory != null) {
            this.autowireCapableBeanFactory.autowireBeanProperties(bean, autowireStrategy, false);
        }
        this.injectApplicationContext(bean);
        return bean;
    }
    
    private void injectApplicationContext(final Object bean) {
        if (bean instanceof ApplicationContextAware) {
            ((ApplicationContextAware)bean).setApplicationContext(this.context);
        }
    }
    
    public void setAutowireStrategy(final int autowireStrategy) {
        switch (autowireStrategy) {
            case 1: {
                SpringObjectFactory.log.info("Setting autowire strategy to name");
                this.autowireStrategy = autowireStrategy;
                break;
            }
            case 2: {
                SpringObjectFactory.log.info("Setting autowire strategy to type");
                this.autowireStrategy = autowireStrategy;
                break;
            }
            case 3: {
                SpringObjectFactory.log.info("Setting autowire strategy to constructor");
                this.autowireStrategy = autowireStrategy;
                break;
            }
            case 0: {
                SpringObjectFactory.log.info("Setting autowire strategy to none");
                this.autowireStrategy = autowireStrategy;
                break;
            }
            default: {
                throw new IllegalStateException("invalid autowire type set, [type:" + autowireStrategy + "]");
            }
        }
    }
    
    public int getAutowireStrategy() {
        return this.autowireStrategy;
    }
    
    private AutowireCapableBeanFactory findAutoWiringBeanFactory(final ApplicationContext context) {
        if (context instanceof AutowireCapableBeanFactory) {
            return (AutowireCapableBeanFactory)context;
        }
        if (context instanceof ConfigurableApplicationContext) {
            return ((ConfigurableApplicationContext)context).getBeanFactory();
        }
        if (context.getParent() != null) {
            return this.findAutoWiringBeanFactory(context.getParent());
        }
        return null;
    }
}
