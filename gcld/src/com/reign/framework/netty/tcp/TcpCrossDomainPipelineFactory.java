package com.reign.framework.netty.tcp;

import com.reign.framework.netty.tcp.handler.*;
import org.jboss.netty.channel.*;
import com.reign.framework.netty.tcp.coder.*;

public class TcpCrossDomainPipelineFactory implements ChannelPipelineFactory
{
    private final FlashPolicyHandler flashPolicyHandler;
    
    public TcpCrossDomainPipelineFactory() throws Exception {
        this.flashPolicyHandler = new FlashPolicyHandler();
    }
    
    @Override
	public ChannelPipeline getPipeline() throws Exception {
        final ChannelPipeline pipeline = Channels.pipeline();
        pipeline.addLast("flashPolicy", this.flashPolicyHandler);
        pipeline.addLast("decoder", new MessageEncoder());
        pipeline.addLast("encoder", new MessageDecoder());
        return pipeline;
    }
}
