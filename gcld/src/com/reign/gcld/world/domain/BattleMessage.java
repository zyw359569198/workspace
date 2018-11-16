package com.reign.gcld.world.domain;

import com.reign.gcld.chat.service.*;
import com.reign.gcld.common.*;
import com.reign.framework.json.*;
import com.reign.framework.netty.util.*;
import com.reign.gcld.common.util.*;
import com.reign.framework.netty.servlet.*;
import java.util.*;

public class BattleMessage extends CityMessage
{
    private int attForceId;
    private int defForceId;
    
    public int getAttForceId() {
        return this.attForceId;
    }
    
    public void setAttForceId(final int attForceId) {
        this.attForceId = attForceId;
    }
    
    public int getDefForceId() {
        return this.defForceId;
    }
    
    public void setDefForceId(final int defForceId) {
        this.defForceId = defForceId;
    }
    
    public BattleMessage(final int cityId, final int a, final int d, final int f) {
        super(cityId, f, "");
        this.setAttForceId(a);
        this.setDefForceId(d);
    }
    
    @Override
    public void messageChanged() {
        final Group attGroup = GroupManager.getInstance().getGroup(String.valueOf(ChatType.COUNTRY.toString()) + this.attForceId);
        final Group defGroup = GroupManager.getInstance().getGroup(String.valueOf(ChatType.COUNTRY.toString()) + this.defForceId);
        final Group cityGroup = GroupManager.getInstance().getGroup(String.valueOf(ChatType.WORLD.toString()) + this.getCityId());
        if (cityGroup != null) {
            final GroupImpl city = (GroupImpl)cityGroup;
            final Map<String, Session> map = city.getUserMap();
            for (final String string : map.keySet()) {
                Map<String, Session> attMap = null;
                Map<String, Session> defMap = null;
                if (attGroup != null) {
                    attMap = ((GroupImpl)attGroup).getUserMap();
                }
                if (defGroup != null) {
                    defMap = ((GroupImpl)defGroup).getUserMap();
                }
                if (attMap != null && attMap.get(string) != null) {
                    final byte[] bytes = JsonBuilder.getJson(JsonBuilder.getJson(State.PUSH, PushCommand.PUSH_CITY_MESSAGE.getModule(), MessageCenter.getMessage(this.getCityId(), LocalMessages.Message_TYPE_4, "").toByte()));
                    cityGroup.notify(WrapperUtil.wrapper(PushCommand.PUSH_CITY_MESSAGE.getCommand(), 0, bytes), new String[] { string });
                }
                if (defMap != null && defMap.get(string) != null) {
                    final byte[] bytes = JsonBuilder.getJson(JsonBuilder.getJson(State.PUSH, PushCommand.PUSH_CITY_MESSAGE.getModule(), MessageCenter.getMessage(this.getCityId(), LocalMessages.Message_TYPE_5, WebUtil.getForceName(this.attForceId)).toByte()));
                    cityGroup.notify(WrapperUtil.wrapper(PushCommand.PUSH_CITY_MESSAGE.getCommand(), 0, bytes), new String[] { string });
                }
            }
        }
    }
    
    @Override
    public String getMessage(final int userForce) {
        if (this.attForceId == userForce) {
            return MessageCenter.getStringMessage(LocalMessages.Message_TYPE_4, "");
        }
        return MessageCenter.getStringMessage(LocalMessages.Message_TYPE_5, WebUtil.getForceName(this.attForceId));
    }
}
