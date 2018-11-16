package com.reign.gcld.antiaddiction;

import com.reign.gcld.user.dto.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.job.service.*;
import com.reign.framework.json.*;
import com.reign.gcld.common.*;

public class AntiAddictionStateIntervalNoneEarnings extends AntiAddictionStateInterval
{
    private long changeSelfInterval;
    
    public AntiAddictionStateIntervalNoneEarnings(final long min, final long max, final int level, final long changeSelfInterval) {
        super(min, max, level);
        this.changeSelfInterval = changeSelfInterval;
    }
    
    @Override
    public long getIntDataAfterAntiAddiction(final long val) {
        return 0L;
    }
    
    @Override
    public void update(final UserDto userDto, final PlayerDto playerDto, final IJobService jobService) {
        final long onlineTime = userDto.getOnlineTime();
        if (!this.isInterval(onlineTime)) {
            userDto.getAntiAddictionStateMachine().changeState(jobService);
        }
        else if (onlineTime - userDto.getEnterAntiAddictionStateTime() > this.changeSelfInterval) {
            userDto.getAntiAddictionStateMachine().changeState(this, jobService);
        }
    }
    
    @Override
    public String getAddictionLoseLevel() {
        return "full";
    }
    
    @Override
    public void enter(final UserDto userDto, final PlayerDto playerDto, final IJobService jobService) {
        final JsonDocument doc = new JsonDocument();
        final int showLevel = this.getAntiAddictionLevel();
        doc.startObject();
        doc.createElement("addictionState", showLevel);
        doc.endObject();
        Players.push(playerDto.playerId, PushCommand.PUSH_ANTIADDICTION, doc.toByte());
        final long nextUpdateTime = System.currentTimeMillis() + this.changeSelfInterval;
        jobService.addJob("antiAddictionService", "updateState", String.valueOf(playerDto.playerId), nextUpdateTime, false);
    }
}
