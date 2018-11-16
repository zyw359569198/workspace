package com.reign.gcld.battle.service;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.battle.dao.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.player.dao.*;
import java.util.concurrent.locks.*;
import org.springframework.transaction.annotation.*;
import com.reign.gcld.common.event.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.json.*;
import com.reign.gcld.battle.domain.*;
import com.reign.gcld.battle.scene.*;
import com.reign.util.*;
import com.reign.gcld.common.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.battle.common.*;
import java.util.*;
import com.reign.gcld.player.domain.*;

@Component("rankBatService")
public class RankBatService implements IRankBatService
{
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private QualifyingLevelCache qualifyingLevelCache;
    @Autowired
    private ArmyCache armyCache;
    @Autowired
    private CCache cCache;
    @Autowired
    private ChargeitemCache chargeitemCache;
    @Autowired
    private IPlayerBatRankDao playerBatRankDao;
    @Autowired
    private INationInfoDao nationInfoDao;
    @Autowired
    private QualifyingGroupCache qualifyingGroupCache;
    @Autowired
    private PlayerAttributeDao playerAttributeDao;
    @Autowired
    private IDataGetter dataGetter;
    private final ReentrantLock lock;
    private static final int RANK = 200;
    private static final int NUM = 5;
    private static int[] weiArr;
    private static int[] shuArr;
    private static int[] wuArr;
    
    static {
        RankBatService.weiArr = new int[200];
        RankBatService.shuArr = new int[200];
        RankBatService.wuArr = new int[200];
        for (int i = 0; i < 200; ++i) {
            RankBatService.weiArr[i] = 0;
            RankBatService.shuArr[i] = 0;
            RankBatService.wuArr[i] = 0;
        }
    }
    
    public RankBatService() {
        this.lock = new ReentrantLock();
    }
    
