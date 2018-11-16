package com.reign.kf.match.sdata.domain;

import java.util.*;
import java.util.regex.*;

public class KfgzWorldStg
{
    public static final String SPLIT = ",";
    public static final String CONDITION_INTERVAL = "interval";
    public static final String CONDITION_STARTTIME = "starttime";
    int id;
    int stg_id;
    int city_id;
    String effectCities;
    String condition;
    int selfArmyId;
    int worldStgid;
    
    public int getId() {
        return this.id;
    }
    
    public void setId(final int id) {
        this.id = id;
    }
    
    public int getStg_id() {
        return this.stg_id;
    }
    
    public void setStg_id(final int stg_id) {
        this.stg_id = stg_id;
    }
    
    public int getCity_id() {
        return this.city_id;
    }
    
    public void setCity_id(final int city_id) {
        this.city_id = city_id;
    }
    
    public String getEffectCities() {
        return this.effectCities;
    }
    
    public void setEffectCities(final String effectCities) {
        this.effectCities = effectCities;
    }
    
    public String getCondition() {
        return this.condition;
    }
    
    public void setCondition(final String condition) {
        this.condition = condition;
    }
    
    public int getSelfArmyId() {
        return this.selfArmyId;
    }
    
    public void setSelfArmyId(final int selfArmyId) {
        this.selfArmyId = selfArmyId;
    }
    
    public int getWorldStgid() {
        return this.worldStgid;
    }
    
    public void setWorldStgid(final int worldStgid) {
        this.worldStgid = worldStgid;
    }
    
    public List<String[]> getConditionInfo() {
        final List<String[]> res = new ArrayList<String[]>();
        if (this.condition == null) {
            return res;
        }
        final String[] cs = this.condition.split(",");
        String[] array;
        for (int length = (array = cs).length, i = 0; i < length; ++i) {
            final String con = array[i];
            final String ps = "(\\w+):(\\w+)";
            if (con.matches(ps)) {
                final Pattern pt = Pattern.compile(ps);
                final Matcher ma = pt.matcher(con);
                if (ma.find()) {
                    final String key = ma.group(1);
                    final String value = ma.group(2);
                    res.add(new String[] { key, value });
                }
            }
        }
        return res;
    }
}
