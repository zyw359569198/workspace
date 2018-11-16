package com.reign.gcld.weapon.service;

import com.reign.gcld.player.dto.*;

public interface IWeaponService
{
    byte[] getWeaponInfo(final PlayerDto p0);
    
    byte[] upgradeWeapon(final int p0, final PlayerDto p1);
    
    byte[] buyWeaponItem(final PlayerDto p0, final int p1);
    
    void assignWeapon(final int p0, final int p1);
    
    byte[] loadGem(final int p0, final int p1, final int p2, final PlayerDto p3);
    
    byte[] unloadGem(final int p0, final int p1, final PlayerDto p2);
    
    byte[] openSlot(final int p0, final int p1, final PlayerDto p2);
    
    byte[] preUnloadGem(final int p0, final PlayerDto p1);
    
    byte[] preLoadGem(final int p0, final int p1, final PlayerDto p2);
    
    byte[] getUnSetGems(final int p0, final int p1, final PlayerDto p2);
    
    void openWeaponFunction(final int p0);
}
