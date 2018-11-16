package com.reign.kfzb.dto.response;

import java.util.*;

public class KfzbPhase2Info
{
    private int curLayer;
    private int curRound;
    Map<Integer, KfzbBattleInfo> map;
    
    public int getCurLayer() {
        return this.curLayer;
    }
    
    public void setCurLayer(final int curLayer) {
        this.curLayer = curLayer;
    }
    
    public Map<Integer, KfzbBattleInfo> getMap() {
        return this.map;
    }
    
    public void setMap(final Map<Integer, KfzbBattleInfo> map) {
        this.map = map;
    }
    
    public int getCurRound() {
        return this.curRound;
    }
    
    public void setCurRound(final int curRound) {
        this.curRound = curRound;
    }
}
