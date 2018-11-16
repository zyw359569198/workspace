package com.reign.gcld.rank.common;

import com.reign.gcld.common.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.util.*;

public abstract class MultiRanker
{
    private List<MultiRankData> positionRankListA;
    private List<MultiRankData> positionRankListB;
    private List<MultiRankData> positionRankListC;
    private Map<Integer, PlayerRankInfo> mapA;
    private Map<Integer, PlayerRankInfo> mapB;
    private Map<Integer, PlayerRankInfo> mapC;
    protected List<MultiRankData> playerAttributes;
    public Lock lock;
    protected IDataGetter dataGetter;
    
    public MultiRanker(final IDataGetter dataGetter) {
        this.positionRankListA = new ArrayList<MultiRankData>();
        this.positionRankListB = new ArrayList<MultiRankData>();
        this.positionRankListC = new ArrayList<MultiRankData>();
        this.mapA = new ConcurrentHashMap<Integer, PlayerRankInfo>();
        this.mapB = new ConcurrentHashMap<Integer, PlayerRankInfo>();
        this.mapC = new ConcurrentHashMap<Integer, PlayerRankInfo>();
        this.playerAttributes = new ArrayList<MultiRankData>();
        this.lock = new ReentrantLock();
        this.dataGetter = dataGetter;
    }
    
    public void init() {
        this.initPlayerAttributes();
        this.initPositionListMap(1);
        this.initPositionListMap(2);
        this.initPositionListMap(3);
    }
    
    public abstract void initPlayerAttributes();
    
    private void initPositionListMap(final int i) {
        List<MultiRankData> rankList = null;
        Map<Integer, PlayerRankInfo> map = null;
        switch (i) {
            case 1: {
                this.positionRankListA = this.getPlayerListByForce(i);
                rankList = this.positionRankListA;
                map = this.mapA;
                break;
            }
            case 2: {
                this.positionRankListB = this.getPlayerListByForce(i);
                rankList = this.positionRankListB;
                map = this.mapB;
                break;
            }
            case 3: {
                this.positionRankListC = this.getPlayerListByForce(i);
                rankList = this.positionRankListC;
                map = this.mapC;
                break;
            }
        }
        int index = 1;
        for (final MultiRankData data : rankList) {
            final int playerId = data.playerId;
            final PlayerRankInfo rankInfo = new PlayerRankInfo();
            rankInfo.setPlayerId(playerId);
            rankInfo.setRankData(data);
            rankInfo.setRank(index++);
            map.put(playerId, rankInfo);
        }
    }
    
    private List<MultiRankData> getPlayerListByForce(final int forceId) {
        List<MultiRankData> rankDatas = null;
        switch (forceId) {
            case 1: {
                rankDatas = this.positionRankListA;
                break;
            }
            case 2: {
                rankDatas = this.positionRankListB;
                break;
            }
            case 3: {
                rankDatas = this.positionRankListC;
                break;
            }
        }
        for (final MultiRankData data : this.playerAttributes) {
            if (this.dataGetter.getPlayerDao().read(data.playerId).getForceId() == forceId) {
                rankDatas.add(data);
            }
        }
        Collections.sort(rankDatas);
        return rankDatas;
    }
    
