package com.reign.framework.netty.mvc.result;

public class ByteResult implements Result<byte[]>
{
    private byte[] result;
    
    public ByteResult(final byte[] result) {
        this.result = result;
    }
    
    @Override
    public String getViewName() {
        return "byte";
    }
    
    @Override
    public byte[] getResult() {
        return this.result;
    }
}
