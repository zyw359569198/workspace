package com.reign.gcld.battle.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.battle.domain.*;
import java.util.*;

public interface IOfficerBuildingInfoDao extends IBaseDao<OfficerBuildingInfo>
{
    OfficerBuildingInfo read(final int p0);
    
    OfficerBuildingInfo readForUpdate(final int p0);
    
    List<OfficerBuildingInfo> getModels();
    
    int getModelSize();
    
    int create(final OfficerBuildingInfo p0);
    
    int deleteById(final int p0);
    
    OfficerBuildingInfo getByBuildingId(final int p0, final int p1);
    
    int updateState(final int p0, final int p1, final int p2);
    
    int updateInfo(final OfficerBuildingInfo p0);
    
    int updatePlayerId(final int p0, final int p1);
    
    void addMemberNum(final int p0, final int p1);
    
    void minuseMemberNum(final int p0, final int p1);
    
    int update(final int p0, final int p1, final int p2, final int p3);
    
    OfficerBuildingInfo getByPlayerId(final int p0);
    
    int updateAutoPass(final int p0, final int p1, final int p2);
    
    List<OfficerBuildingInfo> getByForceId(final int p0);
    
    int deleteByPlayerId(final int p0);
}
