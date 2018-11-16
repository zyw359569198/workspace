package com.reign.plugin.yx.common;

import com.reign.framework.json.*;
import com.reign.plugin.yx.common.xml.*;

public class PingAnMoneyInfo implements DataInfo
{
    public String avaliableGameAmount;
    
    @Override
    public byte[] getJsonInfo() {
        final JsonDocument doc = new JsonDocument();
        doc.createElement("avail_game_acount", this.avaliableGameAmount);
        return doc.toByte();
    }
    
    @Override
    public XMLDocument getXmlInfo() {
        final XMLDocument doc = new XMLDocument();
        doc.createElement("avail_game_acount", this.avaliableGameAmount);
        return doc;
    }
}
