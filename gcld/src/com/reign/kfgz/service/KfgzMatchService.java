package com.reign.kfgz.service;

import org.springframework.stereotype.*;
import com.reign.kfgz.control.*;
import com.reign.kfgz.resource.*;
import com.reign.kfgz.dto.*;
import com.reign.kf.comm.param.match.*;
import org.springframework.beans.*;
import com.reign.kf.match.common.*;
import com.reign.kfgz.comm.*;
import com.reign.kfgz.team.*;
import org.codehaus.jackson.*;
import java.io.*;
import com.reign.kf.match.sdata.cache.*;
import com.reign.kf.match.sdata.domain.*;
import java.util.concurrent.*;
import com.reign.kfgz.dto.request.*;
import com.reign.kfgz.constants.*;
import com.reign.kfgz.world.*;
import com.reign.kfgz.rank.*;
import java.util.*;
import com.reign.kfgz.dto.response.*;

@Component
public class KfgzMatchService implements IKfgzMatchService
{
    @Override
    public KfgzSignResult doSignUp(final KfgzSignInfoParam signInfo, final String gameServer) {
        if (signInfo == null) {
            return null;
        }
        final KfgzPlayerInfo playerInfo = signInfo.getPlayerInfo();
        if (playerInfo.getCompetitorId() == null || playerInfo.getCompetitorId() == 0) {
            final KfgzSignResult res = new KfgzSignResult();
            res.setPlayerId(playerInfo.getPlayerId());
            res.setCompetitor(0);
            res.setState(2);
            return res;
        }
        final int seasonId = signInfo.getSeasonId();
        final int gzId = signInfo.getGzId();
        final KfgzBaseInfo baseInfo = KfgzManager.getGzBaseInfoById(gzId);
        if (baseInfo == null) {
            final KfgzSignResult res2 = new KfgzSignResult();
            res2.setPlayerId(playerInfo.getPlayerId());
            res2.setCompetitor(0);
            res2.setState(2);
            return res2;
        }
        boolean checkSuc = true;
        if (baseInfo.getSeasonId() != seasonId) {
            checkSuc = false;
        }
        int forceId = 0;
        if (gameServer.equals(baseInfo.getGameServer1()) && playerInfo.getNation() == baseInfo.getNation1()) {
            forceId = 1;
        }
        else if (gameServer.equals(baseInfo.getGameServer2()) && playerInfo.getNation() == baseInfo.getNation2()) {
            forceId = 2;
        }
        if (forceId == 0) {
            checkSuc = false;
        }
        if (baseInfo.getState() != 1) {
            checkSuc = false;
        }
        if (baseInfo.getState() == 2) {
            final KfgzSignResult res3 = new KfgzSignResult();
            res3.setPlayerId(playerInfo.getPlayerId());
            res3.setCompetitor(0);
            res3.setState(5);
            return res3;
        }
        if (!checkSuc) {
            final KfgzSignResult res3 = new KfgzSignResult();
            res3.setPlayerId(playerInfo.getPlayerId());
            res3.setCompetitor(0);
            res3.setState(2);
            return res3;
        }
        final int competitorId = playerInfo.getCompetitorId();
        final KfgzSignResult res4 = new KfgzSignResult();
        res4.setPlayerId(playerInfo.getPlayerId());
        res4.setCompetitor(competitorId);
        res4.setForceId(forceId);
        res4.setWorldId(KfgzManager.getWorldIdByGzId(gzId));
        KfPlayerInfo oldPlayerInfo = KfgzPlayerManager.getPlayerByCId(competitorId);
        if (oldPlayerInfo != null && oldPlayerInfo.getGzId() != gzId) {
            KfgzPlayerManager.removeByCId(oldPlayerInfo.getCompetitorId(), oldPlayerInfo.getGzId());
            oldPlayerInfo = null;
        }
        if (oldPlayerInfo == null) {
            KfPlayerInfo ca = null;
            try {
                ca = this.buildNewKfgzPlayerInfo(signInfo, gzId, forceId);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            res4.setState(1);
            res4.setVersion(KfgzResChangeManager.getNowResourceVersion());
        }
        else {
            try {
                this.syncGeneralInfo(signInfo, gzId, oldPlayerInfo, res4);
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
            res4.setState(3);
        }
        return res4;
    }
    
    private void syncGeneralInfo(final KfgzSignInfoParam signInfo, final int gzId, final KfPlayerInfo pInfo, final KfgzSignResult res) throws JsonProcessingException, IOException {
        final List<CampArmyParam> capaList = new ArrayList<CampArmyParam>();
        final List<Integer> outGenerals = new ArrayList<Integer>();
        outGenerals.addAll(pInfo.getgMap().keySet());
        final List<String> messageList = new ArrayList<String>();
        signInfo.setCampList(pInfo.getForceId());
        CampArmyParam[] campDatas;
        for (int length = (campDatas = signInfo.getCampDatas()).length, k = 0; k < length; ++k) {
            final CampArmyParam capa = campDatas[k];
            final KfGeneralInfo gInfo = pInfo.getgMap().get(capa.getGeneralId());
            if (gInfo == null) {
                capaList.add(capa);
            }
            else {
                outGenerals.remove(new Integer(gInfo.getgId()));
                if (gInfo.getState() != 3) {
                    final KfCampArmy campArmy = gInfo.getCampArmy();
                    final KfTeam kt = gInfo.getTeam();
                    kt.teamLock.writeLock().lock();
                    try {
                        final KfTeam kt2 = gInfo.getTeam();
                        if (kt2.getTeamId() != kt.getTeamId() || gInfo.getState() == 3) {
                            continue;
                        }
                        final int oldArmyHp = campArmy.getArmyHp();
                        final int oldArmyHpOrg = campArmy.getArmyHpOrg();
                        final boolean oldTacticRemain = campArmy.isTacticRemain();
                        BeanUtils.copyProperties(capa, campArmy, new String[] { "terrainAttDefAdd", "armyHp", "teamEffect", "playerId" });
                        campArmy.setPlayerId(pInfo.getCompetitorId());
                        if (oldArmyHp >= oldArmyHpOrg) {
                            campArmy.setArmyHp(campArmy.getArmyHpOrg());
                        }
                        else if (campArmy.getArmyHp() >= campArmy.getArmyHpOrg()) {
                            int newHp = campArmy.getArmyHpOrg() - 100;
                            if (newHp <= 3) {
                                newHp = 3;
                            }
                            campArmy.setArmyHp(newHp);
                        }
                        campArmy.setTacticRemain(oldTacticRemain);
                    }
                    finally {
                        kt.teamLock.writeLock().unlock();
                    }
                    kt.teamLock.writeLock().unlock();
                }
            }
        }
        for (int i = 0, max = Math.max(capaList.size(), outGenerals.size()); i < max; ++i) {
            if (i < outGenerals.size()) {
                final KfGeneralInfo gInfo2 = pInfo.getgMap().get(outGenerals.get(i));
                if (gInfo2.getState() != 1) {
                    messageList.add(LocalMessages.SIGN_UP_2);
                    continue;
                }
                final KfTeam kt3 = gInfo2.getTeam();
                if (!(kt3 instanceof KfCity)) {
                    messageList.add(LocalMessages.SIGN_UP_2);
                    continue;
                }
                if (!((KfCity)kt3).isCaptial()) {
                    messageList.add(LocalMessages.SIGN_UP_2);
                    continue;
                }
                kt3.teamLock.writeLock().lock();
                Label_0611: {
                    try {
                        final KfTeam kt4 = gInfo2.getTeam();
                        if (kt4.getTeamId() != kt3.getTeamId()) {
                            messageList.add(LocalMessages.SIGN_UP_1);
                        }
                        else {
                            if (gInfo2.getState() == 1) {
                                kt3.removeGeneral(gInfo2);
                                pInfo.getgMap().remove(gInfo2.getgId());
                                break Label_0611;
                            }
                            messageList.add(LocalMessages.SIGN_UP_2);
                        }
                        continue;
                    }
                    finally {
                        kt3.teamLock.writeLock().unlock();
                    }
                }
                kt3.teamLock.writeLock().unlock();
            }
            if (i < capaList.size()) {
                final CampArmyParam capa2 = capaList.get(i);
                final KfGeneralInfo gInfo3 = new KfGeneralInfo();
                final KfCity captialCity = KfgzManager.getKfWorldByGzId(gzId).getCapitals().get(pInfo.getForceId());
                gInfo3.setpInfo(pInfo);
                gInfo3.setgId(capa2.getGeneralId());
                final KfCampArmy campArmy = new KfCampArmy();
                BeanUtils.copyProperties(capa2, campArmy, new String[] { "terrainAttDefAdd" });
                campArmy.setPlayerId(pInfo.getCompetitorId());
                campArmy.setGeneralInfo(gInfo3);
                gInfo3.setCampArmy(campArmy);
                captialCity.addGeneral(gInfo3);
                pInfo.getgMap().put(gInfo3.getgId(), gInfo3);
            }
        }
        if (messageList.size() > 0) {
            final String[] ms = new String[messageList.size()];
            for (int j = 0; j < messageList.size(); ++j) {
                ms[j] = messageList.get(j);
            }
            res.setMessages(ms);
        }
    }
    
    private KfPlayerInfo buildNewKfgzPlayerInfo(final KfgzSignInfoParam signInfo, final int gzId, final int forceId) throws JsonProcessingException, IOException {
        final KfgzPlayerInfo gzPlayer = signInfo.getPlayerInfo();
        final KfPlayerInfo pInfo = new KfPlayerInfo(gzPlayer.getCompetitorId(), gzId);
        pInfo.setForceId(forceId);
        pInfo.setGzId(gzId);
        pInfo.setNation(gzPlayer.getNation());
        pInfo.setPlayerId(gzPlayer.getPlayerId());
        pInfo.setPlayerName(gzPlayer.getPlayerName());
        pInfo.setPlayerLevel(gzPlayer.getPlayerLevel());
        pInfo.setServerName(gzPlayer.getServerName());
        pInfo.setServerId(gzPlayer.getServerId());
        pInfo.setPic(gzPlayer.getPic());
        pInfo.setTech16(gzPlayer.getTech16());
        pInfo.setTech40(gzPlayer.getTech40());
        pInfo.setTech49(gzPlayer.getTech49());
        pInfo.setTech50(gzPlayer.getTech50());
        pInfo.setTech39(gzPlayer.getTech39());
        pInfo.setTech28(gzPlayer.getTech28());
        pInfo.setOfficerId(gzPlayer.getOfficerId());
        final Halls hall = HallsCache.getHallsById(pInfo.getOfficerId());
        if (hall != null && hall.getOrder() > 0) {
            if (hall.getId() == 1) {
                pInfo.setOfficeTokenNum(2);
            }
            else {
                pInfo.setOfficeTokenNum(1);
            }
        }
        signInfo.setCampList(forceId);
        final Map<Integer, KfGeneralInfo> gMap = new ConcurrentHashMap<Integer, KfGeneralInfo>();
        CampArmyParam[] campDatas;
        for (int length = (campDatas = signInfo.getCampDatas()).length, i = 0; i < length; ++i) {
            final CampArmyParam capa = campDatas[i];
            final KfGeneralInfo gInfo = new KfGeneralInfo();
            final KfCity captialCity = KfgzManager.getKfWorldByGzId(gzId).getCapitals().get(pInfo.getForceId());
            gInfo.setpInfo(pInfo);
            gInfo.setgId(capa.getGeneralId());
            final KfCampArmy campArmy = new KfCampArmy();
            BeanUtils.copyProperties(capa, campArmy, new String[] { "terrainAttDefAdd" });
            campArmy.setPlayerId(pInfo.getCompetitorId());
            campArmy.setGeneralInfo(gInfo);
            gInfo.setCampArmy(campArmy);
            captialCity.addGeneral(gInfo);
            gMap.put(gInfo.getgId(), gInfo);
        }
        pInfo.setgMap(gMap);
        KfgzPlayerManager.addNewPlayer(pInfo);
        return pInfo;
    }
    
    @Override
    public KfgzBaseInfoRes getGzBaseInfo(final KfgzGzKey gzKey, final String serverKey) {
        final KfgzBaseInfoRes res = new KfgzBaseInfoRes();
        final int gzId = gzKey.getGzId();
        final int seasonId = gzKey.getSeasonId();
        final KfgzBaseInfo baseInfo = KfgzManager.getGzBaseInfoById(gzId);
        if (baseInfo == null || baseInfo.getSeasonId() != seasonId) {
            res.setGzId(gzId);
            res.setSeasonId(seasonId);
            res.setState(4);
            return res;
        }
        BeanUtils.copyProperties(baseInfo, res);
        if (serverKey.equals(baseInfo.getGameServer1())) {
            res.addMails(baseInfo.getAndClearMail1());
        }
        if (serverKey.equals(baseInfo.getGameServer2())) {
            res.addMails(baseInfo.getAndClearMail2());
        }
        return res;
    }
    
    @Override
    public KfgzNationResInfo getGzResultInfo(final KfgzNationResKey nKey, final String serverKey) {
        final KfgzNationResInfo res = new KfgzNationResInfo();
        final int seasonId = nKey.getSeasonId();
        final int gzId = nKey.getGzId();
        final int forceId = nKey.getForceId();
        res.setGzId(gzId);
        res.setForceId(forceId);
        res.setSeasonId(seasonId);
        final KfgzBaseInfo baseInfo = KfgzManager.getGzBaseInfoById(gzId);
        if (baseInfo == null || baseInfo.getSeasonId() != seasonId) {
            res.setState(3);
            return res;
        }
        if (baseInfo.getState() != 2) {
            res.setState(2);
            return res;
        }
        final KfWorld kfWorld = KfgzManager.getKfWorldByGzId(gzId);
        final int oppforceId = 3 - forceId;
        int selfCityNum = 0;
        int oppCityNum = 0;
        selfCityNum = kfWorld.getForceCityNum(forceId);
        oppCityNum = kfWorld.getForceCityNum(oppforceId);
        res.setSelfCityCount(selfCityNum);
        res.setOppCityCount(oppCityNum);
        res.setNation1(baseInfo.getNation1());
        res.setNation2(baseInfo.getNation2());
        res.setServerName1(baseInfo.getServerName1());
        res.setServerName2(baseInfo.getServerName2());
        final KfgzBattleRank rank = KfgzManager.getBattleRankingByGzID(gzId);
        for (final Integer cId : nKey.getcIdList()) {
            if (cId != null) {
                final KfgzPlayerResultInfo pResInfo = new KfgzPlayerResultInfo();
                pResInfo.setcId(cId);
                rank.processPlayerResInfo(pResInfo, forceId);
                res.getpList().add(pResInfo);
            }
        }
        final KfgzBattleRewardRes bRes = baseInfo.getBattleReward();
        final String cityR = bRes.getCityReward();
        final String killRankingR = bRes.getKillRankRewardInfo();
        final String OccuPyR = bRes.getOccupyCityReward();
        final String soloR = bRes.getSoloReward();
        final int cReward = KfgzCommConstants.getGzCityReward(cityR);
        res.setCityTicket(cReward);
        final int winReward = KfgzCommConstants.getGzWinReward(cityR);
        final int lostReward = KfgzCommConstants.getGzLostReward(cityR);
        res.setWin(selfCityNum >= oppCityNum);
        if (res.isWin()) {
            res.setWinTicket(winReward);
        }
        else {
            res.setWinTicket(lostReward);
        }
        res.setOccupyCityRewardCoef(Integer.parseInt(OccuPyR));
        res.setSoloRewardCoef(Integer.parseInt(soloR));
        res.setKillRankingReward(killRankingR);
        res.setState(1);
        return res;
    }
}
