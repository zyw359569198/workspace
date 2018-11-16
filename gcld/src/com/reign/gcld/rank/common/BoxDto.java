package com.reign.gcld.rank.common;

import com.reign.gcld.common.*;
import com.reign.gcld.battle.reward.*;
import com.reign.util.*;

public class BoxDto
{
    private int boxType;
    private int rewardType;
    private int number;
    private int quality;
    private String boxIntro;
    private int killNum;
    private int boxId;
    
    public int getBoxId() {
        return this.boxId;
    }
    
    public void setBoxId(final int boxId) {
        this.boxId = boxId;
    }
    
    public int getKillNum() {
        return this.killNum;
    }
    
    public void setKillNum(final int killNum) {
        this.killNum = killNum;
    }
    
    public String getBoxIntro() {
        if (this.rewardType == -1 || this.rewardType == 0) {
            return "";
        }
        MessageFormatter.format(LocalMessages.BOX_INTRO, new Object[] { this.killNum, RewardType.getTypeWord(this.rewardType), this.number });
        return this.boxIntro;
    }
    
    public void setBoxIntro(final String boxIntro) {
        this.boxIntro = boxIntro;
    }
    
    public int getRewardType() {
        return this.rewardType;
    }
    
    public void setRewardType(final int type) {
        this.rewardType = type;
    }
    
    public int getNumber() {
        return this.number;
    }
    
    public void setNumber(final int number) {
        this.number = number;
    }
    
    public int getQuality() {
        return this.quality;
    }
    
    public void setQuality(final int quality) {
        this.quality = quality;
    }
    
    public int getBoxType() {
        return this.boxType;
    }
    
    public void setBoxType(final int boxType) {
        this.boxType = boxType;
    }
    
    public void setNumber(final String string) {
        this.setNumber(Integer.parseInt(string));
    }
    
    public void setBoxRewardType(final String string) {
        this.setRewardType(RewardType.getTypeInt(string));
    }
}
