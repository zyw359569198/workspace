package com.reign.gcld.auto.common;

import java.util.*;

public class PlayerAutoBattleObj
{
    public static final long CHECK_GAP = 10000L;
    public static final int AUTO_COUNT_EACH_DAY = 5;
    public static final int DIE_COUNT_EACH_GENERAL = 3;
    public static final int PLAYER_AUTO_BATTLE_STATE_NORMAL = 0;
    public static final int PLAYER_AUTO_BATTLE_STATE_DOING = 1;
    public static final int PLAYER_AUTO_BATTLE_STATE_STOP_MANUAL = -1;
    public static final int PLAYER_AUTO_BATTLE_STATE_STOP_DEAD = -2;
    public static final int PLAYER_AUTO_BATTLE_STATE_STOP_CITYCHANGED = -3;
    public static final int PLAYER_AUTO_BATTLE_STATE_STOP_LACKFOOD = -4;
    public static final int PLAYER_AUTO_BATTLE_XUEZHAN_WIN = 1;
    public static final int PLAYER_AUTO_BATTLE_XUEZHAN_FAIL = 2;
    public static final int PLAYER_AUTO_BATTLE_JIANSHOU_WIN = 3;
    public static final int PLAYER_AUTO_BATTLE_JIANSHOU_FAIL = 4;
    public static final int PLAYER_AUTO_BATTLE_DEAD_TOOMUCH = 5;
    public static final int AUTO_TYPE_1_XUEZHAN = 1;
    public static final int AUTO_TYPE_2_JIANSHOU = 2;
    public int playerId;
    public int forceId;
    public int targetCityId;
    public int state;
    public int autoType;
    public List<Integer> cityIdlist;
    public int exp;
    public int lost;
    public long endTime;
    public int result;
    public long needCheckTime;
    
    public PlayerAutoBattleObj() {
        this.playerId = 0;
        this.forceId = 0;
        this.targetCityId = 0;
        this.state = 0;
        this.autoType = 0;
        this.cityIdlist = null;
        this.exp = 0;
        this.lost = 0;
        this.endTime = 0L;
        this.result = 0;
        this.needCheckTime = 0L;
    }
}
