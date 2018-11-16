package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class NationIndivTask implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String randRule;
    private String goldRule;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public String getRandRule() {
        return this.randRule;
    }
    
    public void setRandRule(final String randRule) {
        this.randRule = randRule;
    }
    
    public String getGoldRule() {
        return this.goldRule;
    }
    
    public void setGoldRule(final String goldRule) {
        this.goldRule = goldRule;
    }
}
