package com.reign.gcld.world.service;

import org.springframework.beans.factory.*;
import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.world.dao.*;
import com.reign.gcld.chat.service.*;
import com.reign.gcld.tech.service.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.common.log.*;
import java.util.concurrent.locks.*;
import java.util.concurrent.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.json.*;
import com.reign.gcld.world.common.*;
import com.reign.util.*;
import org.springframework.transaction.annotation.*;
import java.text.*;
import com.reign.gcld.log.*;
import com.reign.gcld.world.domain.*;
import com.reign.framework.exception.*;
import java.util.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.task.message.*;
import com.reign.gcld.common.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.rank.common.*;

@Component("killRankService")
public class KillRankService implements IKillRankService, InitializingBean
{
    @Autowired
    public IPlayerDao playerDao;
    @Autowired
    public IPlayerKillRewardDao playerKillRewardDao;
    @Autowired
    public IPlayerResourceDao playerResourceDao;
    @Autowired
    public IPlayerKillInfoDao playerKillInfoDao;
    @Autowired
    private IChatService chatService;
    @Autowired
    private KillTopListCache killTopListCache;
    @Autowired
    private KillTopListTreasureCache killTopListTreasureCache;
    @Autowired
    private TechEffectCache techEffectCache;
    @Autowired
    private CCache cCache;
    @Autowired
    private KillRankingCache killRankingCache;
    private static final Logger timerLog;
    public static DoubleLinkedList<RankData> killRankListA;
    public static DoubleLinkedList<RankData> killRankListB;
    public static DoubleLinkedList<RankData> killRankListC;
    private static ConcurrentMap<Integer, KillRank> cacheMapA;
    private static ConcurrentMap<Integer, KillRank> cacheMapB;
    private static ConcurrentMap<Integer, KillRank> cacheMapC;
    private static ReentrantLock levelRankLock;
    
    static {
        timerLog = new TimerLogger();
        KillRankService.killRankListA = new DoubleLinkedList<RankData>();
        KillRankService.killRankListB = new DoubleLinkedList<RankData>();
        KillRankService.killRankListC = new DoubleLinkedList<RankData>();
        KillRankService.cacheMapA = new ConcurrentHashMap<Integer, KillRank>();
        KillRankService.cacheMapB = new ConcurrentHashMap<Integer, KillRank>();
        KillRankService.cacheMapC = new ConcurrentHashMap<Integer, KillRank>();
        KillRankService.levelRankLock = new ReentrantLock(false);
    }
    
    private int getExtraIron(final Integer killNum, final Float value) {
        int extraIron = (int)(Object)Float.valueOf(killNum * value);
        extraIron = Math.max(1, extraIron);
        extraIron = Math.min(extraIron, 2000);
        return extraIron;
    }
    
    public static int getCrit(final double[] boxProb, final int[] timesArray) {
        final double prob = WebUtil.nextDouble();
        double sum = 0.0;
        int index = 0;
        for (int i = 0; i < boxProb.length; ++i) {
            sum += boxProb[i];
            if (prob <= sum) {
                index = i;
                break;
            }
        }
        if (index >= 0 && index < timesArray.length) {
            return timesArray[index];
        }
        return 1;
    }
    
