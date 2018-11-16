package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;
import com.reign.gcld.battle.common.*;

@Component("soloRewardCache")
public class SoloRewardCache extends AbstractCache<Integer, SoloReward>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<Integer, Map<Integer, Map<Integer, SoloReward>>> soloIdMap;
    
    public SoloRewardCache() {
        this.soloIdMap = new HashMap<Integer, Map<Integer, Map<Integer, SoloReward>>>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<SoloReward> list = this.dataLoader.getModels((Class)SoloReward.class);
        for (final SoloReward sc : list) {
            final Map<Integer, Integer> rewardMap = new HashMap<Integer, Integer>();
            final String[] strs = sc.getReward().split(";");
            String[] array;
            for (int length = (array = strs).length, i = 0; i < length; ++i) {
                final String str = array[i];
                final String[] ss = str.split(",");
                if (ss[0].equalsIgnoreCase("food")) {
                    rewardMap.put(3, Integer.valueOf(ss[1]));
                }
                else if (ss[0].equalsIgnoreCase("ChiefExp")) {
                    rewardMap.put(5, Integer.valueOf(ss[1]));
                }
                else if (ss[0].equalsIgnoreCase("iron")) {
                    rewardMap.put(4, Integer.valueOf(ss[1]));
                }
                else if (ss[0].equalsIgnoreCase("items")) {
                    rewardMap.put(43, Integer.valueOf(ss[1]));
                }
            }
            sc.setRewardMap(rewardMap);
            super.put((Object)sc.getId(), (Object)sc);
            Map<Integer, Map<Integer, SoloReward>> soMap = this.soloIdMap.get(sc.getSoloId());
            if (soMap == null) {
                soMap = new HashMap<Integer, Map<Integer, SoloReward>>();
                this.soloIdMap.put(sc.getSoloId(), soMap);
            }
            Map<Integer, SoloReward> map = soMap.get(sc.getDifficulty());
            if (map == null) {
                map = new HashMap<Integer, SoloReward>();
                soMap.put(sc.getDifficulty(), map);
            }
            map.put(sc.getStar(), sc);
        }
    }
    
    public Map<Integer, SoloReward> getBySoloId(final int soloId, final int difficulty) {
        return this.soloIdMap.get(soloId).get(difficulty);
    }
    
    public Integer getMaxJieBingCount(final int soloId, final int difficulty, final int star) {
        final Map<Integer, Map<Integer, SoloReward>> map1 = this.soloIdMap.get(soloId);
        if (map1 == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("map1 is null").append("soloId", soloId).append("difficulty", difficulty).append("star", star).appendClassName("SoloRewardCache").appendMethodName("getMaxJieBingCount").flush();
            return null;
        }
        final Map<Integer, SoloReward> map2 = map1.get(difficulty);
        if (map2 == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("map1 is null").append("soloId", soloId).append("difficulty", difficulty).append("star", star).appendClassName("SoloRewardCache").appendMethodName("getMaxJieBingCount").flush();
            return null;
        }
        final SoloReward soloReward = map2.get(star);
        if (soloReward == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("soloReward is null").append("soloId", soloId).append("difficulty", difficulty).append("star", star).appendClassName("SoloRewardCache").appendMethodName("getMaxJieBingCount").flush();
            return null;
        }
        return soloReward.getTeqHyNumber();
    }
    
    @Override
	public void clear() {
        super.clear();
        this.soloIdMap.clear();
    }
}
