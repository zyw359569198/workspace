package com.reign.gcld.general.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.general.domain.*;
import java.util.*;

public interface IPlayerGeneralDao extends IBaseDao<PlayerGeneral>
{
    PlayerGeneral read(final int p0);
    
    PlayerGeneral readForUpdate(final int p0);
    
    List<PlayerGeneral> getModels();
    
    int getModelSize();
    
    int create(final PlayerGeneral p0);
    
    int deleteById(final int p0);
    
    PlayerGeneral getPlayerGeneral(final int p0, final int p1);
    
    List<PlayerGeneral> getGeneralList(final int p0);
    
    List<PlayerGeneral> getGeneralListByType(final int p0, final int p1);
    
    int updateForcesDate(final int p0, final int p1, final Date p2, final double p3, final long p4);
    
    int updateState(final int p0, final int p1, final Date p2, final int p3);
    
    int deleteByPlayerIdAndGeneralId(final int p0, final int p1);
}
