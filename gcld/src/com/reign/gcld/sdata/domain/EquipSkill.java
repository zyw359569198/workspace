package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class EquipSkill implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer skillType;
    private String key;
    private String name;
    private String pic;
    private String intro;
    private String explain;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getSkillType() {
        return this.skillType;
    }
    
    public void setSkillType(final Integer skillType) {
        this.skillType = skillType;
    }
    
    public String getKey() {
        return this.key;
    }
    
    public void setKey(final String key) {
        this.key = key;
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
    
    public String getExplain() {
        return this.explain;
    }
    
    public void setExplain(final String explain) {
        this.explain = explain;
    }
}
