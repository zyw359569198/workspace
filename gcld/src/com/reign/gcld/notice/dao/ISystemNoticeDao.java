package com.reign.gcld.notice.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.notice.domain.*;
import java.util.*;

public interface ISystemNoticeDao extends IBaseDao<SystemNotice>
{
    SystemNotice read(final int p0);
    
    SystemNotice readForUpdate(final int p0);
    
    List<SystemNotice> getModels();
    
    int getModelSize();
    
    int create(final SystemNotice p0);
    
    int deleteById(final int p0);
    
    void update(final SystemNotice p0);
    
    String getYxById(final int p0);
}
