package com.reign.gcld.phantom.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.phantom.domain.*;
import java.util.*;

public interface IPlayerWizardDao extends IBaseDao<PlayerWizard>
{
    PlayerWizard read(final int p0);
    
    PlayerWizard readForUpdate(final int p0);
    
    List<PlayerWizard> getModels();
    
    int getModelSize();
    
    int create(final PlayerWizard p0);
    
    int deleteById(final int p0);
    
    List<PlayerWizard> getListByPlayerId(final int p0);
    
    PlayerWizard getByPlayerIdWizardId(final int p0, final int p1);
    
    int updateFlag(final Integer p0, final int p1);
    
    int updateLevel(final Integer p0, final int p1);
    
    int updateExtraPicked(final Integer p0, final int p1);
    
    int resetAllWiazrd();
    
    int gainPhantom(final Integer p0, final int p1, final String p2);
    
    int updateSuccTimeFlag(final Integer p0, final int p1, final Date p2, final int p3);
    
    int increaseTodayNum(final Integer p0, final int p1);
    
    List<PlayerWizard> getNeedRecoverList();
}
