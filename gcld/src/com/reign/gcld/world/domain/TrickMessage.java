package com.reign.gcld.world.domain;

import com.reign.gcld.chat.service.*;
import com.reign.gcld.common.*;
import com.reign.framework.netty.util.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.json.*;

public class TrickMessage extends CityMessage
{
    private String trickName;
    private int userForceId;
    
    public TrickMessage(final int c, final int forceId, final String trickName, final int userForceId) {
        super(c, forceId, LocalMessages.Message_TYPE_3);
        this.setTrickName(trickName);
        this.setUserForceId(userForceId);
    }
    
    public void setTrickName(final String trickName) {
        this.trickName = trickName;
    }
    
    public String getTrickName() {
        return this.trickName;
    }
    
    @Override
    public void messageChanged() {
        final Group cityGroup = GroupManager.getInstance().getGroup(String.valueOf(ChatType.WORLD.toString()) + this.getCityId());
        if (cityGroup == null) {
            return;
        }
        final JsonDocument doc = MessageCenter.getMessage(this.getCityId(), this.getType(), String.valueOf(this.userForceId == this.getForceId()), this.trickName);
        final byte[] bytes = JsonBuilder.getJson(JsonBuilder.getJson(State.PUSH, PushCommand.PUSH_CITY_MESSAGE.getModule(), doc.toByte()));
        cityGroup.notify(WrapperUtil.wrapper(PushCommand.PUSH_CITY_MESSAGE.getCommand(), 0, bytes));
    }
    
    public void setUserForceId(final int userForceId) {
        this.userForceId = userForceId;
    }
    
    public int getUserForceId() {
        return this.userForceId;
    }
    
    @Override
    public String getMessage(final int forceId) {
        return MessageCenter.getStringMessage(this.getType(), String.valueOf(this.userForceId == this.getForceId()), this.trickName);
    }
}
