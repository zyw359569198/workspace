package com.reign.gcld.general.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.general.domain.*;
import java.util.*;

public interface IPlayerGeneralCivilDao extends IBaseDao<PlayerGeneralCivil>
{
    PlayerGeneralCivil read(final int p0);
    
    PlayerGeneralCivil readForUpdate(final int p0);
    
    List<PlayerGeneralCivil> getModels();
    
    int getModelSize();
    
    int create(final PlayerGeneralCivil p0);
    
    int deleteById(final int p0);
    
    PlayerGeneralCivil getCivil(final int p0, final int p1);
    
    List<PlayerGeneralCivil> getCivilList(final int p0);
    
    List<PlayerGeneralCivil> getCivilListOrderByLv(final int p0);
    
    List<PlayerGeneralCivil> getCivilAdviser(final int p0);
    
    int getCivilNum(final int p0);
    
    int updateUpTime(final int p0, final Date p1);
    
    void search(final int p0, final int p1, final int p2, final Date p3, final int p4);
    
    int updateState(final int p0, final int p1, final int p2);
    
    void updateMoveTime(final int p0, final Date p1);
    
    int updateExpAndGlv(final int p0, final int p1, final int p2, final int p3);
    
    int addExp(final int p0, final int p1, final int p2);
    
    int addIntel(final int p0, final int p1, final int p2);
    
    int addPolitics(final int p0, final int p1, final int p2);
    
    int getCivilOwnerNum(final int p0);
    
    int addIntelAndPolitics(final int p0, final int p1, final int p2, final int p3);
    
    int consumeIntelAndPolitics(final int p0, final int p1, final int p2, final int p3);
    
    int setCivilLv(final int p0, final int p1);
    
    int addIntelCareMax(final int p0, final int p1, final int p2, final int p3);
    
    int addPoliticsCareMax(final int p0, final int p1, final int p2, final int p3);
    
    void updateCd(final int p0, final int p1, final Date p2);
}
