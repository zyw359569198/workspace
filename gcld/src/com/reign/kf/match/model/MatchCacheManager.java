package com.reign.kf.match.model;

import com.reign.kf.match.service.*;
import com.reign.kf.comm.entity.match.*;
import java.util.concurrent.*;
import java.io.*;
import com.reign.kf.match.domain.*;
import com.reign.kf.match.common.util.*;
import com.reign.util.*;
import java.util.*;
import com.reign.kf.comm.param.match.*;
import com.reign.kf.comm.protocol.*;

public class MatchCacheManager
{
    private IDataGetter dataGetter;
    private ConcurrentMap<Integer, Vector<MatchScheduleEntity>> matchScheduleCache;
    private ConcurrentMap<Integer, Vector<MatchScheduleEntity>> matchNumScheduleCache;
    private ConcurrentMap<String, Set<Tuple<String, String>>> serversCache;
    private ConcurrentMap<Integer, FightData> fightDataCache;
    private ConcurrentMap<Integer, MatchPlayerEntity> matchPlayerCache;
    private ConcurrentMap<Integer, MatchRTInfoEntity> matchRTInfoCache;
    private ConcurrentMap<Integer, MatchReportEntity> matchReportCache;
    private ConcurrentMap<Integer, List<MatchRankEntity>> matchRankCache;
    private ConcurrentMap<Integer, Integer> finalRankCache;
    private String matchTag;
    private Object[] locks;
    
    public MatchCacheManager() {
        this.matchScheduleCache = new ConcurrentHashMap<Integer, Vector<MatchScheduleEntity>>();
        this.matchNumScheduleCache = new ConcurrentHashMap<Integer, Vector<MatchScheduleEntity>>();
        this.serversCache = new ConcurrentHashMap<String, Set<Tuple<String, String>>>();
        this.fightDataCache = new ConcurrentHashMap<Integer, FightData>();
        this.matchPlayerCache = new ConcurrentHashMap<Integer, MatchPlayerEntity>();
        this.matchRTInfoCache = new ConcurrentHashMap<Integer, MatchRTInfoEntity>();
        this.matchReportCache = new ConcurrentHashMap<Integer, MatchReportEntity>();
        this.matchRankCache = new ConcurrentHashMap<Integer, List<MatchRankEntity>>();
        this.finalRankCache = new ConcurrentHashMap<Integer, Integer>();
        this.locks = new Object[2048];
        for (int i = 0; i < this.locks.length; ++i) {
            this.locks[i] = new Object();
        }
    }
    
    public MatchCacheManager(final String matchTag, final Match match, final IDataGetter dataGetter) {
        this.matchScheduleCache = new ConcurrentHashMap<Integer, Vector<MatchScheduleEntity>>();
        this.matchNumScheduleCache = new ConcurrentHashMap<Integer, Vector<MatchScheduleEntity>>();
        this.serversCache = new ConcurrentHashMap<String, Set<Tuple<String, String>>>();
        this.fightDataCache = new ConcurrentHashMap<Integer, FightData>();
        this.matchPlayerCache = new ConcurrentHashMap<Integer, MatchPlayerEntity>();
        this.matchRTInfoCache = new ConcurrentHashMap<Integer, MatchRTInfoEntity>();
        this.matchReportCache = new ConcurrentHashMap<Integer, MatchReportEntity>();
        this.matchRankCache = new ConcurrentHashMap<Integer, List<MatchRankEntity>>();
        this.finalRankCache = new ConcurrentHashMap<Integer, Integer>();
        this.locks = new Object[2048];
        this.dataGetter = dataGetter;
        for (int i = 0; i < this.locks.length; ++i) {
            this.locks[i] = new Object();
        }
        this.matchTag = matchTag;
    }
    
