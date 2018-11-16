package com.reign.framework.netty.mvc;

public class ObjectFactory
{
    public Object buildBean(final Class<?> clazz) throws InstantiationException, IllegalAccessException {
        final Object o = clazz.newInstance();
        return o;
    }
}
