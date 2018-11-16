package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class BtLevel implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer lv;
    private String name;
    private Integer num;
    private Integer winExp;
    private Integer winIron;
    private Integer drawExp;
    private Integer drawIron;
    private String winHistory;
    private String drawHistory;
    private String upgradeWords;
    
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
    
    public Integer getNum() {
        return this.num;
    }
    
    public void setNum(final Integer num) {
        this.num = num;
    }
    
    public Integer getWinExp() {
        return this.winExp;
    }
    
    public void setWinExp(final Integer winExp) {
        this.winExp = winExp;
    }
    
    public Integer getWinIron() {
        return this.winIron;
    }
    
    public void setWinIron(final Integer winIron) {
        this.winIron = winIron;
    }
    
    public Integer getDrawExp() {
        return this.drawExp;
    }
    
    public void setDrawExp(final Integer drawExp) {
        this.drawExp = drawExp;
    }
    
    public Integer getDrawIron() {
        return this.drawIron;
    }
    
    public void setDrawIron(final Integer drawIron) {
        this.drawIron = drawIron;
    }
    
    public String getWinHistory() {
        return this.winHistory;
    }
    
    public void setWinHistory(final String winHistory) {
        this.winHistory = winHistory;
    }
    
    public String getDrawHistory() {
        return this.drawHistory;
    }
    
    public void setDrawHistory(final String drawHistory) {
        this.drawHistory = drawHistory;
    }
    
    public String getUpgradeWords() {
        return this.upgradeWords;
    }
    
    public void setUpgradeWords(final String upgradeWords) {
        this.upgradeWords = upgradeWords;
    }
}
