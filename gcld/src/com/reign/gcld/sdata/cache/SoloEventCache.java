package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.scenario.common.choice.*;
import org.apache.commons.lang.*;
import java.util.*;
import com.reign.gcld.scenario.common.condition.*;
import com.reign.gcld.scenario.common.action.*;
import com.reign.gcld.scenario.common.*;
import com.reign.gcld.battle.common.*;

@Component("soleEventCache")
public class SoloEventCache extends AbstractCache<Integer, SoloEvent>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<Integer, ScenarioEvent> eventMap;
    
    public SoloEventCache() {
        this.eventMap = new HashMap<Integer, ScenarioEvent>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<SoloEvent> list = this.dataLoader.getModels((Class)SoloEvent.class);
        for (final SoloEvent event : list) {
            super.put((Object)event.getId(), (Object)event);
            final String trigger = event.getTrigger();
            final String operation = event.getOperation();
            final String dealCondition = event.getDealcondition();
            final String choice1 = event.getChoice1();
            final String choise2 = event.getChoice2();
            final String dealOperation1 = event.getDealoperation1();
            final String dealOperation2 = event.getDealoperation2();
            final String flag1 = event.getFlag1();
            final String flag2 = event.getFlag2();
            final String title = event.getTitle();
            final String failCondition = event.getFailcondition();
            final ScenarioCondition triggerCondition = ConditionFactory.getInstance().getContition(trigger);
            if (triggerCondition != null) {
                triggerCondition.setFlag(1);
            }
            final ScenarioAction operationAction = ActionFactory.getInstance().getAction(operation);
            final ScenarioCondition dealConditions = ConditionFactory.getInstance().getContition(dealCondition);
            if (dealConditions != null) {
                dealConditions.setFlag(2);
            }
            ScenarioChansingInfo scenarioChansingInfo = null;
            if (operationAction != null) {
                if (operationAction instanceof ScenarioActionAnd) {
                    scenarioChansingInfo = ((ScenarioActionAnd)operationAction).getChasingInfo();
                }
                else if (operationAction instanceof ScenarioActionChasing) {
                    scenarioChansingInfo = ((ScenarioActionChasing)operationAction).getChasingInfo();
                }
            }
            final ScenarioCondition fail = ConditionFactory.getInstance().getContition(failCondition);
            final ScenarioAction dealOperations1 = ActionFactory.getInstance().getAction(dealOperation1);
            final ScenarioAction dealOperations2 = ActionFactory.getInstance().getAction(dealOperation2);
            final ScenarioChoice choice2 = new ScenarioChoice(choice1, choise2, flag2, title, dealOperation1, dealOperation2);
            final ScenarioEvent scenarioEvent = new ScenarioEvent();
            scenarioEvent.setFlag1(flag1);
            scenarioEvent.setFlag2(flag2);
            scenarioEvent.setTrigger(triggerCondition);
            scenarioEvent.setOperation(operationAction);
            scenarioEvent.setCondition(dealConditions);
            scenarioEvent.setDealOperation1(dealOperations1);
            scenarioEvent.setDealOperation2(dealOperations2);
            scenarioEvent.setSoloEvent(event);
            scenarioEvent.setState(0);
            scenarioEvent.setTriggerTime(0L);
            scenarioEvent.setChoice(choice2);
            scenarioEvent.setScenarioChansingInfo(scenarioChansingInfo);
            scenarioEvent.setFailCondition(fail);
            final int deadLine = getDeadLineCityAndTime(dealCondition);
            if (deadLine > 0) {
                scenarioEvent.setDeadLine(deadLine);
            }
            else {
                scenarioEvent.setDeadLine(-1);
            }
            if (!StringUtils.isBlank(choice1) || !StringUtils.isBlank(choise2)) {
                final int mainChoice = (dealOperation1 == null) ? 2 : 1;
                scenarioEvent.setMainChoice(mainChoice);
            }
            this.eventMap.put(event.getId(), scenarioEvent);
        }
    }
    
    public static int getDeadLineCityAndTime(final String failcondition) {
        try {
            if (StringUtils.isBlank(failcondition)) {
                return -1;
            }
            String result = null;
            final String[] cell = failcondition.split(";");
            for (int i = 0; i < cell.length; ++i) {
                if (cell[i].contains("time_over")) {
                    result = cell[i];
                }
            }
            if (StringUtils.isBlank(result)) {
                return -1;
            }
            final String[] single1 = result.split(",");
            final int time = Integer.parseInt(single1[1]);
            return time;
        }
        catch (Exception e) {
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            errorSceneLog.error("getDeadLineCityAndTime error..", e);
            return -1;
        }
    }
    
    public Map<Integer, ScenarioEvent> getEventMap() {
        return this.eventMap;
    }
    
    @Override
	public void clear() {
        super.clear();
        this.eventMap.clear();
    }
}
