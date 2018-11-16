package com.reign.gcld.auto.service;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.player.domain.*;
import com.reign.framework.json.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.auto.common.*;

public interface IAutoBattleService
{
    byte[] startAutoBattle(final PlayerDto p0, final int p1);
    
    byte[] stopAutoBattle(final PlayerDto p0);
    
    void recoverAutoBattleAfterMuBing(final int p0, final int p1);
    
    void stopAutoBattleAfterBattleEnded(final String p0);
    
    void nextRoundAfterGeneralDead(final int p0, final int p1);
    
    boolean InAutoBattleMode(final int p0);
    
    void resetAtZeroClock();
    
    void appendAutoBattleInfo(final Player p0, final JsonDocument p1);
    
    void zidongdantiao(final PlayerGeneralMilitary p0);
    
    void assembleOneGeneral(final PlayerAutoBattleObj p0, final PlayerGeneralMilitary p1);
    
    void increaseGeneralCount(final String p0);
    
    byte[] getAutoBattleDetail(final PlayerDto p0);
    
    void increaseLost(final String p0);
    
    void increaseExp(final String p0);
}
