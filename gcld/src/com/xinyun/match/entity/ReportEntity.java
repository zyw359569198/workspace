package com.xinyun.match.entity;

import java.io.*;

public class ReportEntity implements Serializable
{
    private static final long serialVersionUID = 4552514805412490826L;
    private String id;
    private String content;
    
    public String getId() {
        return this.id;
    }
    
    public void setId(final String id) {
        this.id = id;
    }
    
    public String getContent() {
        return this.content;
    }
    
    public void setContent(final String content) {
        this.content = content;
    }
}
