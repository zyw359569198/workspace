package com.reign.gcld.scenario.common.action;

import com.reign.gcld.common.*;
import com.reign.gcld.scenario.common.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.juben.common.*;
import com.reign.gcld.sdata.domain.*;

public class ScenarioActionMengqiMove extends ScenarioActionMarching
{
    private int mengdeLocationReserve;
    
    public int getMengdeLocationReserve() {
        return this.mengdeLocationReserve;
    }
    
    public void setMengdeLocationReserve(final int mengdeLocationReserve) {
        this.mengdeLocationReserve = mengdeLocationReserve;
    }
    
    public ScenarioActionMengqiMove(final String[] operationSingle) {
        super(operationSingle);
    }
    
    @Override
    public void doWork(final IDataGetter getter, final int playerId, final ScenarioEvent scenarioEvent) {
        if (this.isDead || this.curCity > this.cities.length - 1) {
            return;
        }
        final long now = System.currentTimeMillis();
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        if (juBenDto == null) {
            return;
        }
        if (this.nextExcutedTime == 0L) {
            this.nextExcutedTime = now + this.time * 1000L;
            this.mengdeLocationReserve = juBenDto.mengdeLocation;
        }
        final int curCityId = this.cities[this.curCity];
        if (curCityId == juBenDto.mengdeLocation || this.curCity == this.cities.length - 1) {
            this.isDead = true;
            scenarioEvent.jubenFail(getter, playerId, juBenDto);
        }
        final SoloRoad road = getter.getSoloRoadCache().getRoad(juBenDto.juBen_id, curCityId, this.mengdeLocationReserve);
        boolean shouldMove = false;
        if (road != null && this.mengdeLocationReserve != juBenDto.mengdeLocation) {
            shouldMove = true;
        }
        final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
        final int realNpcForceId = JuBenService.changeForceId(juBenDto.player_force_id);
        if (shouldMove || now >= this.nextExcutedTime) {
            final int nextCity = this.curCity + 1;
            final int nextCityId = this.cities[nextCity];
            final JuBenCityDto cityDto = juBenDto.juBenCityDtoMap.get(nextCityId);
            if (cityDto == null) {
                errorSceneLog.error("MENGQI_ACTION:city is null...CITYID:" + nextCityId);
                return;
            }
            if (cityDto.state == 0 && cityDto.forceId == realNpcForceId) {
                ++this.curCity;
                this.changeMarchingInfoNextTime(playerId, this.time);
                this.changeMarchingState(0, playerId);
                this.mengdeLocationReserve = juBenDto.mengdeLocation;
            }
            else if (cityDto.state != 0) {
                if (this.ifPitch[nextCity]) {
                    return;
                }
                ++this.curCity;
                this.pitchEnemyAndChange(playerId, nextCity, nextCityId, getter, juBenDto);
            }
            else {
                if (this.ifPitch[nextCity]) {
                    this.isDead = true;
                    this.changeMarchingState(3, playerId);
                    return;
                }
                ++this.curCity;
                this.pitchEnemyAndChange(playerId, nextCity, nextCityId, getter, juBenDto);
            }
        }
        else {
            final JuBenCityDto cityDto2 = juBenDto.juBenCityDtoMap.get(curCityId);
            if (cityDto2.state == 0) {
                this.changeMarchingState(0, playerId);
            }
            else {
                this.changeMarchingState(1, playerId);
            }
        }
    }
    
    private void pitchEnemyAndChange(final int playerId, final int nextCity, final int nextCityId, final IDataGetter getter, final JuBenDto juBenDto) {
        getter.getJuBenService().addNpcToCity(playerId, nextCityId, this.getAddGeneralList(), this.forceId);
        this.ifPitch[nextCity] = true;
        this.changeMarchingInfoNextTime(playerId, this.time);
        this.mengdeLocationReserve = juBenDto.mengdeLocation;
    }
    
    @Override
    public ScenarioActionMengqiMove clone() throws CloneNotSupportedException {
        return (ScenarioActionMengqiMove)super.clone();
    }
}
