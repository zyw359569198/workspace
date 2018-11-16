package com.reign.kf.gw.service;

import org.springframework.beans.factory.*;
import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.kf.gw.dao.*;
import com.reign.kf.comm.entity.*;
import java.io.*;
import com.reign.framework.jdbc.orm.*;
import org.springframework.transaction.annotation.*;
import com.reign.kf.comm.entity.gw.*;
import com.reign.kf.gw.domain.*;
import com.reign.kf.comm.param.gw.*;
import java.util.*;

@Component("gWService")
public class GWService implements IGWService, InitializingBean
{
    @Autowired
    private IAuctionInfoDao auctionInfoDao;
    @Autowired
    private ISeasonInfoDao seasonInfoDao;
    private static Date date;
    
    @Override
    public List<SeasonInfoEntity> getSeasonInfo(final int refer) {
        final List<SeasonInfoEntity> resultList = new ArrayList<SeasonInfoEntity>();
        if (refer == 1) {
            final List<SeasonInfo> siList = this.seasonInfoDao.getReadyAndCancelSeason();
            this.copyProperties(siList, resultList);
        }
        else if (refer == 2) {
            final List<SeasonInfo> siList = this.seasonInfoDao.getAssignedAndCancelSeason();
            this.copyProperties(siList, resultList);
        }
        return resultList;
    }
    
    @Transactional
    @Override
    public CommEntity updateSeasonInfo(final UpdateSeasonParam param) {
        final SeasonInfo si = (SeasonInfo)this.seasonInfoDao.read((Serializable)param.getId());
        if (param.getState() == 1) {
            si.setTag(param.getTag());
        }
        si.setState(param.getState());
        this.seasonInfoDao.update((JdbcModel)si);
        return CommEntity.SUCC_ENTITY;
    }
    
    @Override
    public AuctionInfoEntity getAuctionInfo(final int refer) {
        AuctionInfoEntity entity = null;
        if (refer == 1) {
            final List<AuctionInfo> aiList = this.auctionInfoDao.getModels();
            if (aiList.size() > 0) {
                final AuctionInfo auctionInfo = aiList.get(0);
                if (auctionInfo.getState() == 1 || auctionInfo.getState() == 2) {
                    entity = new AuctionInfoEntity();
                    this.copyProperties(auctionInfo, entity);
                }
            }
        }
        else if (refer == 2) {
            final List<AuctionInfo> aiList = this.auctionInfoDao.getModels();
            if (aiList.size() > 0) {
                final AuctionInfo auctionInfo = aiList.get(0);
                if (auctionInfo.getState() == 0 || auctionInfo.getState() == 1 || auctionInfo.getState() == 2) {
                    entity = new AuctionInfoEntity();
                    this.copyProperties(auctionInfo, entity);
                }
            }
        }
        return entity;
    }
    
    @Transactional
    @Override
    public CommEntity updateAuctionInfo(final UpdateAuctionParam param) {
        final AuctionInfo ai = (AuctionInfo)this.auctionInfoDao.read((Serializable)param.getId());
        ai.setState(param.getState());
        this.auctionInfoDao.update((JdbcModel)ai);
        return CommEntity.SUCC_ENTITY;
    }
    
    private void copyProperties(final AuctionInfo auctionInfo, final AuctionInfoEntity entity) {
        entity.setId(auctionInfo.getId());
        entity.setStartHour(auctionInfo.getStartHour());
        entity.setEndHour(auctionInfo.getEndHour());
        entity.setBaseHour(auctionInfo.getBaseHour());
        entity.setState(auctionInfo.getState());
        entity.setRule(entity.getRule());
        entity.setHost(auctionInfo.getHost());
        entity.setPort(auctionInfo.getPort());
        entity.setScanHour(auctionInfo.getScanHour());
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        GWService.date = new Date();
    }
    
    private void copyProperties(final List<SeasonInfo> siList, final List<SeasonInfoEntity> resultList) {
        for (final SeasonInfo si : siList) {
            final SeasonInfoEntity sie = new SeasonInfoEntity();
            this.copyProperties(si, sie);
            resultList.add(sie);
        }
    }
    
    private void copyProperties(final SeasonInfo si, final SeasonInfoEntity sie) {
        sie.setId(si.getId());
        sie.setSeason(si.getSeason());
        sie.setSignStartTime(si.getSignStartTime());
        sie.setSignEndTime(new Date(GWService.date.getTime() + 180000L));
        sie.setMatchTime(new Date(GWService.date.getTime() + 240000L));
        sie.setMatchRule(si.getMatchRule());
        sie.setHost(si.getHost());
        sie.setPort(si.getPort());
        sie.setState(si.getState());
        sie.setMatchServer(si.getMatchServer());
        sie.setTag(si.getTag());
        sie.setMaxTurn(si.getMaxTurn());
        sie.setRewardType(si.getRewardType());
    }
}
