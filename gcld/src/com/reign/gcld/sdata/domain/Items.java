package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class Items implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer type;
    private Integer index;
    private String name;
    private String effect;
    private Integer copper;
    private String intro;
    private String pic;
    private Integer changeNum;
    private Integer changeItemId;
    private Integer quality;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getType() {
        return this.type;
    }
    
    public void setType(final Integer type) {
        this.type = type;
    }
    
    public Integer getIndex() {
        return this.index;
    }
    
    public void setIndex(final Integer index) {
        this.index = index;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getEffect() {
        return this.effect;
    }
    
    public void setEffect(final String effect) {
        this.effect = effect;
    }
    
    public Integer getCopper() {
        return this.copper;
    }
    
    public void setCopper(final Integer copper) {
        this.copper = copper;
    }
    
    public String getIntro() {
        return this.intro;
    }
    
    public void setIntro(final String intro) {
        this.intro = intro;
    }
    
    public String getPic() {
        return this.pic;
    }
    
    public void setPic(final String pic) {
        this.pic = pic;
    }
    
    public Integer getChangeNum() {
        return this.changeNum;
    }
    
    public void setChangeNum(final Integer changeNum) {
        this.changeNum = changeNum;
    }
    
    public Integer getChangeItemId() {
        return this.changeItemId;
    }
    
    public void setChangeItemId(final Integer changeItemId) {
        this.changeItemId = changeItemId;
    }
    
    public Integer getQuality() {
        return this.quality;
    }
    
    public void setQuality(final Integer quality) {
        this.quality = quality;
    }
}
