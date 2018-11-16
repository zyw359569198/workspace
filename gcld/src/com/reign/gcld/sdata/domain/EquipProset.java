package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class EquipProset implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private Integer itemId;
    private Integer setMain;
    private Integer set1;
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
    
    public Integer getSetMain() {
        return this.setMain;
    }
    
    public void setSetMain(final Integer setMain) {
        this.setMain = setMain;
    }
    
    public Integer getSet1() {
        return this.set1;
    }
    
    public void setSet1(final Integer set1) {
        this.set1 = set1;
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
