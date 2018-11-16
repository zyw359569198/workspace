package com.reign.gcld.scenario.common.action;

import com.reign.gcld.common.*;
import com.reign.gcld.scenario.common.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.juben.common.*;
import org.apache.commons.lang.*;

public class ScenarioActionChasing extends ScenarioAction
{
    protected int camp;
    protected int time;
    protected int totalTime;
    protected int blood;
    protected int[] cities;
    protected int curCity;
    protected long nextExcutedTime;
    protected String pic;
    
    public long getNextExcutedTime() {
        return this.nextExcutedTime;
    }
    
    public void setNextExcutedTime(final long nextExcutedTime) {
        this.nextExcutedTime = nextExcutedTime;
    }
    
    public int getTotalTime() {
        return this.totalTime;
    }
    
    public void setTotalTime(final int totalTime) {
        this.totalTime = totalTime;
    }
    
    public int getCurCity() {
        return this.curCity;
    }
    
    public void setCurCity(final int curCity) {
        this.curCity = curCity;
    }
    
    public int getCamp() {
        return this.camp;
    }
    
    public void setCamp(final int camp) {
        this.camp = camp;
    }
    
    public int getTime() {
        return this.time;
    }
    
    public void setTime(final int time) {
        this.time = time;
    }
    
    public int getBlood() {
        return this.blood;
    }
    
    public void setBlood(final int blood) {
        this.blood = blood;
    }
    
    public int[] getCities() {
        return this.cities;
    }
    
    public void setCities(final int[] cities) {
        this.cities = cities;
    }
    
    public ScenarioActionChasing(final String[] operationSingle) {
        this.pic = operationSingle[1];
        this.camp = Integer.parseInt(operationSingle[2]);
        this.time = Integer.parseInt(operationSingle[3]);
        this.totalTime = Integer.parseInt(operationSingle[4]);
        this.blood = Integer.parseInt(operationSingle[5]);
        this.curCity = 0;
        final int size = operationSingle.length - 6;
        this.cities = new int[size];
        for (int i = 0; i < size; ++i) {
            this.cities[i] = Integer.parseInt(operationSingle[i + 6]);
        }
        this.nextExcutedTime = 0L;
        this.executeTimesGoal = Integer.MAX_VALUE;
        this.repeatCirce = 1000L;
    }
    
    @Override
    public void doWork(final IDataGetter getter, final int playerId, final ScenarioEvent scenarioEvent) {
        final long trigger = scenarioEvent.getTriggerTime();
        if (this.nextExcutedTime == 0L) {
            this.changeNextExcutedTime(this.nextExcutedTime = trigger + 1000L * this.time, scenarioEvent, playerId);
            if (scenarioEvent.getSoloEvent().getId() == Constants.EVENT_ID_HUSONGHUANGSO2 && this.camp == 0) {
                ScenarioEventJsonBuilder.sendDialog(playerId, 6004, null);
                scenarioEvent.eventChangeToSave(getter, playerId);
            }
            return;
        }
        final long now = System.currentTimeMillis();
        if (trigger >= now) {
            return;
        }
        switch (this.camp) {
            case 0: {
                this.chasingJenney(getter, playerId, scenarioEvent);
                break;
            }
            case 1: {
                this.escapeAwayTommy(getter, playerId, scenarioEvent);
                break;
            }
        }
    }
    
