package com.reign.gcld.slave.common;

import java.util.*;

public class SlaveInfo
{
    private int type;
    private List<Integer> playerIdList;
    
    public SlaveInfo() {
        this.playerIdList = new ArrayList<Integer>();
    }
    
    public int getType() {
        return this.type;
    }
    
    public void setType(final int type) {
        this.type = type;
    }
    
    public List<Integer> getPlayerIdList() {
        return this.playerIdList;
    }
    
    public void addPlayerId(final int playerId) {
        this.playerIdList.add(playerId);
    }
}
