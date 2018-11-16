package com.reign.kfwd.cache;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.kfwd.dao.*;
import com.reign.kfwd.domain.*;
import java.util.concurrent.*;
import com.reign.kf.comm.entity.kfwd.response.*;
import java.util.*;

@Component
public class KfwdCacheManager implements IKfwdCacheManager
{
    @Autowired
    IKfwdBattleWarriorDao kfwdBattleWarriorDao;
    @Autowired
    IKfwdBattleWarriorGeneralDao kfwdBattleWarriorGeneralDao;
    @Autowired
    IKfwdRuntimeInspireDao kfwdRuntimeInspireDao;
    @Autowired
    IKfwdRewardDoubleDao kfwdRewardDoubleDao;
    @Autowired
    IKfwdTicketRewardDao kfwdTicketRewardDao;
    Map<Long, KfwdRuntimeInspire> inspireMap;
    Map<Integer, KfwdBattleWarrior> warriorMap;
    Map<Integer, KfwdBattleWarriorGeneral> generalMap;
    Map<Integer, KfwdRewardDouble> rewardDoubleMap;
    Map<Integer, KfwdTicketReward> ticketMap;
    
    public KfwdCacheManager() {
        this.inspireMap = new ConcurrentHashMap<Long, KfwdRuntimeInspire>();
        this.warriorMap = new ConcurrentHashMap<Integer, KfwdBattleWarrior>();
        this.generalMap = new ConcurrentHashMap<Integer, KfwdBattleWarriorGeneral>();
        this.rewardDoubleMap = new ConcurrentHashMap<Integer, KfwdRewardDouble>();
        this.ticketMap = new ConcurrentHashMap<Integer, KfwdTicketReward>();
    }
    
    @Override
    public void iniSeasonCache(final KfwdSeasonInfo newInfo) {
        this.warriorMap.clear();
        this.generalMap.clear();
        this.rewardDoubleMap.clear();
        this.ticketMap.clear();
        final int seasonId = newInfo.getSeasonId();
        final List<KfwdBattleWarrior> warriorList = this.kfwdBattleWarriorDao.getAllWarriorBySeasonId(newInfo.getSeasonId());
        for (final KfwdBattleWarrior kb : warriorList) {
            this.putIntoCache(kb);
        }
        final int PageCount = 1000;
        final int count = this.kfwdBattleWarriorGeneralDao.getAllWarriorGeneralCount(seasonId);
        for (int PageNum = (count + 1000 - 1) / 1000, i = 0; i < PageNum; ++i) {
            final int firstPos = 1000 * i;
            int num = 1000;
            if (i == PageNum - 1) {
                num = count % 1000;
            }
            final List<KfwdBattleWarriorGeneral> gList = this.kfwdBattleWarriorGeneralDao.getAllGeneralBySeasonIdAndSize(newInfo.getSeasonId(), firstPos, num);
            for (final KfwdBattleWarriorGeneral kg : gList) {
                this.putIntoCache(kg);
            }
        }
        final List<KfwdRewardDouble> rdlist = this.kfwdRewardDoubleDao.getInfoBySeasonId(seasonId);
        for (final KfwdRewardDouble krd : rdlist) {
            this.putIntoCache(krd);
        }
        final List<KfwdTicketReward> trlist = this.kfwdTicketRewardDao.getInfoBySeasonId(seasonId);
        for (final KfwdTicketReward ktr : trlist) {
            this.putIntoCache(ktr);
        }
    }
    
    @Override
    public void putIntoCache(final KfwdTicketReward ktr) {
        this.ticketMap.put(ktr.getCompetitorId(), ktr);
    }
    
    @Override
    public void putIntoCache(final KfwdRewardDouble krd) {
        this.rewardDoubleMap.put(krd.getCompetitorId(), krd);
    }
    
    @Override
    public KfwdRewardDouble getRewardDouble(final int competitorId) {
        return this.rewardDoubleMap.get(competitorId);
    }
    
    @Override
    public KfwdTicketReward getTicketInfo(final int competitorId) {
        return this.ticketMap.get(competitorId);
    }
    
    @Override
    public void putIntoCache(final KfwdBattleWarriorGeneral kg) {
        this.generalMap.put(kg.getCompetitorId(), kg);
    }
    
    @Override
    public void putIntoCache(final KfwdBattleWarrior kb) {
        this.warriorMap.put(kb.getCompetitorId(), kb);
    }
    
    @Override
    public void putIntoCache(final KfwdRuntimeInspire kg) {
        this.inspireMap.put(kg.getKey(), kg);
    }
    
    @Override
    public KfwdBattleWarrior getBattleWarrior(final int player1Id) {
        return this.warriorMap.get(player1Id);
    }
    
    @Override
    public KfwdBattleWarriorGeneral getBattleWarriorGeneral(final int player1Id) {
        return this.generalMap.get(player1Id);
    }
    
    @Override
    public KfwdRuntimeInspire getInspire(final int player1Id, final int round) {
        return this.inspireMap.get(KfwdRuntimeInspire.makeKey(player1Id, round));
    }
}
