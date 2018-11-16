package com.reign.kfgz.dto.response;

public class MailDto
{
    int cId;
    String title;
    String content;
    int playerId;
    
    public MailDto() {
    }
    
    public MailDto(final int cId, final String title, final String content, final int playerId) {
        this.cId = cId;
        this.title = title;
        this.content = content;
        this.playerId = playerId;
    }
    
    public int getcId() {
        return this.cId;
    }
    
    public void setcId(final int cId) {
        this.cId = cId;
    }
    
    public String getContent() {
        return this.content;
    }
    
    public void setContent(final String content) {
        this.content = content;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(final String title) {
        this.title = title;
    }
    
    public int getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final int playerId) {
        this.playerId = playerId;
    }
}
