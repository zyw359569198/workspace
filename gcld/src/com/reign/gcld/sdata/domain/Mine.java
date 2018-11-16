package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class Mine implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private Integer type;
    private Integer page;
    private Integer pagePos;
    private Integer output;
    private Integer time;
    private String pic;
    private Integer stone;
    
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
    
    public Integer getType() {
        return this.type;
    }
    
    public void setType(final Integer type) {
        this.type = type;
    }
    
    public Integer getPage() {
        return this.page;
    }
    
    public void setPage(final Integer page) {
        this.page = page;
    }
    
    public Integer getPagePos() {
        return this.pagePos;
    }
    
    public void setPagePos(final Integer pagePos) {
        this.pagePos = pagePos;
    }
    
    public Integer getOutput() {
        return this.output;
    }
    
    public void setOutput(final Integer output) {
        this.output = output;
    }
    
    public Integer getTime() {
        return this.time;
    }
    
    public void setTime(final Integer time) {
        this.time = time;
    }
    
    public String getPic() {
        return this.pic;
    }
    
    public void setPic(final String pic) {
        this.pic = pic;
    }
    
    public Integer getStone() {
        return this.stone;
    }
    
    public void setStone(final Integer stone) {
        this.stone = stone;
    }
}
