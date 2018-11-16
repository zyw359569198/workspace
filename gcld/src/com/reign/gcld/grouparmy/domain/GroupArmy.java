package com.reign.gcld.grouparmy.domain;

import com.reign.framework.mybatis.*;

public class GroupArmy implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer leaderId;
    private Integer generalId;
    private Integer nowCityId;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getLeaderId() {
        return this.leaderId;
    }
    
    public void setLeaderId(final Integer leaderId) {
        this.leaderId = leaderId;
    }
    
    public Integer getGeneralId() {
        return this.generalId;
    }
    
    public void setGeneralId(final Integer generalId) {
        this.generalId = generalId;
    }
    
    public Integer getNowCityId() {
        return this.nowCityId;
    }
    
    public void setNowCityId(final Integer nowCityId) {
        this.nowCityId = nowCityId;
    }
}
