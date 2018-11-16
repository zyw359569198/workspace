package com.reign.kf.match.common.web;

import com.reign.framework.netty.mvc.result.*;

public class MatchResult implements Result<Object>
{
    private Object result;
    
    public MatchResult(final Object result) {
        this.result = result;
    }
    
    @Override
	public Object getResult() {
        return this.result;
    }
    
    @Override
	public String getViewName() {
        return "matchview";
    }
}
