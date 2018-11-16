package com.reign.kfgz.dao;

import com.reign.framework.hibernate.dao.*;
import com.reign.kfgz.domain.*;
import com.reign.kfgz.dto.request.*;
import java.util.*;

public interface IKfgzPlayerRankingInfoDao extends IBaseDao<KfgzPlayerRankingInfo, Integer>
{
    KfgzPlayerRankingInfo getInfoByCIdAndSeasonId(final int p0, final int p1);
    
    List<KfgzPlayerRankingInfo> getOrderedKillArmyInfoByLayerId(final int p0, final int p1);
    
    List<KfgzPlayerRankingInfo> getOrderedSoloInfoByLayerId(final int p0, final int p1);
    
    List<KfgzPlayerRankingInfo> getOrderedOccupyCityoInfoByLayerId(final int p0, final int p1);
    
    List<KfgzPlayerRankingInfo> getOrderedOccupyCityoInfoByLayerIdAndGId(final int p0, final int p1, final int p2);
    
    List<KfgzPlayerRankingInfo> getOrderedKillerArmyInfoByLayerIdAndGId(final int p0, final int p1, final int p2);
    
    List<KfgzPlayerRankingInfo> getOrderedSoloInfoByLayerIdAndGId(final int p0, final int p1, final int p2);
    
    KfgzPlayerRankingInfo getTopPlayerKillerRankingInfo(final int p0, final int p1, final int p2, final KfgzNationResultReq p3, final String p4, final int p5);
    
    Map<Integer, KfgzPlayerRankingInfo> getInfoMapBySeasonIdLayerAndGId(final int p0, final int p1, final int p2);
    
    KfgzPlayerRankingInfo getTopPlayerKillerRankingInfo(final int p0, final int p1, final KfgzNationResultReq p2, final String p3, final int p4);
}
