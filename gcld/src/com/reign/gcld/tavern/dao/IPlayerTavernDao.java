package com.reign.gcld.tavern.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.tavern.domain.*;
import java.util.*;

public interface IPlayerTavernDao extends IBaseDao<PlayerTavern>
{
    PlayerTavern read(final int p0);
    
    PlayerTavern readForUpdate(final int p0);
    
    List<PlayerTavern> getModels();
    
    int getModelSize();
    
    int create(final PlayerTavern p0);
    
    int deleteById(final int p0);
    
    void updatePlayerTavern(final PlayerTavern p0);
    
    void updateLockId(final int p0, final String p1);
    
    int updateMilitaryInfo(final int p0, final String p1);
    
    int updateCivilInfo(final int p0, final String p1);
    
    String getCivilInfo(final int p0);
    
    String getMilitaryInfo(final int p0);
}
