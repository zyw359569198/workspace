package com.reign.gcld.rank.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.rank.domain.*;
import java.util.*;

public interface IPlayerChallengeInfoDao extends IBaseDao<PlayerChallengeInfo>
{
    PlayerChallengeInfo read(final int p0);
    
    PlayerChallengeInfo readForUpdate(final int p0);
    
    List<PlayerChallengeInfo> getModels();
    
    int getModelSize();
    
    int create(final PlayerChallengeInfo p0);
    
    int deleteById(final int p0);
    
    int getByPlayerId(final int p0);
    
    List<PlayerChallengeInfo> getListByPlayerIdOrderByNum(final int p0);
    
    int updateVtimes(final int p0, final int p1, final int p2);
    
    PlayerChallengeInfo getInfoByPAndG(final int p0, final int p1);
}
