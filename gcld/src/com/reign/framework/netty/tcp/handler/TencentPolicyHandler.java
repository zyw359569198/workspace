package com.reign.framework.netty.tcp.handler;

import org.jboss.netty.handler.codec.frame.*;
import org.jboss.netty.buffer.*;
import org.jboss.netty.channel.*;

public class TencentPolicyHandler extends FrameDecoder
{
    @Override
	protected Object decode(final ChannelHandlerContext ctx, final Channel channel, final ChannelBuffer buffer) throws Exception {
        if (buffer.readableBytes() < 14) {
            return null;
        }
        final String head = new String(buffer.array());
        if (head.startsWith("tgw")) {
            buffer.readBytes(100);
        }
        ctx.getPipeline().remove(this);
        if (buffer.readableBytes() < 4) {
            return null;
        }
        return buffer.readBytes(buffer.readableBytes());
    }
}
