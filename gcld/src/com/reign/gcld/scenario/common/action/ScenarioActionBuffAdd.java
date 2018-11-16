package com.reign.gcld.scenario.common.action;

import com.reign.gcld.common.*;
import com.reign.gcld.scenario.common.*;

public class ScenarioActionBuffAdd extends ScenarioAction implements Cloneable
{
    private int camp;
    private float effect;
    private long lastTime;
    
    public long getLastTime() {
        return this.lastTime;
    }
    
    public void setLastTime(final long lastTime) {
        this.lastTime = lastTime;
    }
    
    public int getCamp() {
        return this.camp;
    }
    
    public void setCamp(final int camp) {
        this.camp = camp;
    }
    
    public float getEffect() {
        return this.effect;
    }
    
    public void setEffect(final float effect) {
        this.effect = effect;
    }
    
    public ScenarioActionBuffAdd(final String[] operationSingle) {
        this.camp = 0;
        this.effect = 0.0f;
        this.lastTime = 0L;
        final int camp = Integer.parseInt(operationSingle[1]);
        final float effect = Float.parseFloat(operationSingle[2]);
        final long time = Long.parseLong(operationSingle[3]);
        this.camp = camp;
        this.effect = effect;
        this.lastTime = time;
        this.executeTimesGoal = 1;
    }
    
    @Override
    public void doWork(final IDataGetter getter, final int playerId, final ScenarioEvent scenarioEvent) {
        getter.getJuBenService().setJubenBuff(playerId, this.effect, this.lastTime * 1000L, this.camp + 1);
    }
    
    @Override
    public ScenarioActionBuffAdd clone() throws CloneNotSupportedException {
        return (ScenarioActionBuffAdd)super.clone();
    }
}
