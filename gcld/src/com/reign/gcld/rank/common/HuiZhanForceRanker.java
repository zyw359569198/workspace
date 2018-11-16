package com.reign.gcld.rank.common;

import com.reign.gcld.rank.*;
import com.reign.gcld.world.common.*;
import java.util.concurrent.*;
import com.reign.gcld.huizhan.domain.*;
import java.util.*;
import com.reign.gcld.common.*;

public class HuiZhanForceRanker extends BaseRanker
{
    public HuiZhanForceRanker(final IDataGetter dataGetter) {
        super(dataGetter);
    }
    
    @Override
    public void initList(final int i) {
        DoubleLinkedList<RankData> levelRankList = null;
        ConcurrentMap<Integer, KillRank> cacheMap = null;
        if (i == 1) {
            levelRankList = this.killRankListA;
            cacheMap = this.cacheMapA;
        }
        else if (i == 2) {
            levelRankList = this.killRankListB;
            cacheMap = this.cacheMapB;
        }
        else {
            levelRankList = this.killRankListC;
            cacheMap = this.cacheMapC;
        }
        final HuizhanHistory hh = this.dataGetter.getHuizhanHistoryDao().getLatestHuizhan();
        if (hh == null) {
            return;
        }
        final List<PlayerHuizhan> list = this.dataGetter.getPlayerHuizhanDao().getByhzIdAndForceId(hh.getVId(), i);
        if (list == null) {
            return;
        }
        int index = 1;
        for (final PlayerHuizhan ph : list) {
            final int playerId = ph.getPlayerId();
            final Node<RankData> node = levelRankList.addWithReturn(new RankData(playerId, ph.getForces()));
            final KillRank pr = new KillRank();
            pr.levelData = node;
            pr.levelRank = index++;
            cacheMap.put(playerId, pr);
        }
    }
}