    public void clear() {
        this.fightDataCache.clear();
        this.finalRankCache.clear();
        this.matchNumScheduleCache.clear();
        this.matchPlayerCache.clear();
        this.matchRankCache.clear();
        this.matchReportCache.clear();
        this.matchRTInfoCache.clear();
        this.matchScheduleCache.clear();
        this.serversCache.clear();
    }
    
    public void updateRankInfo(final List<MatchScore> msList) {
        int index = 1;
        for (final MatchScore ms : msList) {
            this.finalRankCache.put(ms.getCompetitorId(), index++);
        }
    }
    
    public void updateTurnRankInfo(final int turn, final List<MatchRankEntity> resultList) {
        this.matchRankCache.put(turn, resultList);
    }
    
    public void updateMatchReport(final MatchResult result) {
        final MatchReportEntity entity = new MatchReportEntity();
        this.copyProperties(result, entity);
        this.matchReportCache.put(result.matchId, entity);
    }
    
    private void copyProperties(final MatchResult result, final MatchReportEntity entity) {
        entity.setMatchId(result.matchId);
        entity.setWinner(result.winner);
        entity.setReport(result.report);
        entity.setHasNext(result.hasNext);
        entity.setSession(result.session);
        entity.setNextTime(result.nextTime);
    }
    
    public MatchRTInfoEntity getMatchRTInfo(final int matchId) {
        final MatchRTInfoEntity entity = this.matchRTInfoCache.get(matchId);
        if (entity == null) {
            this.buildMatchRTInfoEntity(matchId);
            return this.matchRTInfoCache.get(matchId);
        }
        return entity;
    }
    
    private void buildMatchRTInfoEntity(final int matchId) {
        synchronized (this.locks[matchId % this.locks.length]) {
            MatchRTInfoEntity entity = this.matchRTInfoCache.get(matchId);
            if (entity == null) {
                entity = new MatchRTInfoEntity();
                final GcldMatch match = (GcldMatch)this.dataGetter.getGcldMatchDao().read((Serializable)matchId);
                final MatchPlayerEntity player1 = this.getMatchPlayer(match.getPlayer1());
                MatchPlayerEntity player2 = null;
                if (match.getPlayer2() != 0) {
                    player2 = this.getMatchPlayer(match.getPlayer2());
                }
                this.copyProperties(player1, player2, match, entity);
                this.matchRTInfoCache.put(matchId, entity);
            }
        }
        // monitorexit(this.locks[matchId % this.locks.length])
    }
    
    public MatchPlayerEntity getMatchPlayer(final int competitorId) {
        MatchPlayerEntity entity = this.matchPlayerCache.get(competitorId);
        if (entity == null) {
            entity = new MatchPlayerEntity();
            final MatchPlayer matchPlayer = (MatchPlayer)this.dataGetter.getMatchPlayerDao().read((Serializable)competitorId);
            final MatchPlayerGeneral matchPlayerGeneral = (MatchPlayerGeneral)this.dataGetter.getMatchPlayerGeneralDao().read((Serializable)competitorId);
            final MatchScore matchScore = (MatchScore)this.dataGetter.getMatchScoreDao().read((Serializable)competitorId);
            this.copyProperties(matchPlayer, matchPlayerGeneral, matchScore, entity);
            final MatchPlayerEntity temp = this.matchPlayerCache.putIfAbsent(competitorId, entity);
            entity = ((temp == null) ? entity : temp);
        }
        return entity;
    }
    
    private void copyProperties(final MatchPlayer matchPlayer, final MatchPlayerGeneral matchPlayerGeneral, final MatchScore matchScore, final MatchPlayerEntity entity) {
        entity.setCompetitorId(matchPlayer.getId());
        entity.setPlayerId(matchPlayer.getPlayerId());
        entity.setPlayerName(matchPlayer.getPlayerName());
        entity.setPlayerLv(matchPlayer.getPlayerLv());
        entity.setPlayerPic(matchPlayer.getPlayerPic());
        entity.setServerName(matchPlayer.getServerName());
        entity.setServerId(matchPlayer.getServerId());
        entity.setForceName(matchPlayer.getForceName());
        entity.setForceId(matchPlayer.getForceId());
        entity.setCampInfo(MatchUtil.DeGZipByte(matchPlayerGeneral.getGeneralInfo()));
        if (matchScore != null) {
            entity.setWinNum(matchScore.getWinNum());
            entity.setFailNum(matchScore.getFailNum());
        }
    }
    
