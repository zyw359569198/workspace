package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("generalPositionCache")
public class GeneralPositionCache extends AbstractCache<Integer, GeneralPosition>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<Integer, Integer> lvCivilMap;
    private Map<Integer, Integer> lvMilitaryMap;
    private List<GeneralPosition> militaryList;
    
    public GeneralPositionCache() {
        this.lvCivilMap = new HashMap<Integer, Integer>();
        this.lvMilitaryMap = new HashMap<Integer, Integer>();
        this.militaryList = new ArrayList<GeneralPosition>();
    }
    
    public int getCivilCountByLv(final int lv) {
        return this.lvCivilMap.get(lv);
    }
    
    public int getMilitaryCountByLv(final int lv) {
        return this.lvMilitaryMap.get(lv);
    }
    
    public int getQuantityBylv(final int lv, final int type) {
        if (type == 1) {
            return this.lvCivilMap.get(lv);
        }
        return this.lvMilitaryMap.get(lv);
    }
    
    public List<GeneralPosition> getMilitaryList() {
        return this.militaryList;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<GeneralPosition> resultList = this.dataLoader.getModels((Class)GeneralPosition.class);
        int civil = 1;
        int lvMilitary = 1;
        int cCount = 0;
        int mCount = 0;
        for (final GeneralPosition temp : resultList) {
            super.put((Object)temp.getId(), (Object)temp);
            if (temp.getType() == 1) {
                for (int i = civil; i < temp.getOpenLv(); ++i) {
                    this.lvCivilMap.put(i, cCount);
                }
                civil = temp.getOpenLv();
                ++cCount;
            }
            else {
                for (int i = lvMilitary; i < temp.getOpenLv(); ++i) {
                    this.lvMilitaryMap.put(i, mCount);
                }
                lvMilitary = temp.getOpenLv();
                ++mCount;
                this.militaryList.add(temp);
            }
        }
        for (int j = civil; j <= 500; ++j) {
            this.lvCivilMap.put(j, cCount);
        }
        for (int j = lvMilitary; j <= 500; ++j) {
            this.lvMilitaryMap.put(j, mCount);
        }
    }
    
    @Override
	public void clear() {
        super.clear();
        this.lvCivilMap.clear();
        this.lvMilitaryMap.clear();
        this.militaryList.clear();
    }
}
