package com.reign.gcld.rank.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.rank.domain.*;
import java.util.*;

public interface IPlayerCouponDao extends IBaseDao<PlayerCoupon>
{
    PlayerCoupon read(final int p0);
    
    PlayerCoupon readForUpdate(final int p0);
    
    List<PlayerCoupon> getModels();
    
    int getModelSize();
    
    int create(final PlayerCoupon p0);
    
    int deleteById(final int p0);
    
    PlayerCoupon getPlayerCouponByPT(final int p0, final int p1);
    
    int reduceNum(final int p0, final int p1);
    
    int updateCouponNum(final int p0, final int p1);
    
    int addCouponNum(final int p0, final int p1);
}
