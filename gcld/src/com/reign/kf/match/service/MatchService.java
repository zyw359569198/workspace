package com.reign.kf.match.service;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.kf.match.dao.*;
import com.reign.kf.match.log.*;
import org.springframework.transaction.annotation.*;
import com.reign.framework.jdbc.orm.*;
import java.io.*;
import com.reign.kf.match.domain.*;
import com.reign.kf.match.common.util.*;
import java.util.*;
import com.reign.util.*;
import com.reign.kf.match.model.*;
import com.reign.kf.comm.param.match.*;
import com.reign.kf.comm.entity.match.*;
import com.reign.kf.match.common.*;

@Component("matchService")
public class MatchService implements IMatchService
{
    private static final Logger log;
    private static final Logger batLog;
    @Autowired
    private IMatchPlayerDao matchPlayerDao;
    @Autowired
    private IGcldMatchDao gcldMatchDao;
    @Autowired
    private IMatchReportDao matchReportDao;
    @Autowired
    private IMatchScoreDao matchScoreDao;
    @Autowired
    private IMatchPlayerGeneralDao matchPlayerGeneralDao;
    
    static {
        log = CommonLog.getLog(MatchService.class);
        batLog = new BattleLogger();
    }
    
    @Transactional
    @Override
    public void scheduleMatch(final Match match, final int turn) {
        if (turn == 1) {
            this.scheduleFirstTurnMatch(match);
        }
        else {
            this.generatorRankInfo(match, turn - 1);
            if (turn <= match.getMatchConfig().getMaxTurn()) {
                this._scheduleMatch(match, turn);
            }
            else {
                match.setMatchOver();
                final List<MatchScore> msList = this.matchScoreDao.getScoreRankInfo(match.getMatchTag(), Integer.MAX_VALUE);
                match.getCache().updateRankInfo(msList);
            }
        }
    }
    
    private void generatorRankInfo(final Match match, final int turn) {
        final List<MatchScore> msList = this.matchScoreDao.getRankInfo(match.getMatchTag(), 10);
        final List<MatchRankEntity> resultList = new ArrayList<MatchRankEntity>(msList.size());
        int index = 1;
        for (final MatchScore ms : msList) {
            final MatchRankEntity entity = new MatchRankEntity();
            final MatchPlayerEntity playerEntity = match.getCache().getMatchPlayer(ms.getCompetitorId());
            this.copyProperties(match, playerEntity, entity, ms, index++, turn);
            resultList.add(entity);
        }
        match.getCache().updateTurnRankInfo(turn, resultList);
    }
    
    private void copyProperties(final Match match, final MatchPlayerEntity playerEntity, final MatchRankEntity entity, final MatchScore ms, final int rank, final int turn) {
        entity.setCompetitorId(playerEntity.getCompetitorId());
        entity.setPlayerId(playerEntity.getPlayerId());
        entity.setPlayerName(playerEntity.getPlayerName());
        entity.setPlayerLv(playerEntity.getPlayerLv());
        entity.setPlayerPic(playerEntity.getPlayerPic());
        entity.setServerName(playerEntity.getServerName());
        entity.setServerId(playerEntity.getServerId());
        entity.setForceName(playerEntity.getForceName());
        entity.setWinNum(ms.getWinNum());
        entity.setFailNum(ms.getFailNum());
        entity.setRank(rank);
        entity.setTurn(turn);
    }
    
    private void _scheduleMatch(final Match match, final int turn) {
        final List<MatchScore> msList = this.matchScoreDao.getMatchScore(match.getMatchTag());
        final Date nowDate = new Date();
        final Calendar cg = Calendar.getInstance();
        final Date matchDate = this.getMatchTime(match, turn, nowDate);
        cg.setTime(matchDate);
        int matchNum = 0;
        int session = 1;
        for (int i = 0; i < msList.size(); i += 2) {
            final int player1 = msList.get(i).getCompetitorId();
            int player2 = 0;
            if (i + 1 < msList.size()) {
                player2 = msList.get(i + 1).getCompetitorId();
            }
            if (matchNum == match.getMatchConfig().getStepMatchNum()) {
                cg.add(13, match.getMatchConfig().getStepInterval());
                matchNum = 0;
            }
            final GcldMatch gcldMatch = this.getGcldMatch(turn, player1, player2, nowDate, cg.getTime(), match.getMatchTag(), match.getMatchConfig().getSeason(), session++, 1);
            this.gcldMatchDao.create((JdbcModel)gcldMatch);
            match.addMatch(gcldMatch);
            match.getCache().addMatch(gcldMatch);
            if (gcldMatch.getPlayer2() == 0) {
                final MatchResult matchResult = this.startNoOpponentMatch(match, gcldMatch, match.getCache().getMatchRTInfo(gcldMatch.getId()));
                match.getCache().updateMatchReport(matchResult);
            }
            ++matchNum;
        }
    }
    
