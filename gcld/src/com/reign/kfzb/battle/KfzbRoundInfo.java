package com.reign.kfzb.battle;

import ast.gcldcore.fight.*;
import java.util.*;

public class KfzbRoundInfo
{
    public int roundNum;
    static final int STATE_RUNNING = 0;
    static final int STATE_ROUNDFINISHED = 1;
    static final int STATE_BATTLEFINISHED = 2;
    public int state;
    public StringBuilder battleMsg;
    public KfzbCampArmy attCampArmy;
    public KfzbCampArmy defCampArmy;
    public KfzbBattleArmy attBattleArmy;
    public KfzbBattleArmy defBattleArmy;
    public TroopData[][] troopData;
    public String[] reports;
    public int nextMinExeTime;
    public int nextMaxExeTime;
    public KfzbTacticInfo attTacticInfo;
    public KfzbTacticInfo defTacticInfo;
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
    public List<KfzbBattleArmy> attKilledList;
    public List<KfzbBattleArmy> attUpList;
    public List<KfzbBattleArmy> defKilledList;
    public List<KfzbBattleArmy> defUpList;
    public StringBuilder caculateDebugBuffer;
    public StringBuilder timePredicationBuffer;
    
    public KfzbRoundInfo(final int roundNum) {
        this.battleMsg = new StringBuilder();
        this.nextMinExeTime = 0;
        this.nextMaxExeTime = 2100;
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
        this.attKilledList = new ArrayList<KfzbBattleArmy>();
        this.attUpList = new ArrayList<KfzbBattleArmy>();
        this.defKilledList = new ArrayList<KfzbBattleArmy>();
        this.defUpList = new ArrayList<KfzbBattleArmy>();
        this.caculateDebugBuffer = new StringBuilder();
        this.timePredicationBuffer = new StringBuilder("move ahead:1400|st base:700|");
        this.roundNum = roundNum;
    }
}
