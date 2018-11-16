package com.reign.kfgz.dto;

import java.util.*;
import com.reign.kfgz.comm.*;
import com.reign.framework.json.*;

public class KfgzOfficeToken
{
    public static final int STATE_CLOSE = 2;
    public static final long TOKENCD = 300000L;
    int officerId;
    String officerName;
    String playerName;
    int nation;
    int forceId;
    long startTime;
    int foodAdd;
    int cityId;
    int state;
    Set<Integer> usedCIdSet;
    
    public KfgzOfficeToken() {
        this.usedCIdSet = new HashSet<Integer>();
    }
    
    public int getOfficerId() {
        return this.officerId;
    }
    
    public void setOfficerId(final int officerId) {
        this.officerId = officerId;
    }
    
    public String getOfficerName() {
        return this.officerName;
    }
    
    public void setOfficerName(final String officerName) {
        this.officerName = officerName;
    }
    
    public String getPlayerName() {
        return this.playerName;
    }
    
    public void setPlayerName(final String playerName) {
        this.playerName = playerName;
    }
    
    public int getNation() {
        return this.nation;
    }
    
    public void setNation(final int nation) {
        this.nation = nation;
    }
    
    public int getForceId() {
        return this.forceId;
    }
    
    public void setForceId(final int forceId) {
        this.forceId = forceId;
    }
    
    public long getStartTime() {
        return this.startTime;
    }
    
    public void setStartTime(final long startTime) {
        this.startTime = startTime;
    }
    
    public int getFoodAdd() {
        return this.foodAdd;
    }
    
    public void setFoodAdd(final int foodAdd) {
        this.foodAdd = foodAdd;
    }
    
    public int getState() {
        return this.state;
    }
    
    public void setState(final int state) {
        this.state = state;
    }
    
    public int getCityId() {
        return this.cityId;
    }
    
    public void setCityId(final int cityId) {
        this.cityId = cityId;
    }
    
    public boolean isEffect() {
        final long now = System.currentTimeMillis();
        return this.state != 2 && this.startTime + 300000L > now;
    }
    
    public long getTokenCD() {
        final long now = System.currentTimeMillis();
        return this.startTime + 300000L - now;
    }
    
    public int getKey() {
        return KfgzConstants.getWorldForcedKey(this.cityId, this.forceId);
    }
    
    public boolean playerhasUsed(final int cId) {
        return this.usedCIdSet.contains(cId);
    }
    
    public void addNewUseCId(final int competitorId) {
        this.usedCIdSet.add(competitorId);
    }
    
    public byte[] createJsonObject() {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startObject("officeToken");
        doc.createElement("cityId", this.getCityId());
        doc.createElement("playerName", this.getPlayerName());
        doc.createElement("officerName", this.getOfficerName());
        doc.createElement("cd", this.getTokenCD());
        doc.createElement("foodAdd", this.getFoodAdd());
        doc.createElement("forceId", this.getForceId());
        doc.createElement("state", this.isEffect() ? 1 : 2);
        doc.endObject();
        doc.endObject();
        return doc.toByte();
    }
}
