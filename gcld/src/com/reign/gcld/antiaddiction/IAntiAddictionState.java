package com.reign.gcld.antiaddiction;

import com.reign.gcld.user.dto.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.job.service.*;

public interface IAntiAddictionState
{
    void enter(final UserDto p0, final PlayerDto p1, final IJobService p2);
    
    void update(final UserDto p0, final PlayerDto p1, final IJobService p2);
    
    void exit(final UserDto p0, final PlayerDto p1);
    
    long getIntDataAfterAntiAddiction(final long p0);
    
    boolean isInterval(final UserDto p0);
    
    int getAntiAddictionLevel();
    
    String getAddictionLoseLevel();
}
