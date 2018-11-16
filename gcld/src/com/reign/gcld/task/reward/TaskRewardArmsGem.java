package com.reign.gcld.task.reward;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.common.util.*;
import java.util.*;
import com.reign.gcld.common.*;
import com.reign.gcld.player.domain.*;

public class TaskRewardArmsGem implements ITaskReward
{
    private int idBtm;
    private int idTop;
    private int num;
    
    public TaskRewardArmsGem(final String[] s) {
        if (s.length > 3) {
            this.idBtm = Integer.valueOf(s[1]);
            this.idTop = Integer.valueOf(s[2]);
            this.num = Integer.valueOf(s[3]);
        }
        else if (s.length == 2) {
            this.idBtm = 1;
            this.idTop = 1;
            this.num = Integer.valueOf(s[1]);
        }
    }
    
    @Override
    public Map<Integer, Reward> rewardPlayer(final PlayerDto playerDto, final IDataGetter taskDataGetter, final String prefixAttribute, final Object obj) {
        final int id = (this.idBtm == this.idTop) ? this.idBtm : (this.idBtm + WebUtil.nextInt(this.idTop - this.idBtm));
        final Player player = taskDataGetter.getPlayerDao().read(playerDto.playerId);
        taskDataGetter.getStoreHouseService().gainGem(player, this.num, id, prefixAttribute, null);
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        final Reward reward = new Reward(11, LocalMessages.T_COMM_10023, this.num);
        reward.setId(id);
        map.put(11, reward);
        return map;
    }
    
    @Override
    public Map<Integer, Reward> getReward(final PlayerDto playerDto, final IDataGetter taskDataGetter, final Object obj) {
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        map.put(11, new Reward(11, LocalMessages.T_COMM_10023, this.num));
        return map;
    }
}
