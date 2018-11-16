package com.reign.framework.netty.mvc.result;

public class ImageResult implements Result<byte[]>
{
    private byte[] result;
    
    public ImageResult(final byte[] result) {
        this.result = result;
    }
    
    @Override
    public String getViewName() {
        return "image";
    }
    
    @Override
    public byte[] getResult() {
        return this.result;
    }
}
