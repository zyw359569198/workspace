package com.reign.gcld.gift.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.gift.domain.*;
import java.util.*;

public interface IBakGiftInfoDao extends IBaseDao<BakGiftInfo>
{
    BakGiftInfo read(final int p0);
    
    BakGiftInfo readForUpdate(final int p0);
    
    List<BakGiftInfo> getModels();
    
    int getModelSize();
    
    int create(final BakGiftInfo p0);
    
    int deleteById(final int p0);
}
