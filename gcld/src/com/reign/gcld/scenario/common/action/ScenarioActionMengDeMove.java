package com.reign.gcld.scenario.common.action;

import com.reign.gcld.common.*;
import com.reign.gcld.scenario.common.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.juben.common.*;

public class ScenarioActionMengDeMove extends ScenarioAction
{
    private int dst;
    
    public ScenarioActionMengDeMove(final String[] operationSingle) {
        if (operationSingle.length > 1) {
            this.dst = Integer.parseInt(operationSingle[1]);
        }
    }
    
    public int getDst() {
        return this.dst;
    }
    
    public void setDst(final int dst) {
        this.dst = dst;
    }
    
    @Override
    public void doWork(final IDataGetter getter, final int playerId, final ScenarioEvent scenarioEvent) {
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        if (juBenDto == null) {
            return;
        }
        if (this.dst == 0) {
            final int choice = scenarioEvent.getPlayerChoice();
            if (choice != 0) {
                this.dst = choice;
            }
        }
        juBenDto.mengdeLocation = this.dst;
    }
    
    @Override
    public ScenarioActionMengDeMove clone() throws CloneNotSupportedException {
        return (ScenarioActionMengDeMove)super.clone();
    }
}
