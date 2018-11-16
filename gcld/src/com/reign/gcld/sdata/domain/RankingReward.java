package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class RankingReward implements IModel, Comparable<RankingReward>
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer type;
    private Integer count;
    private Integer rewardExp;
    private String title;
    private Integer lv;
    
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
    
    public Integer getCount() {
        return this.count;
    }
    
    public void setCount(final Integer count) {
        this.count = count;
    }
    
    public Integer getRewardExp() {
        return this.rewardExp;
    }
    
    public void setRewardExp(final Integer rewardExp) {
        this.rewardExp = rewardExp;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(final String title) {
        this.title = title;
    }
    
    public Integer getLv() {
        return this.lv;
    }
    
    public void setLv(final Integer lv) {
        this.lv = lv;
    }
    
    @Override
	public int compareTo(final RankingReward o) {
        if (o == null) {
            return 1;
        }
        if (this.id >= o.getId()) {
            return 1;
        }
        return 0;
    }
}
