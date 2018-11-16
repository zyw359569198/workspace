package com.reign.gcld.battle.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.battle.domain.*;
import java.util.*;

public interface IPlayerMistLostDao extends IBaseDao<PlayerMistLost>
{
    PlayerMistLost read(final int p0);
    
    PlayerMistLost readForUpdate(final int p0);
    
    List<PlayerMistLost> getModels();
    
    int getModelSize();
    
    int create(final PlayerMistLost p0);
    
    int deleteById(final int p0);
    
    int updateNpcLostDetail(final int p0, final int p1, final String p2);
    
    PlayerMistLost getMist(final int p0, final int p1);
    
    int deleteByPlayerIdAreaId(final int p0, final int p1);
    
    List<PlayerMistLost> getMistList(final int p0);
}
