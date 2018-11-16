package com.reign.gcld.player.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.player.domain.*;
import java.util.*;

public interface IPlayerOfficeRelativeDao extends IBaseDao<PlayerOfficeRelative>
{
    PlayerOfficeRelative read(final int p0);
    
    PlayerOfficeRelative readForUpdate(final int p0);
    
    List<PlayerOfficeRelative> getModels();
    
    int getModelSize();
    
    int create(final PlayerOfficeRelative p0);
    
    int deleteById(final int p0);
    
    int updateReputationTime(final int p0, final Date p1, final int p2);
    
    List<PlayerOfficeRelative> getListByOfficerId(final int p0);
    
    List<PlayerOfficeRelative> getListByReputationTime();
    
    int updateSalaryGot(final int p0, final int p1);
    
    List<PlayerOfficeRelative> getListOtherOfficerId(final int p0, final int p1, final int p2);
    
    int updateOfficerId(final int p0, final int p1);
    
    int updateOfficerNpc(final int p0, final int p1);
    
    int updateHighestOfficer(final int p0, final int p1);
    
    int getOfficerId(final int p0);
}
