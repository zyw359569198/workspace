package com.reign.gcld.courtesy.service;

import com.reign.gcld.player.dto.*;

public interface ICourtesyService
{
    byte[] getPanel(final PlayerDto p0);
    
    byte[] handleEvent(final PlayerDto p0, final int p1);
    
    byte[] getLiYiDuReward(final PlayerDto p0, final int p1);
    
    void closeLiShangWangLaiModule(final String p0);
    
    void removePlayerAfterLogOut(final PlayerDto p0);
    
    void addXiaoQianEvent(final int p0, final int p1);
    
    void addPlayerEvent(final int p0, final int p1, final int p2);
}
