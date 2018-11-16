package com.reign.gcld.task.reward;

import org.apache.commons.lang.*;

public class TaskRewardFactory
{
    private static final TaskRewardFactory instance;
    
    static {
        instance = new TaskRewardFactory();
    }
    
    public static TaskRewardFactory getInstance() {
        return TaskRewardFactory.instance;
    }
    
    public ITaskReward getTaskReward(final String reward) {
        if (StringUtils.isBlank(reward)) {
            return null;
        }
        final String[] rewards = reward.split(";");
        if (rewards.length < 1) {
            return null;
        }
        if (rewards.length > 1) {
            return new TaskRewardAnd(rewards);
        }
        final String[] s = rewards[0].split(",");
        final String type = s[0];
        if ("copper".equalsIgnoreCase(type)) {
            return new TaskRewardCopper(s);
        }
        if ("lumber".equalsIgnoreCase(type)) {
            return new TaskRewardLumber(s);
        }
        if ("ChiefExp".equalsIgnoreCase(type)) {
            return new TaskRewardChiefExp(s);
        }
        if ("new_building".equalsIgnoreCase(type)) {
            return new TaskRewardNewBuilding(s);
        }
        if ("functionId".equalsIgnoreCase(type)) {
            return new TaskRewardNewFunction(s);
        }
        if ("new_construction".equalsIgnoreCase(type)) {
            return new TaskRewardNewConstruction(s);
        }
        if ("?".equalsIgnoreCase(type)) {
            return new TaskRewardCopper(0);
        }
        if ("daily".equalsIgnoreCase(type)) {
            return new TaskRewardDailyTask(s);
        }
        if ("brunch".equalsIgnoreCase(type)) {
            return new TaskRewardBrunchTask(s);
        }
        if ("new_tech".equalsIgnoreCase(type)) {
            return new TaskRewardNewTech(s);
        }
        if ("food".equalsIgnoreCase(type)) {
            return new TaskRewardFood(s);
        }
        if ("iron".equalsIgnoreCase(type)) {
            return new TaskRewardIron(s);
        }
        if ("new_incense".equalsIgnoreCase(type)) {
            return new TaskRewardNewIncense(s);
        }
        if ("store_lock_on".equalsIgnoreCase(type)) {
            return new TaskRewardStoreLockOn(s);
        }
        if ("store_lock_off".equalsIgnoreCase(type)) {
            return new TaskRewardStoreLockOff(s);
        }
        if ("tavern_lock_on".equalsIgnoreCase(type)) {
            return new TaskRewardTavernLockOn(s);
        }
        if ("tavern_lock_off".equalsIgnoreCase(type)) {
            return new TaskRewardTavernLockOff(s);
        }
        if ("new_event".equalsIgnoreCase(type)) {
            return new TaskRewardNewEvent(s);
        }
        if ("copper_e".equalsIgnoreCase(type)) {
            return new TaskRewardCopperE(s);
        }
        if ("lumber_e".equalsIgnoreCase(type)) {
            return new TaskRewardLumberE(s);
        }
        if ("food_e".equalsIgnoreCase(type)) {
            return new TaskRewardFoodE(s);
        }
        if ("iron_e".equalsIgnoreCase(type)) {
            return new TaskRewardIronE(s);
        }
        if ("Leader".equalsIgnoreCase(type)) {
            return new TaskRewardLeader(s);
        }
        if ("Politics".equalsIgnoreCase(type)) {
            return new TaskRewardPolitics(s);
        }
        if ("Intel".equalsIgnoreCase(type)) {
            return new TaskRewardIntel(s);
        }
        if ("Strength".equalsIgnoreCase(type)) {
            return new TaskRewardStrength(s);
        }
        if ("gold".equalsIgnoreCase(type)) {
            return new TaskRewardGold(s);
        }
        if ("arms_weapon".equalsIgnoreCase(type)) {
            return new TaskRewardArmsWeapon(s);
        }
        if ("gem".equalsIgnoreCase(type)) {
            return new TaskRewardGem(s);
        }
        if ("worship".equalsIgnoreCase(type)) {
            return new TaskRewardWorship(s);
        }
        if ("people_loyal".equalsIgnoreCase(type)) {
            return new TaskRewardPeopleLoyal(s);
        }
        if ("item".equalsIgnoreCase(type)) {
            return new TaskRewardItems(s);
        }
        if ("arms_gem".equalsIgnoreCase(type)) {
            return new TaskRewardArmsGem(s);
        }
        if ("search_get".equalsIgnoreCase(type)) {
            return new TaskRewardSearchGet(s);
        }
        if ("dinner_get".equalsIgnoreCase(type)) {
            return new TaskRewardDinnerGet(s);
        }
        if ("market_get".equalsIgnoreCase(type)) {
            return new TaskRewardMarketGet(s);
        }
        if ("gem_pos_open".equalsIgnoreCase(type)) {
            return new TaskRewardGemPos(s);
        }
        if ("equip".equalsIgnoreCase(type)) {
            return new TaskRewardEquip(s);
        }
        if ("auto_construction_stop".equalsIgnoreCase(type)) {
            return new TaskRewardAutoConstructionStop();
        }
        if ("construction_complete".equalsIgnoreCase(type)) {
            return new TaskRewardConstructionComplete();
        }
        if ("refresh_store".equalsIgnoreCase(type)) {
            return new TaskRewardRefreshStore();
        }
        if ("refresh_store_equip".equalsIgnoreCase(type)) {
            return new TaskRewardRefreshStoreEquip();
        }
        if ("free_construction".equalsIgnoreCase(type)) {
            return new TaskRewardFreeConstruction(s);
        }
        if ("worship_get".equalsIgnoreCase(type)) {
            return new TaskRewardWorshipGet(s);
        }
        if ("drawing".equalsIgnoreCase(type)) {
            return new TaskRewardDrawing(s);
        }
        if ("recruit_token".equalsIgnoreCase(type)) {
            return new TaskRewardRecruitToken(s);
        }
        if ("quesheqiang".equalsIgnoreCase(type)) {
            return new TaskRewardQueSheQiang(s);
        }
        if ("tianhejia".equalsIgnoreCase(type)) {
            return new TaskRewardTianHeJia(s);
        }
        if ("huangyufu".equalsIgnoreCase(type)) {
            return new TaskRewardHuangYuFu(s);
        }
        if ("ticket".equalsIgnoreCase(type)) {
            return new TaskRewardTicket(s);
        }
        if ("danshutiequan".equalsIgnoreCase(type)) {
            return new TaskRewardDanShuTieQuan(s);
        }
        if ("ms".equalsIgnoreCase(type)) {
            return new TaskRewardMs(s);
        }
        if ("byT".equalsIgnoreCase(type)) {
            return new TaskRewardByT(s);
        }
        if ("gjdstq".equalsIgnoreCase(type)) {
            return new TaskRewardGjdstq(s);
        }
        if ("freeNiubiQuenchingTimes".equalsIgnoreCase(type)) {
            return new TaskRewardFreeNiubiQuenchingTimes(s);
        }
        if ("phantom".equalsIgnoreCase(type)) {
            return new TaskRewardPhantom(s);
        }
        return null;
    }
}
