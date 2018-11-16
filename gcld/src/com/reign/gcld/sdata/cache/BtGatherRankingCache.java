package com.reign.gcld.sdata.cache;

import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.sdata.domain.*;
import java.util.*;

@Component("btGatherRankingCache")
public class BtGatherRankingCache extends AbstractCache<Integer, BtGatherRanking>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<Integer, List<BtGatherRanking>> btGatherRankingMap;
    private Map<Integer, BtHyReward> btHyRewardMap;
    private Map<Integer, List<BtKillRanking>> btKillRankingMap;
    private Map<Integer, BtSoloReward> btSoloRewardMap;
    private Map<Integer, BtLevel> btLevelMap;
    private Map<Integer, BtRoad> btRoadMap;
    private List<BtKillRanking> btKillRankingList;
    
    public BtGatherRankingCache() {
        this.btGatherRankingMap = new HashMap<Integer, List<BtGatherRanking>>();
        this.btHyRewardMap = new HashMap<Integer, BtHyReward>();
        this.btKillRankingMap = new HashMap<Integer, List<BtKillRanking>>();
        this.btSoloRewardMap = new HashMap<Integer, BtSoloReward>();
        this.btLevelMap = new HashMap<Integer, BtLevel>();
        this.btRoadMap = new HashMap<Integer, BtRoad>();
        this.btKillRankingList = new ArrayList<BtKillRanking>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<BtGatherRanking> resultList0 = this.dataLoader.getModels((Class)BtGatherRanking.class);
        for (final BtGatherRanking bgr : resultList0) {
            final int kindomLv = bgr.getKindomLv();
            List<BtGatherRanking> list = this.btGatherRankingMap.get(kindomLv);
            if (list == null) {
                list = new ArrayList<BtGatherRanking>();
            }
            list.add(bgr);
            this.btGatherRankingMap.put(kindomLv, list);
        }
        final List<BtHyReward> resultList2 = this.dataLoader.getModels((Class)BtHyReward.class);
        for (final BtHyReward bhr : resultList2) {
            this.btHyRewardMap.put(bhr.getId(), bhr);
        }
        final List<BtKillRanking> resultList3 = this.dataLoader.getModels((Class)BtKillRanking.class);
        for (final BtKillRanking bkr : resultList3) {
            final int hzLv = bkr.getBtLv();
            List<BtKillRanking> list2 = this.btKillRankingMap.get(hzLv);
            if (list2 == null) {
                list2 = new ArrayList<BtKillRanking>();
            }
            list2.add(bkr);
            this.btKillRankingMap.put(hzLv, list2);
            this.btKillRankingList.add(bkr);
        }
        final List<BtSoloReward> resultList4 = this.dataLoader.getModels((Class)BtSoloReward.class);
        for (final BtSoloReward bsr : resultList4) {
            this.btSoloRewardMap.put(bsr.getId(), bsr);
        }
        final List<BtLevel> resultList5 = this.dataLoader.getModels((Class)BtLevel.class);
        for (final BtLevel bl : resultList5) {
            this.btLevelMap.put(bl.getLv(), bl);
        }
        final List<BtRoad> resultList6 = this.dataLoader.getModels((Class)BtRoad.class);
        for (final BtRoad br : resultList6) {
            this.btRoadMap.put(br.getId(), br);
        }
    }
    
    public BtGatherRanking getGatherRankByKindomLvAndRankLv(final int kindomLv, final int rankLv) {
        if (kindomLv <= 0 || rankLv <= 0) {
            return null;
        }
        final List<BtGatherRanking> list = this.btGatherRankingMap.get(kindomLv);
        if (list != null) {
            for (final BtGatherRanking bgr : list) {
                if (rankLv <= bgr.getLowLv()) {
                    return bgr;
                }
            }
        }
        return null;
    }
    
    public BtHyReward getBtHyRewardByPhantomNum(final int phantomNum) {
        if (phantomNum <= 0) {
            return null;
        }
        final int size = this.btHyRewardMap.size();
        int rKey = -1;
        for (final int key : this.btHyRewardMap.keySet()) {
            final BtHyReward bhr = this.btHyRewardMap.get(key);
            if (phantomNum < bhr.getNum()) {
                rKey = key - 1;
                break;
            }
        }
        if (rKey == 0) {
            return null;
        }
        if (rKey == -1) {
            return this.btHyRewardMap.get(size);
        }
        return this.btHyRewardMap.get(rKey);
    }
    
    public BtKillRanking getKillRankByhzLvAndRankLv(final int hzLv, final int rankLv) {
        if (rankLv <= 0 || hzLv <= 0) {
            return null;
        }
        final List<BtKillRanking> list = this.btKillRankingMap.get(hzLv);
        if (list != null) {
            for (final BtKillRanking bkr : list) {
                if (rankLv <= bkr.getLowLv()) {
                    return bkr;
                }
            }
        }
        return null;
    }
    
    public BtLevel getBtLevelByForce(final int force) {
        final int size = this.btLevelMap.size();
        int rKey = -1;
        for (final int key : this.btLevelMap.keySet()) {
            final BtLevel bl = this.btLevelMap.get(key);
            if (force < bl.getNum()) {
                rKey = key - 1;
                break;
            }
        }
        if (rKey == 0) {
            return null;
        }
        if (rKey == -1) {
            return this.btLevelMap.get(size);
        }
        return this.btLevelMap.get(rKey);
    }
    
    public List<BtLevel> getLvListByLv(final int btLv) {
        final List<BtLevel> result = new ArrayList<BtLevel>();
        for (final int key : this.btLevelMap.keySet()) {
            if (key <= btLv) {
                result.add(this.btLevelMap.get(key));
            }
        }
        return result;
    }
    
    public List<BtLevel> getAllLvList() {
        final List<BtLevel> result = new ArrayList<BtLevel>();
        for (final int key : this.btLevelMap.keySet()) {
            result.add(this.btLevelMap.get(key));
        }
        return result;
    }
    
    public int getForceByBtLv(final int btLv) {
        if (btLv <= 0) {
            return 0;
        }
        return this.btLevelMap.get(btLv).getNum();
    }
    
    public BtLevel getNextBtLv(final int curbtLv) {
        final int size = this.btLevelMap.size();
        final int nextLv = curbtLv + 1;
        if (nextLv > size || nextLv <= 0) {
            return null;
        }
        return this.btLevelMap.get(nextLv);
    }
    
    public BtLevel getBtLv(final int btLv) {
        if (btLv <= 0 || btLv > this.btLevelMap.size()) {
            return null;
        }
        return this.btLevelMap.get(btLv);
    }
    
    public String getPathBySenderAndRev(final int send, final int rev) {
        for (final int key : this.btRoadMap.keySet()) {
            if (send == this.btRoadMap.get(key).getSendNation() && rev == this.btRoadMap.get(key).getReceiveNation()) {
                return this.btRoadMap.get(key).getPath();
            }
        }
        return null;
    }
    
    public BtSoloReward getBtSoloRewardByNum(final int num) {
        for (final BtSoloReward bsr : this.btSoloRewardMap.values()) {
            if (num == bsr.getNum()) {
                return bsr;
            }
        }
        return null;
    }
    
    public List<BtKillRanking> getKillRankByLv(final int lv) {
        if (lv <= 0) {
            return null;
        }
        final List<BtKillRanking> result = new ArrayList<BtKillRanking>();
        for (final BtKillRanking bkr : this.btKillRankingList) {
            if (bkr.getLv() == lv) {
                result.add(bkr);
            }
        }
        return result;
    }
    
    public int getBtLvSize() {
        return this.btLevelMap.size();
    }
}