    @Transactional
    @Override
    public byte[] reward(final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final PlayerKillReward pkr = this.playerKillRewardDao.read(playerId);
        if (pkr == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        if (pkr.getReward() <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KILL_RANK_INFO2);
        }
        final long curTime = System.currentTimeMillis();
        if (pkr.getReward() <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KILL_RANK_INFO2);
        }
        final KillRanking killRanking = (KillRanking)this.killRankingCache.get((Object)pkr.getNameList());
        int copper = 0;
        int iron = 0;
        if (killRanking != null) {
            final String[] coppers = killRanking.getBaseReward().split(",");
            final String[] irons = killRanking.getIronReward().split(",");
            copper = Integer.valueOf(coppers[1]);
            iron = Integer.valueOf(irons[1]);
        }
        this.playerResourceDao.addCopperIgnoreMax(playerId, copper, "\u6740\u654c\u6392\u884c\u589e\u52a0\u94f6\u5e01", true);
        final int tech = this.techEffectCache.getTechEffect(playerId, 33);
        if (tech > 0) {
            final C c = (C)this.cCache.get((Object)"World.KillRanking.KilltoIron");
            iron *= (int)(tech / 100.0);
            iron += this.getExtraIron(pkr.getKillNum(), c.getValue());
            this.playerResourceDao.addIronIgnoreMax(playerId, iron, "\u51fb\u6740\u699c\u83b7\u5f97\u9554\u94c1", true);
        }
        this.playerKillRewardDao.updateReward(playerId, 1, curTime);
        if (pkr.getNameList() <= 3) {
            final String msg = MessageFormatter.format(LocalMessages.T_KILL_RANK_INFO1, new Object[] { ColorUtil.getForceMsg(playerDto.forceId, String.valueOf(WorldCityCommon.nationIdNameMapDot.get(playerDto.forceId)) + playerDto.playerName), ColorUtil.getRedMsg(pkr.getNameList()), ColorUtil.getRedMsg(copper) });
            this.chatService.sendBigNotice("COUNTRY", playerDto, msg, null);
        }
        doc.createElement("reward", true);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public void dealTodayInfo() {
        final long start = System.currentTimeMillis();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        this.clearAllCache();
        final Calendar delTime = Calendar.getInstance();
        delTime.add(11, -72);
        this.playerKillInfoDao.deleteByDate(sdf.format(delTime.getTime()));
        KillRankService.timerLog.info(LogUtil.formatThreadLog("KillRankService", "dealTodayInfo", 2, System.currentTimeMillis() - start, ""));
    }
    
    public static float getRewardLv(final int rank) {
        if (rank >= 1 && rank < 3) {
            return 1.0f;
        }
        if (rank >= 3 && rank < 5) {
            return 0.8f;
        }
        if (rank >= 5 && rank < 7) {
            return 0.5f;
        }
        if (rank >= 7 && rank < 9) {
            return 0.3f;
        }
        if (rank >= 9 && rank < 10) {
            return 0.2f;
        }
        return 0.1f;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        Date date = new Date();
        date = WorldCityCommon.getDateAfter23(date);
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String dateStr = sdf.format(date);
        this.initLevelRankListA(dateStr);
        this.initLevelRankListB(dateStr);
        this.initLevelRankListC(dateStr);
    }
    
    public void clearAllCache() {
        KillRankService.killRankListA = new DoubleLinkedList<RankData>();
        KillRankService.killRankListB = new DoubleLinkedList<RankData>();
        KillRankService.killRankListC = new DoubleLinkedList<RankData>();
        KillRankService.cacheMapA.clear();
        KillRankService.cacheMapB.clear();
        KillRankService.cacheMapC.clear();
        Date date = new Date();
        date = WorldCityCommon.getDateAfter23(date);
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String dateStr = sdf.format(date);
        this.initLevelRankListA(dateStr);
        this.initLevelRankListB(dateStr);
        this.initLevelRankListC(dateStr);
    }
    
    private void initLevelRankListA(final String dateStr) {
        final List<PlayerKillInfo> resultList = this.playerKillInfoDao.getListByKillNum(1, dateStr);
        int index = 1;
        for (final PlayerKillInfo pkr : resultList) {
            final Node<RankData> node = KillRankService.killRankListA.addWithReturn(new RankData(pkr.getPlayerId(), pkr.getKillNum()));
            final KillRank pr = new KillRank();
            pr.levelData = node;
            pr.levelRank = index++;
            final int playerId = pkr.getPlayerId();
            KillRankService.cacheMapA.put(playerId, pr);
        }
    }
    
