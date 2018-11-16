package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class KtSdmzS implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer kindomLv;
    private Integer index;
    private String wei;
    private String shu;
    private String wu;
    private Integer n;
    private String weiName;
    private String shuName;
    private String wuName;
    private String weiArmies;
    private String shuArmies;
    private String wuArmies;
    private Integer rewardExp;
    private Integer rewardIron;
    private Integer[] weiArmyIds;
    private Integer[] shuArmyIds;
    private Integer[] wuArmyIds;
    private Set<Integer> weiSet;
    private Set<Integer> shuSet;
    private Set<Integer> wuSet;
    
    public KtSdmzS() {
        this.weiSet = new HashSet<Integer>();
        this.shuSet = new HashSet<Integer>();
        this.wuSet = new HashSet<Integer>();
    }
    
    public Integer[] getArmyIds(final int forceId) {
        switch (forceId) {
            case 1: {
                return this.weiArmyIds;
            }
            case 2: {
                return this.shuArmyIds;
            }
            case 3: {
                return this.wuArmyIds;
            }
            default: {
                return null;
            }
        }
    }
    
    public Set<Integer> getCitySet(final int forceId) {
        switch (forceId) {
            case 1: {
                return this.weiSet;
            }
            case 2: {
                return this.shuSet;
            }
            case 3: {
                return this.wuSet;
            }
            default: {
                return null;
            }
        }
    }
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getKindomLv() {
        return this.kindomLv;
    }
    
    public void setKindomLv(final Integer kindomLv) {
        this.kindomLv = kindomLv;
    }
    
    public Integer getIndex() {
        return this.index;
    }
    
    public void setIndex(final Integer index) {
        this.index = index;
    }
    
    public String getWei() {
        return this.wei;
    }
    
    public void setWei(final String wei) {
        this.wei = wei;
    }
    
    public String getShu() {
        return this.shu;
    }
    
    public void setShu(final String shu) {
        this.shu = shu;
    }
    
    public String getWu() {
        return this.wu;
    }
    
    public void setWu(final String wu) {
        this.wu = wu;
    }
    
    public Integer getN() {
        return this.n;
    }
    
    public void setN(final Integer n) {
        this.n = n;
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
    
    public Integer getRewardExp() {
        return this.rewardExp;
    }
    
    public void setRewardExp(final Integer rewardExp) {
        this.rewardExp = rewardExp;
    }
    
    public Integer getRewardIron() {
        return this.rewardIron;
    }
    
    public void setRewardIron(final Integer rewardIron) {
        this.rewardIron = rewardIron;
    }
    
    public Set<Integer> getWeiSet() {
        return this.weiSet;
    }
    
    public void setWeiSet(final Set<Integer> weiSet) {
        this.weiSet = weiSet;
    }
    
    public Set<Integer> getShuSet() {
        return this.shuSet;
    }
    
    public void setShuSet(final Set<Integer> shuSet) {
        this.shuSet = shuSet;
    }
    
    public Set<Integer> getWuSet() {
        return this.wuSet;
    }
    
    public void setWuSet(final Set<Integer> wuSet) {
        this.wuSet = wuSet;
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
}
