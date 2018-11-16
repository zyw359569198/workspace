package com.reign.gcld.scenario.common.condition;

import org.apache.commons.lang.*;
import java.util.*;

public class ConditionFactory
{
    private static ConditionFactory factory;
    
    static {
        ConditionFactory.factory = new ConditionFactory();
    }
    
    public static ConditionFactory getInstance() {
        return ConditionFactory.factory;
    }
    
    public ScenarioCondition getContition(final String trigger) {
        if (StringUtils.isBlank(trigger)) {
            return null;
        }
        String[] triggerSingle = trigger.split(";");
        if (triggerSingle.length <= 1) {
            return getSingleCondition(trigger);
        }
        final String startString = triggerSingle[0];
        if (startString.startsWith("or")) {
            triggerSingle = Arrays.copyOfRange(triggerSingle, 1, triggerSingle.length);
            return new ScenarioConditionOR(triggerSingle);
        }
        return new ScenarioConditionAnd(triggerSingle);
    }
    
    public static ScenarioCondition getSingleCondition(final String trigger) {
        if (StringUtils.isBlank(trigger)) {
            return null;
        }
        final String[] triggerSingle = trigger.split(",");
        if (triggerSingle.length < 1) {
            return null;
        }
        final String type = triggerSingle[0];
        if ("general_exist".equalsIgnoreCase(type)) {
            return new ScenarioConditionGeneralExist(triggerSingle);
        }
        if ("peace".equalsIgnoreCase(type)) {
            return new ScenarioConditionCityInPeace(triggerSingle);
        }
        if ("city_hold".equalsIgnoreCase(type)) {
            return new ScenarioConditionCityHold(triggerSingle);
        }
        if ("tujin".equalsIgnoreCase(type)) {
            return new ScenarioConditionTujin(triggerSingle);
        }
        if ("hy".equalsIgnoreCase(type)) {
            return new ScenarioConditionMarige(triggerSingle);
        }
        if ("event_trigger".equalsIgnoreCase(type)) {
            return new ScenarioConditionEventFinish(triggerSingle);
        }
        if ("time_over".equalsIgnoreCase(type)) {
            return new ScenarioConditionTimeOver(triggerSingle);
        }
        if ("city_own".equalsIgnoreCase(type)) {
            return new ScenarioConditionCityOwn(triggerSingle);
        }
        if ("appear".equalsIgnoreCase(type)) {
            return new ScenarioConditionRoadAppear(triggerSingle);
        }
        return null;
    }
}
