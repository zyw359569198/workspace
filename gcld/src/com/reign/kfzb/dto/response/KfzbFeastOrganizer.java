package com.reign.kfzb.dto.response;

import org.codehaus.jackson.annotate.*;

@JsonAutoDetect
public class KfzbFeastOrganizer
{
    int goldAddFeastTimes;
    int feastTimes;
    int goldFeastTime;
    int pos;
    int weiNum;
    int shuNum;
    int wuNum;
    
    public int getFeastTimes() {
        return this.feastTimes;
    }
    
    public void setFeastTimes(final int feastTimes) {
        this.feastTimes = feastTimes;
    }
    
    public int getPos() {
        return this.pos;
    }
    
    public void setPos(final int pos) {
        this.pos = pos;
    }
    
    public int getGoldAddFeastTimes() {
        return this.goldAddFeastTimes;
    }
    
    public void setGoldAddFeastTimes(final int goldAddFeastTimes) {
        this.goldAddFeastTimes = goldAddFeastTimes;
    }
    
    public int getGoldFeastTime() {
        return this.goldFeastTime;
    }
    
    public void setGoldFeastTime(final int goldFeastTime) {
        this.goldFeastTime = goldFeastTime;
    }
    
    public int getWeiNum() {
        return this.weiNum;
    }
    
    public void setWeiNum(final int weiNum) {
        this.weiNum = weiNum;
    }
    
    public int getShuNum() {
        return this.shuNum;
    }
    
    public void setShuNum(final int shuNum) {
        this.shuNum = shuNum;
    }
    
    public int getWuNum() {
        return this.wuNum;
    }
    
    public void setWuNum(final int wuNum) {
        this.wuNum = wuNum;
    }
    
    @JsonIgnore
    public int getGoldFeastRemainTime() {
        return this.goldAddFeastTimes - this.goldFeastTime;
    }
}
