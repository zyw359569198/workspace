package com.reign.kf.match.common.web.session;

public abstract class ConnectorDto
{
    protected int type;
    
    public abstract String buildLogStr();
    
    public int getType() {
        return this.type;
    }
}
