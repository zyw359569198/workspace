package com.reign.gcld.rank.common;

import com.reign.gcld.rank.*;
import com.reign.gcld.rank.domain.*;
import com.reign.gcld.world.common.*;
import java.util.concurrent.*;
import java.util.*;
import com.reign.gcld.common.*;

public class BarbarainRanker extends BaseRanker
{
    public BarbarainRanker(final IDataGetter dataGetter) {
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
        final List<BarbariansKillInfo> list = this.dataGetter.getBarbariansKillInfoDao().getByforceId(i);
        if (list == null) {
            return;
        }
        int index = 1;
        for (final BarbariansKillInfo bki : list) {
            final Node<RankData> node = levelRankList.addWithReturn(new RankData(bki.getPlayerid(), bki.getKillnum()));
            final KillRank pr = new KillRank();
            pr.levelData = node;
            pr.levelRank = index++;
            final int playerId = bki.getPlayerid();
            cacheMap.put(playerId, pr);
        }
    }
}
