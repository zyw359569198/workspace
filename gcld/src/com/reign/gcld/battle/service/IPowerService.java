package com.reign.gcld.battle.service;

import com.reign.gcld.player.dto.*;

public interface IPowerService
{
    byte[] getPowerInfo(final PlayerDto p0);
    
    byte[] switchPowerInfo(final PlayerDto p0, final int p1);
    
    void pushBattleRewardInfo(final int p0);
    
    byte[] getExtraPowerInfo(final PlayerDto p0, final int p1);
    
    byte[] buyBonusNpc(final PlayerDto p0, final int p1);
    
    byte[] buyPowerExtra(final PlayerDto p0, final int p1);
    
    boolean bonusLiaoHuaDefeatedOrTimeOut(final int p0);
    
    byte[] getPowerGuide(final PlayerDto p0);
    
    byte[] getCurrentGuide(final PlayerDto p0, final int p1);
}
