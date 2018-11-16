package com.reign.gcld.task.reward;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.common.*;
import java.util.*;

public class TaskRewardItems implements ITaskReward
{
    private int idBtm;
    private int idTop;
    private int num;
    private String item;
    
    public String getItem() {
        return this.item;
    }
    
    public void setItem(final String item) {
        this.item = item;
    }
    
    public TaskRewardItems(final String[] s) {
        this.item = "items";
        if (s.length > 3) {
            this.idBtm = Integer.valueOf(s[1]);
            this.idTop = Integer.valueOf(s[2]);
            this.num = Integer.valueOf(s[3]);
        }
    }
    
    @Override
    public Map<Integer, Reward> rewardPlayer(final PlayerDto playerDto, final IDataGetter taskDataGetter, final String prefixAttribute, final Object obj) {
        final int id = (this.idBtm == this.idTop) ? this.idBtm : (this.idBtm + WebUtil.nextInt(this.idTop - this.idBtm));
        final Items item = (Items)taskDataGetter.getItemsCache().get((Object)id);
        if (item != null) {
            this.setItem(item.getName());
        }
        double multiply = (double)((obj == null) ? 1 : ((obj instanceof Integer) ? obj : 1));
        final double rate = taskDataGetter.getTechEffectCache().getTechEffect(playerDto.playerId, 12) / 100.0;
        multiply += rate;
        final int real = DataCastUtil.double2int(Math.ceil(this.num * multiply));
        taskDataGetter.getStoreHouseService().gainSearchItems(id, real, playerDto, prefixAttribute);
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        final Reward reward = new Reward(24, item.getName(), real);
        reward.setId(id);
        map.put(24, reward);
        return map;
    }
    
    @Override
    public Map<Integer, Reward> getReward(final PlayerDto playerDto, final IDataGetter taskDataGetter, final Object obj) {
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        map.put(24, new Reward(24, this.getItem(), this.num));
        return map;
    }
}
