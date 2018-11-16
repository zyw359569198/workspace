package com.reign.gcld.duel.util;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.rank.service.*;
import com.reign.util.*;
import com.reign.gcld.common.*;

public class DuelUtil
{
    public static Tuple<Boolean, String> canDuel(final PlayerDto playerDto, final IRankService rankService) {
        final Tuple<Boolean, String> tuple = new Tuple();
        tuple.left = false;
        if (playerDto.playerLv < 30) {
            tuple.right = LocalMessages.NATION_TASK_LOW_LV;
            return tuple;
        }
        if (5 != rankService.hasNationTasks(playerDto.forceId)) {
            tuple.right = LocalMessages.NATION_TASK_IS_NOT_DUEL;
            return tuple;
        }
        tuple.left = true;
        return tuple;
    }
    
    public static int getRewardScoreByIndex(final int index) {
        if (1 == index) {
            return 10;
        }
        if (2 == index) {
            return 15;
        }
        return 25;
    }
}
