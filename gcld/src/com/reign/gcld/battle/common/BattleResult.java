package com.reign.gcld.battle.common;

import com.reign.gcld.slave.common.*;
import java.util.*;

public class BattleResult
{
    public Map<Integer, String> oMap;
    public OccupyMineInfo mineInfo;
    public Map<Integer, SlaveInfo> slaveMap;
    public int dropGId;
    public String dropGName;
    public int dropGQuality;
    public String dropGPic;
    public int dropGget;
    public int dropGType;
    public String gTroopName;
    public int gTroopQuality;
    public int cType;
    public String cityName;
    
    public BattleResult() {
        this.oMap = new HashMap<Integer, String>();
        this.mineInfo = null;
        this.slaveMap = null;
        this.dropGget = 0;
        this.cityName = null;
    }
}
