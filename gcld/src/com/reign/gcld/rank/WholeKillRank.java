package com.reign.gcld.rank;

import com.reign.gcld.world.domain.*;
import com.reign.gcld.world.common.*;
import com.reign.gcld.rank.common.*;
import java.util.*;
import java.util.concurrent.*;
import com.reign.gcld.common.*;

public class WholeKillRank extends BaseRanker
{
    private List<WholeKill> list;
    
    public WholeKillRank(final IDataGetter dataGetter, final List<WholeKill> initList) {
        super(dataGetter);
        this.list = null;
        this.list = initList;
    }
    
    @Override
    public void initList(final int i) {
    }
    
    @Override
    public void init() {
        if (this.list == null || this.list.size() < 1) {
            return;
        }
        int indexA = 1;
        int indexB = 1;
        int indexC = 1;
        for (final WholeKill wk : this.list) {
            if (wk != null) {
                if (wk.getReceivedReward() == 0) {
                    continue;
                }
                final int playerId = wk.getPlayerId();
                final int forceId = this.dataGetter.getPlayerDao().getForceId(playerId);
                DoubleLinkedList<RankData> levelRankList = null;
                ConcurrentMap<Integer, KillRank> cacheMap = null;
                switch (forceId) {
                    case 1: {
                        levelRankList = this.killRankListA;
                        cacheMap = this.cacheMapA;
                        break;
                    }
                    case 2: {
                        levelRankList = this.killRankListB;
                        cacheMap = this.cacheMapB;
                        break;
                    }
                    default: {
                        levelRankList = this.killRankListC;
                        cacheMap = this.cacheMapC;
                        break;
                    }
                }
                final Node<RankData> node = levelRankList.addWithReturn(new RankData(playerId, wk.getKillNum()));
                final KillRank pr = new KillRank();
                pr.levelData = node;
                if (forceId == 1) {
                    pr.levelRank = indexA++;
                }
                else if (forceId == 2) {
                    pr.levelRank = indexB++;
                }
                else {
                    pr.levelRank = indexC++;
                }
                cacheMap.put(playerId, pr);
            }
        }
    }
}
