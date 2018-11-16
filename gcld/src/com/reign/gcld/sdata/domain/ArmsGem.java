package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class ArmsGem implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private Integer gemLv;
    private Integer chiefLv;
    private Integer att;
    private Integer def;
    private Integer blood;
    private Integer loadGold;
    private Integer unloadGold;
    private String intro;
    private String pic;
    private Integer type;
    private Integer skillNum;
    private Integer skillMaxLv;
    private Integer upgradeGem1;
    
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
    
    public Integer getGemLv() {
        return this.gemLv;
    }
    
    public void setGemLv(final Integer gemLv) {
        this.gemLv = gemLv;
    }
    
    public Integer getChiefLv() {
        return this.chiefLv;
    }
    
    public void setChiefLv(final Integer chiefLv) {
        this.chiefLv = chiefLv;
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
    
    public String getIntro() {
        return this.intro;
    }
    
    public void setIntro(final String intro) {
        this.intro = intro;
    }
    
    public String getPic() {
        return this.pic;
    }
    
    public void setPic(final String pic) {
        this.pic = pic;
    }
    
    public Integer getType() {
        return this.type;
    }
    
    public void setType(final Integer type) {
        this.type = type;
    }
    
    public Integer getSkillNum() {
        return this.skillNum;
    }
    
    public void setSkillNum(final Integer skillNum) {
        this.skillNum = skillNum;
    }
    
    public Integer getSkillMaxLv() {
        return this.skillMaxLv;
    }
    
    public void setSkillMaxLv(final Integer skillMaxLv) {
        this.skillMaxLv = skillMaxLv;
    }
    
    public Integer getUpgradeGem1() {
        return this.upgradeGem1;
    }
    
    public void setUpgradeGem1(final Integer upgradeGem1) {
        this.upgradeGem1 = upgradeGem1;
    }
}
