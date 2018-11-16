package com.reign.framework.netty.tcp.coder;

import org.jboss.netty.handler.codec.frame.*;
import org.jboss.netty.channel.*;
import org.jboss.netty.buffer.*;
import com.reign.framework.netty.tcp.handler.*;

public class MessageDecoder extends FrameDecoder
{
    @Override
	protected Object decode(final ChannelHandlerContext context, final Channel channel, final ChannelBuffer buffer) throws Exception {
        if (buffer.readableBytes() < 4) {
            return null;
        }
        final int dataLen = buffer.getInt(buffer.readerIndex());
        if (buffer.readableBytes() < dataLen + 4) {
            return null;
        }
        final int head = buffer.readInt();
        final ChannelBuffer bf = ChannelBuffers.dynamicBuffer(head);
        buffer.readBytes(bf, head);
        final RequestMessage r = new RequestMessage();
        r.setCommand(new String(bf.readBytes(32).array()).trim());
        r.setRequestId(bf.readInt());
        r.setContent(bf.readBytes(bf.readableBytes()).array());
        return r;
    }
}
