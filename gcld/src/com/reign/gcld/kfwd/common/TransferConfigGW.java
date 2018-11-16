package com.reign.gcld.kfwd.common;

import com.reign.kf.comm.transfer.*;
import com.reign.gcld.common.*;

public class TransferConfigGW extends TransferConfig
{
    private long checkConnectInterval;
    private long reonnectInterval;
    
    public TransferConfigGW() {
        this.checkConnectInterval = 5000L;
        this.reonnectInterval = 3000L;
        this.checkConnectInterval = Long.parseLong(Configuration.getProperty(Configuration.KF_CHECK_CONNECT_INTERVAL));
        this.reonnectInterval = Long.parseLong(Configuration.getProperty(Configuration.KF_RECONNECT_INTERVAL));
    }
    
    @Override
	public String getHost() {
        return Configuration.getProperty(Configuration.GW_HOST);
    }
    
    @Override
	public int getPort() {
        return Integer.parseInt(Configuration.getProperty(Configuration.GW_PORT));
    }
    
    @Override
	public boolean compress() {
        return false;
    }
    
    @Override
	public String getCommand() {
        return "gw@gameserver";
    }
    
    @Override
	public long getCheckConnectInterval() {
        return this.checkConnectInterval;
    }
    
    @Override
	public long getReonnectInterval() {
        return this.reonnectInterval;
    }
    
    @Override
	public long getSendInterval() {
        return super.getSendInterval();
    }
}
