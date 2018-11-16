package com.reign.plugin.yx.common;

import com.reign.framework.json.*;

public class YxNoticeInfo
{
    private String content;
    
    public void setContent(final String content) {
        this.content = content;
    }
    
    public String getContent() {
        return this.content;
    }
    
    public YxNoticeInfo(final String content) {
        this.content = content;
    }
    
    public void buildJson(final JsonDocument doc) {
        doc.startObject();
        doc.createElement("content", this.content);
        doc.endObject();
    }
}
