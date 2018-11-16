package com.reign.kf.match.common.message;

import java.util.concurrent.*;
import java.lang.reflect.*;
import org.logicalcobwebs.cglib.proxy.*;

public final class HandlerManager
{
    public static ConcurrentMap<Class<? extends Message>, Handler> handlerMap;
    
    static {
        HandlerManager.handlerMap = new ConcurrentHashMap<Class<? extends Message>, Handler>();
    }
    
    public static void registerHandler(final Class<? extends Message> clazz, final Handler handler) {
        HandlerManager.handlerMap.putIfAbsent(clazz, handler);
    }
    
    public static void unload(final Class<? extends Message> clazz) {
        HandlerManager.handlerMap.remove(clazz);
    }
    
    public static Handler getHandler(final Class<? extends Message> clazz) {
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(DefaultHandler.class);
        enhancer.setCallback(new MethodInterceptor() {
            @Override
			public Object intercept(final Object obj, final Method method, final Object[] arguments, final MethodProxy proxy) throws Throwable {
                if (!method.getName().equals("handler")) {
                    return proxy.invokeSuper(obj, arguments);
                }
                final Handler handler = HandlerManager.handlerMap.get(clazz);
                if (handler == null) {
                    return null;
                }
                handler.handler((Message)arguments[0]);
                return null;
            }
        });
        return (Handler)enhancer.create();
    }
}
