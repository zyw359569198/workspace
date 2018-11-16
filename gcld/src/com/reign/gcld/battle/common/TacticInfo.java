package com.reign.gcld.battle.common;

import java.util.*;
import com.reign.gcld.battle.scene.*;
import com.reign.gcld.battle.reward.*;

public class TacticInfo
{
    public boolean executed;
    public boolean beStop;
    public int tacticId;
    public int specialType;
    public int tacticDisplayId;
    public int tacticNameId;
    public String tacticBasicPic;
    public String tacticStr;
    public String specialEffect;
    public String columnStr;
    public int firstCReduce;
    public int allCReduce;
    public int allBarbarainReduce;
    public boolean attacked_guanyu;
    public Map<CampArmy, Integer> reduceMap;
    public IReward reward;
    public BattleDrop tacticDrop;
    public boolean zfBJ;
    public boolean zfJB;
    
    public TacticInfo() {
        this.executed = false;
        this.beStop = false;
        this.tacticId = 0;
        this.specialType = 0;
        this.tacticNameId = 0;
        this.tacticBasicPic = null;
        this.firstCReduce = 0;
        this.allCReduce = 0;
        this.allBarbarainReduce = 0;
        this.attacked_guanyu = false;
        this.tacticDrop = null;
        this.zfBJ = false;
        this.zfJB = false;
    }
}
