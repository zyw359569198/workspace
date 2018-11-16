package com.reign.gcld.sdata.cache;

import org.springframework.stereotype.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("ktKjRelativeCache")
public class KtKjRelativeCache extends AbstractCache<Integer, KtKjT>
{
    Map<Integer, KtKjT> winToMap;
    Map<Integer, KtKjTr> winRewardMap;
    List<KtKjT> ktKjTs;
    @Autowired
    private SDataLoader dataLoader;
    
    public KtKjRelativeCache() {
        this.winToMap = null;
        this.winRewardMap = null;
        this.ktKjTs = null;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        this.winToMap = new HashMap<Integer, KtKjT>();
        this.winRewardMap = new HashMap<Integer, KtKjTr>();
        this.ktKjTs = new ArrayList<KtKjT>();
        final List<KtKjT> list = this.dataLoader.getModels((Class)KtKjT.class);
        for (final KtKjT ktKjT : list) {
            this.winToMap.put(ktKjT.getWin(), ktKjT);
            this.ktKjTs.add(ktKjT);
        }
        final List<KtKjTr> listTrs = this.dataLoader.getModels((Class)KtKjTr.class);
        for (final KtKjTr ktKjTr : listTrs) {
            this.winRewardMap.put(ktKjTr.getKindomLv(), ktKjTr);
        }
    }
    
    @Override
	public void clear() {
        super.clear();
        this.winToMap.clear();
        this.winRewardMap.clear();
        this.ktKjTs.clear();
    }
    
    public KtKjT getKtKjTByWinTimes(final int winTimes) {
        return this.winToMap.get(winTimes);
    }
    
    public KtKjTr getKtKjTrByKindomLv(final int kingdomLv) {
        return this.winRewardMap.get(kingdomLv);
    }
}
