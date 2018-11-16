package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class Gift implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String typeId;
    private String typeName;
    private String childId;
    private String childName;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public String getTypeId() {
        return this.typeId;
    }
    
    public void setTypeId(final String typeId) {
        this.typeId = typeId;
    }
    
    public String getTypeName() {
        return this.typeName;
    }
    
    public void setTypeName(final String typeName) {
        this.typeName = typeName;
    }
    
    public String getChildId() {
        return this.childId;
    }
    
    public void setChildId(final String childId) {
        this.childId = childId;
    }
    
    public String getChildName() {
        return this.childName;
    }
    
    public void setChildName(final String childName) {
        this.childName = childName;
    }
}
