package com.reign.gcld.general.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.general.domain.*;
import java.util.*;

public interface IPlayerGeneralMilitaryDao extends IBaseDao<PlayerGeneralMilitary>
{
    PlayerGeneralMilitary read(final int p0);
    
    PlayerGeneralMilitary readForUpdate(final int p0);
    
    List<PlayerGeneralMilitary> getModels();
    
    int getModelSize();
    
    int create(final PlayerGeneralMilitary p0);
    
    int deleteById(final int p0);
    
    PlayerGeneralMilitary getMilitary(final int p0, final int p1);
    
    List<PlayerGeneralMilitary> getMilitaryList(final int p0);
    
    List<PlayerGeneralMilitary> getMilitaryByState(final int p0, final int p1);
    
    int updateState(final int p0, final Date p1, final int p2, final int p3);
    
    int updateStateAuto(final int p0, final Date p1, final int p2, final int p3, final int p4);
    
    int updateState(final int p0, final int p1);
    
    int updateAllState();
    
    int updateStateByPidAndGid(final int p0, final int p1, final int p2, final Date p3);
    
    int updateByLocationAndState3(final int p0, final int p1, final Date p2);
    
    int updateStateCheck(final int p0, final int p1, final int p2);
    
    int updateAutoRecruit(final int p0, final int p1);
    
    int updateStateCityCheck(final int p0, final int p1, final int p2, final int p3);
    
    int getMilitaryNum(final int p0);
    
    int updateExpAndGlv(final int p0, final int p1, final int p2, final int p3);
    
    int addExp(final int p0, final int p1, final int p2);
    
    int move(final int p0, final int p1, final int p2, final int p3);
    
    int restartRecruit(final int p0, final int p1, final int p2, final Date p3);
    
    List<PlayerGeneralMilitary> getMilitaryByLocationId(final int p0);
    
    List<PlayerGeneralMilitary> getByLocationAndState3(final int p0);
    
    int updateStateLocationId(final int p0, final int p1, final int p2, final int p3);
    
    int countMilitaryByState(final int p0, final int p1);
    
    int updateLocationId(final int p0, final int p1, final int p2);
    
    List<PlayerGeneralMilitary> getMilitaryListOrder(final int p0);
    
    int updateAuto(final int p0, final int p1, final int p2);
    
    int addLeader(final int p0, final int p1, final int p2);
    
    int addStrength(final int p0, final int p1, final int p2);
    
    Map<Integer, PlayerGeneralMilitary> getMilitaryMap(final int p0);
    
    int updateGlv(final int p0, final int p1);
    
    int SetGlv(final int p0, final int p1);
    
    int addLeaderAndStrength(final int p0, final int p1, final int p2, final int p3);
    
    int consumeLeaderAndStrength(final int p0, final int p1, final int p2, final int p3);
    
    List<PlayerGeneralMilitary> getGeneralsForFollow(final int p0);
    
    int updateTacticEffect(final int p0, final int p1, final int p2);
    
    int resetTacticEffect(final int p0);
    
    int attack(final int p0, final int p1, final int p2, final int p3);
    
    Map<Integer, Object> getCityGenrealNum();
    
    List<PlayerGeneralMilitary> getMilitaryByLocationIdOrderByPlayerIdLvDesc(final int p0);
    
    PlayerGeneralMilitary getMaxLvMilitary(final int p0);
    
    void updateLvAndLeader(final int p0, final int p1);
    
    int updateJuBenLocation(final int p0, final int p1, final int p2);
    
    int moveJuben(final int p0, final int p1, final int p2, final int p3);
    
    int getGeneralNumInCity(final int p0, final int p1);
    
    int updateLocationByforceIdAndLocationId(final int p0);
    
    int deleteByPlayerId(final int p0);
    
    int consumeForces(final int p0, final int p1, final double p2, final Date p3);
    
    int consumeForcesByState(final int p0, final int p1, final double p2, final Date p3);
    
    int consumeCityForces(final int p0, final double p1, final Date p2);
    
    int consumeForcesSetState1(final int p0, final int p1, final double p2, final Date p3);
    
    int updateLocationForceSetState1(final int p0, final int p1, final int p2, final double p3, final Date p4);
    
    int upJuBenLocationForceSetState1(final int p0, final int p1, final int p2, final double p3, final Date p4);
    
    int addPlayerForces(final int p0, final double p1);
    
    int addGeneralForces(final int p0, final int p1, final Date p2, final int p3, final long p4);
    
    int addGeneralForces2(final int p0, final int p1, final long p2);
    
    int setPlayerForces(final int p0, final int p1);
    
    int updateForcesDate(final int p0, final int p1, final Date p2, final double p3, final long p4);
    
    int resetForces(final int p0, final int p1, final Date p2, final double p3);
}
