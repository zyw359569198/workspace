package com.reign.gcld.gift.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.gift.domain.*;
import java.util.*;

public interface IGiftInfoDao extends IBaseDao<GiftInfo>
{
    GiftInfo read(final int p0);
    
    GiftInfo readForUpdate(final int p0);
    
    List<GiftInfo> getModels();
    
    int getModelSize();
    
    int create(final GiftInfo p0);
    
    int deleteById(final int p0);
    
    int getAllServerNum(final String p0, final Date p1, final Date p2);
    
    int deleteByDate(final Date p0);
    
    List<GiftInfo> getByDate(final Date p0);
    
    List<GiftInfo> getByYx(final String p0);
}
