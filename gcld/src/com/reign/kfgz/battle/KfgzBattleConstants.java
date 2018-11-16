package com.reign.kfgz.battle;

public class KfgzBattleConstants
{
    public static final int DOSOLO_STATE_SUC = 1;
    public static final int DOSOLO_STATE_FAILED = 2;
    public static final int DOSOLO_STATE_FAILED_CORRECTGENERAL = 4;
    public static final int DOSOLO_STATE_NO_OPPONENT = 3;
    public static final int DORUSH_STATE_SUC = 1;
    public static final int DORUSH_STATE_FAILED = 2;
    public static final int DORETREAT_STATE_SUC = 1;
    public static final int DORETREAT_STATE_FAILED = 2;
    public static final int GZBATTLETYPEMASK = 1024;
    public static final int GZBATTLETYPE_NORMAL = 1025;
    public static final int GZBATTLETYPE_SOLO = 1026;
    
    public static String getBattleTitle(final int teamId, final int gzId) {
        return "kfgz_" + teamId + "_" + gzId;
    }
}
