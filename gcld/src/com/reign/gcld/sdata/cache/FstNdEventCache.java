package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.common.log.*;
import java.util.*;

@Component("fstNdEventCache")
public class FstNdEventCache extends AbstractCache<Integer, FstNdEvent>
{
    @Autowired
    private SDataLoader dataLoader;
    Logger logger;
    public static final String BMW = "bmw";
    public static final String XO = "xo";
    public static final String PICASSO = "picasso";
    public static final String GOLD = "gold";
    
    public FstNdEventCache() {
        this.logger = CommonLog.getLog(FstNdEventCache.class);
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<FstNdEvent> resultList = this.dataLoader.getModels((Class)FstNdEvent.class);
        for (final FstNdEvent temp : resultList) {
            super.put((Object)temp.getId(), (Object)temp);
            final String cost1 = temp.getCost1();
            final String[] tempArr = cost1.split(",");
            if (!"bmw".equals(tempArr[0]) && !"xo".equals(tempArr[0]) && !"picasso".equals(tempArr[0]) && !"gold".equals(tempArr[0])) {
                this.logger.error("class:FstNdEventCache#method:afterPropertiesSet#id:" + temp.getId() + "#cost1:" + cost1);
                throw new RuntimeException("class:FstNdEventCache#method:afterPropertiesSet#id:" + temp.getId() + "#cost1:" + cost1);
            }
            if (Integer.parseInt(tempArr[1]) <= 0) {
                this.logger.error("class:FstNdEventCache#method:afterPropertiesSet#id:" + temp.getId() + "#cost1:" + cost1 + "#negative");
                throw new RuntimeException("class:FstNdEventCache#method:afterPropertiesSet#id:" + temp.getId() + "#cost1:" + cost1 + "#negative");
            }
            final String cost2 = temp.getCost2();
            final String[] tempArr2 = cost1.split(",");
            if (!"bmw".equals(tempArr2[0]) && !"xo".equals(tempArr2[0]) && !"picasso".equals(tempArr2[0]) && !"gold".equals(tempArr2[0])) {
                this.logger.error("class:FstNdEventCache#method:afterPropertiesSet#id:" + temp.getId() + "#cost2:" + cost2);
                throw new RuntimeException("class:FstNdEventCache#method:afterPropertiesSet#id:" + temp.getId() + "#cost2:" + cost2);
            }
            if (Integer.parseInt(tempArr2[1]) <= 0) {
                this.logger.error("class:FstNdEventCache#method:afterPropertiesSet#id:" + temp.getId() + "#cost2:" + cost2 + "#negative");
                throw new RuntimeException("class:FstNdEventCache#method:afterPropertiesSet#id:" + temp.getId() + "#cost2:" + cost2 + "#negative");
            }
        }
    }
}
