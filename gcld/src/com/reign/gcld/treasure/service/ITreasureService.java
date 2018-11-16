package com.reign.gcld.treasure.service;

import com.reign.gcld.player.dto.*;
import com.reign.util.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.kf.comm.entity.auction.*;

public interface ITreasureService
{
    byte[] getTreasures(final PlayerDto p0);
    
    Treasure tryGetTreasure(final PlayerDto p0, final int p1, final double p2);
    
    Tuple<Integer, GeneralTreasure> tryGetGeneralTreasure(final PlayerDto p0, final int p1, final boolean p2, final int p3, final int p4, final boolean p5, final String p6);
    
    boolean autionFailBackToBag(final Integer p0);
    
    boolean putOneItemInBag(final int p0, final NewAuctionGeneralEntity p1);
}
