package com.zyw.novelGame.model;

import java.util.Date;

public class Book {
    private String id;

    private String bookId;

    private String bookName;

    private String bookNameEn;

    private String bookDesc;

    private Date createTime;

    private Date updateTime;

    private Integer isCompletion;

    private String authorId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId == null ? null : bookId.trim();
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName == null ? null : bookName.trim();
    }

    public String getBookNameEn() {
        return bookNameEn;
    }

    public void setBookNameEn(String bookNameEn) {
        this.bookNameEn = bookNameEn == null ? null : bookNameEn.trim();
    }

    public String getBookDesc() {
        return bookDesc;
    }

    public void setBookDesc(String bookDesc) {
        this.bookDesc = bookDesc == null ? null : bookDesc.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getIsCompletion() {
        return isCompletion;
    }

    public void setIsCompletion(Integer isCompletion) {
        this.isCompletion = isCompletion;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId == null ? null : authorId.trim();
    }
}