    private void scheduleFirstTurnMatch(final Match match) {
        final List<MatchPlayer> signPlayers = this.matchPlayerDao.getSignPlayers(match.getMatchTag());
        final Date nowDate = new Date();
        final Calendar cg = Calendar.getInstance();
        final Date matchDate = this.getMatchTime(match, 1, nowDate);
        cg.setTime(matchDate);
        int matchNum = 0;
        int session = 1;
        for (int i = 0; i < signPlayers.size(); i += 2) {
            final int player1 = signPlayers.get(i).getId();
            int player2 = 0;
            if (i + 1 < signPlayers.size()) {
                player2 = signPlayers.get(i + 1).getId();
            }
            if (matchNum == match.getMatchConfig().getStepMatchNum()) {
                cg.add(13, match.getMatchConfig().getStepInterval());
                matchNum = 0;
            }
            final GcldMatch gcldMatch = this.getGcldMatch(1, player1, player2, nowDate, cg.getTime(), match.getMatchTag(), match.getMatchConfig().getSeason(), session++, 1);
            this.gcldMatchDao.create((JdbcModel)gcldMatch);
            match.addMatch(gcldMatch);
            match.getCache().addMatch(gcldMatch);
            if (gcldMatch.getPlayer2() == 0) {
                final MatchResult matchResult = this.startNoOpponentMatch(match, gcldMatch, match.getCache().getMatchRTInfo(gcldMatch.getId()));
                match.getCache().updateMatchReport(matchResult);
            }
            ++matchNum;
        }
    }
    
    private MatchResult startNoOpponentMatch(final Match match, final GcldMatch gcldMatch, final MatchRTInfoEntity entity) {
        gcldMatch.setWinner(gcldMatch.getPlayer1());
        gcldMatch.setFinalWinner(gcldMatch.getPlayer1());
        this.gcldMatchDao.update((JdbcModel)gcldMatch);
        final MatchReport mr = this.getMatchReport(gcldMatch, entity, null);
        this.matchReportDao.create((JdbcModel)mr);
        final MatchResult result = new MatchResult();
        result.matchId = gcldMatch.getId();
        result.reportId = null;
        result.report = null;
        result.winner = gcldMatch.getPlayer1();
        result.hasNext = match.hasNextTurn(gcldMatch.getTurn());
        result.nextTime = new Date(match.getNextTurnTime(gcldMatch.getTurn()).getTime() + 3000 * match.getMatchConfig().getMatchInterval());
        result.session = gcldMatch.getMatchSession();
        return result;
    }
    
    private MatchReport getMatchReport(final GcldMatch gcldMatch, final MatchRTInfoEntity entity, final String reportId) {
        final MatchReport matchReport = new MatchReport();
        matchReport.setMatchId(gcldMatch.getId());
        matchReport.setMatchSeason(gcldMatch.getMatchSeason());
        matchReport.setMatchSession(gcldMatch.getMatchSession());
        matchReport.setTurn(gcldMatch.getTurn());
        matchReport.setPlayer1(gcldMatch.getPlayer1());
        matchReport.setPlayerName1(entity.getPlayer1().getPlayerName());
        matchReport.setPlayer2(gcldMatch.getPlayer2());
        matchReport.setPlayerName2((entity.getPlayer2() != null) ? entity.getPlayer2().getPlayerName() : null);
        matchReport.setPlayer1Winnum(gcldMatch.getPlayer1Winnum());
        matchReport.setPlayer2Winnum(gcldMatch.getPlayer2Winnum());
        matchReport.setWinner(gcldMatch.getWinner());
        matchReport.setFinalWinner(gcldMatch.getFinalWinner());
        matchReport.setMatchNum(gcldMatch.getMatchNum());
        matchReport.setMatchTag(gcldMatch.getMatchTag());
        matchReport.setRecordTime(new Date());
        return matchReport;
    }
    
