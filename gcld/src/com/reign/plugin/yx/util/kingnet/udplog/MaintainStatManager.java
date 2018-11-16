package com.reign.plugin.yx.util.kingnet.udplog;

public class MaintainStatManager
{
    public static MaintainStatManager mInstance;
    private LogSender sender;
    
    public static MaintainStatManager getInstace(final LogSender sender) {
        if (MaintainStatManager.mInstance == null) {
            MaintainStatManager.mInstance = new MaintainStatManager(sender);
        }
        return MaintainStatManager.mInstance;
    }
    
    private MaintainStatManager(final LogSender sender) {
        this.sender = sender;
    }
    
    public void sendUdpLog(final MaintainStat stat) {
        try {
            this.sender.send(stat.toByteBuffer());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
