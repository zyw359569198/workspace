package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.concurrent.*;
import java.util.*;

@Component("giftTxCache")
public class GiftTxCache extends AbstractCache<Integer, GiftTx>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<Integer, List<GiftTx>> txMap;
    
    public GiftTxCache() {
        this.txMap = new ConcurrentHashMap<Integer, List<GiftTx>>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<GiftTx> list = this.dataLoader.getModels((Class)GiftTx.class);
        for (final GiftTx temp : list) {
            super.put((Object)temp.getId(), (Object)temp);
            List<GiftTx> tList = this.txMap.get(temp.getType());
            if (tList == null) {
                tList = new ArrayList<GiftTx>();
            }
            tList.add(temp);
            this.txMap.put(temp.getType(), tList);
        }
    }
    
    public List<GiftTx> getGiftByType(final int type) {
        for (final int i : this.txMap.keySet()) {
            if (i == type) {
                return this.txMap.get(i);
            }
        }
        return null;
    }
    
    public GiftTx getGiftByTypeAndLv(final int type, final int lv) {
        List<GiftTx> gList = new ArrayList<GiftTx>();
        for (final int i : this.txMap.keySet()) {
            if (i == type) {
                gList = this.txMap.get(i);
            }
        }
        for (final GiftTx giftTx : gList) {
            if (giftTx.getLv() == lv) {
                return giftTx;
            }
        }
        return null;
    }
}
