package com.reign.kfzb.dao;

import com.reign.framework.hibernate.dao.*;
import com.reign.kfzb.domain.*;
import java.util.*;

public interface IKfzbRuntimeMatchDao extends IBaseDao<KfzbRuntimeMatch, Integer>
{
    Integer getMinLayerBySeasonId(final int p0);
    
    List<KfzbRuntimeMatch> getMatchBylayerAndSeasonId(final int p0, final int p1);
    
    List<KfzbRuntimeMatch> getMatchBylayerRoundAndSeasonId(final int p0, final int p1, final int p2);
    
    List<KfzbRuntimeMatch> getUnfinishMatchByLayerAndSeasonId(final int p0, final int p1);
    
    List<KfzbRuntimeMatch> getAllRound1Match(final int p0);
    
    List<KfzbRuntimeMatch> getAllMatch(final int p0);
    
    List<KfzbRuntimeMatch> getCampains();
    
    KfzbRuntimeMatch getLastWinMatch(final int p0, final int p1);
    
    List<KfzbRuntimeMatch> getPhase2BattleInfo(final int p0);
}
