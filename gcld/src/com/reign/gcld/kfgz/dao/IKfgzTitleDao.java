package com.reign.gcld.kfgz.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.kfgz.domain.*;
import java.util.*;

public interface IKfgzTitleDao extends IBaseDao<KfgzTitle>
{
    KfgzTitle read(final int p0);
    
    KfgzTitle readForUpdate(final int p0);
    
    List<KfgzTitle> getModels();
    
    int getModelSize();
    
    int create(final KfgzTitle p0);
    
    int deleteById(final int p0);
    
    List<KfgzTitle> getKfgzTitleListBySeasonId(final int p0);
}
