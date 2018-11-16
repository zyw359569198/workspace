package com.reign.gcld.scenario.common.action;

import com.reign.gcld.common.*;
import com.reign.gcld.scenario.common.*;

public class ScenarioActionNiuBi extends ScenarioAction implements Cloneable
{
    private int camp;
    private int att;
    private int def;
    
    public int getCamp() {
        return this.camp;
    }
    
    public void setCamp(final int camp) {
        this.camp = camp;
    }
    
    public int getAtt() {
        return this.att;
    }
    
    public void setAtt(final int att) {
        this.att = att;
    }
    
    public int getDef() {
        return this.def;
    }
    
    public void setDef(final int def) {
        this.def = def;
    }
    
    public ScenarioActionNiuBi(final String[] operationSingle) {
        this.camp = Integer.parseInt(operationSingle[1]);
        this.att = Integer.parseInt(operationSingle[2]);
        this.def = Integer.parseInt(operationSingle[3]);
    }
    
    @Override
    public void doWork(final IDataGetter getter, final int playerId, final ScenarioEvent scenarioEvent) {
        getter.getJuBenService().setJubenAttDefBaseBuff(playerId, this.att, this.def, 0L, this.camp + 1);
    }
    
    @Override
    public ScenarioActionNiuBi clone() throws CloneNotSupportedException {
        return (ScenarioActionNiuBi)super.clone();
    }
}
