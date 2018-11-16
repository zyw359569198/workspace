package com.reign.gcld.rank.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.rank.domain.*;
import java.util.*;

public interface IBarbariansKillInfoDao extends IBaseDao<BarbariansKillInfo>
{
    BarbariansKillInfo read(final int p0);
    
    BarbariansKillInfo readForUpdate(final int p0);
    
    List<BarbariansKillInfo> getModels();
    
    int getModelSize();
    
    int create(final BarbariansKillInfo p0);
    
    int deleteById(final int p0);
    
    List<BarbariansKillInfo> getByforceId(final int p0);
}
