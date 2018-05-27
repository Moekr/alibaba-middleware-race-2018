package com.moekr.dubbo.agent.provider;

import com.moekr.dubbo.agent.protocol.AgentRequest;
import com.moekr.dubbo.agent.protocol.codec.AgentMessageDecoder;
import com.moekr.dubbo.agent.protocol.codec.AgentMessageEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

import java.util.function.Function;

public class AgentServerHandler extends ChannelInitializer<SocketChannel> {
	private final Function<AgentRequest, byte[]> invokeFunction;

	public AgentServerHandler(Function<AgentRequest, byte[]> invokeFunction) {
		this.invokeFunction = invokeFunction;
	}

	@Override
	protected void initChannel(SocketChannel channel) {
		ChannelPipeline pipeline = channel.pipeline();
		pipeline.addLast(new AgentMessageDecoder());
		pipeline.addLast(new AgentMessageEncoder());
		pipeline.addLast(new AgentRequestHandler(invokeFunction));
	}
}
