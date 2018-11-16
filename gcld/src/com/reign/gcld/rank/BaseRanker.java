package com.reign.gcld.rank;

import com.reign.gcld.world.common.*;
import com.reign.gcld.rank.common.*;
import java.util.concurrent.locks.*;
import java.util.concurrent.*;
import com.reign.framework.exception.*;
import com.reign.gcld.common.*;
import com.reign.framework.json.*;
import com.reign.gcld.player.domain.*;
import java.util.*;

public abstract class BaseRanker
{
    public DoubleLinkedList<RankData> killRankListA;
    public DoubleLinkedList<RankData> killRankListB;
    public DoubleLinkedList<RankData> killRankListC;
    public DoubleLinkedList<RankData> killRankListD;
    protected ConcurrentMap<Integer, KillRank> cacheMapA;
    protected ConcurrentMap<Integer, KillRank> cacheMapB;
    protected ConcurrentMap<Integer, KillRank> cacheMapC;
    protected ConcurrentMap<Integer, KillRank> cacheMapD;
    protected InvestInfo killTotalA;
    protected InvestInfo killTotalB;
    protected InvestInfo killTotalC;
    private ReentrantLock levelRankLock;
    protected IDataGetter dataGetter;
    
    public InvestInfo getKillTotalA() {
        return this.killTotalA;
    }
    
    public void setKillTotalA(final InvestInfo killTotalA) {
        this.killTotalA = killTotalA;
    }
    
    public void setKillTotalA(final long killTotalA, final long updateTimeA) {
        this.killTotalA.investNum = killTotalA;
        final long updateTime = this.killTotalA.updateTime;
        this.killTotalA.updateTime = ((updateTimeA > updateTime) ? updateTimeA : updateTime);
    }
    
    public InvestInfo getKillTotalB() {
        return this.killTotalB;
    }
    
    public void setKillTotalB(final InvestInfo killTotalB) {
        this.killTotalB = killTotalB;
    }
    
    public void setKillTotalB(final long killTotalB, final long updateTimeB) {
        this.killTotalB.investNum = killTotalB;
        final long updateTime = this.killTotalB.updateTime;
        this.killTotalB.updateTime = ((updateTimeB > updateTime) ? updateTimeB : updateTime);
    }
    
    public InvestInfo getKillTotalC() {
        return this.killTotalC;
    }
    
    public void setKillTotalC(final InvestInfo killTotalC) {
        this.killTotalC = killTotalC;
    }
    
    public void setKillTotalC(final long killTotalC, final long updateTimeC) {
        this.killTotalC.investNum = killTotalC;
        final long updateTime = this.killTotalC.updateTime;
        this.killTotalC.updateTime = ((updateTimeC > updateTime) ? updateTimeC : updateTime);
    }
    
    public synchronized InvestInfo getKillTotalByForceId(final int forceId) {
        switch (forceId) {
            case 1: {
                return this.getKillTotalA();
            }
            case 2: {
                return this.getKillTotalB();
            }
            case 3: {
                return this.getKillTotalC();
            }
            default: {
                return null;
            }
        }
    }
    
    public long getTotalNum(final int forceId) {
        final InvestInfo ii = this.getKillTotalByForceId(forceId);
        return (ii == null) ? 0L : ii.investNum;
    }
    
    public BaseRanker() {
        this.killRankListA = new DoubleLinkedList<RankData>();
        this.killRankListB = new DoubleLinkedList<RankData>();
        this.killRankListC = new DoubleLinkedList<RankData>();
        this.killRankListD = new DoubleLinkedList<RankData>();
        this.cacheMapA = new ConcurrentHashMap<Integer, KillRank>();
        this.cacheMapB = new ConcurrentHashMap<Integer, KillRank>();
        this.cacheMapC = new ConcurrentHashMap<Integer, KillRank>();
        this.cacheMapD = new ConcurrentHashMap<Integer, KillRank>();
        this.killTotalA = new InvestInfo(1);
        this.killTotalB = new InvestInfo(2);
        this.killTotalC = new InvestInfo(3);
        this.levelRankLock = new ReentrantLock(false);
    }
    
    public BaseRanker(final IDataGetter dataGetter) {
        this.killRankListA = new DoubleLinkedList<RankData>();
        this.killRankListB = new DoubleLinkedList<RankData>();
        this.killRankListC = new DoubleLinkedList<RankData>();
        this.killRankListD = new DoubleLinkedList<RankData>();
        this.cacheMapA = new ConcurrentHashMap<Integer, KillRank>();
        this.cacheMapB = new ConcurrentHashMap<Integer, KillRank>();
        this.cacheMapC = new ConcurrentHashMap<Integer, KillRank>();
        this.cacheMapD = new ConcurrentHashMap<Integer, KillRank>();
        this.killTotalA = new InvestInfo(1);
        this.killTotalB = new InvestInfo(2);
        this.killTotalC = new InvestInfo(3);
        this.levelRankLock = new ReentrantLock(false);
        this.dataGetter = dataGetter;
    }
    
