package com.reign.gcld.scenario.common.action;

import com.reign.gcld.common.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.juben.common.*;
import com.reign.gcld.scenario.common.*;

public class ScenarioActionChasingMengde extends ScenarioActionChasing
{
    public ScenarioActionChasingMengde(final String[] operationSingle) {
        super(operationSingle);
    }
    
    @Override
    protected void escapeAwayTommy(final IDataGetter getter, final int playerId, final ScenarioEvent scenarioEvent) {
        try {
            if (scenarioEvent.getState() >= 3) {
                return;
            }
            final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
            if (juBenDto == null) {
                return;
            }
            final long now = System.currentTimeMillis();
            final ScenarioChansingInfo chansingInfo = scenarioEvent.getScenarioChansingInfo();
            if (chansingInfo == null || chansingInfo.getState() != 0) {
                return;
            }
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            final int curCityId = this.cities[this.curCity];
            final int mengdeLocation = juBenDto.mengdeLocation;
            if (curCityId != mengdeLocation && this.indexOfMengdeLocation(mengdeLocation) > this.curCity) {
                final int nextCity = this.curCity + 1;
                chansingInfo.setNextJennyTime(this.nextExcutedTime += 1000L * this.time);
                this.changeChasingCity(chansingInfo, 1, nextCity, playerId, scenarioEvent);
                return;
            }
            if (now >= this.nextExcutedTime) {
                final int nextCity = this.curCity + 1;
                if (nextCity >= this.cities.length) {
                    errorSceneLog.error("chasingJenney exception..playerId:" + playerId + " nextCity:" + nextCity);
                    return;
                }
                chansingInfo.setNextJennyTime(this.nextExcutedTime += 1000L * this.time);
                this.changeChasingCity(chansingInfo, 1, nextCity, playerId, scenarioEvent);
                juBenDto.mengdeLocation = this.cities[nextCity];
                scenarioEvent.eventChangeToSave(getter, playerId);
            }
        }
        catch (Exception e) {
            final ErrorSceneLog errorSceneLog2 = ErrorSceneLog.getInstance();
            errorSceneLog2.error("escapeAwayTommy playerId.." + playerId);
            errorSceneLog2.error(e.getMessage());
            errorSceneLog2.error(this, e);
        }
    }
    
    private int indexOfMengdeLocation(final int mengdeLocation) {
        int result = 0;
        for (int i = 0; i < this.cities.length; ++i) {
            if (this.cities[i] == mengdeLocation) {
                result = i;
            }
        }
        return result;
    }
    
    @Override
    public ScenarioActionChasingMengde clone() throws CloneNotSupportedException {
        return (ScenarioActionChasingMengde)super.clone();
    }
}
