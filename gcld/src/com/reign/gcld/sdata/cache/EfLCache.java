package com.reign.gcld.sdata.cache;

import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.world.common.*;
import java.util.*;

@Component("EfLCache")
public class EfLCache extends AbstractCache<Integer, EfL>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<Integer, Map<Integer, List<EfL>>> forceIdLinesMap;
    
    public EfLCache() {
        this.forceIdLinesMap = new HashMap<Integer, Map<Integer, List<EfL>>>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final Set<Integer> citySet = new HashSet<Integer>();
        final List<WorldCity> list = this.dataLoader.getModels((Class)WorldCity.class);
        for (final WorldCity wc : list) {
            citySet.add(wc.getId());
        }
        final List<EfL> resultList = this.dataLoader.getModels((Class)EfL.class);
        for (final EfL efl : resultList) {
            Map<Integer, List<EfL>> forceLinesMap = this.forceIdLinesMap.get(efl.getC());
            if (forceLinesMap == null) {
                forceLinesMap = new HashMap<Integer, List<EfL>>();
                this.forceIdLinesMap.put(efl.getC(), forceLinesMap);
            }
            List<EfL> directionLines = forceLinesMap.get(efl.getI());
            if (directionLines == null) {
                directionLines = new LinkedList<EfL>();
                forceLinesMap.put(efl.getI(), directionLines);
            }
            directionLines.add(efl);
            final String[] array = efl.getL().split(";");
            final Integer[] cityIds = new Integer[array.length];
            for (int i = 0; i < array.length; ++i) {
                int cityId = 0;
                try {
                    cityId = Integer.parseInt(array[i]);
                }
                catch (Exception e) {
                    throw new RuntimeException("EfLCache parse line error. id:" + efl.getId());
                }
                if (i != 0 && WorldCityCommon.mainCityNationIdMap.get(cityId) != null) {
                    throw new RuntimeException("EfLCache parse line error. line contains Main cityId. eflId:" + efl.getId() + ". cityId:" + cityId);
                }
                if (WorldCityCommon.barbarainCitySet.contains(cityId)) {
                    throw new RuntimeException("EfLCache parse line error. line contains Barbarain Main cityId. eflId:" + efl.getId() + ". cityId:" + cityId);
                }
                if (!citySet.contains(cityId)) {
                    throw new RuntimeException("EfLCache parse line error. line contains invalid cityId. eflId:" + efl.getId() + ". cityId:" + cityId);
                }
                cityIds[i] = cityId;
            }
            efl.setCityIds(cityIds);
            switch (efl.getC()) {
                case 1: {
                    if (efl.getCityIds()[0] != 123) {
                        throw new RuntimeException("EfLCache parse line error. init cityId is not capital. eflId:" + efl.getId());
                    }
                    break;
                }
                case 2: {
                    if (efl.getCityIds()[0] != 19) {
                        throw new RuntimeException("EfLCache parse line error. init cityId is not capital. eflId:" + efl.getId());
                    }
                    break;
                }
                case 3: {
                    if (efl.getCityIds()[0] != 207) {
                        throw new RuntimeException("EfLCache parse line error. init cityId is not capital. eflId:" + efl.getId());
                    }
                    break;
                }
                default: {
                    throw new RuntimeException("EfLCache parse error. invalid c value. eflId:" + efl.getId());
                }
            }
            super.put((Object)efl.getId(), (Object)efl);
        }
    }
    
    public Map<Integer, Map<Integer, List<EfL>>> getForceIdLinesMap() {
        return this.forceIdLinesMap;
    }
}
