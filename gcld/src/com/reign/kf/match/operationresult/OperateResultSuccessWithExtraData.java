package com.reign.kf.match.operationresult;

import com.reign.framework.json.*;

public class OperateResultSuccessWithExtraData extends OperateResult
{
    private Object extraData;
    
    public OperateResultSuccessWithExtraData(final Object extraData) {
        this.extraData = null;
        this.result = true;
        this.extraData = extraData;
    }
    
    @Override
    public byte[] getResultContent() {
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    public Object getExtraData() {
        return this.extraData;
    }
}
