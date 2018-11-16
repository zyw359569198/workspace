package com.reign.gcld.common.web;

import com.reign.framework.netty.mvc.result.*;

public class JsonPResult implements Result<byte[]>
{
    private byte[] result;
    
    public JsonPResult(final byte[] result) {
        this.result = result;
    }
    
    @Override
	public String getViewName() {
        return "jsonp";
    }
    
    @Override
	public byte[] getResult() {
        return this.result;
    }
}
