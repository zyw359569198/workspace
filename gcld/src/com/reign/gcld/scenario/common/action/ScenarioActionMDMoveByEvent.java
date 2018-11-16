package com.reign.gcld.scenario.common.action;

import com.reign.gcld.common.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.scenario.common.*;
import com.reign.gcld.juben.common.*;

public class ScenarioActionMDMoveByEvent extends ScenarioAction
{
    private int location;
    private int nextLocation;
    private boolean hasWork;
    
    public ScenarioActionMDMoveByEvent(final String[] operationSingle) {
        this.location = Integer.parseInt(operationSingle[1]);
        this.executeTimesGoal = Integer.MAX_VALUE;
        this.nextLocation = Integer.parseInt(operationSingle[2]);
        this.hasWork = false;
    }
    
    public int getLocation() {
        return this.location;
    }
    
    public void setLocation(final int location) {
        this.location = location;
    }
    
    public int getNextLocation() {
        return this.nextLocation;
    }
    
    public void setNextLocation(final int nextLocation) {
        this.nextLocation = nextLocation;
    }
    
    @Override
    public void doWork(final IDataGetter getter, final int playerId, final ScenarioEvent scenarioEvent) {
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        if (juBenDto == null) {
            return;
        }
        final long triggerTime = scenarioEvent.getTriggerTime();
        final long now = System.currentTimeMillis();
        if (now >= triggerTime + 1000L && !this.hasWork && juBenDto.mengdeLocation == this.location) {
            juBenDto.mengdeLocation = this.nextLocation;
            this.location = this.nextLocation;
            ScenarioEventJsonBuilder.sendMengdeInfo(playerId, this.nextLocation, juBenDto.isMengdeSafe);
            this.hasWork = true;
        }
        final JuBenCityDto city = juBenDto.juBenCityDtoMap.get(juBenDto.mengdeLocation);
        if (city == null) {
            return;
        }
        if (city.forceId != juBenDto.player_force_id) {
            scenarioEvent.jubenFail(getter, playerId, juBenDto);
        }
        final int mengdeLocation = juBenDto.mengdeLocation;
        if (this.location != mengdeLocation) {
            ScenarioEventJsonBuilder.sendMengdeInfo(playerId, this.location = mengdeLocation, juBenDto.isMengdeSafe);
        }
    }
    
    @Override
    public ScenarioActionMDMoveByEvent clone() throws CloneNotSupportedException {
        return (ScenarioActionMDMoveByEvent)super.clone();
    }
}
