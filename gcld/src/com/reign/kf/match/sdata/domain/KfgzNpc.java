package com.reign.kf.match.sdata.domain;

import com.reign.kfgz.dto.*;
import javax.persistence.*;
import java.util.*;
import org.apache.commons.lang.*;

public class KfgzNpc
{
    public static final String INICONDITION_SPLIT1 = ",";
    public static final String INICONDITION_SPLIT2 = ":";
    public static final String ARMYINFO_SPLIT = ",";
    public static final String AI_SPLIT1 = ";";
    public static final String AI_SPLIT2 = ":";
    public static final String CHOOSEN_ROAD_SPLIT = ",";
    public static final String ROAD_SPLIT = "-";
    int id;
    String armyInfo;
    String armyAI;
    String iniCondition;
    int iniCityPos;
    int forceId;
    int worldNpcid;
    public static final String INI_CONDITION_STARTTIME = "starttime";
    public static final String INI_CONDITION_INTERVAL = "interval";
    public static final String AI_CHOOSEROADLIST = "chooseroadlist";
    
    public int getId() {
        return this.id;
    }
    
    public void setId(final int id) {
        this.id = id;
    }
    
    public String getArmyInfo() {
        return this.armyInfo;
    }
    
    public void setArmyInfo(final String armyInfo) {
        this.armyInfo = armyInfo;
    }
    
    public String getArmyAI() {
        return this.armyAI;
    }
    
    public void setArmyAI(final String armyAI) {
        this.armyAI = armyAI;
    }
    
    public String getIniCondition() {
        return this.iniCondition;
    }
    
    public void setIniCondition(final String iniCondition) {
        this.iniCondition = iniCondition;
    }
    
    public int getIniCityPos() {
        return this.iniCityPos;
    }
    
    public void setIniCityPos(final int iniCityPos) {
        this.iniCityPos = iniCityPos;
    }
    
    public int getForceId() {
        return this.forceId;
    }
    
    public void setForceId(final int forceId) {
        this.forceId = forceId;
    }
    
    public int getWorldNpcid() {
        return this.worldNpcid;
    }
    
    public void setWorldNpcid(final int worldNpcid) {
        this.worldNpcid = worldNpcid;
    }
    
    @Transient
    public Long[] getExDelay(final KfgzBaseInfo baseInfo) {
        final long startTime = baseInfo.getGzStartTime().getTime();
        final long endTime = baseInfo.getGzEndTime().getTime();
        final Map<String, String> iniInfoMap = this.getNormalSplitInfo();
        final String iniTimeMin = iniInfoMap.get("starttime");
        final String interval = iniInfoMap.get("interval");
        long cd = 0L;
        if (interval != null) {
            cd = Integer.valueOf(interval) * 60000;
        }
        long startInterval = 0L;
        if (iniTimeMin != null) {
            startInterval = Integer.valueOf(iniTimeMin) * 60000;
        }
        if (cd <= 0L) {
            final Long[] time = { startTime + startInterval };
            return time;
        }
        final int len = (int)((endTime - startTime - startInterval) / cd) + 1;
        if (len <= 0) {
            return null;
        }
        final Long[] time2 = new Long[len];
        for (int i = 0; i < len; ++i) {
            time2[i] = startTime + startInterval + cd * i;
        }
        return time2;
    }
    
    @Transient
    public Map<String, String> getNormalSplitInfo() {
        final Map<String, String> map = new HashMap<String, String>();
        if (StringUtils.isBlank(this.iniCondition)) {
            return map;
        }
        final String[] ss = this.iniCondition.split(",");
        String[] array;
        for (int length = (array = ss).length, i = 0; i < length; ++i) {
            final String ss2 = array[i];
            final String[] ss3 = ss2.split(":");
            map.put(ss3[0], ss3[1]);
        }
        return map;
    }
    
    @Transient
    public Map<String, String> getAIInfoMap() {
        final Map<String, String> map = new HashMap<String, String>();
        if (StringUtils.isBlank(this.armyAI)) {
            return map;
        }
        final String[] ss = this.armyAI.split(";");
        String[] array;
        for (int length = (array = ss).length, i = 0; i < length; ++i) {
            final String ss2 = array[i];
            final String[] ss3 = ss2.split(":");
            map.put(ss3[0], ss3[1]);
        }
        return map;
    }
}
