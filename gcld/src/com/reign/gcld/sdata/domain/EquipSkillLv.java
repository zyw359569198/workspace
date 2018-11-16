package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class EquipSkillLv implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer lv;
    private Double upgradeProbGold;
    private Integer upgradeMaxTimesGold;
    private Double upgradeProbFree;
    private Integer upgradeMaxTimesFree;
    
    public Integer getLv() {
        return this.lv;
    }
    
    public void setLv(final Integer lv) {
        this.lv = lv;
    }
    
    public Double getUpgradeProbGold() {
        return this.upgradeProbGold;
    }
    
    public void setUpgradeProbGold(final Double upgradeProbGold) {
        this.upgradeProbGold = upgradeProbGold;
    }
    
    public Integer getUpgradeMaxTimesGold() {
        return this.upgradeMaxTimesGold;
    }
    
    public void setUpgradeMaxTimesGold(final Integer upgradeMaxTimesGold) {
        this.upgradeMaxTimesGold = upgradeMaxTimesGold;
    }
    
    public Double getUpgradeProbFree() {
        return this.upgradeProbFree;
    }
    
    public void setUpgradeProbFree(final Double upgradeProbFree) {
        this.upgradeProbFree = upgradeProbFree;
    }
    
    public Integer getUpgradeMaxTimesFree() {
        return this.upgradeMaxTimesFree;
    }
    
    public void setUpgradeMaxTimesFree(final Integer upgradeMaxTimesFree) {
        this.upgradeMaxTimesFree = upgradeMaxTimesFree;
    }
}
