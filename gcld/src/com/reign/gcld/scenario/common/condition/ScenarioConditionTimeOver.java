package com.reign.gcld.scenario.common.condition;

import com.reign.gcld.scenario.message.*;
import com.reign.gcld.common.*;
import com.reign.gcld.scenario.common.*;

public class ScenarioConditionTimeOver extends ScenarioCondition
{
    private int time;
    private int operator;
    
    public ScenarioConditionTimeOver(final String[] triggerSingle) {
        this.time = 0;
        this.operator = 0;
        this.time = Integer.parseInt(triggerSingle[1]);
        this.operator = Integer.parseInt(triggerSingle[2]);
    }
    
    public int getOperator() {
        return this.operator;
    }
    
    public void setOperator(final int operator) {
        this.operator = operator;
    }
    
    public int getTime() {
        return this.time;
    }
    
    public void setTime(final int time) {
        this.time = time;
    }
    
    @Override
    protected boolean doCheck(final IDataGetter dataGetter, final int playerId, final ScenarioEvent scenarioEvent, final ScenarioEventMessage scenarioEventMessage) {
        final boolean conditionBak = this.conditionFulfil;
        this.conditionFulfil = this.checkRequest(dataGetter, playerId, scenarioEvent);
        if (conditionBak != this.conditionFulfil && conditionBak && scenarioEvent.getSoloEvent().getId() == Constants.EVENT_ID_ZHENGJIUPANGTONG_71) {
            ScenarioEventJsonBuilder.sendDialog(playerId, 8002, null);
        }
        return this.conditionFulfil;
    }
    
    @Override
    public boolean checkRequest(final IDataGetter getter, final int playerId, final ScenarioEvent scenarioEvent) {
        final long triggerTime = scenarioEvent.getTriggerTime();
        final long now = System.currentTimeMillis();
        final long requireTime = triggerTime + this.time * 1000L;
        if (this.operator == 0) {
            return now >= requireTime;
        }
        return now < requireTime;
    }
    
    @Override
    public ScenarioConditionTimeOver clone() throws CloneNotSupportedException {
        return (ScenarioConditionTimeOver)super.clone();
    }
    
    @Override
    protected boolean isConcernedMessage(final ScenarioEventMessage scenarioEventMessage) {
        return true;
    }
}
