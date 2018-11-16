package com.reign.gcld.battle.reward;

import com.reign.gcld.common.*;
import com.reign.gcld.general.domain.*;

public class RbFoodReward implements IReward
{
    public int type;
    public int value;
    public int maxValue;
    
    public RbFoodReward(final int type, final String[] s) {
        this.type = type;
        this.value = Integer.valueOf(s[1]);
        this.maxValue = Integer.valueOf(s[2]);
    }
    
    @Override
    public RewardInfo rewardPlayer(final IDataGetter dataGetter, final int playerId, final String prefixAttribute, final Object obj) {
        final int generalId = (int)obj;
        final PlayerGeneralMilitary pgm = dataGetter.getPlayerGeneralMilitaryDao().getMilitary(playerId, generalId);
        int addValue = this.value;
        if (pgm.getTacticEffect() >= this.maxValue) {
            return new RewardInfo(0);
        }
        if (pgm.getTacticEffect() + this.value > this.maxValue) {
            addValue = pgm.getTacticEffect() + this.value - this.maxValue;
        }
        if (addValue > 0) {
            dataGetter.getPlayerResourceDao().addFoodIgnoreMax(playerId, addValue, String.valueOf(prefixAttribute) + "\u7cae\u98df");
        }
        dataGetter.getPlayerGeneralMilitaryDao().updateTacticEffect(playerId, generalId, addValue);
        final RewardInfo ri = new RewardInfo(1);
        ri.setAddValue(addValue);
        ri.setType(this.type);
        return ri;
    }
    
    @Override
    public RewardInfo getReward(final IDataGetter dataGetter, final int playerId, final Object obj) {
        if (playerId < 0) {
            return null;
        }
        final int generalId = (int)obj;
        final PlayerGeneralMilitary pgm = dataGetter.getPlayerGeneralMilitaryDao().getMilitary(playerId, generalId);
        int addValue = this.value;
        if (pgm.getTacticEffect() >= this.maxValue) {
            return new RewardInfo(0);
        }
        if (pgm.getTacticEffect() + this.value > this.maxValue) {
            addValue = pgm.getTacticEffect() + this.value - this.maxValue;
        }
        final RewardInfo ri = new RewardInfo(1);
        ri.setAddValue(addValue);
        ri.setType(this.type);
        return ri;
    }
    
    @Override
    public RewardInfo canReward(final IDataGetter dataGetter, final int playerId, final Object obj) {
        final PlayerGeneralMilitary pgm = (PlayerGeneralMilitary)obj;
        if (pgm.getTacticEffect() >= this.maxValue) {
            return new RewardInfo(false);
        }
        return new RewardInfo(true);
    }
}
