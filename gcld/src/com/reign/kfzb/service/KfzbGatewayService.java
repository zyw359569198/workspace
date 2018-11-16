package com.reign.kfzb.service;

import org.springframework.beans.factory.*;
import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.kf.gw.kfwd.dao.*;
import com.reign.kfzb.dao.*;
import org.springframework.context.*;
import org.apache.commons.logging.*;
import com.reign.kf.comm.entity.*;
import com.reign.kf.gw.kfwd.domain.*;
import java.util.*;
import org.springframework.beans.*;
import com.reign.framework.hibernate.model.*;
import org.springframework.transaction.annotation.*;
import com.reign.kfzb.dto.response.*;
import com.reign.kfzb.dto.request.*;
import com.reign.kfzb.domain.*;

@Component
public class KfzbGatewayService implements IKfzbGatewayService, InitializingBean, ApplicationContextAware
{
    private static Log seasonInfoLog;
    @Autowired
    IKfzbRewardInfoDDao kfzbRewardInfoDDao;
    @Autowired
    IKfzbSeasonInfoDDao kfzbSeasonInfoDDao;
    @Autowired
    IKfzbGameServerLimitDao kfzbGameServerLimitDao;
    @Autowired
    IMatchServerInfoDao matchServerInfoDao;
    @Autowired
    IKfzbPlayerLimitDDao kfzbPlayerLimitDDao;
    @Autowired
    IKfzbWinnerInfoDDao kfzbWinnerInfoDDao;
    @Autowired
    IKfzbTreasureRewardDDao kfzbTreasureRewardDDao;
    @Autowired
    IKfzbFeastService kfzbFeastService;
    ApplicationContext context;
    IKfzbGatewayService self;
    
    static {
        KfzbGatewayService.seasonInfoLog = LogFactory.getLog("com.xinyun.kfzbSeasonInfo");
    }
    
    @Override
    public KfzbSeasonInfo handleSeasonInfo(final GameServerEntity gs) {
        final KfzbSeasonInfo sInfo = new KfzbSeasonInfo();
        final KfzbSeasonInfoD si = this.kfzbSeasonInfoDDao.getActiveSeasonInfo();
        if (si == null || si.getGlobalState() != 2) {
            return null;
        }
        if (si.getUseLimit() == 1 && gs != null) {
            final KfzbGameServerLimit limitInfo = this.kfzbGameServerLimitDao.getGameServerByName(gs.getServerKey());
            if (limitInfo == null) {
                return null;
            }
        }
        BeanUtils.copyProperties(si, sInfo);
        final MatchServerInfo matchServerInfo = this.matchServerInfoDao.getMatchInfoByMatchId(si.getMatchId());
        if (matchServerInfo == null) {
            return null;
        }
        sInfo.setMatchName(matchServerInfo.getMatchName());
        sInfo.setMatchAdress(matchServerInfo.getMatchAdress());
        return sInfo;
    }
    
    @Override
    public KfzbRewardInfo handleRewardInfo() {
        final KfzbRewardInfoD ri = this.kfzbRewardInfoDDao.getRewardInfo();
        final KfzbRewardInfo rInfo = new KfzbRewardInfo();
        BeanUtils.copyProperties(ri, rInfo);
        final List<KfzbTreasureRewardD> treasureInfoList = this.kfzbTreasureRewardDDao.getModels();
        final List<KfzbTreasureReward> trList = new ArrayList<KfzbTreasureReward>();
        for (final KfzbTreasureRewardD td : treasureInfoList) {
            final KfzbTreasureReward tr = new KfzbTreasureReward();
            BeanUtils.copyProperties(td, tr);
            trList.add(tr);
        }
        rInfo.setList(trList);
        return rInfo;
    }
    
