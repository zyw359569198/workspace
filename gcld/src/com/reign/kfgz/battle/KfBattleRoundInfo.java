package com.reign.kfgz.battle;

import com.reign.kfgz.comm.*;
import ast.gcldcore.fight.*;
import java.util.*;

public class KfBattleRoundInfo
{
    public int roundNum;
    static final int STATE_RUNNING = 0;
    static final int STATE_ROUNDFINISHED = 1;
    static final int STATE_BATTLEFINISHED = 2;
    public int state;
    public StringBuilder battleMsg;
    public KfCampArmy attCampArmy;
    public KfCampArmy defCampArmy;
    public KfBattleArmy attBattleArmy;
    public KfBattleArmy defBattleArmy;
    public TroopData[][] troopData;
    public String[] reports;
    public int nextMinExeTime;
    public int nextMaxExeTime;
    public KfTacticInfo attTacticInfo;
    public KfTacticInfo defTacticInfo;
    public RoundReward attRoundReward;
    public RoundReward defRoundReward;
    public int win;
    public boolean attFirstRowKilled;
    public boolean defFirstRowKilled;
    public int attRemain;
    public int defRemain;
    public int attLost;
    public int defLost;
    public boolean killDefG;
    public boolean killAttG;
    public boolean needPushReport13;
    public int tacticStrategyResult;
    public int attStrategyLost;
    public int defStrategyLost;
    public boolean attRebound;
    public boolean defRebound;
    public List<KfBattleArmy> attKilledList;
    public List<KfBattleArmy> attUpList;
    public List<KfBattleArmy> defKilledList;
    public List<KfBattleArmy> defUpList;
    public StringBuilder caculateDebugBuffer;
    public StringBuilder timePredicationBuffer;
    
    public KfBattleRoundInfo(final int roundNum) {
        this.battleMsg = new StringBuilder();
        this.nextMinExeTime = 0;
        this.nextMaxExeTime = 2100;
        this.attRoundReward = new RoundReward();
        this.defRoundReward = new RoundReward();
        this.attFirstRowKilled = false;
        this.defFirstRowKilled = false;
        this.attRemain = 0;
        this.defRemain = 0;
        this.attLost = 0;
        this.defLost = 0;
        this.killDefG = false;
        this.killAttG = false;
        this.needPushReport13 = false;
        this.tacticStrategyResult = 0;
        this.attStrategyLost = 0;
        this.defStrategyLost = 0;
        this.attRebound = false;
        this.defRebound = false;
        this.attKilledList = new ArrayList<KfBattleArmy>();
        this.attUpList = new ArrayList<KfBattleArmy>();
        this.defKilledList = new ArrayList<KfBattleArmy>();
        this.defUpList = new ArrayList<KfBattleArmy>();
        this.caculateDebugBuffer = new StringBuilder();
        this.timePredicationBuffer = new StringBuilder("move ahead:1400|st base:700|");
        this.roundNum = roundNum;
    }
    
    public int getAllKill(final boolean isAtt) {
        if (!isAtt) {
            int attTacticLost = 0;
            if (this.defTacticInfo != null) {
                attTacticLost = this.defTacticInfo.firstCReduce;
            }
            return this.attLost + this.attStrategyLost + attTacticLost;
        }
        int defTacticLost = 0;
        if (this.attTacticInfo != null) {
            defTacticLost = this.attTacticInfo.firstCReduce;
        }
        return this.defLost + this.defStrategyLost + defTacticLost;
    }
    
    public Map<KfCampArmy, KfHpChangeInfo> getRoundHpChangeInfo() {
        final Map<KfCampArmy, KfHpChangeInfo> map = new HashMap<KfCampArmy, KfHpChangeInfo>();
        KfHpChangeInfo hcAttInfo = map.get(this.attCampArmy);
        if (hcAttInfo == null) {
            hcAttInfo = new KfHpChangeInfo();
            map.put(this.attCampArmy, hcAttInfo);
        }
        hcAttInfo.setKilled(this.defLost + this.defStrategyLost);
        hcAttInfo.setBeKilledCa(this.defCampArmy);
        hcAttInfo.setHpLost(hcAttInfo.getHpLost() + this.attLost + this.attStrategyLost);
        KfHpChangeInfo hcDefInfo = map.get(this.defCampArmy);
        if (hcDefInfo == null) {
            hcDefInfo = new KfHpChangeInfo();
            map.put(this.defCampArmy, hcDefInfo);
        }
        hcDefInfo.setKilled(this.attLost + this.attStrategyLost);
        hcDefInfo.setBeKilledCa(this.attCampArmy);
        hcDefInfo.setHpLost(hcDefInfo.getHpLost() + this.defLost + this.defStrategyLost);
        if (this.attTacticInfo != null) {
            final int attTacticKilled = this.attTacticInfo.allCReduce;
            hcAttInfo = map.get(this.attCampArmy);
            if (hcAttInfo == null) {
                hcAttInfo = new KfHpChangeInfo();
                map.put(this.attCampArmy, hcAttInfo);
            }
            hcAttInfo.setKilled(hcAttInfo.getKilled() + attTacticKilled);
            if (this.attTacticInfo.reduceMap != null) {
                for (final Map.Entry<KfCampArmy, Integer> entry : this.attTacticInfo.reduceMap.entrySet()) {
                    final KfCampArmy defCa = entry.getKey();
                    final int lostHp = entry.getValue();
                    KfHpChangeInfo hcInfo = map.get(defCa);
                    if (hcInfo == null) {
                        hcInfo = new KfHpChangeInfo();
                        map.put(defCa, hcInfo);
                    }
                    hcInfo.setBeKilledCa(this.attCampArmy);
                    hcInfo.setHpLost(hcInfo.getHpLost() + lostHp);
                }
            }
        }
        if (this.defTacticInfo != null) {
            final int defTacticKilled = this.defTacticInfo.allCReduce;
            hcDefInfo = map.get(this.defCampArmy);
            if (hcDefInfo == null) {
                hcDefInfo = new KfHpChangeInfo();
                map.put(this.attCampArmy, hcDefInfo);
            }
            hcDefInfo.setKilled(hcDefInfo.getKilled() + defTacticKilled);
            if (this.defTacticInfo.reduceMap != null) {
                for (final Map.Entry<KfCampArmy, Integer> entry : this.defTacticInfo.reduceMap.entrySet()) {
                    final KfCampArmy attCa = entry.getKey();
                    final int lostHp = entry.getValue();
                    KfHpChangeInfo hcInfo = map.get(attCa);
                    if (hcInfo == null) {
                        hcInfo = new KfHpChangeInfo();
                        map.put(attCa, hcInfo);
                    }
                    hcInfo.setBeKilledCa(this.defCampArmy);
                    hcInfo.setHpLost(hcInfo.getHpLost() + lostHp);
                }
            }
        }
        return map;
    }
}
