package com.reign.gcld.common;

import com.reign.util.*;
import com.reign.gcld.battle.common.*;

public class TimeCount
{
    private static final String MSG_FORMAT = "########PROCESS {0} EXECUTED TIME {1}";
    boolean isFirst;
    long startTime;
    
    public TimeCount() {
        this.isFirst = true;
        this.startTime = System.currentTimeMillis();
    }
    
    public String tickTime(final String name) {
        final long now = System.currentTimeMillis();
        final long exeTime = now - this.startTime;
        this.startTime = now;
        final String formateResult = MessageFormatter.format("########PROCESS {0} EXECUTED TIME {1}", new Object[] { (name == null) ? "DEFAULT" : name, exeTime });
        final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
        errorSceneLog.error(formateResult);
        return formateResult;
    }
}
