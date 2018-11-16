package com.reign.gcld.sdata.cache;

import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.sdata.domain.*;
import java.util.*;

@Component("marketDegreeCache")
public class MarketDegreeCache extends AbstractCache<Integer, MarketDegree>
{
    @Autowired
    private SDataLoader dataLoader;
    private static final Logger errorLog;
    
    static {
        errorLog = CommonLog.getLog(MarketDegreeCache.class);
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<MarketDegree> list = this.dataLoader.getModels((Class)MarketDegree.class);
        final List<MarketProducts> mpList = this.dataLoader.getModels((Class)MarketProducts.class);
        final Set<Integer> degreeSet = new HashSet<Integer>();
        for (final MarketProducts mp : mpList) {
            degreeSet.add(mp.getDegree());
        }
        for (final MarketDegree temp : list) {
            if (!degreeSet.contains(temp.getDegree())) {
                MarketDegreeCache.errorLog.info("MarketDegree has degree:" + temp.getDegree() + ", but it is not exist in MarketProducts");
                throw new RuntimeException("MarketDegree has degree:" + temp.getDegree() + ", but it is not exist in MarketProducts");
            }
            super.put((Object)temp.getDegree(), (Object)temp);
        }
        degreeSet.clear();
    }
    
    public int getDegreeByPlayerLv(final int playerLv) {
        for (final MarketDegree md : this.getModels()) {
            if (playerLv >= md.getMinLv() && playerLv <= md.getMaxLv()) {
                return md.getDegree();
            }
        }
        MarketDegreeCache.errorLog.info("player lv is  too big, then MarketDegree not have the degree mapped to this lv");
        return 0;
    }
}
