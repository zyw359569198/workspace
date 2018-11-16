package com.reign.gcld.rank.common;

public class InMemmoryIndivTaskRewardCreate
{
    public static final String REW_COPPER = "copper";
    public static final String REW_IRON = "iron";
    public static final String REW_QUENCHING = "xilian";
    public static final String REW_FEAT = "gongxun";
    public static final String REW_FOOD = "food";
    public static final String REW_EXP = "exp";
    
    public static InMemmoryIndivTaskReward creatReward(final String reward) {
        final String[] rewards = reward.split(",");
        if (rewards[0].equalsIgnoreCase("copper")) {
            return new InMemmoryIndivTaskRewardCopper(rewards);
        }
        if (rewards[0].equalsIgnoreCase("iron")) {
            return new InMemmoryIndivTaskRewardIron(rewards);
        }
        if (rewards[0].equalsIgnoreCase("xilian")) {
            return new InMemmoryIndivTaskRewardQuenching(rewards);
        }
        if (rewards[0].equalsIgnoreCase("gongxun")) {
            return new InMemmoryIndivTaskRewardFeat(rewards);
        }
        if (rewards[0].equalsIgnoreCase("food")) {
            return new InMemmoryIndivTaskRewardFood(rewards);
        }
        if (rewards[0].equalsIgnoreCase("exp")) {
            return new InMemmoryIndivTaskRewardExp(rewards);
        }
        return null;
    }
}
