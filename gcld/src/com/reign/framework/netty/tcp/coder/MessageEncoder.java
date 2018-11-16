package com.reign.framework.netty.tcp.coder;

import org.jboss.netty.handler.codec.oneone.*;
import org.jboss.netty.channel.*;

public class MessageEncoder extends OneToOneEncoder
{
    @Override
	protected Object encode(final ChannelHandlerContext context, final Channel channel, final Object obj) throws Exception {
        return obj;
    }
}
