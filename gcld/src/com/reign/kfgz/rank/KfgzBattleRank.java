package com.reign.kfgz.rank;

import com.reign.kfgz.comm.*;
import com.reign.framework.json.*;
import com.reign.kfgz.dto.response.*;
import com.reign.kfgz.dto.*;
import com.reign.kfgz.dto.request.*;
import com.reign.kfgz.control.*;
import com.reign.kfgz.constants.*;
import java.util.*;

public class KfgzBattleRank
{
    public static final int KILLARMY_TYPE = 1;
    public static final int DOSOLO_WIN_TYPE = 2;
    public static final int OCCUPY_CITY_TYPE = 3;
    Map<Integer, GzRankingInfo> map;
    int gzId;
    
    public KfgzBattleRank(final int gzId, final KfgzBattleRewardRes bRes) {
        this.map = new HashMap<Integer, GzRankingInfo>();
        this.gzId = gzId;
        final GzRankingInfo killArmyInfo = new GzRankingInfo(1);
        killArmyInfo.setRankingString(bRes.getKillRankRewardInfo());
        this.map.put(killArmyInfo.type, killArmyInfo);
        final GzRankingInfo doSoloInfo = new GzRankingInfo(2);
        this.map.put(doSoloInfo.type, doSoloInfo);
        final GzRankingInfo occupyInfo = new GzRankingInfo(3);
        this.map.put(occupyInfo.type, occupyInfo);
    }
    
    public void addNewRanking(final KfGeneralInfo gInfo, final int rankingType, final int num) {
        if (gInfo == null || !gInfo.isNotNpc()) {
            return;
        }
        final KfPlayerInfo pInfo = gInfo.getpInfo();
        if (pInfo == null) {
            return;
        }
        final GzRankingInfo grInfo = this.map.get(rankingType);
        if (grInfo != null) {
            grInfo.AddNewRanking(pInfo.getCompetitorId(), pInfo.getForceId(), num);
        }
    }
    
    public byte[] getPlayerKillArmyRankingInfo(final KfPlayerInfo pInfo) {
        final GzRankingInfo grInfo = this.map.get(1);
        final KfgzRankingDto rdto = grInfo.getPlayerRankingInfo(pInfo.getCompetitorId(), pInfo.getForceId());
        return GzRankingInfo.getRankingXml(rdto);
    }
    
