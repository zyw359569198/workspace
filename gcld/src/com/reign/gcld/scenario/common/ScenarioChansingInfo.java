package com.reign.gcld.scenario.common;

import com.reign.framework.json.*;
import org.apache.commons.lang.*;
import com.reign.gcld.battle.common.*;

public class ScenarioChansingInfo implements Cloneable
{
    private String tommyPic;
    private String jennyPic;
    private int tommyCity;
    private int jenneyCity;
    private int jenneyBlood;
    private int[] tommyCities;
    private int[] jennyCities;
    private long nextTommyTime;
    private long nextJennyTime;
    private int totalTime;
    private int state;
    
    public String getTommyPic() {
        return this.tommyPic;
    }
    
    public void setTommyPic(final String tommyPic) {
        this.tommyPic = tommyPic;
    }
    
    public String getJennyPic() {
        return this.jennyPic;
    }
    
    public void setJennyPic(final String jennyPic) {
        this.jennyPic = jennyPic;
    }
    
    public int getTotalTime() {
        return this.totalTime;
    }
    
    public void setTotalTime(final int totalTime) {
        this.totalTime = totalTime;
    }
    
    public long getNextTommyTime() {
        return this.nextTommyTime;
    }
    
    public void setNextTommyTime(final long nextTommyTime) {
        this.nextTommyTime = nextTommyTime;
    }
    
    public long getNextJennyTime() {
        return this.nextJennyTime;
    }
    
    public void setNextJennyTime(final long nextJennyTime) {
        this.nextJennyTime = nextJennyTime;
    }
    
    public int getState() {
        return this.state;
    }
    
    public void setState(final int state) {
        this.state = state;
    }
    
    public int[] getTommyCities() {
        return this.tommyCities;
    }
    
    public void setTommyCities(final int[] tommyCities) {
        this.tommyCities = tommyCities;
    }
    
    public int[] getJennyCities() {
        return this.jennyCities;
    }
    
    public void setJennyCities(final int[] jennyCities) {
        this.jennyCities = jennyCities;
    }
    
    public int getTommyCity() {
        return this.tommyCity;
    }
    
    public void setTommyCity(final int tommyCity) {
        this.tommyCity = tommyCity;
    }
    
    public int getJenneyCity() {
        return this.jenneyCity;
    }
    
    public void setJenneyCity(final int jenneyCity) {
        this.jenneyCity = jenneyCity;
    }
    
    public int getJenneyBlood() {
        return this.jenneyBlood;
    }
    
    public void setJenneyBlood(final int jenneyBlood) {
        this.jenneyBlood = jenneyBlood;
    }
    
    public int getJennyCityId() {
        return this.jennyCities[this.jenneyCity];
    }
    
    public int getTommyCityId() {
        return this.tommyCities[this.tommyCity];
    }
    
    public void appendChasingInfo(final long lastTime, final JsonDocument doc) {
        try {
            final long now = System.currentTimeMillis();
            doc.createElement("nextTommyTime", this.getNextTommyTime() - now);
            doc.createElement("nextJennyTime", this.getNextJennyTime() - now);
            doc.createElement("blood", this.getJenneyBlood());
            if (this.jennyCities != null && this.jennyCities.length > 0) {
                doc.createElement("jennyCity", this.getJennyCityId());
                if (!StringUtils.isBlank(this.jennyPic)) {
                    doc.createElement("jennyPic", this.jennyPic);
                }
                if (this.jenneyCity < this.jennyCities.length - 1) {
                    final int nextCityId = this.jennyCities[this.jenneyCity + 1];
                    doc.createElement("nextJennyCity", nextCityId);
                }
            }
            if (this.tommyCities != null && this.tommyCities.length > 0) {
                doc.createElement("tommyCity", this.getTommyCityId());
                if (!StringUtils.isBlank(this.tommyPic)) {
                    doc.createElement("tommyPic", this.tommyPic);
                }
                if (this.tommyCity < this.tommyCities.length - 1) {
                    final int nextCityId = this.tommyCities[this.tommyCity + 1];
                    doc.createElement("nextTommyCity", nextCityId);
                }
            }
            doc.createElement("leftTime", this.getTotalTime() * 1000L - lastTime);
        }
        catch (Exception e) {
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            errorSceneLog.error(this, e);
        }
    }
    
    @Override
    protected ScenarioChansingInfo clone() throws CloneNotSupportedException {
        return (ScenarioChansingInfo)super.clone();
    }
    
    public String getSimpleInfoToStore() {
        final StringBuffer sBuffer = new StringBuffer();
        sBuffer.append(this.jenneyBlood).append(",").append(this.jenneyCity).append(",").append(this.nextJennyTime).append(",").append(this.tommyCity).append(",").append(this.nextTommyTime);
        return sBuffer.toString();
    }
    
    public void restore(final String string, final long restoreTime) {
        if (StringUtils.isBlank(string)) {
            return;
        }
        try {
            final String[] single = string.split(",");
            final int blood = Integer.parseInt(single[0]);
            final int jenneyCity = Integer.parseInt(single[1]);
            final long nextJennyTime = Long.parseLong(single[2]);
            final int tommyCity = Integer.parseInt(single[3]);
            final long nextTommyTime = Long.parseLong(single[4]);
            this.tommyCity = tommyCity;
            this.nextTommyTime = nextTommyTime + restoreTime;
            this.jenneyBlood = blood;
            this.jenneyCity = jenneyCity;
            this.nextJennyTime = nextJennyTime + restoreTime;
        }
        catch (Exception e) {
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            errorSceneLog.error(e.getMessage());
            errorSceneLog.error(this, e);
        }
    }
}