    public void init() {
        for (int i = 1; i <= 3; ++i) {
            this.initList(i);
        }
    }
    
    public abstract void initList(final int p0);
    
    public void clear() {
        this.killRankListA = new DoubleLinkedList<RankData>();
        this.killRankListB = new DoubleLinkedList<RankData>();
        this.killRankListC = new DoubleLinkedList<RankData>();
        this.killRankListD = new DoubleLinkedList<RankData>();
        this.cacheMapA.clear();
        this.cacheMapB.clear();
        this.cacheMapC.clear();
        this.cacheMapD.clear();
    }
    
    public void clearByForceId(final int forceId) {
        switch (forceId) {
            case 1: {
                this.clear1();
                break;
            }
            case 2: {
                this.clear2();
                break;
            }
            case 3: {
                this.clear3();
                break;
            }
            default: {
                this.clear();
                break;
            }
        }
    }
    
    private void clear1() {
        this.killRankListA = new DoubleLinkedList<RankData>();
        this.cacheMapA.clear();
        this.setKillTotalA(0L, 0L);
    }
    
    private void clear2() {
        this.killRankListB = new DoubleLinkedList<RankData>();
        this.cacheMapB.clear();
        this.setKillTotalB(0L, 0L);
    }
    
    private void clear3() {
        this.killRankListC = new DoubleLinkedList<RankData>();
        this.cacheMapC.clear();
        this.setKillTotalC(0L, 0L);
    }
    
    public void fireRankEvent(final int rankId, final RankData data) {
        switch (rankId) {
            case 1: {
                this.fireLevelRankEvent(data);
                break;
            }
        }
    }
    
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
    
    public byte[] getRankList(final int rankId, final int forceId) {
        switch (rankId) {
            case 1: {
                return this.getLevelRankList(forceId);
            }
            default: {
                throw new InternalException("unknow rank type [type:" + rankId + "]");
            }
        }
    }
    
    public List<RankData> getRankListDatas(final int rankId, final int forceId) {
        final List<RankData> result = new ArrayList<RankData>();
        DoubleLinkedList<RankData> levelRankList;
        if (forceId == 1) {
            levelRankList = this.killRankListA;
        }
        else if (forceId == 2) {
            levelRankList = this.killRankListB;
        }
        else if (forceId == 3) {
            levelRankList = this.killRankListC;
        }
        else {
            levelRankList = this.killRankListD;
        }
        final DoubleIterator<Node<RankData>> di = levelRankList.iterator(false);
        while (di.hasNext()) {
            final Node<RankData> node = di.next();
            if (node != null && node.e != null) {
                result.add(node.e);
            }
        }
        return result;
    }
    
