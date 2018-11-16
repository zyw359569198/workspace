package com.reign.gcld.world.service;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.world.common.*;
import com.reign.gcld.world.domain.*;
import com.reign.gcld.common.*;

public interface IKillRankService
{
    byte[] reward(final PlayerDto p0);
    
    void dealTodayInfo();
    
    void fireRankEvent(final int p0, final RankData p1);
    
    int getRank(final int p0, final int p1, final int p2);
    
    byte[] getLevelRankList(final int p0, final int p1, final int p2);
    
    void setBoxInfo(final PlayerKillInfo p0, final int p1);
    
    int updateKillNum(final int p0, final int p1, final int p2);
    
    void dealKillrank(final int p0, final int p1, final IDataGetter p2, final int p3);
}