    private void copyProperties(final MatchPlayerEntity player1, final MatchPlayerEntity player2, final GcldMatch gcldMatch, final MatchRTInfoEntity entity) {
        entity.setPlayer1(player1);
        entity.setPlayer2(player2);
        entity.setMatchId(gcldMatch.getId());
        entity.setMatchNum(gcldMatch.getMatchNum());
        entity.setSeason(gcldMatch.getMatchSeason());
        entity.setSession(gcldMatch.getMatchSession());
        entity.setTurn(gcldMatch.getTurn());
        entity.setMatchTime(gcldMatch.getMatchTime());
        entity.setPlayer1WinNum(gcldMatch.getPlayer1Winnum());
        entity.setPlayer2WinNum(gcldMatch.getPlayer2Winnum());
        entity.setVersion(1);
    }
    
    public void addMatch(final GcldMatch match) {
        final MatchScheduleEntity entity = new MatchScheduleEntity();
        this.copyProperties(match, entity);
        if (match.getMatchNum() > 1) {
            Vector<MatchScheduleEntity> msList = this.matchNumScheduleCache.get(match.getTurn());
            if (msList == null) {
                msList = new Vector<MatchScheduleEntity>();
                final Vector<MatchScheduleEntity> temp = this.matchNumScheduleCache.putIfAbsent(match.getTurn(), msList);
                msList = ((temp != null) ? temp : msList);
            }
            msList.add(entity);
            return;
        }
        Vector<MatchScheduleEntity> msList = this.matchScheduleCache.get(match.getTurn());
        if (msList == null) {
            msList = new Vector<MatchScheduleEntity>();
            final Vector<MatchScheduleEntity> temp = this.matchScheduleCache.putIfAbsent(match.getTurn(), msList);
            msList = ((temp != null) ? temp : msList);
        }
        msList.add(entity);
    }
    
    private void copyProperties(final GcldMatch match, final MatchScheduleEntity entity) {
        entity.setMatchId(match.getId());
        entity.setPlayer1(match.getPlayer1());
        entity.setPlayer2(match.getPlayer2());
        entity.setMatchNum(match.getMatchNum());
        entity.setTurn(match.getTurn());
        entity.setMatchCD(CDUtil.getCD(match.getMatchTime(), new Date()));
        entity.setSession(match.getMatchSession());
    }
    
    public void clearMatchCache(final int matchId) {
        this.matchRTInfoCache.remove(matchId);
    }
    
    public void updateMatchPlayer(final int competitorId, final int winNum, final int failNum) {
        final MatchPlayerEntity entity = this.getMatchPlayer(competitorId);
        if (entity != null) {
            entity.setWinNum(winNum);
            entity.setFailNum(failNum);
        }
    }
    
    public MatchPlayerEntity updateMatchPlayer(final MatchPlayer matchPlayer) {
        MatchPlayerEntity entity = this.matchPlayerCache.get(matchPlayer.getId());
        if (entity == null) {
            entity = new MatchPlayerEntity();
        }
        final MatchPlayerGeneral matchPlayerGeneral = (MatchPlayerGeneral)this.dataGetter.getMatchPlayerGeneralDao().read((Serializable)matchPlayer.getId());
        this.copyProperties(matchPlayer, matchPlayerGeneral, entity);
        this.matchPlayerCache.put(matchPlayer.getId(), entity);
        return entity;
    }
    
