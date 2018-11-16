package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class FightStrategies implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private String pic;
    private String intro;
    private Integer baseDamage;
    
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
    
    public String getPic() {
        return this.pic;
    }
    
    public void setPic(final String pic) {
        this.pic = pic;
    }
    
    public String getIntro() {
        return this.intro;
    }
    
    public void setIntro(final String intro) {
        this.intro = intro;
    }
    
    public Integer getBaseDamage() {
        return this.baseDamage;
    }
    
    public void setBaseDamage(final Integer baseDamage) {
        this.baseDamage = baseDamage;
    }
}
