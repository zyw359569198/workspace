package com.reign.gcld.chat.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.chat.domain.*;
import java.util.*;

public interface ISilenceDao extends IBaseDao<Silence>
{
    Silence read(final int p0);
    
    Silence readForUpdate(final int p0);
    
    List<Silence> getModels();
    
    int getModelSize();
    
    int create(final Silence p0);
    
    int deleteById(final int p0);
    
    Silence getByPlayerIdAndYx(final int p0, final String p1);
    
    Silence getByPlayerId(final int p0);
    
    int update(final Silence p0);
    
    List<Silence> getByDateAndYx(final Date p0, final String p1, final int p2);
}
