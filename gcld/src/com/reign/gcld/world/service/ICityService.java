package com.reign.gcld.world.service;

import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.servlet.*;
import com.reign.util.*;
import java.util.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.rank.domain.*;
import com.reign.gcld.world.common.*;
import com.reign.framework.json.*;
import com.reign.gcld.world.domain.*;

public interface ICityService
{
    void initWorldCity();
    
    void initCityBattleId();
    
    byte[] leaveWorldScene(final PlayerDto p0, final int p1, final Request p2);
    
    byte[] enterWorldScene(final PlayerDto p0, final Request p1);
    
    byte[] attMoveInfo(final PlayerDto p0, final int p1);
    
    void sendAttMoveInfo(final int p0, final int p1, final int p2, final int p3, final int p4, final String p5, final long p6, final boolean p7);
    
    void sendCityUpdateInfo(final int p0, final Map<Integer, Integer> p1, final Map<Integer, Map<Integer, Integer>> p2, final Map<Integer, Map<Integer, Integer>> p3, final boolean p4);
    
    void changeForceIdAndState(final int p0, final int p1, final int p2, final int p3, final String p4);
    
    void changeState(final int p0, final int p1, final boolean p2);
    
    byte[] moveByCityName(final int p0, final String p1);
    
    byte[] autoMoveInfo(final PlayerDto p0, final int p1, final int p2);
    
    byte[] autoMove(final int p0, final int p1, final int p2, final int p3);
    
    byte[] autoMoveStop(final PlayerDto p0, final int p1);
    
    Tuple<Integer, String> move(final int p0, final int p1, final int p2, final boolean p3, final int p4);
    
    boolean startMove(final int p0, final int p1, final int p2, final boolean p3);
    
    void inAutoMove(final String p0);
    
    void changeState(final String p0);
    
    void updateGNumAndSend(final int p0, final boolean p1);
    
    void updateGNumAndSend(final int p0, final int p1);
    
    void initWorldRoadXml();
    
    void joinGroup(final int p0, final int p1, final Request p2);
    
    void leaveGroup(final int p0, final int p1, final Request p2);
    
    Tuple<Double, Double> getMilitariesCost(final List<PlayerGeneralMilitary> p0, final int p1, final int p2, final int p3, final int p4);
    
    void dealTrap(final Player p0, final PlayerGeneralMilitary p1, final int p2);
    
    void stateJob(final String p0);
    
    void updateTitle(final int p0, final int p1);
    
    String getColoredGeneralName(final int p0);
    
    byte[] pleaseGiveMeAReply(final PlayerDto p0, final int p1);
    
    Tuple<Boolean, String> assembleMove(final int p0, final int p1, final int p2, final int p3);
    
    byte[] moveStop(final int p0);
    
    void clearGeneralsMove(final int p0);
    
    byte[] getCityDetailInfo(final int p0, final PlayerDto p1);
    
    byte[] getManzuShoumaiInfo(final PlayerDto p0);
    
    byte[] manzuShoumai(final PlayerDto p0, final int p1);
    
    byte[] faDongmanzu(final PlayerDto p0, final int p1);
    
    void initCountryPrivilege();
    
    byte[] coverManzuShoumaiCd(final PlayerDto p0);
    
    byte[] getCoverManzuShoumaiCdCost(final PlayerDto p0);
    
    ManZuShouMaiDetail getManZuShouMaiDetail(final ForceInfo p0, final int p1);
    
    byte[] getCityEventPanel(final PlayerDto p0, final int p1);
    
    byte[] dealCityEvent(final PlayerDto p0, final int p1, final int p2);
    
    byte[] getPlayerEventPanel(final PlayerDto p0, final int p1);
    
    byte[] dealPlayerEvent(final PlayerDto p0, final int p1, final int p2);
    
    void getPlayerEventInfo(final JsonDocument p0, final City p1, final int p2);
    
    void addPlayerEvent(final String p0);
    
    void getWholeKillTitle(final int p0, final int p1, final JsonDocument p2, final boolean p3);
    
    void clearPlayerEventPerDay();
    
    void addPlayerEventPerDay();
    
    void initResetCityBattleAndCheckCityPGMState();
    
    Map<Integer, Long> getShaDiLingInfoInThisCity(final int p0);
    
    void removeExpiredShaDiLingInfoInThisCity(final String p0);
    
    Long updateShaDiLingInfoInThisCity(final int p0, final int p1);
    
    Map<Integer, Long> getShaDiLingInfoInThisCity(final City p0);
    
    void addPlayerNextEvent(final int p0, final int p1, final Integer p2, final boolean p3);
    
    byte[] getFarmCityInfo(final PlayerDto p0);
    
    void initPlayerWorld();
}
