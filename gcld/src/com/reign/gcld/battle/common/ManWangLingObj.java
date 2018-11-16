package com.reign.gcld.battle.common;

import java.util.*;

public class ManWangLingObj
{
    public int fromForceId;
    public int manZuForceId;
    public int toForceId;
    public int targetCityId;
    public long expireTime;
    public int type;
    public Set<Integer> playerSet;
    public static final int MAN_WANG_LING_TYPE_1_JIANSHOUBIANJIANG = 1;
    public static final int MAN_WANG_LING_TYPE_2_BAOHUMANWANG = 2;
    public static final long MAN_WANG_LING_EXPIRE_TIME = 1800000L;
    
    public ManWangLingObj() {
        this.fromForceId = 0;
        this.manZuForceId = 0;
        this.toForceId = 0;
        this.targetCityId = 0;
        this.expireTime = 0L;
        this.type = 0;
        this.playerSet = new HashSet<Integer>();
    }
}
