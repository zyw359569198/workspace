package com.reign.kfgz.domain;

import com.reign.framework.hibernate.model.*;

public class KfMaxUid implements IModel
{
    private static final long serialVersionUID = 1L;
    int pk;
    int maxId;
    
    public int getPk() {
        return this.pk;
    }
    
    public void setPk(final int pk) {
        this.pk = pk;
    }
    
    public int getMaxId() {
        return this.maxId;
    }
    
    public void setMaxId(final int maxId) {
        this.maxId = maxId;
    }
}
