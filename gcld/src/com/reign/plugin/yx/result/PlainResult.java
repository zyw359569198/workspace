package com.reign.plugin.yx.result;

import com.reign.framework.netty.mvc.result.*;

public class PlainResult implements Result<byte[]>
{
    private byte[] result;
    
    public PlainResult(final byte[] result) {
        this.result = result;
    }
    
    @Override
	public String getViewName() {
        return "plain";
    }
    
    @Override
	public byte[] getResult() {
        return this.result;
    }
}
