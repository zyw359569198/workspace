package com.reign.kf.match.sdata.domain;

import com.reign.framework.mybatis.*;

public class Tactic implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private String intro;
    private Double damageE;
    private Integer range;
    private Integer displayId;
    private Integer pic;
    private String basicPic;
    private String specialEffect;
    private Integer playertime;
    private int specialType;
    
    public int getSpecialType() {
        return this.specialType;
    }
    
    public void setSpecialType(final int specialType) {
        this.specialType = specialType;
    }
    
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
    
    public String getIntro() {
        return this.intro;
    }
    
    public void setIntro(final String intro) {
        this.intro = intro;
    }
    
    public Double getDamageE() {
        return this.damageE;
    }
    
    public void setDamageE(final Double damageE) {
        this.damageE = damageE;
    }
    
    public Integer getRange() {
        return this.range;
    }
    
    public void setRange(final Integer range) {
        this.range = range;
    }
    
    public Integer getDisplayId() {
        return this.displayId;
    }
    
    public void setDisplayId(final Integer displayId) {
        this.displayId = displayId;
    }
    
    public Integer getPic() {
        return this.pic;
    }
    
    public void setPic(final Integer pic) {
        this.pic = pic;
    }
    
    public String getBasicPic() {
        return this.basicPic;
    }
    
    public void setBasicPic(final String basicPic) {
        this.basicPic = basicPic;
    }
    
    public String getSpecialEffect() {
        return this.specialEffect;
    }
    
    public void setSpecialEffect(final String specialEffect) {
        this.specialEffect = specialEffect;
    }
    
    public Integer getPlayertime() {
        return this.playertime;
    }
    
    public void setPlayertime(final Integer playertime) {
        this.playertime = playertime;
    }
    
    public void calcuSpecial() {
        if (this.getSpecialEffect() == null) {
            return;
        }
        final String[] s = this.getSpecialEffect().split(",");
        if ("rob_food".equalsIgnoreCase(s[0])) {
            this.setSpecialType(2);
            return;
        }
        if ("confusion".equalsIgnoreCase(s[0])) {
            this.setSpecialType(1);
        }
    }
}