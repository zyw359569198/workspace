package com.reign.kfzb.cache;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.kfzb.dao.*;
import com.reign.kfzb.domain.*;
import java.util.concurrent.*;
import com.reign.kfzb.dto.response.*;
import java.util.*;

@Component
public class KfzbCacheManager implements IKfzbCacheManager
{
    @Autowired
    IKfzbBattleWarriorDao kfzbBattleWarriorDao;
    @Autowired
    IKfzbBattleWarriorGeneralDao kfzbBattleWarriorGeneralDao;
    Map<Integer, KfzbBattleWarrior> warriorMap;
    Map<Integer, KfzbBattleWarriorGeneral> generalMap;
    
    public KfzbCacheManager() {
        this.warriorMap = new ConcurrentHashMap<Integer, KfzbBattleWarrior>();
        this.generalMap = new ConcurrentHashMap<Integer, KfzbBattleWarriorGeneral>();
    }
    
    @Override
    public void iniSeasonCache(final KfzbSeasonInfo newInfo) {
        final int seasonId = newInfo.getSeasonId();
        final List<KfzbBattleWarrior> wlist = this.kfzbBattleWarriorDao.getWarriorBySeasonId(seasonId);
        for (final KfzbBattleWarrior w : wlist) {
            this.warriorMap.put(w.getCompetitorId(), w);
        }
        this.warriorMap.put(0, new KfzbBattleWarrior());
        final List<KfzbBattleWarriorGeneral> glist = this.kfzbBattleWarriorGeneralDao.getGeneralInfoBySeasonId(seasonId);
        for (final KfzbBattleWarriorGeneral g : glist) {
            this.generalMap.put(g.getCompetitorId(), g);
        }
        this.generalMap.put(0, new KfzbBattleWarriorGeneral());
    }
    
    @Override
    public KfzbBattleWarrior getBattleWarrior(final int cId) {
        return this.warriorMap.get(cId);
    }
    
    @Override
    public void putIntoCache(final KfzbBattleWarrior w1) {
        this.warriorMap.put(w1.getCompetitorId(), w1);
    }
    
    @Override
    public KfzbBattleWarriorGeneral getBattleWarriorGeneral(final int cId) {
        return this.generalMap.get(cId);
    }
    
    @Override
    public void putIntoCache(final KfzbBattleWarriorGeneral g1) {
        this.generalMap.put(g1.getCompetitorId(), g1);
    }
    
    @Override
    public String getGameServerByCId(final int getcId) {
        final KfzbBattleWarrior w = this.warriorMap.get(getcId);
        if (w != null) {
            return w.getGameServer();
        }
        return null;
    }
}
