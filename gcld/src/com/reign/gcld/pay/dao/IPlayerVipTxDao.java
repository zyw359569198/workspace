package com.reign.gcld.pay.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.pay.domain.*;
import java.util.*;

public interface IPlayerVipTxDao extends IBaseDao<PlayerVipTx>
{
    PlayerVipTx read(final int p0);
    
    PlayerVipTx readForUpdate(final int p0);
    
    List<PlayerVipTx> getModels();
    
    int getModelSize();
    
    int create(final PlayerVipTx p0);
    
    int deleteById(final int p0);
    
    PlayerVipTx getByPlayerId(final int p0);
    
    int updateDailyStatus(final int p0, final int p1);
    
    int updateRookieStatus(final int p0, final int p1);
    
    int updateUgradeStatus(final int p0, final int p1, final int p2);
    
    int updateExtraStatus(final int p0, final int p1);
    
    int resetDailyStatus();
}
