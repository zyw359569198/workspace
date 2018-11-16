package com.reign.gcld.building.service;

import com.reign.gcld.common.*;
import com.reign.util.*;
import com.reign.gcld.player.dto.*;
import java.util.*;
import com.reign.gcld.building.domain.*;
import com.reign.gcld.sdata.domain.*;

public interface IBuildingService
{
    void createBuilding(final int p0, final int p1, final int p2);
    
    void assignedBuildingWork(final int p0);
    
    CallBack doUpgrade(final String p0);
    
    Tuple<Integer, byte[]> upgradeBuilding(final int p0, final int p1, final boolean p2);
    
    byte[] getBuildingInfo(final int p0, final int p1);
    
    byte[] cdRecover(final int p0, final int p1);
    
    Tuple<Boolean, Object> cdRecoverConfirm(final int p0, final int p1);
    
    Tuple<Integer, byte[]> cdSpeedUp(final int p0, final int p1);
    
    byte[] cdRecoverConfirmCallBack(final CallBack p0);
    
    byte[] getMainCity(final int p0);
    
    byte[] startAutoUpBuilding(final PlayerDto p0, final int p1);
    
    byte[] stopAutoUpBuilding(final int p0);
    
    byte[] getAutoUpbuilding(final int p0);
    
    Tuple<Boolean, byte[]> addBuildingAddition(final PlayerDto p0, final int p1, final int p2, final int p3);
    
    byte[] getBuildingAdditionPrice(final int p0, final int p1, final int p2);
    
    Tuple<Integer, Chargeitem> getResourceAddition(final int p0, final int p1, final int p2);
    
    void clearBuildingsOutputAddition(final String p0);
    
    void constructionComplete(final int p0);
    
    byte[] freeCdRecoverConfirm(final int p0);
    
    byte[] freeCdRecover(final int p0);
    
    void initPlayerBuilding(final int p0);
    
    void reloadBuilding(final int p0, final int p1);
    
    void openFreeConstruction(final int p0);
    
    List<PlayerBuilding> getPlayerBuildings(final int p0, final int p1);
    
    PlayerBuilding getPlayerBuilding(final int p0, final int p1);
    
    List<PlayerBuilding> getPlayerBuildingByType(final int p0, final int p1);
    
    List<PlayerBuilding> getPlayerBuildingWithoutEvent(final int p0);
    
    List<PlayerBuilding> getPlayerBuildingWithoutEvent2(final int p0, final int p1);
    
    PlayerBuilding getNextBuildingWithEvent(final int p0, final int p1);
    
    void updateEventId(final PlayerBuilding p0, final int p1);
    
    void openLumberArea(final int p0);
    
    void openCopperArea(final int p0);
    
    boolean dropBluePrintById(final int p0, final int p1);
    
    BuildingDrawing dropBluePrintByType(final int p0, final int p1);
    
    boolean buyBluePrintById(final int p0, final int p1);
    
    byte[] killBandit(final int p0, final int p1);
    
    byte[] killKidnapper(final int p0, final int p1);
    
    byte[] openBluePrint(final int p0, final int p1);
    
    byte[] consBluePrint(final int p0, final int p1);
    
    byte[] consCdRecover(final int p0, final int p1);
    
    byte[] consCdRecoverConfirm(final int p0, final int p1);
    
    void finishConsBluePrint(final String p0);
    
    int getId(final int p0, final int p1);
    
    Tuple<Boolean, String> addBuildingAdditionForFree(final PlayerDto p0, final int p1, final int p2, final int p3);
    
    void initResourceAdditionTimeJob();
    
    byte[] useFeatBuilding(final PlayerDto p0);
}
