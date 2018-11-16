package com.reign.gcld.timer.domain;

import com.reign.framework.mybatis.*;

public class PlayerJob implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String className;
    private String methodName;
    private String params;
    private Long executionTime;
    private Integer state;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
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
    
    public String getParams() {
        return this.params;
    }
    
    public void setParams(final String params) {
        this.params = params;
    }
    
    public Long getExecutionTime() {
        return this.executionTime;
    }
    
    public void setExecutionTime(final Long executionTime) {
        this.executionTime = executionTime;
    }
    
    public Integer getState() {
        return this.state;
    }
    
    public void setState(final Integer state) {
        this.state = state;
    }
}
