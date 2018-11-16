package com.reign.gcld.scenario.common.action;

import com.reign.gcld.common.util.*;
import com.reign.gcld.common.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.scenario.common.*;
import java.util.*;
import org.apache.commons.lang.*;

public class ScenarioActionRepeatAction extends ScenarioAction
{
    private int time;
    private ScenarioAction actionToAdd;
    private int startCityId;
    private int nextCityId;
    private int serial;
    private long totalTime;
    private int forceId;
    int generalId;
    String pic;
    
    public long getTotalTime() {
        return this.totalTime;
    }
    
    public void setTotalTime(final long totalTime) {
        this.totalTime = totalTime;
    }
    
    public int getSerial() {
        return this.serial;
    }
    
    public void setSerial(final int serial) {
        this.serial = serial;
    }
    
    public int getNextCityId() {
        return this.nextCityId;
    }
    
    public void setNextCityId(final int nextCityId) {
        this.nextCityId = nextCityId;
    }
    
    public int getStartCityId() {
        return this.startCityId;
    }
    
    public void setStartCityId(final int startCityId) {
        this.startCityId = startCityId;
    }
    
    public ScenarioAction getActionToAdd() {
        return this.actionToAdd;
    }
    
    public void setActionToAdd(final ScenarioAction actionToAdd) {
        this.actionToAdd = actionToAdd;
    }
    
    public int getTime() {
        return this.time;
    }
    
    public void setTime(final int time) {
        this.time = time;
    }
    
    public ScenarioActionRepeatAction(final String[] operationSingle) {
        final String operationString = SymbolUtil.toString(operationSingle, ",");
        final String[] singles = operationString.split("#");
        final String[] typeCell = singles[0].split(",");
        this.time = Integer.parseInt(typeCell[1]);
        this.repeatCirce = this.time * 1000L;
        this.executeTimesGoal = 20;
        this.hasMarching = true;
        this.actionToAdd = ActionFactory.getSingleAction(singles[1]);
        if (this.actionToAdd instanceof ScenarioActionMarching) {
            this.forceId = ((ScenarioActionMarching)this.actionToAdd).getForceId();
            this.pic = ((ScenarioActionMarching)this.actionToAdd).getPic();
        }
        this.startCityId = 0;
        this.nextCityId = 0;
        this.startCityId = 105;
        this.nextCityId = ((ScenarioActionMarching)this.actionToAdd).getCities()[0];
    }
    
    @Override
    public void doWork(final IDataGetter getter, final int playerId, final ScenarioEvent scenarioEvent) {
        final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
        final ScenarioAction action = scenarioEvent.getOperation();
        if (action == null) {
            return;
        }
        ++this.excutedTimes;
        this.excuteTime = System.currentTimeMillis();
        ScenarioAction actionToAddCopy = null;
        try {
            actionToAddCopy = this.actionToAdd.clone();
        }
        catch (CloneNotSupportedException e1) {
            errorSceneLog.error(this, e1);
        }
        if (actionToAddCopy == null) {
            return;
        }
        if (action instanceof ScenarioActionAnd) {
            this.totalTime += this.repeatCirce;
            final ScenarioActionAnd and = (ScenarioActionAnd)action;
            if (actionToAddCopy instanceof ScenarioActionMarching) {
                this.serial = and.actions.size();
                ((ScenarioActionMarching)actionToAddCopy).init(this.serial);
            }
            final List<ScenarioAction> newActions = new ArrayList<ScenarioAction>();
            for (final ScenarioAction single : and.actions) {
                try {
                    newActions.add(single.clone());
                }
                catch (CloneNotSupportedException e2) {
                    errorSceneLog.error(this, e2);
                }
            }
            newActions.add(actionToAddCopy);
            final ScenarioActionAnd newAnd = new ScenarioActionAnd(newActions);
            newAnd.hasMarching = true;
            scenarioEvent.setOperation(newAnd);
        }
        else {
            final List<ScenarioAction> actions = new ArrayList<ScenarioAction>();
            actions.add(action);
            if (actionToAddCopy instanceof ScenarioActionMarching) {
                this.serial = actions.size();
                ((ScenarioActionMarching)actionToAddCopy).init(this.serial);
            }
            actions.add(actionToAddCopy);
            final ScenarioActionAnd actionAnd = new ScenarioActionAnd(actions);
            actionAnd.hasMarching = true;
            scenarioEvent.setOperation(actionAnd);
        }
        ScenarioEventJsonBuilder.sendMarchingInfo(this.serial + 1, 0, this.repeatCirce, this.startCityId, playerId, this.nextCityId, this.forceId, this.pic);
    }
    
    @Override
    public ScenarioActionRepeatAction clone() throws CloneNotSupportedException {
        final ScenarioActionRepeatAction action = (ScenarioActionRepeatAction)super.clone();
        action.actionToAdd = this.actionToAdd.clone();
        return action;
    }
    
    public void ratioTime(final float ratio, final int playerId, final ScenarioEvent scenarioEvent, final String picToChange) {
        try {
            if (picToChange == null || (!StringUtils.isBlank(picToChange) && this.pic.equalsIgnoreCase(picToChange))) {
                final long triggerTime = scenarioEvent.getTriggerTime();
                final long lastTime = System.currentTimeMillis() - triggerTime;
                this.repeatCirce = (long)(this.time * 1000L * ratio);
                this.time *= (int)ratio;
                final long nextExcutedTime = this.repeatCirce - lastTime + this.totalTime;
                ScenarioEventJsonBuilder.sendMarchingInfo(this.serial + 1, 0, nextExcutedTime, this.startCityId, playerId, this.nextCityId, this.forceId, this.pic);
            }
        }
        catch (Exception e) {
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            errorSceneLog.error(this, e);
        }
    }
    
    public void reducePitchNumber(final int generalId, final int number) {
        if (this.actionToAdd instanceof ScenarioActionMarching) {
            final ScenarioActionMarching cast = (ScenarioActionMarching)this.actionToAdd;
            cast.reducePitchNumber(generalId, number);
        }
    }
    
    public void changeMarchingPathTemporary(final int source, final int dest, final String pic2) {
        final boolean b = this.actionToAdd instanceof ScenarioActionMarching;
    }
}
