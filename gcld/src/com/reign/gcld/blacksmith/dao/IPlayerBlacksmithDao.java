package com.reign.gcld.blacksmith.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.blacksmith.domain.*;
import java.util.*;

public interface IPlayerBlacksmithDao extends IBaseDao<PlayerBlacksmith>
{
    PlayerBlacksmith read(final int p0);
    
    PlayerBlacksmith readForUpdate(final int p0);
    
    List<PlayerBlacksmith> getModels();
    
    int getModelSize();
    
    int create(final PlayerBlacksmith p0);
    
    int deleteById(final int p0);
    
    List<PlayerBlacksmith> getListByPlayerId(final int p0);
    
    int getSizeByPlayerId(final int p0);
    
    PlayerBlacksmith getByPlayerIdAndSmithId(final int p0, final int p1);
    
    int addSmithNum(final int p0, final int p1);
    
    int useSmithNum(final int p0, final int p1);
    
    int addSmithLv(final int p0, final int p1);
    
    List<Integer> getPlayerIdListBySmithId(final int p0);
    
    int resetSmithNum();
}
