package com.reign.gcld.store.service;

import com.reign.gcld.store.domain.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.json.*;

public interface IStoreService
{
    byte[] getItems(final int p0, final int p1);
    
    byte[] refreshItem(final int p0, final int p1, final boolean p2);
    
    byte[] refreshItem(final int p0, final int p1, final PlayerStore p2, final Chargeitem p3);
    
    byte[] buyItem(final PlayerDto p0, final int p1);
    
    byte[] lockItem(final int p0, final int p1);
    
    byte[] unlockItem(final int p0, final int p1);
    
    byte[] cdRecover(final int p0, final int p1);
    
    byte[] cdRecoverConfirm(final int p0, final int p1);
    
    void addLockId(final int p0, final int p1);
    
    boolean hasPurpleEquip(final int p0);
    
    byte[] getEquipSuitTipInfo(final int p0, final int p1);
    
    int checkSuitOpen(final Integer p0, final JsonDocument p1, final int p2);
}
