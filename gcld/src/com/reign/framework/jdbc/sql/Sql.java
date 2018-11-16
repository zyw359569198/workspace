package com.reign.framework.jdbc.sql;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "sql", propOrder = { "value" })
public class Sql
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
