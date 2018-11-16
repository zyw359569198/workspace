package com.reign.gcld.gift.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.gift.domain.*;
import java.util.*;

public interface IBakPlayerGiftDao extends IBaseDao<BakPlayerGift>
{
    BakPlayerGift read(final int p0);
    
    BakPlayerGift readForUpdate(final int p0);
    
    List<BakPlayerGift> getModels();
    
    int getModelSize();
    
    int create(final BakPlayerGift p0);
    
    int deleteById(final int p0);
}
