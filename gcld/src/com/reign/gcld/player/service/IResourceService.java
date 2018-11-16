package com.reign.gcld.player.service;

import java.util.*;
import com.reign.gcld.player.dto.*;
import com.reign.util.*;

public interface IResourceService
{
    void output(final PlayerDto p0);
    
    List<ResourceDto> buildingOutputPerHour(final int p0);
    
    long getMax(final int p0, final int p1);
    
    Double getOutput(final int p0);
    
    Tuple<Boolean, String> canAddResource(final int p0, final int p1);
    
    void pushOutput(final int p0);
    
    void dealTroop(final PlayerDto p0, final long p1);
}
