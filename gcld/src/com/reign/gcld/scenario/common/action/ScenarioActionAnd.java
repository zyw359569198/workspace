package com.reign.gcld.scenario.common.action;

import java.util.*;
import com.reign.gcld.common.*;
import com.reign.gcld.scenario.common.*;
import com.reign.gcld.battle.common.*;

public class ScenarioActionAnd extends ScenarioAction implements Cloneable
{
    public ArrayList<ScenarioAction> actions;
    
    public ScenarioActionAnd(final String[] operationSingle) {
        this.actions = null;
        int maxExecutedGoalTimes = 0;
        for (final String operation : operationSingle) {
            final ScenarioAction action = ActionFactory.getSingleAction(operation);
            if (action != null) {
                if (this.actions == null) {
                    this.actions = new ArrayList<ScenarioAction>();
                }
                if (action.executeTimesGoal > maxExecutedGoalTimes) {
                    maxExecutedGoalTimes = action.executeTimesGoal;
                }
                this.actions.add(action);
            }
        }
        this.executeTimesGoal = maxExecutedGoalTimes;
    }
    
    public ScenarioActionAnd() {
        this.actions = null;
    }
    
    public ScenarioActionAnd(final List<ScenarioAction> actions2) {
        this.actions = null;
        int maxExecutedGoalTimes = 0;
        for (final ScenarioAction action : actions2) {
            if (action != null) {
                if (this.actions == null) {
                    this.actions = new ArrayList<ScenarioAction>();
                }
                if (action.executeTimesGoal > maxExecutedGoalTimes) {
                    maxExecutedGoalTimes = action.executeTimesGoal;
                }
                this.actions.add(action);
            }
        }
        this.executeTimesGoal = maxExecutedGoalTimes;
        this.excuteTime = System.currentTimeMillis();
    }
    
    @Override
    public void doWork(final IDataGetter getter, final int playerId, final ScenarioEvent scenarioEvent) {
        if (this.actions == null) {
            return;
        }
        ScenarioAction add = null;
        long max = 0L;
        int curExcutedTimes = 0;
        for (final ScenarioAction action : this.actions) {
            if (action != null) {
                if (action instanceof ScenarioActionGeneralAdd && ((ScenarioActionGeneralAdd)action).getCityId() != 0) {
                    final long div = action.repeatCirce;
                    if (div > max) {
                        max = div;
                        add = action;
                        curExcutedTimes = action.excutedTimes;
                    }
                }
                final int excutedTimesBefore = action.excutedTimes;
                action.work(getter, playerId, scenarioEvent);
                final int excutedTimesAfter = action.excutedTimes;
                if (excutedTimesAfter > excutedTimesBefore && action instanceof ScenarioActionRepeatAction) {
                    break;
                }
                continue;
            }
        }
        if (add != null) {
            final int nowExcutedTimes = add.excutedTimes;
            if (nowExcutedTimes > curExcutedTimes) {
                ScenarioEventJsonBuilder.sendGeneralAddInfo(playerId, add, false);
            }
        }
    }
    
    public ScenarioAction getMaxTimeDivisionAddAction() {
        ScenarioAction result = null;
        if (this.actions == null || this.actions.isEmpty()) {
            return null;
        }
        long max = 0L;
        for (final ScenarioAction action : this.actions) {
            if (action instanceof ScenarioActionGeneralAdd && ((ScenarioActionGeneralAdd)action).getCityId() != 0) {
                final long div = action.repeatCirce;
                if (div <= max) {
                    continue;
                }
                max = div;
                result = action;
            }
        }
        return result;
    }
    
    @Override
    public ScenarioActionAnd clone() throws CloneNotSupportedException {
        final ScenarioActionAnd scenarioActionAnd = (ScenarioActionAnd)super.clone();
        scenarioActionAnd.actions = new ArrayList<ScenarioAction>();
        for (int i = 0; i < this.actions.size(); ++i) {
            final ScenarioAction action = this.actions.get(i);
            if (action != null) {
                scenarioActionAnd.actions.add(action.clone());
            }
        }
        return scenarioActionAnd;
    }
    