    public void firePositionRank(final int forceId, final MultiRankData rankData) {
        this.lock.lock();
        try {
            List<MultiRankData> list = null;
            Map<Integer, PlayerRankInfo> map = null;
            switch (forceId) {
                case 1: {
                    list = this.positionRankListA;
                    map = this.mapA;
                    break;
                }
                case 2: {
                    list = this.positionRankListB;
                    map = this.mapB;
                    break;
                }
                case 3: {
                    list = this.positionRankListC;
                    map = this.mapC;
                    break;
                }
            }
            final PlayerRankInfo rankInfo = map.get(rankData.playerId);
            if (rankInfo == null) {
                final List<ComparableFactor> rankList = rankData.value;
                if (rankList != null && !rankList.isEmpty()) {
                    list.add(rankData);
                    Collections.sort(list);
                    this.resetMap(map, forceId);
                }
            }
            else {
                list.get(rankInfo.getRank() - 1).add(rankData);
                Collections.sort(list);
                this.resetMap(map, forceId);
            }
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    private void resetMap(final Map<Integer, PlayerRankInfo> map, final int forceId) {
        map.clear();
        List<MultiRankData> rankList = null;
        switch (forceId) {
            case 1: {
                rankList = this.positionRankListA;
                break;
            }
            case 2: {
                rankList = this.positionRankListB;
                break;
            }
            case 3: {
                rankList = this.positionRankListC;
                break;
            }
        }
        int index = 1;
        for (final MultiRankData data : rankList) {
            final int playerId = data.playerId;
            final PlayerRankInfo rankInfo = new PlayerRankInfo();
            rankInfo.setPlayerId(playerId);
            rankInfo.setRankData(data);
            rankInfo.setRank(index++);
            map.put(playerId, rankInfo);
        }
    }
    
    public List<Integer> getRankInfo(final int forceId, final int startRank, final int count) {
        final List<Integer> result = new ArrayList<Integer>();
        List<MultiRankData> rankDatas = null;
        switch (forceId) {
            case 1: {
                rankDatas = this.positionRankListA;
                break;
            }
            case 2: {
                rankDatas = this.positionRankListB;
                break;
            }
            case 3: {
                rankDatas = this.positionRankListC;
                break;
            }
        }
        for (int i = 0; i < count; ++i) {
            result.add(rankDatas.get(i + startRank - 1).playerId);
        }
        return result;
    }
    
    public int getPlayerPositionRank(final int playerId, final int forceId) {
        Map<Integer, PlayerRankInfo> map = null;
        switch (forceId) {
            case 1: {
                map = this.mapA;
                break;
            }
            case 2: {
                map = this.mapB;
                break;
            }
            case 3: {
                map = this.mapC;
                break;
            }
        }
        final PlayerRankInfo rankInfo = map.get(playerId);
        if (rankInfo == null) {
            return -1;
        }
        return (rankInfo.getRank() > 120) ? -1 : rankInfo.getRank();
    }
    
    public MultiRankData getRankNum(final int forceId, final int rankNum) {
        if (1 == forceId) {
            if (this.positionRankListA.size() > rankNum) {
                return this.positionRankListA.get(rankNum);
            }
        }
        else if (2 == forceId) {
            if (this.positionRankListB.size() > rankNum) {
                return this.positionRankListB.get(rankNum);
            }
        }
        else if (this.positionRankListC.size() > rankNum) {
            return this.positionRankListC.get(rankNum);
        }
        return null;
    }
    
    public List<MultiRankData> getForcePositionRankList(final int forceId, final int startRank, final int count) {
        final List<MultiRankData> result = new ArrayList<MultiRankData>();
        int seq = 0;
        int num = 0;
        Iterator<MultiRankData> di = null;
        switch (forceId) {
            case 1: {
                di = this.positionRankListA.iterator();
                break;
            }
            case 2: {
                di = this.positionRankListB.iterator();
                break;
            }
            case 3: {
                di = this.positionRankListC.iterator();
                break;
            }
        }
        if (di == null) {
            return result;
        }
        while (di.hasNext()) {
            final MultiRankData node = di.next();
            if (++seq >= startRank) {
                result.add(node);
                ++num;
            }
            if (num >= count) {
                break;
            }
        }
        return result;
    }
    
    public int getTotalPostionRankNumByForceId(final int forceId) {
        int result = 0;
        if (forceId == 1) {
            result = this.positionRankListA.size();
        }
        else if (forceId == 2) {
            result = this.positionRankListB.size();
        }
        else if (forceId == 3) {
            result = this.positionRankListC.size();
        }
        else {
            result = 120;
        }
        result = ((result > 120) ? 120 : result);
        return result;
    }
    
    public int getRankByPlayerId(final Integer forceId, final Integer playerId) {
        Map<Integer, PlayerRankInfo> map = null;
        switch (forceId) {
            case 1: {
                map = this.mapA;
                break;
            }
            case 2: {
                map = this.mapB;
                break;
            }
            case 3: {
                map = this.mapC;
                break;
            }
        }
        final PlayerRankInfo rankInfo = map.get((int)playerId);
        if (rankInfo == null || rankInfo.getRankData() == null) {
            return 0;
        }
        return rankInfo.getRankData().value.get(0).getValue();
    }
}