    private GcldMatch getGcldMatch(final int turn, final int player1, final int player2, final Date scheduleTime, final Date matchTime, final String matchTag, final int season, final int session, final int matchNum) {
        final GcldMatch match = new GcldMatch();
        match.setPlayer1(player1);
        match.setPlayer2(player2);
        match.setTurn(turn);
        match.setMatchSeason(season);
        match.setMatchSession(session);
        match.setMatchTag(matchTag);
        match.setMatchNum(matchNum);
        match.setPlayer1Winnum(0);
        match.setPlayer2Winnum(0);
        match.setWinner(0);
        match.setFinalWinner(0);
        match.setMatchTime(matchTime);
        match.setScheduleTime(scheduleTime);
        match.setPlayer1Inspire(0);
        match.setPlayer2Inspire(0);
        return match;
    }
    
    private Date getMatchTime(final Match match, final int turn, final Date nowDate) {
        final Calendar cg = Calendar.getInstance();
        final Date startTime = match.getMatchConfig().getMatchTime();
        if (startTime.before(cg.getTime()) && turn == 1) {
            cg.add(13, turn * match.getMatchConfig().getMatchInterval());
        }
        else if (turn == 1) {
            cg.setTime(startTime);
        }
        else {
            int second = (int)(match.getTurnLastTime(turn - 1).getTime() - cg.getTime().getTime()) / 1000 + match.getMatchConfig().getMatchInterval() + match.getMatchConfig().getPrepareMatchSec();
            if (second < 0) {
                second = 0;
            }
            cg.add(13, second);
        }
        return cg.getTime();
    }
    
    private void updateMatchScore(final Match match, final int playerId, final int winNum, final int failNum, final String reportId, final int score) {
        MatchScore ms = (MatchScore)this.matchScoreDao.read((Serializable)playerId);
        if (ms == null) {
            final MatchPlayerEntity mpe = match.getCache().getMatchPlayer(playerId);
            final String serverPlayerId = String.valueOf(mpe.getPlayerId()) + mpe.getServerId() + mpe.getServerName();
            ms = new MatchScore();
            ms.setCompetitorId(playerId);
            ms.setWinNum(winNum);
            ms.setFailNum(failNum);
            ms.setMatchTag(match.getMatchTag());
            ms.setReportId(reportId);
            ms.setScore(score);
            final MatchScore lastMs = this.matchScoreDao.getLastScore(serverPlayerId, System.currentTimeMillis() - 432000000L);
            if (lastMs == null) {
                ms.setTotalScore(0);
            }
            else {
                ms.setTotalScore(lastMs.getTotalScore() + lastMs.getScore());
            }
            ms.setUpdateTime(System.currentTimeMillis());
            this.matchScoreDao.create((JdbcModel)ms);
            match.getCache().updateMatchPlayer(playerId, ms.getWinNum(), ms.getFailNum());
            return;
        }
        ms.setScore(ms.getScore() + score);
        ms.setWinNum(ms.getWinNum() + winNum);
        ms.setFailNum(ms.getFailNum() + failNum);
        ms.setReportId(reportId);
        ms.setUpdateTime(System.currentTimeMillis());
        this.matchScoreDao.update((JdbcModel)ms);
        match.getCache().updateMatchPlayer(playerId, ms.getWinNum(), ms.getFailNum());
    }
    
    private String getQueryCode(final String matchTag, final int season, final String serverName, final String serverId, final int playerId) {
        final StringBuilder builder = new StringBuilder();
        builder.append(matchTag).append(season).append(serverName).append(serverId).append(playerId);
        return MD5SecurityUtil.code(builder.toString());
    }
    
