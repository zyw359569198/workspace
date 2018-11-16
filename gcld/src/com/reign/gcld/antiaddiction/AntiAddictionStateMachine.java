package com.reign.gcld.antiaddiction;

import com.reign.gcld.user.dto.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.job.service.*;

public class AntiAddictionStateMachine
{
    private UserDto owner;
    private PlayerDto playerDto;
    private IAntiAddictionState currentState;
    
    public AntiAddictionStateMachine(final UserDto owner) {
        this.owner = owner;
        this.currentState = AntiAddictionStateFactory.getInstance().getDefaultState();
    }
    
    public void setCurrentState(final IAntiAddictionState state) {
        this.owner.setEnterAntiAddictionStateTime(this.owner.getOnlineTime());
        this.currentState = state;
    }
    
    public IAntiAddictionState getCurrentState() {
        return this.currentState;
    }
    
    public void changeState(final IAntiAddictionState state, final IJobService jobService) {
        this.currentState.exit(this.owner, this.playerDto);
        this.owner.setEnterAntiAddictionStateTime(this.owner.getOnlineTime());
        (this.currentState = state).enter(this.owner, this.playerDto, jobService);
    }
    
    public void changeState(final IJobService jobService) {
        this.changeState(this.getStateByUserOnlineTime(), jobService);
    }
    
    public synchronized void changeState(final PlayerDto playerDto, final IJobService jobService) {
        this.playerDto = playerDto;
        this.changeState(this.getStateByUserOnlineTime(), jobService);
    }
    
    public synchronized void update(final PlayerDto playerDto, final IJobService jobService) {
        this.playerDto = playerDto;
        this.currentState.update(this.owner, playerDto, jobService);
    }
    
    private IAntiAddictionState getStateByUserOnlineTime() {
        return AntiAddictionStateFactory.getInstance().getState(this.owner);
    }
}
