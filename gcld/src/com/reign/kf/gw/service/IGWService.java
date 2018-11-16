package com.reign.kf.gw.service;

import java.util.*;
import com.reign.kf.comm.entity.*;
import com.reign.kf.comm.entity.gw.*;
import com.reign.kf.comm.param.gw.*;

public interface IGWService
{
    List<SeasonInfoEntity> getSeasonInfo(final int p0);
    
    CommEntity updateSeasonInfo(final UpdateSeasonParam p0);
    
    AuctionInfoEntity getAuctionInfo(final int p0);
    
    CommEntity updateAuctionInfo(final UpdateAuctionParam p0);
}
