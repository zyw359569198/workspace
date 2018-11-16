package com.reign.gcld.common;

public enum JobClassMethondEnum
{
    BATTLESERVICE_PUSHTOKENMESSAGE("BATTLESERVICE_PUSHTOKENMESSAGE", 0, "battleService", "pushMessageToCitizen", "\u63a8\u9001\u67d0\u4e2a\u52bf\u529b\u5b98\u5458\u4ee4\u4fe1\u606f"), 
    WORLDFARMSERVICE_DEALFARMWORK("WORLDFARMSERVICE_DEALFARMWORK", 1, "worldFarmService", "dealFarmWork", "\u5c6f\u7530\u7ed3\u675f\u5b9a\u65f6\u4efb\u52a1"), 
    INDIVIDUALTASK_SENDMESSAGE("INDIVIDUALTASK_SENDMESSAGE", 2, "individualTaskService", "sendTaskMessage", "\u63a8\u9001\u4efb\u52a1\u6d88\u606f"), 
    CITYSERVICE_ADDPLAYEREVENT("CITYSERVICE_ADDPLAYEREVENT", 3, "cityService", "addPlayerEvent", "\u751f\u6210\u4e00\u4e2a\u73a9\u5bb6\u4e8b\u4ef6");
    
    private String className;
    private String methodName;
    private String intro;
    
    private JobClassMethondEnum(final String s, final int n, final String className, final String methodName, final String intro) {
        this.className = className;
        this.methodName = methodName;
        this.intro = intro;
    }
    
    public String getIntro() {
        return this.intro;
    }
    
    public void setIntro(final String intro) {
        this.intro = intro;
    }
    
    public String getClassName() {
        return this.className;
    }
    
    public void setClassName(final String className) {
        this.className = className;
    }
    
    public String getMethodName() {
        return this.methodName;
    }
    
    public void setMethodName(final String methodName) {
        this.methodName = methodName;
    }
}