    protected void changeNextExcutedTime(final long nextExcutedTime2, final ScenarioEvent scenarioEvent, final int playerId) {
        final ScenarioChansingInfo chansingInfo = scenarioEvent.getScenarioChansingInfo();
        if (chansingInfo == null) {
            return;
        }
        switch (this.camp) {
            case 0: {
                chansingInfo.setNextTommyTime(this.nextExcutedTime = nextExcutedTime2);
                break;
            }
            case 1: {
                chansingInfo.setNextJennyTime(this.nextExcutedTime = nextExcutedTime2);
                break;
            }
        }
        ScenarioEventJsonBuilder.sendChasingInfo(chansingInfo, playerId, scenarioEvent);
    }
    
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
            if (now >= this.nextExcutedTime) {
                final int nextCity = this.curCity + 1;
                if (nextCity >= this.cities.length) {
                    errorSceneLog.error("chasingJenney exception..playerId:" + playerId + " nextCity:" + nextCity);
                    return;
                }
                chansingInfo.setNextJennyTime(this.nextExcutedTime += 1000L * this.time);
                this.changeChasingCity(chansingInfo, 1, nextCity, playerId, scenarioEvent);
                scenarioEvent.eventChangeToSave(getter, playerId);
            }
            else {
                final int nextCity = this.curCity + 1;
                if (nextCity >= this.cities.length) {
                    errorSceneLog.error("chasingJenney exception..playerId:" + playerId + " nextCity:" + nextCity);
                    return;
                }
                final int cityId = this.cities[nextCity];
                final JuBenCityDto cityDto = juBenDto.juBenCityDtoMap.get(cityId);
                if (cityDto == null) {
                    errorSceneLog.error("city is null..." + cityId);
                    return;
                }
                if (cityDto.forceId != 0) {
                    chansingInfo.setNextJennyTime(this.nextExcutedTime = now + 1000L * this.time);
                    this.changeChasingCity(chansingInfo, 1, nextCity, playerId, scenarioEvent);
                    scenarioEvent.eventChangeToSave(getter, playerId);
                }
            }
        }
        catch (Exception e) {
            final ErrorSceneLog errorSceneLog2 = ErrorSceneLog.getInstance();
            errorSceneLog2.error("escapeAwayTommy playerId.." + playerId);
            errorSceneLog2.error(e.getMessage());
            errorSceneLog2.error(this, e);
        }
    }
    
    protected void changeChasingCity(final ScenarioChansingInfo chansingInfo, final int camp, final int nextCity, final int playerId, final ScenarioEvent scenarioEvent) {
        switch (camp) {
            case 0: {
                chansingInfo.setTommyCity(this.curCity = nextCity);
                break;
            }
            case 1: {
                chansingInfo.setJenneyCity(this.curCity = nextCity);
                break;
            }
        }
        ScenarioEventJsonBuilder.sendChasingInfo(chansingInfo, playerId, scenarioEvent);
    }
    
    protected void chasingJenney(final IDataGetter getter, final int playerId, final ScenarioEvent scenarioEvent) {
        try {
            if (scenarioEvent.getState() >= 3) {
                return;
            }
            final long now = System.currentTimeMillis();
            final ScenarioChansingInfo chasing = scenarioEvent.getScenarioChansingInfo();
            if (chasing == null || chasing.getState() != 0) {
                return;
            }
            final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
            if (juBenDto == null) {
                return;
            }
            if (now >= this.nextExcutedTime) {
                final int nextCity = this.curCity + 1;
                final boolean succeed = this.chasingSucceed(chasing, nextCity);
                if (succeed) {
                    chasing.setJenneyBlood(chasing.getJenneyBlood() - 1);
                    chasing.setNextTommyTime(this.nextExcutedTime = now + 1000L * this.time);
                    ScenarioEventJsonBuilder.sendChasingInfo(chasing, playerId, scenarioEvent);
                    scenarioEvent.eventChangeToSave(getter, playerId);
                    ScenarioEventJsonBuilder.sendDialog(playerId, 6005, null);
                }
                else {
                    this.curCity = nextCity;
                    chasing.setNextTommyTime(this.nextExcutedTime = now + 1000L * this.time);
                    this.changeChasingCity(chasing, 0, nextCity, playerId, scenarioEvent);
                    scenarioEvent.eventChangeToSave(getter, playerId);
                }
            }
        }
        catch (Exception e) {
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            errorSceneLog.error("chasingJenney playerId:" + playerId);
            errorSceneLog.error(e.getMessage());
            errorSceneLog.error(this, e);
        }
    }
    
    protected boolean chasingSucceed(final ScenarioChansingInfo chasing, final int nextCity) {
        if (nextCity >= this.cities.length) {
            return false;
        }
        final int nextCityId = this.cities[nextCity];
        final int jennyCityId = chasing.getJennyCityId();
        return nextCityId == jennyCityId;
    }
    
    @Override
    public ScenarioActionChasing clone() throws CloneNotSupportedException {
        return (ScenarioActionChasing)super.clone();
    }
    
    public ScenarioChansingInfo getChasingInfo() {
        ScenarioChansingInfo chansingInfo = null;
        final int camp = this.camp;
        switch (camp) {
            case 0: {
                chansingInfo = new ScenarioChansingInfo();
                chansingInfo.setTommyCity(this.getCurCity());
                chansingInfo.setTommyCities(this.getCities());
                chansingInfo.setTotalTime(this.getTotalTime());
                chansingInfo.setTommyPic(this.pic);
                break;
            }
            case 1: {
                chansingInfo = new ScenarioChansingInfo();
                chansingInfo.setJenneyCity(this.getCurCity());
                chansingInfo.setJenneyBlood(this.getBlood());
                chansingInfo.setJennyCities(this.getCities());
                chansingInfo.setTotalTime(this.getTotalTime());
                chansingInfo.setJennyPic(this.pic);
                break;
            }
        }
        return chansingInfo;
    }
    
    @Override
    public void retoreChasing(final String chasingInfo, final long restoreTime) {
        if (StringUtils.isBlank(chasingInfo)) {
            return;
        }
        try {
            final String[] single = chasingInfo.split(",");
            final int blood = Integer.parseInt(single[0]);
            final int jenneyCity = Integer.parseInt(single[1]);
            final long nextJennyTime = Long.parseLong(single[2]);
            final int tommyCity = Integer.parseInt(single[3]);
            final long nextTommyTime = Long.parseLong(single[4]);
            if (this.camp == 0) {
                this.curCity = tommyCity;
                this.nextExcutedTime = nextTommyTime + restoreTime;
            }
            else {
                this.blood = blood;
                this.curCity = jenneyCity;
                this.nextExcutedTime = nextJennyTime + restoreTime;
            }
        }
        catch (Exception e) {
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            errorSceneLog.error(e.getMessage());
            errorSceneLog.error(this, e);
        }
    }
}
