package com.reign.gcld.weapon.service;

import com.reign.gcld.player.dto.*;

public interface IGemService
{
    byte[] polish(final PlayerDto p0, final int p1);
    
    byte[] gemUpgrade(final PlayerDto p0, final int p1, final String p2);
    
    byte[] gemRefine(final PlayerDto p0, final int p1, final int p2);
}
