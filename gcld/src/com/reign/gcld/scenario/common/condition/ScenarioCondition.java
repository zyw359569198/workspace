package com.reign.gcld.scenario.common.condition;

import com.reign.gcld.scenario.message.*;
import com.reign.gcld.scenario.common.*;
import com.reign.gcld.common.*;
import org.apache.commons.lang.*;

public class ScenarioCondition implements Cloneable
{
    protected boolean conditionFulfil;
    int flag;
    
    public ScenarioCondition() {
        this.conditionFulfil = false;
        this.flag = 0;
        this.conditionFulfil = false;
    }
    
    @Override
	public ScenarioCondition clone() throws CloneNotSupportedException {
        final ScenarioCondition scenarioCondition = (ScenarioCondition)super.clone();
        return scenarioCondition;
    }
    
    public boolean check(final IDataGetter dataGetter, final int playerId, final ScenarioEvent scenarioEvent, final ScenarioEventMessage scenarioEventMessage) {
        if (!this.isConcernedMessage(scenarioEventMessage)) {
            return false;
        }
        if (this.conditionFulfil) {
            return true;
        }
        final boolean result = this.doCheck(dataGetter, playerId, scenarioEvent, scenarioEventMessage);
        if (result && this.flag != 0) {
            final MultiResult flagInfo = scenarioEvent.getFlag(this.flag);
            final String titleString = scenarioEvent.getSoloEvent().getTitle();
            final String deadLineName = scenarioEvent.getSoloEvent().getIntro();
            int deadLine = -1;
            if (scenarioEvent.getState() == 0) {
                deadLine = scenarioEvent.getDeadLine();
            }
            else if (scenarioEvent.getState() == 1) {
                final long trigger = scenarioEvent.getTriggerTime();
                if (trigger == 0L) {
                    deadLine = scenarioEvent.getDeadLine();
                }
                else {
                    deadLine = (int)((trigger - System.currentTimeMillis()) / 1000L + scenarioEvent.getDeadLine());
                }
            }
            ScenarioEventJsonBuilder.sendFlagInfo(playerId, flagInfo, scenarioEvent.getSoloEvent().getId(), this.flag, titleString, deadLine, deadLineName);
        }
        return result;
    }
    
    protected boolean doCheck(final IDataGetter dataGetter, final int playerId, final ScenarioEvent scenarioEvent, final ScenarioEventMessage scenarioEventMessage) {
        return false;
    }
    
    protected boolean isConcernedMessage(final ScenarioEventMessage scenarioEventMessage) {
        return false;
    }
    
    public void setFlag(final int i) {
        this.flag = i;
    }
    
    public boolean isConditionFulfil() {
        return this.conditionFulfil;
    }
    
    public void setConditionFulfil(final boolean conditionFulfil) {
        this.conditionFulfil = conditionFulfil;
    }
    
    public String selfStore() {
        return String.valueOf(this.getConditionType()) + "," + this.conditionFulfil;
    }
    
    protected String getConditionType() {
        return "";
    }
    
    public void selfRestore(final String conditionInfo, final int playerId) {
        if (StringUtils.isBlank(conditionInfo)) {
            return;
        }
        final String[] single = conditionInfo.split(",");
        if (single.length <= 0) {
            return;
        }
        final boolean conditionFulfill = Boolean.parseBoolean(single[1]);
        this.setConditionFulfil(conditionFulfill);
    }
    
    public boolean checkRequest(final IDataGetter getter, final int playerId, final ScenarioEvent scenarioEvent) {
        return this.conditionFulfil;
    }
}
