package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class EfLv implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer efLv;
    private String weiName;
    private String shuName;
    private String wuName;
    private Integer lv;
    private String weiArmies;
    private String shuArmies;
    private String wuArmies;
    private String weiDefName;
    private String shuDefName;
    private String wuDefName;
    private String weiDefArmies;
    private String shuDefArmies;
    private String wuDefArmies;
    private String weiAttName;
    private String shuAttName;
    private String wuAttName;
    private String weiAttArmies;
    private String shuAttArmies;
    private String wuAttArmies;
    private String ticketProb;
    private String ticketArmy;
    private Integer[] weiArmyIds;
    private Integer[] shuArmyIds;
    private Integer[] wuArmyIds;
    private Integer[] weiDefArmyIds;
    private Integer[] shuDefArmyIds;
    private Integer[] wuDefArmyIds;
    private Integer[] weiAttArmyIds;
    private Integer[] shuAttArmyIds;
    private Integer[] wuAttArmyIds;
    private Double[] ticketProbArray;
    private Integer[] ticketArmyIds;
    
    public EfLv() {
        this.weiArmyIds = null;
        this.shuArmyIds = null;
        this.wuArmyIds = null;
        this.weiDefArmyIds = null;
        this.shuDefArmyIds = null;
        this.wuDefArmyIds = null;
        this.weiAttArmyIds = null;
        this.shuAttArmyIds = null;
        this.wuAttArmyIds = null;
        this.ticketProbArray = null;
        this.ticketArmyIds = null;
    }
    
    public Integer getEfLv() {
        return this.efLv;
    }
    
    public void setEfLv(final Integer efLv) {
        this.efLv = efLv;
    }
    
    public String getWeiName() {
        return this.weiName;
    }
    
    public void setWeiName(final String weiName) {
        this.weiName = weiName;
    }
    
    public String getShuName() {
        return this.shuName;
    }
    
    public void setShuName(final String shuName) {
        this.shuName = shuName;
    }
    
    public String getWuName() {
        return this.wuName;
    }
    
    public void setWuName(final String wuName) {
        this.wuName = wuName;
    }
    
    public Integer getLv() {
        return this.lv;
    }
    
    public void setLv(final Integer lv) {
        this.lv = lv;
    }
    
    public String getWeiArmies() {
        return this.weiArmies;
    }
    
    public void setWeiArmies(final String weiArmies) {
        this.weiArmies = weiArmies;
    }
    
    public String getShuArmies() {
        return this.shuArmies;
    }
    
    public void setShuArmies(final String shuArmies) {
        this.shuArmies = shuArmies;
    }
    
    public String getWuArmies() {
        return this.wuArmies;
    }
    
    public void setWuArmies(final String wuArmies) {
        this.wuArmies = wuArmies;
    }
    
    public String getWeiDefName() {
        return this.weiDefName;
    }
    
    public void setWeiDefName(final String weiDefName) {
        this.weiDefName = weiDefName;
    }
    
    public String getShuDefName() {
        return this.shuDefName;
    }
    
    public void setShuDefName(final String shuDefName) {
        this.shuDefName = shuDefName;
    }
    
    public String getWuDefName() {
        return this.wuDefName;
    }
    
    public void setWuDefName(final String wuDefName) {
        this.wuDefName = wuDefName;
    }
    
    public String getWeiDefArmies() {
        return this.weiDefArmies;
    }
    
    public void setWeiDefArmies(final String weiDefArmies) {
        this.weiDefArmies = weiDefArmies;
    }
    
    public String getShuDefArmies() {
        return this.shuDefArmies;
    }
    
    public void setShuDefArmies(final String shuDefArmies) {
        this.shuDefArmies = shuDefArmies;
    }
    
    public String getWuDefArmies() {
        return this.wuDefArmies;
    }
    
    public void setWuDefArmies(final String wuDefArmies) {
        this.wuDefArmies = wuDefArmies;
    }
    
    public String getWeiAttName() {
        return this.weiAttName;
    }
    
    public void setWeiAttName(final String weiAttName) {
        this.weiAttName = weiAttName;
    }
    
    public String getShuAttName() {
        return this.shuAttName;
    }
    
    public void setShuAttName(final String shuAttName) {
        this.shuAttName = shuAttName;
    }
    
    public String getWuAttName() {
        return this.wuAttName;
    }
    
    public void setWuAttName(final String wuAttName) {
        this.wuAttName = wuAttName;
    }
    
    public String getWeiAttArmies() {
        return this.weiAttArmies;
    }
    
    public void setWeiAttArmies(final String weiAttArmies) {
        this.weiAttArmies = weiAttArmies;
    }
    
    public String getShuAttArmies() {
        return this.shuAttArmies;
    }
    
    public void setShuAttArmies(final String shuAttArmies) {
        this.shuAttArmies = shuAttArmies;
    }
    
    public String getWuAttArmies() {
        return this.wuAttArmies;
    }
    
    public void setWuAttArmies(final String wuAttArmies) {
        this.wuAttArmies = wuAttArmies;
    }
    
    public String getTicketProb() {
        return this.ticketProb;
    }
    
    public void setTicketProb(final String ticketProb) {
        this.ticketProb = ticketProb;
    }
    
    public String getTicketArmy() {
        return this.ticketArmy;
    }
    
    public void setTicketArmy(final String ticketArmy) {
        this.ticketArmy = ticketArmy;
    }
    
    public Integer[] getWeiArmyIds() {
        return this.weiArmyIds;
    }
    
    public void setWeiArmyIds(final Integer[] weiArmyIds) {
        this.weiArmyIds = weiArmyIds;
    }
    
    public Integer[] getShuArmyIds() {
        return this.shuArmyIds;
    }
    
    public void setShuArmyIds(final Integer[] shuArmyIds) {
        this.shuArmyIds = shuArmyIds;
    }
    
    public Integer[] getWuArmyIds() {
        return this.wuArmyIds;
    }
    
    public void setWuArmyIds(final Integer[] wuArmyIds) {
        this.wuArmyIds = wuArmyIds;
    }
    
    public Integer[] getWeiDefArmyIds() {
        return this.weiDefArmyIds;
    }
    
    public void setWeiDefArmyIds(final Integer[] weiDefArmyIds) {
        this.weiDefArmyIds = weiDefArmyIds;
    }
    
    public Integer[] getShuDefArmyIds() {
        return this.shuDefArmyIds;
    }
    
    public void setShuDefArmyIds(final Integer[] shuDefArmyIds) {
        this.shuDefArmyIds = shuDefArmyIds;
    }
    
    public Integer[] getWuDefArmyIds() {
        return this.wuDefArmyIds;
    }
    
    public void setWuDefArmyIds(final Integer[] wuDefArmyIds) {
        this.wuDefArmyIds = wuDefArmyIds;
    }
    
    public Integer[] getWeiAttArmyIds() {
        return this.weiAttArmyIds;
    }
    
    public void setWeiAttArmyIds(final Integer[] weiAttArmyIds) {
        this.weiAttArmyIds = weiAttArmyIds;
    }
    
    public Integer[] getShuAttArmyIds() {
        return this.shuAttArmyIds;
    }
    
    public void setShuAttArmyIds(final Integer[] shuAttArmyIds) {
        this.shuAttArmyIds = shuAttArmyIds;
    }
    
    public Integer[] getWuAttArmyIds() {
        return this.wuAttArmyIds;
    }
    
    public void setWuAttArmyIds(final Integer[] wuAttArmyIds) {
        this.wuAttArmyIds = wuAttArmyIds;
    }
    
    public Double[] getTicketProbArray() {
        return this.ticketProbArray;
    }
    
    public void setTicketProbArray(final Double[] ticketProbArray) {
        this.ticketProbArray = ticketProbArray;
    }
    
    public Integer[] getTicketArmyIds() {
        return this.ticketArmyIds;
    }
    
    public void setTicketArmyIds(final Integer[] ticketArmyIds) {
        this.ticketArmyIds = ticketArmyIds;
    }
}
