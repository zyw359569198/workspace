package com.reign.gcld.juben.service;

import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.servlet.*;
import com.reign.util.*;
import com.reign.gcld.juben.common.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.general.domain.*;
import java.util.*;
import com.reign.gcld.scenario.common.*;
import com.reign.gcld.battle.scene.*;
import com.reign.gcld.team.service.*;

public interface IJuBenService
{
    byte[] enterJuBenScene(final PlayerDto p0, final int p1, final int p2, final int p3, final Request p4);
    
    byte[] getJuBenScene(final PlayerDto p0, final int p1, final int p2, final Request p3);
    
    byte[] juBenPermit(final PlayerDto p0, final int p1, final int p2, final int p3, final Request p4);
    
    void initMiniWinTime();
    
    byte[] getJuBenReward(final PlayerDto p0);
    
    byte[] enterJuBenQuick(final PlayerDto p0, final Request p1);
    
    byte[] getJuBenList(final PlayerDto p0, final Request p1);
    
    byte[] autoMoveJuBen(final int p0, final int p1, final int p2);
    
    Tuple<Integer, String> move(final int p0, final int p1, final int p2, final boolean p3);
    
    void sendAttMoveInfo(final int p0, final int p1, final int p2, final int p3, final int p4, final String p5, final long p6, final boolean p7);
    
    void changeForceIdAndState(final int p0, final int p1, final int p2, final int p3, final String p4);
    
    void changeState(final String p0);
    
    void changeState(final int p0, final int p1, final int p2, final boolean p3);
    
    void juBenDeal(final String p0);
    
    void juBenOver(final int p0, final JuBenDto p1, final boolean p2);
    
    boolean startMove(final int p0, final int p1, final int p2, final boolean p3);
    
    byte[] pleaseGiveMeAReply(final PlayerDto p0, final int p1);
    
    Tuple<Boolean, String> assembleMove(final int p0, final int p1, final int p2, final int p3);
    
    byte[] autoMoveJuBenStop(final PlayerDto p0, final int p1);
    
    byte[] quitJuBen(final PlayerDto p0);
    
    byte[] getChoiceInfo(final PlayerDto p0, final int p1);
    
    byte[] getJuBenCityInfo(final int p0, final PlayerDto p1);
    
    void initAllJuBenIngToCache();
    
    void saveEventInfo(final int p0);
    
    void dealTrap(final Player p0, final PlayerGeneralMilitary p1, final int p2);
    
    boolean openNextJuBen(final int p0, final int p1);
    
    void openJuBenNextStar(final int p0, final int p1, final int p2);
    
    void addEndTime(final int p0, final long p1);
    
    byte[] makeAChoice(final int p0, final int p1, final int p2);
    
    void addNpcToCity(final int p0, final int p1, final List<Tuple<Integer, Integer>> p2, final int p3);
    
    void makeAChoiceToTriggerOperation(final int p0, final int p1);
    
    void killAllPlayerPgmsInThisCity(final int p0, final int p1);
    
    void killAllNpcsInThisCity(final int p0, final int p1);
    
    int trickReduceForce(final int p0, final int p1, final int p2, final int p3);
    
    ScenarioEvent getEventByCityId(final int p0, final int p1);
    
    void setJubenBuff(final int p0, final double p1, final long p2, final int p3);
    
    void removeNpcInCity(final int p0, final Integer p1, final int p2, final int p3, final int p4);
    
    void setJubenAttDefBaseBuff(final int p0, final int p1, final int p2, final long p3, final int p4);
    
    void initScenarioNpcVid();
    
    void checkRoyalJadeRobbed(final Battle p0);
    
    void checkJadeTimeOver(final JuBenDto p0);
    
    void checkWorldDramaOpen(final int p0, final int p1, final boolean p2);
    
    OperationResult enterWorldDramaScene(final PlayerDto p0, final int p1, final Integer p2);
    
    boolean isInWorldDrama(final int p0);
}
