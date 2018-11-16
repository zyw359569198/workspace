package com.reign.gcld.scenario.common.condition;

import com.reign.gcld.common.*;
import com.reign.gcld.scenario.common.*;
import com.reign.gcld.scenario.message.*;
import java.util.*;
import org.apache.commons.lang.*;
import com.reign.gcld.common.util.*;

public class ScenarioConditionOR extends ScenarioCondition implements Cloneable
{
    private ArrayList<ScenarioCondition> conditions;
    
    public ScenarioConditionOR(final String[] triggerSingle) {
        this.conditions = null;
        for (final String triggerString : triggerSingle) {
            final ScenarioCondition condition = ConditionFactory.getSingleCondition(triggerString);
            if (condition != null) {
                if (this.conditions == null) {
                    this.conditions = new ArrayList<ScenarioCondition>();
                }
                this.conditions.add(condition);
            }
        }
    }
    
    @Override
    public ScenarioCondition clone() throws CloneNotSupportedException {
        final ScenarioConditionOR scenarioConditionOr = (ScenarioConditionOR)super.clone();
        scenarioConditionOr.conditions = new ArrayList<ScenarioCondition>();
        for (int i = 0; i < this.conditions.size(); ++i) {
            final ScenarioCondition action = this.conditions.get(i);
            if (action != null) {
                scenarioConditionOr.conditions.add(action.clone());
            }
        }
        return scenarioConditionOr;
    }
    
    @Override
    protected boolean doCheck(final IDataGetter dataGetter, final int playerId, final ScenarioEvent scenarioEvent, final ScenarioEventMessage scenarioEventMessage) {
        boolean result = false;
        for (final ScenarioCondition condition : this.conditions) {
            final boolean check = condition.doCheck(dataGetter, playerId, scenarioEvent, scenarioEventMessage);
            if (check) {
                result = true;
                break;
            }
        }
        return this.conditionFulfil = result;
    }
    
    @Override
    protected boolean isConcernedMessage(final ScenarioEventMessage scenarioEventMessage) {
        if (this.conditions == null) {
            return true;
        }
        for (final ScenarioCondition condition : this.conditions) {
            if (condition.isConcernedMessage(scenarioEventMessage)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String selfStore() {
        if (this.conditionFulfil) {
            return new StringBuilder(String.valueOf(this.conditionFulfil)).toString();
        }
        final StringBuffer sb = new StringBuffer();
        for (final ScenarioCondition condition : this.conditions) {
            final String single = condition.selfStore();
            if (!StringUtils.isBlank(single)) {
                sb.append(single).append(";");
            }
        }
        SymbolUtil.removeTheLast(sb);
        return String.valueOf(this.conditionFulfil) + ";" + sb.toString();
    }
    
    @Override
    public void selfRestore(final String conditionInfo, final int playerId) {
        if (StringUtils.isBlank(conditionInfo)) {
            return;
        }
        final String[] single = conditionInfo.split(";");
        final boolean fulfill = Boolean.parseBoolean(single[0]);
        if (fulfill) {
            this.setConditionFulfil(fulfill);
        }
        else {
            for (int size = Math.min(single.length - 1, this.conditions.size()), i = 0; i < size; ++i) {
                final String cell = single[i + 1];
                if (!StringUtils.isBlank(cell)) {
                    final ScenarioCondition condition = this.conditions.get(i);
                    condition.selfRestore(cell, playerId);
                }
            }
        }
    }
    
    @Override
    public boolean checkRequest(final IDataGetter getter, final int playerId, final ScenarioEvent scenarioEvent) {
        boolean result = false;
        for (final ScenarioCondition condition : this.conditions) {
            final boolean check = condition.checkRequest(getter, playerId, scenarioEvent);
            if (check) {
                result = true;
                break;
            }
        }
        return this.conditionFulfil = result;
    }
}
