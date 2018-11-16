package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.common.log.*;
import java.util.concurrent.*;
import java.util.*;

@Component("prisonLashRewardCache")
public class PrisonLashRewardCache extends AbstractCache<Integer, PrisonLashReward>
{
    @Autowired
    private SDataLoader dataLoader;
    private Logger errorLog;
    private Map<Integer, PrisonLashReward> levelExpMap;
    private Map<Integer, PrisonLashReward> officerExpMap;
    private Map<String, Integer> prisonLvLevelMap;
    private Map<String, Integer> prisonLvOfficerMap;
    
    public PrisonLashRewardCache() {
        this.errorLog = CommonLog.getLog(PrisonLashRewardCache.class);
        this.levelExpMap = new HashMap<Integer, PrisonLashReward>();
        this.officerExpMap = new HashMap<Integer, PrisonLashReward>();
        this.prisonLvLevelMap = new ConcurrentHashMap<String, Integer>();
        this.prisonLvOfficerMap = new ConcurrentHashMap<String, Integer>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<PrisonLashReward> resultList = this.dataLoader.getModels((Class)PrisonLashReward.class);
        for (final PrisonLashReward temp : resultList) {
            super.put((Object)temp.getId(), (Object)temp);
            if (1 == temp.getType()) {
                this.levelExpMap.put(temp.getId(), temp);
            }
            else {
                if (2 != temp.getType()) {
                    continue;
                }
                this.officerExpMap.put(temp.getId(), temp);
            }
        }
    }
    
    public int getRewardExp(final int prisonLv, final int generalLv, final int officerLv) {
        return this.getLevelExp(prisonLv, generalLv) + this.getOfficerExp(prisonLv, officerLv);
    }
    
    private int getLevelExp(final int prisonLv, final int generalLv) {
        final String key = this.getKey(prisonLv, generalLv);
        if (this.prisonLvLevelMap.containsKey(key)) {
            return this.prisonLvLevelMap.get(key);
        }
        for (final PrisonLashReward levelExp : this.levelExpMap.values()) {
            if (levelExp.getPrisonLowLv() <= prisonLv && prisonLv <= levelExp.getPrisonHighLv() && levelExp.getLowLv() <= generalLv && generalLv <= levelExp.getHighLv()) {
                this.prisonLvLevelMap.put(key, levelExp.getExpReward());
                return levelExp.getExpReward();
            }
        }
        this.errorLog.error("class:PrisonLashRewardCache#method:getLevelExp#prisonLv:" + prisonLv + "#generalLv:" + generalLv);
        this.prisonLvLevelMap.put(key, 0);
        return 0;
    }
    
    private int getOfficerExp(final int prisonLv, final int officerLv) {
        if (prisonLv < 3 || officerLv <= 0) {
            return 0;
        }
        final String key = this.getKey(prisonLv, officerLv);
        if (this.prisonLvOfficerMap.containsKey(key)) {
            return this.prisonLvOfficerMap.get(key);
        }
        for (final PrisonLashReward levelExp : this.officerExpMap.values()) {
            if (levelExp.getPrisonLowLv() <= prisonLv && prisonLv <= levelExp.getPrisonHighLv() && levelExp.getOfficialLow() <= officerLv && officerLv <= levelExp.getOfficialHigh()) {
                this.prisonLvOfficerMap.put(key, levelExp.getExpReward());
                return levelExp.getExpReward();
            }
        }
        this.errorLog.error("class:PrisonLashRewardCache#method:getOfficerExp#prisonLv:" + prisonLv + "#officerLv:" + officerLv);
        this.prisonLvOfficerMap.put(key, 0);
        return 0;
    }
    
    private String getKey(final int prisonLv, final int parm) {
        return String.valueOf(prisonLv) + "_" + parm;
    }
    
    @Override
	public void clear() {
        super.clear();
        this.levelExpMap.clear();
        this.officerExpMap.clear();
        this.prisonLvLevelMap.clear();
        this.prisonLvOfficerMap.clear();
    }
}
