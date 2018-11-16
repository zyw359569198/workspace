package com.reign.plugin.yx.common;

import java.util.*;
import com.reign.framework.json.*;
import java.text.*;
import java.net.*;
import java.io.*;

public class YxPlayerInfo
{
    private int playerId;
    private String playerName;
    private int state;
    private int lv;
    private int forceId;
    private String userId;
    private Date createTime;
    
    public YxPlayerInfo(final int playerId, final String playerName, final int state, final int lv, final int forceId, final String userId, final Date createTime) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.state = state;
        this.lv = lv;
        this.forceId = forceId;
        this.userId = userId;
        this.createTime = createTime;
    }
    
    public void buildJson(final JsonDocument doc) {
        doc.startObject();
        doc.createElement("userId", this.userId);
        doc.createElement("playerId", this.playerId);
        doc.createElement("playerName", this.playerName);
        doc.createElement("forceId", this.forceId);
        doc.createElement("playerLv", this.lv);
        doc.createElement("state", this.state);
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        doc.createElement("createTime", sdf.format(this.createTime));
        doc.endObject();
    }
    
    public void buildDuowanJson(final JsonDocument doc) throws UnsupportedEncodingException {
        doc.startObject();
        doc.createElement("nickname", URLEncoder.encode(this.playerName, "UTF-8"));
        doc.createElement("grade", this.lv);
        doc.createElement("sex", "m");
        doc.createElement("profession", URLEncoder.encode("", "UTF-8"));
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        doc.createElement("createTime", sdf.format(this.createTime));
        doc.endObject();
    }
    
    public void buildJDJson(final JsonDocument doc) {
        doc.startObject();
        doc.createElement("roleId", this.playerId);
        doc.createElement("roleName", this.playerName);
        doc.endObject();
    }
    
    public void buildSinaJson(final JsonDocument doc) {
        doc.startObject();
        doc.createElement("RoleID", this.playerId);
        doc.createElement("RoleName", this.playerName);
        doc.endObject();
    }
    
    public void build10086Json(final JsonDocument doc) {
        doc.startObject();
        doc.createElement("userid", this.userId);
        doc.createElement("playerId", this.playerId);
        doc.createElement("playerName", this.playerName);
        doc.endObject();
    }
    
    public int getLv() {
        return this.lv;
    }
    
    public int getPlayerId() {
        return this.playerId;
    }
    
    public String getPlayerName() {
        return this.playerName;
    }
    
    public String getUserId() {
        return this.userId;
    }
    
    public int getForceId() {
        return this.forceId;
    }
}
