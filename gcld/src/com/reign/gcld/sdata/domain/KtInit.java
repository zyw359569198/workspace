package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class KtInit implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer ktType;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getKtType() {
        return this.ktType;
    }
    
    public void setKtType(final Integer ktType) {
        this.ktType = ktType;
    }
}
