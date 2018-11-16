package com.reign.gcld.gift.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class GiftInfo implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String giftName;
    private String yx;
    private String contents;
    private Integer allServer;
    private Integer currentPlayer;
    private Date sendTime;
    private Date expiredTime;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public String getGiftName() {
        return this.giftName;
    }
    
    public void setGiftName(final String giftName) {
        this.giftName = giftName;
    }
    
    public String getYx() {
        return this.yx;
    }
    
    public void setYx(final String yx) {
        this.yx = yx;
    }
    
    public String getContents() {
        return this.contents;
    }
    
    public void setContents(final String contents) {
        this.contents = contents;
    }
    
    public Integer getAllServer() {
        return this.allServer;
    }
    
    public void setAllServer(final Integer allServer) {
        this.allServer = allServer;
    }
    
    public Integer getCurrentPlayer() {
        return this.currentPlayer;
    }
    
    public void setCurrentPlayer(final Integer currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
    
    public Date getSendTime() {
        return this.sendTime;
    }
    
    public void setSendTime(final Date sendTime) {
        this.sendTime = sendTime;
    }
    
    public Date getExpiredTime() {
        return this.expiredTime;
    }
    
    public void setExpiredTime(final Date expiredTime) {
        this.expiredTime = expiredTime;
    }
}
