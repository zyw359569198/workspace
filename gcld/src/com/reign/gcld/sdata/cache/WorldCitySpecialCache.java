package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.common.log.*;
import java.util.*;

@Component("worldCitySpecialCache")
public class WorldCitySpecialCache extends AbstractCache<Integer, WorldCitySpecial>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<Integer, Integer> key2CityIdCalcMap;
    private Map<Integer, Integer> key2CityIdDisplayMap;
    private Map<Integer, Integer> cityId2KeyDisplayMap;
    private static final Logger errorLog;
    
    static {
        errorLog = CommonLog.getLog(WorldCitySpecialCache.class);
    }
    
    public WorldCitySpecialCache() {
        this.key2CityIdCalcMap = new HashMap<Integer, Integer>();
        this.key2CityIdDisplayMap = new HashMap<Integer, Integer>();
        this.cityId2KeyDisplayMap = new HashMap<Integer, Integer>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<WorldCitySpecial> resultList = this.dataLoader.getModels((Class)WorldCitySpecial.class);
        for (final WorldCitySpecial temp : resultList) {
            if (temp.getCityId() < 1000) {
                super.put((Object)temp.getCityId(), (Object)temp);
                final Integer cityId = this.key2CityIdCalcMap.get(temp.getKey());
                if (cityId != null) {
                    WorldCitySpecialCache.errorLog.error("#the same key appear times bigger than 1#key:" + temp.getKey() + "#city1:" + cityId + "#cityId2:" + temp.getCityId());
                    throw new RuntimeException("#the same key appear times bigger than 1#key:" + temp.getKey() + "#city1:" + cityId + "#cityId2:" + temp.getCityId());
                }
                this.key2CityIdCalcMap.put(temp.getKey(), temp.getCityId());
                this.key2CityIdDisplayMap.put(temp.getKey(), temp.getCityId());
                this.cityId2KeyDisplayMap.put(temp.getCityId(), temp.getKey());
            }
        }
    }
    
    public Integer getCityIdCalByKey(final int key) {
        return this.key2CityIdCalcMap.get(key);
    }
    
    public Integer getCityIdDisplayByKey(final int key) {
        return this.key2CityIdDisplayMap.get(key);
    }
    
    public Integer getKeyDisplayByCityId(final int cityId) {
        return this.cityId2KeyDisplayMap.get(cityId);
    }
    
    public Set<Integer> getCityIdSet() {
        return this.cityId2KeyDisplayMap.keySet();
    }
    
    public Integer getCityIdCalByCityIdDisplay(final int cityIdDisplay) {
        final Integer key = this.getKeyDisplayByCityId(cityIdDisplay);
        return (key == null) ? null : this.getCityIdCalByKey(key);
    }
    
    @Override
	public void clear() {
        super.clear();
        this.key2CityIdCalcMap.clear();
        this.key2CityIdDisplayMap.clear();
        this.cityId2KeyDisplayMap.clear();
    }
}
