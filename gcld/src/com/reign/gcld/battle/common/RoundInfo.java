package com.reign.gcld.battle.common;

import com.reign.gcld.battle.scene.*;
import ast.gcldcore.fight.*;
import java.util.*;

public class RoundInfo
{
    public StringBuilder battleMsg;
    public CampArmy attCampArmy;
    public CampArmy defCampArmy;
    public BattleArmy nextAttBattleArmy;
    public BattleArmy nextDefBattleArmy;
    public BattleArmy attBattleArmy;
    public BattleArmy defBattleArmy;
    public TroopData[][] troopData;
    public String[] reports;
    public int nextMinExeTime;
    public int nextMaxExeTime;
    public TacticInfo attTacticInfo;
    public TacticInfo defTacticInfo;
    public int win;
    public boolean attFirstRowKilled;
    public boolean defFirstRowKilled;
    public int attRemain;
    public int defRemain;
    public int attLost;
    public int defLost;
    public boolean killDefG;
    public boolean killAttG;
    public int attRoundLostTotal;
    public int defRoundLostTotal;
    public boolean needPushReport13;
    public RoundReward attRoundReward;
    public RoundReward defRoundReward;
    public int tacticStrategyResult;
    public int attStrategyLost;
    public int defStrategyLost;
    public List<BattleArmy> attKilledList;
    public List<BattleArmy> defKilledList;
    public StringBuilder caculateDebugBuffer;
    public StringBuilder timePredicationBuffer;
    public boolean attRebound;
    public boolean defRebound;
    
    public RoundInfo() {
        this.battleMsg = new StringBuilder();
        this.nextMinExeTime = 2100;
        this.nextMaxExeTime = 0;
        this.attFirstRowKilled = false;
        this.defFirstRowKilled = false;
        this.attRemain = 0;
        this.defRemain = 0;
        this.attLost = 0;
        this.defLost = 0;
        this.killDefG = false;
        this.killAttG = false;
        this.attRoundLostTotal = 0;
        this.defRoundLostTotal = 0;
        this.needPushReport13 = false;
        this.tacticStrategyResult = 0;
        this.attStrategyLost = 0;
        this.defStrategyLost = 0;
        this.attKilledList = new ArrayList<BattleArmy>();
        this.defKilledList = new ArrayList<BattleArmy>();
        this.caculateDebugBuffer = new StringBuilder();
        this.timePredicationBuffer = new StringBuilder("move ahead:1400|st base:700|");
        this.attRebound = false;
        this.defRebound = false;
    }
}
