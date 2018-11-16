package com.reign.gcld.mine.service;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.battle.common.*;

public interface IMineService
{
    byte[] getMineInfo(final int p0, final int p1, final PlayerDto p2);
    
    byte[] rush(final int p0, final PlayerDto p1);
    
    byte[] abandon(final int p0, final PlayerDto p1);
    
    OccupyMineInfo handleAfterBattle(final int p0, final int p1);
    
    void releaseMineOutput(final String p0);
    
    byte[] mine(final int p0, final PlayerDto p1);
}
