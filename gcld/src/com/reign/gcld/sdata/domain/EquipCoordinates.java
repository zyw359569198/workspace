package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class EquipCoordinates implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private Integer itemId;
    private Integer pos1Skill;
    private Integer pos2Skill;
    private Integer pos3Skill;
    private Integer pos4Skill;
    private Integer pos5Skill;
    private Integer pos6Skill;
    private Integer att;
    private Integer def;
    private Integer blood;
    private Integer loadGold;
    private Integer unloadGold;
    private String pic;
    private String intro;
    
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
    
    public Integer getItemId() {
        return this.itemId;
    }
    
    public void setItemId(final Integer itemId) {
        this.itemId = itemId;
    }
    
    public Integer getPos1Skill() {
        return this.pos1Skill;
    }
    
    public void setPos1Skill(final Integer pos1Skill) {
        this.pos1Skill = pos1Skill;
    }
    
    public Integer getPos2Skill() {
        return this.pos2Skill;
    }
    
    public void setPos2Skill(final Integer pos2Skill) {
        this.pos2Skill = pos2Skill;
    }
    
    public Integer getPos3Skill() {
        return this.pos3Skill;
    }
    
    public void setPos3Skill(final Integer pos3Skill) {
        this.pos3Skill = pos3Skill;
    }
    
    public Integer getPos4Skill() {
        return this.pos4Skill;
    }
    
    public void setPos4Skill(final Integer pos4Skill) {
        this.pos4Skill = pos4Skill;
    }
    
    public Integer getPos5Skill() {
        return this.pos5Skill;
    }
    
    public void setPos5Skill(final Integer pos5Skill) {
        this.pos5Skill = pos5Skill;
    }
    
    public Integer getPos6Skill() {
        return this.pos6Skill;
    }
    
    public void setPos6Skill(final Integer pos6Skill) {
        this.pos6Skill = pos6Skill;
    }
    
    public Integer getAtt() {
        return this.att;
    }
    
    public void setAtt(final Integer att) {
        this.att = att;
    }
    
    public Integer getDef() {
        return this.def;
    }
    
    public void setDef(final Integer def) {
        this.def = def;
    }
    
    public Integer getBlood() {
        return this.blood;
    }
    
    public void setBlood(final Integer blood) {
        this.blood = blood;
    }
    
    public Integer getLoadGold() {
        return this.loadGold;
    }
    
    public void setLoadGold(final Integer loadGold) {
        this.loadGold = loadGold;
    }
    
    public Integer getUnloadGold() {
        return this.unloadGold;
    }
    
    public void setUnloadGold(final Integer unloadGold) {
        this.unloadGold = unloadGold;
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
}
