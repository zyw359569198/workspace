package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class Treasure implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private Integer pos;
    private Integer type;
    private String effect;
    private Integer gold;
    private String intro;
    private String tipsLack;
    private String tipsOwned;
    private String pic;
    
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
    
    public Integer getPos() {
        return this.pos;
    }
    
    public void setPos(final Integer pos) {
        this.pos = pos;
    }
    
    public Integer getType() {
        return this.type;
    }
    
    public void setType(final Integer type) {
        this.type = type;
    }
    
    public String getEffect() {
        return this.effect;
    }
    
    public void setEffect(final String effect) {
        this.effect = effect;
    }
    
    public Integer getGold() {
        return this.gold;
    }
    
    public void setGold(final Integer gold) {
        this.gold = gold;
    }
    
    public String getIntro() {
        return this.intro;
    }
    
    public void setIntro(final String intro) {
        this.intro = intro;
    }
    
    public String getTipsLack() {
        return this.tipsLack;
    }
    
    public void setTipsLack(final String tipsLack) {
        this.tipsLack = tipsLack;
    }
    
    public String getTipsOwned() {
        return this.tipsOwned;
    }
    
    public void setTipsOwned(final String tipsOwned) {
        this.tipsOwned = tipsOwned;
    }
    
    public String getPic() {
        return this.pic;
    }
    
    public void setPic(final String pic) {
        this.pic = pic;
    }
}
