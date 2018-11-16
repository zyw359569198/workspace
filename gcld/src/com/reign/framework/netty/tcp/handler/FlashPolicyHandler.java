package com.reign.framework.netty.tcp.handler;

import org.jboss.netty.handler.codec.frame.*;
import org.jboss.netty.buffer.*;
import com.reign.framework.common.*;
import com.reign.framework.netty.util.*;
import org.jboss.netty.channel.*;

public class FlashPolicyHandler extends FrameDecoder
{
    @Override
	protected Object decode(final ChannelHandlerContext ctx, final Channel channel, final ChannelBuffer buffer) throws Exception {
        if (buffer.readableBytes() < 2) {
            return null;
        }
        final int magic1 = buffer.getUnsignedByte(buffer.readerIndex());
        final int magic2 = buffer.getUnsignedByte(buffer.readerIndex() + 1);
        final boolean isFlashPolicyRequest = magic1 == 60 && magic2 == 112;
        if (isFlashPolicyRequest) {
            buffer.skipBytes(buffer.readableBytes());
            channel.write(WrapperUtil.wrapper(ServerConstants.CROSSDOMAIN)).addListener(ChannelFutureListener.CLOSE);
            return null;
        }
        ctx.getPipeline().remove(this);
        return buffer.readBytes(buffer.readableBytes());
    }
}
