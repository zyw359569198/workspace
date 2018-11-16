package com.reign.plugin.yx.util.kingnet.demo;

import java.nio.*;
import java.net.*;
import java.io.*;
import com.reign.plugin.yx.util.kingnet.udplog.*;

public class UdpSender
{
    public static final int resourceId = 1108006;
    public static final boolean debug = false;
    private LogSender sender;
    private MaintainStatManager manager;
    
    public UdpSender() throws SocketException {
        this.sender = new LogSender() {
            DatagramSocket socket = new DatagramSocket();
            
            @Override
            public void send(final ByteBuffer data) {
                try {
                    final DatagramPacket packet = new DatagramPacket(data.array(), data.array().length, InetAddress.getByName("10.221.72.32"), 8800);
                    this.socket.send(packet);
                }
                catch (SocketException e) {
                    e.printStackTrace();
                }
                catch (UnknownHostException e2) {
                    e2.printStackTrace();
                }
                catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
            
            @Override
            protected void finalize() throws Throwable {
                this.socket.close();
                super.finalize();
            }
        };
        this.manager = MaintainStatManager.getInstace(this.sender);
    }
    
    public void sendLoginLog(final String mainRef, final String subRef, final LogUserInfo logInfo) throws UnknownHostException {
        final KingNetLogFactory logFactory = new KingNetLogFactory();
        this.manager.sendUdpLog(logFactory.getLoginLog(1108006L, mainRef, subRef, logInfo, false));
    }
    
    public void sendPayLog(final LogUserInfo logInfo, final String unit, final int gold, final String orderId, final String packageId) throws UnknownHostException {
        final KingNetLogFactory logFactory = new KingNetLogFactory();
        this.manager.sendUdpLog(logFactory.getPayLog(1108006L, logInfo, unit, gold, orderId, packageId, false));
    }
    
    public void sendGuideLog(final LogUserInfo logInfo, final int taskId) {
        final KingNetLogFactory logFactory = new KingNetLogFactory();
        this.manager.sendUdpLog(logFactory.getGuideLog(1108006L, logInfo, taskId, false));
    }
}
