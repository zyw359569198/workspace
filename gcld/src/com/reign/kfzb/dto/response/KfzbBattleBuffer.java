package com.reign.kfzb.dto.response;

public class KfzbBattleBuffer
{
    public static final int BUFFERTYPE_NORMAL = 1;
    int bufferId;
    int bufferName;
    int bufferType;
    String bufferEffect;
    int valid;
    int bufferValue;
    
    public int getBufferId() {
        return this.bufferId;
    }
    
    public void setBufferId(final int bufferId) {
        this.bufferId = bufferId;
    }
    
    public int getBufferName() {
        return this.bufferName;
    }
    
    public void setBufferName(final int bufferName) {
        this.bufferName = bufferName;
    }
    
    public int getBufferType() {
        return this.bufferType;
    }
    
    public void setBufferType(final int bufferType) {
        this.bufferType = bufferType;
    }
    
    public String getBufferEffect() {
        return this.bufferEffect;
    }
    
    public void setBufferEffect(final String bufferEffect) {
        this.bufferEffect = bufferEffect;
    }
    
    public int getValid() {
        return this.valid;
    }
    
    public void setValid(final int valid) {
        this.valid = valid;
    }
    
    public int getBufferValue() {
        return this.bufferValue;
    }
    
    public void setBufferValue(final int bufferValue) {
        this.bufferValue = bufferValue;
    }
}
