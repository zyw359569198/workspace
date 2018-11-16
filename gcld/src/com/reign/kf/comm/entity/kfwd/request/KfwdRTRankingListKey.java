package com.reign.kf.comm.entity.kfwd.request;

import java.io.*;

public class KfwdRTRankingListKey implements Serializable
{
    private static final long serialVersionUID = 1L;
    private int scheduleId;
    
    public int getScheduleId() {
        return this.scheduleId;
    }
    
    public void setScheduleId(final int scheduleId) {
        this.scheduleId = scheduleId;
    }
}
