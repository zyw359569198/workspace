package com.reign.gcld.scenario.common.action;

import com.reign.gcld.common.util.*;
import com.reign.gcld.common.*;
import com.reign.gcld.scenario.common.*;
import com.reign.gcld.juben.service.*;
import java.util.*;
import com.reign.gcld.juben.common.*;
import com.reign.gcld.battle.common.*;

public class ScenarioActionAddAction extends ScenarioAction implements Cloneable
{
    private List<ScenarioAction> actionToAdd;
    private int eventId;
    private int operationIndex;
    
    public List<ScenarioAction> getActionToAdd() {
        return this.actionToAdd;
    }
    
    public void setActionToAdd(final List<ScenarioAction> actionToAdd) {
        this.actionToAdd = actionToAdd;
    }
    
    public int getEventId() {
        return this.eventId;
    }
    
    public void setEventId(final int eventId) {
        this.eventId = eventId;
    }
    
    public int getOperationIndex() {
        return this.operationIndex;
    }
    
    public void setOperationIndex(final int operationIndex) {
        this.operationIndex = operationIndex;
    }
    
    public ScenarioActionAddAction(final String[] operationSingle) {
        try {
            final String operationString = SymbolUtil.toString(operationSingle, ",");
            final String[] singles = operationString.split("#");
            final String[] typeCell = singles[0].split(",");
            this.eventId = Integer.parseInt(typeCell[1]);
            this.operationIndex = Integer.parseInt(typeCell[2]);
            ScenarioAction action = null;
            this.actionToAdd = new ArrayList<ScenarioAction>();
            for (int i = 1; i < singles.length; ++i) {
                action = ActionFactory.getSingleAction(singles[i]);
                this.actionToAdd.add(action);
            }
        }
        catch (Exception e) {
            throw new RuntimeException("initial addAction fail...message:" + e.getMessage() + "traceStack:" + e.getStackTrace());
        }
    }
    
    @Override
    public ScenarioActionAddAction clone() throws CloneNotSupportedException {
        final ScenarioActionAddAction action = (ScenarioActionAddAction)super.clone();
        action.actionToAdd = new ArrayList<ScenarioAction>();
        for (final ScenarioAction cellAction : this.actionToAdd) {
            if (cellAction != null) {
                action.actionToAdd.add(cellAction.clone());
            }
        }
        return action;
    }
    
    @Override
    public void doWork(final IDataGetter getter, final int playerId, final ScenarioEvent scenarioEvent) {
        final JuBenDto dto = JuBenManager.getInstance().getByPid(playerId);
        if (dto == null) {
            return;
        }
        if (this.actionToAdd == null || this.actionToAdd.isEmpty()) {
            return;
        }
        for (final ScenarioEvent event : dto.eventList) {
            if (this.eventId == event.getSoloEvent().getId()) {
                final ScenarioAction action = (this.operationIndex == 0) ? event.getOperation() : ((this.operationIndex == 1) ? event.getDealOperation1() : event.getDealOperation2());
                final int toAddSize = this.actionToAdd.size();
                final int oldSize = (action != null) ? 1 : 0;
                if (toAddSize + oldSize >= 2) {
                    final List<ScenarioAction> actions = this.getActionToAddCloneInstance(this.actionToAdd);
                    if (action instanceof ScenarioActionAnd) {
                        if (actions == null || actions.isEmpty()) {
                            return;
                        }
                        ((ScenarioActionAnd)action).actions.addAll(actions);
                        event.setActionByIndex(action, this.operationIndex);
                    }
                    else {
                        if (action != null) {
                            actions.add(action);
                        }
                        final ScenarioActionAnd add = new ScenarioActionAnd(actions);
                        event.setActionByIndex(add, this.operationIndex);
                    }
                }
                else {
                    event.setActionByIndex(this.actionToAdd.get(0), this.operationIndex);
                }
            }
        }
    }
    
    private List<ScenarioAction> getActionToAddCloneInstance(final List<ScenarioAction> actions) {
        final List<ScenarioAction> list = new ArrayList<ScenarioAction>();
        try {
            for (final ScenarioAction action : actions) {
                if (action != null) {
                    list.add(action.clone());
                }
            }
        }
        catch (Exception e) {
            final ErrorSceneLog errorlog = ErrorSceneLog.getInstance();
            errorlog.error("getActionToAddCloneInstance fail....");
            errorlog.error(e.getMessage());
            errorlog.error(this, e);
        }
        return list;
    }
}
