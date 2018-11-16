package com.reign.kf.match.sdata.cache;

import com.reign.kf.match.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("chargeitemCache")
public class ChargeitemCache extends AbstractCache<Integer, Chargeitem>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<Integer, List<Chargeitem>> lvMap;
    public static int size;
    public static final int KFWDDOUBLE_KEY = 45;
    public static final int KFWDDOUBLE_INBATTLE_KEY = 46;
    public static final int KFGZ_BUYPHANTOM = 53;
    public static final int KFGZ_KCI_CHUJI_CD = 54;
    public static final int MUBINGLING = 13;
    public static final int BATTLE_LEGION_INSPIRE = 63;
    public static final int TEAM_ORDER_COST = 72;
    public static ChargeitemCache cc;
    
    static {
        ChargeitemCache.size = 0;
        ChargeitemCache.cc = null;
    }
    
    public ChargeitemCache() {
        this.lvMap = new HashMap<Integer, List<Chargeitem>>();
    }
    
    public static Chargeitem getById(final int id) {
        return (Chargeitem)ChargeitemCache.cc.get((Object)id);
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<Chargeitem> resultList = this.dataLoader.getModels((Class)Chargeitem.class);
        for (final Chargeitem chargeitem : resultList) {
            super.put((Object)chargeitem.getId(), (Object)chargeitem);
            if (ChargeitemCache.size < chargeitem.getId()) {
                ChargeitemCache.size = chargeitem.getId();
            }
            if (chargeitem.getIfShow() == 1) {
                List<Chargeitem> list = this.lvMap.get(chargeitem.getLv());
                if (list == null) {
                    list = new ArrayList<Chargeitem>();
                }
                list.add(chargeitem);
                this.lvMap.put(chargeitem.getLv(), list);
            }
        }
        ChargeitemCache.cc = this;
    }
    
    @Override
	public void clear() {
        super.clear();
        this.lvMap.clear();
    }
    
    public List<Chargeitem> getLvList(final int lv) {
        return this.lvMap.get(lv);
    }
}
