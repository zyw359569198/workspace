package com.reign.kf.match.common;

import ast.gcldcore.fight.*;

public class RoundInfo
{
    public CampArmy attCampArmy;
    public CampArmy defCampArmy;
    public BattleArmy[] attQ;
    public BattleArmy[] defQ;
    public TroopData[][] troopData;
    public String[] reports;
    public int tacticTimes;
    public TacticInfo attTacticInfo;
    public TacticInfo defTacticInfo;
    public int win;
    public int attRemain;
    public int defRemain;
    public int attLost;
    public int defLost;
    public boolean killDefG;
    public boolean killAttG;
    public RoundReward attRoundReward;
    public RoundReward defRoundReward;
    
    public RoundInfo() {
        this.attRemain = 0;
        this.defRemain = 0;
        this.attLost = 0;
        this.defLost = 0;
        this.killDefG = false;
        this.killAttG = false;
    }
}
