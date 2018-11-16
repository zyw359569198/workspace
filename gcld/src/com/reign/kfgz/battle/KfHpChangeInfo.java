package com.reign.kfgz.battle;

import com.reign.kfgz.comm.*;

public class KfHpChangeInfo
{
    int killed;
    int hpLost;
    KfCampArmy beKilledCa;
    
    public int getKilled() {
        return this.killed;
    }
    
    public void setKilled(final int killed) {
        this.killed = killed;
    }
    
    public int getHpLost() {
        return this.hpLost;
    }
    
    public void setHpLost(final int hpLost) {
        this.hpLost = hpLost;
    }
    
    public KfCampArmy getBeKilledCa() {
        return this.beKilledCa;
    }
    
    public void setBeKilledCa(final KfCampArmy beKilledCa) {
        this.beKilledCa = beKilledCa;
    }
}
