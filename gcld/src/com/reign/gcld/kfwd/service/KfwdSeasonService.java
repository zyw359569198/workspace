package com.reign.gcld.kfwd.service;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.kfwd.manager.*;
import com.reign.kf.comm.entity.kfwd.response.*;

@Component
public class KfwdSeasonService implements IKfwdSeasonService
{
    static volatile int curSeasonId;
    static volatile boolean inChangeSeason;
    Object seasonInfoLock;
    @Autowired
    IKfwdMatchService kfwdMatchService;
    @Autowired
    private KfwdMatchManager kfwdMatchManager;
    
    static {
        KfwdSeasonService.inChangeSeason = false;
    }
    
    public KfwdSeasonService() {
        this.seasonInfoLock = new Object();
    }
    
    @Override
    public boolean hasScheduledSeason(final int seasonId) {
        return KfwdSeasonService.curSeasonId == seasonId;
    }
    
    @Override
    public void createNewSeason(final KfwdSeasonInfo seasonInfo, final KfwdScheduleInfoDto scheduleInfo, final KfwdRewardResult rewardInfo, final KfwdTicketMarketListInfo ticketInfo, final KfwdRankTreasureList treasureInfo) {
        if (treasureInfo == null || treasureInfo.getList().size() == 0) {
            return;
        }
        synchronized (this.seasonInfoLock) {
            if (KfwdSeasonService.inChangeSeason) {
                // monitorexit(this.seasonInfoLock)
                return;
            }
            if (KfwdSeasonService.curSeasonId == seasonInfo.getSeasonId()) {
                // monitorexit(this.seasonInfoLock)
                return;
            }
            KfwdSeasonService.inChangeSeason = true;
        }
        // monitorexit(this.seasonInfoLock)
        try {
            this.kfwdMatchManager.clearSeason();
            this.kfwdMatchManager.setSeasonInfo(seasonInfo);
            this.kfwdMatchService.iniNewSeason(seasonInfo, scheduleInfo, rewardInfo, ticketInfo, treasureInfo);
            KfwdSeasonService.curSeasonId = seasonInfo.getSeasonId();
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }
        finally {
            KfwdSeasonService.inChangeSeason = false;
        }
        KfwdSeasonService.inChangeSeason = false;
    }
}
