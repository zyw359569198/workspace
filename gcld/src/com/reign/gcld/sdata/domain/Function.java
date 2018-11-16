package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class Function implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String intro;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public String getIntro() {
        return this.intro;
    }
    
    public void setIntro(final String intro) {
        this.intro = intro;
    }
}
