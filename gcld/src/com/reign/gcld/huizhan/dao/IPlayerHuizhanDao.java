package com.reign.gcld.huizhan.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.huizhan.domain.*;
import java.util.*;

public interface IPlayerHuizhanDao extends IBaseDao<PlayerHuizhan>
{
    PlayerHuizhan read(final int p0);
    
    PlayerHuizhan readForUpdate(final int p0);
    
    List<PlayerHuizhan> getModels();
    
    int getModelSize();
    
    int create(final PlayerHuizhan p0);
    
    int deleteById(final int p0);
    
    PlayerHuizhan getByhuiZhanIdAndplayerId(final int p0, final int p1);
    
    int updateForceByhzIdAndPlayerId(final int p0, final int p1, final int p2);
    
    int addPhantomNumByhzIdAndPlayerId(final int p0, final int p1);
    
    List<PlayerHuizhan> getUnReceivedRewardByPlayerIdOrderByJoinTimeDesc(final int p0);
    
    List<PlayerHuizhan> getByhzIdOrderByForceDesc(final int p0);
    
    List<PlayerHuizhan> getByhzIdAndForceId(final int p0, final int p1);
    
    PlayerHuizhan getByhzIdAndPlayerId(final int p0, final int p1);
    
    int updateKillNumByhzIdAndPlayerId(final int p0, final int p1, final int p2);
    
    List<PlayerHuizhan> getUnReceivedRewardPlayerHuizhan();
    
    int addPKNumByhzIdAndPlayerId(final int p0, final int p1);
    
    int updateAwardFlagByVid(final int p0, final int p1);
    
    int deleteByHzId(final int p0);
}
