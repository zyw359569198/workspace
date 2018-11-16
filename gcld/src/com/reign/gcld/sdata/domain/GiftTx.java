package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class GiftTx implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer type;
    private String name;
    private Integer lv;
    private Integer food;
    private Integer recruitToken;
    private Integer danshutiejuan;
    private Integer freeCons;
    private String equip;
    private Integer gold;
    private String pic;
    private String picUrl;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getType() {
        return this.type;
    }
    
    public void setType(final Integer type) {
        this.type = type;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public Integer getLv() {
        return this.lv;
    }
    
    public void setLv(final Integer lv) {
        this.lv = lv;
    }
    
    public Integer getFood() {
        return this.food;
    }
    
    public void setFood(final Integer food) {
        this.food = food;
    }
    
    public Integer getRecruitToken() {
        return this.recruitToken;
    }
    
    public void setRecruitToken(final Integer recruitToken) {
        this.recruitToken = recruitToken;
    }
    
    public Integer getDanshutiejuan() {
        return this.danshutiejuan;
    }
    
    public void setDanshutiejuan(final Integer danshutiejuan) {
        this.danshutiejuan = danshutiejuan;
    }
    
    public Integer getFreeCons() {
        return this.freeCons;
    }
    
    public void setFreeCons(final Integer freeCons) {
        this.freeCons = freeCons;
    }
    
    public String getEquip() {
        return this.equip;
    }
    
    public void setEquip(final String equip) {
        this.equip = equip;
    }
    
    public Integer getGold() {
        return this.gold;
    }
    
    public void setGold(final Integer gold) {
        this.gold = gold;
    }
    
    public String getPic() {
        return this.pic;
    }
    
    public void setPic(final String pic) {
        this.pic = pic;
    }
    
    public String getPicUrl() {
        return this.picUrl;
    }
    
    public void setPicUrl(final String picUrl) {
        this.picUrl = picUrl;
    }
}
