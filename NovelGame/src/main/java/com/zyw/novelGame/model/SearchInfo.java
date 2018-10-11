package com.zyw.novelGame.model;

import java.util.Date;

public class SearchInfo {
    private String id;

    private String keyword;

    private String keyvalue;

    private Date createTime;
    
    private Long hits;
    

    public Long getHits() {
		return hits;
	}

	public void setHits(Long hits) {
		this.hits = hits;
	}

	public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword == null ? null : keyword.trim();
    }

    public String getKeyvalue() {
        return keyvalue;
    }

    public void setKeyvalue(String keyvalue) {
        this.keyvalue = keyvalue == null ? null : keyvalue.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}