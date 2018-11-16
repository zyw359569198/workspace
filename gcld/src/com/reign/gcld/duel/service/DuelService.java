package com.reign.gcld.duel.service;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.rank.dao.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.general.dao.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.duel.cache.*;
import com.reign.gcld.battle.dao.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.duel.util.*;
import com.reign.framework.json.*;
import com.reign.gcld.rank.service.*;
import com.reign.gcld.battle.service.*;
import com.reign.gcld.duel.model.*;
import com.reign.gcld.general.domain.*;
import com.reign.util.*;
import com.reign.gcld.rank.common.*;
import com.reign.gcld.battle.domain.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.battle.scene.*;
import com.reign.gcld.rank.domain.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.common.util.*;
import java.util.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.world.common.*;

@Component("duelService")
public class DuelService implements IDuelService
{
    @Autowired
    private IRankService rankService;
    @Autowired
    private ITaskKillInfoDao taskKillInfoDao;
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private DuelRecordsCache duelRecordsCache;
    @Autowired
    private IPlayerGeneralMilitaryDao playerGeneralMilitaryDao;
    @Autowired
    private GeneralCache generalCache;
    @Autowired
    private TroopCache troopCache;
    @Autowired
    private DuelsCache duelsCache;
    @Autowired
    private IPlayerBattleAttributeDao playerBattleAttributeDao;
    
