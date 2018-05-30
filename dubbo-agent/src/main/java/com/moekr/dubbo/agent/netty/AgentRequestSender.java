package com.moekr.dubbo.agent.netty;

import com.moekr.dubbo.agent.protocol.AgentRequest;
import com.moekr.dubbo.agent.registry.Endpoint;
import com.moekr.dubbo.agent.registry.Registry;
import com.moekr.dubbo.agent.util.ContextHolder;
import com.moekr.dubbo.agent.util.RequestContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class AgentRequestSender extends SimpleChannelInboundHandler<AgentRequest> {
	private final Registry registry;

	public AgentRequestSender(Registry registry) {
		this.registry = registry;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext context, AgentRequest request) throws Exception {
		Endpoint endpoint = registry.find(request.getInterfaceName()).select();
		RequestContext requestContext = new RequestContext(request.getId(), context);
		ContextHolder.hold(requestContext);
		endpoint.getChannel().writeAndFlush(request);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
		context.close();
	}
}
