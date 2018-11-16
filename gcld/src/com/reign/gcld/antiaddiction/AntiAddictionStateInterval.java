package com.reign.gcld.antiaddiction;

import com.reign.gcld.user.dto.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.job.service.*;
import com.reign.framework.json.*;
import com.reign.gcld.common.*;

public class AntiAddictionStateInterval implements IAntiAddictionState
{
    private long min;
    private long max;
    private int level;
    
    public AntiAddictionStateInterval(final long min, final long max, final int level) {
        this.min = min;
        this.max = max;
        this.level = level;
    }
    
    @Override
    public void enter(final UserDto userDto, final PlayerDto playerDto, final IJobService jobService) {
        final JsonDocument doc = new JsonDocument();
        final int showLevel = this.level;
        doc.startObject();
        doc.createElement("addictionState", showLevel);
        doc.endObject();
        Players.push(playerDto.playerId, PushCommand.PUSH_ANTIADDICTION, doc.toByte());
        final long nextUpdateTime = System.currentTimeMillis() + this.max - userDto.getOnlineTime();
        jobService.addJob("antiAddictionService", "updateState", String.valueOf(playerDto.playerId), nextUpdateTime, false);
    }
    
    @Override
    public void update(final UserDto userDto, final PlayerDto playerDto, final IJobService jobService) {
        if (!this.isInterval(userDto)) {
            userDto.getAntiAddictionStateMachine().changeState(jobService);
        }
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
        final long onlineTime = userDto.getOnlineTime();
        return onlineTime >= this.min && onlineTime < this.max;
    }
    
    @Override
    public int getAntiAddictionLevel() {
        return this.level;
    }
    
    protected boolean isInterval(final long onlineTime) {
        return onlineTime >= this.min && onlineTime < this.max;
    }
    
    @Override
    public String getAddictionLoseLevel() {
        return "";
    }
}
