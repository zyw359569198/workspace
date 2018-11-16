package com.reign.framework.netty.mvc.result;

public class HtmlResult implements Result<byte[]>
{
    private byte[] result;
    
    public HtmlResult(final byte[] result) {
        this.result = result;
    }
    
    @Override
    public String getViewName() {
        return "html";
    }
    
    @Override
    public byte[] getResult() {
        return this.result;
    }
}
