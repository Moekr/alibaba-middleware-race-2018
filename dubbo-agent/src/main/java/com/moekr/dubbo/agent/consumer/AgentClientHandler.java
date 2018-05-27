package com.moekr.dubbo.agent.consumer;

import com.moekr.dubbo.agent.netty.ResponseHandler;
import com.moekr.dubbo.agent.protocol.codec.AgentMessageDecoder;
import com.moekr.dubbo.agent.protocol.codec.AgentMessageEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class AgentClientHandler extends ChannelInitializer<SocketChannel> {
	@Override
	protected void initChannel(SocketChannel channel) {
		ChannelPipeline pipeline = channel.pipeline();
		pipeline.addLast(new AgentMessageDecoder());
		pipeline.addLast(new AgentMessageEncoder());
		pipeline.addLast(new ResponseHandler());
	}
}
