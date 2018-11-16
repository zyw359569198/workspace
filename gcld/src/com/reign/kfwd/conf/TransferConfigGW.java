package com.reign.kfwd.conf;

import com.reign.kf.comm.transfer.*;
import com.reign.kf.match.common.*;

public class TransferConfigGW extends TransferConfig
{
    private long checkConnectInterval;
    private long reonnectInterval;
    
    public TransferConfigGW() {
        this.checkConnectInterval = 5000L;
        this.reonnectInterval = 3000L;
        this.checkConnectInterval = 60000L;
        this.reonnectInterval = 30000L;
    }
    
    @Override
	public String getHost() {
        return Configuration.getProperty("gcld.match.gwHost");
    }
    
    @Override
	public int getPort() {
        return Integer.parseInt(Configuration.getProperty("gcld.match.gwPort"));
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
