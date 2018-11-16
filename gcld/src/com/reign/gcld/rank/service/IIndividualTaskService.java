package com.reign.gcld.rank.service;

import com.reign.gcld.player.dto.*;
import com.reign.util.*;

public interface IIndividualTaskService
{
    byte[] getIndiviInfo(final PlayerDto p0);
    
    void sendTaskMessage(final String p0);
    
    byte[] getIndiviInfo(final int p0, final int p1);
    
    byte[] getRightBarIndivTaskInfo(final PlayerDto p0);
    
    void sendTaskMessage(final PlayerDto p0, final int p1, final String p2);
    
    byte[] getIndivReward(final PlayerDto p0, final int p1);
    
    byte[] getReward(final PlayerDto p0, final int p1);
    
    Tuple<byte[], String> getReward(final PlayerDto p0);
}
