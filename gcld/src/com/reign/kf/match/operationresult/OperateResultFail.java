package com.reign.kf.match.operationresult;

import com.reign.framework.json.*;

public class OperateResultFail extends OperateResult
{
    private String errorMsg;
    
    public OperateResultFail(final String errorMsg) {
        this.result = false;
        this.errorMsg = errorMsg;
    }
    
    @Override
    public byte[] getResultContent() {
        return JsonBuilder.getMjcsJson(State.FAIL, this.errorMsg);
    }
}
