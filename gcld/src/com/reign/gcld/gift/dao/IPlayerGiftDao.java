package com.reign.gcld.gift.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.gift.domain.*;
import java.util.*;

public interface IPlayerGiftDao extends IBaseDao<PlayerGift>
{
    PlayerGift read(final int p0);
    
    PlayerGift readForUpdate(final int p0);
    
    List<PlayerGift> getModels();
    
    int getModelSize();
    
    int create(final PlayerGift p0);
    
    int deleteById(final int p0);
    
    List<PlayerGift> getByPlayerId(final int p0);
    
    int getAllServerByPlayerId(final int p0);
    
    List<PlayerGift> getAllGiftByPlayerId(final int p0);
    
    PlayerGift getByPlayerIdAndGiftId(final int p0, final int p1);
    
    int updateGift(final int p0, final Date p1);
    
    int deleteByGiftId(final int p0);
    
    List<PlayerGift> getByGiftId(final int p0);
}
