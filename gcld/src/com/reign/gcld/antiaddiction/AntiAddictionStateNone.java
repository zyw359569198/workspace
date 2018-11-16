package com.reign.gcld.antiaddiction;

import com.reign.gcld.user.dto.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.job.service.*;

public class AntiAddictionStateNone implements IAntiAddictionState
{
    @Override
    public void enter(final UserDto userDto, final PlayerDto playerDto, final IJobService jobService) {
    }
    
    @Override
    public void update(final UserDto userDto, final PlayerDto playerDto, final IJobService jobService) {
    }
    
    @Override
    public void exit(final UserDto userDto, final PlayerDto playerDto) {
    }
    
    @Override
    public long getIntDataAfterAntiAddiction(final long val) {
        return val;
    }
    
    @Override
    public boolean isInterval(final UserDto userDto) {
        return !userDto.isNeedAntiAddiction();
    }
    
    @Override
    public int getAntiAddictionLevel() {
        return 0;
    }
    
    @Override
    public String getAddictionLoseLevel() {
        return "";
    }
}
