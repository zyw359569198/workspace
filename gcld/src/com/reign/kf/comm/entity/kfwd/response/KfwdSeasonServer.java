package com.reign.kf.comm.entity.kfwd.response;

public abstract class KfwdSeasonServer
{
    protected String[] serverNameLatin;
    protected String[] serverId;
    protected String machineKey;
    protected String serverKey;
    
    public String getServerKey() {
        return this.serverKey;
    }
    
    public void setServerKey(final String serverKey) {
        this.serverKey = serverKey;
    }
    
    public String[] getServerNameLatin() {
        return this.serverNameLatin;
    }
    
    public void setServerNameLatin(final String[] serverNameLatin) {
        this.serverNameLatin = serverNameLatin;
    }
    
    public String[] getServerId() {
        return this.serverId;
    }
    
    public void setServerId(final String[] serverId) {
        this.serverId = serverId;
    }
    
    public String getMachineKey() {
        return this.machineKey;
    }
    
    public void setMachineKey(final String machineKey) {
        this.machineKey = machineKey;
    }
}
