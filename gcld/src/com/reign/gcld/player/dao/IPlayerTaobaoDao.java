package com.reign.gcld.player.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.player.domain.*;
import java.util.*;

public interface IPlayerTaobaoDao extends IBaseDao<PlayerTaobao>
{
    PlayerTaobao read(final int p0);
    
    PlayerTaobao readForUpdate(final int p0);
    
    List<PlayerTaobao> getModels();
    
    int getModelSize();
    
    int create(final PlayerTaobao p0);
    
    int deleteById(final int p0);
}
