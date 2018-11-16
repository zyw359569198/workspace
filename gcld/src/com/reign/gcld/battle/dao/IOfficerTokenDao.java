package com.reign.gcld.battle.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.battle.domain.*;
import java.util.*;

public interface IOfficerTokenDao extends IBaseDao<OfficerToken>
{
    OfficerToken read(final int p0);
    
    OfficerToken readForUpdate(final int p0);
    
    List<OfficerToken> getModels();
    
    int getModelSize();
    
    int create(final OfficerToken p0);
    
    int deleteById(final int p0);
    
    OfficerToken getTokenByForceIdAndOfficerId(final int p0, final int p1);
    
    void addTokenTimer();
    
    void resetBattle(final int p0, final int p1);
    
    void decreaseKillTokenLimited(final int p0, final int p1);
    
    void updateTokenInfo(final int p0, final int p1, final String p2);
    
    void addKillTokenLimited(final int p0, final int p1);
    
    void setTokenNum(final int p0, final int p1, final int p2);
}