    @Override
    public synchronized void processKfgzSeasonInfo() {
        KfzbGatewayService.seasonInfoLog.info("do process kfgz");
        this.self.clearFinshedSeasonInfo();
        final KfzbSeasonInfoD lastSeasonInfo = this.kfzbSeasonInfoDDao.getLastSeaonInfo();
        this.kfzbFeastService.processLastSeasonInfo(lastSeasonInfo);
        final KfzbSeasonInfoD si = this.kfzbSeasonInfoDDao.getActiveSeasonInfo();
        if (si == null || si.getSeasonId() == 0) {
            return;
        }
        if (si.getGlobalState() == 1) {
            final long doScheduleInterVal = 43200000L;
            final long nowtime = System.currentTimeMillis();
            if (si.getActiveTime() != null && si.getActiveTime().getTime() < nowtime + doScheduleInterVal) {
                this.self.scheduleNewSeason(si);
            }
        }
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        this.self = (IKfzbGatewayService)this.context.getBean("kfzbGatewayService");
    }
    
    @Override
	public void setApplicationContext(final ApplicationContext arg0) throws BeansException {
        this.context = arg0;
    }
    
    @Transactional
    @Override
    public void clearFinshedSeasonInfo() {
        final List<KfzbSeasonInfoD> list = this.kfzbSeasonInfoDDao.getNeedEndSeasonInfo();
        for (final KfzbSeasonInfoD si : list) {
            si.setGlobalState(4);
            this.kfzbSeasonInfoDDao.update((IModel)si);
        }
    }
    
    @Transactional
    @Override
    public void scheduleNewSeason(final KfzbSeasonInfoD si) {
        si.setGlobalState(2);
        this.kfzbSeasonInfoDDao.update((IModel)si);
        KfzbGatewayService.seasonInfoLog.info("finishDoZb");
    }
    
    @Override
    public KfzbPlayerLimitInfo handlePlayerLimitInfo() {
        final List<KfzbPlayerLimitD> list = this.kfzbPlayerLimitDDao.getModels();
        final KfzbPlayerLimitInfo res = new KfzbPlayerLimitInfo();
        for (final KfzbPlayerLimitD limit : list) {
            final KfzbPlayerLimit limitRes = new KfzbPlayerLimit();
            BeanUtils.copyProperties(limit, limitRes);
            res.getList().add(limitRes);
        }
        return res;
    }
    
    @Transactional
    @Override
    public Integer handleWinnerInfo(final KfzbWinnerInfo winInfo) {
        final int seasonId = winInfo.getSeasonId();
        if (winInfo.getList().size() == 16) {
            this.kfzbWinnerInfoDDao.deleteAllSeasonInfo(seasonId);
            for (final KfzbTopPlayerInfo pInfo : winInfo.getList()) {
                final KfzbWinnerInfoD winnerInfoD = new KfzbWinnerInfoD();
                BeanUtils.copyProperties(pInfo, winnerInfoD);
                winnerInfoD.setSeasonId(seasonId);
                this.kfzbWinnerInfoDDao.create((IModel)winnerInfoD);
            }
            return 1;
        }
        return 0;
    }
    
    @Override
    public KfzbWinnerInfo handleGetTop16PlayerInfo() {
        final List<KfzbWinnerInfoD> top16list = this.kfzbWinnerInfoDDao.getTop16PlayerInfo();
        if (top16list == null || top16list.size() != 16) {
            return null;
        }
        int pos = 1;
        final KfzbWinnerInfo winInfo = new KfzbWinnerInfo();
        for (final KfzbWinnerInfoD winnerInfoD : top16list) {
            final KfzbTopPlayerInfo topPlayerInfo = new KfzbTopPlayerInfo();
            BeanUtils.copyProperties(winnerInfoD, topPlayerInfo);
            topPlayerInfo.setPos(pos);
            winInfo.setSeasonId(winnerInfoD.getSeasonId());
            winInfo.getList().add(topPlayerInfo);
            ++pos;
        }
        return winInfo;
    }
}
