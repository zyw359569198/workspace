package com.reign.gcld.task.reward;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.weapon.domain.*;
import java.util.*;
import com.reign.gcld.common.*;

public class TaskRewardGemPos implements ITaskReward
{
    public TaskRewardGemPos() {
    }
    
    public TaskRewardGemPos(final String[] s) {
    }
    
    @Override
    public Map<Integer, Reward> rewardPlayer(final PlayerDto playerDto, final IDataGetter taskDataGetter, final String prefixAttribute, final Object obj) {
        final List<PlayerWeapon> list = taskDataGetter.getPlayerWeaponDao().getPlayerWeapons(playerDto.playerId);
        for (final PlayerWeapon pw : list) {
            final String[] gemStrs = pw.getGemId().split(",");
            if (gemStrs.length >= 3) {
                continue;
            }
            taskDataGetter.getPlayerWeaponDao().upgradeLoadGem(playerDto.playerId, pw.getWeaponId(), String.valueOf(pw.getGemId()) + "0,");
        }
        return this.getReward(playerDto, taskDataGetter, obj);
    }
    
    @Override
    public Map<Integer, Reward> getReward(final PlayerDto playerDto, final IDataGetter taskDataGetter, final Object obj) {
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        map.put(29, new Reward(29, LocalMessages.T_COMM_10029, 1));
        return map;
    }
}
