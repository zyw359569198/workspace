package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class StringC implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String param;
    private String value;
    private String system;
    private String intro;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public String getParam() {
        return this.param;
    }
    
    public void setParam(final String param) {
        this.param = param;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public void setValue(final String value) {
        this.value = value;
    }
    
    public String getSystem() {
        return this.system;
    }
    
    public void setSystem(final String system) {
        this.system = system;
    }
    
    public String getIntro() {
        return this.intro;
    }
    
    public void setIntro(final String intro) {
        this.intro = intro;
    }
}
