package com.reign.kf.comm.transfer;

import java.net.*;

public class TransferConfig
{
    public long getSendInterval() {
        return 1000L;
    }
    
    public int getMaxSendSize() {
        return 0;
    }
    
    public URL getMatchServerUrl() {
        return null;
    }
    
    public String getMachineId() {
        return "";
    }
    
    public String getHost() {
        return "";
    }
    
    public int getPort() {
        return 9999;
    }
    
    public boolean compress() {
        return false;
    }
    
    public String getCommand() {
        return "";
    }
    
    public long getCheckConnectInterval() {
        return 5000L;
    }
    
    public long getReonnectInterval() {
        return 3000L;
    }
}
