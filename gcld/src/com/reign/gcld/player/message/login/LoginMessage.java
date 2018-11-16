package com.reign.gcld.player.message.login;

import com.reign.gcld.common.message.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.user.dto.*;

public class LoginMessage implements Message
{
    private PlayerDto playerDto;
    private Action action;
    private UserDto userDto;
    private int roleIndex;
    
    public LoginMessage(final PlayerDto playerDto, final Action action) {
        this.playerDto = playerDto;
        this.action = action;
    }
    
    public LoginMessage(final PlayerDto playerDto, final UserDto userDto, final Action action) {
        this.playerDto = playerDto;
        this.userDto = userDto;
        this.action = action;
    }
    
    public LoginMessage(final int playerId, final UserDto userDto, final Action action) {
        this.playerDto = new PlayerDto();
        this.action = action;
        this.playerDto.playerId = playerId;
        this.userDto = userDto;
    }
    
    public LoginMessage(final int playerId, final String userId, final String yx, final UserDto userDto, final Action action) {
        this.playerDto = new PlayerDto();
        this.action = action;
        this.playerDto.playerId = playerId;
        this.playerDto.userId = userId;
        this.playerDto.yx = yx;
        this.userDto = userDto;
    }
    
    public LoginMessage(final int playerId, final String userId, final String yx, final UserDto userDto, final Action action, final int roleIndex) {
        this.playerDto = new PlayerDto();
        this.action = action;
        this.playerDto.playerId = playerId;
        this.playerDto.userId = userId;
        this.playerDto.yx = yx;
        this.playerDto.yxSource = "";
        this.userDto = userDto;
        this.roleIndex = roleIndex;
    }
    
    public PlayerDto getPlayerDto() {
        return this.playerDto;
    }
    
    public void setPlayerDto(final PlayerDto playerDto) {
        this.playerDto = playerDto;
    }
    
    public Action getAction() {
        return this.action;
    }
    
    public void setAction(final Action action) {
        this.action = action;
    }
    
    public UserDto getUserDto() {
        return this.userDto;
    }
    
    public void setUserDto(final UserDto userDto) {
        this.userDto = userDto;
    }
    
    public int getRoleIndex() {
        return this.roleIndex;
    }
    
    public void setRoleIndex(final int roleIndex) {
        this.roleIndex = roleIndex;
    }
}
