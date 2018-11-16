package com.reign.plugin.yx.util.kingnet.demo;

import com.reign.plugin.yx.*;
import java.net.*;
import com.reign.plugin.yx.util.kingnet.udplog.*;

public class KingNetLogFactory
{
    public MaintainStat getLoginLog(final long resourceId, final String mainRef, final String subRef, final LogUserInfo logInfo, final Boolean debug) throws UnknownHostException {
        logInfo.setVersion(PluginContext.configuration.getServerId("qzone"));
        final MaintainStat stat = new MaintainStat(resourceId, logInfo, debug);
        final int onlineTime = 0;
        final int count = 1;
        stat.setLoginLog(mainRef, subRef, onlineTime, count);
        return stat;
    }
    
    public MaintainStat getGuideLog(final long resourceId, final LogUserInfo userInfo, final int step, final Boolean debug) {
        userInfo.setVersion(PluginContext.configuration.getServerId("qzone"));
        final MaintainStat stat = new MaintainStat(resourceId, userInfo, debug);
        stat.setGuideLog("guide", new StringBuilder(String.valueOf(step)).toString(), 1);
        return stat;
    }
    
    public MaintainStat getPayLog(final long resourceId, final LogUserInfo logInfo, final String unit, final int gold, final String orderId, final String packageId, final Boolean debug) {
        logInfo.setVersion(PluginContext.configuration.getServerId("qzone"));
        final int RMB = gold / 10;
        final MaintainStat stat = new MaintainStat(resourceId, logInfo, debug);
        stat.setUserInfo(logInfo);
        stat.setPayLog(unit, RMB, gold, orderId, packageId, StatConstants.PAY_TYPE.PAY, 1);
        return stat;
    }
}