    private void copyProperties(final MatchPlayer matchPlayer, final Match match, final SignAndSyncParam param, final String machineId) {
        matchPlayer.setPlayerId(param.getPlayerId());
        matchPlayer.setPlayerName(param.getPlayerName());
        matchPlayer.setPlayerLv(param.getPlayerLv());
        matchPlayer.setPlayerPic(param.getPlayerPic());
        matchPlayer.setServerId(param.getServerId());
        matchPlayer.setServerName(param.getServerName());
        matchPlayer.setForceName(param.getForceName());
        matchPlayer.setForceId(param.getForceId());
        matchPlayer.setSeason(match.getMatchConfig().getSeason());
        matchPlayer.setMatchTag(match.getMatchTag());
        matchPlayer.setSignTime(new Date());
        matchPlayer.setUpdateTime(new Date());
        matchPlayer.setState(0);
        matchPlayer.setQueryCode(this.getQueryCode(matchPlayer.getMatchTag(), matchPlayer.getSeason(), matchPlayer.getServerName(), matchPlayer.getServerId(), matchPlayer.getPlayerId()));
        matchPlayer.setMachineId(machineId);
    }
    
    private void copyProperties(final MatchPlayerGeneral mpg, final SignAndSyncParam param) {
        mpg.setGeneralInfo(MatchUtil.GZipByte(param.getCampInfo()));
    }
    
    @Override
    public List<MatchScheduleEntity> getMatchSchedule(final QueryMatchScheduleParam param, final String machineId) {
        final Match match = MatchManager.getInstance().getMatch(param.getMatchTag());
        if (match == null) {
            return null;
        }
        if (param.all) {
            final List<MatchScheduleEntity> resultList = new ArrayList<MatchScheduleEntity>();
            for (int i = 1; i <= param.turn; ++i) {
                resultList.addAll(match.getCache().getMatchSchedule(machineId, i));
            }
            return resultList;
        }
        return match.getCache().getMatchSchedule(machineId, param.turn);
    }
    
    @Override
    public void recover(final Match match) {
        final String matchTag = match.getMatchTag();
        final int turn = this.gcldMatchDao.getCurrentTurn(matchTag);
        final List<GcldMatch> matchList = this.gcldMatchDao.getMatchByTurn(matchTag, turn);
        final List<GcldMatch> tempList = new ArrayList<GcldMatch>();
        GcldMatch lastMatch = null;
        for (final GcldMatch gcldMatch : matchList) {
            if (gcldMatch.getWinner() == 0) {
                tempList.add(gcldMatch);
                lastMatch = null;
            }
            else {
                if (lastMatch != null && lastMatch.getMatchSession() != gcldMatch.getMatchSession() && lastMatch.getFinalWinner() == 0) {
                    tempList.add(this.createMatch(match, lastMatch));
                }
                lastMatch = gcldMatch;
            }
        }
        if (tempList.size() > 0) {
            for (final GcldMatch gcldMatch : tempList) {
                match.addMatch(gcldMatch);
            }
        }
        else {
            this.scheduleMatch(match, turn + 1);
        }
    }
    
    private GcldMatch createMatch(final Match match, final GcldMatch lastMatch) {
        final Calendar cg = Calendar.getInstance();
        cg.add(13, match.getMatchConfig().getMatchInterval() + match.getMatchConfig().getPrepareMatchSec());
        final GcldMatch nextMatch = this.getGcldMatch(lastMatch.getTurn(), lastMatch.getPlayer1(), lastMatch.getPlayer2(), new Date(), cg.getTime(), lastMatch.getMatchTag(), lastMatch.getMatchSeason(), lastMatch.getMatchSession(), lastMatch.getMatchNum() + 1);
        nextMatch.setPlayer1Winnum(lastMatch.getPlayer1Winnum());
        nextMatch.setPlayer2Winnum(lastMatch.getPlayer2Winnum());
        this.gcldMatchDao.create((JdbcModel)nextMatch);
        return nextMatch;
    }
    
    @Override
    public void handleMatchOver(final MatchResult result) {
    }
    
    @Override
    public MatchRTInfoEntity getMatchRTInfo(final QueryMatchRTInfoParam param) {
        final Match match = MatchManager.getInstance().getMatch(param.getMatchTag());
        if (match == null) {
            return null;
        }
        return match.getCache().getMatchRTInfo(param.getMatchId(), param.getVersion());
    }
    
