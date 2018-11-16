package com.reign.gcld.civiltrick.service;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.util.*;
import java.util.*;
import com.reign.gcld.juben.common.*;
import com.reign.gcld.team.service.*;

public interface ICilvilTrickService
{
    byte[] getPitchLocation(final int p0, final int p1, final int p2);
    
    byte[] useTrick(final PlayerDto p0, final int p1, final int p2, final int p3, final int p4, final int p5);
    
    Stratagem afterStateTrick(final int p0, final int p1, final int p2);
    
    Tuple<Integer, Stratagem> afterTrapTrick(final int p0, final int p1);
    
    void removeTrap(final int p0, final Map<String, List<Integer>> p1);
    
    Tuple<Double, Double> afterStateTrick2(final int p0, final int p1, final int p2);
    
    Tuple<List<Stratagem>, List<Stratagem>> getStateList(final int p0, final int p1, final int p2);
    
    byte[] useTrickInScenario(final PlayerDto p0, final int p1, final int p2, final int p3, final int p4, final int p5);
    
    void useTrickForAction(final int p0, final int p1, final int p2, final int p3);
    
    OperationResult hasTrick(final JuBenCityDto p0);
    
    void updateTrickInfo(final int p0, final int p1, final Stratagem p2);
}
