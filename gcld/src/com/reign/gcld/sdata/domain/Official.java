package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class Official implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private String nameShort;
    private String pic;
    private Integer output;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getNameShort() {
        return this.nameShort;
    }
    
    public void setNameShort(final String nameShort) {
        this.nameShort = nameShort;
    }
    
    public String getPic() {
        return this.pic;
    }
    
    public void setPic(final String pic) {
        this.pic = pic;
    }
    
    public Integer getOutput() {
        return this.output;
    }
    
    public void setOutput(final Integer output) {
        this.output = output;
    }
}
