package com.reign.kf.match.common;

import java.util.concurrent.atomic.*;
import java.util.*;

public class BattleData
{
    public AtomicInteger reportSn;
    public AtomicInteger campNum;
    AtomicInteger battleNum;
    public List<BattleArmy[]> attQList;
    public List<BattleArmy[]> defQList;
    public int attArmyIndex;
    public int defArmyIndex;
    public AtomicInteger attQNum;
    public AtomicInteger defQNum;
    public List<CampArmy> attCampList;
    public List<CampArmy> defCampList;
    public boolean attForeWin;
    public boolean defForeWin;
    public int attArmyHpLoss;
    public int defArmyHpLoss;
    public int attArmyHpKill;
    public int defArmyHpKill;
    public int attTotalNum;
    public int defTotalNum;
    public int attCurNum;
    public int defCurNum;
    public int totalTime;
    public int winSide;
    
    public BattleData() {
        this.reportSn = new AtomicInteger(1);
        this.campNum = new AtomicInteger(0);
        this.battleNum = new AtomicInteger(0);
        this.attQList = new ArrayList<BattleArmy[]>(8);
        this.defQList = new ArrayList<BattleArmy[]>(8);
        this.attArmyIndex = 0;
        this.defArmyIndex = 0;
        this.attQNum = new AtomicInteger(0);
        this.defQNum = new AtomicInteger(0);
        this.attTotalNum = 0;
        this.defTotalNum = 0;
        this.attCurNum = 0;
        this.defCurNum = 0;
        this.totalTime = 0;
        this.winSide = 0;
    }
}
