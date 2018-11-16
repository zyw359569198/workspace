package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class FbGuide implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer guideType;
    private Integer powerId;
    private String target;
    private String brief;
    private String detail;
    private String pic;
    private Integer foreignKey;
    private Integer link;
    private Integer lv;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getGuideType() {
        return this.guideType;
    }
    
    public void setGuideType(final Integer guideType) {
        this.guideType = guideType;
    }
    
    public Integer getPowerId() {
        return this.powerId;
    }
    
    public void setPowerId(final Integer powerId) {
        this.powerId = powerId;
    }
    
    public String getTarget() {
        return this.target;
    }
    
    public void setTarget(final String target) {
        this.target = target;
    }
    
    public String getBrief() {
        return this.brief;
    }
    
    public void setBrief(final String brief) {
        this.brief = brief;
    }
    
    public String getDetail() {
        return this.detail;
    }
    
    public void setDetail(final String detail) {
        this.detail = detail;
    }
    
    public String getPic() {
        return this.pic;
    }
    
    public void setPic(final String pic) {
        this.pic = pic;
    }
    
    public Integer getForeignKey() {
        return this.foreignKey;
    }
    
    public void setForeignKey(final Integer foreignKey) {
        this.foreignKey = foreignKey;
    }
    
    public Integer getLink() {
        return this.link;
    }
    
    public void setLink(final Integer link) {
        this.link = link;
    }
    
    public Integer getLv() {
        return this.lv;
    }
    
    public void setLv(final Integer lv) {
        this.lv = lv;
    }
}
