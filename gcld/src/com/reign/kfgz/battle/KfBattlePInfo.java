package com.reign.kfgz.battle;

import com.reign.kf.match.sdata.common.*;
import java.util.*;

public class KfBattlePInfo
{
    private static final long serialVersionUID = -8783437271194511140L;
    boolean isAttSide;
    int competitorId;
    int curStrategy;
    int maxKillG;
    int killTotal;
    int lostTotal;
    int rbType;
    int rbTotal;
    int rbTop;
    Map<Integer, BattleDrop> dropMap;
    
    public KfBattlePInfo() {
        this.isAttSide = false;
        this.maxKillG = 0;
        this.killTotal = 0;
        this.lostTotal = 0;
        this.rbType = 0;
        this.rbTotal = 0;
        this.rbTop = 0;
        this.dropMap = new HashMap<Integer, BattleDrop>();
    }
    
    public KfBattlePInfo(final int competitorId, final boolean isAttSide) {
        this.isAttSide = false;
        this.maxKillG = 0;
        this.killTotal = 0;
        this.lostTotal = 0;
        this.rbType = 0;
        this.rbTotal = 0;
        this.rbTop = 0;
        this.dropMap = new HashMap<Integer, BattleDrop>();
        this.competitorId = competitorId;
        this.isAttSide = isAttSide;
    }
    
    public void addDropMap(final Map<Integer, BattleDrop> addMap) {
        for (final Map.Entry<Integer, BattleDrop> entry : addMap.entrySet()) {
            this.addDrop(entry.getValue());
        }
    }
    
    public void addDropAnd(final BattleDropAnd battleDropAnd) {
        for (final Map.Entry<Integer, BattleDrop> entry : battleDropAnd.getDropAndMap().entrySet()) {
            this.addDrop(entry.getValue());
        }
    }
    
    public void addDrop(final BattleDrop battleDrop) {
        final Integer key = battleDrop.type;
        if (this.dropMap.containsKey(key)) {
            final BattleDrop battleDrop2 = this.dropMap.get(key);
            battleDrop2.num += battleDrop.num;
        }
        else {
            this.dropMap.put(key, new BattleDrop(battleDrop));
        }
    }
    
    public int getMaxKillG() {
        return this.maxKillG;
    }
    
    public void setMaxKillG(final int maxKillG) {
        this.maxKillG = maxKillG;
    }
    
    public int getRbType() {
        return this.rbType;
    }
    
    public void setRbType(final int rbType) {
        this.rbType = rbType;
    }
    
    public int getRbTop() {
        return this.rbTop;
    }
    
    public void setRbTop(final int rbTop) {
        this.rbTop = rbTop;
    }
    
    public int getCompetitorId() {
        return this.competitorId;
    }
    
    public void setCompetitorId(final int competitorId) {
        this.competitorId = competitorId;
    }
    
    public boolean isAttSide() {
        return this.isAttSide;
    }
    
    public void setAttSide(final boolean isAttSide) {
        this.isAttSide = isAttSide;
    }
    
    public int getKillTotal() {
        return this.killTotal;
    }
    
    public void setKillTotal(final int killTotal) {
        this.killTotal = killTotal;
    }
    
    public int getLostTotal() {
        return this.lostTotal;
    }
    
    public void setLostTotal(final int lostTotal) {
        this.lostTotal = lostTotal;
    }
    
    public int getRbTotal() {
        return this.rbTotal;
    }
    
    public void setRbTotal(final int rbTotal) {
        this.rbTotal = rbTotal;
    }
}
