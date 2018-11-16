package com.reign.gcld.sdata.cache;

import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.common.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.sdata.domain.*;
import java.util.*;

@Component("worldGuardCache")
public class WorldGuardCache extends AbstractCache<Integer, WorldGuard>
{
    @Autowired
    private SDataLoader dataLoader;
    @Autowired
    private IDataGetter dataGetter;
    private Map<Integer, Map<Integer, WorldGuard>> wgMap;
    
    public WorldGuardCache() {
        this.wgMap = new HashMap<Integer, Map<Integer, WorldGuard>>();
    }
    
    public WorldGuard getByForceIdDegree(final int forceId, int degree) {
        final int days = this.dataGetter.getRankService().getCountryNpcDefDays();
        final WnCitynpcLv wnCitynpcLv = this.dataGetter.getWnCitynpcLvCache().getWnCitynpcLvByDay(days);
        if (wnCitynpcLv == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("wnCitynpcLv is null").append("days", days).appendClassName(this.getClass().getSimpleName()).appendMethodName("getByForceIdDegree").flush();
            return null;
        }
        if (this.wgMap.get(forceId) == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("gMap.get(forceId) is null").append("forceId", forceId).appendClassName(this.getClass().getSimpleName()).appendMethodName("getByForceIdDegree").flush();
            return null;
        }
        degree *= wnCitynpcLv.getGuardE();
        if (this.wgMap.get(forceId).get(degree) == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("wgMap.get(forceId).get(degree) is null").append("forceId", forceId).append("degree", degree).append("wnCitynpcLv.getGuardE()", wnCitynpcLv.getGuardE()).appendClassName(this.getClass().getSimpleName()).appendMethodName("getByForceIdDegree").flush();
            return null;
        }
        return this.wgMap.get(forceId).get(degree);
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<WorldGuard> list = this.dataLoader.getModels((Class)WorldGuard.class);
        final Map<Integer, WorldGuard> weiMap = new HashMap<Integer, WorldGuard>();
        final Map<Integer, WorldGuard> shuMap = new HashMap<Integer, WorldGuard>();
        final Map<Integer, WorldGuard> wuMap = new HashMap<Integer, WorldGuard>();
        this.wgMap.put(1, weiMap);
        this.wgMap.put(2, shuMap);
        this.wgMap.put(3, wuMap);
        for (final WorldGuard wg : list) {
            super.put((Object)wg.getId(), (Object)wg);
            if (wg.getCountry() == 1) {
                weiMap.put(wg.getDegree(), wg);
            }
            else if (wg.getCountry() == 2) {
                shuMap.put(wg.getDegree(), wg);
            }
            else {
                if (wg.getCountry() != 3) {
                    continue;
                }
                wuMap.put(wg.getDegree(), wg);
            }
        }
    }
    
    @Override
	public void clear() {
        super.clear();
        this.wgMap.clear();
    }
}
