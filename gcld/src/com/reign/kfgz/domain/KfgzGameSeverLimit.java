package com.reign.kfgz.domain;

import com.reign.framework.hibernate.model.*;

public class KfgzGameSeverLimit implements IModel
{
    String serverKey;
    
    public String getServerKey() {
        return this.serverKey;
    }
    
    public void setServerKey(final String serverKey) {
        this.serverKey = serverKey;
    }
}
