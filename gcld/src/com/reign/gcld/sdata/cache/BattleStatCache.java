package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("battleStatCache")
public class BattleStatCache extends AbstractCache<Integer, BattleStat>
{
    @Autowired
    private SDataLoader dataLoader;
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<BattleStat> resultList = this.dataLoader.getModels((Class)BattleStat.class);
        for (final BattleStat battleStat : resultList) {
            super.put((Object)battleStat.getStat(), (Object)battleStat);
        }
    }
    
    @Override
	public void clear() {
        super.clear();
    }
    
    public int getTimePve(final int num) {
        int front = 1;
        for (final BattleStat bs : this.getModels()) {
            if (bs.getTimePve() > num) {
                break;
            }
            front = bs.getStat();
        }
        return front;
    }
    
    public int getWorldPvp(final int num) {
        int front = 1;
        for (final BattleStat bs : this.getModels()) {
            if (bs.getWorldPvp() > num) {
                break;
            }
            front = bs.getStat();
        }
        return front;
    }
}
