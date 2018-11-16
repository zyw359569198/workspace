package com.reign.kf.match.domain;

import com.reign.framework.jdbc.orm.*;
import com.reign.framework.jdbc.orm.annotation.*;

@JdbcEntity
public class Temp implements JdbcModel
{
    private static final long serialVersionUID = 1L;
    @Id
    @AutoGenerator
    private int id;
    private int winNum;
    private int failNum;
    private String matchTag;
    
    public int getId() {
        return this.id;
    }
    
    public void setId(final int id) {
        this.id = id;
    }
    
    public int getWinNum() {
        return this.winNum;
    }
    
    public void setWinNum(final int winNum) {
        this.winNum = winNum;
    }
    
    public int getFailNum() {
        return this.failNum;
    }
    
    public void setFailNum(final int failNum) {
        this.failNum = failNum;
    }
    
    public String getMatchTag() {
        return this.matchTag;
    }
    
    public void setMatchTag(final String matchTag) {
        this.matchTag = matchTag;
    }
}