    @Override
    public List<MatchRankEntity> getMatchTurnRank(final QueryTurnRankParam param) {
        final Match match = MatchManager.getInstance().getMatch(param.getMatchTag());
        if (match == null) {
            return null;
        }
        return match.getCache().getMatchRank(param.getTurn());
    }
    
    @Override
    public MatchScheduleEntity getMatchNumSchedule(final QueryMatchNumScheduleParam param) {
        final Match match = MatchManager.getInstance().getMatch(param.getMatchTag());
        if (match == null) {
            return null;
        }
        return match.getCache().getMatchSchedule(param.getSession(), param.getMatchNum(), param.getTurn());
    }
    
    @Override
    public List<MatchResultEntity> getMatchResult(final QueryMatchResultParam param, final String machineId) {
        final Match match = MatchManager.getInstance().getMatch(param.getMatchTag());
        if (match == null) {
            return null;
        }
        if (!match.getCache().hasGeneratorFinalMatchRank()) {
            return Collections.emptyList();
        }
        final List<MatchScore> msList = this.matchScoreDao.getMatchResult(param.getMatchTag(), machineId);
        final List<MatchResultEntity> resultList = new ArrayList<MatchResultEntity>(msList.size());
        for (final MatchScore ms : msList) {
            final MatchResultEntity entity = new MatchResultEntity();
            entity.setCompetitorId(ms.getCompetitorId());
            entity.setWinNum(ms.getWinNum());
            entity.setFailNum(ms.getFailNum());
            entity.setRank(match.getCache().getFinalMatchRank(ms.getCompetitorId()));
            entity.setScore(ms.getScore());
            entity.setTotalScore(ms.getTotalScore());
            final MatchPlayer matchPlayer = (MatchPlayer)this.matchPlayerDao.read((Serializable)ms.getCompetitorId());
            entity.setPlayerName(matchPlayer.getPlayerName());
            entity.setServerId(matchPlayer.getServerId());
            entity.setServerName(matchPlayer.getServerName());
            resultList.add(entity);
        }
        return resultList;
    }
    
    @Override
    public SignEntity sync(final SignAndSyncParam param, final String machineId) {
        final Match match = MatchManager.getInstance().getMatch(param.getMatchTag());
        if (match == null) {
            return new SignEntity(-1);
        }
        MatchRTInfoEntity rtInfoEntity = null;
        if (param.getMatchId() != 0) {
            rtInfoEntity = match.getCache().getMatchRTInfo(param.getMatchId());
            if (rtInfoEntity == null) {
                return new SignEntity(-1);
            }
            if (!CDUtil.isInCD(rtInfoEntity.getMatchTime().getTime() - 1000 * match.getMatchConfig().getPrepareMatchSec(), new Date())) {
                return new SignEntity(-2);
            }
            if (rtInfoEntity.getPlayer1().getCompetitorId() != param.getCompetitorId() && rtInfoEntity.getPlayer2() != null && rtInfoEntity.getPlayer2().getCompetitorId() != param.getCompetitorId()) {
                return new SignEntity(-3);
            }
        }
        final MatchPlayer matchPlayer = (MatchPlayer)this.matchPlayerDao.read((Serializable)param.getCompetitorId());
        this.copyProperties(matchPlayer, match, param, machineId);
        this.matchPlayerDao.update((JdbcModel)matchPlayer);
        final MatchPlayerGeneral mpg = (MatchPlayerGeneral)this.matchPlayerGeneralDao.read((Serializable)param.getCompetitorId());
        this.copyProperties(mpg, param);
        mpg.setCompetitorId(matchPlayer.getId());
        this.matchPlayerGeneralDao.update((JdbcModel)mpg);
        if (rtInfoEntity == null) {
            match.getCache().updateMatchPlayer(matchPlayer);
        }
        else {
            match.getCache().updateMatchPlayer(param.getMatchId(), matchPlayer);
        }
        match.getCache().updateMatchPlayer(matchPlayer);
        final FightData fightData = new FightData();
        fightData.competitorId = matchPlayer.getId();
        fightData.campDatas = param.getCampDatas();
        match.getCache().updateFightData(fightData);
        final SignEntity entity = new SignEntity();
        entity.setState(1);
        return entity;
    }
    
