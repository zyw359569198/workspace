package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class HmGtMain implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer lv;
    private String name;
    private Integer dMax;
    private Integer iron;
    private Integer items;
    private Integer num;
    private Integer gem;
    private Integer upGold;
    private Integer extraGem;
    private String traderIntro;
    private String itemIntro;
    
    public Integer getLv() {
        return this.lv;
    }
    
    public void setLv(final Integer lv) {
        this.lv = lv;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public Integer getDMax() {
        return this.dMax;
    }
    
    public void setDMax(final Integer dMax) {
        this.dMax = dMax;
    }
    
    public Integer getIron() {
        return this.iron;
    }
    
    public void setIron(final Integer iron) {
        this.iron = iron;
    }
    
    public Integer getItems() {
        return this.items;
    }
    
    public void setItems(final Integer items) {
        this.items = items;
    }
    
    public Integer getNum() {
        return this.num;
    }
    
    public void setNum(final Integer num) {
        this.num = num;
    }
    
    public Integer getGem() {
        return this.gem;
    }
    
    public void setGem(final Integer gem) {
        this.gem = gem;
    }
    
    public Integer getUpGold() {
        return this.upGold;
    }
    
    public void setUpGold(final Integer upGold) {
        this.upGold = upGold;
    }
    
    public Integer getExtraGem() {
        return this.extraGem;
    }
    
    public void setExtraGem(final Integer extraGem) {
        this.extraGem = extraGem;
    }
    
    public String getTraderIntro() {
        return this.traderIntro;
    }
    
    public void setTraderIntro(final String traderIntro) {
        this.traderIntro = traderIntro;
    }
    
    public String getItemIntro() {
        return this.itemIntro;
    }
    
    public void setItemIntro(final String itemIntro) {
        this.itemIntro = itemIntro;
    }
}