    private void initLevelRankListB(final String dateStr) {
        final List<PlayerKillInfo> resultList = this.playerKillInfoDao.getListByKillNum(2, dateStr);
        int index = 1;
        for (final PlayerKillInfo pkr : resultList) {
            final Node<RankData> node = KillRankService.killRankListB.addWithReturn(new RankData(pkr.getPlayerId(), pkr.getKillNum()));
            final KillRank pr = new KillRank();
            pr.levelData = node;
            pr.levelRank = index++;
            final int playerId = pkr.getPlayerId();
            KillRankService.cacheMapB.put(playerId, pr);
        }
    }
    
    private void initLevelRankListC(final String dateStr) {
        final List<PlayerKillInfo> resultList = this.playerKillInfoDao.getListByKillNum(3, dateStr);
        int index = 1;
        for (final PlayerKillInfo pkr : resultList) {
            final Node<RankData> node = KillRankService.killRankListC.addWithReturn(new RankData(pkr.getPlayerId(), pkr.getKillNum()));
            final KillRank pr = new KillRank();
            pr.levelData = node;
            pr.levelRank = index++;
            final int playerId = pkr.getPlayerId();
            KillRankService.cacheMapC.put(playerId, pr);
        }
    }
    
    @Override
    public void fireRankEvent(final int rankId, final RankData data) {
        switch (rankId) {
            case 1: {
                this.fireLevelRankEvent(data);
                break;
            }
        }
    }
    
    @Override
    public int getRank(final int rankId, final int playerId, final int forceId) {
        switch (rankId) {
            case 1: {
                return this.getLevelRank(playerId, forceId);
            }
            default: {
                throw new InternalException("unknow rank type [type:" + rankId + "]");
            }
        }
    }
    
    @Override
    public byte[] getLevelRankList(final int startRank, final int count, final int forceId) {
        DoubleLinkedList<RankData> levelRankList;
        ConcurrentMap<Integer, KillRank> cacheMap;
        if (forceId == 1) {
            levelRankList = KillRankService.killRankListA;
            cacheMap = KillRankService.cacheMapA;
        }
        else if (forceId == 2) {
            levelRankList = KillRankService.killRankListB;
            cacheMap = KillRankService.cacheMapB;
        }
        else {
            levelRankList = KillRankService.killRankListC;
            cacheMap = KillRankService.cacheMapC;
        }
        final JsonDocument doc = new JsonDocument();
        doc.startArray("rankList");
        final DoubleIterator<Node<RankData>> di = levelRankList.iterator(false);
        int seq = 0;
        int num = 0;
        while (di.hasNext()) {
            final Node<RankData> node = di.next();
            KillRank pr = cacheMap.get(node.e.playerId);
            if (pr == null || pr.levelRankByte == null) {
                pr = new KillRank();
                pr.levelRankByte = this.getResult(node.e);
                final KillRank temp = cacheMap.putIfAbsent(node.e.playerId, pr);
                if (temp != null) {
                    temp.levelRankByte = pr.levelRankByte;
                    pr = temp;
                }
            }
            if (++seq >= startRank) {
                doc.appendJson(pr.levelRankByte);
                ++num;
            }
            if (num >= count) {
                break;
            }
        }
        doc.endArray();
        return doc.toByte();
    }
    
    private void fireLevelRankEvent(final RankData data) {
        final int playerId = data.playerId;
        final Player _player = this.playerDao.read(playerId);
        if (_player == null) {
            return;
        }
        try {
            KillRankService.levelRankLock.lock();
            KillRank pr = null;
            if (1 == _player.getForceId()) {
                this.setRanking(KillRankService.killRankListA, data, 200, 1, KillRankService.cacheMapA);
                pr = KillRankService.cacheMapA.get(data.playerId);
            }
            else if (2 == _player.getForceId()) {
                this.setRanking(KillRankService.killRankListB, data, 200, 1, KillRankService.cacheMapB);
                pr = KillRankService.cacheMapB.get(data.playerId);
            }
            else {
                this.setRanking(KillRankService.killRankListC, data, 200, 1, KillRankService.cacheMapC);
                pr = KillRankService.cacheMapC.get(data.playerId);
            }
            if (pr != null) {
                pr.levelRankByte = this.getResult(data);
            }
        }
        finally {
            KillRankService.levelRankLock.unlock();
        }
        KillRankService.levelRankLock.unlock();
    }
    
