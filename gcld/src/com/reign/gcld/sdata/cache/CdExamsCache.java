package com.reign.gcld.sdata.cache;

import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.util.*;
import com.reign.gcld.nation.service.*;
import com.reign.gcld.common.log.*;
import java.util.concurrent.*;
import java.util.*;
import com.reign.gcld.sdata.domain.*;

@Component("cdExamsCache")
public class CdExamsCache extends AbstractCache<Integer, CdExams>
{
    private static final Logger errorLog;
    @Autowired
    private SDataLoader dataLoader;
    @Autowired
    private KingdomLvCache kingdomLvCache;
    @Autowired
    private CdExamsRankingCache cdExamsRankingCache;
    private Map<String, Integer> expMap;
    private Map<String, Integer> rankingExpMap;
    private Map<String, Integer> pRankingExpMap;
    private Map<String, Integer> rankingIronMap;
    private Map<String, Integer> pRankingIronMap;
    private Map<Integer, List<Tuple<Integer, Integer>>> idListMap;
    private Map<String, Integer> generalNumMap;
    
    static {
        errorLog = CommonLog.getLog(NationService.class);
    }
    
    public CdExamsCache() {
        this.expMap = new ConcurrentHashMap<String, Integer>();
        this.rankingExpMap = new ConcurrentHashMap<String, Integer>();
        this.pRankingExpMap = new ConcurrentHashMap<String, Integer>();
        this.rankingIronMap = new ConcurrentHashMap<String, Integer>();
        this.pRankingIronMap = new ConcurrentHashMap<String, Integer>();
        this.idListMap = new ConcurrentHashMap<Integer, List<Tuple<Integer, Integer>>>();
        this.generalNumMap = new ConcurrentHashMap<String, Integer>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<CdExams> resultList = this.dataLoader.getModels((Class)CdExams.class);
        for (final CdExams temp : resultList) {
            super.put((Object)temp.getId(), (Object)temp);
        }
    }
    
    public List<Integer> getIdList(final int beforeLv, final int beforeExp, final int afterLv, final int afterExp) {
        final List<Integer> beforeIdList = new ArrayList<Integer>();
        final int size = this.getModels().size();
        for (int i = 1; i <= size; ++i) {
            final CdExams ce = (CdExams)this.get((Object)i);
            if (beforeLv <= ce.getKdLv() && (beforeLv != ce.getKdLv() || beforeExp < ce.getKdExp())) {
                break;
            }
            beforeIdList.add(i);
        }
        final List<Integer> afterIdList = new ArrayList<Integer>();
        for (int j = 1; j <= size; ++j) {
            final CdExams ce2 = (CdExams)this.get((Object)j);
            if (afterLv <= ce2.getKdLv() && (afterLv != ce2.getKdLv() || afterExp < ce2.getKdExp())) {
                break;
            }
            afterIdList.add(j);
        }
        afterIdList.removeAll(beforeIdList);
        return afterIdList;
    }
    
    public int getNeedExp(final int lv, final int exp) {
        final String key = this.getKey(lv, exp);
        if (this.expMap.containsKey(key)) {
            return this.expMap.get(key);
        }
        final int size = this.getModels().size();
        boolean flag = false;
        int result = 0;
        for (int i = 1; i <= size; ++i) {
            final CdExams ce = (CdExams)this.get((Object)i);
            if (ce.getKdLv() == lv) {
                if (ce.getKdExp() > exp) {
                    result = ce.getKdExp() - exp;
                    break;
                }
                flag = true;
            }
            else if (ce.getKdLv() > lv && flag) {
                result = this.kingdomLvCache.getExp(lv, ce.getKdLv()) - exp + ce.getKdExp();
                break;
            }
        }
        this.expMap.put(key, result);
        return result;
    }
    
    public Tuple<Boolean, CdExams> getCdExams(final int forceLv, final int forceExp) {
        final Tuple<Boolean, CdExams> tuple = new Tuple();
        for (int size = this.getModels().size(), i = 1; i <= size; ++i) {
            final CdExams ce = (CdExams)this.get((Object)i);
            if (ce.getKdLv() == forceLv) {
                if (ce.getKdExp() > forceExp) {
                    tuple.left = true;
                    tuple.right = ce;
                    return tuple;
                }
            }
            else if (ce.getKdLv() > forceLv) {
                tuple.left = false;
                tuple.right = ce;
                return tuple;
            }
        }
        return null;
    }
    
    public Tuple<Integer, Integer> getTuple(final int forceLv, final int forceExp) {
        final Tuple<Integer, Integer> tuple = new Tuple();
        for (int size = this.getModels().size(), i = 1; i <= size; ++i) {
            final CdExams ce = (CdExams)this.get((Object)i);
            if (ce.getKdLv() == forceLv) {
                if (ce.getKdExp() > forceExp) {
                    tuple.left = ce.getKdExp();
                    tuple.right = ((KindomLv)this.kingdomLvCache.get((Object)forceLv)).getExpUpgrade();
                    return tuple;
                }
            }
            else if (ce.getKdLv() > forceLv) {
                tuple.left = ce.getKdExp();
                tuple.right = ((KindomLv)this.kingdomLvCache.get((Object)(forceLv + 1))).getExpUpgrade();
                return tuple;
            }
        }
        return null;
    }
    
