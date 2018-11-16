package com.reign.gcld.kfwd.common.transferconfig;

import com.reign.kf.comm.transfer.*;
import com.reign.gcld.common.*;
import com.reign.util.*;
import org.apache.commons.lang.*;

public class TransferConfigMatch extends TransferConfig
{
    private static final String TAG = "gcld";
    private String host;
    private int port;
    private long checkConnectInterval;
    private long reonnectInterval;
    private String machineId;
    
    public TransferConfigMatch(final String host, final int port) {
        this.checkConnectInterval = 5000L;
        this.reonnectInterval = 3000L;
        this.host = host;
        this.port = port;
        this.checkConnectInterval = Long.parseLong(Configuration.getProperty(Configuration.KF_CHECK_CONNECT_INTERVAL));
        this.reonnectInterval = Long.parseLong(Configuration.getProperty(Configuration.KF_RECONNECT_INTERVAL));
        this.machineId = Configuration.getProperty(Configuration.SERVER_KEY);
    }
    
    @Override
	public String getHost() {
        return this.host;
    }
    
    @Override
	public int getPort() {
        return this.port;
    }
    
    @Override
	public boolean compress() {
        return false;
    }
    
    @Override
	public String getCommand() {
        return "match@kfwdgateway";
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
        return 1000L;
    }
    
    @Override
	public String getMachineId() {
        return this.machineId;
    }
    
    @Override
	public int getMaxSendSize() {
        return 1000;
    }
    
    private String createMachineId() {
        final String webPath = ListenerConstants.WEB_PATH;
        String rtn = MD5SecurityUtil.code(webPath);
        String postFix = "";
        String[] split;
        for (int length = (split = webPath.split("\\\\")).length, i = 0; i < length; ++i) {
            final String str = split[i];
            if (str.indexOf("gcld") != -1) {
                postFix = str;
                break;
            }
        }
        if (!StringUtils.isBlank(postFix)) {
            rtn = String.valueOf(rtn) + "|" + postFix;
        }
        return rtn;
    }
}
