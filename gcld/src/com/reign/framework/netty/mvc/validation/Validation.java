package com.reign.framework.netty.mvc.validation;

import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;

public interface Validation
{
    Result<?> validate(final Request p0, final Rule<?> p1);
}
