package com.reign.gcld.world.domain;

import com.reign.gcld.chat.service.*;
import com.reign.gcld.world.common.*;
import com.reign.gcld.common.*;
import com.reign.framework.netty.util.*;
import com.reign.framework.netty.servlet.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.json.*;

public class MoveMessage extends CityMessage
{
    private int playerId;
    private String generalName;
    private String playerName;
    
    public String getPlayerName() {
        return this.playerName;
    }
    
    public void setPlayerName(final String playerName) {
        this.playerName = playerName;
    }
    
    public String getGeneralName() {
        return this.generalName;
    }
    
    public void setGeneralName(final String generalName) {
        this.generalName = generalName;
    }
    
    public MoveMessage(final int cityId, final int forceId, final int playerId, final String geString, final String type, final String playerName) {
        super(cityId, forceId, type);
        this.setGeneralName(geString);
        this.setPlayerName(playerName);
        this.setPlayerId(playerId);
        this.setType(type);
    }
    
    @Override
    public void messageChanged() {
        final Group cityGroup = GroupManager.getInstance().getGroup(String.valueOf(ChatType.WORLD.toString()) + this.getCityId());
        if (cityGroup == null) {
            return;
        }
        final PlayerDto playerDto = Players.getPlayer(this.playerId);
        if (playerDto != null) {
            this.playerName = String.valueOf(WorldCityCommon.nationIdNameMapDot.get(playerDto.forceId)) + this.playerName;
        }
        final JsonDocument doc = MessageCenter.getMessage(this.getCityId(), this.getType(), this.playerName, this.generalName);
        final byte[] bytes = JsonBuilder.getJson(JsonBuilder.getJson(State.PUSH, PushCommand.PUSH_CITY_MESSAGE.getModule(), doc.toByte()));
        cityGroup.notify(WrapperUtil.wrapper(PushCommand.PUSH_CITY_MESSAGE.getCommand(), 0, bytes));
    }
    
    @Override
    public String getMessage(final int forceId) {
        return MessageCenter.getStringMessage(this.getType(), this.playerName, this.generalName);
    }
    
    public void setPlayerId(final int playerId) {
        this.playerId = playerId;
    }
    
    public int getPlayerId() {
        return this.playerId;
    }
}
