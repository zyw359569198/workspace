package com.reign.kf.match.operationresult;

import com.reign.framework.json.*;

public class OperateResultSuccess extends OperateResult
{
    private static final OperateResultSuccess instance;
    private byte[] content;
    
    static {
        instance = new OperateResultSuccess();
    }
    
    public OperateResultSuccess() {
        this.content = null;
        this.result = true;
    }
    
    public OperateResultSuccess(final byte[] content) {
        this.content = null;
        this.result = true;
        this.content = content;
    }
    
    @Override
    public byte[] getResultContent() {
        if (this.content == null) {
            return JsonBuilder.getJson(State.SUCCESS, "");
        }
        return JsonBuilder.getJson(State.SUCCESS, this.content);
    }
    
    public static OperateResultSuccess getOne() {
        return OperateResultSuccess.instance;
    }
}