    @Override
    public MatchReportEntity getMatchReport(final QueryMatchReportParam param) {
        final Match match = MatchManager.getInstance().getMatch(param.getMatchTag());
        if (match == null) {
            return null;
        }
        return match.getCache().getMatchReport(param.getMatchId());
    }
    
    @Override
    public SignEntity sign(final SignAndSyncParam param, final String machineId) {
        final Match match = MatchManager.getInstance().getMatch(param.getMatchTag());
        if (match == null) {
            return new SignEntity(-1);
        }
        if (match.getState() != 1) {
            return new SignEntity(-2);
        }
        MatchPlayer matchPlayer = this.matchPlayerDao.getMatchPlayer(this.getQueryCode(param.getMatchTag(), match.getMatchConfig().getSeason(), param.getServerName(), param.getServerId(), param.getPlayerId()));
        if (matchPlayer != null) {
            final SignEntity entity = new SignEntity();
            entity.setState(1);
            entity.setCompetitorId(matchPlayer.getId());
            entity.setPlayerId(matchPlayer.getPlayerId());
            entity.setMatchTag(match.getMatchTag());
            return entity;
        }
        matchPlayer = new MatchPlayer();
        this.copyProperties(matchPlayer, match, param, machineId);
        this.matchPlayerDao.create((JdbcModel)matchPlayer);
        final FightData fightData = new FightData();
        fightData.competitorId = matchPlayer.getId();
        fightData.campDatas = param.getCampDatas();
        match.getCache().updateFightData(fightData);
        final MatchPlayerGeneral mpg = new MatchPlayerGeneral();
        this.copyProperties(mpg, param);
        mpg.setCompetitorId(matchPlayer.getId());
        this.matchPlayerGeneralDao.create((JdbcModel)mpg);
        match.getCache().updateMatchPlayer(matchPlayer);
        final SignEntity entity2 = new SignEntity();
        entity2.setState(1);
        entity2.setCompetitorId(matchPlayer.getId());
        entity2.setPlayerId(matchPlayer.getPlayerId());
        entity2.setMatchTag(match.getMatchTag());
        return entity2;
    }
    
    public static float getRevengeValue(final int winNum1, final int winNum2) {
        final int lose = winNum2 - winNum1;
        if (lose == 1) {
            return 0.05f;
        }
        if (lose == 2) {
            return 0.1f;
        }
        if (lose == 3) {
            return 0.15f;
        }
        return 0.0f;
    }
    
    @Override
    public InspireEntity inspire(final InspireParam param, final String machineId) {
        final Match match = MatchManager.getInstance().getMatch(param.getMatchTag());
        if (match == null) {
            return new InspireEntity(-1);
        }
        MatchRTInfoEntity rtInfoEntity = null;
        if (param.getMatchId() == 0) {
            return new InspireEntity(-4);
        }
        rtInfoEntity = match.getCache().getMatchRTInfo(param.getMatchId());
        if (rtInfoEntity == null) {
            return new InspireEntity(-1);
        }
        if (!CDUtil.isInCD(rtInfoEntity.getMatchTime().getTime() - 1000 * match.getMatchConfig().getPrepareMatchSec(), new Date())) {
            return new InspireEntity(-2);
        }
        if (rtInfoEntity.getPlayer1().getCompetitorId() != param.getCompetitorId() && rtInfoEntity.getPlayer2() != null && rtInfoEntity.getPlayer2().getCompetitorId() != param.getCompetitorId()) {
            return new InspireEntity(-3);
        }
        final GcldMatch gcldMatch = (GcldMatch)this.gcldMatchDao.read((Serializable)param.getMatchId());
        if (gcldMatch.getPlayer1() == param.getCompetitorId()) {
            if (gcldMatch.getPlayer1Inspire() > 0) {
                return new InspireEntity(-5);
            }
            gcldMatch.setPlayer1Inspire(1);
            this.gcldMatchDao.update((JdbcModel)gcldMatch);
        }
        else if (gcldMatch.getPlayer2() == param.getCompetitorId()) {
            if (gcldMatch.getPlayer2Inspire() > 0) {
                return new InspireEntity(-5);
            }
            gcldMatch.setPlayer2Inspire(1);
            this.gcldMatchDao.update((JdbcModel)gcldMatch);
        }
        final InspireEntity inspireEntity = new InspireEntity();
        inspireEntity.setState(1);
        inspireEntity.setErrorCode(1);
        inspireEntity.setCompetitorId(param.getCompetitorId());
        inspireEntity.setMatchTag(param.getMatchTag());
        return inspireEntity;
    }
    
