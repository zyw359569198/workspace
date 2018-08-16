package com.zyw.novelGame.model;

public class Author {
    private String id;

    private String authorId;

    private String authorName;

    private String authorNameEn;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId == null ? null : authorId.trim();
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName == null ? null : authorName.trim();
    }

    public String getAuthorNameEn() {
        return authorNameEn;
    }

    public void setAuthorNameEn(String authorNameEn) {
        this.authorNameEn = authorNameEn == null ? null : authorNameEn.trim();
    }
}