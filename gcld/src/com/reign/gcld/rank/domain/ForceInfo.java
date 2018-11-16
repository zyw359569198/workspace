package com.reign.gcld.rank.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class ForceInfo implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer forceId;
    private Integer forceLv;
    private Integer forceExp;
    private Date endtime;
    private Integer iswin;
    private Integer beidiShoumaiCount;
    private Integer xirongShoumaiCount;
    private Integer dongyiShoumaiCount;
    private Integer beidiQinmidu;
    private Integer xirongQinmidu;
    private Integer dongyiQinmidu;
    private Integer beidiShoumaiSum;
    private Integer xirongShoumaiSum;
    private Integer dongyiShoumaiSum;
    private String ids;
    private Integer id;
    private Date tryEndTime;
    private Integer stage;
    private Integer tryWin;
    private Integer generalNum;
    private Integer pWin;
    private Integer pForceId;
    private Integer pCityId;
    private Integer pId;
    private Long farmInvestSum;
    private Integer lv;
    private String nationIndivInfo;
    
    public Integer getForceId() {
        return this.forceId;
    }
    
    public void setForceId(final Integer forceId) {
        this.forceId = forceId;
    }
    
    public Integer getForceLv() {
        return this.forceLv;
    }
    
    public void setForceLv(final Integer forceLv) {
        this.forceLv = forceLv;
    }
    
    public Integer getForceExp() {
        return this.forceExp;
    }
    
    public void setForceExp(final Integer forceExp) {
        this.forceExp = forceExp;
    }
    
    public Date getEndtime() {
        return this.endtime;
    }
    
    public void setEndtime(final Date endtime) {
        this.endtime = endtime;
    }
    
    public Integer getIswin() {
        return this.iswin;
    }
    
    public void setIswin(final Integer iswin) {
        this.iswin = iswin;
    }
    
    public Integer getBeidiShoumaiCount() {
        return this.beidiShoumaiCount;
    }
    
    public void setBeidiShoumaiCount(final Integer beidiShoumaiCount) {
        this.beidiShoumaiCount = beidiShoumaiCount;
    }
    
    public Integer getXirongShoumaiCount() {
        return this.xirongShoumaiCount;
    }
    
    public void setXirongShoumaiCount(final Integer xirongShoumaiCount) {
        this.xirongShoumaiCount = xirongShoumaiCount;
    }
    
    public Integer getDongyiShoumaiCount() {
        return this.dongyiShoumaiCount;
    }
    
    public void setDongyiShoumaiCount(final Integer dongyiShoumaiCount) {
        this.dongyiShoumaiCount = dongyiShoumaiCount;
    }
    
    public Integer getBeidiQinmidu() {
        return this.beidiQinmidu;
    }
    
    public void setBeidiQinmidu(final Integer beidiQinmidu) {
        this.beidiQinmidu = beidiQinmidu;
    }
    
    public Integer getXirongQinmidu() {
        return this.xirongQinmidu;
    }
    
    public void setXirongQinmidu(final Integer xirongQinmidu) {
        this.xirongQinmidu = xirongQinmidu;
    }
    
    public Integer getDongyiQinmidu() {
        return this.dongyiQinmidu;
    }
    
    public void setDongyiQinmidu(final Integer dongyiQinmidu) {
        this.dongyiQinmidu = dongyiQinmidu;
    }
    
    public Integer getBeidiShoumaiSum() {
        return this.beidiShoumaiSum;
    }
    
    public void setBeidiShoumaiSum(final Integer beidiShoumaiSum) {
        this.beidiShoumaiSum = beidiShoumaiSum;
    }
    
    public Integer getXirongShoumaiSum() {
        return this.xirongShoumaiSum;
    }
    
    public void setXirongShoumaiSum(final Integer xirongShoumaiSum) {
        this.xirongShoumaiSum = xirongShoumaiSum;
    }
    
    public Integer getDongyiShoumaiSum() {
        return this.dongyiShoumaiSum;
    }
    
    public void setDongyiShoumaiSum(final Integer dongyiShoumaiSum) {
        this.dongyiShoumaiSum = dongyiShoumaiSum;
    }
    
    public String getIds() {
        return this.ids;
    }
    
    public void setIds(final String ids) {
        this.ids = ids;
    }
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Date getTryEndTime() {
        return this.tryEndTime;
    }
    
    public void setTryEndTime(final Date tryEndTime) {
        this.tryEndTime = tryEndTime;
    }
    
    public Integer getStage() {
        return this.stage;
    }
    
    public void setStage(final Integer stage) {
        this.stage = stage;
    }
    
    public Integer getTryWin() {
        return this.tryWin;
    }
    
    public void setTryWin(final Integer tryWin) {
        this.tryWin = tryWin;
    }
    
    public Integer getGeneralNum() {
        return this.generalNum;
    }
    
    public void setGeneralNum(final Integer generalNum) {
        this.generalNum = generalNum;
    }
    
    public Integer getPWin() {
        return this.pWin;
    }
    
    public void setPWin(final Integer pWin) {
        this.pWin = pWin;
    }
    
    public Integer getPForceId() {
        return this.pForceId;
    }
    
    public void setPForceId(final Integer pForceId) {
        this.pForceId = pForceId;
    }
    
    public Integer getPCityId() {
        return this.pCityId;
    }
    
    public void setPCityId(final Integer pCityId) {
        this.pCityId = pCityId;
    }
    
    public Integer getPId() {
        return this.pId;
    }
    
    public void setPId(final Integer pId) {
        this.pId = pId;
    }
    
    public Long getFarmInvestSum() {
        return this.farmInvestSum;
    }
    
    public void setFarmInvestSum(final Long farmInvestSum) {
        this.farmInvestSum = farmInvestSum;
    }
    
    public Integer getLv() {
        return this.lv;
    }
    
    public void setLv(final Integer lv) {
        this.lv = lv;
    }
    
    public String getNationIndivInfo() {
        return this.nationIndivInfo;
    }
    
    public void setNationIndivInfo(final String nationIndivInfo) {
        this.nationIndivInfo = nationIndivInfo;
    }
}