    @Override
    public MatchResult startMatch(final Match match, final int matchId) {
        final MatchRTInfoEntity rtInfoEntity = match.getCache().getMatchRTInfo(matchId);
        final GcldMatch gcldMatch = (GcldMatch)this.gcldMatchDao.read((Serializable)matchId);
        if (gcldMatch.getPlayer2() == 0) {
            this.updateMatchScore(match, gcldMatch.getPlayer1(), 1, 0, null, 0);
            return null;
        }
        final FightData data1 = match.getCache().getFightData(gcldMatch.getPlayer1());
        final FightData data2 = match.getCache().getFightData(gcldMatch.getPlayer2());
        final float p1Revenge = getRevengeValue(rtInfoEntity.getPlayer1().getWinNum(), rtInfoEntity.getPlayer2().getWinNum());
        final float p2Revenge = getRevengeValue(rtInfoEntity.getPlayer1().getWinNum(), rtInfoEntity.getPlayer2().getWinNum());
        final MatchResult matchResult = new MatchResult();
        matchResult.matchId = matchId;
        final BattleData bd = new BattleData();
        MatchService.log.info("\u672c\u8f6e\u6240\u9700\u65f6\u95f4\uff1a[id:" + gcldMatch.getId() + " totalTime\uff1a" + bd.totalTime + "]");
        if (bd.winSide == 1) {
            gcldMatch.setWinner(data1.competitorId);
            gcldMatch.setPlayer1Winnum(gcldMatch.getPlayer1Winnum() + 1);
            matchResult.winner = data1.competitorId;
        }
        else {
            gcldMatch.setWinner(data2.competitorId);
            gcldMatch.setPlayer2Winnum(gcldMatch.getPlayer2Winnum() + 1);
            matchResult.winner = data2.competitorId;
        }
        matchResult.lastTime = new Date(System.currentTimeMillis() + bd.totalTime * 1000);
        int p1_score = 3;
        int p2_score = 3;
        if (gcldMatch.getPlayer1Winnum() >= 3) {
            p2_score = 0;
            if (gcldMatch.getPlayer2Winnum() >= 2) {
                p1_score = 2;
                p2_score = 1;
            }
            gcldMatch.setFinalWinner(data1.competitorId);
            this.updateMatchScore(match, data1.competitorId, 1, 0, matchResult.reportId, p1_score);
            this.updateMatchScore(match, data2.competitorId, 0, 1, matchResult.reportId, p2_score);
            matchResult.hasNext = match.hasNextTurn(gcldMatch.getTurn());
            matchResult.nextTime = match.getNextTurnTime(gcldMatch.getTurn());
        }
        else if (gcldMatch.getPlayer2Winnum() >= 3) {
            p1_score = 0;
            if (gcldMatch.getPlayer2Winnum() >= 2) {
                p2_score = 2;
                p1_score = 1;
            }
            gcldMatch.setFinalWinner(data2.competitorId);
            this.updateMatchScore(match, data1.competitorId, 0, 1, matchResult.reportId, p1_score);
            this.updateMatchScore(match, data2.competitorId, 1, 0, matchResult.reportId, p2_score);
            matchResult.hasNext = match.hasNextTurn(gcldMatch.getTurn());
            matchResult.nextTime = match.getNextTurnTime(gcldMatch.getTurn());
        }
        else {
            final Calendar cg = Calendar.getInstance();
            final int second = bd.totalTime + match.getMatchConfig().getMatchInterval() + match.getMatchConfig().getPrepareMatchSec();
            cg.add(13, second);
            final GcldMatch nextMatch = this.getGcldMatch(gcldMatch.getTurn(), gcldMatch.getPlayer1(), gcldMatch.getPlayer2(), new Date(), cg.getTime(), gcldMatch.getMatchTag(), gcldMatch.getMatchSeason(), gcldMatch.getMatchSession(), gcldMatch.getMatchNum() + 1);
            nextMatch.setPlayer1Winnum(gcldMatch.getPlayer1Winnum());
            nextMatch.setPlayer2Winnum(gcldMatch.getPlayer2Winnum());
            this.gcldMatchDao.create((JdbcModel)nextMatch);
            match.addMatch(nextMatch);
            match.getCache().addMatch(nextMatch);
            matchResult.hasNext = true;
            if (matchResult.hasNext) {
                matchResult.nextTime = new Date(System.currentTimeMillis() + 1000 * match.getMatchConfig().getPrepareMatchSec() + bd.totalTime * 1000);
            }
        }
        this.gcldMatchDao.update((JdbcModel)gcldMatch);
        matchResult.rtInfo = rtInfoEntity;
        matchResult.session = gcldMatch.getMatchSession();
        MatchService.log.info("\u6218\u62a5\uff1areport- [id:" + matchId + " nextTime:" + matchResult.nextTime + " report: " + matchResult.report + "]");
        final MatchReport mr = this.getMatchReport(gcldMatch, rtInfoEntity, matchResult.reportId);
        this.matchReportDao.create((JdbcModel)mr);
        return matchResult;
    }
    
