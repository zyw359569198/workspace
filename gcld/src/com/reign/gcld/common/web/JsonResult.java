package com.reign.gcld.common.web;

import com.reign.framework.netty.mvc.result.*;

public class JsonResult implements Result<byte[]>
{
    private byte[] result;
    private String viewName;
    
    public JsonResult(final byte[] result) {
        this.result = result;
        this.viewName = "json";
    }
    
    @Override
	public String getViewName() {
        return this.viewName;
    }
    
    public void setViewName(final String viewName) {
        this.viewName = viewName;
    }
    
    @Override
	public byte[] getResult() {
        return this.result;
    }
    
    @Override
    public String toString() {
        return new String(this.result);
    }
}