    public RankData getRankNum(final int forceId, final int rankNum) {
        if (1 == forceId) {
            if (KillRankService.killRankListA.size() > rankNum) {
                return KillRankService.killRankListA.get(rankNum);
            }
        }
        else if (2 == forceId) {
            if (KillRankService.killRankListB.size() > rankNum) {
                return KillRankService.killRankListB.get(rankNum);
            }
        }
        else if (KillRankService.killRankListC.size() > rankNum) {
            return KillRankService.killRankListC.get(rankNum);
        }
        return null;
    }
    
    private int getLevelRank(final int playerId, final int forceId) {
        KillRank pr = null;
        if (1 == forceId) {
            pr = KillRankService.cacheMapA.get(playerId);
            if (pr == null || !this.isValidate(pr, 1)) {
                final DoubleIterator<Node<RankData>> di = KillRankService.killRankListA.iterator(false);
                int index = 1;
                boolean find = false;
                while (di.hasNext()) {
                    final Node<RankData> node = di.next();
                    if (node.e.playerId == playerId) {
                        this.updatePlayerRank(playerId, index, 1, forceId);
                        this.updatePlayerRank(playerId, node, 1, forceId);
                        pr = KillRankService.cacheMapA.get(playerId);
                        find = true;
                        break;
                    }
                    ++index;
                }
                if (!find) {
                    this.updatePlayerRank(playerId, 0, 1, forceId);
                    this.updatePlayerRank(playerId, null, 1, forceId);
                }
            }
        }
        else if (2 == forceId) {
            pr = KillRankService.cacheMapB.get(playerId);
            if (pr == null || !this.isValidate(pr, 1)) {
                final DoubleIterator<Node<RankData>> di = KillRankService.killRankListB.iterator(false);
                int index = 1;
                boolean find = false;
                while (di.hasNext()) {
                    final Node<RankData> node = di.next();
                    if (node.e.playerId == playerId) {
                        this.updatePlayerRank(playerId, index, 1, forceId);
                        this.updatePlayerRank(playerId, node, 1, forceId);
                        pr = KillRankService.cacheMapB.get(playerId);
                        find = true;
                        break;
                    }
                    ++index;
                }
                if (!find) {
                    this.updatePlayerRank(playerId, 0, 1, forceId);
                    this.updatePlayerRank(playerId, null, 1, forceId);
                }
            }
        }
        else {
            pr = KillRankService.cacheMapC.get(playerId);
            if (pr == null || !this.isValidate(pr, 1)) {
                final DoubleIterator<Node<RankData>> di = KillRankService.killRankListC.iterator(false);
                int index = 1;
                boolean find = false;
                while (di.hasNext()) {
                    final Node<RankData> node = di.next();
                    if (node.e.playerId == playerId) {
                        this.updatePlayerRank(playerId, index, 1, forceId);
                        this.updatePlayerRank(playerId, node, 1, forceId);
                        pr = KillRankService.cacheMapC.get(playerId);
                        find = true;
                        break;
                    }
                    ++index;
                }
                if (!find) {
                    this.updatePlayerRank(playerId, 0, 1, forceId);
                    this.updatePlayerRank(playerId, null, 1, forceId);
                }
            }
        }
        return this.getPlayerRank(pr, 1);
    }
    
    private int getPlayerRank(final KillRank pr, final int type) {
        if (pr == null) {
            return 0;
        }
        return (pr.levelRank > 200) ? 0 : pr.levelRank;
    }
    