    public int getDisplayLv(final int forceLv, final int forceExp) {
        for (int size = this.getModels().size(), i = 1; i <= size; ++i) {
            final CdExams ce = (CdExams)this.get((Object)i);
            if (ce.getKdLv() == forceLv) {
                if (ce.getKdExp() > forceExp) {
                    return forceLv;
                }
            }
            else if (ce.getKdLv() > forceLv) {
                return ce.getKdLv();
            }
        }
        return 0;
    }
    
    public int getRankingExp(final int id, final int rank) {
        final String key = this.getRankingKey(id, rank);
        if (this.rankingExpMap.containsKey(key)) {
            return this.rankingExpMap.get(key);
        }
        final CdExams ce = (CdExams)this.get((Object)id);
        final int exp = (int)(ce.getRankingBaseExp() * this.cdExamsRankingCache.getRewardRate(rank));
        this.rankingExpMap.put(key, exp);
        return exp;
    }
    
    public int getPRankingExp(final int id, final int rank) {
        final String key = this.getPRankingKey(id, rank);
        if (this.pRankingExpMap.containsKey(key)) {
            return this.pRankingExpMap.get(key);
        }
        final CdExams ce = (CdExams)this.get((Object)id);
        final int exp = (int)(ce.getRkBasePExp() * this.cdExamsRankingCache.getRewardRate(rank));
        this.pRankingExpMap.put(key, exp);
        return exp;
    }
    
    public int getRankingIron(final int id, final int rank) {
        final String key = this.getRankingKey(id, rank);
        if (this.rankingIronMap.containsKey(key)) {
            return this.rankingIronMap.get(key);
        }
        final CdExams ce = (CdExams)this.get((Object)id);
        final int iron = (int)(ce.getRankingBaseIron() * this.cdExamsRankingCache.getRewardRate(rank));
        this.rankingIronMap.put(key, iron);
        return iron;
    }
    
    public int getPRankingIron(final int id, final int rank) {
        final String key = this.getPRankingKey(id, rank);
        if (this.pRankingIronMap.containsKey(key)) {
            return this.pRankingIronMap.get(key);
        }
        final CdExams ce = (CdExams)this.get((Object)id);
        final int iron = (int)(ce.getRkBasePIron() * this.cdExamsRankingCache.getRewardRate(rank));
        this.pRankingIronMap.put(key, iron);
        return iron;
    }
    
    public List<Tuple<Integer, Integer>> getKillList(final int id) {
        if (this.idListMap.containsKey(id)) {
            this.idListMap.get(id);
        }
        final CdExams ce = (CdExams)this.get((Object)id);
        final List<Tuple<Integer, Integer>> stageList = new ArrayList<Tuple<Integer, Integer>>();
        stageList.add(new Tuple(1, ce.getGNum1()));
        stageList.add(new Tuple(2, ce.getGNum2()));
        stageList.add(new Tuple(3, ce.getGNum3()));
        this.idListMap.put(id, stageList);
        return stageList;
    }
    
    public int getGeneralNum(final int id, final int stage) {
        final String key = this.getGeneralNumKey(id, stage);
        if (this.generalNumMap.containsKey(key)) {
            return this.generalNumMap.get(key);
        }
        final CdExams ce = (CdExams)this.get((Object)id);
        if (ce == null) {
            CdExamsCache.errorLog.error("class:CdExamsCache#method:getGeneralNum#id:" + id);
            this.generalNumMap.put(key, 0);
            return 0;
        }
        int generalNum = 0;
        if (stage < 4) {
            if (stage == 0) {
                generalNum = ce.getOpenKg1();
            }
            else if (stage == 1) {
                generalNum = ce.getOpenKg2();
            }
            else if (stage == 2) {
                generalNum = ce.getOpenKg3();
            }
            else if (stage == 3) {
                generalNum = ce.getGNum0() + ce.getGNum1() + ce.getGNum2();
            }
        }
        if (generalNum == 0) {
            CdExamsCache.errorLog.error("class:CdExamsCache#method:getGeneralNum#id:" + id + "#stage:" + stage);
        }
        this.generalNumMap.put(key, generalNum);
        return generalNum;
    }
    
    private String getKey(final int lv, final int exp) {
        return String.valueOf(lv) + "_" + exp;
    }
    
    private String getRankingKey(final int id, final int rank) {
        return String.valueOf(id) + "_" + rank;
    }
    
    private String getPRankingKey(final int id, final int rank) {
        return String.valueOf(id) + "_" + rank;
    }
    
    private String getGeneralNumKey(final int id, final int stage) {
        return String.valueOf(id) + "_" + stage;
    }
    
    @Override
	public void clear() {
        super.clear();
        this.expMap.clear();
        this.rankingExpMap.clear();
        this.pRankingExpMap.clear();
        this.rankingIronMap.clear();
        this.pRankingIronMap.clear();
        this.idListMap.clear();
        this.generalNumMap.clear();
    }
}
