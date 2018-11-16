package com.reign.gcld.civiltrick.trick;

import org.springframework.stereotype.*;
import com.reign.gcld.world.dao.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;
import java.util.concurrent.*;
import org.apache.commons.lang.*;
import com.reign.gcld.world.domain.*;

@Component("cityTrickStateCache")
public class CityTrickStateCache implements ICityTrickStateCache
{
    @Autowired
    private ICityDao cityDao;
    private Map<Integer, String> stateMap;
    
    public CityTrickStateCache() {
        this.stateMap = new ConcurrentHashMap<Integer, String>();
    }
    
    public String getCityTrickState(final int cityId) {
        final Integer cityIndex = cityId;
        if (this.stateMap.containsKey(cityIndex)) {
            return this.stateMap.get(cityIndex);
        }
        final City city = this.cityDao.read(cityId);
        final String trickInfo = city.getTrickinfo();
        if (StringUtils.isBlank(trickInfo)) {
            return "";
        }
        return TrickFactory.getCityState(trickInfo);
    }
    
    @Override
    public void changeCityState(final int cityId) {
        if (this.stateMap.containsKey(cityId)) {
            this.stateMap.remove(cityId);
        }
    }
}