    public ScenarioAction getStratagemAction() {
        if (this.actions == null || this.actions.isEmpty()) {
            return null;
        }
        for (final ScenarioAction action : this.actions) {
            if (action instanceof ScenarioActionStratagem && ((ScenarioActionStratagem)action).getCityId() == 0) {
                final ScenarioActionStratagem actionStratagem = (ScenarioActionStratagem)action;
                if (actionStratagem.getStratagemId() != Constants.STRATAGEM_HUOGONG_ID) {
                    continue;
                }
                return action;
            }
        }
        return null;
    }
    
    public ScenarioChansingInfo getChasingInfo() {
        if (this.actions == null || this.actions.size() < 2) {
            return null;
        }
        ScenarioChansingInfo chansingInfo = null;
        for (final ScenarioAction action : this.actions) {
            if (action instanceof ScenarioActionChasing) {
                final ScenarioActionChasing temp = (ScenarioActionChasing)action;
                final int camp = temp.getCamp();
                switch (camp) {
                    default: {
                        continue;
                    }
                    case 0: {
                        if (chansingInfo == null) {
                            chansingInfo = new ScenarioChansingInfo();
                        }
                        chansingInfo.setTommyCity(temp.getCurCity());
                        chansingInfo.setTommyCities(temp.getCities());
                        chansingInfo.setTotalTime(temp.getTotalTime());
                        chansingInfo.setTommyPic(temp.pic);
                        continue;
                    }
                    case 1: {
                        if (chansingInfo == null) {
                            chansingInfo = new ScenarioChansingInfo();
                        }
                        chansingInfo.setJenneyCity(temp.getCurCity());
                        chansingInfo.setJenneyBlood(temp.getBlood());
                        chansingInfo.setJennyCities(temp.getCities());
                        chansingInfo.setTotalTime(temp.getTotalTime());
                        chansingInfo.setJennyPic(temp.pic);
                        continue;
                    }
                }
            }
        }
        return chansingInfo;
    }
    
    @Override
    public void retoreChasing(final String chasingInfo, final long restoreTime) {
        for (final ScenarioAction action : this.actions) {
            if (action instanceof ScenarioActionChasing) {
                action.retoreChasing(chasingInfo, restoreTime);
            }
        }
    }
    
    public void ratioTimeForMarchAndRepeat(final float ratio, final int playerId, final ScenarioEvent scenarioEvent, final String picToChange) {
        try {
            for (final ScenarioAction action : this.actions) {
                if (action != null) {
                    if (action instanceof ScenarioActionMarching) {
                        final ScenarioActionMarching cast = (ScenarioActionMarching)action;
                        cast.ratioTime(ratio, playerId, picToChange);
                    }
                    else {
                        if (!(action instanceof ScenarioActionRepeatAction)) {
                            continue;
                        }
                        final ScenarioActionRepeatAction cast2 = (ScenarioActionRepeatAction)action;
                        cast2.ratioTime(ratio, playerId, scenarioEvent, picToChange);
                    }
                }
            }
        }
        catch (Exception e) {
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            errorSceneLog.error(this, e);
        }
    }
    
    public void reducePitchNumber(final int generalId, final int number) {
        try {
            if (this.actions != null && !this.actions.isEmpty()) {
                for (final ScenarioAction action : this.actions) {
                    if (action instanceof ScenarioActionMarching) {
                        final ScenarioActionMarching cast = (ScenarioActionMarching)action;
                        cast.reducePitchNumber(generalId, number);
                    }
                    else {
                        if (!(action instanceof ScenarioActionRepeatAction)) {
                            continue;
                        }
                        final ScenarioActionRepeatAction cast2 = (ScenarioActionRepeatAction)action;
                        cast2.reducePitchNumber(generalId, number);
                    }
                }
            }
        }
        catch (Exception e) {
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            errorSceneLog.error(this, e);
        }
    }
    
    public void changeMarchingPathTemporary(final int source, final int dest, final String pic) {
        for (final ScenarioAction action : this.actions) {
            if (action != null) {
                if (action instanceof ScenarioActionMarching) {
                    final ScenarioActionMarching cast = (ScenarioActionMarching)action;
                    cast.changeMarchingPathTemporary(source, dest, pic);
                }
                else {
                    if (!(action instanceof ScenarioActionRepeatAction)) {
                        continue;
                    }
                    final ScenarioActionRepeatAction cast2 = (ScenarioActionRepeatAction)action;
                    cast2.changeMarchingPathTemporary(source, dest, pic);
                }
            }
        }
    }
}
