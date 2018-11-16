package com.reign.gcld.task.reward;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.world.common.*;
import java.util.*;
import com.reign.gcld.common.*;

public class TaskRewardGjdstq implements ITaskReward
{
    private int num;
    private static final int ITEM_ID = 106;
    
    public TaskRewardGjdstq(final int num) {
        this.num = num;
    }
    
    public TaskRewardGjdstq(final String[] s) {
        if (s.length > 1) {
            this.num = Integer.parseInt(s[1]);
        }
        else {
            this.num = 0;
        }
    }
    
    @Override
    public Map<Integer, Reward> rewardPlayer(final PlayerDto playerDto, final IDataGetter taskDataGetter, final String prefixAttribute, final Object obj) {
        int gjdstq = this.num;
        if (obj != null && obj instanceof CityEventRate) {
            final CityEventRate cer = (CityEventRate)obj;
            final double rate = cer.rate;
            gjdstq = (int)(this.num * rate);
        }
        taskDataGetter.getStoreHouseService().gainSearchItems(106, gjdstq, playerDto, prefixAttribute);
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        map.put(81, new Reward(81, LocalMessages.T_COMM_10048, gjdstq));
        return map;
    }
    
    @Override
    public Map<Integer, Reward> getReward(final PlayerDto playerDto, final IDataGetter taskDataGetter, final Object obj) {
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        int gjdstq = this.num;
        if (obj != null && obj instanceof CityEventRate) {
            final CityEventRate cer = (CityEventRate)obj;
            final double rate = cer.rate;
            gjdstq = (int)(this.num * rate);
        }
        map.put(81, new Reward(81, LocalMessages.T_COMM_10048, gjdstq));
        return map;
    }
}
