package com.reign.gcld.asynchronousDB.cache.obj;

import java.util.*;

public class DBCacheObjPlayer
{
    public DBCacheObj DBCacheObjSilver;
    public DBCacheObj DBCacheObjWood;
    public DBCacheObj DBCacheObjFood;
    public DBCacheObj DBCacheObjChiefExp;
    Map<Integer, DBCacheObjPlayerGeneralMilitary> generalMap;
    
    public DBCacheObjPlayer() {
        this.generalMap = new HashMap<Integer, DBCacheObjPlayerGeneralMilitary>();
    }
}
