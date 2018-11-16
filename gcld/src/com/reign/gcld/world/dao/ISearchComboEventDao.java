package com.reign.gcld.world.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.world.domain.*;
import java.util.*;

public interface ISearchComboEventDao extends IBaseDao<SearchComboEvent>
{
    SearchComboEvent read(final int p0);
    
    SearchComboEvent readForUpdate(final int p0);
    
    List<SearchComboEvent> getModels();
    
    int getModelSize();
    
    int create(final SearchComboEvent p0);
    
    int deleteById(final int p0);
    
    void updateInfo(final int p0, final String p1, final int p2);
    
    void revealEvent(final int p0);
}
