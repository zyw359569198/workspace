package com.reign.gcld.asynchronousDB.cache.obj;

import com.reign.gcld.player.domain.*;
import com.reign.gcld.battle.common.*;

public class DBCacheObjSilver extends DBCacheObjBasic
{
    public DBCacheObjSilver(final int thresholdValue) {
        super(thresholdValue);
    }
    
    @Override
    public void caculateThreshold(final Object... params) {
        try {
            if (params[0].getClass() != Player.class) {
                ErrorSceneLog.getInstance().appendErrorMsg("params[0] is invalid").append("params[0]", params[0]).appendClassName(this.getClass().getSimpleName()).appendMethodName("caculateThreshold").flush();
                return;
            }
            this.thresholdValue = 1000;
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + ".caculateThreshold catch Exception", e);
        }
    }
    
    @Override
    public void commitAsynchronousDBOperation() {
    }
}
