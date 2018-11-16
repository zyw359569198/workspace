package com.reign.gcld.general.service;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.general.domain.*;
import java.util.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.framework.json.*;

public interface IGeneralService
{
    byte[] getGeneralInfo(final PlayerDto p0);
    
    byte[] getCivils(final PlayerDto p0, final int p1);
    
    byte[] getGeneralSimpleInfo(final PlayerDto p0);
    
    byte[] startRecruitForces(final PlayerDto p0, final int p1);
    
    byte[] stopRecruitForces(final PlayerDto p0, final int p1, final int p2);
    
    byte[] fireGeneral(final int p0, final int p1, final int p2);
    
    byte[] getGeneral(final int p0, final int p1);
    
    void sendGeneralMilitaryRecruitInfo(final Player p0, final PlayerGeneralMilitary p1, final boolean p2);
    
    void sendGeneralCivilRecruitInfo(final int p0, final int p1, final PlayerGeneralCivil p2, final boolean p3);
    
    void sendGeneralSizeInfo(final int p0, final int p1);
    
    List<UpdateExp> updateExpAndGeneralLevel(final int p0, final int p1, final int p2);
    
    void sendGeneralMilitaryRecruitInfo(final int p0, final int p1);
    
    void sendGeneralMilitaryRecruitInfo(final int p0, final PlayerGeneralMilitary p1);
    
    void sendGeneralMilitaryRecruitInfo(final PlayerDto p0);
    
    void sendGeneralMilitaryRecruitInfo(final int p0, final boolean p1);
    
    void sendGmForcesReduce(final int p0, final int p1, final int p2, final int p3, final boolean p4);
    
    void sendGmForcesSet(final int p0, final Map<Integer, Long> p1);
    
    void sendGmStateSet(final int p0, final int p1, final int p2);
    
    void sendGmForcesReduce(final int p0, final int p1, final int p2, final int p3, final boolean p4, final String p5, final String p6, final int p7);
    
    void sendGmStateAndLvSet(final int p0, final Map<Integer, MilitaryDto> p1, final boolean p2);
    
    void sendGmForcesReduceUpdateState(final int p0, final Map<Integer, MilitaryDto> p1, final boolean p2);
    
    void sendGmCityStateSet(final int p0, final int p1, final long p2, final int p3, final int p4, final int p5, final int p6);
    
    void sendGmStateLocationIdSet(final int p0, final int p1, final int p2, final int p3);
    
    byte[] cdRecoverConfirm(final PlayerDto p0, final int p1, final int p2);
    
    byte[] getCivilInfo(final PlayerDto p0, final int p1);
    
    byte[] cdRecover(final int p0, final int p1);
    
    byte[] autoRecruit(final int p0, final int p1, final int p2);
    
    Double getOutput(final int p0, final int p1, final int p2, final Troop p3);
    
    int getRate(final int p0, final WorldCity p1);
    
    byte[] getGeneralTreasureInfo(final int p0, final int p1, final int p2, final int p3);
    
    byte[] changeGeneralTreasure(final int p0, final int p1, final int p2, final int p3, final int p4);
    
    void sendGmUpdate(final int p0, final int p1, final boolean p2);
    
    void sendGeneralMaxSizeInfo(final int p0, final int p1, final int p2);
    
    void sendGeneralMilitaryList(final int p0);
    
    double getRecuitConsume(final int p0, final int p1, final WorldCity p2);
    
    void sendGenerlMoveInfo(final int p0, final int p1);
    
    void sendGenerlJuBenMoveInfo(final int p0, final int p1);
    
    void getGeneralPortait(final int p0, final JsonDocument p1, final Map<Integer, Boolean> p2);
    
    void sendGmUpdate1(final int p0, final int p1, final boolean p2);
    
    byte[] changAllEquip(final PlayerDto p0, final int p1, final int p2);
    
    void getGeneralInfoForGoldOrder(final int p0, final JsonDocument p1);
}
