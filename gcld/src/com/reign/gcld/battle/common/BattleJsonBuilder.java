package com.reign.gcld.battle.common;

import com.reign.framework.json.*;

public class BattleJsonBuilder
{
    public static byte[] getJson(final String key, final Object value) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement(key, value);
        doc.endObject();
        return doc.toByte();
    }
}
