package com.reign.gcld.scenario.common.condition;

import com.reign.gcld.common.*;
import com.reign.gcld.scenario.common.*;
import com.reign.gcld.scenario.message.*;
import org.apache.commons.lang.*;

public class ScenarioConditionMarige extends ScenarioCondition implements Cloneable
{
    private int marigeNow;
    private int marigeTotal;
    
    public int getMarigeNow() {
        return this.marigeNow;
    }
    
    public void setMarigeNow(final int marigeNow) {
        this.marigeNow = marigeNow;
    }
    
    public int getMarigeTotal() {
        return this.marigeTotal;
    }
    
    public void setMarigeTotal(final int marigeTotal) {
        this.marigeTotal = marigeTotal;
    }
    
    public ScenarioConditionMarige(final String[] triggerSingle) {
        this.marigeNow = 0;
        this.marigeTotal = 0;
        this.marigeTotal = Integer.parseInt(triggerSingle[1]);
    }
    
    @Override
    protected boolean doCheck(final IDataGetter dataGetter, final int playerId, final ScenarioEvent scenarioEvent, final ScenarioEventMessage scenarioEventMessage) {
        if (scenarioEventMessage != null && this.isConcernedMessage(scenarioEventMessage)) {
            ++this.marigeNow;
            this.conditionFulfil = (this.marigeNow >= this.marigeTotal);
            scenarioEvent.eventChangeToSave(dataGetter, playerId);
        }
        return this.conditionFulfil;
    }
    
    @Override
    protected boolean isConcernedMessage(final ScenarioEventMessage scenarioEventMessage) {
        return scenarioEventMessage instanceof ScenarioEventMessageMirage;
    }
    
    @Override
    public ScenarioConditionMarige clone() throws CloneNotSupportedException {
        return (ScenarioConditionMarige)super.clone();
    }
    
    @Override
    public String selfStore() {
        if (this.conditionFulfil) {
            return String.valueOf(this.getConditionType()) + "," + this.conditionFulfil;
        }
        return String.valueOf(this.getConditionType()) + "," + this.conditionFulfil + "," + this.marigeNow;
    }
    
    @Override
    protected String getConditionType() {
        return "hy";
    }
    
    @Override
    public void selfRestore(final String conditionInfo, final int playerId) {
        if (StringUtils.isBlank(conditionInfo)) {
            return;
        }
        final String[] single = conditionInfo.split(",");
        if (single.length <= 0) {
            return;
        }
        final boolean state = Boolean.parseBoolean(single[1]);
        if (state) {
            this.setConditionFulfil(state);
        }
        else {
            final int now = Integer.parseInt(single[2]);
            this.setMarigeNow(now);
        }
    }
}
