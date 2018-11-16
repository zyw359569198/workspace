package com.reign.gcld.mail.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class Mail implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer fId;
    private String fName;
    private Integer tId;
    private String title;
    private String content;
    private Date sendtime;
    private Integer isRead;
    private Integer isDelete;
    private Integer mailType;
    private Integer linkId;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getFId() {
        return this.fId;
    }
    
    public void setFId(final Integer fId) {
        this.fId = fId;
    }
    
    public String getFName() {
        return this.fName;
    }
    
    public void setFName(final String fName) {
        this.fName = fName;
    }
    
    public Integer getTId() {
        return this.tId;
    }
    
    public void setTId(final Integer tId) {
        this.tId = tId;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(final String title) {
        this.title = title;
    }
    
    public String getContent() {
        return this.content;
    }
    
    public void setContent(final String content) {
        this.content = content;
    }
    
    public Date getSendtime() {
        return this.sendtime;
    }
    
    public void setSendtime(final Date sendtime) {
        this.sendtime = sendtime;
    }
    
    public Integer getIsRead() {
        return this.isRead;
    }
    
    public void setIsRead(final Integer isRead) {
        this.isRead = isRead;
    }
    
    public Integer getIsDelete() {
        return this.isDelete;
    }
    
    public void setIsDelete(final Integer isDelete) {
        this.isDelete = isDelete;
    }
    
    public Integer getMailType() {
        return this.mailType;
    }
    
    public void setMailType(final Integer mailType) {
        this.mailType = mailType;
    }
    
    public Integer getLinkId() {
        return this.linkId;
    }
    
    public void setLinkId(final Integer linkId) {
        this.linkId = linkId;
    }
}
