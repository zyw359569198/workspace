package com.reign.gcld.scenario.common.condition;

import com.reign.gcld.scenario.common.*;
import com.reign.gcld.scenario.message.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.common.*;
import com.reign.gcld.juben.common.*;
import java.util.*;
import org.apache.commons.lang.*;
import com.reign.gcld.common.util.*;

public class ScenarioConditionAnd extends ScenarioCondition implements Cloneable
{
    private ArrayList<ScenarioCondition> conditions;
    
    public ScenarioConditionAnd(final String[] triggerSingle) {
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
    
    public ScenarioConditionAnd() {
        this.conditions = null;
    }
    
    @Override
    protected boolean doCheck(final IDataGetter dataGetter, final int playerId, final ScenarioEvent scenarioEvent, final ScenarioEventMessage scenarioEventMessage) {
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        if (juBenDto == null) {
            return false;
        }
        boolean result = true;
        for (final ScenarioCondition condition : this.conditions) {
            final boolean check = condition.doCheck(dataGetter, playerId, scenarioEvent, scenarioEventMessage);
            if (!check) {
                result = false;
            }
        }
        final int soloId = scenarioEvent.getSoloEvent().getId();
        if (soloId == Constants.EVENT_ID_YUXIOVER_166 && scenarioEvent.getState() >= 1) {
            result &= (juBenDto.royalJadeBelong == 0);
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
    public ScenarioConditionAnd clone() throws CloneNotSupportedException {
        final ScenarioConditionAnd scenarioConditionAnd = (ScenarioConditionAnd)super.clone();
        scenarioConditionAnd.conditions = new ArrayList<ScenarioCondition>();
        for (int i = 0; i < this.conditions.size(); ++i) {
            final ScenarioCondition action = this.conditions.get(i);
            if (action != null) {
                scenarioConditionAnd.conditions.add(action.clone());
            }
        }
        return scenarioConditionAnd;
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
        boolean result = true;
        for (final ScenarioCondition condition : this.conditions) {
            final boolean check = condition.checkRequest(getter, playerId, scenarioEvent);
            if (!check) {
                result = false;
            }
        }
        return this.conditionFulfil = result;
    }
}
