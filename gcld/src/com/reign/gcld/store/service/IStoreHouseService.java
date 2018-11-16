package com.reign.gcld.store.service;

import com.reign.gcld.player.domain.*;
import com.reign.gcld.player.dto.*;

public interface IStoreHouseService
{
    void gainItems(final int p0, final int p1, final int p2, final String p3);
    
    void gainGem(final Player p0, final int p1, final int p2, final String p3, final String p4);
    
    void gainItems(final int p0, final int p1, final int p2, final String p3, final boolean p4);
    
    void gainSearchItems(final int p0, final int p1, final PlayerDto p2, final String p3);
}