    @Override
    public MatchResult handleMatchException(final Match match, final int matchId) {
        final GcldMatch gcldMatch = (GcldMatch)this.gcldMatchDao.read((Serializable)matchId);
        gcldMatch.setWinner(gcldMatch.getPlayer1());
        gcldMatch.setPlayer1Winnum(gcldMatch.getPlayer1Winnum() + 1);
        this.gcldMatchDao.update((JdbcModel)gcldMatch);
        final MatchResult result = new MatchResult();
        result.reportId = null;
        if (gcldMatch.getPlayer1Winnum() >= 3) {
            gcldMatch.setFinalWinner(gcldMatch.getPlayer1());
            int p1_score = 3;
            if (gcldMatch.getPlayer2() != 0) {
                int p2_score = 0;
                if (gcldMatch.getPlayer2Winnum() >= 2) {
                    p1_score = 2;
                    p2_score = 1;
                }
                this.updateMatchScore(match, gcldMatch.getPlayer2(), 0, 1, result.reportId, p2_score);
            }
            this.updateMatchScore(match, gcldMatch.getPlayer1(), 1, 0, result.reportId, p1_score);
            result.hasNext = match.hasNextTurn(gcldMatch.getTurn());
            result.nextTime = match.getNextTurnTime(gcldMatch.getTurn());
        }
        else {
            final Calendar cg = Calendar.getInstance();
            cg.add(13, match.getMatchConfig().getMatchInterval() + match.getMatchConfig().getPrepareMatchSec());
            final GcldMatch nextMatch = this.getGcldMatch(gcldMatch.getTurn(), gcldMatch.getPlayer1(), gcldMatch.getPlayer2(), new Date(), cg.getTime(), gcldMatch.getMatchTag(), gcldMatch.getMatchSeason(), gcldMatch.getMatchSession(), gcldMatch.getMatchNum() + 1);
            nextMatch.setPlayer1Winnum(gcldMatch.getPlayer1Winnum());
            nextMatch.setPlayer2Winnum(gcldMatch.getPlayer2Winnum());
            this.gcldMatchDao.create((JdbcModel)nextMatch);
            match.addMatch(nextMatch);
            match.getCache().addMatch(nextMatch);
            result.hasNext = true;
            if (result.hasNext) {
                result.nextTime = new Date(System.currentTimeMillis());
            }
        }
        final MatchReport mr = this.getMatchReport(gcldMatch, match.getCache().getMatchRTInfo(matchId), null);
        this.matchReportDao.create((JdbcModel)mr);
        result.matchId = gcldMatch.getId();
        result.reportId = null;
        result.winner = gcldMatch.getPlayer1();
        result.session = gcldMatch.getMatchSession();
        return result;
    }
}