    @Override
    public void initBatRankList() {
        try {
            this.lock.lock();
            final List<NationInfo> list = this.nationInfoDao.getModels();
            if (list == null || list.size() < 3) {
                final StringBuilder sb = new StringBuilder();
                for (int i = 0; i < 200; ++i) {
                    RankBatService.weiArr[i] = 0;
                    RankBatService.shuArr[i] = 0;
                    RankBatService.wuArr[i] = 0;
                    sb.append(0).append("#");
                }
                final NationInfo nationInfo = new NationInfo();
                for (int j = 1; j <= 3; ++j) {
                    nationInfo.setForceId(j);
                    nationInfo.setRankInfo(sb.toString());
                    this.nationInfoDao.create(nationInfo);
                }
                return;
            }
            int[] arr = RankBatService.wuArr;
            for (final NationInfo ni : list) {
                final String[] strs = ni.getRankInfo().split("#");
                if (ni.getForceId() == 1) {
                    arr = RankBatService.weiArr;
                }
                else if (ni.getForceId() == 2) {
                    arr = RankBatService.shuArr;
                }
                else {
                    arr = RankBatService.wuArr;
                }
                for (int k = 0; k < arr.length; ++k) {
                    arr[k] = Integer.valueOf(strs[k]);
                }
            }
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    @Transactional
    @Override
    public void dealRankBat() {
        try {
            this.lock.lock();
            this.playerBatRankDao.resetReward();
            this.createBatRankReward(RankBatService.weiArr);
            this.createBatRankReward(RankBatService.shuArr);
            this.createBatRankReward(RankBatService.wuArr);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    private void createBatRankReward(final int[] arr) {
        for (int i = 0; i < arr.length; ++i) {
            if (arr[i] > 0) {
                final QualifyingLevel qualifyingLevel = (QualifyingLevel)this.qualifyingLevelCache.get((Object)(i + 1));
                final String rewardString = ((QualifyingGroup)this.qualifyingGroupCache.get((Object)qualifyingLevel.getGroup())).getRewardQualifying();
                this.playerBatRankDao.updateRewardAndRank(arr[i], rewardString, i + 1);
                if (Players.getPlayer(arr[i]) != null) {
                    EventListener.fireEvent(new CommonEvent(24, arr[i]));
                }
            }
        }
    }
    
    @Override
    public void updateRankInfo() {
        try {
            this.lock.lock();
            final StringBuilder wei = new StringBuilder();
            final StringBuilder shu = new StringBuilder();
            final StringBuilder wu = new StringBuilder();
            for (int i = 0; i < 200; ++i) {
                wei.append(RankBatService.weiArr[i]).append("#");
                shu.append(RankBatService.shuArr[i]).append("#");
                wu.append(RankBatService.wuArr[i]).append("#");
            }
            this.nationInfoDao.updateRankInfo(1, wei.toString());
            this.nationInfoDao.updateRankInfo(2, wei.toString());
            this.nationInfoDao.updateRankInfo(3, wei.toString());
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    @Override
    public byte[] ChallengeRewardInfo(final PlayerDto playerDto, final int targetRank) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final QualifyingGroup qualifyingGroup = (QualifyingGroup)this.dataGetter.getQualifyingGroupCache().get((Object)((QualifyingLevel)this.dataGetter.getQualifyingLevelCache().get((Object)targetRank)).getGroup());
        BattleDropAnd rankDrop = qualifyingGroup.getBattleRewardWin();
        Map<Integer, BattleDrop> dropMap = rankDrop.getDropAndMap();
        doc.startArray("winReward");
        for (final Map.Entry<Integer, BattleDrop> entry : dropMap.entrySet()) {
            doc.startObject();
            doc.createElement("type", entry.getKey());
            doc.createElement("num", entry.getValue().num);
            doc.endObject();
        }
        doc.endArray();
        rankDrop = qualifyingGroup.getBattleRewardLose();
        dropMap = rankDrop.getDropAndMap();
        doc.startArray("loseReward");
        for (final Map.Entry<Integer, BattleDrop> entry : dropMap.entrySet()) {
            doc.startObject();
            doc.createElement("type", entry.getKey());
            doc.createElement("num", entry.getValue().num);
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public long getLeftRankTimes(final int playerId) {
        final PlayerBatRank playerBatRank = this.playerBatRankDao.read(playerId);
        return playerBatRank.getRankBatNum();
    }
    
    @Override
    public int getRankPlayer(final int forceId, final int rank) {
        int[] arr = RankBatService.wuArr;
        if (forceId == 1) {
            arr = RankBatService.weiArr;
        }
        else if (forceId == 2) {
            arr = RankBatService.shuArr;
        }
        return arr[rank - 1];
    }
    
    @Override
    public int addRankBatRewardAndJifen(final int forceId, final PlayerInfo attPlayer, final boolean win) {
        int[] arr = RankBatService.wuArr;
        if (forceId == 1) {
            arr = RankBatService.weiArr;
        }
        else if (forceId == 2) {
            arr = RankBatService.shuArr;
        }
        final int attPlayerId = attPlayer.getPlayerId();
        int rank = 0;
        for (int i = 0; i < arr.length; ++i) {
            if (arr[i] == attPlayerId) {
                rank = i;
            }
        }
        QualifyingGroup qualifyingGroup = null;
        BattleDropAnd rankBatDrop = null;
        int newScore = 0;
        int scoreAdd = 0;
        if (attPlayerId > 0) {
            qualifyingGroup = (QualifyingGroup)this.qualifyingGroupCache.get((Object)((QualifyingLevel)this.qualifyingLevelCache.get((Object)(rank + 1))).getGroup());
            final PlayerBatRank playerBatRank = this.playerBatRankDao.read(attPlayerId);
            if (win) {
                scoreAdd = qualifyingGroup.getCreditWin();
                newScore = playerBatRank.getRankScore() + scoreAdd;
                if (newScore > 1000) {
                    newScore = 1000;
                    scoreAdd = 1000 - playerBatRank.getRankScore();
                }
                rankBatDrop = qualifyingGroup.getBattleRewardWin();
            }
            else {
                scoreAdd = qualifyingGroup.getCreditLose();
                newScore = this.playerBatRankDao.read(attPlayerId).getRankScore() + scoreAdd;
                if (newScore > 1000) {
                    newScore = 1000;
                    scoreAdd = 1000 - playerBatRank.getRankScore();
                }
                rankBatDrop = qualifyingGroup.getBattleRewardLose();
            }
            this.dataGetter.getBattleDropService().saveBattleDrop(attPlayer.getPlayerId(), rankBatDrop);
            attPlayer.addDropAnd(rankBatDrop);
            final BattleDrop jifenDrop = new BattleDrop();
            jifenDrop.type = 6;
            jifenDrop.num = scoreAdd;
            attPlayer.addDrop(jifenDrop);
            this.dataGetter.getPlayerBatRankDao().updateRankScore(attPlayer.getPlayerId(), newScore);
        }
        return 1;
    }
    
    @Transactional
    @Override
    public void changeNameList(final int forceId, final int playerIdA, final int playerIdB) {
        int[] arr = RankBatService.wuArr;
        if (forceId == 1) {
            arr = RankBatService.weiArr;
        }
        else if (forceId == 2) {
            arr = RankBatService.shuArr;
        }
        synchronized (arr) {
            int aRank = -1;
            int bRank = -1;
            if (playerIdB < 0) {
                bRank = -playerIdB - 1;
            }
            for (int i = 0; i < arr.length; ++i) {
                if (arr[i] == playerIdA) {
                    aRank = i;
                }
                else if (arr[i] == playerIdB) {
                    bRank = i;
                }
            }
            if (aRank != -1 && aRank <= bRank) {
                throw new RuntimeException("\u6392\u4f4d\u8d5b\u5f02\u5e38\uff0c\u6311\u6218\u4e86\u6392\u540d\u5728\u81ea\u5df1\u540e\u9762\u7684\u73a9\u5bb6");
            }
            final int temp = arr[bRank];
            arr[bRank] = playerIdA;
            if (aRank >= 0) {
                arr[aRank] = ((playerIdB < 0) ? temp : playerIdB);
            }
            final StringBuilder info = new StringBuilder();
            for (int j = 0; j < 200; ++j) {
                info.append(arr[j]).append("#");
            }
            this.nationInfoDao.updateRankInfo(forceId, info.toString());
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("rbRefresh", true);
            doc.endObject();
            Players.push(playerIdA, PushCommand.PUSH_UPDATE, doc.toByte());
            if (playerIdB > 0) {
                Players.push(playerIdB, PushCommand.PUSH_UPDATE, doc.toByte());
            }
        }
    }
    
    @Override
    public byte[] getRankPanel(final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final int[] arr = this.getSixRankInfo(playerDto.forceId, playerId);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("pRank", arr[0]);
        final PlayerBatRank playerBatRank = this.playerBatRankDao.read(playerId);
        if (playerBatRank == null) {
            return JsonBuilder.getJson(State.FAIL, "\u6392\u4f4d\u8d5b\u529f\u80fd\u8fd8\u672a\u5f00\u653e");
        }
        final double buyTimes = playerBatRank.getBuyTimesToday();
        final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)34);
        double gold = ci.getCost();
        final double timesAdd = ci.getParam();
        gold += buyTimes * timesAdd;
        doc.createElement("gold", (int)gold);
        doc.createElement("times", playerBatRank.getRankBatNum());
        doc.createElement("timesMax", 12);
        long nextTime = playerBatRank.getLastRankTime().getTime() + 14400000L - System.currentTimeMillis();
        if (nextTime < 0L) {
            nextTime = -nextTime;
        }
        doc.createElement("nextAddTime", nextTime);
        doc.createElement("jifen", playerBatRank.getRankScore());
        doc.createElement("jifenMax", 1000);
        doc.createElement("jifenFull", playerBatRank.getRankScore() == 1000);
        QualifyingLevel qualifyingLevel = null;
        BattleDropAnd rankDrop = null;
        Map<Integer, BattleDrop> dropMap = null;
        if (arr[0] > 0) {
            qualifyingLevel = (QualifyingLevel)this.qualifyingLevelCache.get((Object)arr[0]);
            rankDrop = ((QualifyingGroup)this.qualifyingGroupCache.get((Object)qualifyingLevel.getGroup())).getBattleRewardQualifying();
            dropMap = rankDrop.getDropAndMap();
            doc.startArray("rewards");
            for (final Map.Entry<Integer, BattleDrop> entry : dropMap.entrySet()) {
                doc.startObject();
                doc.createElement("type", entry.getKey());
                doc.createElement("num", entry.getValue().num);
                doc.createElement("lv", entry.getValue().pro);
                doc.endObject();
            }
            doc.endArray();
            final int nextGroup = qualifyingLevel.getGroup() - 1;
            if (nextGroup >= 1) {
                final Tuple<Integer, Integer> nextScale = this.dataGetter.getQualifyingLevelCache().getScale(nextGroup);
                doc.createElement("nextBegin", nextScale.left);
                doc.createElement("nextEnd", nextScale.right);
                rankDrop = ((QualifyingGroup)this.qualifyingGroupCache.get((Object)(qualifyingLevel.getGroup() - 1))).getBattleRewardQualifying();
                dropMap = rankDrop.getDropAndMap();
                doc.startArray("nextRewards");
                for (final Map.Entry<Integer, BattleDrop> entry2 : dropMap.entrySet()) {
                    doc.startObject();
                    doc.createElement("type", entry2.getKey());
                    doc.createElement("num", entry2.getValue().num);
                    doc.createElement("lv", entry2.getValue().id);
                    doc.endObject();
                }
                doc.endArray();
            }
        }
        else {
            final int nextGroup = this.dataGetter.getQualifyingLevelCache().getScaleSize();
            final Tuple<Integer, Integer> nextScale = this.dataGetter.getQualifyingLevelCache().getScale(nextGroup);
            doc.createElement("nextBegin", nextScale.left);
            doc.createElement("nextEnd", nextScale.right);
            rankDrop = ((QualifyingGroup)this.qualifyingGroupCache.get((Object)nextGroup)).getBattleRewardQualifying();
            dropMap = rankDrop.getDropAndMap();
            doc.startArray("nextRewards");
            for (final Map.Entry<Integer, BattleDrop> entry2 : dropMap.entrySet()) {
                doc.startObject();
                doc.createElement("type", entry2.getKey());
                doc.createElement("num", entry2.getValue().num);
                doc.createElement("lv", entry2.getValue().id);
                doc.endObject();
            }
            doc.endArray();
        }
        int start = arr[0] - 5;
        if (arr[0] == 0) {
            start = 196;
        }
        else if (arr[0] <= 6) {
            start = 2;
        }
        doc.startArray("info");
        doc.appendJson(this.getRankJson(1, arr[1]));
        for (int i = 2; i < arr.length; ++i) {
            doc.appendJson(this.getRankJson(start, arr[i]));
            ++start;
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public int[] getSixRankInfo(final int forceId, final int playerId) {
        final int[] res = new int[7];
        int[] arr = RankBatService.wuArr;
        if (forceId == 1) {
            arr = RankBatService.weiArr;
        }
        else if (forceId == 2) {
            arr = RankBatService.shuArr;
        }
        int rank = -1;
        for (int i = 0; i < arr.length; ++i) {
            if (arr[i] == playerId) {
                res[0] = i + 1;
                rank = i + 1;
                break;
            }
        }
        int k = 0;
        if (rank == -1) {
            res[++k] = arr[0];
            for (int j = 195; j < 200; ++j) {
                res[++k] = arr[j];
            }
        }
        else if (rank <= 6) {
            for (int j = 0; j <= 5; ++j) {
                res[++k] = arr[j];
            }
        }
        else {
            res[++k] = arr[0];
            for (int j = rank - 5; j < rank; ++j) {
                res[++k] = arr[j - 1];
            }
        }
        return res;
    }
    
    private byte[] getRankJson(final int rank, final int playerId) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("rank", rank);
        if (playerId <= 0) {
            doc.createElement("playerId", (-rank));
            final Army army = (Army)this.armyCache.get((Object)((QualifyingLevel)this.qualifyingLevelCache.get((Object)rank)).getChief());
            doc.createElement("name", army.getName());
        }
        else {
            doc.createElement("playerId", playerId);
            doc.createElement("name", this.playerDao.read(playerId).getPlayerName());
        }
        doc.endObject();
        return doc.toByte();
    }
    
    @Override
    public byte[] getRewardInfo(final int playerId) {
        final PlayerBatRank pbrr = this.playerBatRankDao.read(playerId);
        if (pbrr == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("rank", pbrr.getRank());
        if (pbrr.getReward() != null && !pbrr.getReward().isEmpty()) {
            final BattleDropAnd rankDrop = BattleDropFactory.getInstance().getBattleDropAnd(pbrr.getReward());
            final Map<Integer, BattleDrop> map = rankDrop.getDropAndMap();
            doc.startArray("rbRewards");
            for (final BattleDrop bd : map.values()) {
                doc.startObject();
                doc.createElement("type", bd.type);
                doc.createElement("num", bd.num);
                doc.createElement("lv", bd.id);
                doc.endObject();
            }
            doc.endArray();
        }
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] doReward(final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final PlayerBatRank pbrr = this.playerBatRankDao.read(playerId);
        if (pbrr == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        if (pbrr.getReward() != null && !pbrr.getReward().isEmpty()) {
            final BattleDropAnd rankDrop = BattleDropFactory.getInstance().getBattleDropAnd(pbrr.getReward());
            this.dataGetter.getBattleDropService().saveBattleDrop(playerId, rankDrop);
            this.playerBatRankDao.resetOnePlayerReward(playerId);
            doc.createElement("res", true);
        }
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] buyOneTime(final int playerId) {
        final PlayerBatRank playerBatRank = this.playerBatRankDao.read(playerId);
        final int num = playerBatRank.getRankBatNum();
        if (num > 12) {
            throw new RuntimeException("\u6392\u4f4d\u8d5b\u6b21\u6570\u8d85\u4e0a\u9650");
        }
        if (num == 12) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_NATION_RANK_NUM_OVERFLOW);
        }
        final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)34);
        if (this.playerDao.getConsumeLv(playerId) < ci.getLv()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10002);
        }
        final int buyTimes = playerBatRank.getBuyTimesToday();
        double gold = ci.getCost();
        final double timesAdd = ci.getParam();
        gold += buyTimes * timesAdd;
        final Player player = this.playerDao.read(playerId);
        if (!this.playerDao.consumeGold(player, (int)gold, "\u8d2d\u4e70\u6392\u4f4d\u8d5b\u6b21\u6570\u6d88\u8017\u91d1\u5e01")) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
        }
        this.playerBatRankDao.updateRankBatNumAndBuyTimes(playerId, num + 1, buyTimes + 1);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        gold += timesAdd;
        doc.createElement("gold", (int)gold);
        doc.createElement("times", num + 1);
        doc.createElement("timesMax", 12);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] getJifenReward(final int playerId) {
        final int sccore = this.playerBatRankDao.read(playerId).getRankScore();
        if (sccore < 1000) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_NATION_RANK_CANNOT_JIFEN_REWARD);
        }
        final float sacrificeCount = ((C)this.cCache.get((Object)"Qualifying.Credit.Reward")).getValue();
        this.dataGetter.getPlayerIncenseDao().addIncenseNum(playerId, (int)sacrificeCount);
        this.playerBatRankDao.updateRankScore(playerId, 0);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("JifenReward");
        doc.startObject();
        doc.createElement("Type", 22);
        doc.createElement("Num", (int)sacrificeCount);
        doc.endObject();
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public void resetBuyNumTimes() {
        this.dataGetter.getPlayerBatRankDao().resetBuyNumTimes();
    }
    
    @Transactional
    @Override
    public void addNumPerTwoHours() {
        this.dataGetter.getPlayerBatRankDao().addRankBatNumPerTwoHours(12);
        this.dataGetter.getPlayerBatRankDao().setLastRankTimeAsNow();
        this.pushNumPerTwoHours();
    }
    
    public void pushNumPerTwoHours() {
        final Collection<PlayerDto> onlinePlayerList = Players.getAllPlayer();
        for (final PlayerDto onlinePlayer : onlinePlayerList) {
            final int playerId = onlinePlayer.playerId;
            final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
            if (pa == null) {
                continue;
            }
            final char[] cs = pa.getFunctionId().toCharArray();
            if (cs[46] != '1') {
                continue;
            }
            final PlayerBatRank playerBatRank = this.playerBatRankDao.read(playerId);
            if (playerBatRank != null) {
                final int num = playerBatRank.getRankBatNum();
                if (num <= 0) {
                    continue;
                }
                final JsonDocument doc = new JsonDocument();
                doc.startObject();
                doc.createElement("RankBatNum", num);
                doc.endObject();
                Players.push(playerId, PushCommand.PUSH_UPDATE, doc.toByte());
            }
            else {
                ErrorSceneLog.getInstance().appendErrorMsg("FUNCTION_ID_46 is 1, but playerBatRank is null from playerBatRankDao").appendPlayerId(onlinePlayer.playerId).appendPlayerName(onlinePlayer.playerName).appendClassName("RankBatService").appendMethodName("pushNumPerTwoHours").flush();
            }
        }
    }
    
    @Override
    public int getPlayerRank(final int forceId, final int playerId) {
        int[] arr = RankBatService.wuArr;
        if (forceId == 1) {
            arr = RankBatService.weiArr;
        }
        else if (forceId == 2) {
            arr = RankBatService.shuArr;
        }
        int i;
        for (i = -1, i = 0; i < arr.length && arr[i] != playerId; ++i) {}
        return i + 1;
    }
}