    private void updatePlayerRank(final int playerId, final Node<RankData> value, final int type, final int forceId) {
        if (1 == forceId) {
            KillRank pr = KillRankService.cacheMapA.get(playerId);
            if (pr == null) {
                pr = new KillRank();
            }
            pr.levelData = value;
            final KillRank temp = KillRankService.cacheMapA.putIfAbsent(playerId, pr);
            if (temp != null) {
                temp.levelData = value;
            }
        }
        else if (2 == forceId) {
            KillRank pr = KillRankService.cacheMapB.get(playerId);
            if (pr == null) {
                pr = new KillRank();
            }
            pr.levelData = value;
            final KillRank temp = KillRankService.cacheMapB.putIfAbsent(playerId, pr);
            if (temp != null) {
                temp.levelData = value;
            }
        }
        else {
            KillRank pr = KillRankService.cacheMapC.get(playerId);
            if (pr == null) {
                pr = new KillRank();
            }
            pr.levelData = value;
            final KillRank temp = KillRankService.cacheMapC.putIfAbsent(playerId, pr);
            if (temp != null) {
                temp.levelData = value;
            }
        }
    }
    
    private void updatePlayerRank(final int playerId, final int value, final int type, final int forceId) {
        if (1 == forceId) {
            KillRank pr = KillRankService.cacheMapA.get(playerId);
            if (pr == null) {
                pr = new KillRank();
            }
            pr.levelRank = value;
            final KillRank temp = KillRankService.cacheMapA.putIfAbsent(playerId, pr);
            if (temp != null) {
                temp.levelRank = value;
            }
        }
        else if (2 == forceId) {
            KillRank pr = KillRankService.cacheMapB.get(playerId);
            if (pr == null) {
                pr = new KillRank();
            }
            pr.levelRank = value;
            final KillRank temp = KillRankService.cacheMapB.putIfAbsent(playerId, pr);
            if (temp != null) {
                temp.levelRank = value;
            }
        }
        else {
            KillRank pr = KillRankService.cacheMapC.get(playerId);
            if (pr == null) {
                pr = new KillRank();
            }
            pr.levelRank = value;
            final KillRank temp = KillRankService.cacheMapC.putIfAbsent(playerId, pr);
            if (temp != null) {
                temp.levelRank = value;
            }
        }
    }
    
    private byte[] getResult(final RankData data) {
        final Player player = this.playerDao.read(data.playerId);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("playerId", (Object)data.playerId);
        doc.createElement("playerName", player.getPlayerName());
        doc.createElement("kilNum", (Object)data.value);
        doc.endObject();
        return doc.toByte();
    }
    
    private boolean isValidateNode(final KillRank pr, final int type) {
        return pr.levelData != null;
    }
    
    private boolean isValidate(final KillRank pr, final int type) {
        return pr.levelRank != -1;
    }
    
    private void updatePlayerRank(final int playerId, final int value, final ConcurrentMap<Integer, KillRank> cacheMap) {
        KillRank pr = cacheMap.get(playerId);
        if (pr == null) {
            pr = new KillRank();
        }
        pr.levelRank = value;
        final KillRank temp = cacheMap.putIfAbsent(playerId, pr);
        if (temp != null) {
            temp.levelRank = value;
        }
    }
    
    private void updatePlayerRank(final int playerId, final Node<RankData> value, final ConcurrentMap<Integer, KillRank> cacheMap) {
        KillRank pr = cacheMap.get(playerId);
        if (pr == null) {
            pr = new KillRank();
        }
        pr.levelData = value;
        final KillRank temp = cacheMap.putIfAbsent(playerId, pr);
        if (temp != null) {
            temp.levelData = value;
        }
    }
    
    private Node<RankData> getPlayerRankNode(final KillRank pr, final int type) {
        if (pr == null) {
            return null;
        }
        return pr.levelData;
    }
    
