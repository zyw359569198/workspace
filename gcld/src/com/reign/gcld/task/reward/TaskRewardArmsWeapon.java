package com.reign.gcld.task.reward;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.common.*;
import com.reign.gcld.weapon.domain.*;
import com.reign.gcld.sdata.domain.*;
import java.util.*;

public class TaskRewardArmsWeapon implements ITaskReward
{
    private int id;
    
    public TaskRewardArmsWeapon(final int id) {
        this.id = id;
    }
    
    public TaskRewardArmsWeapon(final String[] s) {
        if (s.length > 1) {
            this.id = Integer.parseInt(s[1]);
        }
        else {
            this.id = 0;
        }
    }
    
    @Override
    public Map<Integer, Reward> rewardPlayer(final PlayerDto playerDto, final IDataGetter taskDataGetter, final String prefixAttribute, final Object obj) {
        if (taskDataGetter.getPlayerWeaponDao().getPlayerWeapon(playerDto.playerId, this.id) == null) {
            final PlayerWeapon playerWeapon = new PlayerWeapon();
            playerWeapon.setPlayerId(playerDto.playerId);
            playerWeapon.setWeaponId(this.id);
            playerWeapon.setType(((ArmsWeapon)taskDataGetter.getArmsWeaponCache().get((Object)this.id)).getType());
            playerWeapon.setLv(0);
            playerWeapon.setGemId("");
            playerWeapon.setTimes(0);
            taskDataGetter.getPlayerWeaponDao().create(playerWeapon);
            taskDataGetter.getBattleDataCache().refreshWeaponEffect(playerDto.playerId, this.id);
        }
        return this.getReward(playerDto, taskDataGetter, obj);
    }
    
    @Override
    public Map<Integer, Reward> getReward(final PlayerDto playerDto, final IDataGetter taskDataGetter, final Object obj) {
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        final Reward reward = new Reward(20, "newArmsWeapon", 1);
        reward.setId(this.id);
        map.put(20, reward);
        return map;
    }
}
