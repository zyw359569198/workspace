package com.reign.gcld.player.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class PlayerAttribute implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private Integer maxStoreNum;
    private String isAreaNew;
    private String functionId;
    private Integer fullRecruitNum;
    private Integer enterCount;
    private Date lastResetTime;
    private Integer battleWinTimes;
    private Integer battleRewardTimes;
    private Integer recruitToken;
    private Integer payPoint;
    private Date lastGiftTime;
    private Date ironForcemineTime;
    private Date gemForcemineTime;
    private Date blackMarketCd;
    private Integer ironConvert;
    private Integer guideId;
    private Integer ironDisplay;
    private Integer techOpen;
    private Integer techResearch;
    private Integer intimacy;
    private Integer freeConstructionNum;
    private Integer hasBandit;
    private Integer kidnapper;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getMaxStoreNum() {
        return this.maxStoreNum;
    }
    
    public void setMaxStoreNum(final Integer maxStoreNum) {
        this.maxStoreNum = maxStoreNum;
    }
    
    public String getIsAreaNew() {
        return this.isAreaNew;
    }
    
    public void setIsAreaNew(final String isAreaNew) {
        this.isAreaNew = isAreaNew;
    }
    
    public String getFunctionId() {
        return this.functionId;
    }
    
    public void setFunctionId(final String functionId) {
        this.functionId = functionId;
    }
    
    public Integer getFullRecruitNum() {
        return this.fullRecruitNum;
    }
    
    public void setFullRecruitNum(final Integer fullRecruitNum) {
        this.fullRecruitNum = fullRecruitNum;
    }
    
    public Integer getEnterCount() {
        return this.enterCount;
    }
    
    public void setEnterCount(final Integer enterCount) {
        this.enterCount = enterCount;
    }
    
    public Date getLastResetTime() {
        return this.lastResetTime;
    }
    
    public void setLastResetTime(final Date lastResetTime) {
        this.lastResetTime = lastResetTime;
    }
    
    public Integer getBattleWinTimes() {
        return this.battleWinTimes;
    }
    
    public void setBattleWinTimes(final Integer battleWinTimes) {
        this.battleWinTimes = battleWinTimes;
    }
    
    public Integer getBattleRewardTimes() {
        return this.battleRewardTimes;
    }
    
    public void setBattleRewardTimes(final Integer battleRewardTimes) {
        this.battleRewardTimes = battleRewardTimes;
    }
    
    public Integer getRecruitToken() {
        return this.recruitToken;
    }
    
    public void setRecruitToken(final Integer recruitToken) {
        this.recruitToken = recruitToken;
    }
    
    public Integer getPayPoint() {
        return this.payPoint;
    }
    
    public void setPayPoint(final Integer payPoint) {
        this.payPoint = payPoint;
    }
    
    public Date getLastGiftTime() {
        return this.lastGiftTime;
    }
    
    public void setLastGiftTime(final Date lastGiftTime) {
        this.lastGiftTime = lastGiftTime;
    }
    
    public Date getIronForcemineTime() {
        return this.ironForcemineTime;
    }
    
    public void setIronForcemineTime(final Date ironForcemineTime) {
        this.ironForcemineTime = ironForcemineTime;
    }
    
    public Date getGemForcemineTime() {
        return this.gemForcemineTime;
    }
    
    public void setGemForcemineTime(final Date gemForcemineTime) {
        this.gemForcemineTime = gemForcemineTime;
    }
    
    public Date getBlackMarketCd() {
        return this.blackMarketCd;
    }
    
    public void setBlackMarketCd(final Date blackMarketCd) {
        this.blackMarketCd = blackMarketCd;
    }
    
    public Integer getIronConvert() {
        return this.ironConvert;
    }
    
    public void setIronConvert(final Integer ironConvert) {
        this.ironConvert = ironConvert;
    }
    
    public Integer getGuideId() {
        return this.guideId;
    }
    
    public void setGuideId(final Integer guideId) {
        this.guideId = guideId;
    }
    
    public Integer getIronDisplay() {
        return this.ironDisplay;
    }
    
    public void setIronDisplay(final Integer ironDisplay) {
        this.ironDisplay = ironDisplay;
    }
    
    public Integer getTechOpen() {
        return this.techOpen;
    }
    
    public void setTechOpen(final Integer techOpen) {
        this.techOpen = techOpen;
    }
    
    public Integer getTechResearch() {
        return this.techResearch;
    }
    
    public void setTechResearch(final Integer techResearch) {
        this.techResearch = techResearch;
    }
    
    public Integer getIntimacy() {
        return this.intimacy;
    }
    
    public void setIntimacy(final Integer intimacy) {
        this.intimacy = intimacy;
    }
    
    public Integer getFreeConstructionNum() {
        return this.freeConstructionNum;
    }
    
    public void setFreeConstructionNum(final Integer freeConstructionNum) {
        this.freeConstructionNum = freeConstructionNum;
    }
    
    public Integer getHasBandit() {
        return this.hasBandit;
    }
    
    public void setHasBandit(final Integer hasBandit) {
        this.hasBandit = hasBandit;
    }
    
    public Integer getKidnapper() {
        return this.kidnapper;
    }
    
    public void setKidnapper(final Integer kidnapper) {
        this.kidnapper = kidnapper;
    }
}