    private boolean setRanking(final DoubleLinkedList<RankData> rankList, final RankData rankData, final int max, final int type, final ConcurrentMap<Integer, KillRank> cacheMap) {
        boolean change = false;
        final KillRank pr = cacheMap.get(rankData.playerId);
        if (pr == null || !this.isValidateNode(pr, type)) {
            if (rankList.size() == 0) {
                final Node<RankData> node = rankList.addWithReturn(rankData);
                this.updatePlayerRank(rankData.playerId, 1, cacheMap);
                this.updatePlayerRank(rankData.playerId, node, cacheMap);
                change = true;
                return change;
            }
            boolean insert = false;
            final DoubleIterator<Node<RankData>> di = rankList.iterator(true);
            while (di.hasPrev()) {
                final Node<RankData> node2 = di.prev();
                final RankData data = node2.e;
                if (rankData.value < data.value) {
                    final Node<RankData> temp = rankList.addWithReturn(rankData, node2);
                    this.updatePlayerRank(rankData.playerId, temp, cacheMap);
                    if (rankList.size() > max) {
                        final RankData popData = rankList.pop();
                        this.updatePlayerRank(popData.playerId, null, cacheMap);
                    }
                    this.clearCache(cacheMap);
                    insert = true;
                    change = true;
                    break;
                }
            }
            if (!insert && rankList.size() < max) {
                final Node<RankData> temp2 = rankList.addBeforeWithReturn(rankData);
                this.updatePlayerRank(rankData.playerId, temp2, cacheMap);
                this.clearCache(cacheMap);
                change = true;
            }
        }
        else {
            final Node<RankData> currentNode = this.getPlayerRankNode(pr, type);
            if (currentNode.e.value == rankData.value) {
                return change;
            }
            boolean insert2 = false;
            if (currentNode.e.value < rankData.value) {
                Node<RankData> tempNode = currentNode;
                while ((tempNode = tempNode.prev) != null) {
                    if (tempNode.e == null) {
                        final Node<RankData> temp3 = rankList.addBeforeWithReturn(rankData);
                        rankList.remove(currentNode);
                        this.updatePlayerRank(rankData.playerId, temp3, cacheMap);
                        if (rankList.size() > max) {
                            final RankData popData2 = rankList.pop();
                            this.updatePlayerRank(popData2.playerId, null, cacheMap);
                        }
                        this.clearCache(cacheMap);
                        insert2 = true;
                        change = true;
                        break;
                    }
                    if (rankData.value < tempNode.e.value) {
                        final Node<RankData> temp3 = rankList.addWithReturn(rankData, tempNode);
                        rankList.remove(currentNode);
                        this.updatePlayerRank(rankData.playerId, temp3, cacheMap);
                        if (rankList.size() > max) {
                            final RankData popData2 = rankList.pop();
                            this.updatePlayerRank(popData2.playerId, null, cacheMap);
                        }
                        this.clearCache(cacheMap);
                        insert2 = true;
                        change = true;
                        break;
                    }
                }
            }
            else {
                Node<RankData> tempNode = currentNode;
                while ((tempNode = tempNode.next) != null) {
                    if (tempNode.e == null) {
                        continue;
                    }
                    if (rankData.value > tempNode.e.value) {
                        final Node<RankData> temp3 = rankList.addBeforeWithReturn(rankData, tempNode);
                        rankList.remove(currentNode);
                        this.updatePlayerRank(rankData.playerId, temp3, cacheMap);
                        if (rankList.size() > max) {
                            final RankData popData2 = rankList.pop();
                            this.updatePlayerRank(popData2.playerId, null, cacheMap);
                        }
                        this.clearCache(cacheMap);
                        insert2 = true;
                        change = true;
                        break;
                    }
                }
            }
            if (!insert2) {
                rankList.remove(currentNode);
                if (rankList.size() < max) {
                    final Node<RankData> temp2 = rankList.addWithReturn(rankData);
                    this.updatePlayerRank(rankData.playerId, temp2, cacheMap);
                    change = true;
                }
                this.clearCache(cacheMap);
            }
        }
        return change;
    }
    
