package com.reign.gcld.tavern.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.tavern.domain.*;
import java.util.*;

public interface IPlayerGeneralRefreshDao extends IBaseDao<PlayerGeneralRefresh>
{
    PlayerGeneralRefresh read(final int p0);
    
    PlayerGeneralRefresh readForUpdate(final int p0);
    
    List<PlayerGeneralRefresh> getModels();
    
    int getModelSize();
    
    int create(final PlayerGeneralRefresh p0);
    
    int deleteById(final int p0);
    
    List<PlayerGeneralRefresh> getListByPlayerId(final int p0);
    
    void lockGeneral(final int p0);
    
    void unlockGeneral(final int p0, final Date p1);
    
    PlayerGeneralRefresh getPlayerGeneralRefresh(final int p0, final int p1);
    
    void recruitGeneral(final int p0);
}
