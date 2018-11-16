package com.reign.kf.match.common.web;

import com.reign.kf.match.common.web.session.*;
import com.reign.kf.match.common.message.*;
import com.reign.framework.netty.servlet.*;

public class LocalSessionListener implements SessionAttributeListener, SessionListener
{
    @Override
	public void attributeAdded(final SessionAttributeEvent event) {
        if (event.value instanceof ConnectorDto) {
            final ConnectorDto connectorDto = (ConnectorDto)event.value;
            if (connectorDto.getType() == 1) {
                GameServers.addGameServer((GameServerDto)connectorDto, event.session);
            }
            else if (connectorDto.getType() == 2) {
                final PlayerDto playerDto = (PlayerDto)connectorDto;
                Players.addPlayer(playerDto, event.session);
                HandlerManager.getHandler(LoginMessage.class).handler(new LoginMessage(playerDto, Action.LOGIN));
            }
        }
    }
    
    @Override
	public void attributeRemoved(final SessionAttributeEvent event) {
        if (event.value instanceof ConnectorDto) {
            final ConnectorDto connectorDto = (ConnectorDto)event.value;
            if (connectorDto.getType() == 1) {
                final GameServerDto gameServerDto = (GameServerDto)connectorDto;
                if (GameServers.isValidate(gameServerDto)) {
                    GameServers.removeGameServer(gameServerDto.getServerName(), gameServerDto.getServerId());
                }
            }
            else if (connectorDto.getType() == 2) {
                final PlayerDto playerDto = (PlayerDto)connectorDto;
                if (Players.isValidate(playerDto)) {
                    Players.removePlayer(playerDto.getUuid());
                    HandlerManager.getHandler(LoginMessage.class).handler(new LoginMessage(playerDto, Action.LOGINOUT));
                }
            }
        }
    }
    
    @Override
	public void attributeReplaced(final SessionAttributeEvent event) {
        if (event.value instanceof ConnectorDto) {
            final ConnectorDto connectorDto = (ConnectorDto)event.value;
            if (connectorDto.getType() == 1) {
                final GameServerDto gameServerDto = (GameServerDto)connectorDto;
                final GameServerDto replacedServer = (GameServerDto)event.session.getAttribute("CONNECTOR");
                GameServers.removeGameServer(gameServerDto.getServerName(), gameServerDto.getServerId());
                GameServers.addGameServer(replacedServer, event.session);
            }
            else if (connectorDto.getType() == 2) {
                final PlayerDto playerDto = (PlayerDto)connectorDto;
                final PlayerDto replacedPlayer = (PlayerDto)event.session.getAttribute("CONNECTOR");
                if (replacedPlayer != null) {
                    Players.removePlayer(playerDto.getUuid());
                    Players.addPlayer(replacedPlayer, event.session);
                    if (playerDto.getUuid() != replacedPlayer.getUuid()) {
                        HandlerManager.getHandler(LoginMessage.class).handler(new LoginMessage(playerDto, Action.LOGINOUT));
                        HandlerManager.getHandler(LoginMessage.class).handler(new LoginMessage(replacedPlayer, Action.LOGIN));
                    }
                    else {
                        HandlerManager.getHandler(LoginMessage.class).handler(new LoginMessage(replacedPlayer, Action.LOGIN));
                    }
                }
            }
        }
    }
    
    @Override
	public void sessionCreated(final SessionEvent event) {
    }
    
    @Override
	public void sessionDestroyed(final SessionEvent event) {
        final ConnectorDto connectorDto = (ConnectorDto)event.session.getAttribute("CONNECTOR");
        if (connectorDto == null) {
            return;
        }
        if (connectorDto.getType() == 1) {
            final GameServerDto gameServerDto = (GameServerDto)connectorDto;
            if (GameServers.isValidate(gameServerDto)) {
                GameServers.removeGameServer(gameServerDto.getServerName(), gameServerDto.getServerId());
            }
        }
        else if (connectorDto.getType() == 2) {
            final PlayerDto playerDto = (PlayerDto)connectorDto;
            if (Players.isValidate(playerDto)) {
                Players.removePlayer(playerDto.getUuid());
                HandlerManager.getHandler(LoginMessage.class).handler(new LoginMessage(playerDto, Action.LOGINOUT));
            }
        }
    }
}
