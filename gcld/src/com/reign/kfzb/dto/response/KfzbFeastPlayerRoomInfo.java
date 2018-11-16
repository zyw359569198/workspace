package com.reign.kfzb.dto.response;

import java.util.*;

public class KfzbFeastPlayerRoomInfo
{
    Map<Integer, Long> map;
    
    public KfzbFeastPlayerRoomInfo() {
        this.map = new HashMap<Integer, Long>();
    }
    
    public Map<Integer, Long> getMap() {
        return this.map;
    }
    
    public void setMap(final Map<Integer, Long> map) {
        this.map = map;
    }
}
