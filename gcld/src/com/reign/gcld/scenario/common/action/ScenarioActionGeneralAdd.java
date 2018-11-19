package com.reign.gcld.scenario.common.action;

import com.reign.gcld.common.*;
import com.reign.gcld.juben.service.*;
import com.reign.util.*;
import com.reign.gcld.scenario.common.*;
import com.reign.gcld.juben.common.*;
import java.util.*;

public class ScenarioActionGeneralAdd extends ScenarioAction implements Cloneable
{
    private int generalId;
    private int cityId;
    private int number;
    private int camp;
    
    public int getCamp() {
        return this.camp;
    }
    
    public void setCamp(final int camp) {
        this.camp = camp;
    }
    
    public int getGeneralId() {
        return this.generalId;
    }
    
    public void setGeneralId(final int generalId) {
        this.generalId = generalId;
    }
    
    public int getCityId() {
        return this.cityId;
    }
    
    public void setCityId(final int cityId) {
        this.cityId = cityId;
    }
    
    public int getNumber() {
        return this.number;
    }
    
    public void setNumber(final int number) {
        this.number = number;
    }
    
    public ScenarioActionGeneralAdd(final String[] operationSingle) {
        this.generalId = 0;
        this.cityId = 0;
        this.number = 0;
        this.camp = 0;
        final int cityId = Integer.parseInt(operationSingle[1]);
        final int generalId = Integer.parseInt(operationSingle[2]);
        final int number = Integer.parseInt(operationSingle[3]);
        final int time = Integer.parseInt(operationSingle[4]);
        final int camp = Integer.parseInt(operationSingle[5]);
        this.executeTimesGoal = ((time == 0) ? 1 : Integer.MAX_VALUE);
        this.repeatCirce = Integer.parseInt(operationSingle[4]) * 1000L;
        this.generalId = generalId;
        this.cityId = cityId;
        this.number = number;
        this.camp = camp;
    }
    
    @Override
    public void doWork(final IDataGetter getter, final int playerId, final ScenarioEvent scenarioEvent) {
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        if (juBenDto == null) {
            return;
        }
        final int capital = getter.getSoloCityCache().getCapitalIdByJubenId(juBenDto.juBen_id);
        JuBenCityDto dto = juBenDto.juBenCityDtoMap.get(this.cityId);
        if (dto == null) {
            for (final Integer targetCityId : juBenDto.juBenCityDtoMap.keySet()) {
                dto = juBenDto.juBenCityDtoMap.get(targetCityId);
                if (dto == null) {
                    continue;
                }
                if (capital == targetCityId) {
                    continue;
                }
                if (this.cityId == 0 && dto.forceId == 0) {
                    continue;
                }
                if (this.cityId == 1 && dto.forceId != 0) {
                    continue;
                }
                final List<Tuple<Integer, Integer>> generalList = new ArrayList<Tuple<Integer, Integer>>();
                final Tuple<Integer, Integer> general = new Tuple(this.generalId, this.number);
                generalList.add(general);
                getter.getJuBenService().addNpcToCity(playerId, targetCityId, generalList, this.camp);
            }
        }
        else {
            final List<Tuple<Integer, Integer>> generalList2 = new ArrayList<Tuple<Integer, Integer>>();
            final Tuple<Integer, Integer> general2 = new Tuple(this.generalId, this.number);
            generalList2.add(general2);
            getter.getJuBenService().addNpcToCity(playerId, this.cityId, generalList2, this.camp);
            if (scenarioEvent.getOperation() instanceof ScenarioActionGeneralAdd) {
                ScenarioEventJsonBuilder.sendGeneralAddInfo(playerId, this, false);
            }
        }
    }
    
    @Override
    public ScenarioActionGeneralAdd clone() throws CloneNotSupportedException {
        final ScenarioActionGeneralAdd action = (ScenarioActionGeneralAdd)super.clone();
        return action;
    }
}