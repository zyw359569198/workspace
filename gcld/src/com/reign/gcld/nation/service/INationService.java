package com.reign.gcld.nation.service;

import com.reign.gcld.player.dto.*;
import com.reign.util.*;
import java.util.*;
import com.reign.gcld.rank.domain.*;

public interface INationService
{
    void initTryTask();
    
    byte[] getNationInfo(final PlayerDto p0);
    
    byte[] openTry(final PlayerDto p0);
    
    byte[] getTryInfo(final PlayerDto p0);
    
    void createPlayerTryRank(final int p0);
    
    byte[] getReward(final PlayerDto p0);
    
    Map<Integer, Tuple<Integer, Date>> getTryMap();
    
    boolean haveTryReward(final int p0, final int p1);
    
    byte[] getTryTaskInfo(final int p0, final int p1, final ForceInfo p2);
    
    byte[] getTryTaskInfo();
    
    int getStageByForceId(final int p0);
    
    void checkTryTask(final String p0);
    
    void addKillGeneralNum(final String p0);
    
    long getTryTaskCd(final int p0);
    
    void pushTryTaskResult(final int p0, final boolean p1);
}
