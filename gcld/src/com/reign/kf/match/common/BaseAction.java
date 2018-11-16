package com.reign.kf.match.common;

import java.io.*;
import com.reign.framework.netty.mvc.annotation.*;
import com.reign.framework.netty.mvc.view.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;

@Action
@Views({ @View(name = "byte", type = ByteView.class) })
public class BaseAction implements Serializable
{
    private static final long serialVersionUID = -1834822056752116266L;
    
    public ByteResult getResult(final byte[] result, final Request request) {
        return new ByteResult(result);
    }
    
    public ByteResult getNoneExtraResult(final byte[] result) {
        return new ByteResult(result);
    }
    
    public Object getFromSession(final String key, final Request request) {
        return request.getSession().getAttribute(key);
    }
    
    public void putToSession(final String key, final Object obj, final Request request) {
        request.getSession().setAttribute(key, obj);
    }
    
    public void clearSession(final Request request) {
        request.getSession().invalidate();
    }
    
    public void clearSession(final Request request, final String key) {
        request.getSession().removeAttribute(key);
    }
}
