package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.util.*;
import java.util.*;
import com.reign.gcld.battle.common.*;

@Component("WdSjInCache")
public class WdSjInCache extends AbstractCache<Integer, WdSjIn>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<Integer, Map<Integer, ArrayList<WdSjIn>>> groupCountryMap;
    private Map<Integer, Tuple<Integer, Integer>> groupTimeWindowMap;
    private Map<Integer, ArrayList<Integer>> countryDistanceMap;
    
    public WdSjInCache() {
        this.groupCountryMap = new HashMap<Integer, Map<Integer, ArrayList<WdSjIn>>>();
        this.groupTimeWindowMap = new HashMap<Integer, Tuple<Integer, Integer>>();
        this.countryDistanceMap = new HashMap<Integer, ArrayList<Integer>>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<WdSjIn> resultList = this.dataLoader.getModels((Class)WdSjIn.class);
        for (final WdSjIn wdSjIn : resultList) {
            Map<Integer, ArrayList<WdSjIn>> countryMap = this.groupCountryMap.get(wdSjIn.getG());
            if (countryMap == null) {
                countryMap = new HashMap<Integer, ArrayList<WdSjIn>>();
                this.groupCountryMap.put(wdSjIn.getG(), countryMap);
            }
            ArrayList<WdSjIn> list = countryMap.get(wdSjIn.getC());
            if (list == null) {
                list = new ArrayList<WdSjIn>();
                countryMap.put(wdSjIn.getC(), list);
            }
            list.add(wdSjIn);
            if (wdSjIn.getInMax() <= wdSjIn.getInMin()) {
                throw new RuntimeException("WdSjInCache init fail in time Window, id:" + wdSjIn.getId());
            }
            Tuple<Integer, Integer> timeWindow = this.groupTimeWindowMap.get(wdSjIn.getG());
            if (timeWindow == null) {
                timeWindow = new Tuple();
                timeWindow.left = wdSjIn.getInMin();
                timeWindow.right = wdSjIn.getInMax();
                this.groupTimeWindowMap.put(wdSjIn.getG(), timeWindow);
            }
            if (wdSjIn.getC() > 0 && wdSjIn.getD() > 0) {
                final int country = wdSjIn.getC();
                final int distance = wdSjIn.getD();
                ArrayList<Integer> disList = this.countryDistanceMap.get(country);
                if (disList == null) {
                    disList = new ArrayList<Integer>();
                    this.countryDistanceMap.put(country, disList);
                }
                disList.add(distance);
            }
            super.put((Object)wdSjIn.getId(), (Object)wdSjIn);
        }
    }
    
    public Map<Integer, Map<Integer, ArrayList<WdSjIn>>> getGroupCountryMap() {
        return this.groupCountryMap;
    }
    
    public Tuple<Integer, Integer> getTimeWindowByGroup(final int group) {
        final Tuple<Integer, Integer> timeWindow = this.groupTimeWindowMap.get(group);
        if (timeWindow == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("timeWindow is null").append("group", group).appendMethodName("getTimeWindowByGroup").flush();
            return null;
        }
        return timeWindow;
    }
    
    public Map<Integer, ArrayList<Integer>> getCountryDistanceMap() {
        return this.countryDistanceMap;
    }
    
    public Map<Integer, ArrayList<WdSjIn>> getWdSjInMapByGroup(final int group) {
        final Map<Integer, ArrayList<WdSjIn>> countryMap = this.groupCountryMap.get(group);
        if (countryMap == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("countryMap is null").append("group", group).appendMethodName("getWdSjInByGroupCountry").flush();
            return null;
        }
        return countryMap;
    }
    
    @Override
	public void clear() {
        super.clear();
        this.groupCountryMap.clear();
        this.groupTimeWindowMap.clear();
        this.countryDistanceMap.clear();
    }
}
