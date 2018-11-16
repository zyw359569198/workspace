package com.reign.kfgz.comm;

import com.reign.kfgz.team.*;

public class KfgzConstants
{
    public static boolean GZALLCLOSE;
    public static final int force_npc = 0;
    public static final int MOVE_STOP = 0;
    public static final int CITY_S_NOT_CAER = -1;
    public static final int CITY_S_SELF = 0;
    public static final int CITY_S_CAN_ATTACK = 1;
    public static final String CHAT_CHANNEL_GLOBAL = "GLOBAL";
    public static final String CHAT_CHANNEL_FORCE = "COUNTRY";
    public static final String CHAT_CHANNEL_ONE2ONE = "ONE2ONE";
    public static final String CHAT_CHANNEL_SYS2ONE = "SYS2ONE";
    public static final int CHAT_TYPE_NORMAL = 0;
    public static final int CHAT_TYPE_BATTLE = 1;
    public static final int RETREAT_COEF = 10;
    public static final int ROAD_TYPE_TRANSFORMABLE = 1;
    public static final long BUYPHANTOMLIMITCD = 900000L;
    public static final int BUYPHANTOMLIMITTIMES = 50;
    public static final int BUYPHANTOMLIMITSUC = 1;
    public static final int BUYPHANTOMLIMIT1 = 2;
    public static final int BUYPHANTOMLIMIT2 = 3;
    public static final int BUYPHANTOMLIMIT3 = 4;
    public static final String FOOD_MUBING = "\u52df\u5175";
    public static final String MUBING_MUBING = "\u5feb\u901f\u52df\u5175";
    public static final String GOLD_MUBING = "\u5feb\u901f\u52df\u5175";
    public static final String BATTLE_REWARD = "\u6218\u6597\u5956\u52b1";
    public static final int CITYFORCEMASK = 5;
    
    static {
        KfgzConstants.GZALLCLOSE = false;
    }
    
    public static void doLockCities(final KfCity cFrom, final KfCity cTo) {
        if (cFrom.getTeamId() < cTo.getTeamId()) {
            cFrom.teamLock.writeLock().lock();
            cTo.teamLock.writeLock().lock();
        }
        else {
            cTo.teamLock.writeLock().lock();
            cFrom.teamLock.writeLock().lock();
        }
    }
    
    public static void doUnlockCities(final KfCity cFrom, final KfCity cTo) {
        if (cFrom.getTeamId() < cTo.getTeamId()) {
            cTo.teamLock.writeLock().unlock();
            cFrom.teamLock.writeLock().unlock();
        }
        else {
            cFrom.teamLock.writeLock().unlock();
            cTo.teamLock.writeLock().unlock();
        }
    }
    
    public static int getWorldForcedKey(final int cityId, final int forceId) {
        return cityId << 5 | forceId;
    }
}
