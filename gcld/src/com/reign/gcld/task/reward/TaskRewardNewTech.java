package com.reign.gcld.task.reward;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.common.*;
import com.reign.gcld.tech.domain.*;
import java.util.*;

public class TaskRewardNewTech implements ITaskReward
{
    private int techId;
    
    public TaskRewardNewTech(final int techId) {
        this.techId = techId;
    }
    
    public TaskRewardNewTech(final String[] s) {
        if (s.length > 1) {
            this.techId = Integer.parseInt(s[1]);
        }
        else {
            this.techId = 0;
        }
    }
    
    @Override
    public Map<Integer, Reward> rewardPlayer(final PlayerDto playerDto, final IDataGetter taskDataGetter, final String prefixAttribute, final Object obj) {
        if (taskDataGetter.getPlayerTechDao().getPlayerTech(playerDto.playerId, this.techId) == null) {
            final PlayerTech playerTech = new PlayerTech();
            playerTech.setPlayerId(playerDto.playerId);
            playerTech.setTechId(this.techId);
            taskDataGetter.getPlayerTechDao().create(playerTech);
            taskDataGetter.getBattleDataCache().removeTroopEffect(playerDto.playerId, playerTech.getTechId());
        }
        return this.getReward(playerDto, taskDataGetter, obj);
    }
    
    @Override
    public Map<Integer, Reward> getReward(final PlayerDto playerDto, final IDataGetter taskDataGetter, final Object obj) {
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        map.put(12, new Reward(12, "newtech", this.techId));
        return map;
    }
}
