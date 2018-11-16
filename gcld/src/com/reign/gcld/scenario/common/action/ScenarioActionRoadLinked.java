package com.reign.gcld.scenario.common.action;

import com.reign.gcld.scenario.common.*;
import com.reign.gcld.juben.service.*;
import com.reign.framework.json.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.common.*;
import com.reign.gcld.juben.common.*;

public class ScenarioActionRoadLinked extends ScenarioAction
{
    private int roadId;
    private int isLinked;
    
    public int getIsLinked() {
        return this.isLinked;
    }
    
    public void setIsLinked(final int isLinked) {
        this.isLinked = isLinked;
    }
    
    public ScenarioActionRoadLinked(final String[] operationSingle) {
        this.roadId = Integer.parseInt(operationSingle[1]);
        this.isLinked = Integer.parseInt(operationSingle[2]);
    }
    
    @Override
    public void doWork(final IDataGetter getter, final int playerId, final ScenarioEvent scenarioEvent) {
        final JuBenDto dto = JuBenManager.getInstance().getByPid(playerId);
        if (dto == null) {
            return;
        }
        boolean result = false;
        if (this.isLinked == 0) {
            result = dto.roadLinked.remove(this.roadId);
        }
        else {
            result = dto.roadLinked.add(this.roadId);
        }
        if (result) {
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("roadId", this.roadId);
            final SoloRoad road = (SoloRoad)getter.getSoloRoadCache().get((Object)this.roadId);
            if (road != null) {
                doc.createElement("start", road.getStart());
                doc.createElement("end", road.getEnd());
            }
            doc.createElement("isLinked", this.isLinked);
            doc.endObject();
            Players.push(playerId, PushCommand.PUSH_JUBEN_ROAD_LINKED, doc.toByte());
        }
    }
    
    @Override
    public ScenarioActionRoadLinked clone() throws CloneNotSupportedException {
        return (ScenarioActionRoadLinked)super.clone();
    }
    
    public int getRoadId() {
        return this.roadId;
    }
    
    public void setRoadId(final int roadId) {
        this.roadId = roadId;
    }
}
