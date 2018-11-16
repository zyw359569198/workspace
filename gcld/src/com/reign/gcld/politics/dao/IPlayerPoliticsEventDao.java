package com.reign.gcld.politics.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.politics.domain.*;
import java.util.*;

public interface IPlayerPoliticsEventDao extends IBaseDao<PlayerPoliticsEvent>
{
    PlayerPoliticsEvent read(final int p0);
    
    PlayerPoliticsEvent readForUpdate(final int p0);
    
    List<PlayerPoliticsEvent> getModels();
    
    int getModelSize();
    
    int create(final PlayerPoliticsEvent p0);
    
    int deleteById(final int p0);
    
    int getEventNum(final int p0);
    
    int addPoliticEventNum(final int p0, final int p1, final Date p2);
    
    int minusePoliticEventNum(final int p0);
    
    int addPeopleLoyal(final int p0, final int p1, final int p2);
    
    int resetPeopleLoyal(final int p0);
    
    int getPeopleLoyal(final int p0);
    
    List<Integer> getByPlayerIdsAndNum(final List<Integer> p0, final int p1);
    
    int addPoliticsNum(final List<Integer> p0, final int p1, final Date p2);
}
