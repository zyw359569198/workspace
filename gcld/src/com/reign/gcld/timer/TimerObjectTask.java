package com.reign.gcld.timer;

public class TimerObjectTask extends TimerTask
{
    private Object[] paramsObjects;
    
    public Object[] getParamsObjects() {
        return this.paramsObjects;
    }
    
    public void setParamsObjects(final Object[] paramsObjects) {
        this.paramsObjects = paramsObjects;
    }
    
    public TimerObjectTask(final long executionTime, final String className, final String methodName, final Object[] params) {
        super(executionTime, className, methodName, "");
        this.paramsObjects = params;
    }
}
