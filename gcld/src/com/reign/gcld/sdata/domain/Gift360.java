package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class Gift360 implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String giftname;
    private Integer copper;
    private Integer lumber;
    private Integer food;
    private Integer iron;
    private Integer recruitToken;
    private Integer freeCons;
    private Integer equip;
    private String name;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public String getGiftname() {
        return this.giftname;
    }
    
    public void setGiftname(final String giftname) {
        this.giftname = giftname;
    }
    
    public Integer getCopper() {
        return this.copper;
    }
    
    public void setCopper(final Integer copper) {
        this.copper = copper;
    }
    
    public Integer getLumber() {
        return this.lumber;
    }
    
    public void setLumber(final Integer lumber) {
        this.lumber = lumber;
    }
    
    public Integer getFood() {
        return this.food;
    }
    
    public void setFood(final Integer food) {
        this.food = food;
    }
    
    public Integer getIron() {
        return this.iron;
    }
    
    public void setIron(final Integer iron) {
        this.iron = iron;
    }
    
    public Integer getRecruitToken() {
        return this.recruitToken;
    }
    
    public void setRecruitToken(final Integer recruitToken) {
        this.recruitToken = recruitToken;
    }
    
    public Integer getFreeCons() {
        return this.freeCons;
    }
    
    public void setFreeCons(final Integer freeCons) {
        this.freeCons = freeCons;
    }
    
    public Integer getEquip() {
        return this.equip;
    }
    
    public void setEquip(final Integer equip) {
        this.equip = equip;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
}
