package com.reign.gcld.task.reward;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.building.domain.*;
import com.reign.framework.json.*;
import com.reign.gcld.player.domain.*;
import java.util.*;
import com.reign.gcld.common.*;

public class TaskRewardNewConstruction implements ITaskReward
{
    public TaskRewardNewConstruction() {
    }
    
    public TaskRewardNewConstruction(final String[] s) {
    }
    
    @Override
    public Map<Integer, Reward> rewardPlayer(final PlayerDto playerDto, final IDataGetter taskDataGetter, final String prefixAttribute, final Object obj) {
        final PlayerConstants pc = taskDataGetter.getPlayerConstantsDao().read(playerDto.playerId);
        final PlayerBuildingWork pbw = new PlayerBuildingWork();
        pbw.setPlayerId(playerDto.playerId);
        pbw.setStartTime(new Date());
        pbw.setEndTime(new Date());
        pbw.setTargetBuildId(0);
        pbw.setWorkId(pc.getExtraNum() + 1);
        pbw.setWorkState(0);
        taskDataGetter.getPlayerConstantsDao().updateExtraNum(playerDto.playerId, 1);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("newConstruction", pc.getExtraNum());
        doc.endObject();
        Players.push(playerDto.playerId, PushCommand.PUSH_UPDATE, doc.toByte());
        taskDataGetter.getPlayerBuildingWorkDao().create(pbw);
        return this.getReward(playerDto, taskDataGetter, obj);
    }
    
    @Override
    public Map<Integer, Reward> getReward(final PlayerDto playerDto, final IDataGetter taskDataGetter, final Object obj) {
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        map.put(9, new Reward(9, LocalMessages.T_COMM_10013, 1));
        return map;
    }
}
