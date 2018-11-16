package com.reign.gcld.notice.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class SystemNotice implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer noticeType;
    private String content;
    private String yx;
    private Date expireTime;
    private Date createTime;
    private Date startTime;
    private Integer frequency;
    private Date nextBroadcastTime;
    
    public Date getNextBroadcastTime() {
        return this.nextBroadcastTime;
    }
    
    public void setNextBroadcastTime(final Date nextBroadcastTime) {
        this.nextBroadcastTime = nextBroadcastTime;
    }
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getNoticeType() {
        return this.noticeType;
    }
    
    public void setNoticeType(final Integer noticeType) {
        this.noticeType = noticeType;
    }
    
    public String getContent() {
        return this.content;
    }
    
    public void setContent(final String content) {
        this.content = content;
    }
    
    public String getYx() {
        return this.yx;
    }
    
    public void setYx(final String yx) {
        this.yx = yx;
    }
    
    public Date getExpireTime() {
        return this.expireTime;
    }
    
    public void setExpireTime(final Date expireTime) {
        this.expireTime = expireTime;
    }
    
    public Date getCreateTime() {
        return this.createTime;
    }
    
    public void setCreateTime(final Date createTime) {
        this.createTime = createTime;
    }
    
    public Date getStartTime() {
        return this.startTime;
    }
    
    public void setStartTime(final Date startTime) {
        this.startTime = startTime;
    }
    
    public Integer getFrequency() {
        return this.frequency;
    }
    
    public void setFrequency(final Integer frequency) {
        this.frequency = frequency;
    }
}
