package com.reign.gcld.timer;

import org.springframework.stereotype.*;
import org.springframework.context.*;
import org.springframework.beans.*;

@Component("springTimerContext")
public class SpringTimerContext implements TimerContext, ApplicationContextAware
{
    private ApplicationContext context;
    
    @Override
	public void setApplicationContext(final ApplicationContext context) throws BeansException {
        this.context = context;
    }
    
    @Override
    public Object getBean(final String beanName) {
        return this.context.getBean(beanName);
    }
}
