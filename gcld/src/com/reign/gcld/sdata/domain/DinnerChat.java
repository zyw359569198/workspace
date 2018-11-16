package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class DinnerChat implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String chief;
    private String general;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public String getChief() {
        return this.chief;
    }
    
    public void setChief(final String chief) {
        this.chief = chief;
    }
    
    public String getGeneral() {
        return this.general;
    }
    
    public void setGeneral(final String general) {
        this.general = general;
    }
}
