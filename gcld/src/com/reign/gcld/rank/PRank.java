package com.reign.gcld.rank;

import com.reign.gcld.nation.domain.*;
import com.reign.gcld.world.common.*;
import com.reign.gcld.rank.common.*;
import java.util.*;
import java.util.concurrent.*;
import com.reign.gcld.common.*;

public class PRank extends BaseRanker
{
    private List<PlayerPRank> list;
    
    public PRank(final IDataGetter dataGetter, final List<PlayerPRank> initList) {
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
        final long now = System.currentTimeMillis();
        for (final PlayerPRank ppr : this.list) {
            final int playerId = ppr.getPlayerId();
            final int forceId = this.dataGetter.getPlayerDao().getForceId(playerId);
            DoubleLinkedList<RankData> levelRankList = null;
            ConcurrentMap<Integer, KillRank> cacheMap = null;
            switch (forceId) {
                case 1: {
                    levelRankList = this.killRankListA;
                    cacheMap = this.cacheMapA;
                    this.setKillTotalA(this.getKillTotalA().investNum + ppr.getNum(), now);
                    break;
                }
                case 2: {
                    levelRankList = this.killRankListB;
                    cacheMap = this.cacheMapB;
                    this.setKillTotalB(this.getKillTotalB().investNum + ppr.getNum(), now);
                    break;
                }
                default: {
                    levelRankList = this.killRankListC;
                    cacheMap = this.cacheMapC;
                    this.setKillTotalC(this.getKillTotalC().investNum + ppr.getNum(), now);
                    break;
                }
            }
            final Node<RankData> node = levelRankList.addWithReturn(new RankData(playerId, ppr.getNum()));
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
