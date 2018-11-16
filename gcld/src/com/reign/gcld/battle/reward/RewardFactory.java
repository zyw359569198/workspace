package com.reign.gcld.battle.reward;

import com.reign.gcld.sdata.domain.*;

public class RewardFactory
{
    private static final RewardFactory instance;
    
    static {
        instance = new RewardFactory();
    }
    
    public static RewardFactory getInstance() {
        return RewardFactory.instance;
    }
    
    public void getReward(final Tactic temp) {
        if (temp.getSpecialEffect() == null) {
            return;
        }
        final String[] s = temp.getSpecialEffect().split(",");
        if ("rob_food".equalsIgnoreCase(s[0])) {
            temp.setReward(new RbFoodReward(3, s));
            temp.setSpecialType(2);
            return;
        }
        if ("confusion".equalsIgnoreCase(s[0])) {
            temp.setSpecialType(1);
        }
    }
}
