package com.reign.gcld.scenario.common.condition;

import com.reign.gcld.common.*;
import com.reign.gcld.scenario.common.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.scenario.message.*;
import com.reign.gcld.juben.common.*;
import java.util.*;

public class ScenarioConditionEventFinish extends ScenarioCondition
{
    private int eventId;
    private int choice;
    
    public int getChoice() {
        return this.choice;
    }
    
    public void setChoice(final int choice) {
        this.choice = choice;
    }
    
    public ScenarioConditionEventFinish(final String[] triggerSingle) {
        this.eventId = Integer.parseInt(triggerSingle[1]);
        this.choice = -1;
        if (triggerSingle.length >= 3) {
            this.choice = Integer.parseInt(triggerSingle[2]);
        }
    }
    
    public int getEventId() {
        return this.eventId;
    }
    
    public void setEventId(final int eventId) {
        this.eventId = eventId;
    }
    
    @Override
    public ScenarioConditionEventFinish clone() throws CloneNotSupportedException {
        return (ScenarioConditionEventFinish)super.clone();
    }
    
    @Override
    protected boolean doCheck(final IDataGetter dataGetter, final int playerId, final ScenarioEvent scenarioEvent, final ScenarioEventMessage scenarioEventMessage) {
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        if (juBenDto == null) {
            return false;
        }
        if (this.isConcernedMessage(scenarioEventMessage)) {
            if (scenarioEventMessage != null) {
                final ScenarioEventMessageEventFinish message = (ScenarioEventMessageEventFinish)scenarioEventMessage;
                final int finishEvent = message.getEventId();
                final int curChoice = message.getChoice();
                final boolean conditionBak = this.conditionFulfil;
                if (juBenDto.grade == 3 && scenarioEvent.getSoloEvent().getId() == 55 && this.eventId == 51) {
                    this.eventId = 76;
                }
                else if (scenarioEvent.getSoloEvent().getId() == 81 && this.eventId == 79) {
                    this.eventId = 80;
                }
                this.conditionFulfil = (finishEvent == this.eventId && (this.choice == -1 || curChoice == this.choice || this.choice == juBenDto.royalJadeBelong));
                if (conditionBak != this.conditionFulfil) {
                    scenarioEvent.eventChangeToSave(dataGetter, playerId);
                }
            }
            return this.conditionFulfil;
        }
        return this.isFinishEvent(playerId);
    }
    
    private boolean isFinishEvent(final int playerId) {
        final JuBenDto dto = JuBenManager.getInstance().getByPid(playerId);
        if (dto == null) {
            return false;
        }
        final List<ScenarioEvent> list = dto.eventList;
        if (list == null) {
            return false;
        }
        for (final ScenarioEvent event : list) {
            if (event == null) {
                continue;
            }
            if (event.getSoloEvent().getId() == this.eventId) {
                return event.getState() == 3;
            }
        }
        return false;
    }
    
    @Override
    public boolean checkRequest(final IDataGetter getter, final int playerId, final ScenarioEvent scenarioEvent) {
        return this.isFinishEvent(playerId);
    }
    
    @Override
    protected boolean isConcernedMessage(final ScenarioEventMessage scenarioEventMessage) {
        final boolean isConcerned = scenarioEventMessage instanceof ScenarioEventMessageEventFinish;
        if (isConcerned) {
            final ScenarioEventMessageEventFinish cast = (ScenarioEventMessageEventFinish)scenarioEventMessage;
            final int messageId = cast.getEventId();
            return messageId == this.eventId;
        }
        return false;
    }
    
    @Override
    public void selfRestore(final String conditionInfo, final int playerId) {
        final boolean isFinish = this.isFinishEvent(playerId);
        this.conditionFulfil = isFinish;
    }
}
