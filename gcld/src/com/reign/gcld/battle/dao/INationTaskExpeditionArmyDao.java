package com.reign.gcld.battle.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.battle.domain.*;
import java.util.*;

public interface INationTaskExpeditionArmyDao extends IBaseDao<NationTaskExpeditionArmy>
{
    NationTaskExpeditionArmy read(final int p0);
    
    NationTaskExpeditionArmy readForUpdate(final int p0);
    
    List<NationTaskExpeditionArmy> getModels();
    
    int getModelSize();
    
    int create(final NationTaskExpeditionArmy p0);
    
    int deleteById(final int p0);
    
    int batchCreate(final List<NationTaskExpeditionArmy> p0);
    
    int updateLocationAndState(final Integer p0, final Integer p1, final int p2);
    
    List<NationTaskExpeditionArmy> getNationTaskEAsByLocationId(final Integer p0);
    
    int updateState(final Integer p0, final int p1);
    
    int updateHpAndTacticVal(final int p0, final int p1, final int p2);
    
    int resetStateByLocationAndState(final int p0, final int p1);
    
    int resetAllState();
    
    List<NationTaskExpeditionArmy> getNationTaskDefenceEAsByLocationId(final Integer p0);
    
    int getMaxVid();
    
    int deleteAllFreeTicketArmy();
}
