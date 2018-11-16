package com.reign.gcld.politics.service;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.player.domain.*;
import java.util.*;

public interface IPoliticsService
{
    void producePoliticsEvent();
    
    byte[] getEventInfo(final PlayerDto p0, final int p1);
    
    byte[] chooseEventOption(final PlayerDto p0, final int p1, final int p2);
    
    void assignPoliticsEvent(final PlayerAttribute p0);
    
    Map<Integer, Integer> getBuildingTypeWithEvent(final int p0);
    
    void rewardPolitcsEvent(final int p0, final int p1);
    
    byte[] getReward(final PlayerDto p0);
    
    int openPolitcsEvent(final int p0);
}
