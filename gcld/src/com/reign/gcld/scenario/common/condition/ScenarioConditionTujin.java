package com.reign.gcld.scenario.common.condition;

import com.reign.gcld.common.*;
import com.reign.gcld.scenario.common.*;
import com.reign.gcld.scenario.message.*;
import org.apache.commons.lang.*;

public class ScenarioConditionTujin extends ScenarioCondition implements Cloneable
{
    private int tujinTotal;
    private int tujinNow;
    
    public int getTujinNow() {
        return this.tujinNow;
    }
    
    public void setTujinNow(final int tujinNow) {
        this.tujinNow = tujinNow;
    }
    
    public int getTujinTotal() {
        return this.tujinTotal;
    }
    
    public void setTujinTotal(final int tujinTotal) {
        this.tujinTotal = tujinTotal;
    }
    
    public ScenarioConditionTujin(final String[] triggerSingle) {
        this.tujinTotal = 0;
        this.tujinNow = 0;
        this.tujinTotal = Integer.parseInt(triggerSingle[1]);
    }
    
    @Override
    protected boolean doCheck(final IDataGetter dataGetter, final int playerId, final ScenarioEvent scenarioEvent, final ScenarioEventMessage scenarioEventMessage) {
        if (scenarioEventMessage != null && this.isConcernedMessage(scenarioEventMessage)) {
            ++this.tujinNow;
            this.conditionFulfil = (this.tujinNow >= this.tujinTotal);
            scenarioEvent.eventChangeToSave(dataGetter, playerId);
        }
        return this.conditionFulfil;
    }
    
    @Override
    protected boolean isConcernedMessage(final ScenarioEventMessage scenarioEventMessage) {
        return scenarioEventMessage instanceof ScenarioEventMessageTujin;
    }
    
    @Override
    public ScenarioConditionTujin clone() throws CloneNotSupportedException {
        return (ScenarioConditionTujin)super.clone();
    }
    
    @Override
    public String selfStore() {
        if (this.conditionFulfil) {
            return String.valueOf(this.getConditionType()) + "," + this.conditionFulfil;
        }
        return String.valueOf(this.getConditionType()) + "," + this.conditionFulfil + "," + this.tujinNow;
    }
    
    @Override
    protected String getConditionType() {
        return "tujin";
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
            this.setTujinNow(now);
        }
    }
}
