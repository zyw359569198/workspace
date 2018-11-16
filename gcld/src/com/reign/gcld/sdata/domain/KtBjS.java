package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class KtBjS implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer kindomLv;
    private Integer index;
    private Integer t;
    private String wei;
    private String shu;
    private String wu;
    private Integer tb;
    private Integer rewardIron;
    private Integer rewardExp;
    private Integer n;
    private Integer td;
    private Set<Integer> weiCities;
    private Set<Integer> shuCities;
    private Set<Integer> wuCities;
    
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
    
    public Integer getT() {
        return this.t;
    }
    
    public void setT(final Integer t) {
        this.t = t;
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
    
    public Integer getTb() {
        return this.tb;
    }
    
    public void setTb(final Integer tb) {
        this.tb = tb;
    }
    
    public Integer getRewardIron() {
        return this.rewardIron;
    }
    
    public void setRewardIron(final Integer rewardIron) {
        this.rewardIron = rewardIron;
    }
    
    public Integer getRewardExp() {
        return this.rewardExp;
    }
    
    public void setRewardExp(final Integer rewardExp) {
        this.rewardExp = rewardExp;
    }
    
    public Integer getN() {
        return this.n;
    }
    
    public void setN(final Integer n) {
        this.n = n;
    }
    
    public Integer getTd() {
        return this.td;
    }
    
    public void setTd(final Integer td) {
        this.td = td;
    }
    
    public Set<Integer> getWeiCities() {
        return this.weiCities;
    }
    
    public void setWeiCities(final Set<Integer> weiCities) {
        this.weiCities = weiCities;
    }
    
    public Set<Integer> getShuCities() {
        return this.shuCities;
    }
    
    public void setShuCities(final Set<Integer> shuCities) {
        this.shuCities = shuCities;
    }
    
    public Set<Integer> getWuCities() {
        return this.wuCities;
    }
    
    public void setWuCities(final Set<Integer> wuCities) {
        this.wuCities = wuCities;
    }
    
    public Set<Integer> getCitiesSetByForceId(final int forceId) {
        switch (forceId) {
            case 1: {
                return this.weiCities;
            }
            case 2: {
                return this.shuCities;
            }
            case 3: {
                return this.wuCities;
            }
            default: {
                return null;
            }
        }
    }
}
