package com.reign.gcld.slave.service;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.slave.domain.*;

public interface ISlaveService
{
    byte[] getSlaveInfo(final PlayerDto p0);
    
    byte[] lash(final PlayerDto p0, final int p1);
    
    byte[] makeCell(final PlayerDto p0);
    
    byte[] escape(final PlayerDto p0, final int p1);
    
    byte[] viewMaster(final PlayerDto p0, final int p1);
    
    byte[] freedom(final PlayerDto p0, final int p1);
    
    byte[] updateLimbo(final PlayerDto p0);
    
    byte[] updateLashLv(final int p0);
    
    boolean dealSlave(final String p0);
    
    void escapeJob(final String p0);
    
    void resetSlaveSystem(final int p0);
    
    boolean haveLimboPic(final int p0, final int p1);
    
    void addLimboPic(final int p0, final int p1, final int p2);
    
    void addPoint(final Slaveholder p0);
    
    byte[] useInTaril(final PlayerDto p0);
    
    byte[] getTrailGold(final PlayerDto p0);
}
