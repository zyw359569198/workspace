package com.reign.kfgz.battle;

import com.reign.kfgz.comm.*;
import java.util.*;

public class KfTacticInfo
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
    public Map<KfCampArmy, Integer> reduceMap;
    public List<KfBattleArmy> defArmyList;
    public boolean attacked_guanyu;
    public boolean zfBJ;
    public boolean zfJB;
    
    public KfTacticInfo() {
        this.executed = false;
        this.beStop = false;
        this.tacticId = 0;
        this.specialType = 0;
        this.tacticNameId = 0;
        this.tacticBasicPic = null;
        this.defArmyList = new ArrayList<KfBattleArmy>();
        this.attacked_guanyu = false;
        this.zfBJ = false;
        this.zfJB = false;
    }
}
