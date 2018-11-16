package com.reign.gcld.asynchronousDB.cache.obj;

import java.util.*;
import com.reign.gcld.battle.common.*;

public abstract class DBCacheObjBasic implements DBCacheObj
{
    protected int cumulateValue;
    protected int thresholdValue;
    public Date lastModifyTime;
    
    public DBCacheObjBasic(final int thresholdValue) {
        this.lastModifyTime = null;
        this.cumulateValue = 0;
        this.caculateThreshold(new Object[0]);
    }
    
    @Override
    public void addValue(final int addValue) {
        if (addValue <= 0) {
            ErrorSceneLog.getInstance().appendErrorMsg("addValue is not positive").append("addValue", addValue).appendClassName(this.getClass().getSimpleName()).appendMethodName("addValue").flush();
            return;
        }
        this.cumulateValue += addValue;
        this.lastModifyTime = new Date();
    }
    
    @Override
    public void minusValue(final int minusValue) {
        if (minusValue <= 0) {
            ErrorSceneLog.getInstance().appendErrorMsg("minusValue is not positive").append("minusValue", minusValue).appendClassName(this.getClass().getSimpleName()).appendMethodName("minusValue").flush();
            return;
        }
        this.cumulateValue -= minusValue;
        this.lastModifyTime = new Date();
    }
    
    @Override
    public boolean needToCommit() {
        return this.cumulateValue >= this.thresholdValue || (this.lastModifyTime != null && System.currentTimeMillis() - 1800000L >= this.lastModifyTime.getTime());
    }
    
    @Override
    public void reset() {
        this.cumulateValue = 0;
        this.lastModifyTime = null;
    }
}
