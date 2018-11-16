package com.reign.kfgz.dto.response;

public class KfgzBattleResultRes
{
    public static final int STATE_SUC = 1;
    int state;
    
    public int getState() {
        return this.state;
    }
    
    public void setState(final int state) {
        this.state = state;
    }
}
