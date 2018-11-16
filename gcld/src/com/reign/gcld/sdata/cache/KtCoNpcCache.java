package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.common.*;
import java.util.*;
import com.reign.gcld.battle.common.*;

@Component("ktCoNpcCache")
public class KtCoNpcCache extends AbstractCache<Integer, KtCoNpc>
{
    @Autowired
    private SDataLoader dataLoader;
    @Autowired
    private IDataGetter dataGetter;
    private List<KtCoNpc> orderList;
    
    public KtCoNpcCache() {
        this.orderList = null;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<KtCoNpc> resultList = this.dataLoader.getModels((Class)KtCoNpc.class);
        for (final KtCoNpc ktHjNpc : resultList) {
            super.put((Object)ktHjNpc.getId(), (Object)ktHjNpc);
        }
        Collections.sort(this.orderList = resultList, new KtCoNpcComparator());
    }
    
    public KtCoNpc getKtCoNpcOfNow() {
        try {
            KtCoNpc result = null;
            final int days = this.dataGetter.getRankService().nowDays();
            for (int i = this.orderList.size() - 1; i >= 0; --i) {
                final KtCoNpc ktCoNpc = this.orderList.get(i);
                if (days >= ktCoNpc.getDay()) {
                    result = ktCoNpc;
                    break;
                }
            }
            if (result == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("KtCoNpc is null, return default value").append("days", days).appendClassName("KtCoNpcCache").appendMethodName("getKtCoNpcOfNow").flush();
                return this.orderList.get(0);
            }
            return result;
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("KtCoNpcCache.getKtCoNpcOfNow catch Exception,return default value", e);
            return this.orderList.get(0);
        }
    }
    
    @Override
	public void clear() {
        this.orderList = null;
        super.clear();
    }
    
    static class KtCoNpcComparator implements Comparator<KtCoNpc>
    {
        @Override
        public int compare(final KtCoNpc arg0, final KtCoNpc arg1) {
            return arg0.getDay() - arg1.getDay();
        }
    }
}
