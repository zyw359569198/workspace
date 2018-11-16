package com.reign.gcld.kfwd.common.handler;

import com.reign.gcld.common.*;
import com.reign.gcld.player.domain.*;

public class RewardOperationHandlerConsumeGold implements IRewardOperationHandler
{
    private int playerId;
    private int gold;
    private String attributeKey;
    
    public RewardOperationHandlerConsumeGold(final int playerId) {
        this.playerId = playerId;
    }
    
    public RewardOperationHandlerConsumeGold(final int playerId, final int gold, final String attributeKey) {
        this.playerId = playerId;
        this.gold = gold;
        this.attributeKey = attributeKey;
    }
    
    @Override
    public void handle(final IDataGetter dataGetter) {
        final Player player = dataGetter.getPlayerDao().read(this.playerId);
        dataGetter.getPlayerDao().consumeGold(player, this.gold, this.attributeKey);
    }
}
