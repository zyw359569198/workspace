package com.reign.plugin.yx.common;

import java.util.*;
import com.reign.framework.json.*;
import java.text.*;

public class YxSourceInfo
{
    private String userId;
    private int playerId;
    private String playerName;
    private int playerLv;
    private Date lastLoginTime;
    
    public YxSourceInfo(final String userId, final int playerId, final String playerName, final int playerLv, final Date lastLoginTime) {
        this.userId = userId;
        this.playerId = playerId;
        this.playerName = playerName;
        this.playerLv = playerLv;
        this.lastLoginTime = lastLoginTime;
    }
    
    public void buildJson(final JsonDocument doc) {
        doc.startObject();
        doc.createElement("userId", this.userId);
        doc.createElement("playerId", this.playerId);
        doc.createElement("playerName", this.playerName);
        doc.createElement("playerLv", this.playerLv);
        doc.createElement("lastLoginTime", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(this.lastLoginTime));
        doc.endObject();
    }
}