    private void copyProperties(final MatchPlayer matchPlayer, final MatchPlayerGeneral matchPlayerGeneral, final MatchPlayerEntity entity) {
        entity.setCompetitorId(matchPlayer.getId());
        entity.setPlayerId(matchPlayer.getPlayerId());
        entity.setPlayerName(matchPlayer.getPlayerName());
        entity.setPlayerLv(matchPlayer.getPlayerLv());
        entity.setPlayerPic(matchPlayer.getPlayerPic());
        entity.setServerName(matchPlayer.getServerName());
        entity.setServerId(matchPlayer.getServerId());
        entity.setForceName(matchPlayer.getForceName());
        entity.setForceId(matchPlayer.getForceId());
        entity.setCampInfo(MatchUtil.DeGZipByte(matchPlayerGeneral.getGeneralInfo()));
    }
    
    public void updateFightData(final FightData fightData) {
        this.fightDataCache.put(fightData.competitorId, fightData);
    }
    
    public List<MatchScheduleEntity> getMatchSchedule(final String machineId, final int turn) {
        final Set<Tuple<String, String>> serverSet = this.getServers(machineId);
        return this.getMatchSchedule(serverSet, turn);
    }
    
    public Set<Tuple<String, String>> getServers(final String machineId) {
        Set<Tuple<String, String>> serverSet = this.serversCache.get(machineId);
        if (serverSet == null) {
            serverSet = this.dataGetter.getMatchPlayerDao().getServerSet(machineId, this.matchTag);
            final Set<Tuple<String, String>> temp = this.serversCache.putIfAbsent(machineId, serverSet);
            serverSet = ((temp == null) ? serverSet : temp);
        }
        return serverSet;
    }
    
    private Vector<MatchScheduleEntity> convertMatchListToScheduleList(final List<GcldMatch> matchList) {
        final Vector<MatchScheduleEntity> reList = new Vector<MatchScheduleEntity>(matchList.size());
        for (final GcldMatch match : matchList) {
            final MatchScheduleEntity entity = new MatchScheduleEntity();
            this.copyProperties(match, entity);
            reList.add(entity);
        }
        return reList;
    }
    
    private List<MatchScheduleEntity> getMatchSchedule(final Set<Tuple<String, String>> serverSet, final int turn) {
        Vector<MatchScheduleEntity> scheduleList = this.matchScheduleCache.get(turn);
        if (scheduleList == null) {
            final List<GcldMatch> matchList = this.dataGetter.getGcldMatchDao().getMatch(turn, 1, this.matchTag);
            scheduleList = this.convertMatchListToScheduleList(matchList);
            final Vector<MatchScheduleEntity> temp = this.matchScheduleCache.putIfAbsent(turn, scheduleList);
            scheduleList = ((temp == null) ? scheduleList : temp);
        }
        MatchScheduleEntity oneEntity = null;
        final List<MatchScheduleEntity> resultList = new ArrayList<MatchScheduleEntity>();
        for (int i = 0; i < scheduleList.size(); ++i) {
            final MatchScheduleEntity entity = scheduleList.get(i);
            if (oneEntity == null) {
                oneEntity = entity;
            }
            final MatchPlayerEntity matchPlayer1Entity = this.getMatchPlayer(entity.getPlayer1());
            if (serverSet.contains(new Tuple(matchPlayer1Entity.getServerName(), matchPlayer1Entity.getServerId()))) {
                resultList.add(entity);
            }
            else if (entity.getPlayer2() != 0) {
                final MatchPlayerEntity matchPlayer2Entity = this.getMatchPlayer(entity.getPlayer2());
                if (serverSet.contains(new Tuple(matchPlayer2Entity.getServerName(), matchPlayer2Entity.getServerId()))) {
                    resultList.add(entity);
                }
            }
        }
        if (resultList.size() == 0 && oneEntity != null) {
            resultList.add(oneEntity);
        }
        return resultList;
    }
    
    public MatchRTInfoEntity getMatchRTInfo(final int matchId, final int version) {
        final MatchRTInfoEntity entity = this.getMatchRTInfo(matchId);
        return (entity.getVersion() > version) ? entity : null;
    }
    
