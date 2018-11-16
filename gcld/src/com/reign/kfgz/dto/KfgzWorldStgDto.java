package com.reign.kfgz.dto;

import com.reign.kf.match.sdata.domain.*;
import java.util.*;

public class KfgzWorldStgDto
{
    int gzId;
    KfgzWorldStg stg;
    Date nextExcuteTime;
    
    public KfgzWorldStg getStg() {
        return this.stg;
    }
    
    public void setStg(final KfgzWorldStg stg) {
        this.stg = stg;
    }
    
    public Date getNextExcuteTime() {
        return this.nextExcuteTime;
    }
    
    public void setNextExcuteTime(final Date nextExcuteTime) {
        this.nextExcuteTime = nextExcuteTime;
    }
    
    public int getGzId() {
        return this.gzId;
    }
    
    public void setGzId(final int gzId) {
        this.gzId = gzId;
    }
}
