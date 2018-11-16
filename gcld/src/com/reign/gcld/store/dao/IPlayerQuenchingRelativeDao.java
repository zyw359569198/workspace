package com.reign.gcld.store.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.store.domain.*;
import java.util.*;

public interface IPlayerQuenchingRelativeDao extends IBaseDao<PlayerQuenchingRelative>
{
    PlayerQuenchingRelative read(final int p0);
    
    PlayerQuenchingRelative readForUpdate(final int p0);
    
    List<PlayerQuenchingRelative> getModels();
    
    int getModelSize();
    
    int create(final PlayerQuenchingRelative p0);
    
    int deleteById(final int p0);
    
    int updateFreeQuenchingTimes(final int p0, final int p1);
    
    int updateFreeNiubiTimes(final int p0, final int p1);
    
    int updateAllFreeQuenchingTimes(final int p0);
    
    int addFreeNiubiTimes(final int p0, final int p1, final String p2);
    
    List<PlayerQuenchingRelative> getListByIds(final List<Integer> p0);
    
    int addFreeQuenchingTimes(final int p0, final int p1);
    
    int updateRemindQuenching(final int p0, final int p1);
}
