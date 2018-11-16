package com.reign.gcld.rank.common;

import com.reign.gcld.rank.*;
import com.reign.gcld.rank.domain.*;
import com.reign.gcld.world.common.*;
import java.util.*;
import com.reign.gcld.player.domain.*;
import java.util.concurrent.*;
import com.reign.gcld.common.*;

public class NationTaskKillRanker extends BaseRanker
{
    private List<TaskKillInfo> list;
    
    public NationTaskKillRanker(final IDataGetter dataGetter) {
        super(dataGetter);
        this.list = null;
    }
    
    public NationTaskKillRanker(final IDataGetter dataGetter, final List<TaskKillInfo> initList) {
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
        int indexD = 1;
        for (final TaskKillInfo taskKillInfo : this.list) {
            if (taskKillInfo == null) {
                continue;
            }
            final int playerId = taskKillInfo.getPlayerId();
            final Player player = this.dataGetter.getPlayerDao().read(playerId);
            final int forceId = player.getForceId();
            DoubleLinkedList<RankData> levelRankList = null;
            ConcurrentMap<Integer, KillRank> cacheMap = null;
            switch (forceId) {
                case 1: {
                    levelRankList = this.killRankListA;
                    cacheMap = this.cacheMapA;
                    this.setKillTotalA(this.getKillTotalA().investNum + taskKillInfo.getKillnum(), taskKillInfo.getUpdatetime());
                    break;
                }
                case 2: {
                    levelRankList = this.killRankListB;
                    cacheMap = this.cacheMapB;
                    this.setKillTotalB(this.getKillTotalB().investNum + taskKillInfo.getKillnum(), taskKillInfo.getUpdatetime());
                    break;
                }
                default: {
                    levelRankList = this.killRankListC;
                    cacheMap = this.cacheMapC;
                    this.setKillTotalC(this.getKillTotalC().investNum + taskKillInfo.getKillnum(), taskKillInfo.getUpdatetime());
                    break;
                }
            }
            final Node<RankData> node = levelRankList.addWithReturn(new RankData(playerId, taskKillInfo.getKillnum()));
            final Node<RankData> nodeD = this.killRankListD.addWithReturn(new RankData(playerId, taskKillInfo.getKillnum()));
            final KillRank pr = new KillRank();
            final KillRank prD = new KillRank();
            pr.levelData = node;
            prD.levelData = nodeD;
            if (forceId == 1) {
                pr.levelRank = indexA++;
            }
            else if (forceId == 2) {
                pr.levelRank = indexB++;
            }
            else {
                pr.levelRank = indexC++;
            }
            prD.levelRank = indexD++;
            cacheMap.put(playerId, pr);
            this.cacheMapD.put(playerId, prD);
        }
    }
}
