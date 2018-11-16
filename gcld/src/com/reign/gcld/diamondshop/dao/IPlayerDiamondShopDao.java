package com.reign.gcld.diamondshop.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.diamondshop.domain.*;
import java.util.*;

public interface IPlayerDiamondShopDao extends IBaseDao<PlayerDiamondShop>
{
    PlayerDiamondShop read(final int p0);
    
    PlayerDiamondShop readForUpdate(final int p0);
    
    List<PlayerDiamondShop> getModels();
    
    int getModelSize();
    
    int create(final PlayerDiamondShop p0);
    
    int deleteById(final int p0);
    
    PlayerDiamondShop getMaxShop(final int p0);
    
    int updateDiamondShopLv(final int p0, final int p1, final int p2);
    
    PlayerDiamondShop getByShopId(final int p0, final int p1);
    
    int reduceDailyTimes(final int p0, final int p1);
    
    int resetRTimes(final int p0, final int p1);
}
