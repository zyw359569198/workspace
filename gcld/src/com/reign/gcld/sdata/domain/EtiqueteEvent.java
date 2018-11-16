package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;
import com.reign.gcld.battle.common.*;

public class EtiqueteEvent implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String event;
    private String words;
    private String button;
    private String sendReward;
    private Integer sendPoint;
    private String reply;
    private String replyReward;
    private Integer replyPoint;
    private BattleDrop sendRewardDrop;
    private BattleDrop replyRewardDrop;
    private String[] wordsArray;
    private String[] replyArray;
    
    public EtiqueteEvent() {
        this.wordsArray = null;
        this.replyArray = null;
    }
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public String getEvent() {
        return this.event;
    }
    
    public void setEvent(final String event) {
        this.event = event;
    }
    
    public String getWords() {
        return this.words;
    }
    
    public void setWords(final String words) {
        this.words = words;
    }
    
    public String getButton() {
        return this.button;
    }
    
    public void setButton(final String button) {
        this.button = button;
    }
    
    public String getSendReward() {
        return this.sendReward;
    }
    
    public void setSendReward(final String sendReward) {
        this.sendReward = sendReward;
    }
    
    public Integer getSendPoint() {
        return this.sendPoint;
    }
    
    public void setSendPoint(final Integer sendPoint) {
        this.sendPoint = sendPoint;
    }
    
    public String getReply() {
        return this.reply;
    }
    
    public void setReply(final String reply) {
        this.reply = reply;
    }
    
    public String getReplyReward() {
        return this.replyReward;
    }
    
    public void setReplyReward(final String replyReward) {
        this.replyReward = replyReward;
    }
    
    public Integer getReplyPoint() {
        return this.replyPoint;
    }
    
    public void setReplyPoint(final Integer replyPoint) {
        this.replyPoint = replyPoint;
    }
    
    public BattleDrop getSendRewardDrop() {
        return this.sendRewardDrop;
    }
    
    public void setSendRewardDrop(final BattleDrop sendRewardDrop) {
        this.sendRewardDrop = sendRewardDrop;
    }
    
    public BattleDrop getReplyRewardDrop() {
        return this.replyRewardDrop;
    }
    
    public void setReplyRewardDrop(final BattleDrop replyRewardDrop) {
        this.replyRewardDrop = replyRewardDrop;
    }
    
    public String[] getWordsArray() {
        return this.wordsArray;
    }
    
    public void setWordsArray(final String[] wordsArray) {
        this.wordsArray = wordsArray;
    }
    
    public String[] getReplyArray() {
        return this.replyArray;
    }
    
    public void setReplyArray(final String[] replyArray) {
        this.replyArray = replyArray;
    }
}
