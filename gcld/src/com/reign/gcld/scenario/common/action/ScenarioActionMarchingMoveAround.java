package com.reign.gcld.scenario.common.action;

import com.reign.gcld.common.*;
import com.reign.gcld.scenario.common.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.juben.common.*;

public class ScenarioActionMarchingMoveAround extends ScenarioAction
{
    private int source;
    private int dest;
    private String pic;
    
    public ScenarioActionMarchingMoveAround(final String[] operationSingle) {
        this.source = Integer.parseInt(operationSingle[1]);
        this.dest = Integer.parseInt(operationSingle[2]);
        if (operationSingle.length >= 4) {
            this.pic = operationSingle[3];
        }
    }
    
    public int getSource() {
        return this.source;
    }
    
    public void setSource(final int source) {
        this.source = source;
    }
    
    public int getDest() {
        return this.dest;
    }
    
    public void setDest(final int dest) {
        this.dest = dest;
    }
    
    public String getPic() {
        return this.pic;
    }
    
    public void setPic(final String pic) {
        this.pic = pic;
    }
    
    @Override
    public void doWork(final IDataGetter getter, final int playerId, final ScenarioEvent scenarioEvent) {
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        if (juBenDto == null) {
            return;
        }
        for (final ScenarioEvent event : juBenDto.eventList) {
            final ScenarioAction action = event.getOperation();
            if (action instanceof ScenarioActionMarching) {
                final ScenarioActionMarching cast = (ScenarioActionMarching)action;
                cast.changeMarchingPathTemporary(this.source, this.dest, this.pic);
            }
            else if (action instanceof ScenarioActionAnd) {
                final ScenarioActionAnd cast2 = (ScenarioActionAnd)action;
                cast2.changeMarchingPathTemporary(this.source, this.dest, this.pic);
            }
            else {
                if (!(action instanceof ScenarioActionRepeatAction)) {
                    continue;
                }
                final ScenarioActionRepeatAction cast3 = (ScenarioActionRepeatAction)action;
                cast3.changeMarchingPathTemporary(this.source, this.dest, this.pic);
            }
        }
    }
    
    @Override
    public ScenarioActionMarchingMoveAround clone() throws CloneNotSupportedException {
        return (ScenarioActionMarchingMoveAround)super.clone();
    }
}
