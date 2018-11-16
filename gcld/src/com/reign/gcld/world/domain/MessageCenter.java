package com.reign.gcld.world.domain;

import com.reign.framework.json.*;
import com.reign.util.*;
import com.reign.gcld.common.*;

public class MessageCenter
{
    public static JsonDocument getMessage(final int cityId, final String type, final String... params) {
        if (LocalMessages.Message_TYPE_1.equalsIgnoreCase(type)) {
            return getDocMessage(cityId, MessageFormatter.format(LocalMessages.MOVE_MESSAGE, new Object[] { params[0], params[1], type }));
        }
        if (LocalMessages.Message_TYPE_2.equalsIgnoreCase(type)) {
            return getDocMessage(cityId, MessageFormatter.format(LocalMessages.MOVE_MESSAGE, new Object[] { params[0], params[1], type }));
        }
        if (LocalMessages.Message_TYPE_3.equalsIgnoreCase(type)) {
            String forString = "";
            if (Boolean.valueOf(params[0])) {
                forString = LocalMessages.ME_MESSAGE;
            }
            else {
                forString = LocalMessages.ENEMY_MESSAGE;
            }
            return getDocMessage(cityId, MessageFormatter.format(LocalMessages.TRICK_MESSAGE, new Object[] { forString, params[1] }));
        }
        if (LocalMessages.Message_TYPE_4.equalsIgnoreCase(type)) {
            return getDocMessage(cityId, ColorUtil.getRedMsg(LocalMessages.ATT_MESSAGE));
        }
        if (LocalMessages.Message_TYPE_5.equalsIgnoreCase(type)) {
            return getDocMessage(cityId, ColorUtil.getRedMsg(MessageFormatter.format(LocalMessages.DEFEND_MESSAGE, new Object[] { params[0] })));
        }
        if (LocalMessages.Message_TYPE_6.equalsIgnoreCase(type)) {
            return getDocMessage(cityId, LocalMessages.NEW_STATE_MESSAGE);
        }
        return null;
    }
    
    public static String getStringMessage(final String type, final String... params) {
        if (LocalMessages.Message_TYPE_1.equalsIgnoreCase(type)) {
            return MessageFormatter.format(LocalMessages.MOVE_MESSAGE, new Object[] { params[0], params[1], type });
        }
        if (LocalMessages.Message_TYPE_2.equalsIgnoreCase(type)) {
            return MessageFormatter.format(LocalMessages.MOVE_MESSAGE, new Object[] { params[0], params[1], type });
        }
        if (LocalMessages.Message_TYPE_3.equalsIgnoreCase(type)) {
            String forString = "";
            if (Boolean.valueOf(params[0])) {
                forString = LocalMessages.ME_MESSAGE;
            }
            else {
                forString = LocalMessages.ENEMY_MESSAGE;
            }
            return MessageFormatter.format(LocalMessages.TRICK_MESSAGE, new Object[] { forString, params[1] });
        }
        if (LocalMessages.Message_TYPE_4.equalsIgnoreCase(type)) {
            return ColorUtil.getRedMsg(LocalMessages.ATT_MESSAGE);
        }
        if (LocalMessages.Message_TYPE_5.equalsIgnoreCase(type)) {
            return ColorUtil.getRedMsg(MessageFormatter.format(LocalMessages.DEFEND_MESSAGE, new Object[] { params[0] }));
        }
        if (LocalMessages.Message_TYPE_6.equalsIgnoreCase(type)) {
            return LocalMessages.NEW_STATE_MESSAGE;
        }
        return null;
    }
    
    public static JsonDocument getDocMessage(final int cityId, final String s) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("cityMessages");
        doc.startObject();
        doc.createElement("cityId", cityId);
        doc.createElement("cityMessage", s);
        doc.endObject();
        doc.endArray();
        doc.endObject();
        return doc;
    }
}
