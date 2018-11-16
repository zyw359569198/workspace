package com.reign.plugin.yx.util.kingnet.udplog;

public class UdpMaintainEncoder
{
    public static UdpByteBuffer encode(final MaintainStat stat) throws Exception {
        final MaintainStatBody body = stat.getBody();
        final MaintainStatHead head = stat.getHead();
        final int bodySize = body.getSize();
        head.setnPackageLength(bodySize + head.getSize());
        final UdpByteBuffer buf = new UdpByteBuffer(bodySize + head.getSize() + 1);
        buf.writeByte(head.getHttpHead());
        buf.writeInt(head.getnPackageLength());
        final int headUid = MaintainBuffHelper.getInstance().uidToInt(head.getnUID());
        buf.writeInt(headUid);
        buf.writeShort(head.getShFlag());
        buf.writeShort(head.getShOptionalLen());
        buf.writeShort(head.getShHeaderLen());
        buf.writeShort(head.getShMessageID());
        buf.writeShort(head.getShMessageType());
        buf.writeShort(head.getShVersionType());
        buf.writeShort(head.getShVersion());
        buf.writeInt((int)head.getnResourceId());
        buf.writeInt((int)head.getnTimestamp());
        final int bodyUid = MaintainBuffHelper.getInstance().uidToInt(body.getUid());
        buf.writeInt(bodyUid);
        buf.writeShort(body.getTableType());
        MaintainBuffHelper.getInstance().encodeString(buf, body.getMsgContent());
        return buf;
    }
}
