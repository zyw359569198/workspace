package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;
import com.reign.gcld.battle.common.*;

public class Barbarain implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private Integer degree;
    private Integer lv;
    private Integer armyId;
    private String weiArmies;
    private String shuArmies;
    private String wuArmies;
    private Integer timeInterval;
    private Integer num;
    private String reward;
    private Integer target;
    private String weiName;
    private String shuName;
    private String wuName;
    private String weiIName;
    private String shuIName;
    private String wuIName;
    private String weiIArmies;
    private String shuIArmies;
    private String wuIArmies;
    private String rewardArmy;
    BattleDrop battleDrop;
    private Integer[] weiArmyIds;
    private Integer[] shuArmyIds;
    private Integer[] wuArmyIds;
    private Integer[] weiIArmyIds;
    private Integer[] shuIArmyIds;
    private Integer[] wuIArmyIds;
    private Integer[] rewardArmyIds;
    
    public Barbarain() {
        this.battleDrop = null;
    }
    
    public BattleDrop getBattleDrop() {
        return this.battleDrop;
    }
    
    public void setBattleDrop(final BattleDrop battleDrop) {
        this.battleDrop = battleDrop;
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
    
    public Integer getDegree() {
        return this.degree;
    }
    
    public void setDegree(final Integer degree) {
        this.degree = degree;
    }
    
    public Integer getLv() {
        return this.lv;
    }
    
    public void setLv(final Integer lv) {
        this.lv = lv;
    }
    
    public Integer getArmyId() {
        return this.armyId;
    }
    
    public void setArmyId(final Integer armyId) {
        this.armyId = armyId;
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
    
    public Integer getTimeInterval() {
        return this.timeInterval;
    }
    
    public void setTimeInterval(final Integer timeInterval) {
        this.timeInterval = timeInterval;
    }
    
    public Integer getNum() {
        return this.num;
    }
    
    public void setNum(final Integer num) {
        this.num = num;
    }
    
    public String getReward() {
        return this.reward;
    }
    
    public void setReward(final String reward) {
        this.reward = reward;
    }
    
    public Integer getTarget() {
        return this.target;
    }
    
    public void setTarget(final Integer target) {
        this.target = target;
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
    
    public String getWeiIName() {
        return this.weiIName;
    }
    
    public void setWeiIName(final String weiIName) {
        this.weiIName = weiIName;
    }
    
    public String getShuIName() {
        return this.shuIName;
    }
    
    public void setShuIName(final String shuIName) {
        this.shuIName = shuIName;
    }
    
    public String getWuIName() {
        return this.wuIName;
    }
    
    public void setWuIName(final String wuIName) {
        this.wuIName = wuIName;
    }
    
    public String getWeiIArmies() {
        return this.weiIArmies;
    }
    
    public void setWeiIArmies(final String weiIArmies) {
        this.weiIArmies = weiIArmies;
    }
    
    public String getShuIArmies() {
        return this.shuIArmies;
    }
    
    public void setShuIArmies(final String shuIArmies) {
        this.shuIArmies = shuIArmies;
    }
    
    public String getWuIArmies() {
        return this.wuIArmies;
    }
    
    public void setWuIArmies(final String wuIArmies) {
        this.wuIArmies = wuIArmies;
    }
    
    public String getRewardArmy() {
        return this.rewardArmy;
    }
    
    public void setRewardArmy(final String rewardArmy) {
        this.rewardArmy = rewardArmy;
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
    
    public Integer[] getWeiIArmyIds() {
        return this.weiIArmyIds;
    }
    
    public void setWeiIArmyIds(final Integer[] weiIArmyIds) {
        this.weiIArmyIds = weiIArmyIds;
    }
    
    public Integer[] getShuIArmyIds() {
        return this.shuIArmyIds;
    }
    
    public void setShuIArmyIds(final Integer[] shuIArmyIds) {
        this.shuIArmyIds = shuIArmyIds;
    }
    
    public Integer[] getWuIArmyIds() {
        return this.wuIArmyIds;
    }
    
    public void setWuIArmyIds(final Integer[] wuIArmyIds) {
        this.wuIArmyIds = wuIArmyIds;
    }
    
    public Integer[] getRewardArmyIds() {
        return this.rewardArmyIds;
    }
    
    public void setRewardArmyIds(final Integer[] rewardArmyIds) {
        this.rewardArmyIds = rewardArmyIds;
    }
}
