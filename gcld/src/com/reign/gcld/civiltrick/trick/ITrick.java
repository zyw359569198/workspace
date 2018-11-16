package com.reign.gcld.civiltrick.trick;

import com.reign.gcld.common.log.*;
import com.reign.gcld.common.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.player.dto.*;
import java.util.*;
import com.reign.gcld.juben.common.*;

public interface ITrick
{
    public static final Logger testLogger = CommonLog.getLog(CilvilProtectTrick.class);
    public static final Logger errorLogger = CommonLog.getLog(CilvilProtectTrick.class);
    public static final int FORSES = 3;
    public static final int STATE_INSPIRE = 1;
    public static final int STATE_SHRINK = 2;
    public static final int TRAP = 4;
    public static final int PROTECT = 5;
    public static final int LIES = 6;
    
    byte[] getPitchLocation(final IDataGetter p0, final int p1, final List<PlayerGeneralMilitary> p2);
    
    byte[] useTrick(final IDataGetter p0, final PlayerDto p1, final int p2, final int p3, final int p4, final int p5, final int p6);
    
    byte[] useTrickInScenario(final IDataGetter p0, final PlayerDto p1, final int p2, final int p3, final int p4, final int p5, final int p6, final Map<Integer, JuBenCityDto> p7, final int p8);
    
    void useTrickNpc(final int p0, final int p1, final IDataGetter p2, final int p3);
}
