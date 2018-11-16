package com.reign.kfwd.battle;

import ast.gcldcore.fight.*;
import java.util.*;

public class KfwdRoundInfo
{
    public int roundNum;
    static final int STATE_RUNNING = 0;
    static final int STATE_ROUNDFINISHED = 1;
    static final int STATE_BATTLEFINISHED = 2;
    public int state;
    public StringBuilder battleMsg;
    public KfwdCampArmy attCampArmy;
    public KfwdCampArmy defCampArmy;
    public KfwdBattleArmy attBattleArmy;
    public KfwdBattleArmy defBattleArmy;
    public TroopData[][] troopData;
    public String[] reports;
    public int nextMinExeTime;
    public int nextMaxExeTime;
    public KfwdTacticInfo attTacticInfo;
    public KfwdTacticInfo defTacticInfo;
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
    public List<KfwdBattleArmy> attKilledList;
    public List<KfwdBattleArmy> attUpList;
    public List<KfwdBattleArmy> defKilledList;
    public List<KfwdBattleArmy> defUpList;
    public StringBuilder caculateDebugBuffer;
    public StringBuilder timePredicationBuffer;
    
    public KfwdRoundInfo(final int roundNum) {
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
        this.attKilledList = new ArrayList<KfwdBattleArmy>();
        this.attUpList = new ArrayList<KfwdBattleArmy>();
        this.defKilledList = new ArrayList<KfwdBattleArmy>();
        this.defUpList = new ArrayList<KfwdBattleArmy>();
        this.caculateDebugBuffer = new StringBuilder();
        this.timePredicationBuffer = new StringBuilder("move ahead:1400|st base:700|");
        this.roundNum = roundNum;
    }
}