    public byte[] getLevelRankList(final int startRank, final int count, final int forceId) {
        DoubleLinkedList<RankData> levelRankList;
        ConcurrentMap<Integer, KillRank> cacheMap;
        if (forceId == 1) {
            levelRankList = this.killRankListA;
            cacheMap = this.cacheMapA;
        }
        else if (forceId == 2) {
            levelRankList = this.killRankListB;
            cacheMap = this.cacheMapB;
        }
        else {
            levelRankList = this.killRankListC;
            cacheMap = this.cacheMapC;
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
    
    private byte[] getLevelRankList(final int forceId) {
        DoubleLinkedList<RankData> levelRankList;
        ConcurrentMap<Integer, KillRank> cacheMap;
        if (forceId == 1) {
            levelRankList = this.killRankListA;
            cacheMap = this.cacheMapA;
        }
        else if (forceId == 2) {
            levelRankList = this.killRankListB;
            cacheMap = this.cacheMapB;
        }
        else {
            levelRankList = this.killRankListC;
            cacheMap = this.cacheMapC;
        }
        final JsonDocument doc = new JsonDocument();
        doc.startArray("rankList");
        final DoubleIterator<Node<RankData>> di = levelRankList.iterator(false);
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
            doc.appendJson(pr.levelRankByte);
        }
        doc.endArray();
        return JsonBuilder.getObjectJson(State.SUCCESS, doc.toByte());
    }
    
    private void fireLevelRankEvent(final RankData data) {
        final int playerId = data.playerId;
        final Player _player = this.dataGetter.getPlayerDao().read(playerId);
        if (_player == null) {
            return;
        }
        try {
            this.levelRankLock.lock();
            KillRank pr = null;
            KillRank prD = null;
            if (1 == _player.getForceId()) {
                this.setRanking(this.killRankListA, data, 200, 1, this.cacheMapA);
                pr = this.cacheMapA.get(data.playerId);
            }
            else if (2 == _player.getForceId()) {
                this.setRanking(this.killRankListB, data, 200, 1, this.cacheMapB);
                pr = this.cacheMapB.get(data.playerId);
            }
            else {
                this.setRanking(this.killRankListC, data, 200, 1, this.cacheMapC);
                pr = this.cacheMapC.get(data.playerId);
            }
            if (pr != null) {
                pr.levelRankByte = this.getResult(data);
            }
            this.setRanking(this.killRankListD, data, 200, 1, this.cacheMapD);
            prD = this.cacheMapD.get(data.playerId);
            if (prD != null) {
                prD.levelRankByte = this.getResult(data);
            }
        }
        finally {
            this.levelRankLock.unlock();
        }
        this.levelRankLock.unlock();
    }
    
    public RankData getRankNum(final int forceId, final int rankNum) {
        if (rankNum < 0) {
            return null;
        }
        if (1 == forceId) {
            if (this.killRankListA.size() > rankNum) {
                return this.killRankListA.get(rankNum);
            }
        }
        else if (2 == forceId) {
            if (this.killRankListB.size() > rankNum) {
                return this.killRankListB.get(rankNum);
            }
        }
        else if (3 == forceId) {
            if (this.killRankListC.size() > rankNum) {
                return this.killRankListC.get(rankNum);
            }
        }
        else if (this.killRankListD.size() > rankNum) {
            return this.killRankListD.get(rankNum);
        }
        return null;
    }
    
    public List<RankData> getRankList(final int forceId) {
        final List<RankData> list = new ArrayList<RankData>();
        DoubleLinkedList<RankData> levelRankList;
        if (forceId == 1) {
            levelRankList = this.killRankListA;
        }
        else if (forceId == 2) {
            levelRankList = this.killRankListB;
        }
        else {
            levelRankList = this.killRankListC;
        }
        final DoubleIterator<Node<RankData>> di = levelRankList.iterator(false);
        while (di.hasNext()) {
            final Node<RankData> node = di.next();
            list.add(node.e);
        }
        return list;
    }
    
    private int getLevelRank(final int playerId, final int forceId) {
        KillRank pr = null;
        if (1 == forceId) {
            pr = this.cacheMapA.get(playerId);
            if (pr == null || !this.isValidate(pr, 1)) {
                final DoubleIterator<Node<RankData>> di = this.killRankListA.iterator(false);
                int index = 1;
                boolean find = false;
                while (di.hasNext()) {
                    final Node<RankData> node = di.next();
                    if (node.e.playerId == playerId) {
                        this.updatePlayerRank(playerId, index, 1, forceId);
                        this.updatePlayerRank(playerId, node, 1, forceId);
                        pr = this.cacheMapA.get(playerId);
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
            pr = this.cacheMapB.get(playerId);
            if (pr == null || !this.isValidate(pr, 1)) {
                final DoubleIterator<Node<RankData>> di = this.killRankListB.iterator(false);
                int index = 1;
                boolean find = false;
                while (di.hasNext()) {
                    final Node<RankData> node = di.next();
                    if (node.e.playerId == playerId) {
                        this.updatePlayerRank(playerId, index, 1, forceId);
                        this.updatePlayerRank(playerId, node, 1, forceId);
                        pr = this.cacheMapB.get(playerId);
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
        else if (3 == forceId) {
            pr = this.cacheMapC.get(playerId);
            if (pr == null || !this.isValidate(pr, 1)) {
                final DoubleIterator<Node<RankData>> di = this.killRankListC.iterator(false);
                int index = 1;
                boolean find = false;
                while (di.hasNext()) {
                    final Node<RankData> node = di.next();
                    if (node.e.playerId == playerId) {
                        this.updatePlayerRank(playerId, index, 1, forceId);
                        this.updatePlayerRank(playerId, node, 1, forceId);
                        pr = this.cacheMapC.get(playerId);
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
            pr = this.cacheMapD.get(playerId);
            if (pr == null || !this.isValidate(pr, 1)) {
                final DoubleIterator<Node<RankData>> di = this.killRankListD.iterator(false);
                int index = 1;
                boolean find = false;
                while (di.hasNext()) {
                    final Node<RankData> node = di.next();
                    if (node.e.playerId == playerId) {
                        this.updatePlayerRank(playerId, index, 1, forceId);
                        this.updatePlayerRank(playerId, node, 1, forceId);
                        pr = this.cacheMapD.get(playerId);
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
        return (pr.levelRank > 999) ? 0 : pr.levelRank;
    }
    
    private void updatePlayerRank(final int playerId, final Node<RankData> value, final int type, final int forceId) {
        if (1 == forceId) {
            KillRank pr = this.cacheMapA.get(playerId);
            if (pr == null) {
                pr = new KillRank();
            }
            pr.levelData = value;
            final KillRank temp = this.cacheMapA.putIfAbsent(playerId, pr);
            if (temp != null) {
                temp.levelData = value;
            }
        }
        else if (2 == forceId) {
            KillRank pr = this.cacheMapB.get(playerId);
            if (pr == null) {
                pr = new KillRank();
            }
            pr.levelData = value;
            final KillRank temp = this.cacheMapB.putIfAbsent(playerId, pr);
            if (temp != null) {
                temp.levelData = value;
            }
        }
        else if (3 == forceId) {
            KillRank pr = this.cacheMapC.get(playerId);
            if (pr == null) {
                pr = new KillRank();
            }
            pr.levelData = value;
            final KillRank temp = this.cacheMapC.putIfAbsent(playerId, pr);
            if (temp != null) {
                temp.levelData = value;
            }
        }
        else {
            KillRank pr = this.cacheMapD.get(playerId);
            if (pr == null) {
                pr = new KillRank();
            }
            pr.levelData = value;
            final KillRank temp = this.cacheMapD.putIfAbsent(playerId, pr);
            if (temp != null) {
                temp.levelData = value;
            }
        }
    }
    
    private void updatePlayerRank(final int playerId, final int value, final int type, final int forceId) {
        if (1 == forceId) {
            KillRank pr = this.cacheMapA.get(playerId);
            if (pr == null) {
                pr = new KillRank();
            }
            pr.levelRank = value;
            final KillRank temp = this.cacheMapA.putIfAbsent(playerId, pr);
            if (temp != null) {
                temp.levelRank = value;
            }
        }
        else if (2 == forceId) {
            KillRank pr = this.cacheMapB.get(playerId);
            if (pr == null) {
                pr = new KillRank();
            }
            pr.levelRank = value;
            final KillRank temp = this.cacheMapB.putIfAbsent(playerId, pr);
            if (temp != null) {
                temp.levelRank = value;
            }
        }
        else if (3 == forceId) {
            KillRank pr = this.cacheMapC.get(playerId);
            if (pr == null) {
                pr = new KillRank();
            }
            pr.levelRank = value;
            final KillRank temp = this.cacheMapC.putIfAbsent(playerId, pr);
            if (temp != null) {
                temp.levelRank = value;
            }
        }
        else {
            KillRank pr = this.cacheMapD.get(playerId);
            if (pr == null) {
                pr = new KillRank();
            }
            pr.levelRank = value;
            final KillRank temp = this.cacheMapD.putIfAbsent(playerId, pr);
            if (temp != null) {
                temp.levelRank = value;
            }
        }
    }
    
    private byte[] getResult(final RankData data) {
        final Player player = this.dataGetter.getPlayerDao().read(data.playerId);
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
    
    public synchronized void fireTotalChange(final int forceId, final int orignTotal, final long updateTime) {
        final InvestInfo killTotalByForceId;
        final InvestInfo forceTotal = killTotalByForceId = this.getKillTotalByForceId(forceId);
        killTotalByForceId.investNum += orignTotal;
        forceTotal.updateTime = updateTime;
        this.setKillTotalByForceId(forceId, forceTotal);
    }
    
    private void setKillTotalByForceId(final int forceId, final InvestInfo l) {
        switch (forceId) {
            case 1: {
                this.setKillTotalA(l);
                break;
            }
            case 2: {
                this.setKillTotalB(l);
                break;
            }
            case 3: {
                this.setKillTotalC(l);
                break;
            }
        }
    }
    
    public int getTotalPostionRankNumByForceId(final int forceId) {
        int result = 0;
        if (forceId == 1) {
            result = this.killRankListA.size();
        }
        else if (forceId == 2) {
            result = this.killRankListB.size();
        }
        else if (forceId == 3) {
            result = this.killRankListC.size();
        }
        else {
            result = 999;
        }
        result = ((result > 999) ? 999 : result);
        return result;
    }
    
    public int getValue(final int forceId, final int playerId) {
        KillRank kr = null;
        if (1 == forceId) {
            kr = this.cacheMapA.get(playerId);
        }
        else if (2 == forceId) {
            kr = this.cacheMapB.get(playerId);
        }
        else if (3 == forceId) {
            kr = this.cacheMapC.get(playerId);
        }
        else {
            kr = this.cacheMapD.get(playerId);
        }
        if (kr == null) {
            return 0;
        }
        final Node<RankData> levelData = kr.levelData;
        if (levelData == null) {
            return 0;
        }
        final RankData rd = levelData.e;
        if (rd == null) {
            return 0;
        }
        return rd.value;
    }
}
