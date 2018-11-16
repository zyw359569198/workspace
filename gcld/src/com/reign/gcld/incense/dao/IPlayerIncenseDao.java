package com.reign.gcld.incense.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.incense.domain.*;
import java.util.*;

public interface IPlayerIncenseDao extends IBaseDao<PlayerIncense>
{
    PlayerIncense read(final int p0);
    
    PlayerIncense readForUpdate(final int p0);
    
    List<PlayerIncense> getModels();
    
    int getModelSize();
    
    int create(final PlayerIncense p0);
    
    int deleteById(final int p0);
    
    int useIncenseNum(final int p0);
    
    int addCopperTimes(final int p0);
    
    int addWoodTimes(final int p0);
    
    int addFoodTimes(final int p0);
    
    int addIronTimes(final int p0);
    
    int addGemTimes(final int p0);
    
    int setOpenBit(final int p0, final int p1);
    
    int addIncenseNum(final int p0, final int p1);
    
    int getIncenseNum(final int p0);
    
    int resetIncenseTimes();
    
    int getOpenBit(final int p0);
    
    int addIncenseNumByForceId(final int p0, final int p1);
}