    @Override
    public byte[] getDuelInfo(final PlayerDto playerDto) {
        final Tuple<Boolean, String> tuple = DuelUtil.canDuel(playerDto, this.rankService);
        if (!(boolean)tuple.left) {
            return JsonBuilder.getJson(State.FAIL, tuple.right);
        }
        final int playerId = playerDto.playerId;
        final int forceId = playerDto.forceId;
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final NationTaskKillRanker ntkr = RankService.nationTaskKillRanker.getRanker1();
        final int rank = ntkr.getRank(1, playerId, 0);
        doc.createElement("score", this.taskKillInfoDao.getKillNum(playerDto.playerId));
        doc.createElement("rank", rank);
        PlayerBattleAttribute pba = this.playerBattleAttributeDao.read(playerId);
        doc.createElement("win", pba.getWinTimes());
        doc.createElement("lose", pba.getFailTimes());
        doc.startArray("records");
        final Queue<Record> records = this.duelRecordsCache.getRecords(playerId);
        if (records != null && records.size() > 0) {
            for (final Record record : records) {
                doc.startObject();
                final Player player = this.playerDao.read(record.getPlayerId());
                doc.createElement("playerName", player.getPlayerName());
                doc.createElement("forceId", player.getForceId());
                doc.createElement("isAtt", record.getIsAtt() ? 1 : 0);
                doc.createElement("isWin", record.isWin());
                doc.createElement("score", record.getScore());
                doc.endObject();
            }
        }
        doc.endArray();
        final Battle battle = NewBattleManager.getInstance().getBattleByBatType(playerId, 16);
        final int state = (battle != null) ? 1 : 0;
        doc.createElement("state", state);
        if (state == 0) {
            final List<Duel> duelList = this.getDuelListByScore(playerId, forceId);
            doc.startArray("players");
            for (final Duel duel : duelList) {
                final Player player2 = this.playerDao.read(duel.getPlayerId());
                doc.startObject();
                doc.createElement("playerId", duel.getPlayerId());
                doc.createElement("playerName", player2.getPlayerName());
                doc.createElement("playerLv", player2.getPlayerLv());
                doc.createElement("forceId", player2.getForceId());
                doc.createElement("score", DuelUtil.getRewardScoreByIndex(duel.getIndex()));
                doc.createElement("pic", player2.getPic());
                final TaskKillInfo tki = this.taskKillInfoDao.getTaskKillInfo(duel.getPlayerId());
                if (tki == null) {
                    doc.createElement("herScore", this.taskKillInfoDao.getKillNum(duel.getPlayerId()));
                }
                else {
                    doc.createElement("herScore", tki.getKillnum());
                }
                doc.createElement("herRank", ntkr.getRank(1, duel.getPlayerId(), player2.getForceId()));
                pba = this.playerBattleAttributeDao.read(duel.getPlayerId());
                doc.createElement("herWin", pba.getWinTimes());
                doc.createElement("herFail", pba.getFailTimes());
                doc.endObject();
            }
            doc.endArray();
        }
        else {
            final int defId = battle.getDefBaseInfo().getId();
            final Player defPlayer = this.playerDao.read(defId);
            doc.createElement("playerName", defPlayer.getPlayerName());
            doc.createElement("forceId", defPlayer.getForceId());
            doc.createElement("pic", defPlayer.getPic());
            final List<PlayerGeneralMilitary> pgmList = this.playerGeneralMilitaryDao.getMilitaryList(defId);
            doc.startArray("generals");
            for (final PlayerGeneralMilitary pgm : pgmList) {
                doc.startObject();
                final General general = (General)this.generalCache.get((Object)pgm.getGeneralId());
                doc.createElement("generalLv", pgm.getLv());
                doc.createElement("quality", general.getQuality());
                doc.createElement("pic", general.getPic());
                final Troop troop = this.troopCache.getTroop(general.getTroop(), pgm.getPlayerId());
                doc.createElement("troopType", troop.getSerial());
                doc.endObject();
            }
            doc.endArray();
            doc.createElement("battleId", battle.getBattleId());
        }
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] getGeneralInfo(final PlayerDto playerDto, final int playerId) {
        final Tuple<Boolean, String> tuple = DuelUtil.canDuel(playerDto, this.rankService);
        if (!(boolean)tuple.left) {
            return JsonBuilder.getJson(State.FAIL, tuple.right);
        }
        final List<PlayerGeneralMilitary> pgmList = this.playerGeneralMilitaryDao.getMilitaryList(playerId);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("generals");
        for (final PlayerGeneralMilitary pgm : pgmList) {
            doc.startObject();
            final General general = (General)this.generalCache.get((Object)pgm.getGeneralId());
            doc.createElement("generalLv", pgm.getLv());
            doc.createElement("quality", general.getQuality());
            doc.createElement("pic", general.getPic());
            final Troop troop = this.troopCache.getTroop(general.getTroop(), pgm.getPlayerId());
            doc.createElement("troopType", troop.getSerial());
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public void clear() {
        this.duelRecordsCache.clear();
        this.duelsCache.removeAll();
        this.playerBattleAttributeDao.clearWinTimesAndFailTimes();
    }
    
    private List<Duel> getDuelListByScore(final int playerId, final int forceId) {
        List<Duel> duelList = this.duelsCache.getDuelList(playerId);
        if (duelList != null) {
            return duelList;
        }
        final Set<Integer> playerIds = new HashSet<Integer>(3);
        final Set<Integer> tempS1 = new HashSet<Integer>();
        final Set<Integer> tempS2 = new HashSet<Integer>();
        final Set<Integer> tempS3 = new HashSet<Integer>();
        final NationTaskKillRanker ntkr = RankService.nationTaskKillRanker.getRanker1();
        int myScore = 0;
        myScore = this.taskKillInfoDao.getKillNum(playerId);
        final int[] forceIds = this.getForces(forceId);
        this.upTenDataByScore(ntkr, forceIds[0], tempS1, myScore);
        this.upTenDataByScore(ntkr, forceIds[1], tempS1, myScore);
        if (tempS1.size() < 1) {
            final List<Integer> list = this.taskKillInfoDao.getPlayerIdListByUp(playerId, forceId);
            if (list != null && !list.isEmpty()) {
                tempS1.addAll(list);
            }
        }
        if (tempS1.size() < 1) {
            final List<Integer> playerIdList = this.playerDao.getPlayerIdByUp(playerId, forceId);
            if (playerIdList != null) {
                tempS1.addAll(playerIdList);
            }
        }
        this.tenDataByScore(ntkr, forceIds[0], tempS2, myScore);
        this.tenDataByScore(ntkr, forceIds[1], tempS2, myScore);
        if (tempS2.size() < 1) {
            final List<Integer> list = this.taskKillInfoDao.getPlayerIdList(playerId, forceId);
            if (list != null && !list.isEmpty()) {
                tempS2.addAll(list);
            }
        }
        this.downTenDataByScore(ntkr, forceIds[0], tempS3, myScore);
        this.downTenDataByScore(ntkr, forceIds[1], tempS3, myScore);
        if (tempS3.size() < 1) {
            final List<Integer> list = this.taskKillInfoDao.getPlayerIdListByDown(playerId, forceId);
            if (list != null && !list.isEmpty()) {
                tempS3.addAll(list);
            }
        }
        if (tempS3.size() < 1) {
            final List<Integer> list = this.playerDao.getPlayerIdListByDown(playerId, forceId);
            if (list != null && !list.isEmpty()) {
                tempS3.addAll(list);
            }
        }
        if (tempS1.size() > 0) {
            final Integer[] temp = tempS1.toArray(new Integer[0]);
            final Integer val = temp[WebUtil.nextInt(temp.length)];
            playerIds.add(val);
        }
        if (tempS2.size() > 0) {
            final Integer[] temp = tempS2.toArray(new Integer[0]);
            final Integer val = temp[WebUtil.nextInt(temp.length)];
            playerIds.add(val);
        }
        if (tempS3.size() > 0) {
            final Integer[] temp = tempS3.toArray(new Integer[0]);
            final Integer val = temp[WebUtil.nextInt(temp.length)];
            playerIds.add(val);
        }
        if (playerIds.size() < 3) {
            tempS1.removeAll(playerIds);
            tempS3.removeAll(playerIds);
            tempS1.addAll(tempS3);
            final Integer[] temp = tempS1.toArray(new Integer[0]);
            if (temp != null && temp.length > 0) {
                final Integer val = temp[WebUtil.nextInt(temp.length)];
                playerIds.add(val);
            }
        }
        if (playerIds.size() < 3) {
            final List<Integer> playerIdList = this.playerDao.getPlayerIdListByEqual(playerId, forceId);
            if (playerIdList != null) {
                playerIdList.removeAll(playerIds);
                if (playerIdList.size() > 0) {
                    playerIds.add(playerIdList.get(WebUtil.nextInt(playerIdList.size())));
                }
            }
        }
        if (playerIds.size() < 3) {
            final List<Integer> playerIdList = this.playerDao.getPlayerIdByUp(playerId, forceId);
            if (playerIdList != null) {
                playerIdList.removeAll(playerIds);
                if (playerIdList.size() > 0) {
                    playerIds.add(playerIdList.get(WebUtil.nextInt(playerIdList.size())));
                }
            }
        }
        if (playerIds.size() < 3) {
            final List<Integer> playerIdList = this.playerDao.getPlayerIdListByDown(playerId, forceId);
            if (playerIdList != null) {
                playerIdList.removeAll(playerIds);
                if (playerIdList.size() > 0) {
                    playerIds.add(playerIdList.get(WebUtil.nextInt(playerIdList.size())));
                }
            }
        }
        duelList = new ArrayList<Duel>();
        for (final Integer temp2 : playerIds) {
            final Duel duel = new Duel(temp2, this.getScore(ntkr, temp2, this.playerDao.getForceId(temp2)), 0, this.getRandomTerrain());
            duelList.add(duel);
        }
        Collections.sort(duelList, new Comparator<Duel>() {
            @Override
            public int compare(final Duel o1, final Duel o2) {
                return o1.getScore() - o2.getScore();
            }
        });
        for (int i = 0; i < duelList.size(); ++i) {
            duelList.get(i).setIndex(i + 1);
        }
        this.duelsCache.put(playerId, duelList);
        return duelList;
    }
    
    private Terrain getRandomTerrain() {
        final int value = WebUtil.nextInt(3) + 1;
        int display = value + 1;
        if (display == 2) {
            display = 1;
        }
        return new Terrain(display, value, display);
    }
    
    private int[] getForces(final int forceId) {
        if (1 == forceId) {
            final int[] a = { 2, 3 };
            return a;
        }
        if (2 == forceId) {
            final int[] a = { 1, 3 };
            return a;
        }
        final int[] a = { 1, 2 };
        return a;
    }
    
    private void upTenDataByScore(final NationTaskKillRanker ntkr, final int forceId, final Set<Integer> tempS, final int score) {
        for (int size = 10, rankBase = this.getRankByScore(ntkr, forceId, score); size > 0 && rankBase >= 0; --rankBase) {
            final RankData rd = ntkr.getRankNum(forceId, rankBase);
            if (rd != null) {
                if (rd.value > score) {
                    tempS.add(rd.playerId);
                    --size;
                }
            }
        }
    }
    
    private void tenDataByScore(final NationTaskKillRanker ntkr, final int forceId, final Set<Integer> tempS, final int score) {
        int size = 10;
        for (int max = ntkr.getTotalPostionRankNumByForceId(forceId), i = 0; i < max; ++i) {
            if (size <= 0) {
                break;
            }
            final RankData rd = ntkr.getRankNum(forceId, i);
            if (rd == null) {
                break;
            }
            if (rd.value < score) {
                break;
            }
            if (rd.value == score) {
                tempS.add(rd.playerId);
                --size;
            }
        }
    }
    
    private void downTenDataByScore(final NationTaskKillRanker ntkr, final int forceId, final Set<Integer> tempS, final int score) {
        int size = 10;
        int rankBase = this.getRankByScore(ntkr, forceId, score);
        rankBase = ((rankBase == 0) ? 0 : (rankBase - 1));
        for (int max = ntkr.getTotalPostionRankNumByForceId(forceId); rankBase <= max; ++rankBase) {
            if (size <= 0) {
                break;
            }
            final RankData rd = ntkr.getRankNum(forceId, rankBase);
            if (rd == null) {
                break;
            }
            if (rd.value < score) {
                tempS.add(rd.playerId);
                --size;
            }
        }
    }
    
    private int getRankByScore(final NationTaskKillRanker ntkr, final int forceId, final int score) {
        final int max = ntkr.getTotalPostionRankNumByForceId(forceId);
        for (int i = 0; i < max; ++i) {
            final RankData rd = ntkr.getRankNum(forceId, i);
            if (rd == null) {
                return max;
            }
            if (rd.value <= score) {
                return i;
            }
        }
        return max;
    }
    
    private int getScore(final NationTaskKillRanker ntkr, final int playerId, final int forceId) {
        int result = ntkr.getValue(forceId, playerId);
        if (result == 0) {
            result = this.taskKillInfoDao.getKillNum(playerId);
        }
        return result;
    }
}
