package com.reign.gcld.gift.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class BakPlayerGift implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer bakId;
    private Integer id;
    private Integer playerId;
    private Integer giftId;
    private Integer received;
    private Date receivedTime;
    private Integer allServer;
    
    public Integer getBakId() {
        return this.bakId;
    }
    
    public void setBakId(final Integer bakId) {
        this.bakId = bakId;
    }
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getGiftId() {
        return this.giftId;
    }
    
    public void setGiftId(final Integer giftId) {
        this.giftId = giftId;
    }
    
    public Integer getReceived() {
        return this.received;
    }
    
    public void setReceived(final Integer received) {
        this.received = received;
    }
    
    public Date getReceivedTime() {
        return this.receivedTime;
    }
    
    public void setReceivedTime(final Date receivedTime) {
        this.receivedTime = receivedTime;
    }
    
    public Integer getAllServer() {
        return this.allServer;
    }
    
    public void setAllServer(final Integer allServer) {
        this.allServer = allServer;
    }
}
