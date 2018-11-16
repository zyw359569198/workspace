package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.rank.common.*;
import java.util.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.battle.reward.*;

@Component("killTopListTreasureCache")
public class KillTopListTreasureCache extends AbstractCache<Integer, KillToplistTreasure>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<Integer, List<Integer>> lvToTreasureMap;
    private List<Integer> lv;
    
    public KillTopListTreasureCache() {
        this.lvToTreasureMap = new HashMap<Integer, List<Integer>>();
        this.lv = new ArrayList<Integer>();
    }
    
    public SDataLoader getDataLoader() {
        return this.dataLoader;
    }
    
    public void setDataLoader(final SDataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<KillToplistTreasure> list = this.dataLoader.getModels((Class)KillToplistTreasure.class);
        for (final KillToplistTreasure k : list) {
            super.put((Object)k.getId(), (Object)k);
            if (this.lvToTreasureMap.containsKey(k.getMinLv())) {
                final List<Integer> list2 = this.lvToTreasureMap.get(k.getMinLv());
                if (!list2.contains(k)) {
                    list2.add(k.getId());
                }
            }
            else {
                final List<Integer> list3 = new ArrayList<Integer>();
                list3.add(k.getId());
                this.lvToTreasureMap.put(k.getMinLv(), list3);
            }
            if (!this.lv.contains(k.getId())) {
                this.lv.add(k.getMinLv());
            }
        }
    }
    
    public BoxDto getReward(final int playerLv) {
        final BoxDto dto = new BoxDto();
        final List<Integer> canGet = this.canGetList(playerLv);
        int sumTotal = 0;
        for (int i = 0; i < canGet.size(); ++i) {
            final KillToplistTreasure treasure = (KillToplistTreasure)this.get((Object)canGet.get(i));
            sumTotal += treasure.getProb();
        }
        Collections.sort(canGet);
        int sum = 0;
        final int prob = WebUtil.nextInt(sumTotal);
        for (int j = 0; j < canGet.size(); ++j) {
            final KillToplistTreasure treasure2 = (KillToplistTreasure)this.get((Object)canGet.get(j));
            sum += treasure2.getProb();
            if (prob <= sum) {
                final String rewardString = treasure2.getReward();
                final String[] single = rewardString.split(",");
                final int type = RewardType.getTypeInt(single[0]);
                dto.setRewardType(type);
                if (type == 5) {
                    dto.setQuality(Integer.parseInt(single[1]));
                    dto.setNumber(Integer.parseInt(single[2]));
                }
                else if (type == 6) {
                    dto.setNumber(1);
                }
                else {
                    dto.setNumber(single[1]);
                }
                return dto;
            }
        }
        return null;
    }
    
    private List<Integer> canGetList(final int playerLv) {
        final List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < this.lv.size(); ++i) {
            if (this.lv.get(i) <= playerLv) {
                for (final Integer integer : this.lvToTreasureMap.get(this.lv.get(i))) {
                    if (!result.contains(integer)) {
                        result.add(integer);
                    }
                }
            }
        }
        return result;
    }
    
    @Override
	public void clear() {
        super.clear();
        this.lvToTreasureMap.clear();
        this.lv.clear();
    }
}
