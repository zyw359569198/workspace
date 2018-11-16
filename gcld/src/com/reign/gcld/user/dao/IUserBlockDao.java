package com.reign.gcld.user.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.user.domain.*;
import java.util.*;

public interface IUserBlockDao extends IBaseDao<UserBlock>
{
    UserBlock read(final int p0);
    
    UserBlock readForUpdate(final int p0);
    
    List<UserBlock> getModels();
    
    int getModelSize();
    
    int create(final UserBlock p0);
    
    int deleteById(final int p0);
    
    List<UserBlock> getUserBlock(final String p0, final String p1);
    
    void update(final int p0, final String p1, final Date p2);
    
    List<UserBlock> getUserBanListByDateAndYx(final Date p0, final String p1);
}
