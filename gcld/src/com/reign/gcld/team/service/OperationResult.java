package com.reign.gcld.team.service;

public class OperationResult
{
    private boolean result;
    private String resultContent;
    private Object extraInfo;
    
    public void setResult(final boolean result) {
        this.result = result;
    }
    
    public void setResultContent(final String resultContent) {
        this.resultContent = resultContent;
    }
    
    public OperationResult(final boolean result) {
        this.result = result;
    }
    
    public OperationResult(final boolean result, final String resultContent) {
        this.result = result;
        this.resultContent = resultContent;
        this.extraInfo = null;
    }
    
    public OperationResult(final boolean result, final String resultContent, final Object extraInfo) {
        this.result = result;
        this.resultContent = resultContent;
        this.extraInfo = extraInfo;
    }
    
    public boolean getResult() {
        return this.result;
    }
    
    public String getResultContent() {
        return this.resultContent;
    }
    
    public Object getExtraInfo() {
        return this.extraInfo;
    }
    
    public void setExtraInfo(final Object obj) {
        this.extraInfo = obj;
    }
}
