package com.reign.gcld.rank.common;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.rank.service.*;
import com.reign.gcld.battle.reward.*;
import com.reign.gcld.common.*;

public class InMemmoryIndivTaskReward implements Cloneable
{
    protected int rewardType;
    protected int num;
    public int itemsId;
    public int itemNum;
    
    public InMemmoryIndivTaskReward(final String[] rewards) {
        this.num = Integer.parseInt(rewards[1]);
    }
    
    public String handleReward(final IDataGetter getter, final PlayerDto playerDto) {
        final StringBuffer sb = new StringBuffer();
        final boolean isDouble = RankService.hasDoubleReward;
        int realNum = this.num;
        if (isDouble) {
            realNum *= 2;
        }
        sb.append(this.rewardType).append(",").append(realNum);
        RewardType.reward(getter, this.rewardType, realNum, playerDto.playerId, 12);
        if (this.itemsId > 0 && getter.getDiamondShopService().canRecvDropProps(playerDto.playerId, this.itemsId)) {
            realNum = (isDouble ? (this.itemNum * 2) : this.itemNum);
            RewardType.reward(getter, this.itemsId, realNum, playerDto.playerId, 12);
            sb.append(";");
            sb.append(this.itemsId).append(",").append(realNum);
        }
        return sb.toString();
    }
    
    public MultiResult getRewardInfo() {
        final boolean isDouble = RankService.hasDoubleReward;
        final int realNum = isDouble ? (this.num * 2) : this.num;
        final int realItemNum = isDouble ? (this.itemNum * 2) : this.itemNum;
        final MultiResult result = new MultiResult();
        result.result1 = this.rewardType;
        result.result2 = realNum;
        result.result3 = this.itemsId;
        result.result4 = realItemNum;
        return result;
    }
    
    @Override
    protected InMemmoryIndivTaskReward clone() throws CloneNotSupportedException {
        return (InMemmoryIndivTaskReward)super.clone();
    }
}
