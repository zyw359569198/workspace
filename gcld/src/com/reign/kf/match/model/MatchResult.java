package com.reign.kf.match.model;

import com.reign.kf.comm.param.match.*;
import com.reign.kf.comm.entity.match.*;
import java.util.*;

public class MatchResult
{
    public Object result;
    public CampArmyParam[] data1;
    public CampArmyParam[] data2;
    public MatchRTInfoEntity rtInfo;
    public String reportId;
    public String report;
    public int winner;
    public boolean hasNext;
    public Date nextTime;
    public int matchId;
    public int session;
    public Date lastTime;
}
