package com.reign.gcld.rank.common;

import com.reign.gcld.player.domain.*;

public class RankComm
{
    public static boolean functionIsOpen(final int functionId32, final int playerId, final PlayerAttribute pa) {
        if (pa == null) {
            return false;
        }
        final char[] cs = pa.getFunctionId().toCharArray();
        return cs[functionId32] == '1';
    }
}
