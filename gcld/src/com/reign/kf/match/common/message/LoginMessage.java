package com.reign.kf.match.common.message;

import com.reign.kf.match.common.web.session.*;

public class LoginMessage implements Message
{
    private PlayerDto playerDto;
    private Action action;
    
    public LoginMessage(final PlayerDto playerDto, final Action action) {
        this.playerDto = playerDto;
        this.action = action;
    }
    
    public PlayerDto getPlayerDto() {
        return this.playerDto;
    }
    
    public Action getAction() {
        return this.action;
    }
}
