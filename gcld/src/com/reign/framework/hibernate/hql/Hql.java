package com.reign.framework.hibernate.hql;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "hql", propOrder = { "value" })
public class Hql
{
    @XmlValue
    protected String value;
    @XmlAttribute
    protected String id;
    
    public String getValue() {
        return this.value;
    }
    
    public void setValue(final String value) {
        this.value = value;
    }
    
    public String getId() {
        return this.id;
    }
    
    public void setId(final String value) {
        this.id = value;
    }
}
