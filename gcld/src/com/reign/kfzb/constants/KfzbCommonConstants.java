package com.reign.kfzb.constants;

import com.reign.kf.comm.util.*;

public class KfzbCommonConstants
{
    public static final int MATCH_GLOBALSTATE_UNBEGIN = 0;
    public static final int MATCH_GLOBALSTATE_ACTIVE = 10;
    public static final int MATCH_GLOBALSTATE_SIGNUP = 20;
    public static final int MATCH_GLOBALSTATE_SIGNUP_FINISH = 30;
    public static final int MATCH_GLOBALSTATE_PREPARE = 40;
    public static final int MATCH_GLOBALSTATE_DAY1BATTLE = 50;
    public static final int MATCH_GLOBALSTATE_DAY1_ENDED = 51;
    public static final int MATCH_GLOBALSTATE_DAY2BATTLE = 60;
    public static final int MATCH_GLOBALSTATE_DAY2_ENDED = 61;
    public static final int MATCH_GLOBALSTATE_DAY3BATTLE = 65;
    public static final int MATCH_GLOBALSTATE_DAY3_ENDED = 70;
    public static final int MATCH_GLOBALSTATE_SEASON_FINISH = 80;
    public static final int DAY2FIRSTLAYER = 4;
    public static volatile int[] LAYERROUNDINFO;
    public static final int BATTLE_WIN = 1;
    public static final int BATTLE_LOST = 2;
    public static final int BATTLE_NONE = 0;
    public static final int RESULT_SUCC = 1;
    public static final int RESULT_FAIL = 0;
    public static final int STATE_PLAYER_BATTLE_FINISH = 3;
    public static final int MAXSROUND_NUM = 15;
    public static final int POSATT = 1;
    public static final int POSDEF = 2;
    public static final float REVENGEBUFF_EFFECT = 0.05f;
    public static final int MATCHKEY_SEASONID_SHIFT = 30;
    public static final int MATCHKEY_MATCHID_SHIFT = 12;
    public static final int MATCHKEY_LAYER_SHIFT = 6;
    public static final int MATCHKEY_MATCHID_MASK = 65535;
    public static final int FEASTROOMSIZE = 10;
    public static final int FEASTROOMIDMASK = 8;
    public static final int FEASTROOMIDRANKMASK = 255;
    
    static {
        KfzbCommonConstants.LAYERROUNDINFO = new int[] { 0, 5, 5, 3, 3 };
    }
    
    public static int addRoundBattleRes(final int oldres, final int round, final int battleRes) {
        final int t = round - 1;
        return (oldres & ~(3 << t * 2)) | battleRes << t * 2;
    }
    
    public static int[] getBattleResByRes(final int value, final int totalRound) {
        final int[] res = new int[totalRound];
        for (int i = 0; i < totalRound; ++i) {
            res[i] = (value & 3 << i * 2) >> 2 * i;
        }
        return res;
    }
    
    public static int[] getBattleWinNum(final int value, final int totalRound) {
        final int[] bres = getBattleResByRes(value, totalRound);
        final int[] res = new int[2];
        for (int i = 0; i < bres.length; ++i) {
            if (bres[i] == 1) {
                final int[] array = res;
                final int n = 0;
                ++array[n];
            }
            else if (bres[i] == 2) {
                final int[] array2 = res;
                final int n2 = 1;
                ++array2[n2];
            }
        }
        return res;
    }
    
    public static int getBattleBuff(final int seasonId, final int matchId, final int round, final int totalBufferNum, final int pos) {
        if (totalBufferNum > 1) {
            final long seed = seasonId * 137 + matchId * 97 + round * 17 + pos;
            final Ran ran = new Ran(seed);
            return ran.nextInt(totalBufferNum) + 1;
        }
        return totalBufferNum;
    }
    
    public static boolean isP1Attack(final int seasonId, final int layer, final int round, final int layerRound, final int matchId) {
        if (round < layerRound) {
            return round % 2 == 1;
        }
        final int res = (matchId + round + seasonId % 1000) * 137 / 10 % 2;
        return res == 0;
    }
    
    public static int[] getBattleWinNumWithRoundLimit(final int battleRes, final int layerRound, final int round) {
        final int[] bres = getBattleResByRes(battleRes, layerRound);
        final int[] res = new int[2];
        for (int length = (bres.length > round) ? round : bres.length, i = 0; i < length; ++i) {
            if (bres[i] == 1) {
                final int[] array = res;
                final int n = 0;
                ++array[n];
            }
            else if (bres[i] == 2) {
                final int[] array2 = res;
                final int n2 = 1;
                ++array2[n2];
            }
        }
        return res;
    }
    
    public static long getMatchKey(final int seasonId, final int matchId, final int layer, final int round) {
        return seasonId << 30 | matchId << 12 | layer << 6 | round;
    }
    
    public static int getSeasonIdFromMatchKey(final long matchKey, final int seasonId) {
        return (int)(matchKey >> 30);
    }
    
    public static int getMatchIdByMatchKey(final long matchKey) {
        return (int)(matchKey >> 12 & 0xFFFFL);
    }
    
    public static long getRoomIdByRoomAndId(final long room, final long id) {
        return id << 8 | room;
    }
    
    public static int getRankIdByRoomId(final long roomId) {
        return (int)(roomId & 0xFFL);
    }
    
    public static int getRanTerrain(final int matchId, final int round, final int seasonId) {
        int ten = (matchId + round + seasonId) % 3 + 1;
        if (ten == 2) {
            ten = 4;
        }
        return ten;
    }
    
    public static int getTerrainValByTerrain(final int terrain) {
        if (terrain == 3) {
            return 2;
        }
        if (terrain == 4) {
            return 3;
        }
        if (terrain == 5 || terrain == 6) {
            return 4;
        }
        return 1;
    }
    
    public static long getBattleIdByMatch(final long seasonId, final long matchId, final long round) {
        return seasonId << 40 | matchId << 5 | round;
    }
    
    public static boolean isC1AttackerRoundFinal(final int seasonId, final int matchId, final int round) {
        final int res = (matchId + round + seasonId / 100000 % 1000) * 137 / 10 % 2;
        return res != 1;
    }
    
    public static String getKfzbKey(final int cId, final int seasonId) {
        final StringBuffer sb = new StringBuffer();
        sb.append(235);
        sb.append(cId >> 3);
        sb.append("kfzb");
        sb.append(cId);
        sb.append(seasonId);
        sb.append(cId % 345);
        sb.append(MD5SecurityUtil.code(String.valueOf(cId)));
        return MD5SecurityUtil.code(sb.toString());
    }
    
    public static boolean getNeedChange(final int seasonId, final int matchId, final int round) {
        return round % 2 == 0;
    }
    
    public static int getRevengeEffect(final int revengeBuff) {
        if (revengeBuff == 0) {
            return 0;
        }
        if (revengeBuff == 2) {
            return 150;
        }
        if (revengeBuff == 2) {
            return 250;
        }
        if (revengeBuff == 3) {
            return 500;
        }
        if (revengeBuff == 4) {
            return 800;
        }
        return 0;
    }
}