    public byte[] getPlayerRankingInfo(final KfPlayerInfo player) {
        final int cId = player.getCompetitorId();
        final int forceId = player.getForceId();
        long killArmyNum = 0L;
        long OccupyCityNum = 0L;
        long soloWinNum = 0L;
        final GzRankingInfo grInfo = this.map.get(1);
        final KfgzRankingDto r1 = grInfo.getPlayerRankingInfoSingle(cId, forceId);
        if (r1 != null) {
            killArmyNum = r1.getValue();
        }
        final GzRankingInfo grInfo2 = this.map.get(3);
        final KfgzRankingDto r2 = grInfo2.getPlayerRankingInfoSingle(cId, forceId);
        if (r2 != null) {
            OccupyCityNum = r2.getValue();
        }
        final GzRankingInfo grInfo3 = this.map.get(2);
        final KfgzRankingDto r3 = grInfo3.getPlayerRankingInfoSingle(cId, forceId);
        if (r3 != null) {
            soloWinNum = r3.getValue();
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject("playerRank");
        doc.createElement("killArmyNum", killArmyNum);
        doc.createElement("occupyCityNum", OccupyCityNum);
        doc.createElement("soloWinNum", soloWinNum);
        doc.endObject();
        return doc.toByte();
    }
    
    public void processPlayerResInfo(final KfgzPlayerResultInfo pResInfo, final int forceId) {
        final int cId = pResInfo.getcId();
        long killArmyNum = 0L;
        int killRank = 0;
        long occupyCityNum = 0L;
        long soloWinNum = 0L;
        final GzRankingInfo grInfo = this.map.get(1);
        final KfgzRankingDto r1 = grInfo.getPlayerRankingInfoSingle(cId, forceId);
        if (r1 != null) {
            killArmyNum = r1.getValue();
            killRank = r1.getPos();
        }
        final GzRankingInfo grInfo2 = this.map.get(3);
        final KfgzRankingDto r2 = grInfo2.getPlayerRankingInfoSingle(cId, forceId);
        if (r2 != null) {
            occupyCityNum = r2.getValue();
        }
        final GzRankingInfo grInfo3 = this.map.get(2);
        final KfgzRankingDto r3 = grInfo3.getPlayerRankingInfoSingle(cId, forceId);
        if (r3 != null) {
            soloWinNum = r3.getValue();
        }
        pResInfo.setKillArmy(killArmyNum);
        pResInfo.setKillRank(killRank);
        pResInfo.setSoloWinNum((int)soloWinNum);
        pResInfo.setOccupyCity((int)occupyCityNum);
    }
    
    public List<KfgzPlayerRankingInfoReq> getBattleResultInfo(final KfgzBaseInfo gzInfo) {
        final List<KfgzPlayerRankingInfoReq> prList = new ArrayList<KfgzPlayerRankingInfoReq>();
        final int gzId = gzInfo.getGzId();
        final Map<Integer, KfgzPlayerRankingInfoReq> prMap = new HashMap<Integer, KfgzPlayerRankingInfoReq>();
        if (KfgzPlayerManager.getPlayerMapByGz(gzId) != null) {
            for (final KfPlayerInfo pInfo : KfgzPlayerManager.getPlayerMapByGz(gzId).values()) {
                final KfgzPlayerRankingInfoReq req = new KfgzPlayerRankingInfoReq();
                req.setcId(pInfo.getCompetitorId());
                req.setGzId(gzId);
                req.setPlayerLv(pInfo.getPlayerLevel());
                req.setPlayerName(pInfo.getPlayerName());
                req.setSeasonId(gzInfo.getSeasonId());
                req.setServerId(pInfo.getServerId());
                req.setServerName(pInfo.getServerName());
                req.setNation(pInfo.getNation());
                final StringBuilder gInfos = new StringBuilder();
                for (final KfGeneralInfo gInfo : pInfo.getgMap().values()) {
                    if (gInfo.getCampArmy() != null) {
                        gInfos.append(KfgzCommConstants.getGeneralsInfo(gInfo.getCampArmy().getGeneralLv(), gInfo.getCampArmy().getGeneralName(), gInfo.getCampArmy().getGeneralPic(), gInfo.getCampArmy().getQuality()));
                    }
                }
                req.setgInfos(gInfos.toString());
                if (pInfo.getForceId() == 1) {
                    req.setGameServer(gzInfo.getGameServer1());
                }
                else if (pInfo.getForceId() == 2) {
                    req.setGameServer(gzInfo.getGameServer2());
                }
                prMap.put(pInfo.getCompetitorId(), req);
            }
        }
        this.getAndSetPlayerRankingInfo(prMap, 1, 1);
        this.getAndSetPlayerRankingInfo(prMap, 1, 2);
        this.getAndSetPlayerRankingInfo(prMap, 3, 1);
        this.getAndSetPlayerRankingInfo(prMap, 3, 2);
        this.getAndSetPlayerRankingInfo(prMap, 2, 1);
        this.getAndSetPlayerRankingInfo(prMap, 2, 2);
        for (final KfgzPlayerRankingInfoReq req2 : prMap.values()) {
            prList.add(req2);
        }
        return prList;
    }
    
    private void getAndSetPlayerRankingInfo(final Map<Integer, KfgzPlayerRankingInfoReq> prMap, final int rankType, final int forceId) {
        final GzRankingInfo grInfo = this.map.get(rankType);
        final LinkedList<KfgzRankingDto> list1 = grInfo.rankingListMap.get(forceId);
        if (list1 != null) {
            for (final KfgzRankingDto rdto : list1) {
                final int cId = rdto.getcId();
                final KfgzPlayerRankingInfoReq req = prMap.get(cId);
                if (req != null) {
                    if (rankType == 1) {
                        req.setKillArmy(rdto.getValue());
                    }
                    else if (rankType == 3) {
                        req.setOccupyCity((int)rdto.getValue());
                    }
                    else {
                        if (rankType != 2) {
                            continue;
                        }
                        req.setSoloNum((int)rdto.getValue());
                    }
                }
            }
        }
    }
}
