package com.reign.gcld.affair.service;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.general.domain.*;

public interface ICivilService
{
    byte[] startAffair(final PlayerDto p0, final int p1, final int p2);
    
    byte[] stopAffair(final PlayerDto p0, final int p1, final int p2);
    
    byte[] finishAllAffair(final int p0, final PlayerDto p1);
    
    void tryAssignAffair(final PlayerGeneralCivil p0);
    
    boolean updateExpAndCivilLevel(final int p0, final PlayerGeneralCivil p1, final int p2);
}