    private void clearCache(final ConcurrentMap<Integer, KillRank> cacheMap) {
        final Set<Map.Entry<Integer, KillRank>> entrySet = cacheMap.entrySet();
        for (final Map.Entry<Integer, KillRank> entry : entrySet) {
            final KillRank pr = entry.getValue();
            pr.levelRank = -1;
        }
    }
    
    @Override
    public void setBoxInfo(final PlayerKillInfo pkr, final int killTotal) {
        final int startId = this.techEffectCache.getTechEffect(pkr.getPlayerId(), 34);
        final int[] box = KillTopListCache.getBoxArray(startId);
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < box.length; ++i) {
            int num = 0;
            if (killTotal >= box[i]) {
                num = ((KillToplist)this.killTopListCache.get((Object)(startId + i))).getTreasureNum();
            }
            sb.append(num).append(",");
        }
        SymbolUtil.removeTheLast(sb);
        pkr.setBox_reward_info(sb.toString());
    }
    
    @Override
    public int updateKillNum(final int playerId, final int killTotal, final int killTotal2) {
        TaskMessageHelper.sendKillNumMessage(playerId, killTotal, killTotal2);
        return 0;
    }
    
    @Override
    public void dealKillrank(final int gKillTotal, final int forceId, final IDataGetter dataGetter, final int playerId) {
        Date date = new Date();
        date = WorldCityCommon.getDateAfter23(date);
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String dateStr = sdf.format(date);
        if (gKillTotal > 0) {
            final PlayerAttribute pa = dataGetter.getPlayerAttributeDao().read(playerId);
            final char[] cs = pa.getFunctionId().toCharArray();
            if (cs[32] == '1') {
                int killNum = gKillTotal;
                final PlayerKillInfo pki = dataGetter.getPlayerKillInfoDao().getByTodayInfo(playerId, dateStr);
                if (pki != null) {
                    killNum += pki.getKillNum();
                }
                dataGetter.getKillRankService().updateKillNum(playerId, killNum, gKillTotal);
                dataGetter.getRankService().updateKillNum(1, gKillTotal, playerId, System.currentTimeMillis());
                final int succ = dataGetter.getPlayerKillInfoDao().updateKillNum(playerId, gKillTotal, dateStr);
                if (succ < 1 && pki == null) {
                    final PlayerKillInfo pkr = new PlayerKillInfo();
                    pkr.setPlayerId(playerId);
                    pkr.setForceId(forceId);
                    pkr.setBox_reward_info("0,0,0,0,0");
                    pkr.setKillNum(gKillTotal);
                    pkr.setKillDate(date);
                    dataGetter.getPlayerKillInfoDao().create(pkr);
                    final JsonDocument doc = new JsonDocument();
                    doc.startObject();
                    doc.createElement("playerId", playerId);
                    doc.createElement("killNum", killNum);
                    doc.endObject();
                    Players.push(playerId, PushCommand.PUSH_KILL_ADD, doc.toByte());
                    dataGetter.getKillRankService().fireRankEvent(1, new RankData(playerId, gKillTotal));
                }
                else {
                    final JsonDocument doc2 = new JsonDocument();
                    doc2.startObject();
                    doc2.createElement("playerId", playerId);
                    doc2.createElement("killNum", killNum);
                    doc2.endObject();
                    Players.push(playerId, PushCommand.PUSH_KILL_ADD, doc2.toByte());
                    dataGetter.getKillRankService().fireRankEvent(1, new RankData(playerId, killNum));
                }
                dataGetter.getRankService().updateTodayKillNum(playerId, gKillTotal);
            }
        }
    }
    
    public void getData() {
        BoxDto boxDto = null;
        final int[] type = new int[10];
        for (int i = 0; i < 100000; ++i) {
            boxDto = this.killTopListTreasureCache.getReward(70);
            final int[] array = type;
            final int rewardType = boxDto.getRewardType();
            ++array[rewardType];
        }
    }
}
