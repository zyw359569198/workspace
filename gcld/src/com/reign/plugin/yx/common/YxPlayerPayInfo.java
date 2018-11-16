package com.reign.plugin.yx.common;

import java.util.*;
import com.reign.framework.json.*;

public class YxPlayerPayInfo
{
    private String userId;
    private String orderId;
    private int gold;
    private int type;
    private Date payTime;
    private int playerId;
    private static int RATE;
    
    static {
        YxPlayerPayInfo.RATE = 10;
    }
    
    public YxPlayerPayInfo(final String userId, final String orderId, final int gold, final int type, final Date payTime, final int playerId) {
        this.userId = userId;
        this.orderId = orderId;
        this.gold = gold;
        this.type = type;
        this.payTime = payTime;
        this.playerId = playerId;
    }
    
    public void buildJson(final JsonDocument doc) {
        doc.startObject();
        doc.createElement("userId", this.userId);
        doc.createElement("payTime", this.payTime);
        doc.createElement("orderId", this.orderId);
        doc.createElement("gold", this.gold);
        doc.createElement("type", this.type);
        doc.endObject();
    }
    
    public void buildSinaJson(final JsonDocument doc, final String currentServerId) {
        doc.startObject();
        doc.createElement("ServerID", currentServerId);
        doc.createElement("UserID", this.userId);
        doc.createElement("RoleID", this.playerId);
        doc.createElement("SinaOrderID", this.orderId);
        doc.createElement("GameOrderID", this.orderId);
        doc.createElement("GamePoint", this.gold);
        doc.createElement("PayPoint", this.gold / YxPlayerPayInfo.RATE);
        doc.endObject();
    }
    
    public String getUserId() {
        return this.userId;
    }
    
    public String getOrderId() {
        return this.orderId;
    }
    
    public int getGold() {
        return this.gold;
    }
    
    public int getType() {
        return this.type;
    }
    
    public Date getPayTime() {
        return this.payTime;
    }
}
