package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;
import com.reign.gcld.battle.common.*;

@Component("etiquetePointCache")
public class EtiquetePointCache extends AbstractCache<Integer, EtiquetePoint>
{
    @Autowired
    private SDataLoader dataLoader;
    private List<EtiquetePoint> orderList;
    private int maxPoint;
    
    public EtiquetePointCache() {
        this.orderList = null;
        this.maxPoint = 0;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<EtiquetePoint> result = this.dataLoader.getModels((Class)EtiquetePoint.class);
        for (final EtiquetePoint temp : result) {
            if (temp.getDemand() > this.maxPoint) {
                this.maxPoint = temp.getDemand();
            }
            final BattleDrop rewardDrop = BattleDropFactory.getInstance().getBattleDrop(temp.getReward());
            temp.setRewardDrop(rewardDrop);
            super.put((Object)temp.getId(), (Object)temp);
        }
        Collections.sort(this.orderList = result, new EtiquetePointComparator());
    }
    
    public int getMaxLiYiDu() {
        return this.maxPoint;
    }
    
    public List<EtiquetePoint> getLiYiDuOrderList() {
        return this.orderList;
    }
    
    @Override
	public void clear() {
        this.orderList = null;
        super.clear();
    }
    
    static class EtiquetePointComparator implements Comparator<EtiquetePoint>
    {
        @Override
        public int compare(final EtiquetePoint arg0, final EtiquetePoint arg1) {
            return arg0.getDemand() - arg1.getDemand();
        }
    }
}
