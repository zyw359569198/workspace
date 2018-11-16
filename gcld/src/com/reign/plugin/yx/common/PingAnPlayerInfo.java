package com.reign.plugin.yx.common;

import com.reign.framework.json.*;
import com.reign.plugin.yx.common.xml.*;

public class PingAnPlayerInfo implements DataInfo
{
    public String roleName;
    public String roleServer;
    public String roleLever;
    public String roleCoin;
    
    @Override
    public byte[] getJsonInfo() {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("role_name", this.roleName);
        doc.createElement("role_server", this.roleServer);
        doc.createElement("role_level", this.roleLever);
        doc.createElement("role_coin", this.roleCoin);
        doc.endObject();
        return doc.toByte();
    }
    
    @Override
    public XMLDocument getXmlInfo() {
        final XMLDocument doc = new XMLDocument();
        doc.createElement("role_name", this.roleName);
        doc.createElement("role_server", this.roleServer);
        doc.createElement("role_level", this.roleLever);
        doc.createElement("role_coin", this.roleCoin);
        return doc;
    }
}
