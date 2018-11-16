package com.reign.gcld.store.service;

import com.reign.gcld.player.dto.*;

public interface IQuenchingService
{
    byte[] getQuenchingInfo(final PlayerDto p0, final int p1);
    
    byte[] quenchingEquip(final PlayerDto p0, final int p1, final int p2);
    
    byte[] getEquips(final PlayerDto p0);
    
    void addFreeQuenchingTimes();
    
    byte[] remindSet(final PlayerDto p0, final int p1);
    
    void checkSpecialSkill(final int p0);
    
    byte[] getRestoreInfo(final PlayerDto p0, final int p1);
    
    byte[] restoreSpecial(final PlayerDto p0, final int p1);
}