    public List<MatchRankEntity> getMatchRank(final int turn) {
        return this.matchRankCache.get(turn);
    }
    
    public MatchScheduleEntity getMatchSchedule(final int session, final int matchNum, final int turn) {
        List<MatchScheduleEntity> msList = this.matchNumScheduleCache.get(turn);
        if (msList == null) {
            synchronized (this.locks[turn % this.locks.length]) {
                if (this.matchNumScheduleCache.get(turn) == null) {
                    final List<GcldMatch> matchList = this.dataGetter.getGcldMatchDao().getMatchNumMatch(turn, this.matchTag);
                    for (final GcldMatch gcldMatch : matchList) {
                        this.addMatch(gcldMatch);
                    }
                }
                msList = this.matchNumScheduleCache.get(turn);
            }
            // monitorexit(this.locks[turn % this.locks.length])
        }
        if (msList != null) {
            for (final MatchScheduleEntity entity : msList) {
                if (entity.getSession() == session && entity.getMatchNum() == matchNum) {
                    return entity;
                }
            }
        }
        final GcldMatch gcldMatch2 = this.dataGetter.getGcldMatchDao().getMatch(turn, matchNum, session, this.matchTag);
        if (gcldMatch2 != null) {
            this.addMatch(gcldMatch2);
            msList = this.matchNumScheduleCache.get(turn);
            if (msList != null) {
                for (final MatchScheduleEntity entity2 : msList) {
                    if (entity2.getSession() == session && entity2.getMatchNum() == matchNum) {
                        return entity2;
                    }
                }
            }
        }
        return null;
    }
    
    public boolean hasGeneratorFinalMatchRank() {
        return this.finalRankCache.size() > 0;
    }
    
    public Integer getFinalMatchRank(final int competitorId) {
        return this.finalRankCache.get(competitorId);
    }
    
    public void updateMatchPlayer(final int matchId, final MatchPlayer matchPlayer) {
        final MatchPlayerEntity matchPlayerEntity = this.updateMatchPlayer(matchPlayer);
        synchronized (this.locks[matchId % this.locks.length]) {
            final MatchRTInfoEntity entity = this.getMatchRTInfo(matchId);
            if (entity.getPlayer1().getCompetitorId() == matchPlayer.getId()) {
                entity.setPlayer1(matchPlayerEntity);
            }
            else if (entity.getPlayer2() != null && entity.getPlayer2().getCompetitorId() == matchPlayer.getId()) {
                entity.setPlayer2(matchPlayerEntity);
            }
            entity.setVersion(entity.getVersion() + 1);
        }
        // monitorexit(this.locks[matchId % this.locks.length])
    }
    
    public MatchReportEntity getMatchReport(final int matchId) {
        final MatchReportEntity entity = this.matchReportCache.get(matchId);
        if (entity == null) {
            return null;
        }
        return entity;
    }
    
    public FightData getFightData(final int competitorId) {
        final FightData data = this.fightDataCache.get(competitorId);
        if (data == null) {
            this.buildFighData(competitorId);
            return this.fightDataCache.get(competitorId);
        }
        return data;
    }
    
    private void buildFighData(final int competitorId) {
        synchronized (this.locks[competitorId % this.locks.length]) {
            FightData data = this.fightDataCache.get(competitorId);
            if (data == null) {
                data = new FightData();
                final MatchPlayerGeneral mpg = (MatchPlayerGeneral)this.dataGetter.getMatchPlayerGeneralDao().read((Serializable)competitorId);
                data.competitorId = competitorId;
                data.campDatas = MatchUtil.DeGZipByte(mpg.getGeneralInfo(), Types.JAVATYPE_CAMPARMYDATALIST);
                this.fightDataCache.put(competitorId, data);
            }
        }
        // monitorexit(this.locks[competitorId % this.locks.length])
    }
}
