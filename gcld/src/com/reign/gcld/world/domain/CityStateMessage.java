package com.reign.gcld.world.domain;

import com.reign.gcld.chat.service.*;
import com.reign.gcld.common.*;
import com.reign.framework.json.*;
import com.reign.framework.netty.util.*;
import com.reign.framework.netty.servlet.*;

public class CityStateMessage extends CityMessage
{
    public CityStateMessage(final int c, final int f, final String type, final int value, final int state) {
        super(c, f, type);
    }
    
    @Override
    public void messageChanged() {
        final Group group = GroupManager.getInstance().getGroup(String.valueOf(ChatType.WORLD.toString()) + this.getCityId());
        final Group forceGroup = GroupManager.getInstance().getGroup(String.valueOf(ChatType.COUNTRY.toString()) + this.getForceId());
        if (group != null && forceGroup != null) {
            final GroupImpl gImpl = (GroupImpl)group;
            final GroupImpl forImpl = (GroupImpl)forceGroup;
            for (final String g : gImpl.getUserMap().keySet()) {
                if (forImpl.getUserMap().get(g) == null) {
                    gImpl.getUserMap().remove(g);
                }
            }
        }
        if (forceGroup != null) {
            final byte[] bytes = JsonBuilder.getJson(JsonBuilder.getJson(State.PUSH, PushCommand.PUSH_CITY_MESSAGE.getModule(), MessageCenter.getDocMessage(this.getCityId(), LocalMessages.NEW_STATE_MESSAGE).toByte()));
            forceGroup.notify(WrapperUtil.wrapper(PushCommand.PUSH_CITY_MESSAGE.getCommand(), 0, bytes));
        }
    }
    
    @Override
    public String getMessage(final int forceId) {
        return LocalMessages.NEW_STATE_MESSAGE;
    }
}
