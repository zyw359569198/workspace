package com.reign.gcld.world.common;

import com.reign.gcld.sdata.domain.*;
import java.util.concurrent.*;

public class CityAttribute
{
    public WorldCity targetWorldCity;
    public WdSjIn wdSjIn;
    public int eventType;
    public int leftCount;
    public int eachLimit;
    public int group;
    public long countDown;
    public int viewForceId;
    public int visiable;
    public int rewadLv;
    public int eventTargetId;
    public ConcurrentHashMap<Integer, Integer> playerIdCountMap;
    public static final int CITY_EVENT_TYPE_BATTLE = 1;
    public static final int CITY_EVENT_TYPE_ORDINARY = 2;
    
    public CityAttribute() {
        this.targetWorldCity = null;
        this.wdSjIn = null;
        this.eventType = 0;
        this.leftCount = 0;
        this.eachLimit = 0;
        this.group = 0;
        this.countDown = 0L;
        this.viewForceId = 0;
        this.visiable = 0;
        this.rewadLv = 0;
        this.eventTargetId = 0;
        this.playerIdCountMap = new ConcurrentHashMap<Integer, Integer>();
    }
    
    public void addPlayerCount(final int playerId, final int option) {
        --this.leftCount;
        final Integer gainCount = this.playerIdCountMap.get(playerId);
        if (gainCount != null) {
            this.playerIdCountMap.put(playerId, gainCount + 1);
        }
        else {
            this.playerIdCountMap.put(playerId, 1);
        }
        if (this.visiable == 0) {
            CityEventManager.getInstance().doHiddenEventBoBao(playerId, this, option);
        }
    }
}
