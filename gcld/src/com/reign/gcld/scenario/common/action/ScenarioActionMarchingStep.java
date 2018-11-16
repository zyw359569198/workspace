package com.reign.gcld.scenario.common.action;

import com.reign.util.*;
import java.util.*;
import com.reign.gcld.common.*;
import com.reign.gcld.scenario.common.*;

public class ScenarioActionMarchingStep extends ScenarioActionMarching
{
    private Map<Integer, List<Tuple<Integer, Integer>>> cityIdMap;
    
    public ScenarioActionMarchingStep(final String[] operationSingle) {
        this.pic = operationSingle[1];
        this.forceId = Integer.parseInt(operationSingle[2]);
        this.time = Integer.parseInt(operationSingle[3]);
        this.cities = new int[operationSingle.length - 4];
        this.ifPitch = new boolean[operationSingle.length - 4];
        this.cityIdMap = new HashMap<Integer, List<Tuple<Integer, Integer>>>();
        for (int i = 0; i < this.cities.length; ++i) {
            final String cityStr = operationSingle[i + 4];
            final String[] cell = cityStr.split("-");
            this.cities[i] = Integer.parseInt(cell[0]);
            this.ifPitch[i] = false;
            List<Tuple<Integer, Integer>> mmList = this.cityIdMap.get(i);
            for (int j = 1; j < cell.length; ++j) {
                final String cellStr = cell[j];
                final String[] single = cellStr.split(":");
                final int general = Integer.parseInt(single[0]);
                final int generalNum = Integer.parseInt(single[1]);
                if (mmList == null) {
                    mmList = new ArrayList<Tuple<Integer, Integer>>();
                    this.cityIdMap.put(i, mmList);
                }
                mmList.add(new Tuple(general, generalNum));
            }
        }
        this.nextExcutedTime = 0L;
        this.executeTimesGoal = Integer.MAX_VALUE;
        this.marchState = -1;
        this.serial = 0;
        this.hasMarching = true;
        this.isDead = false;
        this.isFirst = true;
    }
    
    @Override
    protected List<Tuple<Integer, Integer>> getAddGeneralList() {
        return this.cityIdMap.get(this.curCity);
    }
    
    @Override
    public ScenarioActionMarchingStep clone() throws CloneNotSupportedException {
        return (ScenarioActionMarchingStep)super.clone();
    }
    
    @Override
    public void doWork(final IDataGetter getter, final int playerId, final ScenarioEvent scenarioEvent) {
        super.doWork(getter, playerId, scenarioEvent);
    }
    
    @Override
    protected void changeCityMap(final int source, final int currentCityId) {
        final List<Tuple<Integer, Integer>> cityMap1 = this.cityIdMap.get(currentCityId);
        final List<Tuple<Integer, Integer>> cityMap2 = this.cityIdMap.get(source);
        if (cityMap2 == null) {
            return;
        }
        if (cityMap1 != null) {
            this.cityIdMap.put(currentCityId, cityMap2);
        }
    }
    
    @Override
    protected void doAfterDead(final IDataGetter getter, final int playerId, final ScenarioEvent scenarioEvent) {
        if (scenarioEvent == null || scenarioEvent.getState() >= 2) {
            return;
        }
        scenarioEvent.setState(2);
    }
}
