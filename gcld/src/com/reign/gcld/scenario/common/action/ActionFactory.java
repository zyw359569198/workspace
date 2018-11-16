package com.reign.gcld.scenario.common.action;

import org.apache.commons.lang.*;

public class ActionFactory
{
    private static ActionFactory instance;
    
    static {
        ActionFactory.instance = new ActionFactory();
    }
    
    public static ActionFactory getInstance() {
        return ActionFactory.instance;
    }
    
    public ScenarioAction getAction(final String operation) {
        if (StringUtils.isBlank(operation)) {
            return null;
        }
        final String[] operationSingle = operation.split(";");
        if (operationSingle.length < 1) {
            return null;
        }
        if (operationSingle.length > 1) {
            return new ScenarioActionAnd(operationSingle);
        }
        return getSingleAction(operation);
    }
    
    public static ScenarioAction getSingleAction(final String single) {
        if (StringUtils.isBlank(single)) {
            return null;
        }
        final String[] operationSingle = single.split(",");
        if (operationSingle.length < 1) {
            return null;
        }
        final String type = operationSingle[0];
        if ("time".equalsIgnoreCase(type)) {
            return new ScenarioActionTimeChange(operationSingle);
        }
        if ("scenario_finish".equalsIgnoreCase(type)) {
            return new ScenarioActionScenarioOver(operationSingle);
        }
        if ("general_add".equalsIgnoreCase(type)) {
            return new ScenarioActionGeneralAdd(operationSingle);
        }
        if ("kill_all".equalsIgnoreCase(type)) {
            return new ScenarioActionKillAll(operationSingle);
        }
        if ("stratagem".equalsIgnoreCase(type)) {
            return new ScenarioActionStratagem(operationSingle);
        }
        if ("buff".equalsIgnoreCase(type)) {
            return new ScenarioActionBuffAdd(operationSingle);
        }
        if ("flag".equalsIgnoreCase(type)) {
            return new ScenarioActionFlagShow(operationSingle);
        }
        if ("dialog".equalsIgnoreCase(type)) {
            return new ScenarioActionDialogShow(operationSingle);
        }
        if ("general_remove".equalsIgnoreCase(type)) {
            return new ScenarioActionGeneralRemove(operationSingle);
        }
        if ("add_action".equalsIgnoreCase(type)) {
            return new ScenarioActionAddAction(operationSingle);
        }
        if ("niubi".equalsIgnoreCase(type)) {
            return new ScenarioActionNiuBi(operationSingle);
        }
        if ("chase".equalsIgnoreCase(type)) {
            return new ScenarioActionChasing(operationSingle);
        }
        if ("moving".equalsIgnoreCase(type)) {
            return new ScenarioActionMarching(operationSingle);
        }
        if ("force_remove".equalsIgnoreCase(type)) {
            return new ScenarioActionRepeatAction(operationSingle);
        }
        if ("appear".equalsIgnoreCase(type)) {
            return new ScenarioActionRoadLinked(operationSingle);
        }
        if ("time_ratio".equalsIgnoreCase(type)) {
            return new ScenarioActionTimeRatio(operationSingle);
        }
        if ("mengde_move".equalsIgnoreCase(type)) {
            return new ScenarioActionMengDeMove(operationSingle);
        }
        if ("mengqi_move".equalsIgnoreCase(type)) {
            return new ScenarioActionMengqiMove(operationSingle);
        }
        if ("reduce_num".equalsIgnoreCase(type)) {
            return new ScenarioActionReducePitchNumber(operationSingle);
        }
        if ("chase_m".equalsIgnoreCase(type)) {
            return new ScenarioActionChasingMengde(operationSingle);
        }
        if ("move_event".equalsIgnoreCase(type)) {
            return new ScenarioActionMDMoveByEvent(operationSingle);
        }
        if ("moving_step".equalsIgnoreCase(type)) {
            return new ScenarioActionMarchingStep(operationSingle);
        }
        if ("change_city".equalsIgnoreCase(type)) {
            return new ScenarioActionChangeCityAppearance(operationSingle);
        }
        if ("change_path".equalsIgnoreCase(type)) {
            return new ScenarioActionMarchingMoveAround(operationSingle);
        }
        return null;
    }
}
