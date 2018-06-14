package com.moekr.dubbo.agent.netty;

import com.moekr.dubbo.agent.protocol.AgentRequest;
import com.moekr.dubbo.agent.registry.Endpoint;
import com.moekr.dubbo.agent.registry.EndpointSet;
import com.moekr.dubbo.agent.registry.Registry;
import com.moekr.dubbo.agent.util.ContextHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ConsumerRequestSender extends SimpleChannelInboundHandler<AgentRequest> {
	private final Registry registry;

	public ConsumerRequestSender(Registry registry) {
		this.registry = registry;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext context, AgentRequest request) throws Exception {
		EndpointSet endpointSet = registry.find(request.getInterfaceName());
		Endpoint endpoint = endpointSet.select();
		ContextHolder.hold(request.getId(), context.channel());
		endpoint.channel().writeAndFlush(request);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
		context.close();
	}
}
