package com.reign.framework.netty.mvc.adaptor;

import com.reign.framework.netty.mvc.annotation.*;
import com.reign.framework.netty.mvc.adaptor.injector.*;

public class PairAdaptor extends AbstractAdaptor
{
    @Override
    public ParamInjector evalInjector(final Class<?> clazz, final RequestParam requestParam) {
        if (requestParam == null) {
            return new NullInjector(clazz);
        }
        if (clazz.isArray()) {
            return new ArrayInjector(requestParam.value(), clazz);
        }
        return new NameInjector(requestParam.value(), clazz);
    }
}
