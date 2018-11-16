package com.reign.gcld.battle.common;

import java.sql.*;
import java.util.*;

public class PlayerRoundDbObj
{
    public String battleId;
    public long battleRoundNum;
    public Date exeTime;
    public int playerId;
    public String playerName;
    public Map<Integer, BattleDrop> roundDropMap;
    Map<Integer, GeneralRoundObj> gObjSet;
    
    public PlayerRoundDbObj() {
        this.battleId = null;
        this.battleRoundNum = 0L;
        this.exeTime = null;
        this.playerId = 0;
        this.playerName = null;
        this.roundDropMap = null;
        this.gObjSet = new HashMap<Integer, GeneralRoundObj>();
    }
}
