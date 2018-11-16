package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class GeneralTreasure implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private Integer type;
    private Integer quality;
    private Integer minGeneralLevel;
    private Integer leaMax;
    private Integer leaMin;
    private Integer strMax;
    private Integer strMin;
    private Integer intMax;
    private Integer intMin;
    private Integer polMax;
    private Integer polMin;
    private Integer copperPrice;
    private Integer removeGold;
    private String intro;
    private String pic;
    private Integer prob;
    private Integer minGetLv;
    
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
    
    public Integer getMinGeneralLevel() {
        return this.minGeneralLevel;
    }
    
    public void setMinGeneralLevel(final Integer minGeneralLevel) {
        this.minGeneralLevel = minGeneralLevel;
    }
    
    public Integer getLeaMax() {
        return this.leaMax;
    }
    
    public void setLeaMax(final Integer leaMax) {
        this.leaMax = leaMax;
    }
    
    public Integer getLeaMin() {
        return this.leaMin;
    }
    
    public void setLeaMin(final Integer leaMin) {
        this.leaMin = leaMin;
    }
    
    public Integer getStrMax() {
        return this.strMax;
    }
    
    public void setStrMax(final Integer strMax) {
        this.strMax = strMax;
    }
    
    public Integer getStrMin() {
        return this.strMin;
    }
    
    public void setStrMin(final Integer strMin) {
        this.strMin = strMin;
    }
    
    public Integer getIntMax() {
        return this.intMax;
    }
    
    public void setIntMax(final Integer intMax) {
        this.intMax = intMax;
    }
    
    public Integer getIntMin() {
        return this.intMin;
    }
    
    public void setIntMin(final Integer intMin) {
        this.intMin = intMin;
    }
    
    public Integer getPolMax() {
        return this.polMax;
    }
    
    public void setPolMax(final Integer polMax) {
        this.polMax = polMax;
    }
    
    public Integer getPolMin() {
        return this.polMin;
    }
    
    public void setPolMin(final Integer polMin) {
        this.polMin = polMin;
    }
    
    public Integer getCopperPrice() {
        return this.copperPrice;
    }
    
    public void setCopperPrice(final Integer copperPrice) {
        this.copperPrice = copperPrice;
    }
    
    public Integer getRemoveGold() {
        return this.removeGold;
    }
    
    public void setRemoveGold(final Integer removeGold) {
        this.removeGold = removeGold;
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
    
    public Integer getProb() {
        return this.prob;
    }
    
    public void setProb(final Integer prob) {
        this.prob = prob;
    }
    
    public Integer getMinGetLv() {
        return this.minGetLv;
    }
    
    public void setMinGetLv(final Integer minGetLv) {
        this.minGetLv = minGetLv;
    }
}
