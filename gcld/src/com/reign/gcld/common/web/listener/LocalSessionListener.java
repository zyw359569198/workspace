package com.reign.gcld.common.web.listener;

import com.reign.gcld.user.dto.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.common.*;
import com.reign.gcld.common.message.*;
import com.reign.gcld.player.message.login.*;
import com.reign.framework.netty.servlet.*;

public class LocalSessionListener implements SessionAttributeListener, SessionListener
{
    @Override
	public void attributeAdded(final SessionAttributeEvent event) {
        if (event.value instanceof UserDto) {
            Users.addUser((UserDto)event.value, event.session);
        }
        else if (event.value instanceof PlayerDto) {
            final PlayerDto dto = (PlayerDto)event.value;
            Players.addPlayer(dto, event.session);
            final UserDto userDto = this.getUserDtoFromSession(event.session, dto);
            HandlerManager.getHandler(LoginMessage.class).handler(new LoginMessage(dto, userDto, Action.LOGIN));
        }
    }
    
    @Override
	public void attributeRemoved(final SessionAttributeEvent event) {
        if (event.value instanceof UserDto) {
            final UserDto dto = (UserDto)event.value;
            if (Users.isValidate(dto)) {
                Users.removeUser(dto.getId());
            }
        }
        else if (event.value instanceof PlayerDto) {
            final PlayerDto dto2 = (PlayerDto)event.value;
            if (Players.isValidate(dto2)) {
                Players.removePlayer(dto2.playerId);
                final UserDto userDto = this.getUserDtoFromSession(event.session, dto2);
                HandlerManager.getHandler(LoginMessage.class).handler(new LoginMessage(dto2, userDto, Action.LOGINOUT));
            }
        }
    }
    
    @Override
	public void attributeReplaced(final SessionAttributeEvent event) {
        if (event.value instanceof UserDto) {
            final UserDto user = (UserDto)event.value;
            final UserDto replacedUser = (UserDto)event.session.getAttribute("user");
            if (replacedUser != null) {
                Users.removeUser(user.getId());
                Users.addUser(replacedUser, event.session);
            }
        }
        else if (event.value instanceof PlayerDto) {
            final PlayerDto player = (PlayerDto)event.value;
            final PlayerDto replacedPlayer = (PlayerDto)event.session.getAttribute("PLAYER");
            if (replacedPlayer != null) {
                Players.removePlayer(player.playerId);
                Players.addPlayer(replacedPlayer, event.session);
                final UserDto userDto = this.getUserDtoFromSession(event.session, player);
                if (player.playerId != replacedPlayer.playerId) {
                    HandlerManager.getHandler(LoginMessage.class).handler(new LoginMessage(player, userDto, Action.LOGINOUT));
                    HandlerManager.getHandler(LoginMessage.class).handler(new LoginMessage(replacedPlayer, userDto, Action.LOGIN));
                }
                else {
                    HandlerManager.getHandler(LoginMessage.class).handler(new LoginMessage(replacedPlayer, userDto, Action.LOGIN));
                }
            }
        }
    }
    
    @Override
	public void sessionCreated(final SessionEvent event) {
    }
    
    @Override
	public void sessionDestroyed(final SessionEvent event) {
        final UserDto user = (UserDto)event.session.getAttribute("user");
        if (user != null && Users.isValidate(user)) {
            Users.removeUser(user.getId());
        }
        final PlayerDto player = (PlayerDto)event.session.getAttribute("PLAYER");
        if (player != null && Players.isValidate(player)) {
            Players.removePlayer(player.playerId);
            final UserDto userDto = this.getUserDtoFromSession(event.session, player);
            HandlerManager.getHandler(LoginMessage.class).handler(new LoginMessage(player, userDto, Action.LOGINOUT));
        }
    }
    
    private UserDto getUserDtoFromSession(final Session session, final PlayerDto playerDto) {
        UserDto userDto = null;
        try {
            userDto = (UserDto)session.getAttribute("user");
        }
        catch (IllegalStateException ex) {}
        if (userDto == null) {
            userDto = new UserDto();
            userDto.activate = 1;
            userDto.setLoginTime(System.currentTimeMillis());
            userDto.setOnlineTime(0L);
            userDto.userId = playerDto.userId;
            userDto.yx = playerDto.yx;
            userDto.setNeedAntiAddiction(true);
        }
        return userDto;
    }
}
