package com.reign.gcld.tavern.service;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.player.domain.*;

public interface ITavernService
{
    byte[] getGeneral(final int p0, final int p1);
    
    byte[] refreshGeneral(final int p0, final int p1, final boolean p2, final boolean p3);
    
    byte[] lockGeneral(final int p0, final int p1);
    
    byte[] unlockGeneral(final int p0, final int p1);
    
    byte[] recruitGeneral(final PlayerDto p0, final int p1);
    
    byte[] cdRecover(final int p0, final int p1);
    
    byte[] cdRecoverConfirm(final int p0, final int p1);
    
    void addLockId(final int p0, final int p1);
    
    boolean recruitGeneralDirect(final int p0, final int p1, final boolean p2);
    
    byte[] getCanDropGeneral(final int p0, final PlayerDto p1);
    
    int getMaxGeneralNum(final int p0, final int p1, final int p2);
    
    boolean checkFunctionIsOpen(final int p0, final int p1, final PlayerAttribute p2);
}
