package com.reign.gcld.battle.service;

import com.reign.gcld.player.dto.*;
import java.util.*;
import com.reign.util.*;

public interface IOccupyService
{
    byte[] getAllOfficerBuilding(final PlayerDto p0);
    
    byte[] getRankInfo(final PlayerDto p0, final int p1);
    
    byte[] getOperation(final PlayerDto p0, final int p1);
    
    byte[] applyBuilding(final PlayerDto p0, final int p1);
    
    byte[] quitBuilding(final PlayerDto p0, final int p1);
    
    byte[] getApplyList(final int p0, final PlayerDto p1);
    
    byte[] passApply(final PlayerDto p0, final int p1);
    
    byte[] refuseApply(final PlayerDto p0, final int p1);
    
    Map<Integer, String> handleOfficerAfterBattle(final int p0, final int p1, final Map<Integer, Integer> p2);
    
    byte[] kickMember(final PlayerDto p0, final int p1);
    
    byte[] changeAutoPass(final PlayerDto p0, final int p1);
    
    Tuple<Boolean, Boolean> hasApply(final int p0);
    
    byte[] getSalary(final PlayerDto p0);
    
    void disbandFollowTeam(final int p0);
    
    void onUpdateOfficerId(final int p0, final int p1);
    
    void addFreePhantom();
}
