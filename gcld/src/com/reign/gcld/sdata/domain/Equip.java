package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class Equip implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private Integer type;
    private Integer quality;
    private Integer intimacyGroup;
    private Double intimacyGroupProb;
    private Double probBase;
    private Double probIntimacy;
    private Integer attribute;
    private Integer level;
    private Integer skillType;
    private Integer skillNum;
    private Integer skillLvDefault;
    private Integer skillLvMax;
    private Integer copperBuy;
    private Integer copperSold;
    private Integer maxLevel;
    private Integer defaultLevel;
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
    
    public Integer getType() {
        return this.type;
    }
    
    public void setType(final Integer type) {
        this.type = type;
    }
    
    public Integer getQuality() {
        return this.quality;
    }
    
    public void setQuality(final Integer quality) {
        this.quality = quality;
    }
    
    public Integer getIntimacyGroup() {
        return this.intimacyGroup;
    }
    
    public void setIntimacyGroup(final Integer intimacyGroup) {
        this.intimacyGroup = intimacyGroup;
    }
    
    public Double getIntimacyGroupProb() {
        return this.intimacyGroupProb;
    }
    
    public void setIntimacyGroupProb(final Double intimacyGroupProb) {
        this.intimacyGroupProb = intimacyGroupProb;
    }
    
    public Double getProbBase() {
        return this.probBase;
    }
    
    public void setProbBase(final Double probBase) {
        this.probBase = probBase;
    }
    
    public Double getProbIntimacy() {
        return this.probIntimacy;
    }
    
    public void setProbIntimacy(final Double probIntimacy) {
        this.probIntimacy = probIntimacy;
    }
    
    public Integer getAttribute() {
        return this.attribute;
    }
    
    public void setAttribute(final Integer attribute) {
        this.attribute = attribute;
    }
    
    public Integer getLevel() {
        return this.level;
    }
    
    public void setLevel(final Integer level) {
        this.level = level;
    }
    
    public Integer getSkillType() {
        return this.skillType;
    }
    
    public void setSkillType(final Integer skillType) {
        this.skillType = skillType;
    }
    
    public Integer getSkillNum() {
        return this.skillNum;
    }
    
    public void setSkillNum(final Integer skillNum) {
        this.skillNum = skillNum;
    }
    
    public Integer getSkillLvDefault() {
        return this.skillLvDefault;
    }
    
    public void setSkillLvDefault(final Integer skillLvDefault) {
        this.skillLvDefault = skillLvDefault;
    }
    
    public Integer getSkillLvMax() {
        return this.skillLvMax;
    }
    
    public void setSkillLvMax(final Integer skillLvMax) {
        this.skillLvMax = skillLvMax;
    }
    
    public Integer getCopperBuy() {
        return this.copperBuy;
    }
    
    public void setCopperBuy(final Integer copperBuy) {
        this.copperBuy = copperBuy;
    }
    
    public Integer getCopperSold() {
        return this.copperSold;
    }
    
    public void setCopperSold(final Integer copperSold) {
        this.copperSold = copperSold;
    }
    
    public Integer getMaxLevel() {
        return this.maxLevel;
    }
    
    public void setMaxLevel(final Integer maxLevel) {
        this.maxLevel = maxLevel;
    }
    
    public Integer getDefaultLevel() {
        return this.defaultLevel;
    }
    
    public void setDefaultLevel(final Integer defaultLevel) {
        this.defaultLevel = defaultLevel;
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
