package com.reign.kf.match.operationresult;

import com.reign.framework.json.*;

public abstract class OperateResult
{
    protected boolean result;
    
    public boolean isSuccess() {
        return this.result;
    }
    
    public abstract byte[] getResultContent();
    
    public void buildNotifyJson(final JsonDocument doc) {
    }
    
    public String getLog() {
        return "";
    }
}
